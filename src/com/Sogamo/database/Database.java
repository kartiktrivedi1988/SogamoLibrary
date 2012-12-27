package com.Sogamo.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {

	private Context context;
	private SQLiteDatabase db;
	private OpenHelper openHelper;

	public Database(Context context) {
		this.context = context;

	}

	private void openConnnection() {
		openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}

	private void closeConnnection() {
		openHelper.close();
		db.close();
	}

	public boolean createTable(String sql) {
		openConnnection();
		try {
			db.execSQL(sql);
			closeConnnection();
			return true;

		} catch (Exception e) {
			// String s="";
		}
		closeConnnection();
		return false;

	}

	public boolean insert(ContentValues values, String TABLE_NAME) {
		openConnnection();

		long a = db.insert(TABLE_NAME, null, values);
		closeConnnection();
		return a > 0 ? true : false;
	}

	public boolean update(ContentValues values, String TABLE_NAME,
			String whereClause) {
		openConnnection();

		long a = db.update(TABLE_NAME, values, whereClause, null);
		closeConnnection();
		return a > 0 ? true : false;
	}

	public boolean delete(String TABLE_NAME, String whereClause) {
		openConnnection();

		long a = db.delete(TABLE_NAME, whereClause, null);
		closeConnnection();
		return a > 0 ? true : false;
	}

	public ArrayList<ContentValues> SelectData(String query) {
		openConnnection();
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				ContentValues values = new ContentValues();
				for (int i = 0; i < cursor.getColumnCount(); ++i) {
					values.put(cursor.getColumnName(i), cursor.getString(i));
				}
				list.add(values);
			} while (cursor.moveToNext());

		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		closeConnnection();
		return list;
	}

	public ArrayList<ContentValues> SelectData(String query, int limit) {
		openConnnection();
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		Cursor cursor = db.rawQuery(query, null);

		if (limit > cursor.getCount())
			limit = cursor.getCount();
		int counter = 1;
		if (cursor.moveToFirst()) {
			do {
				ContentValues values = new ContentValues();
				for (int i = 0; i < cursor.getColumnCount(); ++i) {
					values.put(cursor.getColumnName(i), cursor.getString(i));
				}
				list.add(values);
				if (counter == limit)
					break;
			} while (cursor.moveToNext());

		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		closeConnnection();
		return list;
	}

	public int count(String qry) {
		openConnnection();
		Cursor cursor = db.rawQuery(qry, null);
		int a = cursor.getCount();
		cursor.close();
		closeConnnection();
		return a;

	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, "Sogamo.db", null, 1);
		}

		public void onCreate(SQLiteDatabase db) {

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}

}
