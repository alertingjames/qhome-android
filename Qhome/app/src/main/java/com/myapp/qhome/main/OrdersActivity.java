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
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Order;
import com.myapp.qhome.models.OrderItem;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class OrdersActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    FrameLayout trackFrame;
    EditText ui_edtsearch;
    TextView title;
    ListView list;
    AVLoadingIndicatorView progressBar;
    Order pOrder;
    ImageView[] nodes = {};
    View[] lines = {};

    public ArrayList<Order> orders = new ArrayList<>();
    OrderListAdapter orderListAdapter = new OrderListAdapter(this);

    public ArrayList<OrderItem> orderItems = new ArrayList<>();
    OrderItemListAdapter orderItemListAdapter = new OrderItemListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        Commons.ordersActivity = this;

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
                if(((View)findViewById(R.id.indicator_all)).getVisibility() == View.VISIBLE) orderListAdapter.filter(text);
                else orderItemListAdapter.filter(text);
            }
        });

        list = (ListView) findViewById(R.id.list);
        ((TextView)findViewById(R.id.caption1)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption2)).setTypeface(bold);

       setupUI(findViewById(R.id.activity), this);

       initTrackFrame();
       all();
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
    }

    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        ((View)findViewById(R.id.indicator_all)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_placed)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_confirmed)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_prepared)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_ready)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_delivered)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_all)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_placed)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_confirmed)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_prepared)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_ready)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_delivered)).setTypeface(normal);

        ((TextView)findViewById(R.id.txt_all)).setTextColor(getColor(R.color.lightPrimary));
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

    public void selAll(View view){
        all();
    }

    private void all(){
        clearIndicators();
        trackFrame.setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_all)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_all)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_all)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_all)));
        getOrders();
        getOrderItems();
    }

    public void selPlaced(View view){
        placeds();
    }

    private void placeds(){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("placed"));
        ((View)findViewById(R.id.indicator_placed)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_placed)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_placed)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus2(((LinearLayout)findViewById(R.id.lyt_placed)));

        getItemsByStatus("placed");
    }

    public void selConfirmed(View view){
        confirmeds();
    }

    private void confirmeds(){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("confirmed"));
        ((View)findViewById(R.id.indicator_confirmed)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_confirmed)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_confirmed)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus2(((LinearLayout)findViewById(R.id.lyt_confirmed)));

        getItemsByStatus("confirmed");
    }

    public void selPrepared(View view){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("prepared"));
        ((View)findViewById(R.id.indicator_prepared)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_prepared)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_prepared)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_prepared)));

        getItemsByStatus("prepared");
    }

    public void selReady(View view){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("ready"));
        ((View)findViewById(R.id.indicator_ready)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_ready)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_ready)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_ready)));

        getItemsByStatus("ready");
    }

    public void selDelivered(View view){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
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

    public void getOrders() {

        String url = ReqConst.SERVER_URL + "getUserOrders";
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
                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));
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
                orders.clear();
                JSONArray feedsArr = response.getJSONArray("data");
                for(int i=0; i<feedsArr.length(); i++) {
                    JSONObject object = (JSONObject) feedsArr.get(i);
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
                    orders.add(order);
                }

                orderListAdapter.setDatas(orders);
                if(orderListAdapter.getCount() == 0){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                orderListAdapter.notifyDataSetChanged();
                list.setAdapter(orderListAdapter);

            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    public void deleteOrder(Order order){
        pOrder = order;
        showAlertDialogForQuestion(getString(R.string.warning), getString(R.string.sure_cancel), this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                String url = ReqConst.SERVER_URL + "delOrder";
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
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("order_id", String.valueOf(order.getId()));
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
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                int index = orders.indexOf(pOrder);
                orders.remove(index);

                orderListAdapter.setDatas(orders);
                if(orderListAdapter.getCount() == 0){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                orderListAdapter.notifyDataSetChanged();
                list.setAdapter(orderListAdapter);

                getOrderItems();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getOrderItems() {

        String url = ReqConst.SERVER_URL + "userOrderItems";
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

    public void toCheckOut(Order order){
        Commons.cartItems.clear();
        for(OrderItem item: order.getItems()){
            CartItem cartItem = new CartItem();
            cartItem.setId(item.getId());
            cartItem.setImei_id(item.getImei_id());
            cartItem.setProducer_id(item.getProducer_id());
            cartItem.setStore_id(item.getStore_id());
            cartItem.setStore_name(item.getStore_name());
            cartItem.setAr_store_name(item.getAr_store_name());
            cartItem.setPicture_url(item.getPicture_url());
            cartItem.setCategory(item.getCategory());
            cartItem.setAr_category(item.getAr_category());
            cartItem.setProduct_id(item.getProduct_id());
            cartItem.setProduct_name(item.getProduct_name());
            cartItem.setAr_product_name(item.getAr_product_name());
            cartItem.setPrice(item.getPrice());
            cartItem.setUnit(item.getUnit());
            cartItem.setQuantity(item.getQuantity());

            Commons.cartItems.add(cartItem);
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.ordersActivity = null;
    }
}
















































