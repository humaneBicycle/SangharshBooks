package com.sangharsh.books.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sangharsh.books.R;
import com.sangharsh.books.UIUpdateHomeFrag;
import com.sangharsh.books.model.Notification;

import java.util.ArrayList;

public class NotificationLongPressOptions extends BottomSheetDialogFragment {

    AppCompatButton deleteButton;
    Context context;
    UIUpdateHomeFrag callback;
    Notification notification;
    int position;
    NotificationAdapter adapter;
    ArrayList<Notification> notifications;

    public NotificationLongPressOptions(Context context, Notification notification, UIUpdateHomeFrag callback,NotificationAdapter adapter,int pos,ArrayList<Notification> notifications){
        this.context=context;
        this.notification=notification;
        this.callback = callback;
        this.adapter = adapter;
        this.position = pos;
        this.notifications = notifications;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.long_click_options,container);
//        deleteButton = v.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("notification").whereEqualTo("id",notification.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()) {
                                FirebaseFirestore.getInstance().collection("notification").document(task.getResult().getDocuments().get(0).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            notifications.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            adapter.notifyItemRangeChanged(position,notifications.size());
                                            callback.update();
                                            dismiss();
                                            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        {
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        return v;
    }
}
