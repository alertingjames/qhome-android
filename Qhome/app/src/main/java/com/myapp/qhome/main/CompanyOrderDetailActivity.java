package com.myapp.qhome.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.OrderItem;
import com.myapp.qhome.models.Product;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class CompanyOrderDetailActivity extends BaseActivity {

    TextView title, orderIDBox, orderDateBox, phoneBox, phoneBox2, addressBox, addressLineBox, subTotalBox, shippingBox, totalBox, bonusBox;
    LinearLayout bonusFrame;
    RoundedImageView pictureBox;
    TextView productNameBox, storeNameBox, categoryBox, quantityBox, statusBox;
    AVLoadingIndicatorView progressBar;
    public static DecimalFormat df = new DecimalFormat("0.00");
    ImageView[] nodes = {};
    View[] lines = {};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_order_detail);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);

        ((TextView)findViewById(R.id.caption5)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption6)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption7)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption8)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption9)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption10)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption11)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption12)).setTypeface(bold);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        title = (TextView)findViewById(R.id.title);

        orderIDBox = (TextView)findViewById(R.id.order_number);
        orderDateBox = (TextView)findViewById(R.id.order_date);
        phoneBox = (TextView)findViewById(R.id.phone);
        phoneBox2 = (TextView)findViewById(R.id.phone2);
        addressBox = (TextView)findViewById(R.id.address);
        addressLineBox = (TextView)findViewById(R.id.address2);
        subTotalBox = (TextView)findViewById(R.id.subtotal_price);
        shippingBox = (TextView)findViewById(R.id.shipping_price);
        totalBox = (TextView)findViewById(R.id.total_price);

        bonusFrame = (LinearLayout)findViewById(R.id.bonusFrame);
        bonusBox = (TextView)findViewById(R.id.bonus);

        if(Commons.orderItem.getDiscount() > 0)bonusFrame.setVisibility(View.VISIBLE);

        productNameBox = (TextView)findViewById(R.id.productNameBox);
        storeNameBox = (TextView)findViewById(R.id.storeNameBox);
        categoryBox = (TextView)findViewById(R.id.categoryBox);
        quantityBox = (TextView)findViewById(R.id.quantityBox);
        statusBox = (TextView)findViewById(R.id.statusBox);

        pictureBox = (RoundedImageView)findViewById(R.id.pictureBox);

        Picasso.with(getApplicationContext()).load(Commons.orderItem.getPicture_url()).into(pictureBox);

        if(Commons.lang.equals("ar")){
            productNameBox.setText(Commons.orderItem.getAr_product_name());
            storeNameBox.setText(Commons.orderItem.getAr_store_name());
            categoryBox.setText(Commons.orderItem.getAr_category());
            quantityBox.setText(String.valueOf(Commons.orderItem.getQuantity()) + " " + getString(R.string.x));
        }else {
            productNameBox.setText(Commons.orderItem.getProduct_name());
            storeNameBox.setText(Commons.orderItem.getStore_name());
            categoryBox.setText(Commons.orderItem.getCategory());
            quantityBox.setText(getString(R.string.x) + " " + String.valueOf(Commons.orderItem.getQuantity()));
        }

        statusBox.setText(Commons.orderStatus.statusStr.get(Commons.orderItem.getStatus()));

        orderIDBox.setText(Commons.order.getOrderID());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(Commons.order.getDate())));
        orderDateBox.setText(date);
        phoneBox.setText(Commons.orderItem.getProducer_contact());
        phoneBox2.setText(Commons.order.getPhone_number());
        addressBox.setText(Commons.order.getAddress());
        addressLineBox.setText(Commons.order.getAddress_line());

        double bonus = (Commons.orderItem.getPrice() * Commons.orderItem.getQuantity()) * Commons.orderItem.getDiscount()/100;
        if(Commons.lang.equals("ar")){
            subTotalBox.setText(getString(R.string.qr) + " "  + df.format(Commons.orderItem.getPrice() * Commons.orderItem.getQuantity() + bonus));
            bonusBox.setText(getString(R.string.qr) + " "  + df.format(bonus) + "-");
            shippingBox.setText(getString(R.string.qr) + " "  + String.valueOf(Constants.SHIPPING_PRICE));
            totalBox.setText(getString(R.string.qr) + " "  + df.format(Commons.orderItem.getPrice() * Commons.orderItem.getQuantity()
                    + Commons.order.getShipping() * Commons.orderItem.getQuantity()/Commons.order.getItems().size()));
        }else {
            subTotalBox.setText(df.format(Commons.orderItem.getPrice() * Commons.orderItem.getQuantity() + bonus) + " " + getString(R.string.qr));
            bonusBox.setText("-" + df.format(bonus) + " " + getString(R.string.qr));
            shippingBox.setText(String.valueOf(Constants.SHIPPING_PRICE) + " " + getString(R.string.qr));
            totalBox.setText(df.format(Commons.orderItem.getPrice() * Commons.orderItem.getQuantity()
                    + Commons.order.getShipping() * Commons.orderItem.getQuantity()/Commons.order.getItems().size()) + " " + getString(R.string.qr));
        }

        setupUI(findViewById(R.id.activity), this);

    }

    public void contact(View view){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Commons.orderItem.getProducer_contact()));
        startActivity(intent);
    }

    public void contact2(View view){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Commons.order.getPhone_number()));
        startActivity(intent);
    }

    public void back(View view){
        onBackPressed();
    }

    public void cancelItem(View view){
        cancelOrderItem(Commons.orderItem);
    }

    private void cancelOrderItem(OrderItem orderItem){
        showAlertDialogForQuestion(getString(R.string.warning), getString(R.string.sure_cancel), this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                String url = ReqConst.SERVER_URL + "cancelOrderItem";
                progressBar.setVisibility(View.VISIBLE);
                StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("REST response========>", response);
                        VolleyLog.v("Response:%n %s", response.toString());
                        parseDeleteResponse(response);

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
                        params.put("item_id", String.valueOf(orderItem.getId()));
                        return params;
                    }
                };

                post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                        0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                QhomeApp.getInstance().addToRequestQueue(post, url);

                return null;
            }
        });
    }

    public void parseDeleteResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                showToast(getString(R.string.canceled));
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getProduct(View view){
        getProductInfo(Commons.orderItem);
    }

    private void getProductInfo(OrderItem orderItem){
        getProduct(String.valueOf(orderItem.getProduct_id()));
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
                intent.putExtra("from", "order");
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.fade_off);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}








































