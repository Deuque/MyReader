package com.dcinspirations.bscproject.models;

public class TTsSettings {
    private int sr,pitch;

    public TTsSettings(int sr, int pitch) {
        this.sr = sr;
        this.pitch = pitch;
    }

    public TTsSettings() {
    }

    public int getSr() {
        return sr;
    }

    public void setSr(int sr) {
        this.sr = sr;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }
}
