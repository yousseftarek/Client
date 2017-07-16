package com.example.mylife_mk3;

import android.app.Application;

/**
 * Created by Tarek on 18-Feb-2017.
 */

public final class MyLife extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "OpenSans-Regular.ttf");
    }
}
