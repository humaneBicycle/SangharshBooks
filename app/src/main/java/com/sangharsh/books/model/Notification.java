package com.sangharsh.books.model;

public class Notification {
    String title;
    String body;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Notification(String title, String body, String id) {
        this.title = title;
        this.body = body;
        this.id=id;
    }
    public  Notification(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
