package com.sangharsh.books.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sangharsh.books.fragments.BannerFragment;
import com.sangharsh.books.fragments.GreetingFragment;
import com.sangharsh.books.model.Banner;

import java.util.ArrayList;

public class BannerPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Banner> banners;
    FragmentManager fragmentManager;
    BannerFragment fragment;


    public BannerPagerAdapter(@NonNull FragmentManager fm, ArrayList<Banner> banners, Context context) {
        super(fm);
        fragmentManager = fm;
        this.banners = banners;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position < banners.size()){
             fragment = new BannerFragment(banners.get(position)
                    .getImageUrl(), banners.get(position).getRedirectUrl(), banners.get(position).getCategory(),
                    banners.get(position).getSubcategory(), banners.get(position).getText());
            return fragment;
        } else {
            return null;
        }
    }
    public void updateImage(){
        fragment.loadImage();
    }

    public void update(ArrayList<Banner> newList){
        this.banners = newList;
        notifyDataSetChanged();
    }

    public void addBanner(Banner newBanner){
        banners.add(newBanner);
    }
}
