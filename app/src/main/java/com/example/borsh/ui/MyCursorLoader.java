package com.example.borsh.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import java.lang.ref.WeakReference;

class MyCursorLoader extends CursorLoader {
		private static final String TAG = "MyLogs MyCursorLoader";
		private WeakReference<Context> mContextRef;

		MyCursorLoader(Context context) {
				super(context);
				mContextRef = new WeakReference<>(context);
		}

		@Override
		public Cursor loadInBackground() {
				Log.d(TAG, "loadInBackground: start loading from local DB");
				Cursor cursor = null;
				if(mContextRef != null && mContextRef.get() != null){
						cursor = mContextRef.get().getContentResolver().query(
								RssFragment.CONTENT_URI, null, null, null,null);
				}
				Log.d(TAG, "loadInBackground: finish loading from local DB");
				return cursor;
		}
}
