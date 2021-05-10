package com.myapp.qhome.main;

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
import android.widget.ListView;
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
import com.google.zxing.client.result.VINParsedResult;
import com.hbb20.CountryCodePicker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.AddressListAdapter;
import com.myapp.qhome.adapters.PhoneListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Address;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Phone;
import com.myapp.qhome.models.Product;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class ShippingAddressActivity extends BaseActivity {

    AVLoadingIndicatorView progressBar;
    ListView list;
    EditText phoneBox, addressBox, areaBox, streetBox, houseBox;
    LinearLayout phoneFrame, addressFrame;
    FrameLayout addButton;
    CountryCodePicker ccp;
    ArrayList<Phone> phones = new ArrayList<>();
    PhoneListAdapter phoneListAdapter = new PhoneListAdapter(this);
    ArrayList<Address> addresses = new ArrayList<>();
    AddressListAdapter addressListAdapter = new AddressListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        list = (ListView)findViewById(R.id.list);

        ((TextView)findViewById(R.id.caption)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption2)).setTypeface(bold);

        phoneBox = (EditText)findViewById(R.id.phoneBox);
        addressBox = (EditText)findViewById(R.id.addressBox);
        areaBox = (EditText)findViewById(R.id.areaBox);
        streetBox = (EditText)findViewById(R.id.streetBox);
        houseBox = (EditText)findViewById(R.id.houseBox);

        phoneFrame = ((LinearLayout)findViewById(R.id.phoneFrame));
        addressFrame = ((LinearLayout)findViewById(R.id.addressFrame));

        addButton = (FrameLayout) findViewById(R.id.btn_add);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        try{
            String to = getIntent().getStringExtra("to");
            if(to.equals("phone"))phone();
        }catch (NullPointerException e){
            e.printStackTrace();
            address();
        }

        setupUI(findViewById(R.id.activity), this);
        setDefaultCountryCode();
    }

    private void setDefaultCountryCode(){
        if(Commons.lang.equals("ar")){
            ccp.setDefaultCountryUsingNameCode("QA");
        }else {
            ccp.setDefaultCountryUsingNameCode("US");
        }
        ccp.resetToDefaultCountry();
    }

    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        ((View)findViewById(R.id.indicator_address)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_phone)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_address)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_phone)).setTypeface(normal);

        ((TextView)findViewById(R.id.txt_address)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_phone)).setTextColor(getColor(R.color.lightPrimary));

        if(phoneFrame.getVisibility() == View.VISIBLE)phoneFrame.setVisibility(View.GONE);
        if(addressFrame.getVisibility() == View.VISIBLE)addressFrame.setVisibility(View.GONE);
    }

    public void selAddress(View view){
        if(((View)findViewById(R.id.indicator_address)).getVisibility() == View.VISIBLE)return;
        address();
    }

    private void address(){
        clearIndicators();
        ((View)findViewById(R.id.indicator_address)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_address)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_address)).setTextColor(getColor(R.color.colorPrimary));

        refreshAddr();
    }

    public void selPhone(View view){
        if(((View)findViewById(R.id.indicator_phone)).getVisibility() == View.VISIBLE)return;
        phone();
    }

    private void phone(){
        clearIndicators();
        ((View)findViewById(R.id.indicator_phone)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_phone)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_phone)).setTextColor(getColor(R.color.colorPrimary));

        refreshPhone();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getPhones() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getPhones";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Phones response===>", response);
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
                refreshPhone();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        onBackPressed();
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
                refreshAddr();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshAddr(){
        if(Commons.addresses.isEmpty()){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        }
        else {
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
        }

        addressListAdapter.setDatas(Commons.addresses);
        list.setAdapter(addressListAdapter);
    }

    private void refreshPhone(){
        if(Commons.phones.isEmpty()){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        }
        else {
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
        }

        phoneListAdapter.setDatas(Commons.phones);
        list.setAdapter(phoneListAdapter);
    }

    public void savePhoneNumber(View view){
        if(phoneBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_phone));
            return;
        }

        if(!validCellPhone(phoneBox.getText().toString().trim())){
            showToast(getString(R.string.invalid_phone));
            return;
        }

        String memberId = "0";
        if(Commons.thisUser != null)memberId = String.valueOf(Commons.thisUser.get_idx());
        uploadPhoneNumberToSever(memberId, Commons.IMEI, "+" + ccp.getSelectedCountryCode() + phoneBox.getText().toString().trim());
    }

    public void saveAddress(View view){
        if(addressBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_address));
            return;
        }

        if(areaBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_area));
            return;
        }

        if(streetBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_street));
            return;
        }

        if(houseBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_house));
            return;
        }

        String memberId = "0";
        if(Commons.thisUser != null)memberId = String.valueOf(Commons.thisUser.get_idx());
        uploadAddressToSever(memberId, Commons.IMEI, addressBox.getText().toString().trim(),
                areaBox.getText().toString().trim(), streetBox.getText().toString().trim(), houseBox.getText().toString().trim());

    }

    public void uploadAddressToSever(String memberId, String imei, String address, String area, String street, String house) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "saveAddress")
                .addMultipartParameter("member_id", memberId)
                .addMultipartParameter("imei_id", imei)
                .addMultipartParameter("address", address)
                .addMultipartParameter("area", area)
                .addMultipartParameter("street", street)
                .addMultipartParameter("house", house)
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
                                showToast(getString(R.string.address_added));
                                dismissAddressFrame();
                                getAddresses();
                            }else {
                                showToast(getString(R.string.server_issue));
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
                        showToast(error.getErrorDetail());
                    }
                });
    }

    public void uploadPhoneNumberToSever(String memberId, String imei, String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "savePhoneNumber")
                .addMultipartParameter("member_id", memberId)
                .addMultipartParameter("imei_id", imei)
                .addMultipartParameter("phone_number", phoneNumber)
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
                                showToast(getString(R.string.phone_added));
                                dismissPhoneFrame();
                                getPhones();
                            }else {
                                showToast(getString(R.string.server_issue));
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
                        showToast(error.getErrorDetail());
                    }
                });
    }

    public void dismissPhoneFrame(View view){
        dismissPhoneFrame();
    }

    private void dismissPhoneFrame(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
        phoneFrame.setAnimation(animation);
        phoneFrame.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        addButton.setAnimation(animation);
        addButton.setVisibility(View.VISIBLE);
        phoneBox.setText("");
    }

    public void dismissAddressFrame(View view){
        dismissAddressFrame();
    }

    private void dismissAddressFrame(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
        addressFrame.setAnimation(animation);
        addressFrame.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        addButton.setAnimation(animation);
        addButton.setVisibility(View.VISIBLE);
        addressBox.setText("");
        areaBox.setText("");
        streetBox.setText("");
        houseBox.setText("");
    }

    public void add(View view){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
        if(((View)findViewById(R.id.indicator_phone)).getVisibility() == View.VISIBLE){
            phoneFrame.setVisibility(View.VISIBLE);
            phoneFrame.setAnimation(animation);
        }
        else if(((View)findViewById(R.id.indicator_address)).getVisibility() == View.VISIBLE){
            addressFrame.setVisibility(View.VISIBLE);
            addressFrame.setAnimation(animation);
        }

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
        addButton.setAnimation(animation);
        addButton.setVisibility(View.GONE);
    }

    Address pAddress;

    public void deleteAddress(Address address) {

        pAddress = address;

        showAlertDialogForQuestion(getString(R.string.warning), getString(R.string.del_text), this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                String url = ReqConst.SERVER_URL + "delAddress";
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
                        params.put("addr_id", String.valueOf(address.getId()));
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

    public void parseDelResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                int index = Commons.addresses.indexOf(pAddress);
                Commons.addresses.remove(index);
                refreshAddr();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Phone pPhone;

    public void deletePhoneNumber(Phone phone) {

        pPhone = phone;

        showAlertDialogForQuestion(getString(R.string.warning), getString(R.string.del_text), this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                String url = ReqConst.SERVER_URL + "delPhone";
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
                        params.put("phone_id", String.valueOf(phone.getId()));
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
                int index = Commons.phones.indexOf(pPhone);
                Commons.phones.remove(index);
                refreshPhone();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}





























