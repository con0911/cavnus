package com.android.keepfocus.controller;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.GroupManagermentActivity;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.utils.CircleLayout;

import java.util.ArrayList;

/**
 * Created by sev_user on 10/10/2016.
 */
public class FamilyViewAdapter extends ArrayAdapter<ParentGroupItem>  implements CircleLayout.OnRotationFinishedListener,
        CircleLayout.OnCenterClickListener {

    protected CircleLayout circleLayout;
    protected TextView selectedTextView;
    protected ImageView avatarView;
    protected ImageView outCircle;
    private Button addMember;
    private Button detailFamily;
    private Button deleteFamily;
    private Button listMember;

    private Activity activity;
    private static LayoutInflater inflater = null;

    public FamilyViewAdapter(Activity activity, int resource,
                             int textViewResourceId, ArrayList<ParentGroupItem> objects) {
        super(activity, resource, textViewResourceId, objects);
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }
    MainDatabaseHelper kFDHelper = new MainDatabaseHelper(getContext());


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.layout_family, parent, false);



        TextView nameProfile = (TextView) convertView.findViewById(R.id.title_family);
        final int mPosition = position;
        final ParentGroupItem profileItem = getItem(mPosition);

        nameProfile.setText(profileItem.getGroup_name());
        convertView.setOnClickListener(new OnItemClickListener( position ));
        convertView.setOnLongClickListener(new OnItemLongClickListener(position) );

        circleLayout = (CircleLayout) convertView.findViewById(R.id.circle_layout_family);
        avatarView = (ImageView) convertView.findViewById(R.id.in_circle);
        outCircle = (ImageView) convertView.findViewById(R.id.out_circle);
        circleLayout.setOnRotationFinishedListener(this);
        circleLayout.setOnCenterClickListener(this);

        addMember = (Button) convertView.findViewById(R.id.add_member);
        detailFamily = (Button) convertView.findViewById(R.id.detail_family);
        deleteFamily = (Button) convertView.findViewById(R.id.delete_family);
        listMember = (Button) convertView.findViewById(R.id.list_member);


        addMember.setOnClickListener(new OnItemClickListener( position ));
        detailFamily.setOnClickListener(new OnItemClickListener( position ));
        deleteFamily.setOnClickListener(new OnItemClickListener( position ));
        listMember.setOnClickListener(new OnItemClickListener( position ));
        avatarView.setOnClickListener(new OnItemClickListener( position ));

        Bitmap avatar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.blocked);
        avatarView.setImageBitmap(getCircleBitmap(avatar));

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
            switch (v.getId()) {
                case R.id.in_circle:
                    Toast.makeText(activity.getApplicationContext(), "Change avatar", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.add_member:
                    groupManagermentActivity.addNewMember(mPosition);
                    break;
                case R.id.detail_family:
                    groupManagermentActivity.showDetail(mPosition);
                    break;
                case R.id.delete_family:
                    groupManagermentActivity.onItemLongClick(mPosition);
                    break;

                case R.id.list_member:
                    groupManagermentActivity.showListMember(mPosition);
                    break;
            }
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


    @Override
    public void onCenterClick() {
        Toast.makeText(activity.getApplicationContext(), "Change avatar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRotationFinished(View view) {
        Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2, view.getHeight() / 2);
        animation.setDuration(250);
        view.startAnimation(animation);
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
