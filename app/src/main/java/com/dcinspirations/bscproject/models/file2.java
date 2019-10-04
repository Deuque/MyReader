package com.dcinspirations.bscproject.models;

public class file2 {
    private String name, type,url,size;

    public file2(String name, String type, String url, String size) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.size = size;
    }

    public file2() {
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
