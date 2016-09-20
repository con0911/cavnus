package com.android.keepfocus.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.ChildSchedulerDetail;
import com.android.keepfocus.data.ChildNotificationItemMissHistory;

public class NotiMissingModelArrayAdapter extends ArrayAdapter<ChildNotificationItemMissHistory>  {
	private final String TAG = "NotiMissingModelArrayAdapter";
	private ArrayList<ChildNotificationItemMissHistory> mListNotiMissing = null;
	private Context mContext = null;
	private LayoutInflater mLayoutInflater = null;
	private OnTouchListener listener;
//	public NotiMissingModelArrayAdapter(Activity activity, ArrayList<NotificationItemMissHistory> list) {
//		// TODO Auto-generated constructor stub
//		this.mContext = activity;
//		mLayoutInflater = activity.getLayoutInflater();
//		this.mListNotiMissing = list;
//	}
	
	public NotiMissingModelArrayAdapter(Activity activity,int resourId, ArrayList<ChildNotificationItemMissHistory> list,OnTouchListener _listener){
		super(activity, R.layout.listview_row_item, list);
		this.listener = _listener;
		this.mContext = activity;
		mLayoutInflater = activity.getLayoutInflater();
		this.mListNotiMissing = list;
//		this.mListNotiMissing.addAll(list);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		if(position > mListNotiMissing.size()){
			return null;
		}
		ChildNotificationItemMissHistory aNotiItem = mListNotiMissing.get(position);
		final ViewHolder viewHolder = new ViewHolder();
		ViewHolder Holder = null;
		if(convertView == null){
			view = mLayoutInflater.inflate(R.layout.listview_row_item,  null);
			view.setTag(viewHolder);
			
			viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
			viewHolder.tvtitle = (TextView) view.findViewById(R.id.tvTitle);
			viewHolder.tvSumary = (TextView) view.findViewById(R.id.tvSumary);
			viewHolder.position = position;
			
			Holder = viewHolder;
		} else {
			view = convertView;
			Holder = ((ViewHolder) view.getTag());
		}
		if(this.listener != null){
			view.setOnTouchListener(this.listener);
		}
		Drawable iconApp = null;
		try {
			iconApp = mContext.getPackageManager().getApplicationIcon(aNotiItem.getPakageName());
		} catch (Exception e) {
			// TODO: handle exception
		}
		Holder.NotiModel = aNotiItem;
		Holder.position = position;
		Holder.tvtitle.setText(aNotiItem.getmNotiTitle());
		Holder.tvSumary.setText(aNotiItem.getmNotiSumary());
		Holder.icon.setImageDrawable(iconApp);
		return view;
	}
	
	static class ViewHolder{
		protected ImageView icon;
		protected TextView tvtitle;
		protected TextView tvSumary;
		protected int position;
		protected ChildNotificationItemMissHistory NotiModel;
//		protected int color;
//		protected int imageId;
		
//		public ViewHolder(){
//			position = 0;
//			
//		}
	}

}
