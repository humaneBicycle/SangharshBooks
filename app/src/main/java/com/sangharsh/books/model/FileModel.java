package com.sangharsh.books.model;

import java.util.HashMap;

public class FileModel {
    String name;
    String pointingDirId;


    public FileModel(){}

    boolean isPaid;
    int price;

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public FileModel(String name, String pointingDirId) {
        this.name = name;
        this.pointingDirId = pointingDirId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPointingDirId() {
        return pointingDirId;
    }

    public void setPointingDirId(String pointingDirId) {
        this.pointingDirId = pointingDirId;
    }
}
