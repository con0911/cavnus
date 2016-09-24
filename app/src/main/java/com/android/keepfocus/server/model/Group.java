package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/21/2016.
 * this class should be call GroupRequest. it's model for request object of group apis on server
 */

public class Group {

    private int id;
    private String GroupName;
    private String GroupCode;
    private String CreateBy;
    private String CreateDate;


    public Group(String GroupName) {

        this.GroupName = GroupName;

    }

    public Group(int id, String GroupName) {

        this.id = id;
        this.GroupName = GroupName;
    }


    public Group(int id) {
        this.id = id;
    }
}
