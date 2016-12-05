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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.DeviceMemberManagerment;
import com.android.keepfocus.activity.FamilyManagerment;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.fancycoverflow.FancyCoverFlow;
import com.android.keepfocus.fancycoverflow.FancyCoverFlowAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by sev_user on 11/26/2016.
 */
public class CircleGroupAdapter extends FancyCoverFlowAdapter {

    private ArrayList<ParentGroupItem> data;
    private Activity activity;

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
        deleteGroup.setText("Delete");
        detailGroup.setText("Details");
        addmember.setText("Add device");
        name.setText(profileItem.getGroup_name());
        blockallLayout = (LinearLayout) convertView.findViewById(R.id.btn_left_side);
        allowallLayout = (LinearLayout) convertView.findViewById(R.id.btn_right_side);
        addMemberLayout = (LinearLayout) convertView.findViewById(R.id.btn_center_side);
        blockallLayout.setVisibility(View.VISIBLE);
        allowallLayout.setVisibility(View.VISIBLE);
        addMemberLayout.setVisibility(View.VISIBLE);

        Uri selectedImage = Uri.parse(profileItem.getIcon_uri().toString());
        //viewHolder.iconFamily.setImageURI(selectedImage);
        InputStream is = null;
        try {
            is = activity.getContentResolver().openInputStream(selectedImage);
        }catch (Exception e){
            Log.d("TAG", "Exception " + e);
        }
        if (is!=null) {
            Bitmap bitmap = null;
            Bitmap resizedBm = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);

                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = activity.getContentResolver().query(selectedImage, orientationColumn, null, null, null);
                int orientation = -1;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }
                Bitmap bmRotate = null;
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bmRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                //bitmap.recycle();
                bitmap = null;
                resizedBm = getResizedBitmap(bmRotate, 225, 225);
                bmRotate = null;
                iconFamily.setImageBitmap(resizedBm);
                resizedBm = null;
            }catch (IOException e) {
                e.printStackTrace();
                Bitmap icon = BitmapFactory.decodeResource(activity.getResources(),R.drawable.images);
                iconFamily.setImageBitmap(icon);
            }
        } else {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(),R.drawable.images);
            iconFamily.setImageBitmap(icon);
        }
        convertView.setTag(String.valueOf(position));

        iconFamily.setOnClickListener(onClickListener(position));
        deleteGroup.setOnClickListener(onClickListener(position));
        addmember.setOnClickListener(onClickListener(position));
        detailGroup.setOnClickListener(onClickListener(position));
        convertView.setLayoutParams(new FancyCoverFlow.LayoutParams(600,
                FrameLayout.LayoutParams.MATCH_PARENT));
        return convertView;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
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
                        familyManagerment.addNewMember(position);
                        break;
                    case R.id.txt_right_side:
                        familyManagerment.showDetail(position);
                        break;
                }

            }
        };
    }
}