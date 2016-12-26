package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 12/26/2016.
 */
public class Manager {
    private int id;
    private String manager_name;
    private String device_model;
    private String device_type;
    private String registration_id;

    public Manager(int id, String manager_name, String device_model, String device_type, String registration_id) {
        this.id = id;
        this.manager_name = manager_name;
        this.device_model = device_model;
        this.device_type = device_type;
        this.registration_id = registration_id;
    }

    public Manager() {
    }

    public Manager(String manager_name, int id, String registration_id) {
        this.manager_name = manager_name;
        this.id = id;
        this.registration_id = registration_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }
}
