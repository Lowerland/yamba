package com.example.yamba;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {
	public static final String AUTHORITY = "content://com.example.yamba.provider";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);

	DbHelper dbHelper;
	SQLiteDatabase db;

	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		if (uri.getLastPathSegment() == null) {
			return "vnd.android.cursor.item/vnd.example.yamba.status";
		} else {
			return "vnd.android.cursor.dir/vnd.example.yamba.status";
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		db = dbHelper.getWritableDatabase();
		long id = db.insertWithOnConflict(StatusData.TABLE, null, values,
				SQLiteDatabase.CONFLICT_IGNORE);

		if (id != -1)
			uri = Uri.withAppendedPath(uri, Long.toString(id));

		return uri;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		return 0;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(StatusData.TABLE, projection, selection,
				selectionArgs, null, null, sortOrder);
		return cursor;
	}

}

class DbHelper extends SQLiteOpenHelper {

	static final String TAG = "DbHelper";

	public DbHelper(Context context) {
		super(context, StatusData.DB_NAME, null, StatusData.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format("create table %s"
				+ "(%s int primary key, %s int, %s text, %s text)",
				StatusData.TABLE, StatusData.C_ID, StatusData.C_CREATED_AT,
				StatusData.C_USER, StatusData.C_TEXT);
		Log.d(TAG, "onCreate with SQL: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade from " + oldVersion + " to " + newVersion);
		// Usually ALTER TABLE statement
		db.execSQL("drop table if exists" + StatusData.TABLE);
		onCreate(db);
	}

}
