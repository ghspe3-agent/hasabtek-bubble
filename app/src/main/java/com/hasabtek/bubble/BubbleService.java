package com.hasabtek.bubble;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class BubbleService extends Service {
    private WindowManager wm;
    private View bubble;

    @Override public void onCreate() {
        super.onCreate();
        Notification.Builder nb;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel("hasabtek", "حاسبتك", NotificationManager.IMPORTANCE_MIN);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
            nb = new Notification.Builder(this, "hasabtek");
        } else {
            nb = new Notification.Builder(this);
        }
        Notification n = nb.setContentTitle("زر حاسبتك شغال")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
        startForeground(1, n);
    }

    @Override public int onStartCommand(Intent i, int flags, int startId) {
        if (bubble != null) return START_STICKY;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        TextView tv = new TextView(this);
        tv.setText("حاسبتك");
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(11);
        tv.setGravity(Gravity.CENTER);
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.OVAL);
        g.setColor(Color.parseColor("#1FA855"));
        tv.setBackground(g);
        float d = getResources().getDisplayMetrics().density;
        int size = (int) (64 * d);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                size, size,
                Build.VERSION.SDK_INT >= 26
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = 0;
        lp.y = (int) (200 * d);
        bubble = tv;
        tv.setOnTouchListener(new View.OnTouchListener() {
            private int ox, oy;
            private float sx, sy;
            private boolean moved;
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ox = lp.x; oy = lp.y; sx = e.getRawX(); sy = e.getRawY(); moved = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = e.getRawX() - sx, dy = e.getRawY() - sy;
                        if (Math.abs(dx) > 10 || Math.abs(dy) > 10) moved = true;
                        lp.x = ox + (int) dx; lp.y = oy + (int) dy;
                        wm.updateViewLayout(bubble, lp);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!moved) {
                            Intent open = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://files.instinct.com/hqsdoqw43qpd"));
                            open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(open);
                        }
                        return true;
                }
                return false;
            }
        });
        wm.addView(bubble, lp);
        return START_STICKY;
    }

    @Override public void onDestroy() {
        if (bubble != null && wm != null) wm.removeView(bubble);
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent i) { return null; }
}
