package com.example.borsh.ui;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

class MyRssAdapter extends SimpleCursorAdapter {
   private RequestOptions mOptions;

   MyRssAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
      super(context, layout, cursor, from, to, flags);
      mOptions = new RequestOptions();
      mOptions = mOptions.centerCrop();
   }

   @Override public void setViewImage(ImageView imageView, String uri) {
      Log.d("MyLogs MyRssAdapter", "setViewImage: ");

      Glide.with(imageView)
          .load(uri)
          .apply(mOptions)
          .thumbnail(0.1f)
          .into(imageView);
   }
}