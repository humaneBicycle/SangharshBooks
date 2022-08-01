package com.sangharsh.books.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sangharsh.books.Listener;
import com.sangharsh.books.NotificationActivity;
import com.sangharsh.books.R;
import com.sangharsh.books.adapter.BannerPagerAdapter;
import com.sangharsh.books.model.HomeDocument;

import java.util.Calendar;
import java.util.Date;

public class GreetingFragment extends Fragment {
;
    TextView greetings;
    ImageView bell;
    ConstraintLayout constraintLayout;

    public GreetingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



//        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blur);
//        Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//// create a canvas where we can draw on
//        Canvas canvas = new Canvas(newBitmap);
//// create a paint instance with alpha
//        Paint alphaPaint = new Paint();
//        alphaPaint.setAlpha(210);
//// now lets draw using alphaPaint instance
//        canvas.drawBitmap(originalBitmap, 0, 0, alphaPaint);
//
//        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), newBitmap);
//        final float roundPx = (float) originalBitmap.getWidth() * 0.06f;
//        roundedBitmapDrawable.setCornerRadius(roundPx);
//        constraintLayout.setBackground(roundedBitmapDrawable);

        return greetingUI(inflater,container);
    }

    private String getGreetings(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting ;
        if(hour>= 12 && hour < 17){
            greeting = getResources().getString(R.string.morning);
        } else if(hour >= 17 && hour < 21){
            greeting = getResources().getString(R.string.evening);
        } else if(hour >= 21){
            greeting = getResources().getString(R.string.night);
        } else {
            greeting = getResources().getString(R.string.morning);
        }
        return greeting;
    }
    private View greetingUI(LayoutInflater inflater, ViewGroup container){
        View view = inflater.inflate(R.layout.fragment_greeting, container, false);


        bell = view.findViewById(R.id.notification_greetings_flag);
        constraintLayout = view.findViewById(R.id.greetings_background);
        greetings = view.findViewById(R.id.greetings_frag);
        greetings.setText(getGreetings());

        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), NotificationActivity.class));
            }
        });
        return view;
    }


}