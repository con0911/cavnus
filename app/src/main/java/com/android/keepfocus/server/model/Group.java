package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/21/2016.
 * this class should be call GroupRequest. it's model for request object of group apis on server
 */

public class Group {

    private int id;
    private String group_name;
    private String group_code;
    private String create_by;
    private String create_date;


    public Group(String group_name) {

        this.group_name = group_name;

    }

    /*public Group(int id, String group_name) {

        this.id = id;
        this.group_name = group_name;
    }*/


    public Group(int id) {
        this.id = id;
    }
    public Group(int id,String group_code) {
        this.id = id;
        this.group_code = group_code;
    }

    public Group(String create_by, String group_code) {
        this.create_by = create_by;
        this.group_code = group_code;
    }

    public Group(int id, String group_name, String group_code) {
        this.group_name = group_name;
        this.group_code = group_code;
        this.id = id;
    }
}
