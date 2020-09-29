/**
 * 
 */
package com.balert.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetSchoolLocationActivity extends Activity {

	private EditText latitudeEditText, longitudeEditText;
	private Button saveButton;
	private SharedPreference sharedPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_school_location);

		latitudeEditText = (EditText) findViewById(R.id.editTextLatitude);
		longitudeEditText = (EditText) findViewById(R.id.editTextLongitude);

		saveButton = (Button) findViewById(R.id.buttonSave);

		sharedPreference = new SharedPreference(this, false);

		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String latitude = latitudeEditText.getText().toString().trim();
				String longitude = longitudeEditText.getText().toString()
						.trim();

				if (latitude.length() > 0 && longitude.length() > 0) {
					sharedPreference.saveLocation(latitude, longitude);
					Toast.makeText(getApplicationContext(),
							"Location set successfully!", Toast.LENGTH_SHORT)
							.show();
					finish();
					return;

				} else {
					Toast.makeText(getApplicationContext(),
							"Enter both latitude and longitude",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

}
