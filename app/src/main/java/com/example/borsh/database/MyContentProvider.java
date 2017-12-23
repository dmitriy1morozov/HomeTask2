package com.example.borsh.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;

import static com.example.borsh.database.DBContract.*;

public class MyContentProvider extends ContentProvider {
		public static final String TAG = "MyLogs RssProvider";

		public static final String AUTHORITY = "com.example.borsh.rss.database";
    public static final String PATH = DB_TABLE;
		public static final Uri URI_CONTENT = Uri.parse(String.format("content://%s/%s", AUTHORITY, PATH));

		static final String CONTENT_TYPE_SINGLE_ROW = String.format(Locale.US,"%s.%s/%s.%s", "vnd", "android.cursor.item", AUTHORITY, PATH);
		static final String CONTENT_TYPE_MULTIPLE_ROWS = String.format(Locale.US,"%s.%s/%s.%s", "vnd", "android.cursor.dir", AUTHORITY, PATH);

		//UriMatcher constants
		public static final int URI_FEED_ID = 1;
		public static final int URI_POST_ID = 2;

		private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		{
				uriMatcher.addURI(AUTHORITY, PATH, URI_FEED_ID);
				uriMatcher.addURI(AUTHORITY, PATH + "/*", URI_POST_ID);
		}

		DBHelper mDbHelper;
		SQLiteDatabase mSqliteDatabase;

		public MyContentProvider() {
		}

		@Override public boolean onCreate() {
				Log.d(TAG, "onCreate: ");
				mDbHelper = new DBHelper(getContext());
				return true;
		}

		@Override public Uri insert(@NonNull Uri uri, ContentValues values) {
				if(uriMatcher.match(uri) != URI_FEED_ID){
						throw new IllegalArgumentException("Wrong URI: " + uri);
				}

				mSqliteDatabase = mDbHelper.getWritableDatabase();
				String title = values.getAsString(POST_TITLE);
				long timestamp = values.getAsLong(POST_TIMESTAMP);
				String date = values.getAsString(POST_DATE);
				String image = values.getAsString(POST_IMAGE_URI);
				String description = values.getAsString(POST_DESCRIPTION);
				String hyperlink = values.getAsString(POST_HYPERLINK);
				String rawQuery = String.format(Locale.US,
						"INSERT OR IGNORE INTO %s"
								+"(%s, %s, %s, %s, %s, %s, %s)"
								+ " VALUES "
								+ "(%s, '%s', %d, '%s', '%s', '%s', '%s')",
						DB_TABLE,
						POST_ID, POST_TITLE, POST_TIMESTAMP, POST_DATE, POST_IMAGE_URI, POST_DESCRIPTION, POST_HYPERLINK,
						"NULL", title, timestamp, date, image, description, hyperlink
				);
				mSqliteDatabase.execSQL(rawQuery);
				long totalRowCount = DatabaseUtils.queryNumEntries(mSqliteDatabase, DB_TABLE);
				Uri resultUri = ContentUris.withAppendedId(URI_CONTENT, totalRowCount);
				if(getContext() != null){
						getContext().getContentResolver().notifyChange(resultUri, null);
				}
				mSqliteDatabase.close();
				return resultUri;
		}

		@Override public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
				Log.d(TAG, "delete: ");
				switch (uriMatcher.match(uri)){
						case URI_FEED_ID:
								Log.d(TAG, "delete URI_FEED_ID");
								break;
						case URI_POST_ID:
								String id = uri.getLastPathSegment();
								Log.d(TAG, "delete URI_POST_ID = " + id);
								if(TextUtils.isEmpty(selection)){
										selection = String.format( "%s = %s" ,POST_ID, id);
								}else{
										selection = String.format("%s AND %s = %s", selection, POST_ID, id);
								}
								break;
						default:
								throw new IllegalArgumentException("Wrong URI: " + uri);
				}
				mSqliteDatabase = mDbHelper.getWritableDatabase();
				int cnt = mSqliteDatabase.delete(DB_TABLE, selection, selectionArgs);

				//if(getContext() != null){
				//		getContext().getContentResolver().notifyChange(uri, null);
				//}
				mSqliteDatabase.close();
				return cnt;
		}

		@Override public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
				Log.d(TAG, "update: ");
				return 0;
		}

		@Override public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
				String sortOrder) {
				switch (uriMatcher.match(uri)) {
						case URI_FEED_ID:
								Log.d(TAG, "query URI_FEED_ID");
								if (TextUtils.isEmpty(sortOrder)) {
										sortOrder = String.format(Locale.US,"%s %s", POST_TIMESTAMP , "DESC");
								}
								break;
						case URI_POST_ID:
								String id = uri.getLastPathSegment();
								Log.d(TAG, "query URI_POST_ID = " + id);
								if (TextUtils.isEmpty(selection)) {
										selection = String.format(Locale.US, "%s = %s", POST_ID, id);
								} else {
										selection = String.format(Locale.US, "%s AND %s = %s", selection, POST_ID, id);
								}
								break;
						default:
								throw new IllegalArgumentException("Wrong URI: " + uri);
				}
				mSqliteDatabase = mDbHelper.getWritableDatabase();
				Cursor cursor = mSqliteDatabase.query(DB_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
				if(getContext() != null){
						cursor.setNotificationUri(getContext().getContentResolver(), URI_CONTENT);
				}
				return cursor;
		}


		@Override public String getType(@NonNull Uri uri) {
				Log.d(TAG, "getType: ");
				switch (uriMatcher.match(uri)){
						case URI_FEED_ID:
								return CONTENT_TYPE_MULTIPLE_ROWS;
						case URI_POST_ID:
								return CONTENT_TYPE_SINGLE_ROW;
				}
				return null;
		}
}
