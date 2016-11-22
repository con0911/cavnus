package com.android.keepfocus.settings;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.DeviceMemberManagerment;
import com.android.keepfocus.activity.FamilyManagerment;
import com.android.keepfocus.data.ParentMemberItem;

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
        iconFamily = (ImageView) convertView.findViewById(R.id.img_center);
        blockall = (TextView) convertView.findViewById(R.id.txt_green);
        addmember = (TextView) convertView.findViewById(R.id.txt_yellow);
        allowAll = (TextView) convertView.findViewById(R.id.txt_orange);
        name = (TextView) convertView.findViewById(R.id.family_name);
        blockall.setText("Block All");
        addmember.setText("Scheduler");
        allowAll.setText("Allow All");


        View parentBlockall = (View) blockall.getParent();
        View parentAllowAll = (View) allowAll.getParent();
        Log.e("thong.nv", "profileItem");
        Log.e("thong.nv", "profileItem.name" + profileItem.getName_member());
        Log.e("thong.nv", "profileItem is Block " + profileItem.getIs_blockall());
        Log.e("thong.nv", "profileItem is alow " + profileItem.getIs_alowall());
        if (profileItem.getIs_blockall() == 1) {
            parentBlockall.setPressed(true);
            blockall.setText("Un Block");
            blockall.setTextColor(Color.parseColor("#2962ff"));
        } else {
            parentBlockall.setPressed(false);
            blockall.setText("Block All");
            blockall.setTextColor(Color.parseColor("#ffffff"));
        }

        if (profileItem.getIs_alowall() == 1) {
            allowAll.setText("Un Allow");
            allowAll.setTextColor(Color.parseColor("#2962ff"));
            parentAllowAll.setPressed(true);
        } else {
            parentAllowAll.setPressed(false);
            allowAll.setTextColor(Color.parseColor("#ffffff"));
            allowAll.setText("Allow All");
        }

        Bitmap icon = BitmapFactory.decodeResource(activity.getResources(),R.drawable.images);
        iconFamily.setImageBitmap(icon);
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

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DeviceMemberManagerment deviceMemberManagerment = (DeviceMemberManagerment) activity;
                switch (v.getId()){
                    case R.id.img_center:
                        deviceMemberManagerment.onItemClick(position);
                        break;
                    case R.id.txt_yellow:
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