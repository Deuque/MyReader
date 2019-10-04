package com.dcinspirations.bscproject.models;

public class myfile {
    private String name, type,url,time;

    public myfile(String name, String type, String url, String time) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.time = time;
    }

    public myfile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
