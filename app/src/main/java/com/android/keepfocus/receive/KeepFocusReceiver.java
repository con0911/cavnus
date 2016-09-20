package com.android.keepfocus.receive;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.keepfocus.service.KeepFocusMainService;
import com.android.keepfocus.service.ServiceBlockApp;

public class KeepFocusReceiver extends BroadcastReceiver {

	private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
	public final String TAG = "KeepFocusReceiver";
	Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d(TAG, "onReceiver action : " + intent.getAction());
        String action = intent.getAction();
        mContext = context;
        switch (action) {
		case BOOT_ACTION:
			Intent itServiceBlockLaunch = new Intent(context,
                    ServiceBlockApp.class);
            mContext.startService(itServiceBlockLaunch);
            Log.d(TAG, "start service thang Ly Thong");
            startServices();
			break;
//		case Utils.ACTION_CATCH_NOTIFICATION:
//			Log.v(TAG, "Received message"); 
//            Log.v(TAG, "intent.getAction() :: " + intent.getAction());
//            Log.v(TAG, "intent.getStringExtra(Constants.EXTRA_PACKAGE) :: " + intent.getStringExtra(Utils.EXTRA_PACKAGE));
//            Log.v(TAG, "intent.getStringExtra(Constants.EXTRA_MESSAGE) :: " + intent.getStringExtra(Utils.EXTRA_MESSAGE));
//            String title = intent.getStringExtra(Utils.EXTRA_TITLE);
//            String mgs = intent.getStringExtra(Utils.EXTRA_MESSAGE);
//            String pkgName = intent.getStringExtra(Utils.EXTRA_PACKAGE);
//            Calendar c = Calendar.getInstance();
//    		Date rightNow = c.getTime();
//    		if (mDatabaseHelper.isAppOrNotifiBlock(pkgName,
//    				Utils.DAY_OF_WEEK[rightNow.getDay()], rightNow.getHours(),
//    				rightNow.getMinutes(), Utils.NOTIFICATION_BLOCK)) {
//    				try {
//    					NotificationItemMissHistory aNotiHistory = new NotificationItemMissHistory();
//    					int appId = mDatabaseHelper.getAppItemIdByPackageName(pkgName);
//    					aNotiHistory.setmApp_id(appId);
//    					aNotiHistory.setmNotiTitle(title);
//    					aNotiHistory.setmNotiSumary(mgs);
//    					aNotiHistory.setPakageName(pkgName);
//    					aNotiHistory.setmNotiDate((int)rightNow.getTime());
//    					mDatabaseHelper.addNotificationHistoryItemtoDb(aNotiHistory);
//    					NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//    					nMgr.cancelAll();
//    					Log.d(TAG, "onNotificationPosted : cancelAllNotifications with packageName : " + intent.getStringExtra(Utils.EXTRA_PACKAGE));
//    				} catch (Exception e) {
//    					// TODO: handle exception
//    					Log.d(TAG, "error Cancel Notifications");
//    				}
//    		}
//            break;
		default:
			break;
		}
        
        //context.startService(new Intent(context,KeepFocusMainService.class));
    }
    private void startServices(){
//    	Intent mServiceIntent = new Intent();
//    	mServiceIntent.setAction("android.service.notification.NotificationListenerService");
    	mContext.startService(new Intent(mContext,KeepFocusMainService.class));
    	Log.d(TAG, "Start myService");
    }

}
