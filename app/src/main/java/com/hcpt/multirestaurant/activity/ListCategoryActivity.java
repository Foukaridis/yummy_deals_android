package com.hcpt.multirestaurant.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.adapter.ListCategoryAdapter;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.network.ParserUtility;
import com.hcpt.multirestaurant.object.Category;
import com.hcpt.multirestaurant.util.NetworkUtil;

public class ListCategoryActivity extends BaseActivity implements
        OnClickListener {

    private TextView lblShopName;
    private ImageView btnBack;
    private ArrayList<Category> arrCategories = new ArrayList<Category>();
    private ListView lsvCategory;
    private ListCategoryAdapter categoryAdapter;
    private int shopId = -1;
    private String shopName = "";
    public static BaseActivity self;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category);
        self = this;
        initUI();
        initControl();
        initData();

    }

    private void initUI() {
        lsvCategory = (ListView) findViewById(R.id.lsvCategory);
        lblShopName = (TextView) findViewById(R.id.lblShopName);
        btnBack = (ImageView) findViewById(R.id.btnBack);
    }

    private void initControl() {
        categoryAdapter = new ListCategoryAdapter(self, arrCategories);
        lsvCategory.setAdapter(categoryAdapter);
        lsvCategory.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {

                if (NetworkUtil.checkNetworkAvailable(self)) {
                    Category category = arrCategories.get(index);
                    Bundle b = new Bundle();
                    b.putInt(GlobalValue.KEY_SHOP_ID, shopId);
                    b.putString(GlobalValue.KEY_SHOP_NAME, shopName);
                    b.putInt(GlobalValue.KEY_CATEGORY_ID, category.getId());
                    b.putString(GlobalValue.KEY_CATEGORY_NAME,
                            category.getName());

                    gotoActivity(self, ListFoodActivity.class, b);
                } else {
                    Toast.makeText(self,
                            R.string.message_network_is_unavailable,
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        btnBack.setOnClickListener(this);
        lblShopName.setOnClickListener(this);
    }

    private void initData() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(GlobalValue.KEY_SHOP_ID)) {
                shopId = b.getInt(GlobalValue.KEY_SHOP_ID);
            }

            if (b.containsKey(GlobalValue.KEY_SHOP_NAME)) {
                shopName = b.getString(GlobalValue.KEY_SHOP_NAME);
                lblShopName.setText(shopName);
            }
        }

        if (shopId != -1) {
            if (!NetworkUtil.checkNetworkAvailable(this)) {
                Toast.makeText(this, R.string.message_network_is_unavailable, Toast.LENGTH_LONG).show();
            } else
                ModelManager.getListCategoryByShop(self, shopId, true,
                        new ModelManagerListener() {

                            @Override
                            public void onSuccess(Object object) {
                                // TODO Auto-generated method stub
                                String json = (String) object;
                                if(ParserUtility
                                        .parseListCategories(json).size() == 0){
                                    showToastMessage(getResources().getString(R.string.have_no_date));
                                }else{
                                    arrCategories.clear();
                                    arrCategories.addAll(ParserUtility
                                            .parseListCategories(json));
                                    categoryAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                // TODO Auto-generated method stub
                                arrCategories.clear();
                                categoryAdapter.notifyDataSetChanged();
                                Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                            }
                        });
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnBack) {
            onBackPressed();
        } else if (v == lblShopName) {
            gotoShopDetail(shopId);
        }
    }
}
