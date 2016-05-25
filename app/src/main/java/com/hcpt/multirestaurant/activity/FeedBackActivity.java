package com.hcpt.multirestaurant.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.util.CustomToast;
import com.hcpt.multirestaurant.util.NetworkUtil;
import com.hcpt.multirestaurant.widget.AutoBgButton;

public class FeedBackActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private EditText edtTitle, edtDes;
    private AutoBgButton btnSend;
    public static final String MESSAGE_SUCCESS = "success";
    private String type = "2";
    private Activity context = this;
    private String shopName;
    private int shopId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            shopName = extras.getString(GlobalValue.KEY_SHOP_NAME);
            shopId = extras.getInt(GlobalValue.KEY_SHOP_ID);
        }
        initUI();
    }

    private void initData() {
        // TODO Auto-generated method stub
    }

    private void initControlUI() {
        // TODO Auto-generated method stub
    }

    private void initUI() {
        // TODO Auto-generated method stub
        btnSend = (AutoBgButton) findViewById(R.id.btnSend);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        edtTitle = (EditText) findViewById(R.id.edtTitleFB);
        edtTitle.setEnabled(false);
        edtTitle.setText("Report " + shopName + " (ID:" + shopId + ")");
        edtDes = (EditText) findViewById(R.id.edtDesFB);
        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private String validateForm() {
        String message = MESSAGE_SUCCESS;

        String des = edtDes.getText().toString();
        String title = edtTitle.getText().toString();

        // username
        if (title.isEmpty()) {
            message = this.getResources().getString(
                    R.string.error_title_empty);
            return message;
        }

        if (des.isEmpty()) {
            message = this.getResources().getString(
                    R.string.error_des_empty);
            return message;
        }

        return message;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnBack) {
            onBackPressed();
            return;
        }
        if (v == btnSend) {
            String message = validateForm();
            if (!message.equals(MESSAGE_SUCCESS)) {
                CustomToast.showCustomAlert(this, message,
                        Toast.LENGTH_SHORT);
            } else {
                if (NetworkUtil.checkNetworkAvailable(this)) {
                    send();
                } else {
                    Toast.makeText(this,
                            R.string.message_network_is_unavailable,
                            Toast.LENGTH_LONG).show();
                }

            }
            return;
        }
    }

    private void send() {
        // TODO Auto-generated method stub
        ModelManager.putFeedBack(this,
                GlobalValue.myAccount.getId() + "", edtTitle.getText()
                        .toString(), edtDes.getText().toString(), type, true,
                new ModelManagerListener() {

                    @Override
                    public void onSuccess(Object object) {
                        // TODO Auto-generated method stub
                        CustomToast.showCustomAlert(context,
                                getString(R.string.message_success),
                                Toast.LENGTH_SHORT);
                        onBackPressed();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
