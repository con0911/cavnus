package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/22/2016.
 */

/**
 * Created by sev_user on 9/22/2016.
 */
public class Device {

    private int id;
    private String device_name;
    private String device_model;
    private String device_type;
    private String registation_id;
    private String device_code;
    private String device_mode;

    public Device(int id, String device_name, String device_model, String device_type, String registation_id, String device_code, String device_mode) {
        this.id = id;
        this.device_name = device_name;
        this.device_model = device_model;
        this.device_type = device_type;
        this.registation_id = registation_id;
        this.device_code = device_code;
        this.device_mode = device_mode;
    }

    public Device(String device_name) {

        this.device_name = device_name;

    }

    public Device(int id) {
        this.id = id;
    }


}

