package com.example.borsh.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.util.Locale;

import static com.example.borsh.database.DBContract.*;

public class MyContentProvider extends ContentProvider {
		private static final String TAG = "MyLogs RssProvider";

		private static final String AUTHORITY = "com.example.borsh.database";
    private static final String PATH = DB_TABLE;
		public static final Uri URI_CONTENT = Uri.parse(String.format("content://%s/%s", AUTHORITY, PATH));

		private static final String CONTENT_TYPE_SINGLE_ROW = String.format(Locale.US,"%s.%s/%s.%s", "vnd", "android.cursor.item", AUTHORITY, PATH);
		private static final String CONTENT_TYPE_MULTIPLE_ROWS = String.format(Locale.US,"%s.%s/%s.%s", "vnd", "android.cursor.dir", AUTHORITY, PATH);

		//UriMatcher constants
		private static final int URI_FEED_ID = 1;
		private static final int URI_POST_ID = 2;

		private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		{
				uriMatcher.addURI(AUTHORITY, PATH, URI_FEED_ID);
				uriMatcher.addURI(AUTHORITY, PATH + "/*", URI_POST_ID);
		}

		private DBHelper mDbHelper;
		private SQLiteDatabase mSqliteDatabase;

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
				long rowId = mSqliteDatabase.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
				Uri resultUri = ContentUris.withAppendedId(URI_CONTENT, rowId);

				if(getContext() != null){
						getContext().getContentResolver().notifyChange(resultUri, null);
				}
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
										selection = String.format( "%s = %s" , BaseColumns._ID, id);
								}else{
										selection = String.format("%s AND %s = %s", selection, BaseColumns._ID, id);
								}
								break;
						default:
								throw new IllegalArgumentException("Wrong URI: " + uri);
				}
				mSqliteDatabase = mDbHelper.getWritableDatabase();
				int cnt = mSqliteDatabase.delete(DB_TABLE, selection, selectionArgs);

				if(getContext() != null){
						getContext().getContentResolver().notifyChange(uri, null);
				}
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
										selection = String.format(Locale.US, "%s = %s", BaseColumns._ID, id);
								} else {
										selection = String.format(Locale.US, "%s AND %s = %s", selection, BaseColumns._ID, id);
								}
								break;
						default:
								throw new IllegalArgumentException("Wrong URI: " + uri);
				}
				mSqliteDatabase = mDbHelper.getWritableDatabase();
				Cursor cursor = mSqliteDatabase.query(DB_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
				if(getContext() != null){
						cursor.setNotificationUri(getContext().getContentResolver(), uri);
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
