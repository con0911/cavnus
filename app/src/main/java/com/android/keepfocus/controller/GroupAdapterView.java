package com.android.keepfocus.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.GroupManagermentActivity;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;

import java.util.ArrayList;

/**
 * Created by sev_user on 9/17/2016.
 */
public class GroupAdapterView extends ArrayAdapter<ParentGroupItem> {
    private Activity activity;
    private static LayoutInflater inflater = null;

    public GroupAdapterView(Activity activity, int resource,
                          int textViewResourceId, ArrayList<ParentGroupItem> objects) {
        super(activity, resource, textViewResourceId, objects);
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }
    MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
            getContext());


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.tab_group, parent, false);


        TextView nameProfile = (TextView) convertView.findViewById(R.id.titleProperties);
        final int mPosition = position;
        final ParentGroupItem profileItem = getItem(mPosition);
        nameProfile.setText(profileItem.getGroup_name());
        convertView.setOnClickListener(new OnItemClickListener( position ));
        convertView.setOnLongClickListener(new OnItemLongClickListener(position) );

        return convertView;
    }



    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;
        OnItemClickListener(int position){
            mPosition = position;
        }
        @Override
        public void onClick(View v) {
            GroupManagermentActivity groupManagermentActivity = (GroupManagermentActivity)activity;
            groupManagermentActivity.onItemClick(mPosition);
        }
    }

    private class OnItemLongClickListener  implements View.OnLongClickListener {
        private int mPosition;
        OnItemLongClickListener(int position){
            mPosition = position;
        }
        @Override
        public boolean onLongClick(View v) {
            Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake);
            v.startAnimation(shake);
            GroupManagermentActivity groupManagermentActivity = (GroupManagermentActivity)activity;
            groupManagermentActivity.onItemLongClick(mPosition);
            return false;
        }
    }

}