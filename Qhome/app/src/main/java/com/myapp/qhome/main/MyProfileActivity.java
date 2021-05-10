package com.myapp.qhome.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.client.result.VINParsedResult;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Address;
import com.myapp.qhome.models.Phone;
import com.myapp.qhome.models.User;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends BaseActivity {

    TextView nameBox, emailBox, instagramBox, phoneBox, addressBox, addressLineBox, alertTitle, saveButton;
    FrameLayout darkBackground;
    EditText contentBox;
    ImageView cancelButton;
    LinearLayout alertFrame, instagramFrame;
    AVLoadingIndicatorView progressBar;
    String opt = "";
    String area = "", street = "", house = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Commons.phoneId = 0;
        Commons.addrId = 0;

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption5)).setTypeface(normal);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        nameBox = (TextView)findViewById(R.id.nameBox);
        emailBox = (TextView)findViewById(R.id.emailBox);
        instagramBox = (TextView)findViewById(R.id.instagramBox);
        phoneBox = (TextView)findViewById(R.id.phoneBox);
        addressBox = (TextView)findViewById(R.id.address);
        addressLineBox = (TextView)findViewById(R.id.address2);

        instagramFrame = (LinearLayout)findViewById(R.id.instagramFrame);

        nameBox.setText(Commons.thisUser.get_name());
        emailBox.setText(Commons.thisUser.get_email());
        if(Commons.thisUser.get_role().equals("producer")){
            instagramFrame.setVisibility(View.VISIBLE);
            instagramBox.setText(Commons.thisUser.get_instagram());
        }else instagramFrame.setVisibility(View.GONE);
        phoneBox.setText(Commons.thisUser.get_phone_number());

        darkBackground = (FrameLayout) findViewById(R.id.bg_dark);
        alertFrame = (LinearLayout)findViewById(R.id.alertFrame);
        alertTitle = (TextView)findViewById(R.id.alertTitle);
        contentBox = (EditText)findViewById(R.id.content);
        cancelButton = (ImageView)findViewById(R.id.btn_cancel);
        saveButton = (TextView)findViewById(R.id.btn_save);

        saveButton.setTypeface(bold);

        setupUI(findViewById(R.id.activity), this);

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

    @Override
    public void onResume() {
        super.onResume();
        getPhones();
        getAddresses();
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
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
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

                if(Commons.phones.size() > 0)phoneBox.setText(Commons.phones.get(Commons.phoneId).getPhone_number());
                else {
                    if(Commons.thisUser != null)phoneBox.setText(Commons.thisUser.get_phone_number());
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

                if(Commons.thisUser != null && Commons.thisUser.get_address().length() == 0){
                    if(Commons.addresses.size() > 0){
                        addressBox.setText(Commons.addresses.get(Commons.addrId).getAddress());
                        if(Commons.lang.equals("ar")){
                            addressLineBox.setText(Commons.addresses.get(Commons.addrId).getHouse() + "و " + Commons.addresses.get(Commons.addrId).getStreet() + "و " + Commons.addresses.get(Commons.addrId).getArea());
                        }else {
                            addressLineBox.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet() + ", " + Commons.addresses.get(Commons.addrId).getHouse());
                        }
                        area = Commons.addresses.get(Commons.addrId).getArea();
                        street = Commons.addresses.get(Commons.addrId).getStreet();
                        house = Commons.addresses.get(Commons.addrId).getHouse();
                    }
                }
                else {
                    if(Commons.addresses.size() > 0){
                        addressBox.setText(Commons.addresses.get(Commons.addrId).getAddress());
                        if(Commons.lang.equals("ar")){
                            addressLineBox.setText(Commons.addresses.get(Commons.addrId).getHouse() + "و " + Commons.addresses.get(Commons.addrId).getStreet() + "و " + Commons.addresses.get(Commons.addrId).getArea());
                        }else {
                            addressLineBox.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet() + ", " + Commons.addresses.get(Commons.addrId).getHouse());
                        }
                        area = Commons.addresses.get(Commons.addrId).getArea();
                        street = Commons.addresses.get(Commons.addrId).getStreet();
                        house = Commons.addresses.get(Commons.addrId).getHouse();
                    }else {
                        addressBox.setText(Commons.thisUser.get_address());
                        if(Commons.lang.equals("ar")){
                            addressLineBox.setText(Commons.thisUser.get_house() + "و " + Commons.thisUser.get_street() + "و " + Commons.thisUser.get_area());
                        }else {
                            addressLineBox.setText(Commons.thisUser.get_area() + ", " + Commons.thisUser.get_street() + ", " + Commons.thisUser.get_house());
                        }
                        area = Commons.thisUser.get_area();
                        street = Commons.thisUser.get_street();
                        house = Commons.thisUser.get_house();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void editName(View view){
        opt = "name";
        if(alertFrame.getVisibility() != View.VISIBLE){
            alertFrame.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);
            alertFrame.setAnimation(animation);
            darkBackground.setVisibility(View.VISIBLE);
        }
        alertTitle.setText(getString(R.string.change_name));
        contentBox.setText(Commons.thisUser.get_name());
    }

    public void editInstagram(View view){
        opt = "instagram";
        if(alertFrame.getVisibility() != View.VISIBLE){
            alertFrame.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);
            alertFrame.setAnimation(animation);
            darkBackground.setVisibility(View.VISIBLE);
        }
        alertTitle.setText(getString(R.string.change_instagram));
        contentBox.setText(Commons.thisUser.get_instagram());
    }

    public void save(View view){
        if(contentBox.getText().toString().trim().length() == 0){
            if(opt.equals("name"))showToast(getString(R.string.enter_name));
            else if(opt.equals("instagram"))showToast(getString(R.string.enter_instagram));
            return;
        }
        dismissFrame();
        if(opt.equals("name"))nameBox.setText(contentBox.getText().toString().trim());
        else if(opt.equals("instagram"))instagramBox.setText(contentBox.getText().toString().trim());
    }

    public void dismissFrame(View view){
        dismissFrame();
    }

    private void dismissFrame(){
        if(alertFrame.getVisibility() == View.VISIBLE){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
            alertFrame.setAnimation(animation);
            alertFrame.setVisibility(View.GONE);
            darkBackground.setVisibility(View.GONE);
        }
    }

    public void touchedFrame(View view){

    }

    public void saveProfile(View view){
        updateMember();
    }

    public void updateMember() {
        String url = ReqConst.SERVER_URL + "updateMember";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse(response);

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

                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("name", nameBox.getText().toString().trim());
                params.put("email", emailBox.getText().toString().trim());
                params.put("phone_number", phoneBox.getText().toString().trim());
                params.put("instagram", instagramBox.getText().toString().trim());
                params.put("address", addressBox.getText().toString().trim());
                params.put("area", area);
                params.put("street", street);
                params.put("house", house);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                JSONObject object = response.getJSONObject("data");
                User user = new User();
                user.set_idx(object.getInt("id"));
                user.set_name(object.getString("name"));
                user.set_email(object.getString("email"));
                user.set_password(object.getString("password"));
                user.set_phone_number(object.getString("phone_number"));
                user.set_address(object.getString("address"));
                user.set_area(object.getString("area"));
                user.set_street(object.getString("street"));
                user.set_house(object.getString("house"));
                user.set_instagram(object.getString("instagram"));
                user.set_auth_status(object.getString("auth_status"));
                user.set_registered_time(object.getString("registered_time"));
                user.set_role(object.getString("role"));
                user.set_status(object.getString("status"));
                user.set_stores(Integer.parseInt(object.getString("stores")));

                Commons.thisUser = user;

                showToast(getString(R.string.profile_updated));
                finish();

            }else if(success.equals("1")){
                showToast(getString(R.string.unregistered_user));
                finish();
            }
            else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }
}


















































