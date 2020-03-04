package com.example.nearme;

import android.content.Context;

import java.util.ArrayList;

class Singleton {
    private static final Singleton ourInstance = new Singleton();

    static Singleton getInstance() {
        return ourInstance;
    }
ArrayList<String> lsData = new ArrayList<>();
    Context context;
    String title;

    public String getMylat() {
        return mylat;
    }

    public void setMylat(String mylat) {
        this.mylat = mylat;
    }

    public String getMylong() {
        return mylong;
    }

    public void setMylong(String mylong) {
        this.mylong = mylong;
    }

    String mylat;
    String mylong;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    private Singleton() {
    }

    public ArrayList<String> getLsData() {
        return lsData;
    }

    public void setLsData(ArrayList<String> lsData) {
        this.lsData = lsData;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
