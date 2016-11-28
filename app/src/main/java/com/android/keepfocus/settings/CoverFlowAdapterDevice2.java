package com.android.keepfocus.settings;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.DeviceMemberManagerment;
import com.android.keepfocus.activity.FamilyManagerment;
import com.android.keepfocus.data.ParentMemberItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CoverFlowAdapterDevice2 extends BaseAdapter {

    private ArrayList<ParentMemberItem> data;
    private Activity activity;
    private TextView blockall;
    private TextView allowAll;
    private TextView addmember;
    private TextView name;
    private ImageView iconFamily;

    public CoverFlowAdapterDevice2(Activity activity, ArrayList<ParentMemberItem> objects) {
        this.activity = activity;
        this.data = objects;
    }

    @Override
    public int getCount() {
        if(data.size() > 0) {
            return data.size();
        }
        return 1;
    }

    @Override
    public ParentMemberItem getItem(int position) {
        if(data.size() > 0) {
            return data.get(position);
        }
        return null;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ParentMemberItem profileItem = getItem(position);
        final DeviceMemberManagerment deviceMemberManagerment = (DeviceMemberManagerment) activity;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.device_adapter_layout, null, false);
        iconFamily = (ImageView) convertView.findViewById(R.id.img_center_child);
        blockall = (TextView) convertView.findViewById(R.id.txt_left_side);
        addmember = (TextView) convertView.findViewById(R.id.txt_center_side);
        allowAll = (TextView) convertView.findViewById(R.id.txt_right_side);
        name = (TextView) convertView.findViewById(R.id.family_name);
        blockall.setText("Block All");
        addmember.setText("Schedules");
        allowAll.setText("Allow All");


        View parentBlockall = (View) blockall.getParent();
        View parentAllowAll = (View) allowAll.getParent();
        Log.e("thong.nv", "profileItem");
        Log.e("thong.nv", "profileItem.name" + profileItem.getName_member());
        Log.e("thong.nv", "profileItem is Block " + profileItem.getIs_blockall());
        Log.e("thong.nv", "profileItem is alow " + profileItem.getIs_alowall());
        if (profileItem.getIs_blockall() == 1) {
            parentBlockall.setPressed(true);
            blockall.setText("Return to\nSchedule");
            //blockall.setTextColor(Color.parseColor("#2962ff"));
        } else {
            parentBlockall.setPressed(false);
            blockall.setText("Block All");
            blockall.setTextColor(Color.parseColor("#ffffff"));
        }

        if (profileItem.getIs_alowall() == 1) {
            allowAll.setText("Return to\nSchedule");
            //allowAll.setTextColor(Color.parseColor("#2962ff"));
            parentAllowAll.setPressed(true);
        } else {
            parentAllowAll.setPressed(false);
            allowAll.setTextColor(Color.parseColor("#ffffff"));
            allowAll.setText("Allow All");
        }

        Uri selectedImage = Uri.parse(profileItem.getImage_member().toString());
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
            } catch (IOException e) {
                e.printStackTrace();
                Bitmap icon = BitmapFactory.decodeResource(activity.getResources(),R.drawable.person);
                iconFamily.setImageBitmap(icon);
            }
        } else {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(),R.drawable.person);
            iconFamily.setImageBitmap(icon);
        }

        convertView.setTag(String.valueOf(position));
        name.setText(profileItem.getName_member());

        iconFamily.setOnClickListener(onClickListener(position));
        addmember.setOnClickListener(onClickListener(position));

        FamilyManagerment familyManagerment = new FamilyManagerment();
        familyManagerment.onMainButtonClicked(convertView);
        blockall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileItem.getIs_blockall() == 0) {
                    deviceMemberManagerment.blockAll(position);
                } else {
                    deviceMemberManagerment.unBlockAll(position);
                }
            }
        });
        addmember.setOnClickListener(onClickListener(position));
        allowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileItem.getIs_alowall() == 0) {
                    deviceMemberManagerment.allowAll(position);
                } else {
                    deviceMemberManagerment.unAllowAll(position);
                }
            }
        });
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
                DeviceMemberManagerment deviceMemberManagerment = (DeviceMemberManagerment) activity;
                switch (v.getId()){
                    case R.id.img_center_child:
                        deviceMemberManagerment.changeIcon(position);
                        break;
                    case R.id.txt_center_side:
                        View parent2 = (View) v.getParent();
                        parent2.setPressed(true);
                        deviceMemberManagerment.showDetail(position);
                        setDelayPress(false, parent2);
                        break;
                }

            }
        };
    }

    public static Bitmap getCircleBitmap(Bitmap bitmapimg) {

        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.GREEN;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setStrokeWidth(10);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth()  / 3, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }

    public void setDelayPress(boolean press, View v){
        final Handler handler = new Handler();
        final View view = v;
        final boolean pressed = press;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setPressed(pressed);
            }
        }, 150);
    }


}