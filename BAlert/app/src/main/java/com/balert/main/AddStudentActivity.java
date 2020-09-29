
package com.balert.main;

import java.io.ByteArrayOutputStream;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
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

public class AddStudentActivity extends Activity implements OnClickListener, TextWatcher {

    private Button viewButton, saveButton, clearButton;

    private EditText nameEditText, regEditText, classEditText, sectionEditText, latitudeEditText,
            longitudeEditText, mobileEditText, distanceEditText;

    private ImageView profileImageView;

    private byte[] byteImage = null;

    private RadioGroup radioGroup;

    private static final int GALLERY_PHOTO_REQUEST = 3, CAMERA_PHOTO_REQUEST = 4;

    private Bitmap mainBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        profileImageView = (ImageView)findViewById(R.id.imageViewProfile);

        viewButton = (Button)findViewById(R.id.buttonDisplay);

        saveButton = (Button)findViewById(R.id.buttonSave);
        clearButton = (Button)findViewById(R.id.buttonClear);

        nameEditText = (EditText)findViewById(R.id.editTextName);
        regEditText = (EditText)findViewById(R.id.editTextRegisterNo);
        classEditText = (EditText)findViewById(R.id.editTextClass);
        sectionEditText = (EditText)findViewById(R.id.editTextSection);
        latitudeEditText = (EditText)findViewById(R.id.editTextLatitude);
        longitudeEditText = (EditText)findViewById(R.id.editTextLongitude);
        mobileEditText = (EditText)findViewById(R.id.editTextMobile);
        distanceEditText = (EditText)findViewById(R.id.editTextDistance);

        radioGroup = (RadioGroup)findViewById(R.id.radioGroupType);

        viewButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        profileImageView.setOnClickListener(this);
        nameEditText.addTextChangedListener(this);
        regEditText.addTextChangedListener(this);
        classEditText.addTextChangedListener(this);
        sectionEditText.addTextChangedListener(this);
        latitudeEditText.addTextChangedListener(this);
        longitudeEditText.addTextChangedListener(this);
        mobileEditText.addTextChangedListener(this);
        distanceEditText.addTextChangedListener(this);
        saveButton.setEnabled(false);

    }

    @Override
    public void onClick(View v) {

        if (v == profileImageView) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set your profile picture.");
            builder.setMessage("Select an option.");

            builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    try {
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 0);
                        intent.putExtra("aspectY", 0);
                        intent.putExtra("outputX", 250);
                        intent.putExtra("outputY", 250);
                        intent.putExtra("return-data", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                            GALLERY_PHOTO_REQUEST);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNeutralButton("Camera", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 0);
                        intent.putExtra("aspectY", 0);
                        intent.putExtra("outputX", 250);
                        intent.putExtra("outputY", 250);
                        intent.putExtra("return-data", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(intent, CAMERA_PHOTO_REQUEST);
                }
            });

            builder.show();

        } else if (v == viewButton) {
            startActivity(new Intent(this, ViewStudentActivity.class));
            finish();

        } else if (v == saveButton) {

            DatabaseHelper databaseHelper = new DatabaseHelper(this);

            try {
                if (byteImage == null) {
                    byteImage = getImage();
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
                int distance = Integer.parseInt(distanceEditText.getText().toString().trim());

                if (!mobile.startsWith("+91")) {
                    mobile = "+91" + mobile;
                }

                if (databaseHelper.isNumberExist(mobile)) {
                    Toast.makeText(this, "Mobile number already exist!", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.saveStudent(name, regNo, clas, byteImage, section, latitude,
                            longitude, mobile, distance, isMobileUser);

                    Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                }

            } catch (Exception ex) {
                Log.e(Util.TAG, ex.toString());
                Toast.makeText(this, "Some error found: " + ex.toString(), Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (v == clearButton) {
            clearFields();
        }
    }

    private void clearFields() {
        nameEditText.setText("");
        regEditText.setText("");
        classEditText.setText("");
        sectionEditText.setText("");
        latitudeEditText.setText("");
        longitudeEditText.setText("");
        mobileEditText.setText("");
        distanceEditText.setText("");
        profileImageView.setImageResource(R.drawable.profile_pic);
        saveButton.setEnabled(false);
    }

    protected byte[] getImage() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (reqCode) {
                case GALLERY_PHOTO_REQUEST:
                    try {
                        /*Uri selectedImage = data.getData();
                        String[] filePathColumn = {
                                MediaStore.Images.Media.DATA
                        };

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
                                null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        if (mainBitmap != null) {
                            mainBitmap.recycle();
                            mainBitmap = null;

                            profileImageView.setImageBitmap(null);
                        }

                        mainBitmap = decodeSampledBitmap(picturePath, 110, 110);

                        if (getOrientation(this, selectedImage) != 0) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(getOrientation(this, selectedImage));
                            mainBitmap = Bitmap.createBitmap(mainBitmap, 0, 0,
                                    mainBitmap.getWidth(), mainBitmap.getHeight(), matrix, true);
                        }

                        profileImageView.setImageBitmap(mainBitmap);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        mainBitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
                        byteImage = byteArrayOutputStream.toByteArray();*/

                        String filepath = data.getData().getPath();
                        Toast.makeText(this, "filepath "+filepath, Toast.LENGTH_SHORT).show();
                        Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                        profileImageView.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();

                        if (mainBitmap != null) {
                            mainBitmap.recycle();
                            mainBitmap = null;

                            profileImageView.setImageBitmap(null);
                        }

                        mainBitmap = data.getParcelableExtra("data");
                        try {
                            profileImageView.setImageBitmap(mainBitmap);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            mainBitmap.compress(CompressFormat.PNG,100, byteArrayOutputStream);
                            byteImage = byteArrayOutputStream.toByteArray();

                        } catch (Exception e1) {
                            e1.printStackTrace();
                            //Toast.makeText(this, "Failed to set profile picture. Try again.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    break;

                case CAMERA_PHOTO_REQUEST:

                    try {
                        Bundle extras = data.getExtras();

                        if (mainBitmap != null) {
                            mainBitmap.recycle();
                            mainBitmap = null;
                            profileImageView.setImageBitmap(null);
                        }

                        mainBitmap = (Bitmap)extras.get("data");

                        /** Convert Image into Byte Array */
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        mainBitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] imageByte = byteArrayOutputStream.toByteArray();

                        mainBitmap = decodeSampledBitmap(imageByte, 110, 110);

                        profileImageView.setImageBitmap(mainBitmap);

                        byteArrayOutputStream = new ByteArrayOutputStream();
                        mainBitmap.compress(CompressFormat.PNG, 100*1024, byteArrayOutputStream);
                        byteImage = byteArrayOutputStream.toByteArray();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to set profile picture. Try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    }

    @Override
    public void afterTextChanged(Editable arg0) {
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        if (!(nameEditText.getText().toString().equals("")
                || regEditText.getText().toString().equals("")
                || sectionEditText.getText().toString().equals("")
                || latitudeEditText.getText().toString().equals("")
                || longitudeEditText.getText().toString().equals("")
                || mobileEditText.getText().toString().equals("") || distanceEditText.getText()
                .toString().equals(""))) {
            saveButton.setEnabled(true);
        } else {
            saveButton.setEnabled(false);
        }
    }

    private int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri, new String[] {
                MediaStore.Images.ImageColumns.ORIENTATION
        }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        int val = cursor.getInt(0);
        cursor.close();
        return val;
    }

    private Bitmap decodeSampledBitmap(String picturePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);
    }

    public static Bitmap decodeSampledBitmap(byte[] pictureData, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}