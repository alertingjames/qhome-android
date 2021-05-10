package com.myapp.qhome.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.gson.JsonObject;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CartItemListAdapter;
import com.myapp.qhome.adapters.CartWishlistAdapter;
import com.myapp.qhome.adapters.WishlistAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Product;
import com.myapp.qhome.models.Store;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends BaseActivity {

    AVLoadingIndicatorView progressBar;
    ListView list;
    TextView totalBox;
    LinearLayout totalFrame;
    ArrayList<CartItem> cartItems = new ArrayList<>();
    ArrayList<Product> products = new ArrayList<>();
    CartItemListAdapter cartItemListAdapter = new CartItemListAdapter(this);
    CartWishlistAdapter wishlistAdapter = new CartWishlistAdapter(this);
    CartItem pCartItem = null;
    ImageView button = null;
    Product pProduct;
    TextView caption1, caption2;

    double total = 0.0d;
    public static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        list = (ListView)findViewById(R.id.list);
        totalFrame = (LinearLayout)findViewById(R.id.totalFrame);

        caption1 = (TextView)findViewById(R.id.caption1);
        caption2 = (TextView)findViewById(R.id.caption2);

        caption1.setTypeface(bold);
        caption2.setTypeface(bold);

        totalBox = ((TextView)findViewById(R.id.total_price));
    }

    public void back(View view){
        onBackPressed();
    }

    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        ((View)findViewById(R.id.indicator_cart)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_wishlist)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_cart)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_wishlist)).setTypeface(normal);
    }

    public void selCart(View view){
        if(((View)findViewById(R.id.indicator_cart)).getVisibility() == View.VISIBLE)return;
        cart();
    }

    private void cart(){
        clearIndicators();
        ((View)findViewById(R.id.indicator_cart)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_cart)).setTypeface(bold);
        getCart();
    }

    public void selWishlist(View view){
        if(((View)findViewById(R.id.indicator_wishlist)).getVisibility() == View.VISIBLE)return;
        wishlist();
    }

    private void wishlist(){
        clearIndicators();
        ((View)findViewById(R.id.indicator_wishlist)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_wishlist)).setTypeface(bold);
        totalFrame.setVisibility(View.GONE);

        if(products.isEmpty()){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
            caption1.setText(getString(R.string.wishlist_empty));
            caption1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_empty_wishlist, 0, 0);
        }
        else {
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
        }

        wishlistAdapter.setDatas(products);
        list.setAdapter(wishlistAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        cart();
        getWishlistProducts();
    }

    private void getCart() {
        progressBar.setVisibility(View.VISIBLE);
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

    public void parseGetCartResponse(String json) {
        progressBar.setVisibility(View.GONE);
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

                    cartItem.setPrice_id(Integer.parseInt(!object.getString("price_id").equals("")? object.getString("price_id"):"0"));

                    Commons.cartItems.add(cartItem);
                    Commons.cartItemsCount += cartItem.getQuantity();
                }

                if(Commons.cartItems.isEmpty()){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                    caption1.setText(getString(R.string.cart_empty));
                    caption1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_empty_cart, 0, 0);
                    totalFrame.setVisibility(View.GONE);
                }
                else {
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                    totalFrame.setVisibility(View.VISIBLE);
                }

                if(Commons.cartItems.size() > 0){
                    ((TextView)findViewById(R.id.txt_cart)).setText(getString(R.string.cart) + " (" + String.valueOf(Commons.cartItemsCount) + ")");
                }else {
                    ((TextView)findViewById(R.id.txt_cart)).setText(getString(R.string.cart));
                }

                refreshTotal();

                cartItemListAdapter.setDatas(Commons.cartItems);
                list.setAdapter(cartItemListAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshTotal(){
        total = 0.0d;
        for(int i=0; i<Commons.cartItems.size(); i++){
            total += Commons.cartItems.get(i).getPrice() * Commons.cartItems.get(i).getQuantity();
        }
        totalBox.setText(getString(R.string.total) + " " + df.format(total) + " " + getString(R.string.qr));
    }

    public void deleteCartItem(CartItem cartItem){
        pCartItem = cartItem;
        delCartItem(String.valueOf(cartItem.getId()));
    }

    public void delCartItem(String itemId) {

        String url = ReqConst.SERVER_URL + "delCartItem";
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
                params.put("item_id", itemId);
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
                int index = Commons.cartItems.indexOf(pCartItem);
                Commons.cartItems.remove(index);
                refreshCart();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addToWishlist(CartItem cartItem){
        pCartItem = cartItem;
        addCartToWishlist(cartItem);
    }

    private void addCartToWishlist(CartItem item) {

        String url = ReqConst.SERVER_URL + "addCartToWishlist";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());
                parseGetLikeStoreResponse(response);
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
                params.put("item_id", String.valueOf(item.getId()));
                params.put("imei_id", Commons.IMEI);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetLikeStoreResponse(String json) {

        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                int index = Commons.cartItems.indexOf(pCartItem);
                Commons.cartItems.remove(index);
                refreshCart();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void unsaveProduct(String productId) {

        String url = ReqConst.SERVER_URL + "unsaveProduct";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetUnlikeStoreResponse(response);

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
                params.put("product_id", productId);
                params.put("imei_id", Commons.IMEI);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetUnlikeStoreResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    public void updateCartItem(ImageView btn, CartItem cartItem, String quantity){
        pCartItem = cartItem;
        button = btn;
        updateCartItemQuantity(String.valueOf(cartItem.getId()), quantity);
    }

    private void updateCartItemQuantity(String itemId, String quantity) {
        AndroidNetworking.upload(ReqConst.SERVER_URL + "updateCartItemQuantity")
                .addMultipartParameter("item_id", itemId)
                .addMultipartParameter("quantity", quantity)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.d("UPLOADED!!!", String.valueOf(bytesUploaded) + "/" + String.valueOf(totalBytes));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                button.setVisibility(View.GONE);
                                pCartItem.setQuantity(Integer.parseInt(quantity));
                                Commons.cartItemsCount = 0;
                                for(CartItem cartItem:Commons.cartItems){
                                    if(cartItem.getId() == pCartItem.getId())cartItem.setQuantity(Integer.parseInt(quantity));
                                    Commons.cartItemsCount += cartItem.getQuantity();
                                }
                                if(Commons.cartItems.size() > 0){
                                    if(Commons.lang.equals("ar")){
                                        ((TextView)findViewById(R.id.txt_cart)).setText("(" + String.valueOf(Commons.cartItemsCount) + ") " + getString(R.string.cart));
                                    }else {
                                        ((TextView)findViewById(R.id.txt_cart)).setText(getString(R.string.cart) + " (" + String.valueOf(Commons.cartItemsCount) + ")");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                    }
                });
    }

    private void refreshCart(){

        if(Commons.cartItems.isEmpty()){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
            caption1.setText(getString(R.string.cart_empty));
            caption1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_empty_cart, 0, 0);
            totalFrame.setVisibility(View.GONE);
        }
        else {
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
            totalFrame.setVisibility(View.VISIBLE);
        }

        Commons.cartItemsCount = 0;
        for(CartItem cartItem:Commons.cartItems){
            Commons.cartItemsCount += cartItem.getQuantity();
        }

        if(Commons.cartItems.size() > 0){
            if(Commons.lang.equals("ar")){
                ((TextView)findViewById(R.id.txt_cart)).setText("(" + String.valueOf(Commons.cartItemsCount) + ") " + getString(R.string.cart));
            }else {
                ((TextView)findViewById(R.id.txt_cart)).setText(getString(R.string.cart) + " (" + String.valueOf(Commons.cartItemsCount) + ")");
            }
        }else {
            ((TextView)findViewById(R.id.txt_cart)).setText(getString(R.string.cart));
        }

        refreshTotal();

        cartItemListAdapter.setDatas(Commons.cartItems);
        list.setAdapter(cartItemListAdapter);

        getWishlistProducts();
    }

    String option = "";

    public void getProductInfo(CartItem cartItem){
        option = "cart";
        getProduct(String.valueOf(cartItem.getProduct_id()));
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
                if(option.equals("cart"))intent.putExtra("from", "cart");
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.fade_off);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

                if(products.size() > 0){
                    ((TextView)findViewById(R.id.txt_wishlist)).setText(getString(R.string.wishlist) + " (" + String.valueOf(products.size()) + ")");
                }else {
                    ((TextView)findViewById(R.id.txt_wishlist)).setText(getString(R.string.wishlist));
                }
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
                parseDelResponse(response);

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

    public void parseDelResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                int index = products.indexOf(pProduct);
                products.remove(index);

                if(products.size() > 0){
                    if(Commons.lang.equals("ar")){
                        ((TextView)findViewById(R.id.txt_wishlist)).setText("(" + String.valueOf(products.size()) + ") " + getString(R.string.wishlist));
                    }else {
                        ((TextView)findViewById(R.id.txt_wishlist)).setText(getString(R.string.wishlist) + " (" + String.valueOf(products.size()) + ")");
                    }
                }else {
                    ((TextView)findViewById(R.id.txt_wishlist)).setText(getString(R.string.wishlist));
                }

                if(products.isEmpty()){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                    caption1.setText(getString(R.string.wishlist_empty));
                    caption1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_empty_wishlist, 0, 0);
                }
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                wishlistAdapter.setDatas(products);
                list.setAdapter(wishlistAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addToCart(Product product){

        String price = "0";
        if(product.getNew_price() == 0){
            price = String.valueOf(product.getPrice());
        }
        else {
            price = String.valueOf(product.getNew_price());
        }

        int count = 1;

        addProductToCart(product.getPicture_url(),
                Commons.IMEI,
                String.valueOf(product.getUserId()),
                String.valueOf(product.getStoreId()),
                product.getStore_name(),
                product.getAr_store_name(),
                String.valueOf(product.getIdx()),
                product.getName(),
                product.getAr_name(),
                product.getCategory(),
                product.getAr_category(),
                price,
                "qr",
                String.valueOf(count),
                0);
    }

    private void addProductToCart(String pictureUrl, String imei, String producerId, String storeId, String stname, String astname, String productId,
                                  String pname, String apname, String category, String acategory, String price, String unit, String quantity, int comprice_id) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "addWishlistToCart")
                .addMultipartParameter("picture_url", pictureUrl)
                .addMultipartParameter("imei_id", imei)
                .addMultipartParameter("producer_id", producerId)
                .addMultipartParameter("store_id", storeId)
                .addMultipartParameter("store_name", stname)
                .addMultipartParameter("ar_store_name", astname)
                .addMultipartParameter("product_id", productId)
                .addMultipartParameter("product_name", pname)
                .addMultipartParameter("ar_product_name", apname)
                .addMultipartParameter("category", category)
                .addMultipartParameter("ar_category", acategory)
                .addMultipartParameter("price", price)
                .addMultipartParameter("unit", unit)
                .addMultipartParameter("quantity", quantity)
                .addMultipartParameter("comprice_id", String.valueOf(comprice_id))
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.d("UPLOADED!!!", String.valueOf(bytesUploaded) + "/" + String.valueOf(totalBytes));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                cart();
                                getWishlistProducts();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void checkOut(View view){
        Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
        startActivity(intent);
    }

    public void toHome(View view){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
        finish();
    }

}




























