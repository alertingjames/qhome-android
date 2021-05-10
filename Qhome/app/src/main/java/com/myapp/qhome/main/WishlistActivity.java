package com.myapp.qhome.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CartItemListAdapter;
import com.myapp.qhome.adapters.WishlistAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Product;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WishlistActivity extends BaseActivity {

    AVLoadingIndicatorView progressBar;
    GridView list;
    ArrayList<Product> products = new ArrayList<>();
    WishlistAdapter wishlistAdapter = new WishlistAdapter(this);

    Product pProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        list = (GridView)findViewById(R.id.list);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
    }

    @Override
    public void onResume() {
        super.onResume();
        getWishlistProducts();
        getCart();
    }

    private void getWishlistProducts() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getSavedProducts";
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
                products.clear();
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Product product = new Product();
                    product.setIdx(object.getInt("id"));
                    product.setStoreId(object.getInt("store_id"));
                    product.setUserId(object.getInt("member_id"));
                    product.setName(object.getString("name"));
                    product.setPicture_url(object.getString("picture_url"));
                    product.setCategory(object.getString("category"));
                    product.setPrice(Double.parseDouble(object.getString("price")));
                    product.setNew_price(Double.parseDouble(object.getString("new_price")));
                    product.setUnit(object.getString("unit"));
                    product.setDescription(object.getString("description"));
                    product.setRegistered_time(object.getString("registered_time"));
                    product.setStatus(object.getString("status"));
                    if(object.getString("isLiked").equals("yes"))
                        product.setLiked(true);
                    else if(object.getString("isLiked").equals("no"))
                        product.setLiked(false);
                    else if(object.getString("isLiked").length() == 0)
                        product.setLiked(false);

                    product.setAr_name(object.getString("ar_name"));
                    product.setAr_category(object.getString("ar_category"));
                    product.setAr_description(object.getString("ar_description"));

                    product.setStore_name(object.getString("store_name"));
                    product.setAr_store_name(object.getString("ar_store_name"));

                    products.add(product);
                }

                if(products.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                wishlistAdapter.setDatas(products);
                list.setAdapter(wishlistAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getWishlistProductInfo(Product product){
        getProduct(String.valueOf(product.getIdx()));
    }

    private void getProduct(String productId) {
        String url = ReqConst.SERVER_URL + "productInfo";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUploadBusinessResponse(response);
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
                params.put("product_id", productId);
                params.put("imei_id", Commons.IMEI);
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseUploadBusinessResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                JSONObject object = response.getJSONObject("data");
                Product product = new Product();
                product.setIdx(object.getInt("id"));
                product.setStoreId(object.getInt("store_id"));
                product.setUserId(object.getInt("member_id"));
                product.setName(object.getString("name"));
                product.setPicture_url(object.getString("picture_url"));
                product.setCategory(object.getString("category"));
                product.setPrice(Double.parseDouble(object.getString("price")));
                product.setNew_price(Double.parseDouble(object.getString("new_price")));
                product.setUnit(object.getString("unit"));
                product.setDescription(object.getString("description"));
                product.setRegistered_time(object.getString("registered_time"));
                product.setStatus(object.getString("status"));
                if(object.getString("isLiked").equals("yes"))
                    product.setLiked(true);
                else if(object.getString("isLiked").equals("no"))
                    product.setLiked(false);
                else if(object.getString("isLiked").length() == 0)
                    product.setLiked(false);

                product.setAr_name(object.getString("ar_name"));
                product.setAr_category(object.getString("ar_category"));
                product.setAr_description(object.getString("ar_description"));

                product.setStore_name(object.getString("store_name"));
                product.setAr_store_name(object.getString("ar_store_name"));

                Commons.cartProduct = product;

                Intent intent = new Intent(getApplicationContext(), PDetailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.fade_off);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void toCart(View view){
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(intent);
    }

    private void getCart() {
        String url = ReqConst.SERVER_URL + "getCartItems";
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
                Commons.cartItems.clear();
                Commons.cartItemsCount = 0;
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    CartItem cartItem = new CartItem();
                    cartItem.setId(object.getInt("id"));
                    cartItem.setImei_id(object.getString("imei_id"));
                    cartItem.setProducer_id(object.getInt("producer_id"));
                    cartItem.setStore_id(object.getInt("store_id"));
                    cartItem.setStore_name(object.getString("store_name"));
                    cartItem.setAr_store_name(object.getString("ar_store_name"));
                    cartItem.setPicture_url(object.getString("picture_url"));
                    cartItem.setCategory(object.getString("category"));
                    cartItem.setAr_category(object.getString("ar_category"));
                    cartItem.setProduct_id(object.getInt("product_id"));
                    cartItem.setProduct_name(object.getString("product_name"));
                    cartItem.setAr_product_name(object.getString("ar_product_name"));
                    cartItem.setPrice(Double.parseDouble(object.getString("price")));
                    cartItem.setUnit(object.getString("unit"));
                    cartItem.setQuantity(Integer.parseInt(object.getString("quantity")));

                    Commons.cartItems.add(cartItem);
                    Commons.cartItemsCount += cartItem.getQuantity();
                }

                if(Commons.cartItems.size() > 0){
                    ((FrameLayout)findViewById(R.id.countFrame)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count)).setText(String.valueOf(Commons.cartItemsCount));
                }else ((FrameLayout)findViewById(R.id.countFrame)).setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(Product product){
        pProduct = product;
        delProduct(String.valueOf(product.getIdx()));
    }

    public void delProduct(String productId) {

        String url = ReqConst.SERVER_URL + "unsaveProduct";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());
                parseDelItemResponse(response);

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
                params.put("product_id", productId);
                params.put("imei_id", Commons.IMEI);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseDelItemResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                int index = products.indexOf(pProduct);
                products.remove(index);
                if(products.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                wishlistAdapter.setDatas(products);
                list.setAdapter(wishlistAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        onBackPressed();
    }

}

































