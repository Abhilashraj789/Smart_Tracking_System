/**
 * 
 */
package com.balert.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class MessageReciever extends BroadcastReceiver  {

	private DatabaseHelper databaseHelper;
	private String sender;
	private String messageText;
    TextToSpeech _speech;
	public static Context context;
	public static String locationName;
	static String stopLocation = "";
	static String stud_name = "";

	@Override
	public void onReceive(Context context, Intent intent) {

		this.context = context;
		Bundle bundle = intent.getExtras();

		databaseHelper = new DatabaseHelper(context);
		/*_speech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int i) {
				if (i == TextToSpeech.SUCCESS) {

					int result = _speech.setLanguage(Locale.US);

					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e("TTS", "This Language is not supported");
					}
				} else {
					Log.e("TTS", "Initilization Failed!");
				}
			}
		});
*/
		// AlertDialog.Builder builder = new AlertDialog.Builder(context);

		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] msgs = new SmsMessage[pdus.length];

			for (int i = 0; i < msgs.length; i++) {

				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				if (i == 0) {
					sender = msgs[i].getOriginatingAddress();

					if (databaseHelper.isNumberExist(sender)) {

						Toast.makeText(context, "New message from parent!",
								Toast.LENGTH_SHORT).show();

						messageText = msgs[i].getMessageBody().toString()
								.trim();

						if (messageText.equalsIgnoreCase("Absent")) {
							databaseHelper.updatePresenceFlag(sender, 0);

							// builder.setTitle("Absent Notification!");

							Cursor cursor = databaseHelper
									.getStudentDetails(sender);


							if (cursor.moveToNext()) {
								stud_name = cursor.getString(1);
								String location = cursor.getString(6) + ","
										+ cursor.getString(7);

								//Toast.makeText(context , location , Toast.LENGTH_LONG).show();
								stopLocation = getLocation(location);
							}
							cursor.close();


							/*for (int j = 0; j < 3; j++) {
								Toast.makeText(
										context,
										stud_name
												+ " is absent today. The location is "
												+ stopLocation,
										Toast.LENGTH_LONG).show();
							}*/


							//_speech.speak(message, TextToSpeech.QUEUE_FLUSH, null);


							/*
							 * builder.setMessage(stud_name +
							 * " is absent today. His location is " +
							 * stopLocation); builder.setPositiveButton("OK",
							 * new DialogInterface.OnClickListener() {
							 * 
							 * @Override public void onClick( DialogInterface
							 * dialog, int which) { dialog.dismiss(); } });
							 * builder.show();
							 */

						} else if (messageText.equalsIgnoreCase("Location")) {
							String location = LocationAccessObject
									.getLatitude()
									+ ","
									+ LocationAccessObject.getLongitude();
							new SendLocationSMS(sender, location).start();
						}
					} else {
						Log.d(Util.TAG, "Non registered user: " + sender);
					}
				}
			}
		}
	}

	public static String  getLocation(String currentLocation) {

		//Toast.makeText(context , currentLocation , Toast.LENGTH_LONG).show();
		String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ currentLocation + "&sensor=true";
		//Toast.makeText(context, "url "+url, Toast.LENGTH_SHORT).show();
		getJSONfromURL(url);
		/*parseJSON(jsonObject);*/

		//Toast.makeText(context , locationName , Toast.LENGTH_LONG).show();
		return locationName;
	}

	/** Method to retrieve JSON Object from URL */
	private static void getJSONfromURL(String url) {
		final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				//Toast.makeText(context, "data "+response, Toast.LENGTH_SHORT).show();
				parseJSON(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		Volley.newRequestQueue(context).add(jsonObjectRequest);
	}

	/** Method to parse the JSON Object */
	private static void parseJSON(JSONObject jsonObject) {
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			locationName = jsonArray.getJSONObject(0).getString("formatted_address");

			String message = stud_name
					+ " is absent today." + " The location is " + locationName;
			Toast.makeText(context , message , Toast.LENGTH_LONG).show();

			Intent intent1 = new Intent(context , TTS.class);
			intent1.putExtra("msg",message);
			context.startService(intent1);

			Intent intent = new Intent(context , GetAddress.class);
			intent.putExtra("message" , message);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);


			//Toast.makeText(context , locationName , Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			Log.e("Exception ",
					"Exception while parsing json object " + e.toString());
		}

	}
}

