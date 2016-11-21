package com.android.keepfocus.data;

import java.util.ArrayList;

/**
 * Created by nguyenthong on 9/13/2016.
 */
public class ParentMemberItem {
    private int id_member;
    private String name_member;
    private int type_member;
    private String image_member;
    private int id_member_server;
    private int is_blockall;
    private int is_alowall;
    private ArrayList<ParentProfileItem> listProfile;

    public ParentMemberItem() {
        this.id_member = -1;
        this.name_member = "";
        this.type_member = 1;
        this.image_member = "";
        this.id_member_server = -1;
        this.is_alowall = 0;
        this.is_blockall = 0;
        this.listProfile = new ArrayList<ParentProfileItem>();
    }

    public int getId_member() {
        return id_member;
    }

    public void setId_member(int id_member) {
        this.id_member = id_member;
    }

    public String getName_member() {
        return name_member;
    }

    public void setName_member(String name_member) {
        this.name_member = name_member;
    }

    public int getType_member() {
        return type_member;
    }

    public void setType_member(int type_member) {
        this.type_member = type_member;
    }

    public String getImage_member() {
        return image_member;
    }

    public void setImage_member(String image_member) {
        this.image_member = image_member;
    }

    public ArrayList<ParentProfileItem> getListProfile() {
        return listProfile;
    }

    public void setListProfile(ArrayList<ParentProfileItem> listProfile) {
        this.listProfile = listProfile;
    }

    public int getId_member_server() {
        return id_member_server;
    }

    public void setId_member_server(int id_member_server) {
        this.id_member_server = id_member_server;
    }

    public int getIs_alowall() {
        return is_alowall;
    }

    public void setIs_alowall(int is_alowall) {
        this.is_alowall = is_alowall;
    }

    public int getIs_blockall() {
        return is_blockall;
    }

    public void setIs_blockall(int is_blockall) {
        this.is_blockall = is_blockall;
    }
}
