package com.example.borsh.hometask2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.lang.ref.WeakReference;

public class ApiService extends Service {
		private static final String TAG = "MyLogs ApiService";

		private WeakReference<OnApiListener> mOnApiListenerRef;
		private Thread mApiRequestThread;

		public ApiService() {
		}

		@Override public IBinder onBind(Intent intent) {
				Log.d(TAG, "onBind: ");
				return new LocalBinder();
		}

		@Override public void onDestroy() {
				Log.d(TAG, "onDestroy: ");
				super.onDestroy();
				if(mApiRequestThread != null){
						mApiRequestThread.interrupt();
						mApiRequestThread = null;
				}
		}

		//----------------------------------------------------------------------------------------------
		public class LocalBinder extends Binder {
				public void requestData(){
						if(mOnApiListenerRef != null && mOnApiListenerRef.get() != null){
								mApiRequestThread = new ApiThread(mOnApiListenerRef.get());
								mApiRequestThread.start();
						}
				}

				public void setOnApiListener(OnApiListener onApiListener){
						mOnApiListenerRef = new WeakReference<>(onApiListener);
				}
		}
}
