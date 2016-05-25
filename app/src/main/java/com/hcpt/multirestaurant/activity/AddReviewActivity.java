package com.hcpt.multirestaurant.activity;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.config.WebServiceConfig;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.util.NetworkUtil;

public class AddReviewActivity extends BaseActivity implements OnClickListener {

    private ImageView mBtnBack;
    private RatingBar mRtb;
    private EditText mTxtName, mTxtReview;
    private TextView mBtnAdd;

    private String mUser = "";
    private String mRate = "";
    private String mContent = "";
    private String mShopId = "";
    private String mFoodId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_review);
        initUI();
        getExtraValues();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == mBtnBack) {
            onBackPressed();
        } else if (v == mBtnAdd) {
            if (!NetworkUtil.checkNetworkAvailable(this)) {
                Toast.makeText(this, R.string.message_network_is_unavailable, Toast.LENGTH_LONG).show();
            } else {
                addReview();
            }
        }
    }

    private void getExtraValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(GlobalValue.KEY_SHOP_ID)) {
                mShopId = bundle.getInt(GlobalValue.KEY_SHOP_ID) + "";
            }

            if (bundle.containsKey(GlobalValue.KEY_FOOD_ID)) {
                mFoodId = bundle.getInt(GlobalValue.KEY_FOOD_ID) + "";
            }
        }

        if (GlobalValue.myAccount != null) {
            mTxtName.setText(GlobalValue.myAccount.getUserName());
        }
    }

    private void addReview() {
        // Get values.
        if (GlobalValue.myAccount != null) {
            mUser = GlobalValue.myAccount.getUserName();
        } else {
            mUser = mTxtName.getText().toString().trim();
        }

        mRate = (mRtb.getProgress() * 2) + "";

        mContent = mTxtReview.getText().toString().trim();

        // Call add api.
        if (mUser.isEmpty()) {
            Toast.makeText(self,
                    getResources().getString(R.string.please_enter_your_name),
                    Toast.LENGTH_SHORT).show();
            mTxtName.requestFocus();
        } else if (mContent.isEmpty()) {
            Toast.makeText(
                    self,
                    getResources().getString(
                            R.string.please_leave_your_messages),
                    Toast.LENGTH_SHORT).show();
            mTxtReview.requestFocus();
        } else {
            ModelManager.addFoodReview(self, mShopId, mFoodId, mRate, mUser,
                    mContent, false, new ModelManagerListener() {

                        @Override
                        public void onError(VolleyError error) {
                            Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(Object object) {
                            try {
                                String json = (String) object;
                                if (!json.isEmpty()) {
                                    JSONObject obj = new JSONObject(json);
                                    if (obj.getString(WebServiceConfig.KEY_STATUS).equals(WebServiceConfig.KEY_STATUS_SUCCESS)) {
                                        Toast.makeText(
                                                self,
                                                R.string.message_adding_new_comment_successfully,
                                                Toast.LENGTH_SHORT).show();
                                        AddReviewActivity.this.finish();
                                    } else {
                                        Toast.makeText(
                                                self,
                                                R.string.error_adding_new_comment,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            self,
                                            R.string.error_adding_new_comment,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        }
    }

    private void initUIControls() {
        // TODO Auto-generated method stub
        mBtnBack.setOnClickListener(this);
        mBtnAdd.setOnClickListener(this);
    }

    private void initUI() {
        // TODO Auto-generated method stub
        mBtnBack = (ImageView) findViewById(R.id.btnBack);
        mRtb = (RatingBar) findViewById(R.id.rtbRating);
        mTxtName = (EditText) findViewById(R.id.txtFullname);
        mTxtReview = (EditText) findViewById(R.id.txtReview);
        mBtnAdd = (TextView) findViewById(R.id.btnAdd);

        // Should call this method at the end of declaring UI.
        initUIControls();
    }
}
