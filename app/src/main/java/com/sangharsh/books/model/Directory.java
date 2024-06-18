package com.sangharsh.books.model;

import java.util.ArrayList;

public class Directory {
    int depth;//used to fetch first screen list
    ArrayList<FileModel> files;
    ArrayList<PDFModel> pdfModels;
    ArrayList<ShortTest> tests;
    String name;
    String path;
    boolean isPaid;
    int price;


    public boolean isPaid(){
        return isPaid;
    }


    public int getPrice() {
        return price;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Directory(){}

    public Directory( int depth,ArrayList<FileModel> files, ArrayList<PDFModel> pdfModels,  String path,ArrayList<ShortTest> tests) {
        this.depth = depth;
        this.files = files;
        this.pdfModels = pdfModels;
        this.path = path;
        this.tests = tests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ShortTest> getTests() {
        return tests;
    }

    public void setTests(ArrayList<ShortTest> tests) {
        this.tests = tests;
    }

    public ArrayList<FileModel> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FileModel> files) {
        this.files = files;
    }

    public ArrayList<PDFModel> getPdfModels() {
        return pdfModels;
    }

    public void setPdfModels(ArrayList<PDFModel> pdfModels) {
        this.pdfModels = pdfModels;
    }
}