package com.example.oxidepanel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int OVERLAY_REQ = 501;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(36, 48, 36, 36);
        root.setBackgroundColor(0xFF101722);

        TextView title = new TextView(this);
        title.setText("OXIDE PRIVATE PANEL");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(23);
        title.setPadding(0, 0, 0, 22);
        root.addView(title);

        TextView info = new TextView(this);
        info.setText("Экранная панель настроек. Не читает память игры и не управляет прицелом.");
        info.setTextColor(0xFFB8C5D6);
        info.setTextSize(14);
        root.addView(info);

        Button prepare = new Button(this);
        prepare.setText("PREPARE OVERLAY");
        root.addView(prepare);
        prepare.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(i, OVERLAY_REQ);
            } else startOverlay();
        });

        Button stop = new Button(this);
        stop.setText("STOP OVERLAY");
        root.addView(stop);
        stop.setOnClickListener(v -> stopService(new Intent(this, OverlayService.class)));
        setContentView(root);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_REQ && (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)))
            startOverlay();
    }

    private void startOverlay() {
        Intent i = new Intent(this, OverlayService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i);
        else startService(i);
        finish();
    }
}
