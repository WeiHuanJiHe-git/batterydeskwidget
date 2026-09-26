package com.weihuanjihe.batterywidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BatteryMonitorService extends Service {
    private static final String CHANNEL_ID = "battery_realtime";
    private static final int NOTIFICATION_ID = 100;

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) return;
            BatteryState state = BatteryState.fromIntent(intent);
            BatteryWidgetUpdater.updateAll(context, state.percent, state.charging);
            updateForegroundNotification(state);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        BatteryState state = BatteryWidgetUpdater.readBatteryState(this);
        startForeground(NOTIFICATION_ID, createNotification(state));
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BatteryState state = BatteryWidgetUpdater.readBatteryState(this);
        BatteryWidgetUpdater.updateAll(this, state.percent, state.charging);
        updateForegroundNotification(state);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try { unregisterReceiver(batteryReceiver); } catch (Exception ignored) {}
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "桌面电量实时同步", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("用于桌面电量小组件实时刷新");
        channel.setShowBadge(false);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) manager.createNotificationChannel(channel);
    }

    private Notification createNotification(BatteryState state) {
        String content = "当前电量 " + state.percent + "%" + (state.charging ? " · 充电中" : "");
        return new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                .setContentTitle("桌面电量实时同步")
                .setContentText(content)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    private void updateForegroundNotification(BatteryState state) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(NOTIFICATION_ID, createNotification(state));
    }
}
