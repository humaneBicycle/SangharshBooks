package com.sangharsh.books.model;

public class PDFModel {
    boolean isOfflineAvailable;
    String url;
    String offlinePath;//null if not downloaded
    String name;
    String pointingDir;
    boolean isBookMarked;

    boolean isPaid;
    int price;


    public boolean isPaid(){
        return isPaid;
    }


    public int getPrice() {
        return price;
    }

    public boolean isBookMarked() {
        return isBookMarked;
    }

    public void setBookMarked(boolean bookMarked) {
        isBookMarked = bookMarked;
    }

    public PDFModel (){}

    public PDFModel(boolean isOfflineAvailable, String url, String offlinePath, String name,String pointingDir,boolean isBookMarked) {
        this.isOfflineAvailable = isOfflineAvailable;
        this.url = url;
        this.offlinePath = offlinePath;
        this.name = name;
        this.pointingDir = pointingDir;
        this.isBookMarked=isBookMarked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOfflineAvailable() {
        return isOfflineAvailable;
    }

    public void setOfflineAvailable(boolean offlineAvailable) {
        isOfflineAvailable = offlineAvailable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOfflinePath() {
        return offlinePath;
    }

    public void setOfflinePath(String offlinePath) {
        this.offlinePath = offlinePath;
    }

    public String getPointingDir() {
        return pointingDir;
    }

    public void setPointingDir(String pointingDir) {
        this.pointingDir = pointingDir;
    }
}
