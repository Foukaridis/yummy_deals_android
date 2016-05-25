package com.hcpt.multirestaurant.activity.tabs;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.activity.SearchListFoodResultActivity;
import com.hcpt.multirestaurant.activity.SearchShopResultActivity;
import com.hcpt.multirestaurant.adapter.CategoryAdapter;
import com.hcpt.multirestaurant.adapter.CityAdapter;
import com.hcpt.multirestaurant.adapter.SpinnerSimpleAdapter;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.interfaces.IMaps;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.network.ParserUtility;
import com.hcpt.multirestaurant.object.Category;
import com.hcpt.multirestaurant.object.City;
import com.hcpt.multirestaurant.util.MapsUtil;
import com.hcpt.multirestaurant.util.NetworkUtil;
import com.hcpt.multirestaurant.widget.AutoBgButton;

@SuppressLint("NewApi")
public class SearchActivity extends BaseActivity implements OnClickListener {

    private Spinner spnCategories, spnCity, spnSortBy, spnSortType;
    private ArrayList<City> arrCities = new ArrayList<City>();
    private ArrayList<Category> arrCategory = new ArrayList<Category>();
    private CityAdapter cityAdapter;
    private CategoryAdapter categoryAdapter;
    private EditText edtSearch;
    private AutoBgButton btnSearch;
    private LinearLayout btnSelectShop, btnSelectMenu, btnSelectAll,
            btnSelectOpen;
    private ImageView imgticShop, imgticMenu, imgTickAll, imgTickOpen;
    private TextView lblMenu, lblShop, lblAll, lblOpen, lblDistance;
    private boolean isSelectShop = true;
    private boolean isOpen = false;

    private String cityId = "";
    private String categoryId = "";

    private SeekBar skbDistance;
    private SpinnerSimpleAdapter sortByAdapter, sortTypeAdapter;

    private static final String ALL = "0";
    private static final String OPEN = "1";
    private static String ALL_OR_OPEN = ALL;

    private static final String SORT_BY_RATING = "rate";
    private static final String SORT_BY_NAME = "name";
    private static final String SORT_BY_DATE = "date";
    private static String SORT_BY = SORT_BY_RATING;

    private static final String SORT_TYPE_DESC = "desc";
    private static final String SORT_TYPE_ASC = "asc";
    private static String SORT_TYPE = SORT_TYPE_DESC;

    private String distance = "0";
    private String lat = "", lon = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_search);
        getLatLong();
        initUI();
        initControl();
        setData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            refreshContent();
        }
    }

    private void refreshContent() {
        if ((arrCategory.size() < 2) || (arrCities.size() < 2)) {
            setData();
        }
    }

    private void initUI() {
        spnCategories = (Spinner) findViewById(R.id.spnCategories);
        spnCity = (Spinner) findViewById(R.id.spnCity);
        spnSortBy = (Spinner) findViewById(R.id.spnSortBy);
        spnSortType = (Spinner) findViewById(R.id.spnSortType);
        btnSearch = (AutoBgButton) findViewById(R.id.btnSearch);
        btnSelectMenu = (LinearLayout) findViewById(R.id.btnSelectMenu);
        btnSelectShop = (LinearLayout) findViewById(R.id.btnSelectShop);
        btnSelectAll = (LinearLayout) findViewById(R.id.btnSelectAll);
        btnSelectOpen = (LinearLayout) findViewById(R.id.btnSelectOpen);
        imgticMenu = (ImageView) findViewById(R.id.imgticMenu);
        imgticShop = (ImageView) findViewById(R.id.imgticShop);
        imgTickAll = (ImageView) findViewById(R.id.imgTickAll);
        imgTickOpen = (ImageView) findViewById(R.id.imgTickOpen);
        lblMenu = (TextView) findViewById(R.id.lblMenu);
        lblShop = (TextView) findViewById(R.id.lblShop);
        lblAll = (TextView) findViewById(R.id.lblAll);
        lblOpen = (TextView) findViewById(R.id.lblOpen);
        lblDistance = (TextView) findViewById(R.id.lbl_distance);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        skbDistance = (SeekBar) findViewById(R.id.skb_distance);
    }

    private void initControl() {

        btnSearch.setOnClickListener(this);
        btnSelectMenu.setOnClickListener(this);
        btnSelectShop.setOnClickListener(this);
        btnSelectAll.setOnClickListener(this);
        btnSelectOpen.setOnClickListener(this);

        spnCity.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int index, long arg3) {
                if (index != 0) {
                    cityId = arrCities.get(index).getId() + "";
                } else {
                    cityId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        spnCategories.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int index, long arg3) {
                // TODO Auto-generated method stub
                if (index != 0) {
                    categoryId = arrCategory.get(index).getId() + "";
                } else {
                    categoryId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        spnSortBy.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    SORT_BY = SORT_BY_RATING;
                } else if (position == 1) {
                    SORT_BY = SORT_BY_NAME;
                } else if (position == 2) {
                    SORT_BY = SORT_BY_DATE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spnSortType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    SORT_TYPE = SORT_TYPE_DESC;
                } else {
                    SORT_TYPE = SORT_TYPE_ASC;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        skbDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                distance = progress + "";
                if (distance.equals("0")) {
                    lblDistance.setText("All");
                } else {
                    lblDistance.setText(distance + " Km");
                }
            }
        });
    }

    private void setData() {
        // set up city
        arrCities.clear();
        arrCities.add(new City("All Cities"));

        ModelManager.getListCity(this, true, new ModelManagerListener() {

            @Override
            public void onSuccess(Object object) {
                String json = (String) object;
                ArrayList<City> arr = ParserUtility.parseListCity(json);
                arrCities.addAll(arr);
                setDataCityToList(arrCities);

            }

            @Override
            public void onError(VolleyError error) {
                // TODO Auto-generated method stub
                setDataCityToList(arrCities);
                Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
            }
        });

        // setup category
        arrCategory.clear();
        arrCategory.add(new Category("All Categories"));

        ModelManager.getListCategory(this, true, new ModelManagerListener() {

            @Override
            public void onSuccess(Object object) {
                String json = (String) object;
                ArrayList<Category> arr = ParserUtility
                        .parseListCategories(json);
                arrCategory.addAll(arr);
                setDataCategoryToList(arrCategory);

            }

            @Override
            public void onError(VolleyError error) {
                // TODO Auto-generated method stub
                setDataCategoryToList(arrCategory);
                Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
            }
        });

        setSortByData();
        setSortTypeData();
        updateAllOpenButton();
    }

    private void updateMenuShopButton() {
        if (isSelectShop) {
            // set select shop
            btnSelectShop
                    .setBackgroundResource(R.drawable.bg_button_select_right);
            imgticShop.setVisibility(View.VISIBLE);
            lblShop.setTextColor(getResources().getColor(R.color.cl_white));
            // set unselect menu
            btnSelectMenu
                    .setBackgroundResource(R.drawable.bg_button_not_select_left);
            imgticMenu.setVisibility(View.GONE);
            lblMenu.setTextColor(getResources().getColor(R.color.primary_dark));
        } else {
            // set select shop
            btnSelectShop
                    .setBackgroundResource(R.drawable.bg_button_not_select_right);
            imgticShop.setVisibility(View.GONE);
            lblShop.setTextColor(getResources().getColor(R.color.primary_dark));
            // set unselect menu
            btnSelectMenu
                    .setBackgroundResource(R.drawable.bg_button_select_left);
            imgticMenu.setVisibility(View.VISIBLE);
            lblMenu.setTextColor(getResources().getColor(R.color.cl_white));
        }
    }

    private void updateAllOpenButton() {
        if (isOpen) {
            // set select shop
            ALL_OR_OPEN = OPEN;
            btnSelectOpen
                    .setBackgroundResource(R.drawable.bg_button_select_right);
            imgTickOpen.setVisibility(View.VISIBLE);
            lblOpen.setTextColor(getResources().getColor(R.color.cl_white));
            // set unselect menu
            btnSelectAll
                    .setBackgroundResource(R.drawable.bg_button_not_select_left);
            imgTickAll.setVisibility(View.GONE);
            lblAll.setTextColor(getResources().getColor(R.color.primary_dark));
        } else {
            // set select shop
            ALL_OR_OPEN = ALL;
            btnSelectOpen
                    .setBackgroundResource(R.drawable.bg_button_not_select_right);
            imgTickOpen.setVisibility(View.GONE);
            lblOpen.setTextColor(getResources().getColor(R.color.primary_dark));
            // set unselect menu
            btnSelectAll
                    .setBackgroundResource(R.drawable.bg_button_select_left);
            imgTickAll.setVisibility(View.VISIBLE);
            lblAll.setTextColor(getResources().getColor(R.color.cl_white));
        }
    }

    private void setDataCityToList(ArrayList<City> arrCity) {
        cityAdapter = new CityAdapter(self,
                android.R.layout.simple_spinner_item, arrCity);
        cityAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCity.setAdapter(cityAdapter);
        spnCity.setSelection(0);
    }

    private void setDataCategoryToList(ArrayList<Category> arrCategory) {
        categoryAdapter = new CategoryAdapter(self,
                android.R.layout.simple_spinner_item, arrCategory);
        categoryAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategories.setAdapter(categoryAdapter);
        spnCategories.setSelection(0);
    }

    private void setSortByData() {
        sortByAdapter = new SpinnerSimpleAdapter(self,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.arr_sort_by));
        spnSortBy.setAdapter(sortByAdapter);
    }

    private void setSortTypeData() {
        sortTypeAdapter = new SpinnerSimpleAdapter(self,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.arr_sort_type));
        spnSortType.setAdapter(sortTypeAdapter);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnSelectMenu) {
            isSelectShop = false;
            updateMenuShopButton();
            return;
        }
        if (v == btnSelectShop) {
            isSelectShop = true;
            updateMenuShopButton();
            return;
        }
        if (v == btnSelectOpen) {
            ALL_OR_OPEN = OPEN;
            isOpen = true;
            updateAllOpenButton();
            return;
        }
        if (v == btnSelectAll) {
            ALL_OR_OPEN = ALL;
            isOpen = false;
            updateAllOpenButton();
            return;
        }
        if (v == btnSearch) {
            if (NetworkUtil.checkNetworkAvailable(self))
                onClickSearch();
            else {
                Toast.makeText(self, R.string.message_network_is_unavailable,
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private void onClickSearch() {
        // TODO Auto-generated method stub
        if (lat.isEmpty() || lon.isEmpty()) {
            Toast.makeText(
                    self,
                    getResources()
                            .getString(R.string.wait_for_getting_location),
                    Toast.LENGTH_SHORT).show();
            getLatLong();
        } else {
            lat = GlobalValue.glatlng.latitude + "";
            lon = GlobalValue.glatlng.longitude + "";
        }
        Bundle b = new Bundle();
        b.putString(GlobalValue.KEY_SEARCH, edtSearch.getText().toString());
        b.putString(GlobalValue.KEY_CATEGORY_ID, categoryId);
        b.putString(GlobalValue.KEY_CITY_ID, cityId);
        b.putString(GlobalValue.KEY_OPEN, ALL_OR_OPEN);
        b.putString(GlobalValue.KEY_DISTANCE, distance);
        b.putString(GlobalValue.KEY_SORT_BY, SORT_BY);
        b.putString(GlobalValue.KEY_SORT_TYPE, SORT_TYPE);
        b.putString(GlobalValue.KEY_LAT, lat);
        b.putString(GlobalValue.KEY_LONG, lon);

        if (isSelectShop)
            ((MainTabActivity) getParent()).gotoActivity(
                    SearchShopResultActivity.class, b);
        else
            ((MainTabActivity) getParent()).gotoActivity(
                    SearchListFoodResultActivity.class, b);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        this.getParent().onBackPressed();
    }

    private void getLatLong() {
        new MapsUtil.GetCurrentLatLong(self, new IMaps() {

            @Override
            public void processFinished(Object obj) {
                LatLng latLng = (LatLng) obj;
                lat = latLng.latitude + "";
                lon = latLng.longitude + "";
            }
        }).execute();
    }
}
