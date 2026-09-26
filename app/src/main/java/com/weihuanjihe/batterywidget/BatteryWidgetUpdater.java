package com.weihuanjihe.batterywidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.RemoteViews;

public final class BatteryWidgetUpdater {
    private BatteryWidgetUpdater() {}

    public static BatteryState readBatteryState(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return BatteryState.fromIntent(batteryIntent);
    }

    public static void updateAll(Context context) {
        BatteryState state = readBatteryState(context);
        updateAll(context, state.percent, state.charging);
    }

    public static void updateAll(Context context, int percent, boolean charging) {
        percent = Math.max(0, Math.min(100, percent));
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, BatteryWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(componentName);
        Bitmap batteryBitmap = createBatteryBitmap(percent, charging);
        Bitmap ringBitmap = createRingBitmap(percent);

        for (int id : ids) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_battery);
            views.setTextViewText(R.id.tvPercent, percent + "%");
            views.setImageViewBitmap(R.id.ivBattery, batteryBitmap);
            views.setImageViewBitmap(R.id.ivRing, ringBitmap);
            views.setOnClickPendingIntent(R.id.widgetRoot, BatteryWidgetProvider.refreshPendingIntent(context));
            manager.updateAppWidget(id, views);
        }
    }

    private static Bitmap createRingBitmap(int percent) {
        int size = 320;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        float strokeWidth = 22f;
        float inset = 24f;
        RectF oval = new RectF(inset, inset, size - inset, size - inset);

        Paint background = new Paint(Paint.ANTI_ALIAS_FLAG);
        background.setStyle(Paint.Style.STROKE);
        background.setStrokeWidth(strokeWidth);
        background.setStrokeCap(Paint.Cap.ROUND);
        background.setColor(Color.rgb(218, 218, 218));

        Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
        foreground.setStyle(Paint.Style.STROKE);
        foreground.setStrokeWidth(strokeWidth);
        foreground.setStrokeCap(Paint.Cap.ROUND);
        foreground.setColor(Color.rgb(20, 20, 20));

        canvas.drawArc(oval, -90f, 360f, false, background);
        if (percent > 0) canvas.drawArc(oval, -90f, 360f * percent / 100f, false, foreground);
        return bitmap;
    }

    private static Bitmap createBatteryBitmap(int percent, boolean charging) {
        int width = 440, height = 260;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint outline = new Paint(Paint.ANTI_ALIAS_FLAG);
        outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(20f);
        outline.setColor(Color.rgb(20, 20, 20));

        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(Color.rgb(20, 20, 20));

        float terminalWidth = 34f, margin = 18f;
        float bodyRight = width - terminalWidth - 32f;
        RectF body = new RectF(margin, margin, bodyRight, height - margin);
        canvas.drawRoundRect(body, 48f, 48f, outline);

        RectF terminal = new RectF(bodyRight + 12f, height * 0.34f, width - 12f, height * 0.66f);
        canvas.drawRoundRect(terminal, 10f, 10f, fill);

        float innerPadding = 34f;
        float availableWidth = body.width() - innerPadding * 2f;
        float fillWidth = availableWidth * percent / 100f;
        if (fillWidth > 0) {
            RectF level = new RectF(body.left + innerPadding, body.top + innerPadding,
                    body.left + innerPadding + fillWidth, body.bottom - innerPadding);
            canvas.drawRoundRect(level, 28f, 28f, fill);
        }

        if (charging) {
            Paint bolt = new Paint(Paint.ANTI_ALIAS_FLAG);
            bolt.setStyle(Paint.Style.FILL);
            bolt.setColor(Color.WHITE);
            Path path = new Path();
            float cx = body.centerX(), cy = body.centerY();
            path.moveTo(cx + 12f, cy - 76f);
            path.lineTo(cx - 44f, cy + 4f);
            path.lineTo(cx - 4f, cy + 4f);
            path.lineTo(cx - 18f, cy + 78f);
            path.lineTo(cx + 48f, cy - 16f);
            path.lineTo(cx + 8f, cy - 16f);
            path.close();
            canvas.drawPath(path, bolt);
        }
        return bitmap;
    }
}
