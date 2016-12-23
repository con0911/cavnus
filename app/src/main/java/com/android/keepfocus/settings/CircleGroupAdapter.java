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
import com.android.keepfocus.activity.FamilyManagerment;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.fancycoverflow.FancyCoverFlow;
import com.android.keepfocus.fancycoverflow.FancyCoverFlowAdapter;
import com.android.keepfocus.server.request.controllers.GroupRequestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by sev_user on 11/26/2016.
 */
public class CircleGroupAdapter extends FancyCoverFlowAdapter {

    private ArrayList<ParentGroupItem> data;
    private Activity activity;
    private GroupRequestController groupRequestController;

    public CircleGroupAdapter(Activity activity, ArrayList<ParentGroupItem> objects) {
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
    public ParentGroupItem getItem(int position) {
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

        final TextView deleteGroup;
        final TextView detailGroup;
        final TextView addmember;
        final TextView name;
        final ImageView iconFamily;
        final LinearLayout blockallLayout;
        final LinearLayout allowallLayout;
        final LinearLayout addMemberLayout;

        final ParentGroupItem profileItem = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.device_adapter_layout, null, false);

        iconFamily = (ImageView) convertView.findViewById(R.id.img_center_child);
        deleteGroup = (TextView) convertView.findViewById(R.id.txt_left_side);
        addmember = (TextView) convertView.findViewById(R.id.txt_center_side);
        detailGroup = (TextView) convertView.findViewById(R.id.txt_right_side);
        name = (TextView) convertView.findViewById(R.id.family_name);
        deleteGroup.setText(R.string.delete_device);
        detailGroup.setText(R.string.devices);
        addmember.setText(R.string.edit_family_name);
        name.setText(profileItem.getGroup_name());
        blockallLayout = (LinearLayout) convertView.findViewById(R.id.btn_left_side);
        allowallLayout = (LinearLayout) convertView.findViewById(R.id.btn_right_side);
        addMemberLayout = (LinearLayout) convertView.findViewById(R.id.btn_center_side);
        blockallLayout.setVisibility(View.VISIBLE);
        allowallLayout.setVisibility(View.VISIBLE);
        addMemberLayout.setVisibility(View.VISIBLE);
        if (profileItem.getIcon_arrarByte() == null) {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(),R.drawable.images);
            iconFamily.setImageBitmap(icon);
        } else {
            Bitmap bmp = BitmapFactory.decodeByteArray(profileItem.getIcon_arrarByte(),0,profileItem.getIcon_arrarByte().length);
            iconFamily.setImageBitmap(bmp);
        }
        convertView.setTag(String.valueOf(position));

        iconFamily.setOnClickListener(onClickListener(position));
        deleteGroup.setOnClickListener(onClickListener(position));
        addmember.setOnClickListener(onClickListener(position));
        detailGroup.setOnClickListener(onClickListener(position));
        convertView.setLayoutParams(new FancyCoverFlow.LayoutParams(600,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return convertView;
    }


    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FamilyManagerment familyManagerment = (FamilyManagerment)activity;
                switch (v.getId()){
                    case R.id.img_center_child:
                        familyManagerment.changeIcon(position);
                        break;
                    case R.id.txt_left_side:
                        familyManagerment.onItemLongClick(position);
                        break;
                    case R.id.txt_center_side:
                        //familyManagerment.addNewMember(position);
                        familyManagerment.renameGroup();
                        break;
                    case R.id.txt_right_side:
                        familyManagerment.showDetail(position);
                        break;
                }

            }
        };
    }
}
