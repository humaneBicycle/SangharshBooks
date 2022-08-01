package com.sangharsh.books.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.UIUpdateHomeFrag;
import com.sangharsh.books.model.Notification;

import java.util.ArrayList;

public class AddNotificationBottomSheetAdapter extends BottomSheetDialogFragment {
    EditText notiTitle, notiBody;
    AppCompatButton addButton;
    Context context;
    SangharshBooks sangharshBooks;
    UIUpdateHomeFrag callback;
    ArrayList<Notification> notifications;
    NotificationAdapter adapter;

    public AddNotificationBottomSheetAdapter(Context context, SangharshBooks sangharshBooks, UIUpdateHomeFrag callback, ArrayList<Notification> notifications, NotificationAdapter adapter){
        this.context = context;
        this.sangharshBooks = sangharshBooks;
        this.callback = callback;
        this.adapter = adapter;
        this.notifications = notifications;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_notification_bottom_sheet,container);
        notiBody = v.findViewById(R.id.body_noti);
        notiTitle = v.findViewById(R.id.title_noti);
        addButton=v.findViewById(R.id.add_noti);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!notiBody.getText().toString().isEmpty() && !notiTitle.getText().toString().isEmpty()){
                    Notification notification = new Notification(notiTitle.getText().toString(),notiBody.getText().toString(),FirebaseFirestore.getInstance().collection("notification").document().getId());

                    FirebaseFirestore.getInstance().collection("notification").document().set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show();
                                notifications.add(notification);
                                adapter.notifyItemInserted(notifications.size());
                                callback.update();
                                dismiss();
                            }else{
                                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(context, "Please fill title and body!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }
}
