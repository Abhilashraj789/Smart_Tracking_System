/**
 * 
 */
package com.balert.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class EmergencyAlertsActivity extends Activity {

	private ListView alertsListView;

	private DatabaseHelper databaseHelper;

	private ArrayList<HashMap<String, String>> arrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency_alerts);

		alertsListView = (ListView) findViewById(R.id.listViewEmergencyAlerts);
		databaseHelper = new DatabaseHelper(this);

		arrayList = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.tyre_prob_icon + "");
		hashMap.put("title", "Tyre Puncture");
		hashMap.put("tag", "tyre");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.engine_prob_icon + "");
		hashMap.put("title", "Engine Problem");
		hashMap.put("tag", "engine");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.other_prob_icon + "");
		hashMap.put("title", "Other Problems");
		hashMap.put("tag", "other");

		arrayList.add(hashMap);

		alertsListView.setAdapter(new SimpleAdapter(this, arrayList,
				R.layout.alerts_entry_view, new String[] { "icon", "title",
						"tag" }, new int[] { R.id.imageViewAlertIcon,
						R.id.textViewAlertOption, R.id.textViewAlertTag }));

		alertsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						final String tag = ((TextView) view
								.findViewById(R.id.textViewAlertTag)).getText()
								.toString();

						AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(
								EmergencyAlertsActivity.this);
						confirmBuilder.setTitle("Confirm");
						confirmBuilder.setMessage("Want to send?");

						final EditText editText = new EditText(
								EmergencyAlertsActivity.this);
						editText.setHint("Enter the problem description");

						if (tag.equals("other")) {
							confirmBuilder.setView(editText);
						}

						confirmBuilder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										String message = "";

										if (tag.equals("tyre")) {
											message = "Bus tyre got punctured! ";

										} else if (tag.equals("engine")) {
											message = "Bus engine got some problem! ";

										} else if (tag.equals("other")) {

											message = editText.getText()
													.toString();

											if (message.length() == 0) {
												message = "Bus has got some problems! ";
											}
										}

										Cursor cursor = databaseHelper
												.getContacts(-1);

										String currentLocation = LocationAccessObject
												.getLatitude()
												+ ","
												+ LocationAccessObject
														.getLongitude();

										ArrayList<String> numbers = new ArrayList<String>();

										if (cursor != null
												&& cursor.moveToNext()) {
											do {
												numbers.add(cursor.getString(2));
											} while (cursor.moveToNext());
										}
										cursor.close();

										cursor = databaseHelper
												.getStudentDetails(-1);

										if (cursor != null
												&& cursor.moveToNext()) {
											do {
												if (cursor.getInt(11) == 1) {
													numbers.add(cursor
															.getString(8));
												}
											} while (cursor.moveToNext());
										}
										cursor.close();

										new SendSMS(currentLocation, message,
												numbers).start();
									}
								});

						confirmBuilder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						confirmBuilder.show();
					}

				});
	}

	private class SendSMS extends Thread {

		private String location;
		private String text;
		private ArrayList<String> mobileNos;

		public SendSMS(String location, String text, ArrayList<String> mobileNos) {
			this.location = location;
			this.text = text;
			this.mobileNos = mobileNos;
		}

		@Override
		public void run() {
			try {
				String currentLocation = text + " Bus is now at "
						+ GetCurrentLocationDetails.getLocation(location);

				SmsManager smsManager = SmsManager.getDefault();

				ArrayList<String> messages = smsManager
						.divideMessage(currentLocation);

				for (int i = 0; i < mobileNos.size(); i++) {

					smsManager.sendMultipartTextMessage(mobileNos.get(i), null,
							messages, null, null);

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
}