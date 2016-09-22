package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/21/2016.
 */
public class Header {
    private String Email;
    private String DeviceCode;
    private String RegistationId;
    private String Password;

    public Header() {

    }

    public Header(String pEmail, String pDeviceCode, String pRegistationId, String pPassword) {
        Email = pEmail;
        DeviceCode = pDeviceCode;
        RegistationId = pRegistationId;
        Password = pPassword;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        DeviceCode = deviceCode;
    }

    public String getRegistationId() {
        return RegistationId;
    }

    public void setRegistationId(String registationId) {
        RegistationId = registationId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

