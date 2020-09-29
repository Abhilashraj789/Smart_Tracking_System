/**
 * 
 */
package com.balert.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity implements OnClickListener {

	private CheckBox showCheckBox;
	private EditText currentPasswordEditText, newPasswordEditText;
	private Button changeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		showCheckBox = (CheckBox) findViewById(R.id.checkBoxShowPswrd);
		currentPasswordEditText = (EditText) findViewById(R.id.editTextOldPassword);
		newPasswordEditText = (EditText) findViewById(R.id.editTextNewPassword);
		changeButton = (Button) findViewById(R.id.buttonChange);

		showCheckBox.setOnClickListener(this);
		changeButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v == showCheckBox) {

			if (showCheckBox.isChecked()) {
				currentPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_NORMAL);
				newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_NORMAL);
			} else {
				currentPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}

		} else {

			String givenCurrentPassword = currentPasswordEditText.getText()
					.toString().trim();
			String newPassword = newPasswordEditText.getText().toString()
					.trim();

			SharedPreference sharedPreference = new SharedPreference(this, true);
			String currentPassword = sharedPreference.getPassword();

			if (givenCurrentPassword.length() == 0) {
				Toast.makeText(this, "Enter current password",
						Toast.LENGTH_SHORT).show();

			} else if (newPassword.length() == 0) {
				Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT)
						.show();

			} else if (!givenCurrentPassword.equals(currentPassword)) {
				Toast.makeText(this, "Current password given is incorrect!",
						Toast.LENGTH_SHORT).show();
			} else {
				sharedPreference.savePassword(newPassword);
				Toast.makeText(this, "Password changed successfully!!",
						Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
		}

	}
}