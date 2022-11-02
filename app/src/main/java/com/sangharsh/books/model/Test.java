package com.sangharsh.books.model;

import java.util.ArrayList;

public class Test {
    String testTitle;
    String testDescription;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    ArrayList<Question> questions;

    public Test() {
    }

    public Test(String testTitle, String testDescription, ArrayList<Question> questions) {
        this.testTitle = testTitle;
        this.testDescription = testDescription;
        this.questions = questions;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

}
