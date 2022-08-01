package com.sangharsh.books.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangharsh.books.R;
import com.squareup.picasso.Picasso;

public class BannerFragment extends Fragment {
    String url;
    String redirectUrl;
    String text;
    Listener listener;
    ImageView imageView;

    public BannerFragment() {
    }

    public interface Listener{
        void startCatActivity(String category);
    }

    public BannerFragment(String imgUrl, String redirectUrl, String category, String subcategory, String text) {
        this.url = imgUrl;
        this.redirectUrl = redirectUrl;
        listener = (Listener) getActivity();
        this.text = text;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.latestTitle);
        if (url == null || url.isEmpty()){
            imageView.setVisibility(View.GONE);
//            imageView.setImageResource(R.drawable.blur_blue);
        } else {
            Picasso.get()
                    .load(url)
                    .into(imageView);
        }

        if (text == null || text.isEmpty()){
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            view.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(redirectUrl));
                    startActivity(i);
                }
            });
        }
    }
    public void loadImage(){
        Picasso.get()
                .load(url)
                .into(imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_banner, container, false);
        return view;
    }
}