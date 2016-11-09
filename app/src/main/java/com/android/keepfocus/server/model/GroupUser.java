package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 11/9/2016.
 */
public class GroupUser {
    private int id;
    private int group_id;
    private int deviceid;
    private String license_string;


    public GroupUser(int id, int group_id, int deviceid, String license_string) {
        this.id = id;
        this.group_id = group_id;
        this.deviceid = deviceid;
        this.license_string = license_string;
    }
}
