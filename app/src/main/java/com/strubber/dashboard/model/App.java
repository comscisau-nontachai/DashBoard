package com.strubber.dashboard.model;

/**
 * Created by delaroy on 2/10/17.
 */
public class App {

    private int mDrawable;
    private String mName;

    public App(String name, int drawable){
        mName = name;
        mDrawable = drawable;
    }

    public int getDrawable() { return mDrawable; }
    public String getName() { return mName; }

}
