package com.sangharsh.books.model;

public class Question {
    String question;
    String quesImgUrl;
    String option1;
    String option1ImgUrl;
    String option2;
    String option2ImgUrl;
    String option3;
    String option3ImgUrl;
    String option4;
    String option4ImgUrl;
    String correctOption;

    public Question() {
    }

    public Question(String question, String option1, String option2, String option3, String option4, String correctOption) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctOption = correctOption;
    }

    public Question(String question, String quesImgUrl, String option1, String option1ImgUrl, String option2, String option2ImgUrl, String option3, String option3ImgUrl, String option4, String option4ImgUrl, String correctOption) {
        this.question = question;
        this.quesImgUrl = quesImgUrl;
        this.option1 = option1;
        this.option1ImgUrl = option1ImgUrl;
        this.option2 = option2;
        this.option2ImgUrl = option2ImgUrl;
        this.option3 = option3;
        this.option3ImgUrl = option3ImgUrl;
        this.option4 = option4;
        this.option4ImgUrl = option4ImgUrl;
        this.correctOption = correctOption;
    }



    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuesImgUrl() {
        return quesImgUrl;
    }

    public void setQuesImgUrl(String quesImgUrl) {
        this.quesImgUrl = quesImgUrl;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption1ImgUrl() {
        return option1ImgUrl;
    }

    public void setOption1ImgUrl(String option1ImgUrl) {
        this.option1ImgUrl = option1ImgUrl;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption2ImgUrl() {
        return option2ImgUrl;
    }

    public void setOption2ImgUrl(String option2ImgUrl) {
        this.option2ImgUrl = option2ImgUrl;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption3ImgUrl() {
        return option3ImgUrl;
    }

    public void setOption3ImgUrl(String option3ImgUrl) {
        this.option3ImgUrl = option3ImgUrl;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getOption4ImgUrl() {
        return option4ImgUrl;
    }

    public void setOption4ImgUrl(String option4ImgUrl) {
        this.option4ImgUrl = option4ImgUrl;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }
}
