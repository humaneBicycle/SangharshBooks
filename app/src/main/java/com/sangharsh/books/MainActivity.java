package com.sangharsh.books;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.sangharsh.books.adapter.ViewPagerAdapter;
import com.sangharsh.books.fragments.AboutFragment;
import com.sangharsh.books.fragments.BookmarksFragment;
import com.sangharsh.books.fragments.DownloadsFragment;
import com.sangharsh.books.fragments.HomeFragment;
import com.sangharsh.books.interfaces.UIUpdateHomeFrag;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements UIUpdateHomeFrag {

    SmoothBottomBar smoothBottomBar;
    HomeFragment homeFragment;
    SangharshBooks sangharshBooks;
    private InterstitialAd mInterstitialAd;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @SuppressLint("MissingInflatedId")
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
            if (FirebaseAuth.getInstance().getCurrentUser() == null){
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
        setContentView(R.layout.activity_main);
        loadBanner();

        sangharshBooks = (SangharshBooks) getApplication();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("sangharshBooks","Sangharsh Books", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        findViewById(R.id.notification_main_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NotificationActivity.class));
            }
        });

        loadAd();
        createviewPager();
    }

    private void loadAd(){
        Log.i(TAG, "loadAd: sba load interstitial ad called");
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.admob_id_interstitial), adRequest,
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
        viewPagerAdapter.add(new AboutFragment(),"about");
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

        viewPager.setOffscreenPageLimit(5);
    }


    @Override
    protected void onResume() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            loadAd();
        }
//        if(sangharshBooks.getAdCount()%2!=0){
//            loadAd();
//        }
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