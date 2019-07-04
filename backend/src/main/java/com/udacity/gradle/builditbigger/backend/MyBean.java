package com.udacity.gradle.builditbigger.backend;

import com.example.android.javajokes.Joker;

import java.util.ArrayList;

/** The object model for the data we are sending through endpoints */
public class MyBean {

    private ArrayList<String> mJokes;

    public ArrayList<String> getJavaJokes () {
        return mJokes;
    }

    public void setJokes () {
        Joker joker = new Joker();
        ArrayList<String> jokes = joker.getJokes();
        mJokes = jokes;
    }
}