package com.example.borsh.ui;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.borsh.rssPOJO.ItemsItem;
import com.example.borsh.rssPOJO.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;

public class ApiService extends Service {
   private static final String TAG = "MyLogs ApiService";

   private static final Uri CONTENT_URI = Uri.parse("content://com.example.borsh.rss.database/feed");

   public ApiService() {
   }

   @Override public int onStartCommand(Intent intent, int flags, int startId) {

      ApiRss apiRss = ApiRss.retrofit.create(ApiRss.class);
      Call<Response> call = apiRss.getRssFeed();
      Log.d(TAG, "run: startRetrofit execute");
      call.enqueue(new Callback<Response>() {
         @Override
         public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
            String status = response.body().getStatus();
            if(status.equals("error")){
               Log.d(TAG, "onResponse: ERROR = Daily rate limit exceeded, please try again later or use an api key.");
            } else if (status.equals("ok")) {
               List<ItemsItem> posts = response.body().getItems();
               insertPostsIntoDatabase(posts);
               Log.d(TAG, "run: finished Retrofit execute");
            }
         }

         @Override public void onFailure(Call<Response> call, Throwable t) {
            Log.d(TAG, "Retrofit onFailure: " + t.getMessage());
         }
      });
      return super.onStartCommand(intent, flags, startId);
   }

   @Nullable @Override public IBinder onBind(Intent intent) {
      Log.d(TAG, "onBind: ");
      return null;
   }

   @Override public void onDestroy() {
      Log.d(TAG, "onDestroy: ");
      super.onDestroy();
   }

   //----------------------------------------------------------------------------------------------
   private void insertPostsIntoDatabase(@NonNull List<ItemsItem> posts){
      ContentValues contentValues = new ContentValues();
      SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
      for (int i = 0; i < posts.size(); i++) {
         ItemsItem singlePost = posts.get(i);
         String title = singlePost.getTitle();
         title = title.replace("'", "''");
         String dateString = singlePost.getPubDate();
         long timestamp = 0;
         try {
            Date date = parser.parse(dateString);
            timestamp = date.getTime() / 1000;
         } catch (ParseException pe) {
            Log.d(TAG, "onApiReplied: " + pe.getMessage());
         }
         String imageUri = singlePost.getEnclosure().getLink();
         imageUri = imageUri.replace("'", "''");
         String description = singlePost.getDescription();
         description = description.replace("'", "''");
         String hyperlink = singlePost.getLink();
         hyperlink = hyperlink.replace("'", "''");

         contentValues.put("title", title);
         contentValues.put("timestamp", timestamp);
         contentValues.put("imageUri", imageUri);
         contentValues.put("description", description);
         contentValues.put("hyperlink", hyperlink);
         Uri newUri = getContentResolver().insert(CONTENT_URI, contentValues);
         //Log.d(TAG, "insert, result Uri : " + String.valueOf(newUri));
      }
   }
}
