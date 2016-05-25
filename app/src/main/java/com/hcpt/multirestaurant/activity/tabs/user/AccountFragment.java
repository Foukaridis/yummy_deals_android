package com.hcpt.multirestaurant.activity.tabs.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.hcpt.multirestaurant.BaseFragment;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.activity.tabs.MainUserActivity;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.config.WebServiceConfig;
import com.hcpt.multirestaurant.modelmanager.ErrorNetworkHandler;
import com.hcpt.multirestaurant.modelmanager.ModelManager;
import com.hcpt.multirestaurant.modelmanager.ModelManagerListener;
import com.hcpt.multirestaurant.object.Account;
import com.hcpt.multirestaurant.util.DialogUtility;
import com.hcpt.multirestaurant.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountFragment extends BaseFragment {
    private View view;
    private LinearLayout btnMyAccount, btnOderHistory, btnRegisterShopOwner, btnFeedback, btnLogout;
    private MainUserActivity self;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.fragment_account, container, false);
        self = (MainUserActivity) getActivity();
        initUI(view);
        initControl();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            self = (MainUserActivity) getActivity();
            checkUserRole();
        }
    }


    private void initControl() {
        // TODO Auto-generated method stub
        btnOderHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (NetworkUtil.checkNetworkAvailable(self)) {
                    self.setLoadNew(true);
                    self.gotoFragment(MainUserActivity.HISTORY_PAGE);
                } else {
                    Toast.makeText(self,
                            R.string.message_network_is_unavailable,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        btnRegisterShopOwner.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClickRegisterShopOwner();
            }
        });

        btnFeedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                self.gotoFragment(MainUserActivity.FEEDBACK_PAGE);
            }
        });
        btnMyAccount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                self.gotoFragment(MainUserActivity.INFO_PAGE);
            }
        });
        btnLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                self.showLogoutConfirmDialog();
            }
        });
    }

    private void checkUserRole() {
        if (GlobalValue.myAccount.getRole().equals(Account.ROLE_SHOP_OWNER)) {
            btnRegisterShopOwner.setVisibility(View.GONE);
        } else {
            btnRegisterShopOwner.setVisibility(View.VISIBLE);
        }
    }

    private void onClickRegisterShopOwner() {
        if (GlobalValue.myAccount.isUser()) {
            if (NetworkUtil.checkNetworkAvailable(self)) {
                //call api update register shop owner
                ModelManager.registerShopOwner(self, GlobalValue.myAccount.getId(), true, new ModelManagerListener() {
                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(self, ErrorNetworkHandler.processError(error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(Object object) {
                        String response = object.toString();
                        checkResponse(response);
                    }
                });
            } else {
                Toast.makeText(self,
                        R.string.message_network_is_unavailable,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            DialogUtility.alert(self, R.string.message_permission_register_shop_owner);
        }
    }

    private void checkResponse(String response) {
        JSONObject json = null;
        try {
            json = new JSONObject(response);
            if (json.getString(WebServiceConfig.KEY_STATUS).equalsIgnoreCase(
                    WebServiceConfig.KEY_STATUS_SUCCESS)) {
                DialogUtility.alert(self, R.string.message_send_request_successfully);
            } else {
                String message = json.getString(WebServiceConfig.KEY_MESSAGE);
                DialogUtility.alert(self, message);
            }
        } catch (JSONException e) {
            DialogUtility.alert(self, R.string.message_error_undefined);
        }
    }

    // Intent intent = new Intent(this, HomeActivity.class);
    // startActivity(intent);
    private void initUI(View view) {
        // TODO Auto-generated method stub
        btnMyAccount = (LinearLayout) view.findViewById(R.id.btnMyAccount);
        btnOderHistory = (LinearLayout) view.findViewById(R.id.btnHistoryOrder);
        btnRegisterShopOwner = (LinearLayout) view.findViewById(R.id.btnRegisterShopOwner);
        btnFeedback = (LinearLayout) view.findViewById(R.id.btnFeedback);
        btnLogout = (LinearLayout) view.findViewById(R.id.btnLogout);

    }
}
