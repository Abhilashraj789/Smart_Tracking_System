/**
 * 
 */
package com.balert_parent_app.main;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreference {

	private SharedPreferences sharedPreferences;

	private static final String PREF_NAME = "balert_number";
	private static final String KEY = "number";

	public SharedPreference(Context context) {
		sharedPreferences = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
	}

	public void save(String number) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY, number);
		editor.commit();
	}

	public String getNumber() {
		return sharedPreferences.getString(KEY, "NULL");
	}
}