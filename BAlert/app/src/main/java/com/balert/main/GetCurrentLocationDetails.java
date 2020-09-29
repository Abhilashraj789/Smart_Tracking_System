/**
 * Class which gets location name by parsing the json data from google map api
 */
package com.balert.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class GetCurrentLocationDetails {

	static JSONObject jsonObject;

	public static String getLocation(String currentLocation) {

		String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="
				+ currentLocation
				+ "&destinations="
				+ currentLocation
				+ "&mode=driving&sensor=true";

		return parseJSON(getJSONfromURL(url), currentLocation);
	}


	/** Method to retrieve JSON Object from URL */
	private static JSONObject getJSONfromURL(String url) {
		String result = "";


		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();

			// convert response to string
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			result = sb.toString();

			JSONObject jsonObj = new JSONObject(result);

			return jsonObj;

		} catch (Exception e) {
			Log.e(Util.TAG,
					"Exception while converting into json object "
							+ e.toString());
		}

		return null;
	}

	/** Method to parse the JSON Object */
	private static String parseJSON(JSONObject jsonObject,
			String currentLocation) {
		try {
			JSONArray jsonArray = jsonObject
					.getJSONArray("destination_addresses");

			return jsonArray.get(0).toString();

		} catch (Exception e) {
			Log.e(Util.TAG,
					"Exception while parsing json object: " + e.toString());
		}

		return "https://www.google.com/maps/search/" + currentLocation;

	}
}