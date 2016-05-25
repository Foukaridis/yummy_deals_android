package com.hcpt.multirestaurant.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.adapter.ListFoodHomeAdapter;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.network.ParserUtility;
import com.hcpt.multirestaurant.object.Menu;
import com.hcpt.multirestaurant.object.Offer;
import com.hcpt.multirestaurant.object.Shop;
import com.hcpt.multirestaurant.widget.TwoWayView;

import java.util.ArrayList;

public class OfferActivity extends BaseActivity implements OnClickListener {

    private TwoWayView lsvOffer;
    private ArrayList<Menu> arrFoods;
    private ListFoodHomeAdapter foodAdapter;
    private LinearLayout layoutOffers;
    private TextView lblBestDeal;
    private Shop shop;
    private Offer offer;
    private AQuery aq;
    private TextView lblShopName;
    private ImageView btnBack, imgOfferDetail;
    private ProgressBar progress;
    private int shopId = -1;
    private int offerId = -1;
    public static BaseActivity self;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        self = this;
        aq = new AQuery(self);
        setContentView(R.layout.activity_offer);
        initUI();
        initControl();
        initData();

    }

    private void initUI() {
        lsvOffer = (TwoWayView) findViewById(R.id.lsvOffers);
        lblShopName = (TextView) findViewById(R.id.lblShopName);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        imgOfferDetail = (ImageView) findViewById(R.id.imgOfferDetail);
        layoutOffers = (LinearLayout) findViewById(R.id.layoutOffers);
        progress = (ProgressBar) findViewById(R.id.progess);
        lblBestDeal = (TextView) findViewById(R.id.lblBestDealAround);

    }

    private void initControl() {
        layoutOffers.setVisibility(View.GONE);
        lsvOffer.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                Menu menu = arrFoods.get(index);
                Bundle b = new Bundle();
                b.putInt(GlobalValue.KEY_FOOD_ID, menu.getId());
                gotoActivity(self, FoodDetailActivity.class, b);
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

            if (b.containsKey(GlobalValue.KEY_OFFER_ID)) {
                offerId = b.getInt(GlobalValue.KEY_OFFER_ID);
            }
        }

        if (offerId != -1) {
            ModelManager.getOfferById(self, offerId, false,
                    new ModelManagerListener() {

                        @Override
                        public void onSuccess(Object object) {
                            // TODO Auto-generated method stub
                            String json = (String) object;
                            offer = ParserUtility.parseOffer(json);
                            setOfferImage(offer);
                        }

                        @Override
                        public void onError(VolleyError error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                        }
                    });

            ModelManager.getListFoodByPromotion(self, offerId, false,
                    new ModelManagerListener() {

                        @Override
                        public void onSuccess(Object object) {
                            // TODO Auto-generated method stub
                            String json = (String) object;

                            arrFoods = ParserUtility.parseListFood(json);
                            if (arrFoods.size() > 0) {
                                layoutOffers.setVisibility(View.VISIBLE);
                            } else {
                                layoutOffers.setVisibility(View.GONE);
                            }
                            lblBestDeal.setText("Menus of Offer : "
                                    + arrFoods.size());
                            foodAdapter = new ListFoodHomeAdapter(self,
                                    arrFoods);
                            lsvOffer.setAdapter(foodAdapter);
                        }

                        @Override
                        public void onError(VolleyError error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                        }
                    });

        }

        if (shopId != -1) {
            ModelManager.getShopById(self, shopId, false,
                    new ModelManagerListener() {

                        @Override
                        public void onSuccess(Object object) {

                            String json = (String) object;
                            shop = ParserUtility.parseShop(json);
                            if (shop != null) {
                                lblShopName.setText(shop.getShopName());
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });

        }

    }

    private void setOfferImage(Offer offer) {
        aq.id(imgOfferDetail)
                .progress(progress)
                .image(offer.getImage(), true, true, 0,
                        R.drawable.no_image_available_horizontal,
                        new BitmapAjaxCallback() {
                            @SuppressWarnings("deprecation")
                            @Override
                            public void callback(String url, ImageView iv,
                                                 Bitmap bm, AjaxStatus status) {

                                if (bm != null) {
                                    Drawable d = new BitmapDrawable(
                                            getResources(), bm);
                                    imgOfferDetail.setBackgroundDrawable(d);
                                } else {
                                    imgOfferDetail
                                            .setBackgroundResource(R.drawable.no_image_available_horizontal);
                                }
                            }
                        });
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
