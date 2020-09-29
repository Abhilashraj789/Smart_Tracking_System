package com.balert.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BootStartup extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (sp.getBoolean("Alert", false) == true) {
			//context.startService(new Intent(context, MainService.class));
			Toast.makeText(context, "MainServices Start", Toast.LENGTH_SHORT)
					.show();
		}
	}
}