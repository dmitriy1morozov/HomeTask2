package com.example.borsh.hometask2;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.borsh.rss.ItemsItem;
import com.example.borsh.rss.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnApiListener{

		private static final String TAG = "MyLogs MainActivity";

		private static final String ATTRIBUTE_THUMB = "item_thumbnail";
		private static final String ATTRIBUTE_DESCRIPTION = "item_description";
		private static final String ATTRIBUTE_TITLE = "item_title";
		private static final String ATTRIBUTE_DATE = "item_date";
		private static final String ATTRIBUTE_HYPERLINK = "item_hyperlink";

		private ListView mFeedListView;
		private MyRssAdapter mAdapter;

		private ApiService.LocalBinder mBinder;
		private ServiceConnection mServiseConnection = new ServiceConnection() {
				@Override public void onServiceConnected(ComponentName name, IBinder service) {
						mBinder = (ApiService.LocalBinder)service;
						mBinder.setOnApiListener(MainActivity.this);
						mBinder.requestData();
				}

				@Override public void onServiceDisconnected(ComponentName name) {
						mBinder = null;
				}
		};

		@Override protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				mFeedListView = (ListView) findViewById(R.id.listview_main_rss);
		}


		private void updateAdapter(List<ItemsItem> posts) {
				//Input Data
				String[] titles = new String[posts.size()];
				Uri[] thumbs = new Uri[posts.size()];
				String[] descriptions = new String[posts.size()];
				String[] dates = new String[posts.size()];
				String[] hyperlinks = new String[posts.size()];
				for(int i = 0; i < posts.size(); i++){
						titles[i] = posts.get(i).getTitle();
						thumbs[i] = Uri.parse(posts.get(i).getEnclosure().getLink());
						descriptions[i] = posts.get(i).getDescription();
						dates[i] = posts.get(i).getPubDate();
						hyperlinks[i] = posts.get(i).getLink();
				}
				//Adapter data
				ArrayList<Map<String, Object>> data = new ArrayList<>(titles.length);
				Map<String, Object> map;
				for (int i = 0; i < titles.length; i++){
						map = new HashMap<>();
						map.put(ATTRIBUTE_THUMB, thumbs[i]);
						map.put(ATTRIBUTE_TITLE, titles[i]);
						map.put(ATTRIBUTE_DESCRIPTION, descriptions[i]);
						map.put(ATTRIBUTE_DATE, dates[i]);
						map.put(ATTRIBUTE_HYPERLINK, hyperlinks[i]);
						data.add(map);
				}

				String[] from = {ATTRIBUTE_THUMB, ATTRIBUTE_TITLE, ATTRIBUTE_DESCRIPTION, ATTRIBUTE_DATE, ATTRIBUTE_HYPERLINK};
				int[] to = {R.id.image_item_thumb, R.id.text_item_title, R.id.text_item_description, R.id.text_item_date, R.id.text_item_hyperlink};
				mAdapter = new MyRssAdapter(this, data, R.layout.item_rss, from, to);

				runOnUiThread(new Runnable() {
						@Override public void run() {
								mFeedListView.setAdapter(mAdapter);
								mFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
										@Override
										public void onItemClick(AdapterView<?> parent, View view, int position,
												long id) {
												TextView titleTextView = view.findViewById(R.id.text_item_title);
												String title = titleTextView.getText().toString();

												TextView descriptionTextView = view.findViewById(R.id.text_item_description);
												String description = descriptionTextView.getText().toString();

												TextView hyperlinkTextView = view.findViewById(R.id.text_item_hyperlink);
												final String hyperlinkText = hyperlinkTextView.getText().toString();
												final Uri hyperlink = Uri.parse(hyperlinkText);

												final AlertDialog.Builder builder = new AlertDialog.Builder(
														MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
												builder.setTitle(title);
												builder.setMessage(description);
												builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
														@Override public void onClick(DialogInterface dialog, int which) {
														}
												});
												builder.setPositiveButton("Go to site", new DialogInterface.OnClickListener() {
														@Override public void onClick(DialogInterface dialog, int which) {
																Intent intent = new Intent(Intent.ACTION_VIEW);
																intent.setData(hyperlink);
																startActivity(intent);
														}
												});
												AlertDialog dialog = builder.create();
												dialog.show();
										}
								});
						}
				});
		}

		//----------------------------------------------------------------------------------------------
		@Override public boolean onCreateOptionsMenu(Menu menu) {
				getMenuInflater().inflate(R.menu.mymenu, menu);
				return super.onCreateOptionsMenu(menu);
		}

		@Override public boolean onOptionsItemSelected(MenuItem item) {
				if(item.getItemId() == R.id.btn_mymenu_sync){
						Intent intent = new Intent(MainActivity.this, ApiService.class);
						bindService(intent, mServiseConnection, Service.BIND_AUTO_CREATE);
						Log.d(TAG, "onOptionsItemSelected: " + "bindRequested");
				}
				return super.onOptionsItemSelected(item);
		}

		//----------------------------------------------------------------------------------------------
		@Override public void onApiReplied(final Response rss) {
				List<ItemsItem> posts = rss.getItems();
				updateAdapter(posts);
				unbindService(mServiseConnection);
		}
}
