package com.sangharsh.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sangharsh.books.adapter.AddNotificationBottomSheetAdapter;
import com.sangharsh.books.adapter.NotificationAdapter;
import com.sangharsh.books.model.Notification;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements UIUpdateHomeFrag {

    TextView isEmptyTC;
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    ProgressBar progressBar;
    ImageView back;
    ArrayList<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                setTheme(R.style.Theme_Dark);
                ((SangharshBooks)getApplication()).setDarkMode(true);
                Log.d("sba", "onCreate: 21");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.Theme_Light);
                ((SangharshBooks)getApplication()).setDarkMode(false);
                Log.d("sba", "onCreate: 22");
                // process
                break;
        }

        setContentView(R.layout.activity_notification);

        isEmptyTC = findViewById(R.id.text_nothing_available_noti_activity);
        progressBar = findViewById(R.id.progress_noti);
        recyclerView = findViewById(R.id.notification_rv);
//        fab = findViewById(R.id.add_noti);
        back = findViewById(R.id.back_noti);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fetchNotifications();


//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AddNotificationBottomSheetAdapter sheet = new AddNotificationBottomSheetAdapter(NotificationActivity.this,(SangharshBooks)getApplication(),NotificationActivity.this,notifications,notificationAdapter);
//                sheet.show(getSupportFragmentManager(),"addNotificationSheet");
//            }
//        });
    }

    @Override
    public void update() {
        if(notifications.size()==0){
            isEmptyTC.setVisibility(View.VISIBLE);
        }
        if(notifications.size()==1){
            isEmptyTC.setVisibility(View.INVISIBLE);
        }
       //fetchNotifications();
        //isEmptyTC.setVisibility(View.INVISIBLE);
    }

    private void fetchNotifications(){
        FirebaseFirestore.getInstance().collection("notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){
                        notifications = (ArrayList<Notification>) task.getResult().toObjects(Notification.class);
                        //Log.d("sba", "notification fetch size "+ notifications.size());

                            notificationAdapter = new NotificationAdapter(NotificationActivity.this, notifications, NotificationActivity.this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                            recyclerView.setAdapter(notificationAdapter);
                            progressBar.setVisibility(View.GONE);


                    }else{
                        progressBar.setVisibility(View.GONE);
                        isEmptyTC.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}