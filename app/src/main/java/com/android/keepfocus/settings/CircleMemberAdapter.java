package com.android.keepfocus.settings;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.DeviceMemberManagerment;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.fancycoverflow.FancyCoverFlow;
import com.android.keepfocus.fancycoverflow.FancyCoverFlowAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by sev_user on 11/26/2016.
 */
public class CircleMemberAdapter extends FancyCoverFlowAdapter {

    private ArrayList<ParentMemberItem> data;
    private Activity activity;

    public CircleMemberAdapter(Activity activity, ArrayList<ParentMemberItem> objects) {
        this.activity = activity;
        this.data = objects;
    }

    @Override
    public int getCount() {
        if (data.size() > 0) {
            return data.size();
        }
        return 1;
    }

    @Override
    public ParentMemberItem getItem(int position) {
        if (data.size() > 0) {
            return data.get(position);
        }
        return null;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getCoverFlowItem(final int position, View convertView, ViewGroup parent) {

        final TextView blockall;
        final TextView allowAll;
        final TextView addmember;
        final TextView name;
        final ImageView iconFamily;
        final LinearLayout blockallLayout;
        final LinearLayout allowallLayout;
        final LinearLayout addMemberLayout;

        final ParentMemberItem profileItem = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.device_adapter_layout, null, false);

        iconFamily = (ImageView) convertView.findViewById(R.id.img_center_child);
        blockall = (TextView) convertView.findViewById(R.id.txt_left_side);
        addmember = (TextView) convertView.findViewById(R.id.txt_center_side);
        allowAll = (TextView) convertView.findViewById(R.id.txt_right_side);
        name = (TextView) convertView.findViewById(R.id.family_name);
        blockall.setText("Block all");
        allowAll.setText("Allow all");
        addmember.setText(R.string.delete_device);
        blockallLayout = (LinearLayout) convertView.findViewById(R.id.btn_left_side);
        allowallLayout = (LinearLayout) convertView.findViewById(R.id.btn_right_side);
        addMemberLayout = (LinearLayout) convertView.findViewById(R.id.btn_center_side);
        blockallLayout.setVisibility(View.VISIBLE);
        allowallLayout.setVisibility(View.VISIBLE);
        addMemberLayout.setVisibility(View.VISIBLE);

        if (profileItem.getIs_blockall() == 1) {
            blockallLayout.setBackgroundResource(R.drawable.circle_left_side_press);
            blockall.setText("Return to\nSchedule");
        } else {
            blockallLayout.setBackgroundResource(R.drawable.circle_left_side_no_press);
            blockall.setText("Block All");
        }

        if (profileItem.getIs_alowall() == 1) {
            allowAll.setText("Return to\nSchedule");
            allowallLayout.setBackgroundResource(R.drawable.circle_right_side_press);
        } else {
            allowallLayout.setBackgroundResource(R.drawable.circle_right_side_no_press);
            allowAll.setText("Allow All");
        }
        if (profileItem.getIcon_array_byte() == null) {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(),R.drawable.person);
            iconFamily.setImageBitmap(icon);
        } else {
            Bitmap bmp = BitmapFactory.decodeByteArray(profileItem.getIcon_array_byte(),0,profileItem.getIcon_array_byte().length);
            iconFamily.setImageBitmap(bmp);
        }

        name.setText(profileItem.getName_member());

        convertView.setTag(String.valueOf(position));

        blockall.setOnClickListener(onClickListener(position));
        allowAll.setOnClickListener(onClickListener(position));
        addmember.setOnClickListener(onClickListener(position));
        iconFamily.setOnClickListener(onClickListener(position));
        //iconFamily.setOnLongClickListener(onLongClickListener(position));

        convertView.setLayoutParams(new FancyCoverFlow.LayoutParams(600,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return convertView;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DeviceMemberManagerment deviceMemberManagerment = (DeviceMemberManagerment) activity;
                final ParentMemberItem profileItem = getItem(position);
                switch (v.getId()) {
                    case R.id.txt_left_side:
                        if (profileItem.getIs_blockall() == 0) {
                            deviceMemberManagerment.blockAll(position);
                        } else {
                            deviceMemberManagerment.unBlockAll(position);
                        }
                        break;
                    case R.id.txt_right_side:
                        if (profileItem.getIs_alowall() == 0) {
                            deviceMemberManagerment.allowAll(position);
                        } else {
                            deviceMemberManagerment.unAllowAll(position);
                        }
                        break;
                    case R.id.img_center_child:
                        deviceMemberManagerment.changeIcon(position);
                        break;
                    case R.id.txt_center_side:
/*                        View parent2 = (View) v.getParent();
                        parent2.setPressed(true);
                        deviceMemberManagerment.showDetail(position);*/
                        deviceMemberManagerment.onItemLongClick(position);
                        break;
                }

            }
        };
    }

/*    private View.OnLongClickListener onLongClickListener(final int position){
        return new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                DeviceMemberManagerment deviceMemberManagerment = (DeviceMemberManagerment) activity;
                switch (v.getId()){
                    case R.id.img_center_child:
                        deviceMemberManagerment.onItemLongClick(position);
                }
                return true;
            }
        };
    }*/
}