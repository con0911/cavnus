package com.android.keepfocus.service;

import java.util.Calendar;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.android.keepfocus.data.ChildNotificationItemMissHistory;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.utils.MainUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class KeepFocusMainService extends NotificationListenerService {

	private MainDatabaseHelper mKeepFocusDataHelper;
	private String TAG = "KeepFocusMainService";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mKeepFocusDataHelper = new MainDatabaseHelper(
				getApplicationContext());
		Log.d(TAG, "onCreate() KeepFocusMainService object and KeepFocusDateHelper object");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "Notification Service be Destroy");
	}
/* Method check when any Notifications fire to Notification Bar
 * */
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		Date rightNow = c.getTime();
		Log.d(TAG, "onNotificationPosted");
		if (mKeepFocusDataHelper.isAppOrNotifiBlock(
				MainUtils.DAY_OF_WEEK[rightNow.getDay()], rightNow.getHours(),
				rightNow.getMinutes(), MainUtils.NOTIFICATION_BLOCK)) {
				try {
					addNotificationsToDb(sbn);
					KeepFocusMainService.this.cancelAllNotifications();
					Log.d(TAG, "onNotificationPosted : cancelAllNotifications with packageName : " + sbn.getPackageName());
				} catch (Exception e) {
					// TODO: handle exception
					Log.d(TAG, "error Cancel Notifications");
				}
		}
	}
	
   @Override
public void onNotificationRemoved(StatusBarNotification sbn) {
	// TODO Auto-generated method stub
	Log.d(TAG, "Notification removed");
}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void addNotificationsToDb(StatusBarNotification sbn){
		ChildNotificationItemMissHistory aNotiHistory = new ChildNotificationItemMissHistory();
		if(mKeepFocusDataHelper == null){
			mKeepFocusDataHelper = new MainDatabaseHelper(getApplicationContext());
		}
		Notification aNotification = (Notification) sbn.getNotification();
		Bundle extras = aNotification.extras;
		String title = extras.getString(MainUtils.EXTRA_TITLE);
		String text = extras.getCharSequence(MainUtils.EXTRA_NOTI_CONTENT).toString();
		int app_id = mKeepFocusDataHelper.getAppItemIdByPackageName(sbn.getPackageName());
		aNotiHistory.setmApp_id(app_id);
		aNotiHistory.setmNotiTitle(title);
		aNotiHistory.setmNotiSumary(text);
		aNotiHistory.setPakageName(sbn.getPackageName());
		aNotiHistory.setmNotiDate((int) sbn.getPostTime());
		
		mKeepFocusDataHelper.addNotificationHistoryItemtoDb(aNotiHistory);
		}
}
