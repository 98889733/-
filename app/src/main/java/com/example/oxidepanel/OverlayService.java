package com.example.oxidepanel;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager wm;
    private View panel;
    private WindowManager.LayoutParams params;

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        buildPanel();
    }

    private GradientDrawable bg(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(22);
        d.setStroke(2, stroke); return d;
    }

    private TextView label(String s, int size) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(size);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(10, 10, 10, 10); return t;
    }

    private Button toggle(String text) {
        Button b = new Button(this); b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF26364A));
        return b;
    }

    private void buildPanel() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 12, 16, 14);
        card.setBackground(bg(0xEE101722, 0xFF39A7FF));

        TextView title = label("OXIDE  •  PANEL", 17);
        card.addView(title);

        LinearLayout tabs = new LinearLayout(this);
        Button fight = toggle("FIGHT");
        Button box = toggle("BOX");
        tabs.addView(fight, new LinearLayout.LayoutParams(0, -2, 1));
        tabs.addView(box, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(tabs);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        card.addView(content);

        Runnable showFight = () -> {
            content.removeAllViews();
            content.addView(label("FIGHT SETTINGS", 14));
            content.addView(toggle("Aim circle: visual indicator only"));
            TextView size = label("Circle size: 150", 13);
            content.addView(size);
            SeekBar seek = new SeekBar(this); seek.setMax(450); seek.setProgress(100);
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar s, int p, boolean u) { size.setText("Circle size: " + (p + 50)); }
                public void onStartTrackingTouch(SeekBar s) {}
                public void onStopTrackingTouch(SeekBar s) {}
            });
            content.addView(seek);
            content.addView(toggle("Target preference: Head / Body (display setting)"));
            content.addView(toggle("Prediction: display toggle only"));
            content.addView(label("No automatic aiming is performed.", 11));
        };
        Runnable showBox = () -> {
            content.removeAllViews();
            content.addView(label("BOX SETTINGS", 14));
            content.addView(toggle("Nicknames (demo toggle)"));
            content.addView(toggle("Skeleton (demo toggle)"));
            content.addView(toggle("Head marker (demo toggle)"));
            content.addView(toggle("Health / status (demo toggle)"));
            content.addView(toggle("Boxes (demo toggle)"));
            content.addView(toggle("Visible color: GREEN"));
            content.addView(toggle("Occluded color: RED"));
            content.addView(label("These are UI controls only; no player data is read.", 11));
        };
        fight.setOnClickListener(v -> showFight.run());
        box.setOnClickListener(v -> showBox.run());
        showFight.run();

        Button hide = toggle("HIDE PANEL");
        hide.setOnClickListener(v -> stopSelf());
        card.addView(hide);

        int type = Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        params = new WindowManager.LayoutParams(-2, -2, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 24; params.y = 100;

        title.setOnTouchListener(new View.OnTouchListener() {
            int sx, sy; float tx, ty;
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = params.x; sy = params.y; tx = e.getRawX(); ty = e.getRawY(); return true;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    params.x = sx + (int)(e.getRawX() - tx);
                    params.y = sy + (int)(e.getRawY() - ty);
                    wm.updateViewLayout(panel, params); return true;
                }
                return true;
            }
        });
        panel = card;
        wm.addView(panel, params);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_NOT_STICKY; }
    @Override public void onDestroy() {
        if (panel != null && wm != null) wm.removeView(panel);
        panel = null; super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
