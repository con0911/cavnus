package com.android.keepfocus.receive;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.keepfocus.activity.BlockLauncher;
import com.android.keepfocus.utils.MainUtils;


public class DevicePolicyReceiver extends DeviceAdminReceiver{
    private static final String TAG = "DevicePolicyReceiver";
    /** Called when this application is approved to be a device administrator. */
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        /*Toast.makeText(context, "Device admin enabled",
                Toast.LENGTH_LONG).show();*/
        Log.d(TAG, "onEnabled");
    }

    /** Called when this application is no longer the device administrator. */
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        /*Toast.makeText(context, "Device admin disabled",
                Toast.LENGTH_LONG).show();*/
        Log.d(TAG, "onDisabled");
        /*intent.setClass(context, BlockLauncher.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Log.d(TAG, "onPasswordChanged");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Log.d(TAG, "onPasswordFailed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Log.d(TAG, "onPasswordSucceeded");
    }
}
