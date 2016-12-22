package com.android.keepfocus.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.keepfocus.activity.BlockLauncher;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.utils.MainUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by nguyenthong on 9/6/2016.
 */
public class ServiceBlockApp extends Service {
    private Context mContext;
    private static final String TAG = "BlockLaunchService";
    private Timer mainTimer;
    private String oldPackageApp;
    private String currentPackageApp;
    private boolean isVersionLagerLL;
    private MainDatabaseHelper dbHelper;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Log.d(TAG, "onCreate");
        dbHelper = new MainDatabaseHelper(mContext);
        // Check version Android
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            isVersionLagerLL = true;
        } else {
            isVersionLagerLL = false;
        }
        oldPackageApp = " ";
        currentPackageApp = " ";
        // Make Timer for Test
        mainTimer = new Timer();
        mainTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (isVersionLagerLL) {
                    getCurrentPackageNewVersion();
                } else {
                    getCurrentPackageOldVersion();
                }
                // Log.e(TAG, "Find out " + currentPackageApp);
                if (isHaveToCheck() || isBlockSettings()) {
                    oldPackageApp = currentPackageApp;
                    Log.e(TAG, "Check " + currentPackageApp);
                    Calendar c = Calendar.getInstance();
                    Date rightNow = c.getTime();
                    if (isBlockSettings() || isBlockAllNow() || dbHelper.isAppOrNotifiBlock(
                            MainUtils.DAY_OF_WEEK[rightNow.getDay()],
                            rightNow.getHours(), rightNow.getMinutes(),
                            MainUtils.LAUNCHER_APP_BLOCK)) {
                        // Need more code to block app
                        MainUtils.namePackageBlock = currentPackageApp;
                        oldPackageApp = MainUtils.PACKET_APP;
                        currentPackageApp = MainUtils.PACKET_APP;
                        Log.e(TAG, "Block app " + currentPackageApp);
                        if (BlockLauncher.isPause) {
                            BlockLauncher.finishBlock();
                        }
                        Intent i = new Intent();
                        i.setClass(mContext, BlockLauncher.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                } else {
                    // Log.e(TAG, "Don't check duplicate");
                }

            }
        }, 0, MainUtils.timeSleep);
    }

    private boolean isHaveToCheck() {
        //check is system app
        if (isSystemApp(currentPackageApp)) {
            Log.e(TAG, "isSystemApp true " + currentPackageApp);
            return false;
        }
        //Return false if isAllow is true;
        if (isAllowAllNow()) {
            return false;
        }
        if (/*!oldPackageApp.equals(currentPackageApp)
                && */ !currentPackageApp.equals(MainUtils.PACKET_APP)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isBlockAllNow() {
        SharedPreferences prefs = this.getSharedPreferences(
                MainUtils.PACKET_APP, Context.MODE_PRIVATE);
        boolean isBlock = prefs.getBoolean(MainUtils.IS_BLOCK_ALL,false);
        return isBlock;
    }

    private boolean isAllowAllNow() {
        SharedPreferences prefs = this.getSharedPreferences(
                MainUtils.PACKET_APP, Context.MODE_PRIVATE);
        boolean isAllow = prefs.getBoolean(MainUtils.IS_ALLOW_ALL,false);
        return isAllow;
    }

    private boolean isBlockSettings() {
        SharedPreferences prefs = this.getSharedPreferences(
                MainUtils.PACKET_APP, Context.MODE_PRIVATE);
        boolean isBlockSettings = prefs.getBoolean(MainUtils.IS_BLOCK_SETTINGS,false);
        if (isBlockSettings && currentPackageApp.contains("com.android.settings")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSystemApp(String packageName) {
        if(isInBlackList(packageName)) {
            return false;
        }
        PackageManager mPackageManager = mContext.getPackageManager();
        try {
            ApplicationInfo ai = mPackageManager.getApplicationInfo(
                    packageName, 0);
            // First check if it is preloaded.
            // If yes then check if it is System app or not.
            if (ai != null
                    && (ai.flags & (ApplicationInfo.FLAG_SYSTEM )) != 1) {
                // Check if signature matches
                return false;
            } else {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isInBlackList(String packageName) {
        String[] blackList = {"YouTube","Facebook","Gmail","Google +","Hangouts", "Google Play Music",
                "Google Play Movies","Google Play Games","Chrome", "Internet", "Safari", "Camera", "Play Store", "Gallery", "Photos"};
        String nameApp = "";
        PackageManager mPackageManager = mContext.getPackageManager();
        try {
            ApplicationInfo ai = mPackageManager.getApplicationInfo(
                    packageName, 0);
            if (ai != null) {
                nameApp =  mPackageManager.getApplicationLabel(ai).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<blackList.length;i++) {
            if (nameApp.equals(blackList[i])) {
                return true;
            }
        }
        return false;
    }

    private void getCurrentPackageOldVersion() {
        ActivityManager mActivityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager
                .getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        currentPackageApp = componentName.getPackageName();
    }

    @SuppressLint("NewApi")
    private void getCurrentPackageNewVersion() {
        UsageStatsManager usm = (UsageStatsManager) this
                .getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                currentPackageApp = mySortedMap.get(mySortedMap.lastKey())
                        .getPackageName();
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),
                this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}