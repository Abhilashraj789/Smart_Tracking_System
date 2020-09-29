/**
 * 
 */
package com.balert_parent_app.main;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MessageReciever extends BroadcastReceiver {

	private String sender;
	private String messageText;
	private SharedPreference sharedPreference;

	AlertDialog.Builder builder;

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();

		sharedPreference = new SharedPreference(context);

		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		builder = new AlertDialog.Builder(context);

		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] msgs = new SmsMessage[pdus.length];

			for (int i = 0; i < msgs.length; i++) {

				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				if (i == 0) {
					sender = msgs[i].getOriginatingAddress();

					if (sharedPreference.getNumber().equals(sender)) {

						audioManager
								.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

						messageText = msgs[i].getMessageBody().toString()
								.trim();

						/*
						 * builder.setTitle("Info");
						 * builder.setMessage(messageText);
						 * 
						 * builder.setPositiveButton("OK", new
						 * DialogInterface.OnClickListener() {
						 * 
						 * @Override public void onClick(DialogInterface dialog,
						 * int which) { dialog.dismiss(); } }); builder.show();
						 */

						for (int j = 0; j < 3; j++) {
							Toast.makeText(context, messageText,
									Toast.LENGTH_LONG).show();
						}

						// notificationIntent.putExtra("messageText",
						// messageText);
						/*
						 * PendingIntent pendingIntent = PendingIntent
						 * .getActivity(context, 0, notificationIntent, 0);
						 * Notification notification = createNotification();
						 * notification.setLatestEventInfo(context,
						 * "Notification from bAlert",
						 * "You have received  a notification! ",
						 * pendingIntent);
						 * 
						 * notificationManager.notify( (int)
						 * SystemClock.uptimeMillis(), notification);
						 */
					} else {
						Log.d("bAlert", "Not from bAlert: " + sender);
					}
				}
			}
		}
	}

	private Notification createNotification() {
		Notification notification = new Notification();
		notification.icon = R.drawable.balert;
		notification.when = System.currentTimeMillis();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;
		return notification;
	}
}