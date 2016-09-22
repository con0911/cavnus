package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/22/2016.
 */
public class User {
    private int id;
    private String email;
    private String password;
    private String register_date;
    private String device_code;
    private String registation_id;

    public User(int id, String email, String password, String register_date, String device_code, String registation_id) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.register_date = register_date;
        this.device_code = device_code;
        this.registation_id = registation_id;
    }

    public User() {

    }
}
