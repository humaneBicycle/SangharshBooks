package com.sangharsh.books;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceGetter {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public PreferenceGetter(Context c){
        this.context = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void putBoolean(String key, boolean b){
        editor.putBoolean(key,b).commit();
    }

    public boolean getBoolean(String key){
        //second argument is the value is first do not exist
        return preferences.getBoolean(key,false);
    }

}