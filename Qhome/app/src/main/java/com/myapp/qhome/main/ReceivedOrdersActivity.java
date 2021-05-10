package com.myapp.qhome.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
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
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.OrderItemListAdapter;
import com.myapp.qhome.adapters.OrderListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Order;
import com.myapp.qhome.models.OrderItem;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class ReceivedOrdersActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    FrameLayout trackFrame;
    EditText ui_edtsearch;
    TextView title;
    ListView list;
    AVLoadingIndicatorView progressBar;
    OrderItem pOrderItem;
    ImageView[] nodes = {};
    View[] lines = {};
    String currentTab = "placed";

    public ArrayList<OrderItem> orderItems = new ArrayList<>();
    OrderItemListAdapter orderItemListAdapter = new OrderItemListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_orders);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        Commons.receivedOrdersActivity = this;

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        title = (TextView)findViewById(R.id.title);

        trackFrame = (FrameLayout)findViewById(R.id.trackFrame);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

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

        initTrackFrame();
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
    }

    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        ((View)findViewById(R.id.indicator_placed)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_confirmed)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_prepared)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_ready)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_delivered)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_placed)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_confirmed)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_prepared)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_ready)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_delivered)).setTypeface(normal);

        ((TextView)findViewById(R.id.txt_placed)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_confirmed)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_prepared)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_ready)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_delivered)).setTextColor(getColor(R.color.lightPrimary));
    }

    private void initTrackFrame(){
        nodes = new ImageView[]{
                ((ImageView) findViewById(R.id.img_placed)),
                ((ImageView)findViewById(R.id.img_confirmed)),
                ((ImageView)findViewById(R.id.img_prepared)),
                ((ImageView)findViewById(R.id.img_ready)),
                ((ImageView)findViewById(R.id.img_delivered))
        };

        lines = new View[]{
                ((View)findViewById(R.id.view_confirm)),
                ((View)findViewById(R.id.view_prepare)),
                ((View)findViewById(R.id.view_ready)),
                ((View)findViewById(R.id.view_delivery))
        };
    }

    private void appearTrackFrame(int sel){
        for(ImageView node:nodes)node.setImageResource(R.drawable.gray_circle);
        for(View line:lines)line.setBackgroundColor(getColor(R.color.gray));
        for(int i=0;i<nodes.length; i++){
            if(i <= sel){
                if(Commons.lang.equals("ar"))nodes[i].setImageResource(R.drawable.marroon_circle_flip);
                else nodes[i].setImageResource(R.drawable.marroon_circle);
            }
            if(i == 0)continue;
            else if(i <= sel)lines[i - 1].setBackgroundColor(getColor(R.color.colorPrimary));
        }
    }

    private void tabFocus(LinearLayout tabLayout){
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView)findViewById(R.id.hScrollView);
        int x = tabLayout.getLeft();
        int y = tabLayout.getTop();
        horizontalScrollView.scrollTo(x, y);
    }

    private void tabFocus2(LinearLayout tabLayout){
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView)findViewById(R.id.hScrollView);
        int x = tabLayout.getLeft() - 200;
        int y = tabLayout.getTop();
        horizontalScrollView.scrollTo(x, y);
    }

    public void selPlaced(View view){
        placeds();
    }

    private void placeds(){
        currentTab = "placed";
        clearIndicators();
        appearTrackFrame(Commons.orderStatus.statusIndex.get("placed"));
        ((View)findViewById(R.id.indicator_placed)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_placed)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_placed)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus2(((LinearLayout)findViewById(R.id.lyt_placed)));

        getItemsByStatus("placed");
    }

    public void selConfirmed(View view){
        selConfirmed();
    }

    private void selConfirmed(){
        currentTab = "confirmed";
        clearIndicators();
        appearTrackFrame(Commons.orderStatus.statusIndex.get("confirmed"));
        ((View)findViewById(R.id.indicator_confirmed)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_confirmed)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_confirmed)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus2(((LinearLayout)findViewById(R.id.lyt_confirmed)));

        getItemsByStatus("confirmed");
    }

    public void selPrepared(View view){
        selPrepared();
    }

    private void selPrepared(){
        currentTab = "prepared";
        clearIndicators();
        appearTrackFrame(Commons.orderStatus.statusIndex.get("prepared"));
        ((View)findViewById(R.id.indicator_prepared)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_prepared)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_prepared)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_prepared)));

        getItemsByStatus("prepared");
    }

    public void selReady(View view){
        selReady();
    }

    private void selReady(){
        currentTab = "ready";
        clearIndicators();
        appearTrackFrame(Commons.orderStatus.statusIndex.get("ready"));
        ((View)findViewById(R.id.indicator_ready)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_ready)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_ready)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_ready)));

        getItemsByStatus("ready");
    }

    public void selDelivered(View view){
        selDelivered();
    }

    private void selDelivered(){
        currentTab = "delivered";
        clearIndicators();
        appearTrackFrame(Commons.orderStatus.statusIndex.get("delivered"));
        ((View)findViewById(R.id.indicator_delivered)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_delivered)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_delivered)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_delivered)));

        getItemsByStatus("delivered");
    }

    private void getItemsByStatus(String status){
        ArrayList<OrderItem> items = new ArrayList<>();
        for(OrderItem item: orderItems){
            if(item.getStatus().equals(status))items.add(item);
        }
        orderItemListAdapter.setDatas(items);
        if(orderItemListAdapter.getCount() == 0){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
            trackFrame.setVisibility(View.GONE);
        }else {
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
            trackFrame.setVisibility(View.VISIBLE);
        }
        orderItemListAdapter.notifyDataSetChanged();
        list.setAdapter(orderItemListAdapter);

        if(items.size() > 0){
            if(status.equals("placed")){
                ((FrameLayout)findViewById(R.id.countFrame_placed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_placed)).setText(String.valueOf(items.size()));
            }else if(status.equals("confirmed")){
                ((FrameLayout)findViewById(R.id.countFrame_confirmed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_confirmed)).setText(String.valueOf(items.size()));
            }else if(status.equals("prepared")){
                ((FrameLayout)findViewById(R.id.countFrame_prepared)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_prepared)).setText(String.valueOf(items.size()));
            }else if(status.equals("ready")){
                ((FrameLayout)findViewById(R.id.countFrame_ready)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_ready)).setText(String.valueOf(items.size()));
            }else if(status.equals("delivered")){
                ((FrameLayout)findViewById(R.id.countFrame_delivered)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_delivered)).setText(String.valueOf(items.size()));
            }
        }
    }

    public void getOrderItems() {

        String url = ReqConst.SERVER_URL + "receivedOrderItems";
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
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

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

                    Log.d("STATUS!!!", item.getStatus());

                    orderItems.add(item);
                }

                getItemsByStatus(currentTab);

                getItemStatus("placed");
                getItemStatus("confirmed");
                getItemStatus("prepared");
                getItemStatus("ready");
                getItemStatus("delivered");

            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    private void getItemStatus(String status){

        ArrayList<OrderItem> items = new ArrayList<>();
        for(OrderItem item: orderItems){
            if(item.getStatus().equals(status))items.add(item);
        }

        if(items.size() > 0){
            if(status.equals("placed")){
                ((FrameLayout)findViewById(R.id.countFrame_placed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_placed)).setText(String.valueOf(items.size()));
            }else if(status.equals("confirmed")){
                ((FrameLayout)findViewById(R.id.countFrame_confirmed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_confirmed)).setText(String.valueOf(items.size()));
            }else if(status.equals("prepared")){
                ((FrameLayout)findViewById(R.id.countFrame_prepared)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_prepared)).setText(String.valueOf(items.size()));
            }else if(status.equals("ready")){
                ((FrameLayout)findViewById(R.id.countFrame_ready)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_ready)).setText(String.valueOf(items.size()));
            }else if(status.equals("delivered")){
                ((FrameLayout)findViewById(R.id.countFrame_delivered)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_delivered)).setText(String.valueOf(items.size()));
            }
        }else {
            if(status.equals("placed")){
                ((FrameLayout)findViewById(R.id.countFrame_placed)).setVisibility(View.GONE);
            }else if(status.equals("confirmed")){
                ((FrameLayout)findViewById(R.id.countFrame_confirmed)).setVisibility(View.GONE);
            }else if(status.equals("prepared")){
                ((FrameLayout)findViewById(R.id.countFrame_prepared)).setVisibility(View.GONE);
            }else if(status.equals("ready")){
                ((FrameLayout)findViewById(R.id.countFrame_ready)).setVisibility(View.GONE);
            }else if(status.equals("delivered")){
                ((FrameLayout)findViewById(R.id.countFrame_delivered)).setVisibility(View.GONE);
            }
        }
    }

    public void cancelOrderItem(OrderItem orderItem){
        pOrderItem = orderItem;
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
                onResume();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void progressOrderItem(OrderItem orderItem, String next){
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "progressOrderItem";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());
                parseProgressResponse(response);

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
                params.put("item_id", String.valueOf(orderItem.getId()));
                params.put("next", next);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseProgressResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

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

                    Log.d("STATUS!!!", item.getStatus());

                    orderItems.add(item);
                }

                if(response.getString("next").equals("confirmed"))selConfirmed();
                else if(response.getString("next").equals("prepared"))selPrepared();
                else if(response.getString("next").equals("ready"))selReady();
                else if(response.getString("next").equals("delivered"))selDelivered();

                getItemStatus("placed");
                getItemStatus("confirmed");
                getItemStatus("prepared");
                getItemStatus("ready");
                getItemStatus("delivered");

            }else if(success.equals("1")){
                showToast(getString(R.string.customer_canceled_order));
                onResume();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

                    Log.d("STATUS!!!", item.getStatus());

                    orderItems.add(item);
                }

                order.setItems(orderItems);

                Commons.order = order;
                Intent intent = new Intent(getApplicationContext(), ReceivedOrderDetailActivity.class);
                startActivity(intent);

            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    public void toHome(View view){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.receivedOrdersActivity = null;
    }
}












































