/**
 * Class which reads the values from accelerometer
 */
package com.balert.main;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.telephony.SmsManager;
import android.widget.Toast;

public class AccelerometerAccessObject implements SensorEventListener {

	private static Context context;

	private Sensor accelerometerSensor;
	private SensorManager sensorManager;

	private final float NOISE = 2.0f;
	private boolean isInitialized;
	private float lastZ;

	private static final int MAX_ACCELERATION = 10;

	private static boolean isAlertSent = false;

	private DatabaseHelper databaseHelper;

	public AccelerometerAccessObject(Context contxt) {

		context = contxt;

		databaseHelper = new DatabaseHelper(contxt);

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);

		accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		isAlertSent = false;

		if (accelerometerSensor == null) {
			Toast.makeText(context, "Sorry, no accelerometer sensor found!!",
					Toast.LENGTH_SHORT).show();
		} else {
			sensorManager.registerListener(this, accelerometerSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float z = event.values[2];

		if (!isInitialized) {
			lastZ = z;
			isInitialized = true;

		} else {
			float deltaZ = Math.abs(lastZ - z);

			if (deltaZ < NOISE)
				deltaZ = 0.0f;

			lastZ = z;

			if (deltaZ > MAX_ACCELERATION) {

				if (isAlertSent == false) {

					Cursor cursor = databaseHelper.getContacts(-1);

					if (cursor != null && cursor.moveToNext()) {

						ArrayList<String> mobileList = new ArrayList<String>();

						do {
							mobileList.add(cursor.getString(2));
						} while (cursor.moveToNext());
						cursor.close();

						isAlertSent = true;

						new SendAlert(mobileList,
								LocationAccessObject.getLatitude() + ","
										+ LocationAccessObject.getLongitude())
								.start();

					}

				}

			}
		}
	}

	private class SendAlert extends Thread {

		private ArrayList<String> mobileList;
		private String location;

		public SendAlert(ArrayList<String> mobileList, String location) {
			this.mobileList = mobileList;
			this.location = location;
		}

		@Override
		public void run() {

			try {

				String message = "As suspected, bus met with an accident! Bus is now at "
						+ GetCurrentLocationDetails.getLocation(location);

				SmsManager smsManager = SmsManager.getDefault();

				ArrayList<String> messages = smsManager.divideMessage(message);

				for (int i = 0; i < mobileList.size(); i++) {
					smsManager.sendMultipartTextMessage(mobileList.get(i),
							null, messages, null, null);

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

	public void unregisterSensorEvents() {
		sensorManager.unregisterListener(this);
	}
}