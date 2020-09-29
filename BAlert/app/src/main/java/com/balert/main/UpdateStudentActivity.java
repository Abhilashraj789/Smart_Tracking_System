package com.balert.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UpdateStudentActivity extends Activity implements OnClickListener,
		TextWatcher {

	private Button addButton, viewButton, updateButton;
	private EditText nameEditText, regEditText, classEditText, sectionEditText,
			latitudeEditText, longitudeEditText, mobileEditText,
			distanceEditText;
	private ImageView profileImageView;
	private byte[] byteImage1 = null;
	private String picturePath = null;
	private RadioGroup radioGroup;
	private DatabaseHelper databaseHelper;
	private byte[] image;
	private int studId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_student);

		profileImageView = (ImageView) findViewById(R.id.imageViewProfile);

		addButton = (Button) findViewById(R.id.buttonAddNew);
		viewButton = (Button) findViewById(R.id.buttonDisplay);

		updateButton = (Button) findViewById(R.id.buttonSave);

		nameEditText = (EditText) findViewById(R.id.editTextName);
		regEditText = (EditText) findViewById(R.id.editTextRegisterNo);
		classEditText = (EditText) findViewById(R.id.editTextClass);
		sectionEditText = (EditText) findViewById(R.id.editTextSection);
		latitudeEditText = (EditText) findViewById(R.id.editTextLatitude);
		longitudeEditText = (EditText) findViewById(R.id.editTextLongitude);
		mobileEditText = (EditText) findViewById(R.id.editTextMobile);
		distanceEditText = (EditText) findViewById(R.id.editTextDistanceUpdate);

		radioGroup = (RadioGroup) findViewById(R.id.radioGroupType);

		addButton.setOnClickListener(this);
		viewButton.setOnClickListener(this);
		updateButton.setOnClickListener(this);

		profileImageView.setOnClickListener(this);
		nameEditText.addTextChangedListener(this);
		regEditText.addTextChangedListener(this);
		classEditText.addTextChangedListener(this);
		sectionEditText.addTextChangedListener(this);
		latitudeEditText.addTextChangedListener(this);
		longitudeEditText.addTextChangedListener(this);
		mobileEditText.addTextChangedListener(this);
		distanceEditText.addTextChangedListener(this);
		updateButton.setEnabled(false);

		databaseHelper = new DatabaseHelper(this);

		Bundle bundle = getIntent().getExtras();
		studId = bundle.getInt("studId");

		Cursor cursor = databaseHelper.getStudentDetails(studId);

		if (cursor.moveToNext()) {
			nameEditText.setText(cursor.getString(1));
			regEditText.setText(cursor.getString(2));
			classEditText.setText(cursor.getString(3));

			image = cursor.getBlob(4);

			Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,
					image.length);
			profileImageView.setImageBitmap(bitmap);

			sectionEditText.setText(cursor.getString(5));
			latitudeEditText.setText(cursor.getString(6));
			longitudeEditText.setText(cursor.getString(7));
			mobileEditText.setText(cursor.getString(8));
			distanceEditText.setText(cursor.getInt(9) + "");
			Log.e(Util.TAG, cursor.getInt(9) + "");
			int ismobileUser = cursor.getInt(10);
			if (ismobileUser == 1) {
				radioGroup.check(R.id.radioYes);
			} else {
				radioGroup.check(R.id.radioNo);
			}
		}
		cursor.close();

	}

	@Override
	public void onClick(View v) {

		if (v.equals(profileImageView)) {
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 1);

		} else if (v.equals(viewButton)) {
			startActivity(new Intent(this, ViewStudentActivity.class));
			finish();

		} else if (v.equals(updateButton)) {

			try {
				if (picturePath != null) {
					BufferedInputStream bif = new BufferedInputStream(
							new FileInputStream(picturePath));
					byteImage1 = new byte[bif.available()];
					bif.read(byteImage1);
					picturePath = null;
				} else {
					byteImage1 = image;
				}
				int selectedId = radioGroup.getCheckedRadioButtonId();

				int isMobileUser = 0;

				if (selectedId == R.id.radioYes) {
					isMobileUser = 1;
				}

				String name = nameEditText.getText().toString();
				String regNo = regEditText.getText().toString();
				String clas = classEditText.getText().toString();
				String section = sectionEditText.getText().toString();
				String latitude = latitudeEditText.getText().toString();
				String longitude = longitudeEditText.getText().toString();
				String mobile = mobileEditText.getText().toString().trim();
				int distance = Integer.parseInt(distanceEditText.getText()
						.toString().trim());

				int row = databaseHelper.updateStudent(studId, name, regNo,
						clas, byteImage1, section, latitude, longitude, mobile,
						distance, isMobileUser);

				if (row > 0) {
					Toast.makeText(this,
							"Student details updated successfully!",
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(this, ViewStudentActivity.class));
					finish();

				} else {
					Toast.makeText(this, "Updation failed! Try again!",
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception ex) {
				Log.e(Util.TAG, ex.toString());
				Toast.makeText(this, "Some error found: " + ex.toString(),
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (reqCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				cursor.close();

				profileImageView.setImageBitmap(BitmapFactory
						.decodeFile(picturePath));
			}
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		if (!(nameEditText.getText().toString().equals("")
				|| regEditText.getText().toString().equals("")
				|| sectionEditText.getText().toString().equals("")
				|| latitudeEditText.getText().toString().equals("")
				|| longitudeEditText.getText().toString().equals("")
				|| mobileEditText.getText().toString().equals("") || distanceEditText
				.getText().toString().equals(""))) {
			updateButton.setEnabled(true);
		} else {
			updateButton.setEnabled(false);
		}
	}
}