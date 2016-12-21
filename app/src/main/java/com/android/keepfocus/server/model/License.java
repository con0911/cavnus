package com.android.keepfocus.server.model;

/**
 * Created by Sion on 12/15/2016.
 */
public class License {
    private String license_key;
    private int id_groupuser;
    private int is_use;

    public License() {
    }

    public License(String license_key) {
        this.license_key = license_key;
    }
    public License(String license_key, int id_groupuser, int is_use) {
        this.license_key = license_key;
        this.id_groupuser = id_groupuser;
        this.is_use = is_use;
    }

    public String getLicense_key() {
        return license_key;
    }

    public void setLicense_key(String license_key) {
        this.license_key = license_key;
    }

    public int getId_groupuser() {
        return id_groupuser;
    }

    public void setId_groupuser(int id_groupuser) {
        this.id_groupuser = id_groupuser;
    }

    public int getIs_use() {
        return is_use;
    }

    public void setIs_use(int is_use) {
        this.is_use = is_use;
    }
}
