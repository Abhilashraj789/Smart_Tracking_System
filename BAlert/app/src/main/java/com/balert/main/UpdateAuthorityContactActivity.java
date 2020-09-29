/**
 * 
 */
package com.balert.main;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateAuthorityContactActivity extends Activity implements
		OnClickListener {

	private EditText nameUpdateEditText, phoneUpdateEditText;

	private Button updateButton;

	private DatabaseHelper databaseHelper;

	private int contact_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_authority_contact);

		nameUpdateEditText = (EditText) findViewById(R.id.editTextNameUpdate);
		phoneUpdateEditText = (EditText) findViewById(R.id.editTextPhoneUpdate);

		updateButton = (Button) findViewById(R.id.buttonUpdate);

		updateButton.setOnClickListener(this);

		databaseHelper = new DatabaseHelper(this);

		Bundle bundle = getIntent().getExtras();
		contact_id = bundle.getInt("id");

		Cursor cursor = databaseHelper.getContacts(contact_id);

		if (cursor.moveToNext()) {
			nameUpdateEditText.setText(cursor.getString(1));
			phoneUpdateEditText.setText(cursor.getString(2));
		}
		cursor.close();

	}

	@Override
	public void onClick(View v) {

		String name = nameUpdateEditText.getText().toString().trim();
		String phone = phoneUpdateEditText.getText().toString().trim();

		if (name.length() == 0) {
			Toast.makeText(this, "Enter name!", Toast.LENGTH_SHORT).show();

		} else if (phone.length() == 0) {
			Toast.makeText(this, "Enter phone number!", Toast.LENGTH_SHORT)
					.show();

		} else {
			databaseHelper.updateContact(contact_id, name, phone);
			Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT)
					.show();
			startActivity(new Intent(this, AuthortyContactView.class));
			finish();
		}
	}
}