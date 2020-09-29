/**
 * 
 */
package com.balert.main;

import java.util.ArrayList;

import android.telephony.SmsManager;

public class SendLocationSMS extends Thread {

	private String mobile;
	private String location;

	public SendLocationSMS(String mobile, String location) {
		this.mobile = mobile;
		this.location = location;
	}

	@Override
	public void run() {

		try {
			String currentLocation = "Bus is now at "
					+ GetCurrentLocationDetails.getLocation(location);

			SmsManager smsManager = SmsManager.getDefault();

			ArrayList<String> messages = smsManager
					.divideMessage(currentLocation);

			smsManager.sendMultipartTextMessage(mobile, null, messages, null,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.run();
	}
}
