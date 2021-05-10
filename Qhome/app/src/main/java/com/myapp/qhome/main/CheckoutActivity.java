package com.myapp.qhome.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CouponListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Address;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Coupon;
import com.myapp.qhome.models.Phone;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends BaseActivity {

    TextView phone, address, addressLine, coupon, method, subTotal, shipping, total;
    TextView confirmButton;
    LinearLayout shippingFrame1, shippingFrame2;
    LinearLayout itemsBox;
    public static DecimalFormat df = new DecimalFormat("0.00");
    AVLoadingIndicatorView progressBar;
    ArrayList<Coupon> coupons = new ArrayList<>();
    CouponListAdapter couponListAdapter = new CouponListAdapter(this);
    int pDiscount = 0;
    public int couponId = 0;
    double subTotalPrice = 0.0d;
    String orderItemStr = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Commons.phoneId = 0;
        Commons.addrId = 0;

        Commons.checkoutActivity = this;

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption31)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption32)).setTypeface(normal);

        ((TextView)findViewById(R.id.caption4)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption5)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption6)).setTypeface(bold);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        phone = (TextView)findViewById(R.id.phone);
        address = (TextView)findViewById(R.id.address);
        addressLine = (TextView)findViewById(R.id.address2);
        coupon = (TextView)findViewById(R.id.coupon);
        method = (TextView)findViewById(R.id.method);
        subTotal = (TextView)findViewById(R.id.subtotal_price);
        shipping = (TextView)findViewById(R.id.shipping_price);
        total = (TextView)findViewById(R.id.total_price);
        total.setPaintFlags(total.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        itemsBox = (LinearLayout)findViewById(R.id.items);

        confirmButton = (TextView)findViewById(R.id.btn_confirm);
        confirmButton.setTypeface(bold);

        shippingFrame1 = (LinearLayout)findViewById(R.id.shippingFrame1);
        shippingFrame2 = (LinearLayout)findViewById(R.id.shippingFrame2);

        itemsBox.removeAllViews();
        for(CartItem cartItem:Commons.cartItems){
            subTotalPrice += cartItem.getPrice() * cartItem.getQuantity();
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_order_picture, null);
            RoundedImageView picture = (RoundedImageView) view.findViewById(R.id.item_picture);
            TextView mask = (TextView)view.findViewById(R.id.txt_mask);
            if(Commons.lang.equals("ar"))mask.setText(String.valueOf(cartItem.getQuantity()) + " " + getString(R.string.x));
            else mask.setText(getString(R.string.x) + " " + String.valueOf(cartItem.getQuantity()));
            Picasso.with(getApplicationContext()).load(cartItem.getPicture_url()).into(picture);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CheckoutItemsActivity.class);
                    startActivity(intent);
                }
            });
            itemsBox.addView(view);
        }

        if(Commons.lang.equals("ar")){
            subTotal.setText(getString(R.string.qr) + " "  + df.format(subTotalPrice));
            shipping.setText(getString(R.string.qr) + " "  + String.valueOf(Constants.SHIPPING_PRICE));
            total.setText(getString(R.string.qr) + " "  + df.format(subTotalPrice + Constants.SHIPPING_PRICE));
        }else {
            subTotal.setText(df.format(subTotalPrice) + " " + getString(R.string.qr));
            shipping.setText(String.valueOf(Constants.SHIPPING_PRICE) + " " + getString(R.string.qr));
            total.setText(df.format(subTotalPrice + Constants.SHIPPING_PRICE) + " " + getString(R.string.qr));
        }

        Commons.totalPrice = subTotalPrice + Constants.SHIPPING_PRICE;

        setupUI(findViewById(R.id.activity), this);

    }

    public void toCoupons(View view){
        if(Commons.thisUser != null){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
            ((LinearLayout)findViewById(R.id.bonusFrame)).setAnimation(animation);
            ((LinearLayout)findViewById(R.id.bonusFrame)).setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((LinearLayout)findViewById(R.id.bg_dark)).setVisibility(View.VISIBLE);
                }
            }, 200);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void dontUseCoupon(View view){
        couponId = 0;
        applyCoupon(pDiscount = 0);
    }

    public void applyCoupon(int discount){
        pDiscount = discount;
        dismissFrame();
        double subtp = 0.0d;
        subtp = subTotalPrice - (double) subTotalPrice * discount/100;
        if(Commons.lang.equals("ar")){
            subTotal.setText(getString(R.string.qr) + " "  + df.format(subtp));
            shipping.setText(getString(R.string.qr) + " "  + String.valueOf(Constants.SHIPPING_PRICE));
            total.setText(getString(R.string.qr) + " "  + df.format(subtp + Constants.SHIPPING_PRICE));
        }else {
            subTotal.setText(df.format(subtp) + " " + getString(R.string.qr));
            shipping.setText(String.valueOf(Constants.SHIPPING_PRICE) + " " + getString(R.string.qr));
            total.setText(df.format(subtp + Constants.SHIPPING_PRICE) + " " + getString(R.string.qr));
        }

        if(discount > 0) coupon.setText(getString(R.string.discount) + ": -" + String.valueOf(discount) + "%");
        else coupon.setText(getString(R.string.use_coupon));

        Commons.totalPrice = subtp + Constants.SHIPPING_PRICE;
    }

    public void dismissFrame(View view){
        dismissFrame();
    }

    public void dismissFrame(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
        ((LinearLayout)findViewById(R.id.bonusFrame)).setAnimation(animation);
        ((LinearLayout)findViewById(R.id.bonusFrame)).setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.bg_dark)).setVisibility(View.GONE);
    }

    public void viewAllItems(View view){
        Intent intent = new Intent(getApplicationContext(), CheckoutItemsActivity.class);
        startActivity(intent);
    }

    public void toPhone(View view){
        Intent intent = new Intent(getApplicationContext(), ShippingAddressActivity.class);
        intent.putExtra("to", "phone");
        startActivity(intent);
    }

    public void toAddress(View view){
        Intent intent = new Intent(getApplicationContext(), ShippingAddressActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        onBackPressed();
    }

    private void getPhones() {
        progressBar.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.GONE);
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

    public void parseProductsResponse(String json) {
        progressBar.setVisibility(View.GONE);
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

                if(Commons.phones.size() > 0)phone.setText(Commons.phones.get(Commons.phoneId).getPhone_number());
                else {
                    if(Commons.thisUser != null)phone.setText(Commons.thisUser.get_phone_number());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAddresses() {
        progressBar.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.GONE);
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
        progressBar.setVisibility(View.GONE);
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

                if(Commons.thisUser == null){
                    if(Commons.addresses.size() > 0){
                        shippingFrame1.setVisibility(View.GONE);
                        shippingFrame2.setVisibility(View.VISIBLE);
                        address.setText(Commons.addresses.get(Commons.addrId).getAddress());
                        if(Commons.lang.equals("ar")){
                            addressLine.setText(Commons.addresses.get(Commons.addrId).getHouse() + "و " + Commons.addresses.get(Commons.addrId).getStreet() + "و " + Commons.addresses.get(Commons.addrId).getArea());
                        }else {
                            addressLine.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet() + ", " + Commons.addresses.get(Commons.addrId).getHouse());
                        }
                    }else {
                        shippingFrame1.setVisibility(View.VISIBLE);
                        shippingFrame2.setVisibility(View.GONE);
                    }
                }else if(Commons.thisUser != null && Commons.thisUser.get_address().length() == 0){
                    if(Commons.addresses.size() > 0){
                        shippingFrame1.setVisibility(View.GONE);
                        shippingFrame2.setVisibility(View.VISIBLE);
                        address.setText(Commons.addresses.get(Commons.addrId).getAddress());
                        if(Commons.lang.equals("ar")){
                            addressLine.setText(Commons.addresses.get(Commons.addrId).getHouse() + "و " + Commons.addresses.get(Commons.addrId).getStreet() + "و " + Commons.addresses.get(Commons.addrId).getArea());
                        }else {
                            addressLine.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet() + ", " + Commons.addresses.get(Commons.addrId).getHouse());
                        }
                    }else {
                        shippingFrame1.setVisibility(View.VISIBLE);
                        shippingFrame2.setVisibility(View.GONE);
                    }
                }
                else {
                    shippingFrame1.setVisibility(View.GONE);
                    shippingFrame2.setVisibility(View.VISIBLE);

                    if(Commons.addresses.size() > 0){
                        shippingFrame1.setVisibility(View.GONE);
                        shippingFrame2.setVisibility(View.VISIBLE);
                        address.setText(Commons.addresses.get(Commons.addrId).getAddress());
                        if(Commons.lang.equals("ar")){
                            addressLine.setText(Commons.addresses.get(Commons.addrId).getHouse() + "و " + Commons.addresses.get(Commons.addrId).getStreet() + "و " + Commons.addresses.get(Commons.addrId).getArea());
                        }else {
                            addressLine.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet() + ", " + Commons.addresses.get(Commons.addrId).getHouse());
                        }
                    }else {
                        address.setText(Commons.thisUser.get_address());
                        if(Commons.lang.equals("ar")){
                            addressLine.setText(Commons.thisUser.get_house() + "و " + Commons.thisUser.get_street() + "و " + Commons.thisUser.get_area());
                        }else {
                            addressLine.setText(Commons.thisUser.get_area() + ", " + Commons.thisUser.get_street() + ", " + Commons.thisUser.get_house());
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getPhones();
        getAddresses();
        if(Commons.thisUser != null){
            getCoupons();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.checkoutActivity = null;
    }

    public void confirmOrder(View view){

        if(phone.getText().length() == 0){
            showToast(getString(R.string.enter_phone));
            return;
        }

        if(shippingFrame1.getVisibility() == View.VISIBLE){
            showToast(getString(R.string.enter_address));
            return;
        }

        try {
            uploadOrder(createOrderItemJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    boolean companyF = false;

    public String createOrderItemJsonString()throws JSONException{

        orderItemStr = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (Commons.cartItems.size()>0){
            for(int i=0; i<Commons.cartItems.size(); i++){

                String userId = Commons.thisUser != null? String.valueOf(Commons.thisUser.get_idx()):"0";
                String producerId = String.valueOf(Commons.cartItems.get(i).getProducer_id());
                String imei = Commons.IMEI;
                String storeId = String.valueOf(Commons.cartItems.get(i).getStore_id());
                String storeName = Commons.cartItems.get(i).getStore_name();
                String storeARName = Commons.cartItems.get(i).getAr_store_name();
                String productId = String.valueOf(Commons.cartItems.get(i).getProduct_id());
                String productName = Commons.cartItems.get(i).getProduct_name();
                String productARName = Commons.cartItems.get(i).getAr_product_name();
                String category = Commons.cartItems.get(i).getCategory();
                String arCategory = Commons.cartItems.get(i).getAr_category();
                String price = df.format(Commons.cartItems.get(i).getPrice());
                String priceUnit = Commons.cartItems.get(i).getUnit();
                String quantity = String.valueOf(Commons.cartItems.get(i).getQuantity());
                String pictureUrl = Commons.cartItems.get(i).getPicture_url();
                String compriceId = String.valueOf(Commons.cartItems.get(i).getPrice_id());

                if(Commons.cartItems.get(i).getPrice_id() > 0)companyF = true;

                jsonObj = new JSONObject();

                try {
                    jsonObj.put("member_id",userId);
                    jsonObj.put("producer_id",producerId);
                    jsonObj.put("imei_id",imei);
                    jsonObj.put("store_id",storeId);
                    jsonObj.put("store_name",storeName);
                    jsonObj.put("ar_store_name",storeARName);
                    jsonObj.put("product_id",productId);
                    jsonObj.put("product_name",productName);
                    jsonObj.put("ar_product_name",productARName);
                    jsonObj.put("category",category);
                    jsonObj.put("ar_category",arCategory);
                    jsonObj.put("price",price);
                    jsonObj.put("unit",priceUnit);
                    jsonObj.put("quantity",quantity);
                    jsonObj.put("picture_url",pictureUrl);
                    jsonObj.put("comprice_id",compriceId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArr.put(jsonObj);
            }
            JSONObject scheduleObj = new JSONObject();
            scheduleObj.put("orderItems", jsonArr);
            orderItemStr = scheduleObj.toString();
            Log.d("ITEMSTR!!!", orderItemStr);
            return orderItemStr;
        }

        return orderItemStr;
    }

    public void uploadOrder(final String orderItemStr) {

        String url = ReqConst.SERVER_URL + "uploadOrder";

        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUploadOrderResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
                showToast(getString(R.string.server_issue));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("member_id", Commons.thisUser != null? String.valueOf(Commons.thisUser.get_idx()):"0");
                params.put("imei_id", Commons.IMEI);
                params.put("orderID", RandomStringUtils.randomAlphanumeric(12).toUpperCase());
                params.put("price", df.format(Commons.totalPrice));
                params.put("unit", "qr");
                params.put("shipping", String.valueOf(Constants.SHIPPING_PRICE));
                params.put("email", Commons.thisUser != null? Commons.thisUser.get_email():"");
                params.put("address", address.getText().toString().trim());
                params.put("address_line", addressLine.getText().toString().trim());
                params.put("phone_number", phone.getText().toString());
                params.put("coupon_id", String.valueOf(couponId));
                params.put("discount", String.valueOf(pDiscount));
                params.put("company", (companyF)? "naql":"");

                params.put("orderItems", orderItemStr);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseUploadOrderResponse(String json) {

        progressBar.setVisibility(View.GONE);

        Log.d("JsonAAA====",json);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {

                String orderID = response.getString("orderID");
                long orderTimestamp = Long.parseLong(response.getString("date_time"));

                showToast(getString(R.string.order_submited));

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                String date = dateFormat.format(new Date(orderTimestamp));

                Intent intent = new Intent(getApplicationContext(), OrderPlacedActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("order_date", date);
                startActivity(intent);
                finish();
            }else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast(getString(R.string.server_issue));
        }
    }

    private void getCoupons() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getCoupons";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetCouponsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
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

    public void parseGetCouponsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                coupons.clear();
                JSONArray dataArr = response.getJSONArray("availables");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Coupon coupon = new Coupon();
                    coupon.setId(object.getInt("id"));
                    coupon.setDiscount(object.getInt("discount"));
                    coupon.setExpireTime(object.getLong("expire_time"));
                    coupons.add(coupon);
                }

                if(coupons.isEmpty()){
                    ((TextView)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                }else ((TextView)findViewById(R.id.no_result)).setVisibility(View.GONE);

                couponListAdapter.setDatas(coupons);
                ((ListView)findViewById(R.id.list)).setAdapter(couponListAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}




















































