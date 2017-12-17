package com.example.borsh.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.example.borsh.database.DBContract.*;

class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
				super(context, DB_NAME, null, DB_VERSION);
		}

		@Override public void onCreate(SQLiteDatabase db) {
				db.execSQL(DB_QUERY_CREATE_TABLE);
		}

		@Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
}
