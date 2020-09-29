/**
 * 
 */
package com.balert_parent_app.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class HomeActivity extends Activity {

	private ListView listView;
	private SharedPreference sharedPreference;
	private AlertDialog.Builder alertBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		listView = (ListView) findViewById(R.id.listView1);

		sharedPreference = new SharedPreference(this);

		alertBuilder = new AlertDialog.Builder(this);

		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.location + "");
		hashMap.put("title", "Query Location");
		hashMap.put("tag", "location");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.delete + "");
		hashMap.put("title", "Absent");
		hashMap.put("tag", "absent");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.set + "");
		hashMap.put("title", "Settings");
		hashMap.put("tag", "settings");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.help + "");
		hashMap.put("title", "Help");
		hashMap.put("tag", "help");

		arrayList.add(hashMap);

		hashMap = new HashMap<String, String>();
		hashMap.put("icon", R.drawable.about + "");
		hashMap.put("title", "Info");
		hashMap.put("tag", "info");

		arrayList.add(hashMap);

		listView.setAdapter(new SimpleAdapter(this, arrayList,
				R.layout.alerts_entry_view, new String[] { "icon", "title",
						"tag" }, new int[] { R.id.imageViewAlertIcon,
						R.id.textViewAlertOption, R.id.textViewAlertTag }));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				String tag = ((TextView) view
						.findViewById(R.id.textViewAlertTag)).getText()
						.toString();

				if (tag.equals("settings")) {
					alertBuilder.setTitle("Set Number");

					final EditText editText = new EditText(HomeActivity.this);
					editText.setHint("Enter the number");
					editText.setInputType(InputType.TYPE_CLASS_PHONE);
					alertBuilder.setView(editText);

					alertBuilder.setPositiveButton("Set",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String number = editText.getText()
											.toString();
									if (number.length() == 0) {
										Toast.makeText(getApplicationContext(),
												"Enter number!",
												Toast.LENGTH_SHORT).show();
									} else {
										if (!number.startsWith("+91")) {
											number = "+91" + number;
										}

										sharedPreference.save(number);
										Toast.makeText(getApplicationContext(),
												"Set successfully!",
												Toast.LENGTH_SHORT).show();
									}
								}
							});

					alertBuilder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					alertBuilder.show();

				} else if (tag.equals("location")) {

					String number = sharedPreference.getNumber();
					if (number.equals("NULL")) {
						Toast.makeText(getApplicationContext(),
								"Set number first!", Toast.LENGTH_SHORT).show();
					} else {
						sendSMS("Location", number);
					}

				} else if (tag.equals("absent")) {
					String number = sharedPreference.getNumber();
					if (number.equals("NULL")) {
						Toast.makeText(getApplicationContext(),
								"Set number first!", Toast.LENGTH_SHORT).show();
					} else {
						sendSMS("Absent", number);
					}
				} else if (tag.equals("help")) {
					startActivity(new Intent(HomeActivity.this,
							HelpActivity.class));
				} else if (tag.equals("info")) {
					startActivity(new Intent(HomeActivity.this,
							AboutActivity.class));
				}
			}
		});
	}

	private void sendSMS(String text, String number) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(number, null, text, null, null);
		Toast.makeText(getApplicationContext(), "Query sent!",
				Toast.LENGTH_SHORT).show();
	}

}