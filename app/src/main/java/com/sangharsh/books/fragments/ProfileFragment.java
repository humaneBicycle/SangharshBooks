package com.sangharsh.books.fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sangharsh.books.NotificationActivity;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.UIUpdateHomeFrag;
import com.sangharsh.books.adapter.NotificationAdapter;
import com.sangharsh.books.model.Notification;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements UIUpdateHomeFrag {

    SangharshBooks sangharshBooks;
    TextView isEmptyTC;
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    ProgressBar progressBar;
    ArrayList<Notification> notifications;

    public ProfileFragment(){}



    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        isEmptyTC = view.findViewById(R.id.text_nothing_available_noti_activity);
        progressBar = view.findViewById(R.id.progress_noti);
        recyclerView = view.findViewById(R.id.notification_rv);

        sangharshBooks = (SangharshBooks) getActivity().getApplication();

        fetchNotifications();
        sangharshBooks = (SangharshBooks)getActivity().getApplication();

//        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//                    if (compoundButton.isChecked()) {
//                        //Log.d(TAG, "onCheckedChanged: 11");
//                        //getActivity().recreate();
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                        sangharshBooks.setDarkMode(true);
//                    } else {
//                        //Log.d(TAG, "onCheckedChanged: 12");
//                        //getActivity().recreate();
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                        sangharshBooks.setDarkMode(false);
//                    }
//
//
//
//
//            }
//        });



        return view;
    }

//    private void initUI(View view){
//        aboutUs = view.findViewById(R.id.about_us);
//        privacyPolicy = view.findViewById(R.id.privacy_policy);
//    }
    private void fetchNotifications(){
        if(sangharshBooks.getNotifications()!=null) {
            notifications = sangharshBooks.getNotifications();
            confirmNotification();
        }else{
            FirebaseFirestore.getInstance().collection("notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            notifications = (ArrayList<Notification>) task.getResult().toObjects(Notification.class);
                            sangharshBooks.setNotifications(notifications);
                            confirmNotification();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            isEmptyTC.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    private void confirmNotification() {
        notificationAdapter = new NotificationAdapter(getContext(), notifications, ProfileFragment.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(notificationAdapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void update() {

    }
}