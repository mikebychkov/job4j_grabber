package com.grabber;

import java.util.Calendar;

public class Post {
    private int id;
    private String name;
    private String text;
    private String link;
    private Calendar created;

    public Post(int id, String name, String text, String link, Calendar created) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.link = link;
        this.created = created;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        return this.text;
    }

    public String getLink() {
        return this.link;
    }

    public Calendar getCreated() {
        return this.created;
    }
}
