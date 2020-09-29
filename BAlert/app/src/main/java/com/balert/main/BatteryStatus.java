/**
 * 
 */
package com.balert.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryStatus extends BroadcastReceiver {

	private static final int LOW_LEVEL = 20;

	private static int currentBatteryStatus;

	@Override
	public void onReceive(Context context, Intent intent) {

		int current_level = intent.getIntExtra("level", 0);

		// IntentFilter ifilter = new
		// IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		// Intent batteryStatus = context.registerReceiver(null, ifilter);
		// int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
		// -1);
		// boolean bCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
		// || status == BatteryManager.BATTERY_STATUS_FULL;

		if (current_level <= LOW_LEVEL /* && !bCharging */) {
			Toast.makeText(
					context,
					"Your battery is low! Kindly charge! "
							, Toast.LENGTH_LONG).show();
			// showNotification(context);
		}
	}

	private void showNotification(Context context) {
		/*
		 * Notification noti = new Notification.Builder(context)
		 * .setContentTitle("Battery is low")
		 * .setContentText("Kindly charge ur phone!")
		 * .setSmallIcon(android.R.drawable.stat_notify_error).build();
		 * NotificationManager notificationManager = (NotificationManager)
		 * context .getSystemService(Context.NOTIFICATION_SERVICE); noti.flags
		 * |= Notification.FLAG_AUTO_CANCEL; notificationManager.notify(0,
		 * noti);
		 */

		@SuppressWarnings("deprecation")
		Notification notification = new Notification(
				android.R.drawable.stat_sys_warning, "Battery is low!",
				System.currentTimeMillis());
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}

	public static int getCurrentBatteryStatus() {
		return currentBatteryStatus;
	}

	public static void setCurrentBatteryStatus(int currentBatteryStatus) {
		BatteryStatus.currentBatteryStatus = currentBatteryStatus;
	}
}