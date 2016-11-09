package com.android.keepfocus.settings;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.FamilyManagerment;
import com.android.keepfocus.data.ParentGroupItem;

import java.util.ArrayList;

public class CoverFlowAdapter2 extends BaseAdapter {

    private ArrayList<ParentGroupItem> data;
    private Activity activity;

    public CoverFlowAdapter2(Activity activity, ArrayList<ParentGroupItem> objects) {
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
    public ParentGroupItem getItem(int position) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        final int mPosition = position;
        final ParentGroupItem profileItem = getItem(mPosition);


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_radial_buttons, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.delete.setText("Delete");
        viewHolder.addmember.setText("Add member");
        viewHolder.detail.setText("Details");
        viewHolder.name.setText(profileItem.getGroup_name());

        //Bitmap avatar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.blocked);
        //viewHolder.gameImage.setImageBitmap(getCircleBitmap(avatar));
        //viewHolder.iconFamily.setImageResource(R.drawable.blocked);


        convertView.setTag(String.valueOf(position));

        viewHolder.iconFamily.setOnClickListener(onClickListener(position));
        viewHolder.delete.setOnClickListener(onClickListener(position));
        viewHolder.addmember.setOnClickListener(onClickListener(position));
        viewHolder.detail.setOnClickListener(onClickListener(position));
        /*viewHolder.btnDelete.setOnClickListener(onClickListener(position));
        viewHolder.btnAdd.setOnClickListener(onClickListener(position));
        viewHolder.btnDetail.setOnClickListener(onClickListener(position));*/

        FamilyManagerment familyManagerment = new FamilyManagerment();
        familyManagerment.onMainButtonClicked(convertView);
        return convertView;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FamilyManagerment familyManagerment = (FamilyManagerment)activity;
                switch (v.getId()){
                    case R.id.img_center:
                        familyManagerment.showListMember(position);
                        break;
                    case R.id.txt_green:
                        familyManagerment.onItemLongClick(position);
                        break;
                    case R.id.txt_yellow:
                        familyManagerment.addNewMember(position);
                        break;
                    case R.id.txt_orange:
                        familyManagerment.showDetail(position);
                        break;

                    /*case R.id.btn_yellow:
                        familyManagerment.addNewMember(position);
                        break;
                    case R.id.btn_orange:
                        familyManagerment.showDetail(position);
                        break;
                    case R.id.btn_green:
                        familyManagerment.onItemLongClick(position);
                        break;*/
                }

            }
        };
    }


    private static class ViewHolder {
        private TextView delete;
        private TextView detail;
        private TextView addmember;
        private TextView name;
        private ImageView iconFamily;
        private LinearLayout btnDelete;
        private LinearLayout btnAdd;
        private LinearLayout btnDetail;

        public ViewHolder(View v) {
            iconFamily = (ImageView) v.findViewById(R.id.img_center);
            delete = (TextView) v.findViewById(R.id.txt_green);
            addmember = (TextView) v.findViewById(R.id.txt_yellow);
            detail = (TextView) v.findViewById(R.id.txt_orange);
            name = (TextView) v.findViewById(R.id.family_name);
            btnDelete = (LinearLayout) v.findViewById(R.id.btn_green);
            btnAdd = (LinearLayout) v.findViewById(R.id.btn_yellow);
            btnDetail = (LinearLayout) v.findViewById(R.id.btn_orange);
        }
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


}