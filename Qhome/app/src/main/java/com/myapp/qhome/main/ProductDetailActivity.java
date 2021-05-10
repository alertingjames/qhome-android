package com.myapp.qhome.main;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.github.mmin18.widget.RealtimeBlurView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.PictureListAdapter;
import com.myapp.qhome.adapters.ProductPictureListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Picture;
import com.myapp.qhome.models.Store;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ProductDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    AVLoadingIndicatorView progressBar;
    Toolbar toolbar;
    private int mMaxScrollSize;
    private boolean mIsImageHidden = false;
    private int count = 1;

    TextView priceBox, oldPriceBox, nameBox, descriptionBox, countBox, countBox2;
    FrameLayout countFrame, oldPriceFrame;

    int compriceid = 0;

    ViewPager pager;
    PageIndicatorView pageIndicatorView;
    ArrayList<Picture> pictures = new ArrayList<>();
    ProductPictureListAdapter adapter = new ProductPictureListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        try{
            String from = getIntent().getStringExtra("from");
            if(from.equals("mine"))((LinearLayout)findViewById(R.id.lyt_add_to_cart)).setVisibility(View.GONE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        try{
            compriceid = (int) getIntent().getIntExtra("compriceid", 0);
            Log.d("COMPRICE!!!", String.valueOf(compriceid));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.flexible_example_toolbar);
        if(Commons.lang.equals("ar")) toolbar.setTitle(Commons.store.getAr_name());
        else toolbar.setTitle(Commons.store.getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.flexible_example_appbar);

        if(Commons.lang.equals("ar")) setTitle(Commons.store.getAr_name());
        else setTitle(Commons.store.getName());

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        priceBox = (TextView)findViewById(R.id.priceBox);
        oldPriceBox = (TextView)findViewById(R.id.oldPriceBox);
        nameBox = (TextView)findViewById(R.id.nameBox);
        descriptionBox = (TextView)findViewById(R.id.descriptionBox);
        countBox = (TextView)findViewById(R.id.count);
        countBox2 = (TextView)findViewById(R.id.count2);
        countFrame = (FrameLayout)findViewById(R.id.countFrame);
        oldPriceFrame = (FrameLayout)findViewById(R.id.oldPriceFrame);

        ((TextView)findViewById(R.id.caption)).setTypeface(bold);
        priceBox.setTypeface(bold);
        nameBox.setTypeface(bold);
        oldPriceBox.setTypeface(normal);
        descriptionBox.setTypeface(normal);

        if(Commons.lang.equals("ar")) {
            if(Commons.product.getNew_price() == 0){
                oldPriceFrame.setVisibility(View.GONE);
                priceBox.setText(getString(R.string.qr) + " " + String.valueOf(Commons.product.getPrice()));
            }
            else {
                oldPriceFrame.setVisibility(View.VISIBLE);
                priceBox.setText(getString(R.string.qr) + " " + String.valueOf(Commons.product.getNew_price()));
                oldPriceBox.setText(getString(R.string.qr) + " " + String.valueOf(Commons.product.getPrice()));
            }

            nameBox.setText(Commons.product.getAr_name());
            descriptionBox.setText(Commons.product.getAr_description());
        }
        else {
            if(Commons.product.getNew_price() == 0){
                oldPriceFrame.setVisibility(View.GONE);
                priceBox.setText(String.valueOf(Commons.product.getPrice()) + " " + getString(R.string.qr));
            }
            else {
                oldPriceFrame.setVisibility(View.VISIBLE);
                priceBox.setText(String.valueOf(Commons.product.getNew_price()) + " " + getString(R.string.qr));
                oldPriceBox.setText(String.valueOf(Commons.product.getPrice()) + " " + getString(R.string.qr));
            }

            nameBox.setText(Commons.product.getName());
            descriptionBox.setText(Commons.product.getDescription());
        }

        pager = findViewById(R.id.viewPager);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);

        ((ImageView)findViewById(R.id.btn_decrease)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                if(count < 1) count = 1;
                ((TextView)findViewById(R.id.count2)).setText(String.valueOf(count));
            }
        });

        ((ImageView)findViewById(R.id.btn_increase)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                ((TextView)findViewById(R.id.count2)).setText(String.valueOf(count));
            }
        });

        ((FrameLayout)findViewById(R.id.btn_cart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCart();
            }
        });

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        getProductPictures(String.valueOf(Commons.product.getIdx()));
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
            }
        }else if (currentScrollPercentage <= 20) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ((RealtimeBlurView)findViewById(R.id.real_time_blur_view))
                        .animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .start();
                ((RealtimeBlurView)findViewById(R.id.real_time_blur_view)).setVisibility(View.GONE);
            }
        }
    }

    private void getProductPictures(String productId) {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getProductPictures";
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
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("product_id", productId);

                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseUploadBusinessResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                pictures.clear();
                JSONArray dataArr = response.getJSONArray("data");
                for (int i = 0; i < dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Picture picture = new Picture();
                    picture.setIdx(object.getInt("id"));
                    picture.setUrl(object.getString("picture_url"));
                    pictures.add(picture);
                }

                adapter.setDatas(pictures);
                adapter.notifyDataSetChanged();
                pager.setAdapter(adapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addToCart(View view){

        String price = "0";
        if(Commons.product.getNew_price() == 0){
            price = String.valueOf(Commons.product.getPrice());
        }
        else {
            price = String.valueOf(Commons.product.getNew_price());
        }

        addProductToCart(Commons.product.getPicture_url(),
                Commons.IMEI,
                String.valueOf(Commons.product.getUserId()),
                String.valueOf(Commons.store.getIdx()),
                Commons.store.getName(),
                Commons.store.getAr_name(),
                String.valueOf(Commons.product.getIdx()),
                Commons.product.getName(),
                Commons.product.getAr_name(),
                Commons.product.getCategory(),
                Commons.product.getAr_category(),
                price, "qr", String.valueOf(count));
    }

    private void addProductToCart(String pictureUrl, String imei, String producerId, String storeId, String stname, String astname, String productId,
                                 String pname, String apname, String category, String acategory, String price, String unit, String quantity) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "addToCart")
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
                .addMultipartParameter("comprice_id", String.valueOf(compriceid))
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
                                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0,0);
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

    private void toCart(){
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Commons.cartItems.size() > 0){
            countFrame.setVisibility(View.VISIBLE);
            countBox.setText(String.valueOf(Commons.cartItemsCount));
        }else countFrame.setVisibility(View.GONE);

        ((TextView)findViewById(R.id.count2)).setText("1");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
































