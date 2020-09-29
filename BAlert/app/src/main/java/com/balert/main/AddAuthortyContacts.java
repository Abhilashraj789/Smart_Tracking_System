/**
 * 
 */
package com.balert.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddAuthortyContacts extends Activity implements OnClickListener {

	private EditText nameEditText, phoneEditText;

	private Button saveButton, clearButton;

	private DatabaseHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authority_contact_add);

		nameEditText = (EditText) findViewById(R.id.editTextName);
		phoneEditText = (EditText) findViewById(R.id.editTextPhone);

		saveButton = (Button) findViewById(R.id.buttonSave);
		clearButton = (Button) findViewById(R.id.buttonClear);

		saveButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);

		databaseHelper = new DatabaseHelper(this);

	}

	@Override
	public void onClick(View v) {

		if (v == saveButton) {

			String name = nameEditText.getText().toString().trim();
			String phone = phoneEditText.getText().toString().trim();

			if (name.length() == 0) {
				Toast.makeText(this, "Enter name!", Toast.LENGTH_SHORT).show();

			} else if (phone.length() == 0) {
				Toast.makeText(this, "Enter phone number!", Toast.LENGTH_SHORT)
						.show();

			} else {
				databaseHelper.saveContact(name, phone);
				Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT)
						.show();
				nameEditText.setText("");
				phoneEditText.setText("");

				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Confirm!");
				alert.setMessage("Want to add another contact?");

				alert.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alert.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startActivity(new Intent(
										AddAuthortyContacts.this,
										AuthortyContactView.class));
								finish();
							}
						});

				alert.show();
			}
		} else {
			nameEditText.setText("");
			phoneEditText.setText("");
		}

	}
}