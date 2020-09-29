/**
 * 
 */
package com.balert.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AuthortyContactView extends Activity implements
		OnItemClickListener {

	private ListView listView;

	private DatabaseHelper databaseHelper;

	private Button addContactButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acivity_authority_contacts);

		listView = (ListView) findViewById(R.id.listViewAuthority);

		databaseHelper = new DatabaseHelper(this);

		addContactButton = (Button) findViewById(R.id.buttonAddContacts);

		addContactButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(AuthortyContactView.this,
						AddAuthortyContacts.class));
			}
		});

		Cursor cursor = databaseHelper.getContacts(-1);

		ArrayList<HashMap<String, String>> itemArrayList = new ArrayList<HashMap<String, String>>();

		if (cursor != null && cursor.moveToNext()) {

			do {
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String phone = cursor.getString(2);

				HashMap<String, String> hashMap = new HashMap<String, String>();

				hashMap.put("contact_id", String.valueOf(id));
				hashMap.put("contact_name", name);
				hashMap.put("contact_phone", phone);

				itemArrayList.add(hashMap);

			} while (cursor.moveToNext());

			cursor.close();

			listView.setAdapter(new SimpleAdapter(this, itemArrayList,
					R.layout.contacts_view, new String[] { "contact_id",
							"contact_name", "contact_phone" }, new int[] {
							R.id.textViewId, R.id.textViewName,
							R.id.textViewPhone }));
			listView.setOnItemClickListener(this);

		} else {
			Toast.makeText(this, "No contacts found!", Toast.LENGTH_SHORT)
					.show();
		}

		((Button) findViewById(R.id.buttonAddContacts))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(AuthortyContactView.this,
								AddAuthortyContacts.class));
					}
				});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		TextView idTextView = (TextView) view.findViewById(R.id.textViewId);

		final int contact_id = Integer
				.parseInt(idTextView.getText().toString());

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Options");
		alert.setMessage("Select the option!");

		alert.setPositiveButton("Update", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(AuthortyContactView.this,
						UpdateAuthorityContactActivity.class);
				intent.putExtra("id", contact_id);
				startActivity(intent);
			}
		});

		alert.setNegativeButton("Delete", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						AuthortyContactView.this);

				builder.setTitle("Confirm!");
				builder.setMessage("Are u sure to delete this?");

				builder.setPositiveButton("Yes", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						databaseHelper.deleteContact(contact_id);

						Toast.makeText(getApplicationContext(),
								"Deleted successfully!", Toast.LENGTH_SHORT)
								.show();
						startActivity(getIntent());
						finish();
					}
				});

				builder.setNegativeButton("No", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builder.show();
			}
		});

		alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alert.show();
	}

	@Override
	protected void onDestroy() {
		databaseHelper.close();
		super.onDestroy();
	}
}