package com.example.borsh.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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

import static com.example.borsh.database.DBContract.*;

public class RssFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

		private static final String TAG = "MyLogs RssFragment";

		public static final Uri CONTENT_URI = Uri.parse("content://com.example.borsh.rss.database/feed");

		private ImageView mNoDataInLocalDbImage;
		private SwipeRefreshLayout mSwipeToRefresh;
		private ListView mFeedListView;
		private MyRssAdapter mAdapter;

		@Nullable @Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.fragment_rss, container, false);
				mNoDataInLocalDbImage = rootView.findViewById(R.id.image_rss_no_data_in_local_db);
				mSwipeToRefresh = rootView.findViewById(R.id.swipe_rss);
				mSwipeToRefresh.setOnRefreshListener(this);
				mFeedListView = rootView.findViewById(R.id.listview_rss);

				String[] from = { POST_IMAGE_URI, POST_TITLE, POST_DESCRIPTION, POST_DATE, POST_HYPERLINK};
				int[] to = {R.id.image_item_image, R.id.text_item_title, R.id.text_item_description, R.id.text_item_date, R.id.text_item_hyperlink};
				mAdapter = new MyRssAdapter(getActivity(), R.layout.item_rss, null, from, to, 0);
				mFeedListView.setAdapter(mAdapter);

				getActivity().getSupportLoaderManager().initLoader(0, null, RssFragment.this);
				mFeedListView.setOnItemClickListener(this);

				Log.d(TAG, "onCreateView!!!!!: ");
				if(savedInstanceState != null){
						int firstVisiblePostIndex = savedInstanceState.getInt("firstVisibleIndex");
						Log.d(TAG, "OnCreateView: firstVisiblePostIndex = " + firstVisiblePostIndex);
						mFeedListView.smoothScrollToPosition(firstVisiblePostIndex);
				}
				return rootView;
		}


		@Override public void onSaveInstanceState(Bundle outState) {
				int firstVisiblePostIndex = mFeedListView.getFirstVisiblePosition();
				outState.putInt("firstVisibleIndex", firstVisiblePostIndex);
				super.onSaveInstanceState(outState);
		}

		@Override public void onResume() {
				Log.d(TAG, "onResume: ");
				//getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
				super.onResume();
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
						mSwipeToRefresh.setRefreshing(false);
						FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
						fragmentTransaction.addToBackStack(null);
						NoInternetDialogFragment dialogFragment = new NoInternetDialogFragment();
						dialogFragment.show(fragmentTransaction, "dialog");
				}else{
						queryRssApi();
						getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
				}
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
				mSwipeToRefresh.setRefreshing(false);
		}

		@Override public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
				Log.d(TAG, "onLoaderReset: ");
				if(mAdapter.isEmpty()){
						mNoDataInLocalDbImage.setVisibility(View.VISIBLE);
				}else{
						mNoDataInLocalDbImage.setVisibility(View.GONE);
				}
				mSwipeToRefresh.setRefreshing(false);
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
