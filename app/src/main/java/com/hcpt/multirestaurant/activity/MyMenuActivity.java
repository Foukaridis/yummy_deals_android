package com.hcpt.multirestaurant.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hcpt.multirestaurant.BaseActivity;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.adapter.MyMenuAdapter;
import com.hcpt.multirestaurant.config.GlobalValue;
import com.hcpt.multirestaurant.object.Shop;

@SuppressLint("NewApi")
public class MyMenuActivity extends BaseActivity {
	private ImageView btnBack;
	private TextView lblSum, lblVAT, lblShip, lblShopName;
	private ListView lsvFood;
	private MyMenuAdapter foodAdapter;
	private double total;
	public Shop shop = null;
	private int shop_position = -1;

	public interface MenuListener {
		public void deleteItem(int position);

		public void addQuantity(int position, int quantity);

		public void deleteQuantity(int position, int quantity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_food_of_shop_cart);
		initUI();
		initData();

	}

	private void initUI() {
		btnBack = (ImageView) findViewById(R.id.btnBack);
		lblShopName = (TextView) findViewById(R.id.lblShopName);
		lblSum = (TextView) findViewById(R.id.lblSum);
		lsvFood = (ListView) findViewById(R.id.lsvShop);
		lblShip = (TextView) findViewById(R.id.lblShip);
		lblVAT = (TextView) findViewById(R.id.lblVAT);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

	private void initData() {
		Bundle b = getIntent().getExtras();
		if (b != null) {
			shop_position = b.getInt("position");
		}
		shop = GlobalValue.arrMyMenuShop.get(shop_position);
		lblShopName.setText(shop.getShopName());

		foodAdapter = new MyMenuAdapter(self, shop.getArrOrderFoods(),
				new MenuListener() {

					@Override
					public void deleteItem(int position) {
						// TODO Auto-generated method stub
						shop.removeFoodOrder(shop.getArrOrderFoods().get(
								position));
						refreshContent();
					}

					@Override
					public void addQuantity(int position, int quantity) {
						// TODO Auto-generated method stub
						shop.updateQuantityOfFood(position, quantity);
						GlobalValue.arrMyMenuShop.get(shop_position)
								.updateQuantityOfFood(position, quantity);
						refreshContent();
					}

					@Override
					public void deleteQuantity(int position, int quantity) {
						// TODO Auto-generated method stub
						shop.updateQuantityOfFood(position, quantity);
						GlobalValue.arrMyMenuShop.get(shop_position)
								.updateQuantityOfFood(position, quantity);
						refreshContent();
					}

				});
		lsvFood.setAdapter(foodAdapter);
		refreshContent();
	}

	private void refreshContent() {

		lblVAT.setText(getString(R.string.vat) + " "
				+ getString(R.string.currency)
				+ String.format("%.1f", shop.getCurrentTotalVAT()));
		lblShip.setText(getString(R.string.ship) + " "
				+ getString(R.string.currency)
				+ String.format("%.1f", shop.getcurrentShipping()));

		total = shop.getCurrentTotalVAT() + shop.getcurrentShipping()
				+ shop.getTotalPrice();
		lblSum.setText(getString(R.string.currency)
				+ String.format("%.1f", total));

		foodAdapter.notifyDataSetChanged();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
