/**
 * 
 */
package com.balert.main;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreference {

	private static final String LOGIN_PREFERENCE = "pref_authentication";
	private static final String PASSWORD_KEY = "password";

	private static final String SCHOOL_LOCATION_PREFERENCE = "pref_school_location";
	private static final String LATITUDE_KEY = "latitude";
	private static final String LONGITUDE_KEY = "longitude";

	private SharedPreferences sharedPreferences;

	public SharedPreference(Context context, boolean isLogin) {
		if (isLogin) {
			sharedPreferences = context.getSharedPreferences(LOGIN_PREFERENCE,
					Context.MODE_PRIVATE);
		} else {
			sharedPreferences = context.getSharedPreferences(
					SCHOOL_LOCATION_PREFERENCE, Context.MODE_PRIVATE);
		}
	}

	public void savePassword(String password) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(PASSWORD_KEY, password);
		editor.commit();
	}

	public String getPassword() {
		return sharedPreferences.getString(PASSWORD_KEY, "admin");
	}

	public void saveLocation(String latitude, String longitude) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(LATITUDE_KEY, latitude);
		editor.putString(LONGITUDE_KEY, longitude);
		editor.commit();
	}

	public String getLatitude() {
		return sharedPreferences.getString(LATITUDE_KEY, "NULL");
	}

	public String getLongitude() {
		return sharedPreferences.getString(LONGITUDE_KEY, "NULL");
	}
}