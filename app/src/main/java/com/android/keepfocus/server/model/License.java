package com.android.keepfocus.server.model;

/**
 * Created by Sion on 12/15/2016.
 */
public class License {
    private String license_key;

    public License() {
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
