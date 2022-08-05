package com.sangharsh.books;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sangharsh.books.adapter.BannerPagerAdapter;
import com.sangharsh.books.adapter.ViewPagerAdapter;
import com.sangharsh.books.fragments.BookmarksFragment;
import com.sangharsh.books.fragments.DownloadsFragment;
import com.sangharsh.books.fragments.HomeFragment;
import com.sangharsh.books.fragments.ProfileFragment;
import com.sangharsh.books.model.HomeDocument;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements UIUpdateHomeFrag{

    SmoothBottomBar smoothBottomBar;
    HomeFragment homeFragment;
    SangharshBooks sangharshBooks;
    DownloadsFragment downloadsFragment;
    ProfileFragment profileFragment;
    BookmarksFragment bookmarksFragment;
    private InterstitialAd mInterstitialAd;
    private CardView cardView;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MobileAds.initialize(this);

            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    setTheme(R.style.Theme_Dark);
                    ((SangharshBooks)getApplication()).setDarkMode(true);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    setTheme(R.style.Theme_Light);
                    ((SangharshBooks)getApplication()).setDarkMode(false);
                    break;
            }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadBanner();

        sangharshBooks = (SangharshBooks) getApplication();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("sangharshBooks","Sangharsh Books", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        cardView = findViewById(R.id.latestCard);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                        Log.i("sba ", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("sba", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });



        createviewPager();


//        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//        ft1.replace(R.id.body_holder_main_activity, homeFragment, "home");
//        ft1.commit();
//        sangharshBooks.setActiveFragment("home");

//        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public boolean onItemSelect(int i) {
//                if(i==0) {
//                    viewPager.setCurrentItem(0);
////                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////                    if(homeFragment==null) {
////                        homeFragment = new HomeFragment();
////                    }
////                    fragmentTransaction.replace(R.id.body_holder_main_activity, new HomeFragment(), "home");
////                    sangharshBooks.setActiveFragment("home");
////                    fragmentTransaction.commit();
////                    try{
////                        if(sangharshBooks.getActiveFragment().equals("home")) {
////                            cardView.setVisibility(View.VISIBLE);
////                        }
////                    }catch (Exception e){
////                        e.printStackTrace();
////                    }
//
//                }else if(i==1){
////                    if(downloadsFragment == null){
////                        downloadsFragment = new DownloadsFragment();
////                        Log.d("sba ", "onItemSelect: downloads reloaded");
////                    }
////                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////                    fragmentTransaction.replace(R.id.body_holder_main_activity,downloadsFragment, "downloads").commit();
////                    sangharshBooks.setActiveFragment("downloads");
////                    try{
////                        cardView.setVisibility(View.GONE);
////                    }catch (Exception e){
////                        e.printStackTrace();
////                    }
//                    viewPager.setCurrentItem(1);
//
//                }else if(i==2){
////                    try{
////                        cardView.setVisibility(View.GONE);
////                    }catch (Exception e){
////                        e.printStackTrace();
////                    }
////                    if(bookmarksFragment ==null){
////                        bookmarksFragment = new BookmarksFragment();
////
////                    }
////                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////                    fragmentTransaction.replace(R.id.body_holder_main_activity, bookmarksFragment, "bookmarks");
////                    sangharshBooks.setActiveFragment("bookmarks");
////                    fragmentTransaction.commit();
//                    viewPager.setCurrentItem(2);
//                }else if(i==3){
////                    try{
////                        cardView.setVisibility(View.GONE);
////                    }catch (Exception e){
////                        e.printStackTrace();
////                    };
////                    if(profileFragment ==null){
////                        profileFragment = new ProfileFragment();
////                    }
////                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////                    fragmentTransaction.replace(R.id.body_holder_main_activity, profileFragment, "profile");
////                    sangharshBooks.setActiveFragment("profile");
////                    fragmentTransaction.commit();
//                }
//                    viewPager.setCurrentItem(3);
//
//                return false;
//            }
//        });
    }

    private void createviewPager() {
        smoothBottomBar = findViewById(R.id.bottomBar);
        if(homeFragment==null) {
            homeFragment = new HomeFragment();
        }
        viewPager = findViewById(R.id.body_holder_main_activity_view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(homeFragment,"home");
        viewPagerAdapter.add(new DownloadsFragment(),"downloads");
        viewPagerAdapter.add(new BookmarksFragment(),"bookmarks");
        viewPagerAdapter.add(new ProfileFragment(),"profile");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                smoothBottomBar.setItemActiveIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                if (viewPager.getCurrentItem() != i) {
                    viewPager.setCurrentItem(i);
                }
                return false;
            }
        });

        viewPager.setOffscreenPageLimit(4);
    }


    @Override
    protected void onResume() {
        if (mInterstitialAd != null && sangharshBooks.getAdCount()%3==0) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
        super.onResume();
    }

    @Override
    public void update() {
        homeFragment.updateAdapterDataset();
    }

    private void loadBanner() {
        AdView mAdView = findViewById(R.id.myAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }








}