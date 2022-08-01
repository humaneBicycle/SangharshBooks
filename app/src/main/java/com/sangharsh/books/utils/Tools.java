package com.sangharsh.books.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Tools {
//    public static String apiKey(){
//        return Keys.keys[new Random().nextInt(Keys.keys.length-1)];
//    }
//    public static String capitaliseFirst(String input){
//        String out = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
//        return out;
//    }
//
//    public static int getSelectedPosition(ArrayList<Workout> workouts, String presentDay){
//        for (int i = 0; i < workouts.size(); i++){
//            if (workouts.get(i).getDayName().equals(presentDay)){
//                return i;
//            }
//        }
//        return 0;
//    }
//
//    public static String videoId(String url){
//        String id = "";
//        URL ytLink = null;
//        try {
//            ytLink = new URL(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        if (url.contains("https://youtu.be")) {
//            id = ytLink.getFile();
//            id=id.substring(1,id.length());
//        } else if (url.contains("https://www.youtube.com/watch?v=")) {
//            id = ytLink.getQuery();
//            id = id.substring(2, id.length());
//        }
//        return id;
//    }

    public String getTimeStamp(){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return timeStamp;
    }

    public String getTimeStamp(String nameOfFile){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return timeStamp.substring(0, timeStamp.length()-2) + nameOfFile + timeStamp.substring(timeStamp.length()-2, timeStamp.length()-1);
    }

}