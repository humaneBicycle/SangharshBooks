package com.sangharsh.books.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.sangharsh.books.BuyCoinsActivity;
import com.sangharsh.books.LiveData.UserData;
import com.sangharsh.books.LoginActivity;
import com.sangharsh.books.R;
import com.sangharsh.books.ReferActivity;
import com.sangharsh.books.Tools.CircleTransform;
import com.sangharsh.books.model.User;
import com.squareup.picasso.Picasso;

public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    private TextView nameTxt;
    private TextView phoneTxt;
    private TextView signUpTxt;
    private LinearLayout userInfoLayout;
    private TextView editProfileTxt;
    private ImageView profileImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_about, container, false);
        findViews(v);
        setUpViews();
        showPoints();
        v.findViewById(R.id.rate_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();
            }

        });
        v.findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getString(R.string.privacy_policy_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        return v;
    }

    private void showPoints() {
        UserData.getUserLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                phoneTxt.setText(user.getPoints() + " Coins");
            }
        });
    }

    private void findViews(View view) {
        MaterialButton logoutBtn = view.findViewById(R.id.logOutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout();
            }
        });

        userInfoLayout = view.findViewById(R.id.userInfoLayout);
        nameTxt = view.findViewById(R.id.nameTxt);
        phoneTxt = view.findViewById(R.id.phoneNumTxt);
        signUpTxt = view.findViewById(R.id.signUpTxt);
        editProfileTxt = view.findViewById(R.id.editProfileTxt);
        profileImg = view.findViewById(R.id.displayImg);

        view.findViewById(R.id.referButton).setOnClickListener(view1 ->{
            startActivity(new Intent(getActivity(), ReferActivity.class));
        });
        view.findViewById(R.id.buyCoinsBtn).setOnClickListener(view1 ->{
            startActivity(new Intent(getActivity(), BuyCoinsActivity.class));
        });

    }

    private void confirmLogout() {
        String message = "Are you sure you want to logout?";
        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            message = message + "\nWARNING: You are logged in as anonymous user. You will loose all your progress once you logout.";
        }
        new AlertDialog.Builder(getActivity())
                .setTitle("Log out")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        logout();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    private void setUpViews() {
        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            editProfileTxt.setVisibility(View.GONE);
            phoneTxt.setVisibility(View.GONE);
            userInfoLayout.setOnClickListener(view -> login());
        } else {
            signUpTxt.setVisibility(View.GONE);
            if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null && !FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().isEmpty())
                Picasso.get()
                        .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                        .transform(new CircleTransform())
                        .into(profileImg);
            nameTxt.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            phoneTxt.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    private void login() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("convertToPermanent", true);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    public void rateApp(){
        if (getActivity().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE).getBoolean("is_rev", false)){
            rateApp(0);
            return;
        }
        getActivity().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE).edit()
                .putBoolean("is_rev", true).commit();
        ReviewManager manager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.isComplete()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnCompleteListener((Task<Void> newTask) -> {
                   if (!newTask.isSuccessful()){
                       rateApp(0);
                   }
                });
            } else {
                rateApp(0);
            }
        });
    }

    public void rateApp(int error)
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getContext().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
}