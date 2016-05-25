package com.hcpt.multirestaurant.activity.tabs;

import java.util.ArrayList;

import android.app.Activity;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.object.Shop;
import com.hcpt.multirestaurant.util.DialogUtility;
import com.hcpt.multirestaurant.util.MySharedPreferences;


@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {
    /**
     * Called when the activity is first created.
     */
    TabHost tabHost = null;
    private Activity self;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);
        self = this;
        // restart app when catching crash issues.
        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        // init tabhost
        initTabPages();
        // Set value for shop cart
        if (GlobalValue.arrMyMenuShop == null) {
            GlobalValue.arrMyMenuShop = new ArrayList<Shop>();
        }
    }

    private void initTabPages() {

        tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec(GlobalValue.KEY_TAB_HOME)
                .setIndicator(createTabIndicator(R.drawable.ic_home_selected))
                .setContent(new Intent(this, HomeActivity.class)));

        tabHost.addTab(tabHost
                .newTabSpec(GlobalValue.KEY_TAB_SEARCH)
                .setIndicator(
                        createTabIndicator(R.drawable.ic_search_unselected))
                .setContent(new Intent(this, SearchActivity.class)));

        tabHost.addTab(tabHost
                .newTabSpec(GlobalValue.KEY_TAB_MY_MENU)
                .setIndicator(
                        createTabIndicator(R.drawable.ic_my_menu_unselected))
                .setContent(new Intent(this, MainCartActivity.class)));

        tabHost.addTab(tabHost
                .newTabSpec(GlobalValue.KEY_TAB_SETTING)
                .setIndicator(createTabIndicator(R.drawable.ic_user_non_select))
                .setContent(new Intent(this, MainUserActivity.class)));

//        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
//            tabHost.getTabWidget().getChildAt(i)
//                    .setBackgroundColor(getResources().getColor(R.color.primary_light)); // unselected
//        }
//        tabHost.getTabWidget().getChildAt(0)
//                .setBackgroundColor(getResources().getColor(R.color.primary_dark)); // selected
//
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                updateIconSelected();
                if (tabId.equals(GlobalValue.KEY_TAB_MY_MENU)) {
                    MainCartActivity activity = (MainCartActivity) getLocalActivityManager()
                            .getActivity(GlobalValue.KEY_TAB_MY_MENU);
                    activity.refreshContent();

                } else if (tabId.equals(GlobalValue.KEY_TAB_HOME)) {
                    // HomeActivity activity = (HomeActivity)
                    // getLocalActivityManager()
                    // .getActivity(GlobalValue.KEY_TAB_HOME);
                    // activity.refreshContent();
                } else if (tabId.equals(GlobalValue.KEY_TAB_SETTING)) {
                    MainUserActivity activity = (MainUserActivity) getLocalActivityManager()
                            .getActivity(GlobalValue.KEY_TAB_SETTING);
                    activity.refreshContent();
                }
//                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
//                    tabHost.getTabWidget().getChildAt(i)
//                            .setBackgroundColor(getResources().getColor(R.color.primary_light)); // unselected
//                }
//                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
//                        .setBackgroundColor(getResources().getColor(R.color.primary_dark)); // selected
            }
        });

        tabHost.setCurrentTab(0);
        updateIconSelected();
    }

    private void updateIconSelected() {
        TabWidget tabwidget = tabHost.getTabWidget();
        if (tabHost.getCurrentTab() == 0) {
            tabwidget.getChildTabViewAt(0).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_home_selected);
            tabwidget.getChildTabViewAt(1).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_search_unselected);
            tabwidget.getChildTabViewAt(2).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_my_menu_unselected);
            tabwidget.getChildTabViewAt(3).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_user_non_select);

        } else if (tabHost.getCurrentTab() == 1) {
            tabwidget.getChildTabViewAt(0).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_home_unselected);
            tabwidget.getChildTabViewAt(1).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_search_selected);
            tabwidget.getChildTabViewAt(2).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_my_menu_unselected);
            tabwidget.getChildTabViewAt(3).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_user_non_select);
        } else if (tabHost.getCurrentTab() == 2) {
            tabwidget.getChildTabViewAt(0).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_home_unselected);
            tabwidget.getChildTabViewAt(1).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_search_unselected);
            tabwidget.getChildTabViewAt(2).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_my_menu_selected);
            tabwidget.getChildTabViewAt(3).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_user_non_select);
        } else if (tabHost.getCurrentTab() == 3) {
            tabwidget.getChildTabViewAt(0).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_home_unselected);
            tabwidget.getChildTabViewAt(1).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_search_unselected);
            tabwidget.getChildTabViewAt(2).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_my_menu_unselected);
            tabwidget.getChildTabViewAt(3).findViewById(R.id.imgIcon)
                    .setBackgroundResource(R.drawable.ic_user_select);
        }
    }

    private View createTabIndicator(int resource) {
        View tabIndicator = getLayoutInflater()
                .inflate(R.layout.view_tab, null);
        ImageView image = (ImageView) tabIndicator.findViewById(R.id.imgIcon);
        image.setBackgroundResource(resource);
        return tabIndicator;
    }

    public void gotoActivity(Class<?> cla) {
        Intent intent = new Intent(this, cla);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left2);
    }

    public void gotoActivity(Class<?> cla, Bundle bundle) {
        Intent intent = new Intent(this, cla);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        showQuitDialog();
    }

    private void showQuitDialog() {

        DialogUtility.showYesNoDialog(self, R.string.message_quit_app, R.string.yes, R.string.no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (GlobalValue.myAccount != null) {
                    GlobalValue.myAccount = null;
                }
                new MySharedPreferences(self).setCacheUserInfo("");
                finish();
            }
        });
    }
}