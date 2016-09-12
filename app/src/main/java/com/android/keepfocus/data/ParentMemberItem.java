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
    private ArrayList<ParentProfileItem> listProfile;

    public ParentMemberItem() {
        this.id_member = -1;
        this.name_member = "";
        this.type_member = 1;
        this.image_member = "";
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
}
