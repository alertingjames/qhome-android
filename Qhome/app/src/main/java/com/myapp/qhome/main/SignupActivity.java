package com.myapp.qhome.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.hbb20.CountryCodePicker;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.myapp.qhome.main.LoginActivity.isValidEmail;

public class SignupActivity extends BaseActivity {

    EditText nameBox, emailBox, passwordBox, phoneNumberBox, instagramBox, cusNameBox, cusEmailBox, cusPasswordBox, cusPhoneBox, addressBox, areaBox, streetBox, houseBox;
    ImageButton showButton, cusShowButton;
    boolean isShow = false, cusIsShow = false;
    AVLoadingIndicatorView progressBar;
    CountryCodePicker ccp, ccp2;

    String signupOpt = "producer";
    String name = "", email = "", password = "", phoneNumber = "", address = "", area = "", street = "", house = "", instagram = "", role = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setupUI(findViewById(R.id.activity), this);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        nameBox = (EditText)findViewById(R.id.nameBox);
        emailBox = (EditText)findViewById(R.id.emailBox);
        passwordBox = (EditText)findViewById(R.id.passwordBox);
        phoneNumberBox = (EditText)findViewById(R.id.phoneBox);
        instagramBox = (EditText)findViewById(R.id.instagramBox);
        cusNameBox = (EditText)findViewById(R.id.customer_nameBox);
        cusEmailBox = (EditText)findViewById(R.id.customer_emailBox);
        cusPasswordBox = (EditText)findViewById(R.id.customer_passwordBox);
        cusPhoneBox = (EditText)findViewById(R.id.customer_phoneBox);
        addressBox = (EditText)findViewById(R.id.addressBox);
        areaBox = (EditText)findViewById(R.id.areaBox);
        streetBox = (EditText)findViewById(R.id.streetBox);
        houseBox = (EditText)findViewById(R.id.houseBox);

        nameBox.setTypeface(normal);
        emailBox.setTypeface(normal);
        passwordBox.setTypeface(normal);
        phoneNumberBox.setTypeface(normal);
        instagramBox.setTypeface(normal);
        cusNameBox.setTypeface(normal);
        cusEmailBox.setTypeface(normal);
        cusPasswordBox.setTypeface(normal);
        cusPhoneBox.setTypeface(normal);
        addressBox.setTypeface(normal);
        areaBox.setTypeface(normal);
        streetBox.setTypeface(normal);
        houseBox.setTypeface(normal);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.btn_register)).setTypeface(bold);
        ((TextView)findViewById(R.id.customer_btn_register)).setTypeface(bold);

        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp2 = (CountryCodePicker) findViewById(R.id.ccp2);

        showButton = (ImageButton)findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isShow){
                    isShow = true;
                    showButton.setImageResource(R.drawable.eyelock);
                    if(passwordBox.getText().length() > 0){
                        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT);
                        passwordBox.setTypeface(normal);
                    }
                }else {
                    isShow = false;
                    showButton.setImageResource(R.drawable.eyeunlock);
                    if(passwordBox.getText().length() > 0){
                        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }
            }
        });
        cusShowButton = (ImageButton)findViewById(R.id.customer_showButton);
        cusShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cusIsShow){
                    cusIsShow = true;
                    cusShowButton.setImageResource(R.drawable.eyelock);
                    if(cusPasswordBox.getText().length() > 0){
                        cusPasswordBox.setInputType(InputType.TYPE_CLASS_TEXT);
                        cusPasswordBox.setTypeface(normal);
                    }
                }else {
                    cusIsShow = false;
                    cusShowButton.setImageResource(R.drawable.eyeunlock);
                    if(cusPasswordBox.getText().length() > 0){
                        cusPasswordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }
            }
        });

        ((TextView)findViewById(R.id.txt_producer)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_producer)).setPaintFlags(((TextView)findViewById(R.id.txt_producer)).getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        ((TextView)findViewById(R.id.txt_customer)).setTypeface(normal);

        setDefaultCountryCode();
    }

    private void setDefaultCountryCode(){
        if(Commons.lang.equals("ar")){
            ccp.setDefaultCountryUsingNameCode("QA");
            ccp2.setDefaultCountryUsingNameCode("QA");
        }else {
            ccp.setDefaultCountryUsingNameCode("US");
            ccp2.setDefaultCountryUsingNameCode("US");
        }
        ccp.resetToDefaultCountry();
        ccp2.resetToDefaultCountry();
    }

    public void back(View view){
        onBackPressed();
    }

    private void clearIndicators(){
        ((LinearLayout)findViewById(R.id.lyt_producer)).setBackground(null);
        ((LinearLayout)findViewById(R.id.lyt_customer)).setBackground(null);
        ((TextView)findViewById(R.id.txt_producer)).setTextColor(Color.parseColor("#518F1F41"));
        ((TextView)findViewById(R.id.txt_producer)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_producer)).setPaintFlags(0);
        ((TextView)findViewById(R.id.txt_producer)).setTextSize(16);
        ((TextView)findViewById(R.id.txt_producer)).setGravity(Gravity.CENTER);
        ((TextView)findViewById(R.id.txt_customer)).setTextColor(Color.parseColor("#518F1F41"));
        ((TextView)findViewById(R.id.txt_customer)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_customer)).setPaintFlags(0);
        ((TextView)findViewById(R.id.txt_customer)).setTextSize(16);
        ((TextView)findViewById(R.id.txt_customer)).setGravity(Gravity.CENTER);

        ((LinearLayout)findViewById(R.id.producerFrame)).setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.customerFrame)).setVisibility(View.GONE);
    }

    public void producer(View view){
        clearIndicators();
        ((LinearLayout)findViewById(R.id.lyt_producer)).setBackgroundResource(R.drawable.seltabbg);
        ((TextView)findViewById(R.id.txt_producer)).setTextColor(getColor(R.color.colorPrimary));
        ((TextView)findViewById(R.id.txt_producer)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_producer)).setPaintFlags(((TextView)findViewById(R.id.txt_producer)).getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        ((TextView)findViewById(R.id.txt_producer)).setTextSize(18);
        ((TextView)findViewById(R.id.txt_producer)).setGravity(Gravity.BOTTOM);
        ((LinearLayout)findViewById(R.id.producerFrame)).setVisibility(View.VISIBLE);

        signupOpt = "producer";
    }

    public void customer(View view){
        clearIndicators();
        ((LinearLayout)findViewById(R.id.lyt_customer)).setBackgroundResource(R.drawable.seltabbg);
        ((TextView)findViewById(R.id.txt_customer)).setTextColor(getColor(R.color.colorPrimary));
        ((TextView)findViewById(R.id.txt_customer)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_customer)).setPaintFlags(((TextView)findViewById(R.id.txt_customer)).getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        ((TextView)findViewById(R.id.txt_customer)).setTextSize(18);
        ((TextView)findViewById(R.id.txt_customer)).setGravity(Gravity.BOTTOM);
        ((LinearLayout)findViewById(R.id.customerFrame)).setVisibility(View.VISIBLE);

        signupOpt = "customer";
    }

    public void next(View view){
        registerMember();
    }

    public void dismisFrame(View view){
        dismisFrame();
    }

    private void dismisFrame(){
        if(((LinearLayout)findViewById(R.id.lyt_confirm)).getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
            ((LinearLayout)findViewById(R.id.lyt_confirm)).startAnimation(animation);
            ((LinearLayout)findViewById(R.id.lyt_confirm)).setVisibility(View.GONE);
            ((View)findViewById(R.id.bg_dark)).setVisibility(View.GONE);
        }
    }

    public void showConfirm(View view){

        if(signupOpt.equals("producer")){
            if(nameBox.getText().length() == 0){
                showToast(getString(R.string.enter_name));
                return;
            }

            if(emailBox.getText().length() == 0){
                showToast(getString(R.string.enter_email));
                return;
            }

            if(!isValidEmail(emailBox.getText().toString().trim())){
                showToast(getString(R.string.com_accountkit_email_invalid));
                return;
            }

            if(passwordBox.getText().length() == 0){
                showToast(getString(R.string.enter_password));
                return;
            }

            if(phoneNumberBox.getText().length() == 0){
                showToast(getString(R.string.enter_phone));
                return;
            }

            if(!validCellPhone(phoneNumberBox.getText().toString().trim())){
                showToast(getString(R.string.invalid_phone));
                return;
            }

            if(instagramBox.getText().length() == 0){
                showToast(getString(R.string.enter_instagram));
                return;
            }
        }else {
            if(cusNameBox.getText().length() == 0){
                showToast(getString(R.string.enter_name));
                return;
            }

            if(cusEmailBox.getText().length() == 0){
                showToast(getString(R.string.enter_email));
                return;
            }

            if(!isValidEmail(cusEmailBox.getText().toString().trim())){
                showToast(getString(R.string.com_accountkit_email_invalid));
                return;
            }

            if(cusPasswordBox.getText().length() == 0){
                showToast(getString(R.string.enter_password));
                return;
            }

            if(cusPhoneBox.getText().length() == 0){
                showToast(getString(R.string.enter_phone));
                return;
            }

            if(addressBox.getText().length() == 0){
                showToast(getString(R.string.enter_address));
                return;
            }

            if(areaBox.getText().length() == 0){
                showToast(getString(R.string.enter_area));
                return;
            }

            if(streetBox.getText().length() == 0){
                showToast(getString(R.string.enter_street));
                return;
            }

            if(houseBox.getText().length() == 0){
                showToast(getString(R.string.enter_house));
                return;
            }
        }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
        ((LinearLayout)findViewById(R.id.lyt_confirm)).startAnimation(animation);
        ((LinearLayout)findViewById(R.id.lyt_confirm)).setVisibility(View.VISIBLE);
        ((View)findViewById(R.id.bg_dark)).setVisibility(View.VISIBLE);
        String text = "";
        name = ""; email = ""; password = ""; phoneNumber = ""; address = ""; area = ""; street = ""; house = ""; instagram = ""; role = "";
        if(signupOpt.equals("producer")){
            text = getString(R.string.name) + ": " + nameBox.getText().toString().trim() + "\n"
                    + getString(R.string.email) + ": " + emailBox.getText().toString().trim() + "\n"
                    + getString(R.string.phone_number) + ": " + "+" + ccp.getSelectedCountryCode() + phoneNumberBox.getText().toString().trim() + "\n"
                    + getString(R.string.instagram) + ": " + instagramBox.getText().toString().trim();
            name = nameBox.getText().toString().trim();
            email = emailBox.getText().toString().trim();
            password = passwordBox.getText().toString().trim();
            phoneNumber = "+" + ccp.getSelectedCountryCode() + phoneNumberBox.getText().toString().trim();
            instagram = instagramBox.getText().toString().trim();
            role = "producer";
        }else {
            text = getString(R.string.name) + ": " + cusNameBox.getText().toString().trim() + "\n"
                    + getString(R.string.email) + ": " + cusEmailBox.getText().toString().trim() + "\n"
                    + getString(R.string.phone_number) + ": " + "+" + ccp2.getSelectedCountryCode() + cusPhoneBox.getText().toString().trim() + "\n"
                    + getString(R.string.address) + ": " + addressBox.getText().toString().trim() + "\n"
                    + getString(R.string.area) + ": " + areaBox.getText().toString().trim() + "\n"
                    + getString(R.string.street) + ": " + streetBox.getText().toString().trim() + "\n"
                    + getString(R.string.house) + ": " + houseBox.getText().toString().trim();
            name = cusNameBox.getText().toString().trim();
            email = cusEmailBox.getText().toString().trim();
            password = cusPasswordBox.getText().toString().trim();
            phoneNumber = "+" + ccp2.getSelectedCountryCode() + cusPhoneBox.getText().toString().trim();
            address = addressBox.getText().toString().trim();
            area = areaBox.getText().toString().trim();
            street = streetBox.getText().toString().trim();
            house = houseBox.getText().toString().trim();
            role = "customer";
        }
        ((TextView)((LinearLayout)findViewById(R.id.lyt_confirm)).findViewById(R.id.btn_cancel)).setTypeface(normal);
        ((TextView)((LinearLayout)findViewById(R.id.lyt_confirm)).findViewById(R.id.btn_confirm)).setTypeface(bold);
        ((TextView)((LinearLayout)findViewById(R.id.lyt_confirm)).findViewById(R.id.content)).setTypeface(normal);
        ((TextView)((LinearLayout)findViewById(R.id.lyt_confirm)).findViewById(R.id.content)).setText(text);
        ((TextView)((LinearLayout)findViewById(R.id.lyt_confirm)).findViewById(R.id.btn_confirm))
                .setPaintFlags(((TextView)((LinearLayout)findViewById(R.id.lyt_confirm)).findViewById(R.id.btn_confirm)).getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

    }

    public void registerMember() {
        String url = ReqConst.SERVER_URL + "registerMember";
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

                params.put("name", name);
                params.put("imei_id", Commons.IMEI);
                params.put("email", email);
                params.put("password", password);
                params.put("phone_number", phoneNumber);
                params.put("instagram", instagram);
                params.put("address", address);
                params.put("area", area);
                params.put("street", street);
                params.put("house", house);
                params.put("role", role);

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

                showToast(getString(R.string.verify_phone));
                Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class);
                intent.putExtra("email", user.get_email());
                startActivity(intent);
                finish();

            } else if(success.equals("1")){
                showToast(getString(R.string.existing_email));
            } else if(success.equals("2")){
                showToast(getString(R.string.existing_user));
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






























