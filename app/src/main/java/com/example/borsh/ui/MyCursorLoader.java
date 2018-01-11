package com.example.borsh.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import static com.example.borsh.database.MyContentProvider.URI_CONTENT;

class MyCursorLoader extends CursorLoader {
		private static final String TAG = "MyLogs MyCursorLoader";

		MyCursorLoader(Context context) {
				super(context);
		}

		@Override
		public Cursor loadInBackground() {
				Log.d(TAG, "loadInBackground: start loading from local DB");
				Cursor cursor = getContext().getContentResolver().query(
						URI_CONTENT, null, null, null,null);
				Log.d(TAG, "loadInBackground: finish loading from local DB");
				return cursor;
		}
}
