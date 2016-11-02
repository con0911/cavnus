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
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.activity.GroupDetail;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.utils.CircleLayout;

import java.util.ArrayList;

/**
 * Created by sev_user on 10/10/2016.
 */
public class MemberViewAdapter extends ArrayAdapter<ParentMemberItem>  implements CircleLayout.OnRotationFinishedListener,
        CircleLayout.OnCenterClickListener {

    protected CircleLayout circleLayout;
    protected TextView selectedTextView;
    protected ImageView avatarView;
    protected ImageView outCircle;
    private Button scheduler;
    private Button deleteMember;
    private Button block;

    private Activity activity;
    private static LayoutInflater inflater = null;

    public MemberViewAdapter(Activity activity, int resource,
                                int textViewResourceId, ArrayList<ParentMemberItem> objects) {
        super(activity, resource, textViewResourceId, objects);
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }
    MainDatabaseHelper kFDHelper = new MainDatabaseHelper(getContext());


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.layout_member, parent, false);



        TextView nameProfile = (TextView) convertView.findViewById(R.id.title_member);
        final int mPosition = position;
        final ParentMemberItem profileItem = getItem(mPosition);

        nameProfile.setText(profileItem.getName_member());
        convertView.setOnClickListener(new OnItemClickListener( position ));
        circleLayout = (CircleLayout) convertView.findViewById(R.id.circle_layout_member);
        avatarView = (ImageView) convertView.findViewById(R.id.in_circle);
        outCircle = (ImageView) convertView.findViewById(R.id.out_circle);
        circleLayout.setOnRotationFinishedListener(this);
        circleLayout.setOnCenterClickListener(this);

        scheduler = (Button) convertView.findViewById(R.id.scheduler);
        deleteMember = (Button) convertView.findViewById(R.id.delete_member);
        block = (Button) convertView.findViewById(R.id.block_member);


        scheduler.setOnClickListener(new OnItemClickListener( position ));
        deleteMember.setOnClickListener(new OnItemClickListener( position ));
        block.setOnClickListener(new OnItemClickListener( position ));
        avatarView.setOnClickListener(new OnItemClickListener( position ));

        Bitmap avatar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.person);
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
            GroupDetail groupDetail = (GroupDetail)activity;
            switch (v.getId()) {
                case R.id.in_circle:
                    Toast.makeText(activity.getApplicationContext(), "Change avatar", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.scheduler:
                    groupDetail.listScheduler(mPosition);
                    break;
                case R.id.block_member:
                    groupDetail.showDetail(mPosition);
                    break;
                case R.id.delete_member:
                    groupDetail.deleteMember(mPosition);
                    break;
            }
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