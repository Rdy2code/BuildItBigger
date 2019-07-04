package com.example.android.javajokes;

import java.util.ArrayList;

public class Joker {

    private static final ArrayList<String> jokeList = new ArrayList<String>() {{
        add("My first joke");
        add("My second joke");
        add("My third joke");
        add("My fourth joke");
        add("My fifth joke");
        add("My sixth joke");
        add("My seventh joke");
        add("My eighth joke");
    }};

    public Joker () {

    }

    public ArrayList<String> getJokes () {
        return jokeList;
    }
}
