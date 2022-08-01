package com.sangharsh.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sangharsh.books.R;
import com.sangharsh.books.adapter.AddDirectoryBottomSheetAdapter;
import com.sangharsh.books.adapter.BannerPagerAdapter;
import com.sangharsh.books.fragments.BookmarksFragment;
import com.sangharsh.books.fragments.DownloadsFragment;
import com.sangharsh.books.fragments.GreetingFragment;
import com.sangharsh.books.fragments.HomeFragment;
import com.sangharsh.books.fragments.ProfileFragment;
import com.sangharsh.books.model.HomeDocument;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements UIUpdateHomeFrag{

    SmoothBottomBar smoothBottomBar;
    FloatingActionButton fab;
    HomeFragment homeFragment;
    SangharshBooks sangharshBooks;
    private HomeDocument homeDocument;
    private BannerPagerAdapter bannerAdapter;
    private ViewPager bannerPager;
    private TextView counterTxt;
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

        smoothBottomBar = findViewById(R.id.bottomBar);

//        if(savedInstanceState!=null){
//            //smoothBottomBar.setItemActiveIndex();
//            if(sangharshBooks.getActiveFragment().equals("profile")) {
//                smoothBottomBar.setItemActiveIndex(3);
////                fetchHome();
//                bannerPager.setVisibility(View.GONE);
//            }
//            if(bannerPager==null){
//                fetchHome();
//            }
//
//        }
//        if(savedInstanceState==null){
//            fetchHome();
//            if(homeFragment==null) {
//                homeFragment = new HomeFragment();
//                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                ft1.replace(R.id.body_holder_main_activity, homeFragment, "home");
//                ft1.commit();
//            }
//        }
        fetchHome();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.body_holder_main_activity, new HomeFragment(), "home");
                ft1.commit();

        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                if(i==0) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if(homeFragment==null) {
                        homeFragment = new HomeFragment();
                    }
                    fragmentTransaction.replace(R.id.body_holder_main_activity, homeFragment, "home");
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("greetings");
                    if(fragment != null)
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    sangharshBooks.setActiveFragment("home");

                    bannerPager.setVisibility(View.VISIBLE);

                    fragmentTransaction.commit();
                }else if(i==1){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.body_holder_main_activity, new DownloadsFragment(), "downloads");
                    sangharshBooks.setActiveFragment("downloads");
                    bannerPager.setVisibility(View.GONE);
                    fragmentTransaction.replace(R.id.greetings_holder, new GreetingFragment(), "greetings");
                    fragmentTransaction.commit();
                }else if(i==2){
                    bannerPager.setVisibility(View.GONE);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.body_holder_main_activity, new BookmarksFragment(), "bookmarks");
                    fragmentTransaction.replace(R.id.greetings_holder, new GreetingFragment(), "greetings");
                    sangharshBooks.setActiveFragment("bookmarks");
                    fragmentTransaction.commit();
                }else if(i==3){
                    bannerPager.setVisibility(View.GONE);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.body_holder_main_activity, new ProfileFragment(), "profile");
                    fragmentTransaction.replace(R.id.greetings_holder, new GreetingFragment(), "greetings");
                    sangharshBooks.setActiveFragment("profile");
                    fragmentTransaction.commit();
                }

                return false;
            }
        });


//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.greetings_holder, new GreetingFragment(), "greetings");
//        ft.commit();


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
//        smoothBottomBar.setBackground(roundedBitmapDrawable);
    }

    private void loadBanner() {
        AdView mAdView = findViewById(R.id.myAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void update() {
        homeFragment.updateAdapterDataset();
    }



    private void fetchHome() {
        FirebaseFirestore.getInstance().collection("app").document("Home")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (homeDocument == null) {
                                    homeDocument = document.toObject(HomeDocument.class);
                                    showSlider();
                                } else {
                                    HomeDocument newHome = document.toObject(HomeDocument.class);
                                    homeDocument.getBanners().addAll(newHome.getBanners());
                                    addToSlider();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error fetching the data " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.i("Get Data", "Data Fetching Error:" + task.getException().toString());
                        }
                    }
                });

    }
    private void showSlider(){
        findViewById(R.id.latestCard).setVisibility(View.VISIBLE);
        if (bannerPager == null){
            bannerPager = findViewById(R.id.viewPager);
        }

        if (homeDocument.getBanners() != null && homeDocument.getBanners().size() > 0) {
            bannerAdapter = new BannerPagerAdapter(getSupportFragmentManager(), homeDocument.getBanners(),
                    this);
            bannerPager.setAdapter(bannerAdapter);
            bannerPager.setVisibility(View.VISIBLE);
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
            setCounter(0);
        }
    }

    private void setCounter(int count) {
        if (counterTxt == null){
            counterTxt = (TextView) findViewById(R.id.counter);
        }
        if (homeDocument.getBanners() == null || homeDocument.getBanners().size() <=0) {
            bannerPager.setVisibility(View.GONE);
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
            if (bannerPager.getVisibility() == View.GONE){
                bannerPager.setVisibility(View.VISIBLE);
            }
            if (counterTxt.getVisibility() == View.GONE){
                counterTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addToSlider() {
        bannerAdapter.update(homeDocument.getBanners());
    }


}