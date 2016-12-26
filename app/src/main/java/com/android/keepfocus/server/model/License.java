package com.android.keepfocus.server.model;

/**
 * Created by Sion on 12/15/2016.
 */
public class License {
    private String license_key;
    private String device_name;

    public License() {
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public License(String license_key, String device_name) {
        this.license_key = license_key;
        this.device_name = device_name;
    }

    public License(String license_key) {
        this.license_key = license_key;
    }

    public String getLicense_key() {
        return license_key;
    }

    public void setLicense_key(String license_key) {
        this.license_key = license_key;
    }
}
