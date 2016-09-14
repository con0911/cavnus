package com.android.keepfocus.data;

/**
 * Created by nguyenthong on 9/13/2016.
 */
public class ParentAppItem {
    private int id_app_parent;
    private String namePackage;
    private String nameApp;
    private String iconApp;

    public ParentAppItem() {
        this.id_app_parent = -1;
        this.namePackage = "";
        this.nameApp = "";
        this.iconApp = "";
    }

    public int getId_app_parent() {
        return id_app_parent;
    }

    public void setId_app_parent(int id_app_parent) {
        this.id_app_parent = id_app_parent;
    }

    public String getNamePackage() {
        return namePackage;
    }

    public void setNamePackage(String namePackage) {
        this.namePackage = namePackage;
    }

    public String getNameApp() {
        return nameApp;
    }

    public void setNameApp(String nameApp) {
        this.nameApp = nameApp;
    }

    public String getIconApp() {
        return iconApp;
    }

    public void setIconApp(String iconApp) {
        this.iconApp = iconApp;
    }
}
