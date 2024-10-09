package com.talmiron.lishcaautoinvities;

import java.time.LocalDateTime;

public class Meeting {
    private int id;
    private String name;
    private String title;
    private String date;
    private String hour;

    public Meeting(int id, String name, String title, String date, String hour) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.hour = hour;
        this.date = date;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getHour() { return hour; }
}

