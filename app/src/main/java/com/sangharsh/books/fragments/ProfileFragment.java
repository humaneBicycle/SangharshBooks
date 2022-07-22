package com.sangharsh.books.fragments;

import static android.content.ContentValues.TAG;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sangharsh.books.PreferenceGetter;
import com.sangharsh.books.R;

public class ProfileFragment extends Fragment {

    SwitchMaterial switchMaterial;
    TextView aboutUs;
    TextView privacyPolicy;

    public ProfileFragment(){}



    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);

        switchMaterial.setChecked(Configuration.UI_MODE_NIGHT_YES == (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK));


        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (compoundButton.isChecked()) {
                        Log.d(TAG, "onCheckedChanged: 11");
                        //getActivity().recreate();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        Log.d(TAG, "onCheckedChanged: 12");
                        //getActivity().recreate();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }





            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    private void initUI(View view){
        switchMaterial = view.findViewById(R.id.active_mode_theme);
        aboutUs = view.findViewById(R.id.about_us);
        privacyPolicy = view.findViewById(R.id.privacy_policy);
    }
}