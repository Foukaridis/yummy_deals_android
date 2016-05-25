package com.hcpt.multirestaurant.activity.tabs;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.activity.FoodDetailActivity;
import com.hcpt.multirestaurant.activity.ShopDetailActivity;
import com.hcpt.multirestaurant.adapter.ListFoodHomeAdapter;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.network.ParserUtility;
import com.hcpt.multirestaurant.object.Menu;
import com.hcpt.multirestaurant.object.Shop;
import com.hcpt.multirestaurant.util.GPSTracker;
import com.hcpt.multirestaurant.util.ImageUtil;
import com.hcpt.multirestaurant.util.Logger;
import com.hcpt.multirestaurant.util.NetworkUtil;
import com.hcpt.multirestaurant.widget.TwoWayView;

@SuppressLint("NewApi")
public class HomeActivity extends BaseActivity implements LocationListener {

    private GoogleMap googleMap;
    private TextView lblBestDeal, lblBestShop;
    private Marker currentMaker;
    private HashMap<String, Shop> markerRestaurantMap = new HashMap<String, Shop>();
    private AQuery aq;
    private ArrayList<Shop> arrShop = new ArrayList<Shop>();
    private ArrayList<Menu> arrFood;
    private TwoWayView lsvOffer;
    private Activity self;
    private ListFoodHomeAdapter foodAdapter;
    private LinearLayout layoutOffers;
    private GPSTracker gps;
    Handler handler;

    private boolean isShowFakeMessage = true;

    public static final String HOME_SCREEN = "homeActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_home);
        aq = new AQuery(this);
        gps = new GPSTracker(self);
        handler = new Handler();
        initUI();
        initControl();
        initGoogleMap();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (GlobalValue.glatlng == null) {
            getDefaultLocation();
        } else {
            refreshContent();
        }
    }

    private void initUI() {
        lsvOffer = (TwoWayView) findViewById(R.id.lsvOffers);
        lblBestDeal = (TextView) findViewById(R.id.lblBestDealAround);
        lblBestShop = (TextView) findViewById(R.id.lblBestShopAround);
        layoutOffers = (LinearLayout) findViewById(R.id.layoutOffers);
    }

    private void initControl() {
        layoutOffers.setVisibility(View.GONE);
        // initListOffer:
        arrFood = new ArrayList<Menu>();
        foodAdapter = new ListFoodHomeAdapter(self, arrFood);
        lsvOffer.setAdapter(foodAdapter);
        lsvOffer.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                Menu menu = arrFood.get(index);
                Bundle b = new Bundle();
                b.putInt(GlobalValue.KEY_FOOD_ID, menu.getId());
                b.putString(GlobalValue.KEY_NAVIGATE_TYPE, "FAST");
                b.putString(GlobalValue.KEY_FROM_SCREEN, HOME_SCREEN);
                GlobalValue.KEY_LOCAL_NAME = menu.getLocalName();
                gotoActivity(self, FoodDetailActivity.class, b);
            }
        });

        // init Map

    }

    private void initGoogleMap() {
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            googleMap = fm.getMap();
            googleMap.setMyLocationEnabled(true);
            googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            googleMap
                    .setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            // TODO Auto-generated method stub
                            String title = marker.getTitle();
                            for (int i = 0; i < arrShop.size(); i++) {
                                if (arrShop.get(i).getShopName().equals(title)) {
                                    gotoShopDetailActivity(arrShop.get(i)
                                            .getShopId());
                                    break;
                                }
                            }
                        }
                    });

            // get location using GPS. disable when fake data
            if (gps.canGetLocation()) {
                handler.post(runGoogleUpdateLocation);
            } else {
                gps.showSettingsAlert();
            }

        }
    }

    private void setLocationLatLong(double longitude, double latitude) {
        LatLng currentLocation = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        googleMap.animateCamera(CameraUpdateFactory
                .zoomTo(GlobalValue.ZOOM_SIZE));// zoom : 2-21
    }

    private void setData(double latitude, double longitude) {
        getOfferData(latitude, longitude);
        getListShop(latitude, longitude);
        setLocationLatLong(longitude, latitude);
    }

    private void getDefaultLocation() {
        ModelManager.getDefaultLocation(this, true, new ModelManagerListener() {

            @Override
            public void onError(VolleyError error) {
                refreshContent();
                Toast.makeText(self, ErrorNetworkHandler.processError(error),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(Object object) {
                String json = (String) object;
                GlobalValue.glatlng = ParserUtility.parseDefaultLocation(json);
                refreshContent();
            }
        });
    }

    private void getListShop(double latitude, double longitude) {
        ModelManager.getListShop(this, longitude, latitude,
                true, new ModelManagerListener() {

                    @Override
                    public void onError(VolleyError error) {
                        arrShop.clear();
                        lblBestShop.setText(arrShop.size() + " "
                                + getString(R.string.home_shop_title));
                        updateMarkerGoogle(arrShop);
                        Toast.makeText(self, ErrorNetworkHandler.processError(error),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(Object object) {
                        String json = (String) object;

                        arrShop.clear();
                        arrShop.addAll(ParserUtility.getListShop(json));
                        lblBestShop.setText(arrShop.size() + " "
                                + getString(R.string.home_shop_title));
                        updateMarkerGoogle(arrShop);

                    }
                });
    }

    private void updateMarkerGoogle(ArrayList<Shop> arr) {
        googleMap.clear();
        markerRestaurantMap.clear();
        Bitmap bitmap = null;
        for (final Shop shop : arr) {
            LatLng item = new LatLng(shop.getLatitude(), shop.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(
                    item).title(shop.getShopName()));
            try {
                // set marker icon
                bitmap = ImageUtil.createBitmapFromUrl(shop.getImage());
                shop.setBmImage(bitmap);
                // resize
                bitmap = ImageUtil.getResizedBitmap(bitmap, 60, 60);
                // ser Bitmap to marker
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            } catch (Exception e) {
                marker.setIcon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_address_map));
            }
            markerRestaurantMap.put(marker.getId(), shop);

        }

    }

    private void getOfferData(double latitude, double longitude) {
        ModelManager.getListFoodOfDay(this, longitude,
                latitude, true, new ModelManagerListener() {

                    @Override
                    public void onSuccess(Object object) {
                        String json = (String) object;
                        arrFood.clear();
                        arrFood.addAll(ParserUtility.parseListFood(json));
                        if (arrFood.size() > 0) {
                            layoutOffers.setVisibility(View.VISIBLE);
                        } else {
                            layoutOffers.setVisibility(View.GONE);
                        }
                        lblBestDeal.setText(arrFood.size() + " "
                                + getString(R.string.home_menu_title));
                        foodAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // TODO Auto-generated method stub
                        arrFood.clear();
                        foodAdapter.notifyDataSetChanged();
                        Toast.makeText(self, ErrorNetworkHandler.processError(error),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private class CustomInfoWindowAdapter implements InfoWindowAdapter {

        private View v;

        public CustomInfoWindowAdapter() {
            v = getLayoutInflater().inflate(R.layout.map_detail, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (currentMaker != null && !currentMaker.isInfoWindowShown()) {
                currentMaker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            currentMaker = marker;
            Logger.d("MapMaker iD ", marker.getId());
            Logger.d("MapMaker", markerRestaurantMap.toString());

            Shop shop = markerRestaurantMap.get(marker.getId());
            TextView lblName = (TextView) v.findViewById(R.id.lblShopName);
            TextView lblPhone = (TextView) v.findViewById(R.id.lblPhone);
            final ImageView imgShop = (ImageView) v.findViewById(R.id.img_map);
            TextView lblDescription = (TextView) v
                    .findViewById(R.id.lblDescription);
            lblName.setSelected(true);
            lblDescription.setSelected(true);
            if (shop != null) {
                lblName.setText(shop.getShopName());
                lblDescription.setText(shop.getAddress());
                lblPhone.setText(shop.getPhone());
                if (shop.getImage() != null
                        && !shop.getImage().equalsIgnoreCase("")) {
                    if (shop.getBmImage() != null) {
                        aq.id(imgShop).image(shop.getBmImage());
                    } else {
                        aq.id(imgShop).image(shop.getImage(), true, true, 0,
                                R.drawable.no_image_available,
                                new BitmapAjaxCallback() {
                                    @Override
                                    public void callback(String url, ImageView iv,
                                                         Bitmap bm, AjaxStatus status) {

                                        if (bm != null) {
                                            Drawable d = new BitmapDrawable(
                                                    getResources(), ImageUtil
                                                    .getResizedBitmap(bm,
                                                            150, 150));
                                            imgShop.setBackground(d);
                                        } else {
                                            imgShop.setBackgroundResource(R.drawable.no_image_available);
                                        }
                                    }
                                });
                    }
                } else {
                    imgShop.setImageResource(R.drawable.ic_logo);
                }
            } else {
                Logger.d("AAA", "Restaurant is null");
            }

            return v;
        }
    }

    public void refreshContent() {
       refreshMyLocation();
        if (gps.getLatitude() == 0 && gps.getLongitude() == 0) {
        setData(GlobalValue.glatlng.latitude, GlobalValue.glatlng.longitude);
        } else {
            setData(gps.getLatitude(), gps.getLongitude());
        }

    }


    public void gotoShopDetailActivity(int shopId) {
        Bundle bundle = new Bundle();
        bundle.putInt(GlobalValue.KEY_SHOP_ID, shopId);
        ((MainTabActivity) getParent()).gotoActivity(ShopDetailActivity.class,
                bundle);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        this.getParent().onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        handler.removeCallbacks(runGoogleUpdateLocation);
    }

    private void refreshMyLocation() {
        Location location = null;
        if (googleMap != null) {
            location = googleMap.getMyLocation();

            if (location == null) {
                if (gps.canGetLocation()) {
                    location = gps.getLocation();
                }
                handler.postDelayed(runGoogleUpdateLocation, 15 * 1000);
            }
        }
        if (location != null)
            setLocationLatLong(location.getLongitude(), location.getLatitude());

    }

    Runnable runGoogleUpdateLocation = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (NetworkUtil.checkNetworkAvailable(self))
                refreshMyLocation();
        }
    };

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

}
