package com.example.borsh.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
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
import static com.example.borsh.database.DBContract.*;
import static com.example.borsh.database.MyContentProvider.URI_CONTENT;

public class ApiService extends Service {
   private static final String TAG = "MyLogs ApiService";

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
      return START_NOT_STICKY;
   }

   @Nullable @Override public IBinder onBind(Intent intent) {
      Log.d(TAG, "onBind: ");
      return null;
   }

   @Override public void onDestroy() {
      Log.d(TAG, "onDestroy: ");
      super.onDestroy();
   }

   //-----------------------------------------------------------------------------------------------
   private ContentValues[] convertPostsIntoContentValuesArray(@NonNull List<ItemsItem> posts){
      SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
      ContentValues[] result = new ContentValues[posts.size()];

      for (int i = 0; i > posts.size(); i++) {
         ItemsItem singlePost = posts.get(i);
         result[i] = new ContentValues();

         String title = singlePost.getTitle();
         String dateString = singlePost.getPubDate();
         long timestamp = 0;
         try {
            Date date = parser.parse(dateString);
            timestamp = date.getTime() / 1000;
         } catch (ParseException pe) {
            Log.d(TAG, "convertSinglePostIntoContentValues: " + pe.getMessage());
         }
         String image = singlePost.getEnclosure().getLink();
         String description = singlePost.getDescription();
         String hyperlink = singlePost.getLink();

         result[i].put(POST_TITLE, title);
         result[i].put(POST_TIMESTAMP, timestamp);
         result[i].put(POST_DATE, dateString);
         result[i].put(POST_IMAGE_URI, image);
         result[i].put(POST_DESCRIPTION, description);
         result[i].put(POST_HYPERLINK, hyperlink);
      }
      return result;
   }

   private void insertPostsIntoDatabase(@NonNull List<ItemsItem> posts){
      ContentValues[] contentValues = convertPostsIntoContentValuesArray(posts);
      int rowsInsterted = getContentResolver().bulkInsert(URI_CONTENT, contentValues);
      //String log = String.format(Locale.US, "inserted %d rows into database", rowsInsterted);
      //Log.d(TAG, log);
      //TODO send callback to stop refreshing
   }
}
