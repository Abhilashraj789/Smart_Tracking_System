package com.balert.main;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText passwordEditText;
	private Button loginButton;
	private CheckBox showCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		passwordEditText = (EditText) findViewById(R.id.editTextPassword);

		loginButton = (Button) findViewById(R.id.buttonLogin);

		showCheckBox = (CheckBox) findViewById(R.id.checkBoxShowPassword);

		showCheckBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (showCheckBox.isChecked()) {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_NORMAL);
				} else {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}

			}
		});

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = passwordEditText.getText().toString().trim();

				if (password.length() == 0) {
					Toast.makeText(LoginActivity.this, "Enter password!",
							Toast.LENGTH_SHORT).show();
				} else {
					if (new SharedPreference(LoginActivity.this, true)
							.getPassword().equals(password)) {
						startActivity(new Intent(LoginActivity.this,
								MainMenusActivity.class));

					} else {
						Toast.makeText(LoginActivity.this, "Invalid password!",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

	}

}
