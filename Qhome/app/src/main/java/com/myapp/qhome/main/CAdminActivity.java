package com.myapp.qhome.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CAdminOrderItemListAdapter;
import com.myapp.qhome.adapters.OrderItemListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Order;
import com.myapp.qhome.models.OrderItem;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class CAdminActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    ListView list;
    AVLoadingIndicatorView progressBar;
    LinearLayout notiFrame, notiLayout;

    public ArrayList<OrderItem> orderItems = new ArrayList<>();
    CAdminOrderItemListAdapter orderItemListAdapter = new CAdminOrderItemListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadmin);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        Commons.cAdminActivity = this;

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        title = (TextView)findViewById(R.id.title);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        notiFrame = (LinearLayout)findViewById(R.id.notiFrame);
        notiLayout = (LinearLayout)findViewById(R.id.notiLayout);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(true);
        ui_edtsearch.requestFocus();

        title.setTypeface(bold);
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
                orderItemListAdapter.filter(text);
            }
        });

        list = (ListView) findViewById(R.id.list);

        ((TextView)findViewById(R.id.caption1)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption2)).setTypeface(bold);

        setupUI(findViewById(R.id.activity), this);

    }

    public void search(View view){
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
    }

    public void cancelSearch(View view){
        cancelButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        getOrderItems();
        if(notiLayout.getChildCount() > 0) notiLayout.removeAllViews();
        getAdminNotification();
    }

    public void showNotiFrame(View view){
        if(notiLayout.getChildCount() > 0){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_in);
            notiFrame.setAnimation(animation);
            notiFrame.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((View)findViewById(R.id.notiBackground)).setVisibility(View.VISIBLE);
                }
            }, 200);
        }
    }

    public void getOrderItems() {

        String url = ReqConst.SERVER_URL + "comOrderItems";
        progressBar.setVisibility(View.VISIBLE);

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
                progressBar.setVisibility(View.GONE);
                showToast(getString(R.string.server_issue));
            }
        }) {

        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetOrderItemsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("CADMINORDERS :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                orderItems.clear();
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

                    item.setCompriceStr(obj.getString("comprice"));
                    item.setProducer_contact(obj.getString("producer_contact"));

                    Log.d("STATUS!!!", item.getStatus());

                    orderItems.add(item);
                }

                orderItemListAdapter.setDatas(orderItems);

                if(orderItemListAdapter.getCount() == 0){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                }else {
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                }

                orderItemListAdapter.notifyDataSetChanged();
                list.setAdapter(orderItemListAdapter);

            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }


    private void getAdminNotification(){

        Firebase ref;
        ref = new Firebase(ReqConst.FIREBASE_URL + "company");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                try{
                    LayoutInflater inflater = getLayoutInflater();
                    View myLayout = inflater.inflate(R.layout.notification_layout, null);
                    String noti = map.get("msg").toString();
                    String time = map.get("date").toString();
                    String fromid = map.get("id").toString();
                    String fromname = map.get("name").toString();
                    String fromphone = map.get("phone").toString();
                    String fromaddress = map.get("address").toString();
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING,200);

                    noti = noti + "\n" + "Customer ID: " + fromid + "\n" + "Customer Name: " + fromname + "\n" + "Phone: " + fromphone + "\n" + "Address: " + fromaddress;

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String date = dateFormat.format(new Date(Long.parseLong(time)));
                    ((TextView)myLayout.findViewById(R.id.date)).setText(date);
                    ((TextView)myLayout.findViewById(R.id.name)).setText("Qhome");
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    ((TextView)myLayout.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });

                    ((ImageView)myLayout.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });
                    notiLayout.addView(myLayout);
                    ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                    ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void dismissNotiFrame(){
        if(notiFrame.getVisibility() == View.VISIBLE){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_out);
            notiFrame.setAnimation(animation);
            notiFrame.setVisibility(View.GONE);
            ((View)findViewById(R.id.notiBackground)).setVisibility(View.GONE);
        }
    }

    public void dismissNotiFrame(View view){
        dismissNotiFrame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.cAdminActivity = null;
    }

    public void getOrder(OrderItem orderItem){

        String url = ReqConst.SERVER_URL + "orderById";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetOrdersResponse(response);

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
                params.put("order_id", String.valueOf(orderItem.getOrder_id()));
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetOrdersResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                JSONObject object = response.getJSONObject("data");
                Order order = new Order();
                order.setId(object.getInt("id"));
                order.setUser_id(object.getInt("member_id"));
                order.setImei_id(object.getString("imei_id"));
                order.setOrderID(object.getString("orderID"));
                order.setPrice(Double.parseDouble(object.getString("price")));
                order.setUnit(object.getString("unit"));
                order.setShipping(Double.parseDouble(object.getString("shipping")));
                order.setDate(object.getString("date_time"));
                order.setEmail(object.getString("email"));
                order.setAddress(object.getString("address"));
                order.setAddress_line(object.getString("address_line"));
                order.setPhone_number(object.getString("phone_number"));
                order.setStatus(object.getString("status"));
                order.setDiscount(object.getInt("discount"));
                order.setCompany(object.getString("company"));

                ArrayList<OrderItem> orderItems = new ArrayList<>();
                JSONArray objArr = object.getJSONArray("items");
                for(int j=0; j<objArr.length(); j++) {
                    JSONObject obj = (JSONObject) objArr.get(j);
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
                    item.setContact(obj.getString("contact"));
                    item.setDiscount(obj.getInt("discount"));
                    item.setCompriceStr(obj.getString("comprice"));

                    Log.d("STATUS!!!", item.getStatus());

                    orderItems.add(item);
                }

                order.setItems(orderItems);

                Commons.order = order;
                Intent intent = new Intent(getApplicationContext(), CompanyOrderDetailActivity.class);
                startActivity(intent);

            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

}



































