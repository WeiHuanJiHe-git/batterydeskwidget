package com.weihuanjihe.batterywidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BatteryWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_REFRESH = "com.weihuanjihe.batterywidget.ACTION_REFRESH";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        BatteryWidgetUpdater.updateAll(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BatteryWidgetUpdater.updateAll(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            BatteryWidgetUpdater.updateAll(context);
        }
    }

    public static PendingIntent refreshPendingIntent(Context context) {
        Intent intent = new Intent(context, BatteryWidgetProvider.class);
        intent.setAction(ACTION_REFRESH);
        return PendingIntent.getBroadcast(context, 1001, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static void startMonitorService(Context context) {
        Intent intent = new Intent(context, BatteryMonitorService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception ignored) {
            // Manual widget refresh still works if an OEM blocks background service start.
        }
    }
}
