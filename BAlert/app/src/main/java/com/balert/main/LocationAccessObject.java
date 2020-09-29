/**
 * Class which gives Location details
 */
package com.balert.main;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;


public class LocationAccessObject implements LocationListener {

	private static double latitude;
	private static double longitude;

	private AlertDialog.Builder alBuilder;

	private LocationManager locationManager;

	private static Context context;

	private static final int MAX_SPEED = 30;

	private static boolean isAlerted = false;
	private DatabaseHelper databaseHelper;

	public LocationAccessObject(Context cntxt) {

		context = cntxt;

		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		alBuilder = new AlertDialog.Builder(context);

		databaseHelper = new DatabaseHelper(context);
	}

	public void registerLocationListener() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}

	@Override
	public void onLocationChanged(Location location) {

		try {
			if (location.getLatitude() == 0.0) {
				Log.e(Util.TAG, "Location 0");
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			Log.e(Util.TAG, "Location " + location.getLatitude() + ","
					+ location.getLongitude());

			float speed_mts_per_sec = location.getSpeed();

			float currentSpeed_kms = (float) (speed_mts_per_sec * 3.6);

			if (currentSpeed_kms >= MAX_SPEED && !isAlerted) {
				isAlerted = true;

				Cursor cursor = databaseHelper.getContacts(-1);

				ArrayList<String> mobileList = new ArrayList<String>();

				if (cursor != null && cursor.moveToNext()) {

					do {
						mobileList.add(cursor.getString(2));
					} while (cursor.moveToNext());
				}
				cursor.close();
				new SendAlert(mobileList, currentSpeed_kms).start();
			}
		} catch (Exception e) {
		}

		setLatitude(location.getLatitude());
		setLongitude(location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		try {
			alBuilder.setTitle("Enable GPS");
			alBuilder.setMessage("GPS is not enabled. Want to enable now?");

			alBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							context.startActivity(new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					});

			alBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			alBuilder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/* Method to unregister to the location updates */
	public void unregisterLocationUpdates() {
		locationManager.removeUpdates(this);
	}

	private class SendAlert extends Thread {

		private ArrayList<String> mobileList;
		private float currentSpeed;

		public SendAlert(ArrayList<String> mobileList, float speed) {
			this.mobileList = mobileList;
			this.currentSpeed = speed;
		}

		@Override
		public void run() {

			try {

				String message = "We have detected over speed in the vehicle. Current speed is: "
						+ currentSpeed + " km/h";

				SmsManager smsManager = SmsManager.getDefault();

				for (int i = 0; i < mobileList.size(); i++) {
					smsManager.sendTextMessage(mobileList.get(i), null,
							message, null, null);

					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			super.run();
		}
	}

	public static double getLatitude() {
		return latitude;
	}

	public static double getLongitude() {
		return longitude;
	}

	public static void setLatitude(double lat) {
		latitude = lat;
	}

	public static void setLongitude(double longit) {
		longitude = longit;
	}
}