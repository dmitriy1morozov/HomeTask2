package com.example.borsh.hometask2;

import android.util.Log;
import com.example.borsh.rss.Response;
import java.io.IOException;
import java.lang.ref.WeakReference;
import retrofit2.Call;

public class ApiThread extends Thread {
		private static final String TAG = "MyLogs ApiThread";

		private static final String RSS_URL  = "https//www.nasa.gov%2Frss%2Fdyn%2Flg_image_of_the_day.rss";
		private WeakReference<OnApiListener> mOnApiListener;

		public ApiThread(OnApiListener onApiListener) {
				this.mOnApiListener = new WeakReference<>(onApiListener);
		}

		@Override public void run() {
				try {
						Thread.currentThread().sleep(2000);
				} catch (InterruptedException e) {
						e.printStackTrace();
				}

				if(mOnApiListener != null && mOnApiListener.get() != null){
						//TODO Send RSS object from API
						ApiRss apiRss = ApiRss.retrofit.create(ApiRss.class);
						final Call<Response> call = apiRss.getRssFeed();
						try {
								Log.d(TAG, "run: startRetrofit execute");
								retrofit2.Response<Response> response = call.execute();
								Log.d(TAG, "run: finished Retrofit execute");
								//TODO send data to listener
								mOnApiListener.get().onApiReplied(response.body());
						} catch (IOException e) {
								e.printStackTrace();
						}
				}
		}
}
