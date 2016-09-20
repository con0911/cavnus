package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.keepfocus.R;
import com.android.keepfocus.gcm.MainGcmActivity;
import com.android.keepfocus.service.KeepFocusMainService;
import com.android.keepfocus.service.ServiceBlockApp;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        Button parentBtn = (Button) findViewById(R.id.parentBtn);
        Button childBtn = (Button) findViewById(R.id.childBtn);
        Button start = (Button) findViewById(R.id.startSv);
        Button stop = (Button) findViewById(R.id.stopSv);
        Button gcm = (Button) findViewById(R.id.gcmTest);

        parentBtn.setOnClickListener(this);
        childBtn.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        gcm.setOnClickListener(this);

    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.parentBtn:
                Intent parentIntent = new Intent(this,GroupManagermentActivity.class);
                startActivity(parentIntent);
                break;
            case R.id.childBtn:
                Intent childIntent = new Intent(this,ChildSchedulerActivity.class);
                startActivity(childIntent);
                break;
            case R.id.gcmTest:
                Intent gcmIntent = new Intent(this,MainGcmActivity.class);
                startActivity(gcmIntent);
                break;
            case R.id.startSv:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                    startService(new Intent(this, KeepFocusMainService.class));
                }
                startService(new Intent(this, ServiceBlockApp.class));
                break;
            case R.id.stopSv:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                    stopService(new Intent(this, KeepFocusMainService.class));
                }
                stopService(new Intent(this, ServiceBlockApp.class));
                break;
        }
    }

}
