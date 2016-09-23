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


    public Group(String GroupName, String GroupCode, String CreateBy, String CreateDate) {

        this.GroupName = GroupName;
        this.GroupCode = GroupCode;
        this.CreateBy = CreateBy;
        this.CreateDate = CreateDate;

    }

    public Group(int id, String GroupName, String GroupCode, String CreateBy, String CreateDate) {

        this.id = id;
        this.GroupName = GroupName;
        this.GroupCode = GroupCode;
        this.CreateBy = CreateBy;
        this.CreateDate = CreateDate;
    }


    public Group() {

    }
}
