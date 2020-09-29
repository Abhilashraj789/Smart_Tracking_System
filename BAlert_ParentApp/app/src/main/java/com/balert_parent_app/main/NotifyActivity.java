/**
 * 
 */
package com.balert_parent_app.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class NotifyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notify);

		TextView notificationTextView = (TextView) findViewById(R.id.textViewNotification);
		notificationTextView.setMovementMethod(ScrollingMovementMethod
				.getInstance());

		notificationTextView.setText(getIntent().getStringExtra("messageText"));

	}

	@Override
	public void onBackPressed() {
		finish();
	}
}