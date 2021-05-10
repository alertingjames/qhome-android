package com.myapp.qhome.main;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
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
import com.myapp.qhome.adapters.PictureListAdapter;
import com.myapp.qhome.adapters.ProductListAdapter;
import com.myapp.qhome.adapters.RatingListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.comparators.ProductChainedComparator;
import com.myapp.qhome.comparators.ProductNameComparator;
import com.myapp.qhome.comparators.ProductPriceComparator;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Picture;
import com.myapp.qhome.models.Product;
import com.myapp.qhome.models.Rating;
import com.myapp.qhome.models.Store;
import com.myapp.qhome.preference.PrefConst;
import com.myapp.qhome.preference.Preference;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StoreDetailActivity extends BaseActivity {

    TextView title, nameBox, descriptionBox, categoryBox, storeNameBox;
    EditText ui_edtsearch;
    RoundedImageView logo, storeLogoBox;
    AVLoadingIndicatorView progressBar;
    RatingBar ratingBar;
    TextView ratingBox;
    EditText subjectBox, feedbackBox;
    String lang = "";

    public ImageButton likeButton;
    public int productId = 0;

    ScrollView scrollView;

    boolean companyF = false;
    public int comDeliveryPriceId = 0;

    GridView productList;
    ArrayList<Product> products = new ArrayList<>();
    ProductListAdapter productListAdapter = new ProductListAdapter(this);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        Commons.storeDetailActivity = this;

        scrollView = (ScrollView)findViewById(R.id.scrollView) ;

        comDeliveryPriceId = Commons.store.getPriceId();

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        nameBox = (TextView) findViewById(R.id.storeNameBox);
        descriptionBox = (TextView)findViewById(R.id.descriptionBox);
        categoryBox = (TextView) findViewById(R.id.storeCategoryBox);
        logo = (RoundedImageView) findViewById(R.id.logo);

        storeLogoBox = (RoundedImageView) findViewById(R.id.store_logo);
        storeNameBox = (TextView) findViewById(R.id.store_name);

        title = (TextView)findViewById(R.id.title);
        if(Commons.lang.equals("ar"))title.setText(Commons.store.getAr_name());
        else title.setText(Commons.store.getName());
        title.setTypeface(bold);

        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        ratingBox = (TextView)findViewById(R.id.ratingBox);
        subjectBox = (EditText)findViewById(R.id.subjectBox);
        feedbackBox = (EditText)findViewById(R.id.feedbackBox);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rt, boolean fromUser) {
                Float ratingvalue = (Float) ratingBar.getRating();
                ratingBox.setText(String.valueOf(ratingvalue));
            }
        });

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(true);
        ui_edtsearch.requestFocus();

        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().trim().toLowerCase(Locale.getDefault());
                productListAdapter.filter(text);

            }
        });

        productList = (GridView)findViewById(R.id.list);
        if(Commons.store.getCategory2().length() == 0)((ImageView)findViewById(R.id.btn_filter)).setVisibility(View.GONE);
        else ((ImageView)findViewById(R.id.btn_filter)).setVisibility(View.VISIBLE);

        detail();

        setupUI(findViewById(R.id.activity), this);

        ((TextView)findViewById(R.id.caption)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
//        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);
//        ((TextView)findViewById(R.id.caption5)).setTypeface(normal);
//        ((TextView)findViewById(R.id.caption6)).setTypeface(normal);
//        ((TextView)findViewById(R.id.caption7)).setTypeface(normal);

        if(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_IMEI, "").length() == 0){
            Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_IMEI, Commons.homeActivity.getDeviceIMEI());
        }

        Commons.IMEI = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_IMEI, "");

        if(Commons.store.getPriceId() > 0)((ImageView)findViewById(R.id.naql)).setVisibility(View.VISIBLE);
        else ((ImageView)findViewById(R.id.naql)).setVisibility(View.GONE);

    }

    public void back(View view){
        onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        ((View)findViewById(R.id.indicator_detail)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_products)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_rate)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_detail)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_products)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_rate)).setTypeface(normal);

        ((TextView)findViewById(R.id.txt_detail)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_products)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_rate)).setTextColor(getColor(R.color.lightPrimary));

        ((FrameLayout)findViewById(R.id.detailFrame)).setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.productsFrame)).setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.rateFrame)).setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selDetail(View view){
        detail();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void detail(){
        clearIndicators();
        ((View)findViewById(R.id.indicator_detail)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_detail)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_detail)).setTextColor(getColor(R.color.colorPrimary));
        ((FrameLayout)findViewById(R.id.detailFrame)).setVisibility(View.VISIBLE);

        Picasso.with(getApplicationContext()).load(Commons.store.getLogo_url()).into(logo);

        if(Commons.lang.equals("ar")){
            nameBox.setText(Commons.store.getAr_name());
            String aCat2 = "";
            if(Commons.store.getAr_category2().length() > 0) aCat2 = Commons.store.getAr_category2() + "و ";
            categoryBox.setText(aCat2 + Commons.store.getAr_category());
            descriptionBox.setText(StringEscapeUtils.unescapeJava(Commons.store.getAr_description()));
        }else {
            nameBox.setText(Commons.store.getName());
            String cat2 = "";
            if(Commons.store.getCategory2().length() > 0) cat2 = ", " + Commons.store.getCategory2();
            categoryBox.setText(Commons.store.getCategory() + cat2);
            descriptionBox.setText(StringEscapeUtils.unescapeJava(Commons.store.getDescription()));
        }

        ((RatingBar)findViewById(R.id.ratingbar_profile)).setRating(Commons.store.getRatings());
        ((TextView)findViewById(R.id.ratings_profile)).setText(String.valueOf(Commons.store.getRatings()));
        ((TextView)findViewById(R.id.likes)).setText(String.valueOf(Commons.store.getLikes()));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selProducts(View view){
        clearIndicators();
        ((View)findViewById(R.id.indicator_products)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_products)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_products)).setTextColor(getColor(R.color.colorPrimary));

        ((FrameLayout)findViewById(R.id.productsFrame)).setVisibility(View.VISIBLE);
        getStoreProducts();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selRate(View view){

        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }

        clearIndicators();
        ((View)findViewById(R.id.indicator_rate)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_rate)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_rate)).setTextColor(getColor(R.color.colorPrimary));

        ((FrameLayout)findViewById(R.id.rateFrame)).setVisibility(View.VISIBLE);

        Picasso.with(getApplicationContext()).load(Commons.store.getLogo_url()).into(storeLogoBox);
        if(Commons.lang.equals("ar"))storeNameBox.setText(Commons.store.getAr_name());
        else storeNameBox.setText(Commons.store.getName());

        ((RatingBar)findViewById(R.id.ratingbar_small)).setRating(Commons.store.getRatings());
        ((TextView)findViewById(R.id.ratings)).setText(String.valueOf(Commons.store.getRatings()));
        ((TextView)findViewById(R.id.reviews)).setText(String.valueOf(Commons.store.getReviews()));
        ((TextView)findViewById(R.id.reviews_cap)).setTypeface(normal);

        getRatings();
    }

    private void getStoreProducts() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getProducts";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseStoreProductsResponse(response);
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

    public void parseStoreProductsResponse(String json) {
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

                    Log.d("Store IDs!!!", String.valueOf(product.getStoreId()) + "/" + String.valueOf(Commons.store.getIdx()));

                    if(product.getStoreId() == Commons.store.getIdx()){
                        if(product.getNew_price() == 0){
                            if(product.getPrice() >= Commons.minPriceVal && product.getPrice() <= Commons.maxPriceVal)
                                products.add(product);
                        }else {
                            if(product.getNew_price() >= Commons.minPriceVal && product.getNew_price() <= Commons.maxPriceVal)
                                products.add(product);
                        }
                    }
                }

                Collections.sort(products, new ProductChainedComparator(
                        new ProductNameComparator(),
                        new ProductPriceComparator()
                ));

                if(products.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                productListAdapter.setDatas(products);
                productList.setAdapter(productListAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void submitFeedback(View view){

        if(subjectBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.write_subject));
            return;
        }

        if(ratingBar.getRating() == 0){
            showToast(getString(R.string.please_rate_out_of_5_stars));
            return;
        }

        if(feedbackBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.write_feedback));
            return;
        }

        submitRating();
    }

    private void submitRating() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "placeStoreFeedback";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast(getString(R.string.server_issue));
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("store_id", String.valueOf(Commons.store.getIdx()));
                params.put("subject", subjectBox.getText().toString().trim());
                params.put("rating", String.valueOf(ratingBar.getRating()));
                params.put("description", feedbackBox.getText().toString().trim());
                if(lang.length() == 0)params.put("lang", Commons.lang);
                else params.put("lang", lang);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            if (success.equals("0")) {
                showToast(getString(R.string.feedback_submited));
                ((RatingBar)findViewById(R.id.ratingbar_small)).setRating(Float.parseFloat(response.getString("ratings")));
                ((TextView)findViewById(R.id.ratings)).setText(response.getString("ratings"));
                ((TextView)findViewById(R.id.reviews)).setText(response.getString("reviews"));
                ((TextView)findViewById(R.id.caption1)).setText(getString(R.string.update_feedback));
                Commons.store.setRatings(Float.parseFloat(response.getString("ratings")));
                Commons.store.setReviews(Integer.parseInt(response.getString("reviews")));
                lang = response.getString("lang");
            }
        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    private void getRatings() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getRatings";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRatingsResponse(response);
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
                params.put("store_id", String.valueOf(Commons.store.getIdx()));
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseRatingsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {

                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Rating rating = new Rating();
                    rating.setIdx(object.getInt("id"));
                    rating.setStoreId(object.getInt("store_id"));
                    rating.setUserId(object.getInt("member_id"));
                    rating.setUserName(object.getString("member_name"));
                    rating.setUserPictureUrl(object.getString("member_photo"));
                    rating.setRating(Float.parseFloat(object.getString("rating")));
                    rating.setSubject(object.getString("subject"));
                    rating.setDescription(object.getString("description"));
                    rating.setDate(object.getString("date_time"));
                    rating.setLang(object.getString("lang"));

                    if(rating.getStoreId() == Commons.store.getIdx() && rating.getUserId() == Commons.thisUser.get_idx()){
                        subjectBox.setText(rating.getSubject());
                        ratingBar.setRating(rating.getRating());
                        ratingBox.setText(String.valueOf(rating.getRating()));
                        feedbackBox.setText(rating.getDescription());
                        ((TextView)findViewById(R.id.caption1)).setText(getString(R.string.update_feedback));

                        if(rating.getLang().equals("ar")){
                            subjectBox.setGravity(Gravity.END);
                            feedbackBox.setGravity(Gravity.END);
                        }else {
                            subjectBox.setGravity(Gravity.START);
                            feedbackBox.setGravity(Gravity.START);
                        }

                        lang = rating.getLang();

                        return;
                    }

                    if(i == dataArr.length() - 1){
                        ((TextView)findViewById(R.id.caption1)).setText(getString(R.string.place_feedback));
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveProduct(String productId) {

        String url = ReqConst.SERVER_URL + "saveProduct";
        progressBar.setVisibility(View.VISIBLE);

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

    public void parseGetLikeStoreResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                if(likeButton != null){
                    likeButton.setBackgroundResource(R.drawable.ic_liked);
                    for(Product product: products){
                        if(product.getIdx() == productId){
                            product.setLiked(true);
                        }
                    }
                }
            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
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
                if(likeButton != null){
                    likeButton.setBackgroundResource(R.drawable.ic_like);
                    for(Product product: products){
                        if(product.getIdx() == productId){
                            product.setLiked(false);
                        }
                    }
                }
            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    public void openFilter(View view){
        String[] categoryItems = {};
        String[] arCategoryItems = {};
        if(Commons.store.getCategory2().length() == 0){
            categoryItems = new String[]{Commons.store.getCategory(), "All Products"};
            arCategoryItems = new String[]{Commons.store.getAr_category(), "جميع المنتجات"};
        }else {
            categoryItems = new String[]{Commons.store.getCategory(), Commons.store.getCategory2(), "All Products"};
            arCategoryItems = new String[]{Commons.store.getAr_category(), Commons.store.getAr_category2(), "جميع المنتجات"};
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(StoreDetailActivity.this);
        String[] finalCategoryItems = categoryItems;
        String[] finalArCategoryItems = arCategoryItems;
        mBuilder.setSingleChoiceItems(Commons.lang.equals("ar")?arCategoryItems:categoryItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == finalCategoryItems.length - 1){
                    getStoreProducts();
                }else {
                    if(Commons.lang.equals("ar"))filterProductsByCategory(finalArCategoryItems[i]);
                    else filterProductsByCategory(finalCategoryItems[i]);
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void filterProductsByCategory(String category){
        ArrayList<Product> productArrayList = new ArrayList<>();
        for(Product product:products){
            if(product.getCategory().equals(category) || product.getAr_category().equals(category))
                productArrayList.add(product);
        }
        if(productArrayList.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
        productListAdapter.setDatas(productArrayList);
        productListAdapter.notifyDataSetChanged();
        productList.setAdapter(productListAdapter);

    }

    public void toCart(View view){
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCart();
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

    public void toInstagram(View view){
        getProducerInstagram();
    }

    private void getProducerInstagram() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getInstagram";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetAdsResponse(response);
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
                params.put("member_id", String.valueOf(Commons.store.getUserId()));
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetAdsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :", success);
            if (success.equals("0")) {
                String username = response.getString("instagram");
                Uri uri = Uri.parse("http://instagram.com/_u/" + username);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + username)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}











































