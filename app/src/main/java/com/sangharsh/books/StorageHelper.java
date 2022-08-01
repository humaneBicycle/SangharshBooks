package com.sangharsh.books;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sangharsh.books.model.PDFModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StorageHelper {
    public static final String BOOKMARKS="bookmarks";
    public static final String DOWNLOADED="downloaded";

    Context context;

    public StorageHelper (Context context){
        this.context = context;
    }

    public void savePDFModel(ArrayList<PDFModel> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public ArrayList<PDFModel> getArrayListOfPDFModel(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<PDFModel>>() {}.getType();
        if(gson.fromJson(json, type)!=null){
            return gson.fromJson(json, type);
        }else{
            return new ArrayList<>();
        }
    }


}
