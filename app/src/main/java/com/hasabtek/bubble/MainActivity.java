package com.hasabtek.bubble;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        float d = getResources().getDisplayMetrics().density;
        int p = (int) (24 * d);
        l.setPadding(p, p, p, p);
        l.setGravity(Gravity.CENTER);
        TextView t = new TextView(this);
        t.setText("حاسبتك الجمركية\n\nدوس الزر تحت واسمح بـ «العرض فوق التطبيقات»، وزر حاسبتك العائم هيظهر على الشاشة فوق أي تطبيق.\n\nأي ضغطة على الزر بتفتح حاسبتك على طول.");
        t.setTextSize(16);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, 0, 0, p);
        Button b = new Button(this);
        b.setText("تشغيل الزر العائم");
        b.setOnClickListener(v -> startBubble());
        l.addView(t);
        l.addView(b);
        setContentView(l);
    }

    private void startBubble() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
        } else {
            startBubbleService();
            finish();
        }
    }

    private void startBubbleService() {
        Intent i = new Intent(this, BubbleService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
    }

    @Override protected void onResume() {
        super.onResume();
        // coming back from the overlay-permission screen: start if granted
        if (Settings.canDrawOverlays(this)) {
            startBubbleService();
            finish();
        }
    }
}
