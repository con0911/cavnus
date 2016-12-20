package com.android.keepfocus.data;

import android.util.Log;

import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

/**
 * Created by nguyenthong on 9/13/2016.
 */
public class ParentProfileItem {
    private int id_profile;
    private String day_profile;
    private String name_profile;
    private boolean isActive;
    private int id_profile_server;
    private ArrayList<ParentTimeItem> listTimer;
    private ArrayList<ParentAppItem> listAppBlock;
    private static final String TAG = "ParentProfileItem";

    public ParentProfileItem() {
        this.id_profile = -1;
        this.day_profile = "";
        this.name_profile = "";
        this.isActive = true;
        this.id_profile_server = -1;
        listTimer = new ArrayList<ParentTimeItem>();
        listAppBlock = new ArrayList<ParentAppItem>();
    }

    public int getId_profile() {
        return id_profile;
    }

    public void setId_profile(int id_profile) {
        this.id_profile = id_profile;
    }

    public String getDay_profile() {
        return day_profile;
    }

    public void setDay_profile(String day_profile) {
        this.day_profile = day_profile;
    }

    public String getName_profile() {
        return name_profile;
    }

    public void setName_profile(String name_profile) {
        this.name_profile = name_profile;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ArrayList<ParentTimeItem> getListTimer() {
        return listTimer;
    }

    public void setListTimer(ArrayList<ParentTimeItem> listTimer) {
        this.listTimer = listTimer;
    }

    public ArrayList<ParentAppItem> getListAppBlock() {
        return listAppBlock;
    }

    public void setListAppBlock(ArrayList<ParentAppItem> listAppBlock) {
        this.listAppBlock = listAppBlock;
    }

    public int getId_profile_server() {
        return id_profile_server;
    }

    public void setId_profile_server(int id_profile_server) {
        this.id_profile_server = id_profile_server;
    }

    public boolean checkIsContainDay(int day) {
        if (day < 0 || day > 6) {
            Log.e(TAG, "Out of index MainUtils.DAY_OF_WEEK " + day);
            return false;
        }
        String dayStr = MainUtils.DAY_OF_WEEK[day];
        if (this.day_profile.contains(dayStr)) {
            return true;
        } else {
            return false;
        }
    }
}
