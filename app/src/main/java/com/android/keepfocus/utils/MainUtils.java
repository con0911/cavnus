package com.android.keepfocus.utils;

import android.os.CountDownTimer;
import android.widget.Toast;

import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;

/**
 * Created by nguyenthong on 9/6/2016.
 */
public class MainUtils {
    public static final String TERMS_AND_CONDITIONS = "terms_and_conditions";
    public static final String MODE_DEVICE = "device_mode";
    public static final String NAME_DEVICE = "name_device";
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_PARENT = 1;
    public static final int MODE_ADDITION_PARENT = 2;
    public static final int MODE_CHILD = 3;
    public static final int NOTIFICATION_BLOCK = 1;
    public static final int LAUNCHER_APP_BLOCK = 2;
    public static final String REGISTATION_ID = "mode_device";
    public static final String TYPE_JOIN ="type_join";
    public static final String TYPE_LOGIN = "type_login";
    public static final String CHILD_PROFILE_SERVER_ID = "child_profile_server_id";
    public static String namePackageBlock;
    public static ParentGroupItem parentGroupItem;
    public static ParentProfileItem parentProfile;
    public static ParentMemberItem memberItem;
    public static ParentMemberItem memberItemForBlockAll;
    public static ChildKeepFocusItem childKeepFocusItem;
    public static boolean mIsEditNameGroup = false;
    public static String getRegistationId = "";//get token in login screen

    public static final String[] DAY_OF_WEEK = { "Sun", "Mon", "Tue", "Wed",
            "Thu", "Fri", "Sat" };
    public static final int timeSleep = 500;
    public static final String PACKET_APP = "com.android.keepfocus";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_PACKAGE = "extra_package";
    public static final String EXTRA_TITLE = "android.title";
    public static final String IS_BLOCK_ALL = "is_block_all";
    public static final String IS_ALLOW_ALL = "is_allow_all";
    public static final String IS_BLOCK_SETTINGS = "is_block_settings";
    public static final String EXTRA_NOTI_CONTENT = "android.text";
    public static final String GROUP_ID = "GROUP_ID";

    public static String UPDATE_CHILD_SCHEDULER = "com.android.keepfocus.UPDATE_CHILD_SCHEDULER";
    public static String UPDATE_CHILD_DEVICE = "com.android.keepfocus.UPDATE_CHILD_DEVICE";
    public static String UPDATE_FAMILY_GROUP = "com.android.keepfocus.UPDATE_FAMILY_GROUP";
    public static String UPDATE_SCHEDULER = "com.android.keepfocus.UPDATE_SCHEDULER";
    public static String BLOCK_ALL = "com.android.keepfocus.BLOCK_ALL";
    public static String UNBLOCK_ALL = "com.android.keepfocus.UNBLOCK_ALL";
    public static String ALLOW_ALL = "com.android.keepfocus.ALLOW_ALL";
    public static String UNALLOW_ALL = "com.android.keepfocus.UNALLOW_ALL";
    public static String BLOCK_SETTINGS = "com.android.keepfocus.BLOCK_SETTINGS";
    public static String UN_BLOCK_SETTINGS = "com.android.keepfocus.UN_BLOCK_SETTINGS";
    public static String PACKAGE_UNINSTALL = "com.android.keepfocus.PACKAGE_UNINSTALL";
    public static String EXIT_CHILD_TO_SETUPWIZARD = "com.android.keepfocus.EXIT_CHILD_TO_SETUPWIZARD";
    public static String EXIT_MANAGER_TO_SETUPWIZARD = "com.android.keepfocus.EXIT_MANAGER_TO_SETUPWIZARD";
    public static String MANAGER_JOIN_SUCCESS = "com.android.keepfocus.MANAGER_JOIN_SUCCESS";
    private static CountDownTimer mCDT = null;

    public static void extendDisplayTimeOfToast(final Toast toast) {
        if (mCDT == null) {
            mCDT = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long l) {
                    toast.show();
                }

                @Override
                public void onFinish() {
                    toast.show();
                }
            }.start();
        }
    }
}
