package com.example.borsh.ui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.borsh.service.ApiService;

import static com.example.borsh.database.DBContract.*;
import static com.example.borsh.database.MyContentProvider.URI_CONTENT;

public class RssFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

		private static final String TAG = "MyLogs RssFragment";

		private ImageView mNoDataInLocalDbImage;
		private SwipeRefreshLayout mSwipeToRefresh;
		private ListView mFeedListView;
		private MyRssAdapter mAdapter;
		private ContentObserver mContentObserver;

		private int mFirstVisibleIndex = 0;

		@Nullable @Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.fragment_rss, container, false);
				mNoDataInLocalDbImage = rootView.findViewById(R.id.image_rss_no_data_in_local_db);
				mSwipeToRefresh = rootView.findViewById(R.id.swipe_rss);
				mSwipeToRefresh.setOnRefreshListener(this);

				String[] from = { POST_IMAGE_URI, POST_TITLE, POST_DESCRIPTION, POST_DATE, POST_HYPERLINK };
				int[] to = {
						R.id.image_item_image, R.id.text_item_title, R.id.text_item_description,
						R.id.text_item_date, R.id.text_item_hyperlink
				};
				mAdapter = new MyRssAdapter(getActivity(), R.layout.item_rss, null, from, to, 0);
				mFeedListView = rootView.findViewById(R.id.listview_rss);
				mFeedListView.setAdapter(mAdapter);
				mFeedListView.setOnItemClickListener(this);
				if(savedInstanceState != null){
						mFirstVisibleIndex = savedInstanceState.getInt("firstVisibleIndex");
				}
				getActivity().getSupportLoaderManager().initLoader(0, null, RssFragment.this);
				return rootView;
		}


		@Override public void onSaveInstanceState(Bundle outState) {
				int firstVisiblePostIndex = mFeedListView.getFirstVisiblePosition();
				outState.putInt("firstVisibleIndex", firstVisiblePostIndex);
				super.onSaveInstanceState(outState);
		}

		@Override public void onStart() {
				super.onStart();
				ContentResolver contentResolver = getContext().getContentResolver();
				mContentObserver = new ContentObserver(new Handler()) {
						@Override public boolean deliverSelfNotifications() {
								Log.d(TAG, "deliverSelfNotifications: ");
								return super.deliverSelfNotifications();
						}

						@Override public void onChange(boolean selfChange) {
								super.onChange(selfChange);
								Log.d(TAG, "onChange1 = : " + selfChange);
								getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
						}

						@Override public void onChange(boolean selfChange, Uri uri) {
								super.onChange(selfChange, uri);
								Log.d(TAG, "onChange2: = " + selfChange);
						}
				};
				contentResolver.registerContentObserver(URI_CONTENT, true, mContentObserver);
		}

		@Override public void onResume() {
				super.onResume();
				mFeedListView.smoothScrollToPositionFromTop(mFirstVisibleIndex,0);
		}

		@Override public void onStop() {
				super.onStop();
				getContext().getContentResolver().unregisterContentObserver(mContentObserver);
		}

		@Override public void onDestroy() {
				super.onDestroy();
				mSwipeToRefresh.setOnRefreshListener(null);
				mFeedListView.setOnItemClickListener(null);
		}

		private void queryRssApi() {
				Intent intent = new Intent(getActivity(), ApiService.class);
				getActivity().startService(intent);
		}

		private boolean isInternetConnected() {
				boolean result;
				ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				if (connectivityManager != null) {
						NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
						result = (activeNetwork != null && activeNetwork.isConnected());
				} else {
						result = false;
				}
				return result;
		}
		//==============================================================================================
		@Override public void onRefresh() {
				Log.d(TAG, "onRefresh: ");
				if(!isInternetConnected()){
						FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
						fragmentTransaction.addToBackStack(null);
						NoInternetDialogFragment dialogFragment = new NoInternetDialogFragment();
						dialogFragment.show(fragmentTransaction, "dialog");
				}else{
						queryRssApi();
				}
				mSwipeToRefresh.setRefreshing(false);
		}

		@Override public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
				return new MyCursorLoader(getActivity());
		}

		@Override
		public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
				Log.d(TAG, "onLoadFinished: ");
				mAdapter.swapCursor(data);
				if(mAdapter.isEmpty()){
						mNoDataInLocalDbImage.setVisibility(View.VISIBLE);
				}else{
						mNoDataInLocalDbImage.setVisibility(View.GONE);
				}
		}

		@Override public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
				Log.d(TAG, "onLoaderReset: ");
				if(mAdapter.isEmpty()){
						mNoDataInLocalDbImage.setVisibility(View.VISIBLE);
				}else{
						mNoDataInLocalDbImage.setVisibility(View.GONE);
				}
		}

		@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) mAdapter.getItem(position);
				int titleColumnIndex = cursor.getColumnIndex(POST_TITLE);
				int descriptionColumnIndex = cursor.getColumnIndex(POST_DESCRIPTION);
				int hyperlinkColumnIndex = cursor.getColumnIndex(POST_HYPERLINK);
				String title = cursor.getString(titleColumnIndex);
				String description = cursor.getString(descriptionColumnIndex);
				String hyperlinkString = cursor.getString(hyperlinkColumnIndex);
				final Uri hyperlink = Uri.parse(hyperlinkString);

				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
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
}