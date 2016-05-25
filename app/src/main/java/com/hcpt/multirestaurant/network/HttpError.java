package com.hcpt.multirestaurant.network;

import com.android.volley.VolleyError;


public interface HttpError {
    void onHttpError(VolleyError volleyError);
}
