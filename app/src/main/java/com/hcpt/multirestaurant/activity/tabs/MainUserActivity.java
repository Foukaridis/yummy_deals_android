package com.hcpt.multirestaurant.activity.tabs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;

import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.activity.tabs.user.LoginFragment;
import com.hcpt.multirestaurant.config.GlobalValue;

public class MainUserActivity extends FragmentActivity {

	public static final int LOGIN_PAGE = 0;
	public static final int INFO_PAGE = 1;
	public static final int REGISTER_PAGE = 2;
	public static final int MYACC_PAGE = 3;
	public static final int HISTORY_PAGE = 4;
	public static final int FEEDBACK_PAGE = 5;
	public static final int UPDATEINFOR_PAGE = 6;
	public static final int UPDATEPASS_PAGE = 7;
	public static final int DETAILHISTORY_PAGE = 8;
	public Fragment[] listFragments;
	private FragmentManager fm;
	public int curFragment;
	public int preFragment;

	private boolean isLoadNew = true;

	public boolean isLoadNew() {
		return isLoadNew;
	}

	public void setLoadNew(boolean isLoadNew) {
		this.isLoadNew = isLoadNew;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Set view
		setContentView(R.layout.activity_main_tab_user);
		initUI();
		initControl();
	}
	

	private void initUI() {
		fm = getSupportFragmentManager();
		listFragments = new Fragment[9];
		listFragments[LOGIN_PAGE] = fm.findFragmentById(R.id.fragmentLogin);
		listFragments[REGISTER_PAGE] = fm
				.findFragmentById(R.id.fragmentRegister);
		listFragments[INFO_PAGE] = fm.findFragmentById(R.id.fragmentInfo);
		listFragments[MYACC_PAGE] = fm.findFragmentById(R.id.fragmentMyAcc);
		listFragments[FEEDBACK_PAGE] = fm
				.findFragmentById(R.id.fragmentFeedBack);
		listFragments[UPDATEINFOR_PAGE] = fm
				.findFragmentById(R.id.fragmentUpdateInfor);
		listFragments[UPDATEPASS_PAGE] = fm
				.findFragmentById(R.id.fragmenUpdatePass);
		listFragments[HISTORY_PAGE] = fm.findFragmentById(R.id.fragmentHistory);
		listFragments[DETAILHISTORY_PAGE] = fm
				.findFragmentById(R.id.fragmentDetailHistory);
	}

	private void initControl() {
		refreshContent();
	}

	public void refreshContent() {
		if (GlobalValue.myAccount == null) {
			LoginFragment fm = (LoginFragment) listFragments[LOGIN_PAGE];
			fm.refreshContent();
			showFragment(LOGIN_PAGE);
		} else {
			showFragment(MYACC_PAGE);
		}
	}

	public void showFragment(int fragmentIndex) {
		preFragment = curFragment;
		curFragment = fragmentIndex;
		FragmentTransaction transaction = fm.beginTransaction();

		for (int i = 0; i < listFragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(listFragments[i]);
			} else {
				transaction.hide(listFragments[i]);
			}
		}

		transaction.commit();
	}

	public void gotoFragment(int fragment) {
		preFragment = curFragment;
		curFragment = fragment;
		Log.e("huy-log", "current-fragment 1:" + curFragment);
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.setCustomAnimations(R.anim.push_left_in,
				R.anim.push_left_out);
		for (Fragment item : listFragments) {
			transaction.hide(item);
		}
		transaction.show(listFragments[fragment]);
		transaction.commit();
	}

	public void backFragment(int fragment) {
		preFragment = curFragment;
		curFragment = fragment;
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.setCustomAnimations(R.anim.push_right_in,
				R.anim.push_right_out);
		transaction.hide(listFragments[preFragment]);
		transaction.show(listFragments[fragment]);
		transaction.commit();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (curFragment != MYACC_PAGE && curFragment != LOGIN_PAGE) {
			Log.e("huy-log", "current-fragment 2:" + curFragment);
			if (curFragment == INFO_PAGE) {
				backFragment(MainUserActivity.MYACC_PAGE);
			} else {
				backFragment(preFragment);
			}

		} else {

			if (curFragment == LOGIN_PAGE) {
				this.getParent().onBackPressed();
			} else {
				// showLogoutConfirmDialog();
			}

		}

	}

	public void showLogoutConfirmDialog() {

		AlertDialog.Builder build = new Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog)).setTitle("Log out")
				.setMessage("Do you want to log out?")
				.setNegativeButton("Yes", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (GlobalValue.myAccount != null)
							GlobalValue.myAccount = null;
						refreshContent();
					}
				}).setPositiveButton("No", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		build.show();
	}
}
