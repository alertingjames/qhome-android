package com.myapp.qhome.main;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CouponListAdapter;
import com.myapp.qhome.adapters.CouponsAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Coupon;
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

public class CouponListActivity extends BaseActivity {

    TextView title;
    ListView list;
    AVLoadingIndicatorView progressBar;

    ArrayList<Coupon> coupons = new ArrayList<>();
    ArrayList<Coupon> expireds = new ArrayList<>();
    ArrayList<Coupon> useds = new ArrayList<>();
    CouponsAdapter adapter = new CouponsAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        title = (TextView)findViewById(R.id.title);

        title.setTypeface(bold);

        list = (ListView) findViewById(R.id.list);
        getCoupons();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        ((View)findViewById(R.id.indicator_used)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_unused)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_expired)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_used)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_unused)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_expired)).setTypeface(normal);

        ((TextView)findViewById(R.id.txt_used)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_unused)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_expired)).setTextColor(getColor(R.color.lightPrimary));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selUnused(View view){
        unused();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void unused(){
        clearIndicators();
        ((View)findViewById(R.id.indicator_unused)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_unused)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_unused)).setTextColor(getColor(R.color.colorPrimary));
        if(coupons.isEmpty()){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

        adapter.setDatas(coupons);
        ((ListView)findViewById(R.id.list)).setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selUsed(View view){
        clearIndicators();
        ((View)findViewById(R.id.indicator_used)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_used)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_used)).setTextColor(getColor(R.color.colorPrimary));
        if(useds.isEmpty()){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

        adapter.setDatas(useds);
        ((ListView)findViewById(R.id.list)).setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selExpired(View view){
        clearIndicators();
        ((View)findViewById(R.id.indicator_expired)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_expired)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_expired)).setTextColor(getColor(R.color.colorPrimary));
        if(expireds.isEmpty()){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

        adapter.setDatas(expireds);
        ((ListView)findViewById(R.id.list)).setAdapter(adapter);
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
                expireds.clear();
                useds.clear();
                JSONArray dataArr = response.getJSONArray("availables");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Coupon coupon = new Coupon();
                    coupon.setId(object.getInt("id"));
                    coupon.setDiscount(object.getInt("discount"));
                    coupon.setExpireTime(object.getLong("expire_time"));
                    coupons.add(coupon);
                }

                dataArr = response.getJSONArray("expireds");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Coupon coupon = new Coupon();
                    coupon.setId(object.getInt("id"));
                    coupon.setDiscount(object.getInt("discount"));
                    coupon.setExpireTime(object.getLong("expire_time"));
                    expireds.add(coupon);
                }

                dataArr = response.getJSONArray("useds");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Coupon coupon = new Coupon();
                    coupon.setId(object.getInt("id"));
                    coupon.setDiscount(object.getInt("discount"));
                    coupon.setExpireTime(object.getLong("expire_time"));
                    useds.add(coupon);
                }

                if(coupons.isEmpty()){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                adapter.setDatas(coupons);
                ((ListView)findViewById(R.id.list)).setAdapter(adapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
















































