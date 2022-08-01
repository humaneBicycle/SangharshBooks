package com.sangharsh.books.model;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeDocument {

    private ArrayList<Banner> banners;
    private HashMap<String, Object> extras;

    public HashMap<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(HashMap<String, Object> extras) {
        this.extras = extras;
    }

    public HomeDocument(ArrayList<Banner> banners, HashMap<String, Object> extras) {
        this.banners = banners;
        this.extras = extras;
    }

    public HomeDocument(ArrayList<Banner> banners) {
        this.banners = banners;
    }

    public HomeDocument() {
    }


    public ArrayList<Banner> getBanners() {
        return banners;
    }

    public void setBanners(ArrayList<Banner> banners) {
        this.banners = banners;
    }
}
