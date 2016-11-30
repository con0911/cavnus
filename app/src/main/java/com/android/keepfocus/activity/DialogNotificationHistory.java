package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.android.keepfocus.R;
import com.android.keepfocus.data.ChildNotificationItemMissHistory;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.utils.BackgroundContainer;
import com.android.keepfocus.utils.NotiMissingModelArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogNotificationHistory extends Dialog {
	static final int SHOW_MISSING_NOTIFICATION_ID = 0;
	private Context mContext;
	private MainDatabaseHelper mDataHelper;
	private boolean mSwiping = false;
	private boolean mItemPressed = false;
	private static final int SWIPE_DURATION = 250;
	private static final int MOVE_DURATION = 150;
	private ListView lv;
	private ChildSchedulerActivity mActivity;
	private NotiMissingModelArrayAdapter notiAdapter;
	private BackgroundContainer mBackgroundContainer;
	private ArrayList<ChildNotificationItemMissHistory> mListMissingNotification;
	private HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
	// private static int mNotifCount = 0;
	private View mClearNotifications;

	public DialogNotificationHistory(Context mContext,
									 ChildSchedulerActivity activity) {
		// TODO Auto-generated constructor stub
		super(mContext);
		this.mContext = mContext;
		this.mActivity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDataHelper = new MainDatabaseHelper(mContext);
		setContentView(R.layout.list_notification_missing);
		setCancelable(false);
		setCanceledOnTouchOutside(true);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);
		lv = (ListView) findViewById(R.id.lvNotificationMissing);
		mListMissingNotification = mDataHelper.getListNotificaionHistoryItem();
		notiAdapter = new NotiMissingModelArrayAdapter((Activity) mContext,
				R.layout.listview_row_item, mListMissingNotification,
				mTouchListener);
		lv.setAdapter(notiAdapter);
		mClearNotifications = findViewById(R.id.btnClearAll);
		mClearNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("trungdh", "Clear all notifications clicked");
				mDataHelper.deleteAllNotifications(mListMissingNotification);
				notiAdapter.clear();
				mActivity.setNotifCount(0);
				dismiss();
			}
		});
	}

	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

		float mDownX;
		private int mSwipeSlop = -1;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (mSwipeSlop < 0) {
				mSwipeSlop = ViewConfiguration.get(mContext)
						.getScaledTouchSlop();
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mItemPressed) {
					// Multi-item swipes not handled
					return false;
				}
				mItemPressed = true;
				mDownX = event.getX();
				break;
			case MotionEvent.ACTION_CANCEL:
				v.setAlpha(1);
				v.setTranslationX(0);
				mItemPressed = false;
				break;
			case MotionEvent.ACTION_MOVE: {
				float x = event.getX() + v.getTranslationX();
				float deltaX = x - mDownX;
				float deltaAbs = Math.abs(deltaX);
				if (!mSwiping) {
					if (deltaAbs > mSwipeSlop) {
						mSwiping = true;
						lv.requestDisallowInterceptTouchEvent(true);
						mBackgroundContainer.showBackground(v.getTop(),
								v.getHeight());
					}
				}
				if (mSwiping) {
					v.setTranslationX(x - mDownX);
					v.setAlpha(1 - deltaAbs / v.getWidth());
				}
			}
				break;
			case MotionEvent.ACTION_UP: {
				// User let go - figure out whether to animate the view out, or
				// back into place
				if (mSwiping) {
					float x = event.getX() + v.getTranslationX();
					float deltaX = x - mDownX;
					float deltaXAbs = Math.abs(deltaX);
					float fractionCovered;
					float endX;
					float endAlpha;
					final boolean remove;
					if (deltaXAbs > v.getWidth() / 4) {
						// Greater than a quarter of the width - animate it out
						fractionCovered = deltaXAbs / v.getWidth();
						endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
						endAlpha = 0;
						remove = true;
					} else {
						// not far enough - animate it back
						fractionCovered = 1 - (deltaXAbs / v.getWidth());
						endX = 0;
						endAlpha = 1;
						remove = false;
					}
					// Animate position and alpha of swiped item
					// NOTE: This is a simplified version of swipe behavior, for
					// the
					// purposes of this demo about animation. A real version
					// should use
					// velocity (via the VelocityTracker class) to send the item
					// off or
					// back at an appropriate speed.
					long duration = (int) ((1 - fractionCovered) + SWIPE_DURATION);
					lv.setEnabled(false);
					v.animate().setDuration(duration).alpha(endAlpha)
							.translationX(endX).withEndAction(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									v.setAlpha(1);
									v.setTranslationX(0);
									if (remove) {
										animateRemoval(lv, v);
									} else {
										mBackgroundContainer.hideBackground();
										mSwiping = false;
										lv.setEnabled(true);
									}
								}
							});
				} else {
					readNotiItemSelected(lv, v);
				}
			}
				mItemPressed = false;
				break;
			default:
				return false;
			}
			return true;
		}
	};

	/**
	 * This method animates all other views in the ListView container (not
	 * including ignoreView) into their final positions. It is called after
	 * ignoreView has been removed from the adapter, but before layout has been
	 * run. The approach here is to figure out where everything is now, then
	 * allow layout to run, then figure out where everything is after layout,
	 * and then to run animations between all of those start/end positions.
	 */
	private void animateRemoval(final ListView listview, View viewToRemove) {
		int firstVisiblePosition = listview.getFirstVisiblePosition();
		for (int i = 0; i < listview.getChildCount(); ++i) {
			View child = listview.getChildAt(i);
			if (child != viewToRemove) {
				int position = firstVisiblePosition + i;
				long itemId = notiAdapter.getItemId(position);
				mItemIdTopMap.put(itemId, child.getTop());
			}
		}
		// Delete the item from the adapter
		int position = lv.getPositionForView(viewToRemove);
		ChildNotificationItemMissHistory a_NotiItemMissRemoved = mListMissingNotification
				.get(position);
		mDataHelper.deleteNotificationHistoryItemById(a_NotiItemMissRemoved
				.getmNotiItem_id());
		notiAdapter.remove(notiAdapter.getItem(position));
		mActivity.setNotifCount(notiAdapter.getCount());

		final ViewTreeObserver observer = listview.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				observer.removeOnPreDrawListener(this);
				boolean firstAnimation = true;
				int firstVisiblePosition = listview.getFirstVisiblePosition();
				for (int i = 0; i < listview.getChildCount(); ++i) {
					final View child = listview.getChildAt(i);
					int position = firstVisiblePosition + i;
					long itemId = notiAdapter.getItemId(position);
					Integer startTop = mItemIdTopMap.get(itemId);
					int top = child.getTop();
					if (startTop != null) {
						if (startTop != top) {
							int delta = startTop - top;
							child.setTranslationY(delta);
							child.animate().setDuration(MOVE_DURATION)
									.translationY(0);
							if (firstAnimation) {
								child.animate().withEndAction(new Runnable() {
									public void run() {
										mBackgroundContainer.hideBackground();
										mSwiping = false;
										lv.setEnabled(true);
									}
								});
								firstAnimation = false;
							}
						}
					} else {
						// Animate new views along with the others. The catch is
						// that they did not
						// exist in the start state, so we must calculate their
						// starting position
						// based on neighboring views.
						int childHeight = child.getHeight()
								+ listview.getDividerHeight();
						startTop = top + (i > 0 ? childHeight : -childHeight);
						int delta = startTop - top;
						child.setTranslationY(delta);
						child.animate().setDuration(MOVE_DURATION)
								.translationY(0);
						if (firstAnimation) {
							child.animate().withEndAction(new Runnable() {
								public void run() {
									mBackgroundContainer.hideBackground();
									mSwiping = false;
									lv.setEnabled(true);
								}
							});
							firstAnimation = false;
						}
					}
				}
				mItemIdTopMap.clear();
				return true;
			}
		});
		if (mListMissingNotification.size() == 0) {
			this.dismiss();
			mActivity.setNotifCount(0);
		}
	}

	private void readNotiItemSelected(final ListView lv, View viewSelected) {
		int firstVisiblePosition = lv.getFirstVisiblePosition();
		int position = 0;
		for (int i = 0; i < lv.getChildCount(); i++) {
			View child = lv.getChildAt(i);
			if (child == viewSelected) {
				position = firstVisiblePosition + i;
				ChildNotificationItemMissHistory aNotiItemSeleted = mListMissingNotification
						.get(position);
				PackageManager pmi = mContext.getPackageManager();
				Intent intent = null;
				intent = pmi.getLaunchIntentForPackage(aNotiItemSeleted
						.getPakageName());
				if (intent != null) {
					mContext.startActivity(intent);
				}
			}
		}
	}
}
