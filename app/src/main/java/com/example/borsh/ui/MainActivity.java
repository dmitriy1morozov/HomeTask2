package com.example.borsh.ui;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "MyLogs MainActivity";

   private Fragment mRssFragment = new RssFragment();

   @Override protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(TAG, "onCreate: ");
      setContentView(R.layout.activity_main);
      mRssFragment.setRetainInstance(true);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.frame_main_root, mRssFragment, "rssFragment")
          .commit();
   }
}