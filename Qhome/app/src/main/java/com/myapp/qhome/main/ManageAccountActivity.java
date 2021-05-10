package com.myapp.qhome.main;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.github.mmin18.widget.RealtimeBlurView;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Address;
import com.myapp.qhome.models.OrderItem;
import com.myapp.qhome.models.Phone;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageAccountActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    AVLoadingIndicatorView progressBar;
    Toolbar toolbar;
    LinearLayout tabFrame;
    private int mMaxScrollSize;
    private boolean mIsImageHidden = false;
    private int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.flexible_example_toolbar);
        toolbar.setTitle(getString(R.string.manage_account));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        setTitle(getString(R.string.manage_account));

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        tabFrame = (LinearLayout)findViewById(R.id.tabFrame);

        ((TextView)findViewById(R.id.coupons)).setTypeface(bold);
        ((TextView)findViewById(R.id.orders)).setTypeface(bold);
        ((TextView)findViewById(R.id.wishlist)).setTypeface(bold);

        ((TextView)findViewById(R.id.coupons2)).setTypeface(bold);
        ((TextView)findViewById(R.id.orders2)).setTypeface(bold);
        ((TextView)findViewById(R.id.wishlist2)).setTypeface(bold);

        ((TextView)findViewById(R.id.myProfile)).setTypeface(bold);
        ((TextView)findViewById(R.id.shippingInfo)).setTypeface(bold);
        ((TextView)findViewById(R.id.luckydraw)).setTypeface(bold);
        ((TextView)findViewById(R.id.favorites)).setTypeface(bold);
        ((TextView)findViewById(R.id.feedback)).setTypeface(bold);
        ((TextView)findViewById(R.id.myStores)).setTypeface(bold);
        ((TextView)findViewById(R.id.contactUs)).setTypeface(bold);
        ((TextView)findViewById(R.id.received_orders)).setTypeface(bold);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        if(Commons.thisUser != null && Commons.thisUser.get_role().equals("producer")) {
            ((LinearLayout)findViewById(R.id.receivedFrame)).setVisibility(View.VISIBLE);
        }else ((LinearLayout)findViewById(R.id.receivedFrame)).setVisibility(View.GONE);

        if(Commons.thisUser != null) {
            ((LinearLayout)findViewById(R.id.luckydrawFrame)).setVisibility(View.VISIBLE);
        }else {
            ((LinearLayout)findViewById(R.id.luckydrawFrame)).setVisibility(View.GONE);
        }

    }

    public void toSettings(View view){
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void toLuckyDraw(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), LuckyDrawActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toCoupons(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), CouponListActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toPost(View view){
        if(Commons.thisUser != null && Commons.thisUser.get_role().equals("producer")){
            Intent intent = new Intent(getApplicationContext(), MyStoreListActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toShippingAddress(View view){
        Intent intent = new Intent(getApplicationContext(), ShippingAddressActivity.class);
        startActivity(intent);
    }

    public void toContactUs(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toOrders(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toProfile(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toReceivedOrders(View view){
        Intent intent = new Intent(getApplicationContext(), ReceivedOrdersActivity.class);
        startActivity(intent);
    }

    public void toFeedback(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView)findViewById(R.id.cnt_store)).setText("(" + String.valueOf(Commons.myStores.size()) + ")");
        if(Commons.thisUser != null && Commons.thisUser.get_role().equals("producer"))getOrderItems();
        getPhones();
        getAddresses();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        Log.d("Percentage+++", String.valueOf(currentScrollPercentage));

        if (currentScrollPercentage >= 10) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;
                ((RealtimeBlurView)findViewById(R.id.real_time_blur_view)).setVisibility(View.VISIBLE);
                ((RealtimeBlurView)findViewById(R.id.real_time_blur_view))
                        .animate()
                        .alpha(1.0f)
                        .setDuration(500)
                        .start();
                tabFrame.animate()
                        .alpha(1.0f)
                        .setDuration(800)
                        .start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabFrame.setVisibility(View.VISIBLE);
                    }
                }, 500);
            }
        }
        else if (currentScrollPercentage <= 20) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ((RealtimeBlurView)findViewById(R.id.real_time_blur_view))
                        .animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .start();
                ((RealtimeBlurView)findViewById(R.id.real_time_blur_view)).setVisibility(View.GONE);

                tabFrame.animate()
                        .alpha(0.0f)
                        .setDuration(200)
                        .start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabFrame.setVisibility(View.GONE);
                    }
                }, 200);

            }
        }
    }

    public void toWishlist(View view){
        Intent intent = new Intent(getApplicationContext(), WishlistActivity.class);
        startActivity(intent);
    }

    public void toFavorites(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void getOrderItems() {
        String url = ReqConst.SERVER_URL + "receivedOrderItems";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetOrderItemsResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetOrderItemsResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                ArrayList<OrderItem> onGoingItems = new ArrayList<>();
                ArrayList<OrderItem> placedItems = new ArrayList<>();

                JSONArray dataArr = response.getJSONArray("data");
                for(int j=0; j<dataArr.length(); j++) {
                    JSONObject obj = (JSONObject) dataArr.get(j);
                    OrderItem item = new OrderItem();
                    item.setId(obj.getInt("id"));
                    item.setOrder_id(obj.getInt("order_id"));
                    item.setUser_id(obj.getInt("member_id"));
                    item.setImei_id(obj.getString("imei_id"));
                    item.setProducer_id(obj.getInt("producer_id"));
                    item.setStore_id(obj.getInt("store_id"));
                    item.setStore_name(obj.getString("store_name"));
                    item.setAr_store_name(obj.getString("ar_store_name"));
                    item.setProduct_id(obj.getInt("product_id"));
                    item.setProduct_name(obj.getString("product_name"));
                    item.setAr_product_name(obj.getString("ar_product_name"));
                    item.setCategory(obj.getString("category"));
                    item.setAr_category(obj.getString("ar_category"));
                    item.setPrice(Double.parseDouble(obj.getString("price")));
                    item.setUnit(obj.getString("unit"));
                    item.setQuantity(obj.getInt("quantity"));
                    item.setDate_time(obj.getString("date_time"));
                    item.setPicture_url(obj.getString("picture_url"));
                    item.setStatus(obj.getString("status"));
                    item.setOrderID(obj.getString("orderID"));
                    item.setContact(obj.getString("contact"));
                    item.setDiscount(obj.getInt("discount"));

                    Log.d("STATUS!!!", item.getStatus());

                    if(!item.getStatus().equals("delivered")){
                        onGoingItems.add(item);
                        if(item.getStatus().equals("placed"))placedItems.add(item);
                    }
                }

                ((TextView)findViewById(R.id.cnt_received)).setText("(" + String.valueOf(onGoingItems.size()) + ")");
                if(placedItems.size() > 0) {
                    ((FrameLayout) findViewById(R.id.orderCountFrame)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.count_order)).setText(String.valueOf(placedItems.size()));
                }else ((FrameLayout) findViewById(R.id.orderCountFrame)).setVisibility(View.GONE);

            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    private void getPhones() {
        String url = ReqConst.SERVER_URL + "getPhones";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseProductsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(Commons.thisUser != null)params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                else params.put("member_id","0");
                params.put("imei_id", Commons.IMEI);      Log.d("IMEI!!!", Commons.IMEI);
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseProductsResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                Commons.phones.clear();
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Phone phone = new Phone();
                    phone.setId(object.getInt("id"));
                    phone.setImei_id(object.getString("imei_id"));
                    phone.setUserId(object.getInt("member_id"));
                    phone.setPhone_number(object.getString("phone_number"));
                    phone.setStatus(object.getString("status"));

                    Commons.phones.add(phone);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAddresses() {
        String url = ReqConst.SERVER_URL + "getAddresses";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetCartResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(Commons.thisUser != null)params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                else params.put("member_id","0");
                params.put("imei_id", Commons.IMEI);
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetCartResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                Commons.addresses.clear();
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Address address = new Address();
                    address.setId(object.getInt("id"));
                    address.setImei_id(object.getString("imei_id"));
                    address.setUserId(object.getInt("member_id"));
                    address.setAddress(object.getString("address"));
                    address.setArea(object.getString("area"));
                    address.setStreet(object.getString("street"));
                    address.setHouse(object.getString("house"));
                    address.setStatus(object.getString("status"));

                    Commons.addresses.add(address);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}









































