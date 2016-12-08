package com.android.keepfocus.receive;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;



public class DevicePolicyReceiver extends DeviceAdminReceiver{
    private static final String TAG = "DevicePolicyReceiver";
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }
}
