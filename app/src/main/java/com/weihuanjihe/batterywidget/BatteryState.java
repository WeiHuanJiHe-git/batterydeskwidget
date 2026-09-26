package com.weihuanjihe.batterywidget;

import android.content.Intent;
import android.os.BatteryManager;

final class BatteryState {
    final int percent;
    final boolean charging;

    BatteryState(int percent, boolean charging) {
        this.percent = Math.max(0, Math.min(100, percent));
        this.charging = charging;
    }

    static BatteryState fromIntent(Intent intent) {
        if (intent == null) return new BatteryState(0, false);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int percent = scale > 0 ? Math.round(level * 100f / scale) : 0;
        boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
        return new BatteryState(percent, charging);
    }
}
