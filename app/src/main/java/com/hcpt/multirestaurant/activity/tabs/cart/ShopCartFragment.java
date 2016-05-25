package com.hcpt.multirestaurant.activity.tabs.cart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcpt.multirestaurant.BaseFragment;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.activity.MyMenuActivity;
import com.hcpt.multirestaurant.activity.tabs.MainCartActivity;
import com.hcpt.multirestaurant.activity.tabs.MainTabActivity;
import com.hcpt.multirestaurant.adapter.ShopCartAdapter;
import com.hcpt.multirestaurant.adapter.ShopCartAdapter.ShopCartListener;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.object.Shop;
import com.hcpt.multirestaurant.util.CustomToast;
import com.hcpt.multirestaurant.widget.AutoBgButton;

@SuppressLint("NewApi")
public class ShopCartFragment extends BaseFragment implements OnClickListener {

	private AutoBgButton btnOrder;
	private TextView lblSum, lblVAT, lblShip;
	private ListView lsvShops;
	private ShopCartAdapter shopAdapter;
	private double total;
	private MainCartActivity self;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_list_shop_cart, container,
				false);
		self = (MainCartActivity) getActivity();
		initUI(view);
		initData();
		return view;
	}

	private void initUI(View view) {
		btnOrder = (AutoBgButton) view.findViewById(R.id.btnOrder);
		lblSum = (TextView) view.findViewById(R.id.lblSum);
		lsvShops = (ListView) view.findViewById(R.id.lsvShop);
		lblShip = (TextView) view.findViewById(R.id.lblShip);
		lblVAT = (TextView) view.findViewById(R.id.lblVAT);
		btnOrder.setOnClickListener(this);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			refreshContent();
		}
	}

	private void initData() {
		shopAdapter = new ShopCartAdapter(self, GlobalValue.arrMyMenuShop,
				new ShopCartListener() {

					@Override
					public void showDetailOrder(int position) {
						// TODO Auto-generated method stub
						Bundle b = new Bundle();
						b.putInt("position", position);
						((MainTabActivity) self.getParent()).gotoActivity(
								MyMenuActivity.class, b);

					}

					@Override
					public void deleteItem(int position) {
						// TODO Auto-generated method stub
						GlobalValue.arrMyMenuShop.remove(position);
						shopAdapter.notifyDataSetChanged();
					}
				});
		lsvShops.setAdapter(shopAdapter);

	}

	@Override
	public void refreshContent() {
		total = (double) 0;
		shopAdapter.notifyDataSetChanged();
		double totalOfShop = 0;
		double VATOfShop = 0;
		double ShipPriceOfShop = 0;

		for (int i = 0; i < GlobalValue.arrMyMenuShop.size(); i++) {
			Shop shop = GlobalValue.arrMyMenuShop.get(i);
			if (shop.getArrOrderFoods().size() == 0) {
				GlobalValue.arrMyMenuShop.remove(i);
			} else {
				totalOfShop += shop.getTotalPrice();
				VATOfShop += shop.getCurrentTotalVAT();
				ShipPriceOfShop += shop.getcurrentShipping();
			}
		}

		total = totalOfShop + VATOfShop + ShipPriceOfShop;

		lblVAT.setText(getString(R.string.vat) + " "
				+ getString(R.string.currency)
				+ String.format("%.1f", VATOfShop));
		lblShip.setText(getString(R.string.ship) + " "
				+ getString(R.string.currency)
				+ String.format("%.1f", ShipPriceOfShop));
		lblSum.setText(getString(R.string.currency)
				+ String.format("%.1f", total));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnOrder) {
			onBtnOrderClick();
		}
	}

	private void onBtnOrderClick() {
		if (GlobalValue.myAccount == null) {
			CustomToast.showCustomAlert(self,
					self.getString(R.string.message_no_login),
					Toast.LENGTH_SHORT);
		} else {

			if (GlobalValue.arrMyMenuShop.size() > 0) {
				((MainCartActivity) self)
						.gotoFragment(MainCartActivity.PAGE_CONFIRM);
			} else {
				CustomToast.showCustomAlert(self,
						self.getString(R.string.message_no_item_menu),
						Toast.LENGTH_SHORT);
			}
		}
	}

}
