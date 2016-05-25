package com.hcpt.multirestaurant.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.hcpt.multirestaurant.R;
import com.hcpt.multirestaurant.object.Menu;

import java.util.ArrayList;

public class ListFoodHomeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Menu> lsvMenu;
    private LayoutInflater inflater = null;
    private AQuery aq;

    public ListFoodHomeAdapter(Context mcontext, ArrayList<Menu> arrOffer) {
        context = mcontext;
        lsvMenu = arrOffer;
        inflater = (LayoutInflater) mcontext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        aq = new AQuery(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lsvMenu.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Hoder holder;
        if (convertView == null) {
            holder = new Hoder();
            convertView = inflater.inflate(R.layout.row_list_food_home, null);
            holder.imgOffer = (ImageView) convertView
                    .findViewById(R.id.imgFood);
            holder.progress = (ProgressBar) convertView
                    .findViewById(R.id.progess);
            holder.lblRateNumber = (TextView) convertView
                    .findViewById(R.id.lblRateNumber);
            convertView.setTag(holder);

        } else {
            holder = (Hoder) convertView.getTag();
        }
        final Menu o = lsvMenu.get(position);
        if (o != null) {

            holder.lblRateNumber.setText(String.format("%.1f", (o.getRateValue() / 2)));

            aq.id(holder.imgOffer)
                    .progress(holder.progress)
                    .image(o.getImage(), true, true, 0,
                            R.drawable.no_image_available_horizontal,
                            new BitmapAjaxCallback() {
                                @SuppressLint("NewApi")
                                @Override
                                public void callback(String url, ImageView iv,
                                                     Bitmap bm, AjaxStatus status) {

                                    Drawable d = new BitmapDrawable(context
                                            .getResources(), bm);
                                    holder.imgOffer.setBackgroundDrawable(d);
                                }
                            });
        }
        return convertView;
    }

    static class Hoder {
        ImageView imgOffer;
        ProgressBar progress;
        TextView lblRateNumber;
    }
}
