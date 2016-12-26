package com.android.keepfocus.data;

/**
 * Created by nguyenthong on 12/26/2016.
 */
public class ParentManagementItem {
    private int id_manager;
    private int id_manager_sever;
    private String manager_name;
    private String device_model;
    private String device_type;

    public ParentManagementItem() {
        this.id_manager = -1;
        this.id_manager_sever = -1;
        this.manager_name = "";
        this.device_model = "";
        this.device_type = "";
    }

    public int getId_manager() {
        return id_manager;
    }

    public void setId_manager(int id_manager) {
        this.id_manager = id_manager;
    }

    public int getId_manager_sever() {
        return id_manager_sever;
    }

    public void setId_manager_sever(int id_manager_sever) {
        this.id_manager_sever = id_manager_sever;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }
}
