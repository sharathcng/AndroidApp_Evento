package com.example.evento.Model;

public class Events {

    private String name,poster,date,time,description;

    public Events() {
    }

    public Events(String name, String poster, String date, String time, String description) {
        this.name = name;
        this.poster = poster;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
