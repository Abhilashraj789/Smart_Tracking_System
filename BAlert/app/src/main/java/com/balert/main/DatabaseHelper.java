package com.balert.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "db_balert";

	private static final String TABLE_NAME = "table_student_info";
	private static final String ID_KEY = "stud_id";
	private static final String NAME_KEY = "stud_name";
	private static final String REG_KEY = "stud_reg_no";
	private static final String CLASS_KEY = "stud_class";
	private static final String PHOTO_KEY = "stud_photo";
	private static final String SECTION_KEY = "stud_section";
	private static final String LATITUDE_KEY = "stud_latitude";
	private static final String LONGITUDE_KEY = "stud_longitude";
	private static final String MOBILE_KEY = "stud_mobile";
	private static final String DISTANCE_KEY = "stud_distance";
	private static final String TYPE_KEY = "stud_type";
	private static final String PRESENCE_FLAG_KEY = "stud_presence_flag";

	private static final String TABLE_AUTHORITY_NAME = "table_authority";
	private static final String AUTHORITY_ID_KEY = "id";
	private static final String AUTHORITY_NAME_KEY = "name";
	private static final String AUTHORITY_NUMBER_KEY = "phone";

	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase database;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME_KEY
				+ " VARCHAR NOT NULL, " + REG_KEY + " VARCHAR NOT NULL ,"
				+ CLASS_KEY + " VARCHAR NOT NULL, " + PHOTO_KEY
				+ " BLOB NOT NULL, " + SECTION_KEY + " VARCHAR NOT NULL,"
				+ LATITUDE_KEY + " VARCHAR NOT NULL, " + LONGITUDE_KEY
				+ " VARCHAR NOT NULL, " + MOBILE_KEY + " VARCHAR NOT NULL, "
				+ DISTANCE_KEY + " INTEGER NOT NULL, " + TYPE_KEY
				+ " INTEGER NOT NULL, " + PRESENCE_FLAG_KEY
				+ " INTEGER NOT NULL)";
		db.execSQL(query);

		query = "CREATE TABLE IF NOT EXISTS " + TABLE_AUTHORITY_NAME + " ("
				+ AUTHORITY_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ AUTHORITY_NAME_KEY + " VARCHAR NOT NULL,"
				+ AUTHORITY_NUMBER_KEY + " VARCHAR NOT NULL)";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public int saveStudent(String name, String regNo, String clas,
			byte[] photo, String section, String latitude, String longitude,
			String mobile, int distance, int type) {

		database = getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(NAME_KEY, name);
		contentValues.put(REG_KEY, regNo);
		contentValues.put(CLASS_KEY, clas);
		contentValues.put(PHOTO_KEY, photo);
		contentValues.put(SECTION_KEY, section);
		contentValues.put(LATITUDE_KEY, latitude);
		contentValues.put(LONGITUDE_KEY, longitude);
		contentValues.put(MOBILE_KEY, mobile);
		contentValues.put(DISTANCE_KEY, distance);
		contentValues.put(TYPE_KEY, type);
		contentValues.put(PRESENCE_FLAG_KEY, 1);

		int r = (int) database.insert(TABLE_NAME, null, contentValues);
		database.close();

		return r;
	}

	public boolean isNumberExist(String mobile) {
		boolean isExist = false;

		SQLiteDatabase database = getReadableDatabase();
		String query = "SELECT " + ID_KEY + " FROM " + TABLE_NAME + " WHERE "
				+ MOBILE_KEY + " LIKE '%" + mobile + "%'";
		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null && cursor.moveToNext()) {
			isExist = true;
		}
		cursor.close();
		database.close();
		return isExist;
	}

	public boolean delete(int rowId) {
		database = getWritableDatabase();
		boolean flag = database
				.delete(TABLE_NAME, ID_KEY + " = " + rowId, null) > 0;
		database.close();
		return flag;
	}

	public Cursor getPhoto(int rowId) throws Exception {
		database = getReadableDatabase();
		Cursor cursor = database.query(true, TABLE_NAME,
				new String[] { PHOTO_KEY }, ID_KEY + "=" + rowId, null, null,
				null, null, null);
		return cursor;
	}

	public Cursor getStudentDetails(int rowId) {
		String query = "";
		if (rowId == -1) {
			query = "SELECT * FROM " + TABLE_NAME;

		} else {
			query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_KEY + " = "
					+ rowId;
		}
		database = getReadableDatabase();
		Cursor cursor = database.rawQuery(query, null);
		return cursor;
	}

	public Cursor getStudentDetails(String mobile) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + MOBILE_KEY
				+ " LIKE '%" + mobile + "%'";
		database = getReadableDatabase();
		Cursor cursor = database.rawQuery(query, null);
		return cursor;
	}

	public int updateStudent(int rowId, String name, String regNo, String clas,
			byte[] photo, String section, String latitude, String longitude,
			String mobile, int distance, int type) {

		database = getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(NAME_KEY, name);
		contentValues.put(REG_KEY, regNo);
		contentValues.put(CLASS_KEY, clas);
		contentValues.put(PHOTO_KEY, photo);
		contentValues.put(SECTION_KEY, section);
		contentValues.put(LATITUDE_KEY, latitude);
		contentValues.put(LONGITUDE_KEY, longitude);
		contentValues.put(MOBILE_KEY, mobile);
		contentValues.put(DISTANCE_KEY, distance);
		contentValues.put(TYPE_KEY, type);
		contentValues.put(PRESENCE_FLAG_KEY, 1);

		int r = database.update(TABLE_NAME, contentValues, ID_KEY + " = "
				+ rowId, null);
		database.close();
		return r;
	}

	public boolean updatePresenceFlag(String phone, int flag) {
		database = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PRESENCE_FLAG_KEY, flag);
		boolean isDone = database.update(TABLE_NAME, contentValues, MOBILE_KEY
				+ " = '" + phone + "'", null) > 0;
		database.close();
		return isDone;
	}

	public void updatePresence() {
		database = getWritableDatabase();
		String query = "UPDATE " + TABLE_NAME + " SET " + PRESENCE_FLAG_KEY
				+ " = 1 ";
		database.execSQL(query);
		database.close();
	}

	public void saveContact(String name, String phone) {
		database = getWritableDatabase();
		String query = "INSERT INTO " + TABLE_AUTHORITY_NAME + "("
				+ AUTHORITY_NAME_KEY + "," + AUTHORITY_NUMBER_KEY
				+ ") VALUES('" + name + "','" + phone + "')";
		database.execSQL(query);
		database.close();
	}

	public void deleteContact(int id) {
		database = getWritableDatabase();
		String query = "DELETE FROM " + TABLE_AUTHORITY_NAME + " WHERE "
				+ AUTHORITY_ID_KEY + " = " + id;
		database.execSQL(query);
		database.close();
	}

	public Cursor getContacts(int rowId) {

		database = getReadableDatabase();

		String query = "";

		if (rowId == -1) {
			query = "SELECT * FROM " + TABLE_AUTHORITY_NAME;
		} else {
			query = "SELECT * FROM " + TABLE_AUTHORITY_NAME + " WHERE "
					+ AUTHORITY_ID_KEY + " = " + rowId;
		}
		Cursor cursor = database.rawQuery(query, null);
		return cursor;
	}

	public void updateContact(int id, String name, String phone) {
		database = getWritableDatabase();
		String query = "UPDATE " + TABLE_AUTHORITY_NAME + " SET "
				+ AUTHORITY_NAME_KEY + " = '" + name + "',"
				+ AUTHORITY_NUMBER_KEY + " = '" + phone + "' WHERE "
				+ AUTHORITY_ID_KEY + " = " + id;
		database.execSQL(query);
		database.close();
	}
}