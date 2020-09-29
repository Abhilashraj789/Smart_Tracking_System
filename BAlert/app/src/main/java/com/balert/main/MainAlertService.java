/**
 * 
 */
package com.balert.main;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class MainAlertService extends Service {

	private LocationAccessObject locationAccessObject;
	private AccelerometerAccessObject accelerometerAccessObject;
	private BatteryStatus batteryStatus;
	private DatabaseHelper databaseHelper;

	private ArrayList<String> sentList;

	public static boolean isActive;

	private SharedPreference sharedPreference;

	private boolean isReached;

	private static final int SCHOOL_REGION = 100;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		locationAccessObject = new LocationAccessObject(this);

		accelerometerAccessObject = new AccelerometerAccessObject(this);

		batteryStatus = new BatteryStatus();

		databaseHelper = new DatabaseHelper(this);

		sentList = new ArrayList<String>();

		sharedPreference = new SharedPreference(this, false);

		isActive = true;

		isReached = false;

		databaseHelper.updatePresence();

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		locationAccessObject.registerLocationListener();

		registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		new CheckLocationReached().start();
		// new CheckSchoolReached().start();

		return super.onStartCommand(intent, flags, startId);
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			int level = intent.getIntExtra("level", 0);

			if (level <= 20) {

				for (int i = 0; i < 3; i++) {
					Toast.makeText(MainAlertService.this,
							"Your battery is low! Kindly charge! ",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(MainAlertService.this,
						"Current battery level is: " + level, Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	private class CheckLocationReached extends Thread {

		@Override
		public void run() {

			while (isActive) {

				Cursor cursor = databaseHelper.getStudentDetails(-1);

				if (cursor != null && cursor.moveToNext()) {

					do {
						String stud_latitude = cursor.getString(6).trim();
						String stud_longitude = cursor.getString(7).trim();

						int student_distance = cursor.getInt(9);
						String mobileNo = cursor.getString(8).trim();

						int presence = cursor.getInt(11);

						double currentLatitude = LocationAccessObject
								.getLatitude();
						double currentLongitude = LocationAccessObject
								.getLongitude();

						if (presence == 1 && !sentList.contains(mobileNo)) {
							int dist_kms = (int) HaversineDistance.getDistance(
									currentLatitude, currentLongitude,
									Double.parseDouble(stud_latitude),
									Double.parseDouble(stud_longitude));

							int dist_mts = dist_kms * 1000;

							if (dist_mts <= student_distance) {
								new SendLocationSMS(mobileNo, currentLatitude
										+ "," + currentLongitude).start();
								sentList.add(mobileNo);
							}
						} else {
							Log.d(Util.TAG,
									"Student is absent or already sent!"
											+ presence);
						}

					} while (cursor.moveToNext());

					cursor.close();
				}

				try {
					Thread.sleep(1000 * 30);
				} catch (InterruptedException e) {
				}
			}

			super.run();
		}
	}

	private class CheckSchoolReached extends Thread {

		@Override
		public void run() {

			while (isReached) {

				double school_latitude = 0.0;
				double school_longitude = 0.0;
				try {
					school_latitude = Double.parseDouble(sharedPreference
							.getLatitude());
					school_longitude = Double.parseDouble(sharedPreference
							.getLongitude());
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				int dist_kms = (int) HaversineDistance.getDistance(
						LocationAccessObject.getLatitude(),
						LocationAccessObject.getLongitude(), school_latitude,
						school_longitude);

				int dist_mts = dist_kms * 1000;

				if (dist_mts < SCHOOL_REGION) {

					ArrayList<String> numbers = new ArrayList<String>();

					Cursor cursor = databaseHelper.getStudentDetails(-1);

					if (cursor != null && cursor.moveToNext()) {
						do {
							if (cursor.getInt(11) == 1) {
								numbers.add(cursor.getString(8));
							}
						} while (cursor.moveToNext());
					}
					cursor.close();

					SmsManager smsManager = SmsManager.getDefault();

					for (int i = 0; i < numbers.size(); i++) {

						smsManager.sendTextMessage(numbers.get(i), null,
								"Bus reached the school safely! ", null, null);

						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					isReached = true;
					isActive = false;
					stopSelf();

				} else {
					Log.d(Util.TAG, "School not yet reached! " + dist_mts);
				}

				try {
					Thread.sleep(1000 * 30);
				} catch (InterruptedException e) {
				}

			}

			super.run();
		}
	}

	@Override
	public void onDestroy() {
		try {
			unregisterReceiver(mBatInfoReceiver);
			sentList.clear();
			databaseHelper.close();
			isActive = false;
			isReached = false;
			accelerometerAccessObject.unregisterSensorEvents();
			locationAccessObject.unregisterLocationUpdates();
			unregisterReceiver(batteryStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
}