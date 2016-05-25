package com.hcpt.multirestaurant.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.activity.tabs.MainTabActivity;
import com.hcpt.multirestaurant.util.GPSTracker;
import com.hcpt.multirestaurant.util.NetworkUtil;

public class SplashActivity extends BaseActivity {

	private GPSTracker gps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		NetworkUtil.enableStrictMode();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onResume() {
		super.onResume();
		gps = new GPSTracker(self);
		checkBaseCondition();
	}



	private void checkBaseCondition() {
		if (NetworkUtil.checkNetworkAvailable(this)) {

			if (!gps.canGetLocation()) {
				gps.showSettingsAlert();
			} else {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						gotoActivity(MainTabActivity.class);
						finish();
					}
				}, 3000);
			}
		} else {
			showWifiSetting(this);
		}
	}

	public void showWifiSetting(final Context act) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);

		// Setting Dialog Title
		alertDialog.setTitle("Wifi is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("Wifi is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_SETTINGS);
						act.startActivity(intent);
						dialog.dismiss();
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

}
