package com.android.keepfocus.data;

public class ChildAppItem {
    private int appId;
    private String namePackage;
    private String nameApp;

    public ChildAppItem() {
        this.appId = -1;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
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

}
