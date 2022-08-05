package com.sangharsh.books.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sangharsh.books.DirectoryChangeListener;
import com.sangharsh.books.MainActivity;
import com.sangharsh.books.R;
import com.sangharsh.books.SangharshBooks;
import com.sangharsh.books.UIUpdateHomeFrag;
import com.sangharsh.books.adapter.BannerPagerAdapter;
import com.sangharsh.books.adapter.DirectoryAdapter;
import com.sangharsh.books.model.Directory;
import com.sangharsh.books.model.FileModel;
import com.sangharsh.books.model.HomeDocument;
import com.sangharsh.books.model.PDFModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements UIUpdateHomeFrag, DirectoryChangeListener {

    RecyclerView recyclerView;
    Directory directory;
    ProgressBar progressBar;
    TextView nothingAvailableTV;
    ArrayList<Directory> directories;
    SangharshBooks sangharshBooks;
    private HomeDocument homeDocument;
    DirectoryAdapter directoryAdapter;
    private BannerPagerAdapter bannerAdapter;
    private ViewPager bannerPager;
    private TextView counterTxt;
    CardView cardView;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sangharshBooks = (SangharshBooks) getActivity().getApplication();
        sangharshBooks.resetAddress();
        initUI(view);
        fetchHome();
        return view;
    }

    private void initUI(View v){
        bannerPager = v.findViewById(R.id.viewPager);
        bannerPager = v.findViewById(R.id.viewPager);
        cardView = v.findViewById(R.id.latestCard);
        counterTxt = (TextView) v.findViewById(R.id.counter);
        recyclerView = v.findViewById(R.id.rv_home);
        progressBar = v.findViewById(R.id.progress_home_frag);
        nothingAvailableTV = v.findViewById(R.id.text_nothing_available_home_frag);

        if(sangharshBooks.getHomeDirectory()!=null) {
            directory = sangharshBooks.getHomeDirectory();
            confirmAdapter();
        }else{
            FirebaseFirestore.getInstance().collection("directory").whereEqualTo("path", sangharshBooks.path).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            directories = (ArrayList<Directory>) task.getResult().toObjects(Directory.class);
                            directory = task.getResult().toObjects(Directory.class).get(0);
                            sangharshBooks.setHomeDirectory(directory);
                            confirmAdapter();
                        } else {
                            sangharshBooks.addStack("/");
                            nothingAvailableTV.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(getContext(), "Something went wrong. Please try again later!" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
    private void confirmAdapter(){

        directoryAdapter = new DirectoryAdapter(getContext(),directory,sangharshBooks,HomeFragment.this);
        recyclerView.setAdapter(directoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void update() {
        Log.d("sba", "update: file added listened");
    }

    @Override
    public void onFileModelAdded(FileModel fileModel) {
        if(directory!=null && directoryAdapter!=null) {
            directory.getFiles().add(fileModel);
            directoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPDFModelAdded(PDFModel pdfModel) {
        if(directory!=null && directoryAdapter!=null) {
            directory.getPdfModels().add(pdfModel);
            directoryAdapter.notifyDataSetChanged();
        }
    }

    public void updateAdapterDataset(){
        directoryAdapter.notifyDataSetChanged();
        if((directory.getPdfModels().size()+directory.getFiles().size())==0){
            nothingAvailableTV.setVisibility(View.VISIBLE);
        }
        if((directory.getPdfModels().size()+directory.getFiles().size())==1){
            nothingAvailableTV.setVisibility(View.GONE);
        }

    }

    private void fetchHome() {
        if(homeDocument==null) {
            FirebaseFirestore.getInstance().collection("app").document("Home")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                        homeDocument = document.toObject(HomeDocument.class);
                                        showSlider();

                                }
                            } else {
                                Toast.makeText(getContext(), "Error fetching the data " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                Log.i("Get Data", "Data Fetching Error:" + task.getException().toString());
                            }
                        }
                    });
        }else{
            Log.d("sba homefrag", "fetchHome: not fetched");
            showSlider();
        }


    }
    private void setCounter(int count) {
        if (homeDocument.getBanners() == null || homeDocument.getBanners().size() <=0) {
            cardView.setVisibility(View.GONE);
            counterTxt.setVisibility(View.GONE);
        } else {
            String html = "";
            for (int i = 0; i < homeDocument.getBanners().size(); i++) {
                if (i == count) {
                    html = html + " &#9679;";
                } else {
                    html = html + " &#9675;";
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                counterTxt.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } else {
                counterTxt.setText(Html.fromHtml(html));
            }
//            if (sangharshBooks.getActiveFragment().equals("home")){
//                cardView.setVisibility(View.VISIBLE);
//            }
            if (counterTxt.getVisibility() == View.GONE){
                counterTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showSlider(){

        if (homeDocument.getBanners() != null && homeDocument.getBanners().size() > 0) {
            if(bannerAdapter==null) {
                Log.d("sba", "showSlider: bannerAdapter is null");
                bannerAdapter = new BannerPagerAdapter(((AppCompatActivity) getContext()).getSupportFragmentManager(), homeDocument.getBanners()
                        );

                setCounter(0);
            }
            bannerPager.setAdapter(bannerAdapter);
            bannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setCounter(position);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
    }

}