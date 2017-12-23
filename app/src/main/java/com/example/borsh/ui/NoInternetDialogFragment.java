package com.example.borsh.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NoInternetDialogFragment extends DialogFragment implements View.OnClickListener {
		private static final String TAG = "MyLogs NoInternet";

		@Override public void onCreate(@Nullable Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
		}

		@Nullable @Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
				View rootView =  inflater.inflate(R.layout.fragment_no_internet, container, false);
				rootView.findViewById(R.id.btn_wifi_enable).setOnClickListener(this);
				rootView.findViewById(R.id.btn_mobiledata_enable).setOnClickListener(this);
				return rootView;
		}

		@Override public void onClick(View v) {
				switch(v.getId()){
						case R.id.btn_wifi_enable:
								startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
								break;
						case R.id.btn_mobiledata_enable:
								Intent intent = new Intent();
								intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
								startActivity(intent);
								break;
				}
		}
}
