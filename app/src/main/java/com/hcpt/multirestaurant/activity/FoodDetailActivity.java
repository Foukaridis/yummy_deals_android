package com.hcpt.multirestaurant.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.adapter.CommentAdapter;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.network.ParserUtility;
import com.hcpt.multirestaurant.object.Category;
import com.hcpt.multirestaurant.object.Comment;
import com.hcpt.multirestaurant.object.Menu;
import com.hcpt.multirestaurant.object.Shop;
import com.hcpt.multirestaurant.util.CustomToast;
import com.hcpt.multirestaurant.widget.AutoBgButton;


import java.util.ArrayList;

public class FoodDetailActivity extends BaseActivity implements OnClickListener {

    private TextView lblShopName, lblMenuName;
    private ImageView btnAddtoMenu;
    private AutoBgButton btnAddReview;
    private ImageView btnBack;
    private int foodId = -1;
    private Menu food;
    private AQuery aq;
    private ProgressBar progress;
    private Shop shop;
    private Category category;
    private String shopName = "", categoryName = "";
    public static BaseActivity self;
    private boolean isFastSearch = false;

    private ListView mLsvComment;
    private ArrayList<Comment> mArrComment;
    private CommentAdapter mCommentAdapter;
    private TextView mLblCountComment;
    private Button mBtnLoadmore;
    private int page = 1;

    private String fromScreen = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        self = this;
        aq = new AQuery(self);
        initUI();
        initControl();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (foodId != -1) {
            // Get food detail
            ModelManager.getFoodById(self, foodId, true,
                    new ModelManagerListener() {

                        @Override
                        public void onSuccess(Object object) {
                            // TODO Auto-generated method stub
                            String json = (String) object;
                            food = ParserUtility.parseFood(json);
                            setDataToUI(food);
                            // Get food comments
                            getComments(foodId + "", food);
                        }

                        @Override
                        public void onError(VolleyError error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void initUI() {
        btnAddtoMenu = (ImageView) findViewById(R.id.btnAddToMenu);
        btnAddReview = (AutoBgButton) findViewById(R.id.btnAddReview);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        lblShopName = (TextView) findViewById(R.id.lblShopName);
        lblMenuName = (TextView) findViewById(R.id.lblMenuName);
        progress = (ProgressBar) findViewById(R.id.progess);
        mLsvComment = (ListView) findViewById(R.id.lsv_comment);
        // Add loadmore button
        mBtnLoadmore = (Button) getLayoutInflater().inflate(
                R.layout.loadmore_button, null);
        mLsvComment.addFooterView(mBtnLoadmore);
        mLblCountComment = (TextView) findViewById(R.id.lblReviewNumber);
    }

    private void initControl() {
        btnBack.setOnClickListener(this);
        btnAddtoMenu.setOnClickListener(this);
        lblShopName.setOnClickListener(this);
        btnAddReview.setOnClickListener(this);
        mBtnLoadmore.setOnClickListener(this);
    }

    private void initData() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(GlobalValue.KEY_FOOD_ID)) {
                foodId = b.getInt(GlobalValue.KEY_FOOD_ID);
            }
            if (b.containsKey(GlobalValue.KEY_SHOP_NAME)) {
                shopName = b.getString(GlobalValue.KEY_SHOP_NAME);
                lblShopName.setText(shopName);
            }

            if (b.containsKey(GlobalValue.KEY_CATEGORY_NAME)) {
                categoryName = b.getString(GlobalValue.KEY_CATEGORY_NAME);
                lblMenuName.setText(categoryName);
            }
            if (b.containsKey(GlobalValue.KEY_NAVIGATE_TYPE)) {
                isFastSearch = true;
            }
            if (b.containsKey(GlobalValue.KEY_FROM_SCREEN)) {
                fromScreen = b.getString(GlobalValue.KEY_FROM_SCREEN);
            }
        }

    }

    private void setDataToUI(Menu mfood) {
        mLblCountComment.setText(food.getRateNumber() + " comment(s)");
        ModelManager.getShopById(self, mfood.getShopId(), true,
                new ModelManagerListener() {

                    @Override
                    public void onSuccess(Object object) {
                        // TODO Auto-generated method stub
                        String json = (String) object;
                        shop = ParserUtility.parseShop(json);
                        if (shop != null && shopName.equals(""))
                            lblShopName.setText(shop.getShopName());
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                    }
                });

        ModelManager.getCatgoryById(self, mfood.getCategoryId(), true,
                new ModelManagerListener() {

                    @Override
                    public void onSuccess(Object object) {
                        // TODO Auto-generated method stub
                        String json = (String) object;
                        category = ParserUtility.parseCategory(json);
                        if (category != null && categoryName.equals(""))
                            lblMenuName.setText(category.getName());
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnBack) {
            onBackPressed();
        } else if (v == btnAddtoMenu) {
            addToMyMenu(food);
        } else if (v == lblShopName) {
            gotoShopDetail(shop.getShopId());
        } else if (v == mBtnLoadmore) {
            loadMore();
        } else if (v == btnAddReview) {
            try {
                if (GlobalValue.myAccount == null) {
                    CustomToast.showCustomAlert(self,
                            self.getString(R.string.message_no_login),
                            Toast.LENGTH_SHORT);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(GlobalValue.KEY_SHOP_ID, shop.getShopId());
                    bundle.putInt(GlobalValue.KEY_FOOD_ID, foodId);
                    gotoActivity(self, AddReviewActivity.class, bundle);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadMore() {
        page++;
        ModelManager.getFoodsComments(self, foodId + "", page, false,
                new ModelManagerListener() {

                    @Override
                    public void onSuccess(Object object) {
                        // TODO Auto-generated method stub
                        String json = (String) object;
                        ArrayList<Comment> arrTemp = ParserUtility
                                .parseComments(json);

                        // Show/hide 'Load more' button
                        if (arrTemp != null
                                && arrTemp.size() % GlobalValue.COMMENT_PAGE == 0) {
                            // mBtnLoadmore.setVisibility(View.VISIBLE);
                        } else {
                            mLsvComment.removeFooterView(mBtnLoadmore);
                        }

                        if (arrTemp != null && arrTemp.size() > 0) {
                            mArrComment.addAll(arrTemp);
                            mCommentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(self,
                                    "You had all of comments already.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addToMyMenu(Menu food) {
        int shopIndex = -1;
        if (GlobalValue.arrMyMenuShop == null) {
            GlobalValue.arrMyMenuShop = new ArrayList<Shop>();
        }

        if (shop != null) {
            for (int i = 0; i < GlobalValue.arrMyMenuShop.size(); i++) {
                Shop item = GlobalValue.arrMyMenuShop.get(i);
                if (item.getShopId() == shop.getShopId()) {
                    shopIndex = i;
                    break;
                }
            }

            if (shopIndex != -1) {
                shop = GlobalValue.arrMyMenuShop.get(shopIndex);
                boolean isExistedMenu = false;
                for (Menu menu : shop.getArrOrderFoods()) {
                    if (menu.getId() == food.getId()) {
                        isExistedMenu = true;
                    }
                }

                if (isExistedMenu) {
                    CustomToast.showCustomAlert(self,
                            "This item is existed in my cart !",
                            Toast.LENGTH_SHORT);
                } else {
                    food.setOrderNumber(1);
                    GlobalValue.arrMyMenuShop.get(shopIndex).addFoodOrder(food);
                    CustomToast.showCustomAlert(self,
                            "This item is added to my cart !",
                            Toast.LENGTH_SHORT);
                    if (isFastSearch) {
                        onBackPressed();
                    } else {
                        gotoShopDetail();
                    }
                }

            } else {
                food.setOrderNumber(1);
                shop.addFoodOrder(food);
                GlobalValue.arrMyMenuShop.add(shop);
                CustomToast.showCustomAlert(self,
                        "This item is added to my cart !", Toast.LENGTH_SHORT);
                if (isFastSearch) {
                    onBackPressed();
                } else {
                    gotoShopDetail();
                }
            }
        } else {
            CustomToast.showCustomAlert(self, "Have error! Please try again!",
                    Toast.LENGTH_SHORT);
        }
    }

    private void gotoShopDetail() {
        if (ListFoodActivity.self != null) {
            ListFoodActivity.self.finish();
        }
        if (ListCategoryActivity.self != null) {
            ListCategoryActivity.self.finish();
        }
        if (ListCategoryActivity.self != null) {
            ListCategoryActivity.self.finish();
        }
        if (OfferActivity.self != null) {
            OfferActivity.self.finish();
        }
        if (ShopDetailActivity.self == null) {
            Bundle b = new Bundle();
            b.putInt(GlobalValue.KEY_SHOP_ID, shop.getShopId());
            backActivity(ShopDetailActivity.class, b);
        }

        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void getComments(String id, final Menu mfood) {
        ModelManager.getFoodsComments(self, id, page, false,
                new ModelManagerListener() {

                    @Override
                    public void onSuccess(Object object) {
                        // TODO Auto-generated method stub
                        String json = (String) object;
                        mArrComment = ParserUtility.parseComments(json);

                        if (mArrComment != null) {
                            if (mArrComment.size() > 0) {
                                // Show/hide 'Load more' button
                                if (mArrComment.size() % GlobalValue.COMMENT_PAGE == 0) {
                                } else {
                                    mLsvComment.removeFooterView(mBtnLoadmore);
                                }
                            }else{
                                mLsvComment.removeFooterView(mBtnLoadmore);
                            }
                            mCommentAdapter = new CommentAdapter(self,
                                    mArrComment, false);
                            mCommentAdapter.food = mfood;
                            mCommentAdapter.mfood = mfood;
                            mLsvComment.setAdapter(mCommentAdapter);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
