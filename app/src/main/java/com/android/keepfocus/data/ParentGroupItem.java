package com.android.keepfocus.data;

import java.util.ArrayList;

/**
 * Created by nguyenthong on 9/13/2016.
 */
public class ParentGroupItem {
    private int id_group;
    private String group_name;
    private String group_code;
    private String create_date;
    private int id_group_server;


    private String icon_uri;
    private ArrayList<ParentMemberItem> listMember;

    public ParentGroupItem() {
        this.id_group = -1;
        this.group_name = "";
        this.group_code = "";
        this.create_date = "";
        this.id_group_server = -1;
        this.icon_uri = "";
        listMember = new ArrayList<ParentMemberItem>();
    }


    public String getIcon_uri() {
        return icon_uri;
    }

    public void setIcon_uri(String icon_uri) {
        this.icon_uri = icon_uri;
    }

    public int getId_group() {
        return id_group;
    }

    public void setId_group(int id_group) {
        this.id_group = id_group;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_code() {
        return group_code;
    }

    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public ArrayList<ParentMemberItem> getListMember() {
        return listMember;
    }

    public void setListMember(ArrayList<ParentMemberItem> listMember) {
        this.listMember = listMember;
    }

    public int getId_group_server() {
        return id_group_server;
    }

    public void setId_group_server(int id_group_server) {
        this.id_group_server = id_group_server;
    }
}
