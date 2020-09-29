/**
 * 
 */
package com.balert.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainMenusActivity extends Activity {

	private ListView listView;
	private SharedPreference sharedPreference;
	private static final int SCHOOL_REGION = 200;
	private DatabaseHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency_alerts);

		sharedPreference = new SharedPreference(this, false);

		databaseHelper = new DatabaseHelper(this);

		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

		listView = (ListView) findViewById(R.id.listViewEmergencyAlerts);

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.service + "");
		hashMap.put("title", "Service");
		hashMap.put("tag", "service");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.school_icon + "");
		hashMap.put("title", "School Reached");
		hashMap.put("tag", "reached");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.stuinfo + "");
		hashMap.put("title", "Student");
		hashMap.put("tag", "student");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.authority + "");
		hashMap.put("title", "Authority");
		hashMap.put("tag", "authority");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.location + "");
		hashMap.put("title", "School Location");
		hashMap.put("tag", "location");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.other_prob_icon + "");
		hashMap.put("title", "Alert");
		hashMap.put("tag", "alert");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.password_icon + "");
		hashMap.put("title", "Change Password");
		hashMap.put("tag", "password");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.bout + "");
		hashMap.put("title", "About");
		hashMap.put("tag", "about");

		arrayList.add(hashMap);

		listView.setAdapter(new SimpleAdapter(this, arrayList,
				R.layout.alerts_entry_view, new String[] { "icon", "title",
						"tag" }, new int[] { R.id.imageViewAlertIcon,
						R.id.textViewAlertOption, R.id.textViewAlertTag }));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final String tag = ((TextView) view
						.findViewById(R.id.textViewAlertTag)).getText()
						.toString();

				if (tag.equals("service")) {
					if (!MainAlertService.isActive) {
						startService(new Intent(MainMenusActivity.this,
								MainAlertService.class));
						Toast.makeText(getApplicationContext(),
								"Service started!", Toast.LENGTH_SHORT).show();
					} else {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainMenusActivity.this);
						builder.setTitle("Confirm!");
						builder.setMessage("Service already started! Want to stop now?");

						builder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										stopService(new Intent(
												MainMenusActivity.this,
												MainAlertService.class));
										Toast.makeText(getApplicationContext(),
												"Service stopped!",
												Toast.LENGTH_SHORT).show();
									}
								});

						builder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						builder.show();
					}

				} else if (tag.equals("reached")) {
					new SchoolReached().start();
				} else if (tag.equals("student")) {
					startActivity(new Intent(MainMenusActivity.this,
							ViewStudentActivity.class));

				} else if (tag.equals("authority")) {
					startActivity(new Intent(MainMenusActivity.this,
							AuthortyContactView.class));

				} else if (tag.equals("location")) {
					startActivity(new Intent(MainMenusActivity.this,
							SetSchoolLocationActivity.class));

				} else if (tag.equals("alert")) {
					startActivity(new Intent(MainMenusActivity.this,
							EmergencyAlertsActivity.class));

				} else if (tag.equals("about")) {
					startActivity(new Intent(MainMenusActivity.this,
							About.class));

				} else if (tag.equals("password")) {
					startActivity(new Intent(MainMenusActivity.this,
							ChangePasswordActivity.class));

				}
			}

		});
	}

	private class SchoolReached extends Thread {

		@Override
		public void run() {

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

				databaseHelper.close();

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

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Alert sent",
								Toast.LENGTH_SHORT).show();
						stopService(new Intent(MainMenusActivity.this,
								MainAlertService.class));

					}
				});

			} else {
				Log.d(Util.TAG, "School not yet reached! " + dist_mts);
			}

			try {
				Thread.sleep(1000 * 30);
			} catch (InterruptedException e) {
			}

			super.run();
		}
	}
}