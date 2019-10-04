package com.dcinspirations.bscproject.models;

public class user {
    private String key,username,status;



    public user() {
    }

    public user(String key, String name, String status) {
        this.key = key;
        this.username = name;
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
