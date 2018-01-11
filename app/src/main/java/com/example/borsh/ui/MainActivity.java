package com.example.borsh.ui;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "MyLogs MainActivity";
   private static final String TAG_FRAGMENT_RSS = "rssFragment";

   @Override protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(TAG, "onCreate: ");
      setContentView(R.layout.activity_main);
      if(savedInstanceState == null){
         Fragment rssFragment = new RssFragment();
         rssFragment.setRetainInstance(true);
         getSupportFragmentManager().beginTransaction()
             .add(R.id.frame_main_root, rssFragment, TAG_FRAGMENT_RSS)
             .commit();
      }
   }
}