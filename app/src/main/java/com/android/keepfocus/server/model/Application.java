package com.android.keepfocus.server.model;

import android.graphics.Bitmap;

/**
 * Created by sev_user on 9/22/2016.
 */
public class Application {
    private int id;
    private String app_name;
    private String package_name;
    private Bitmap app_icon;

    public Application(int id, String app_name, String package_name, Bitmap app_icon) {
        this.id = id;
        this.app_name = app_name;
        this.package_name = package_name;
        this.app_icon = app_icon;
    }

    public Application() {

    }
}
