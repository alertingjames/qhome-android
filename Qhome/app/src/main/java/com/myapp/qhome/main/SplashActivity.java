package com.myapp.qhome.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.User;
import com.myapp.qhome.preference.PrefConst;
import com.myapp.qhome.preference.Preference;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SplashActivity extends BaseActivity {

    ImageView appName, appLogo;
    public static int splashTimeOut=4000;  //time in mili seconds
    Animation uptodown, downtoup;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        appName = (ImageView) findViewById(R.id.app_name);
        appLogo = (ImageView)findViewById(R.id.app_logo);

        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        // downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        appLogo.setAnimation(uptodown);
        appName.setAnimation(downtoup);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appLogo.animate().rotationY(360f).setDuration(3000);
            }
        }, 800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appName.animate().rotationY(360f).setDuration(3000);
            }
        }, 800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                login();
            }
        },splashTimeOut);

    }

    private void login(){
        String email = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_EMAIL, "");
        String role = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_ROLE, "");
        if(email.length() > 0 && role.length() > 0)
            login(email, "", role);
        else {
            Commons.thisUser = null;
            String langRemember = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_LANG_REMEMBER, "");
            String readTerms = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_READ_TERMS, "");
            if(langRemember.length() == 0){
                Intent intent = new Intent(getApplicationContext(), ChooseLangActivity.class);
                startActivity(intent);
            }else {
                if(readTerms.length() == 0){
                    Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }
            finish();
        }
    }

    private void login(String email, String password, String role) {

        progressBar.setVisibility(View.VISIBLE);

        String url = ReqConst.SERVER_URL + "login";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseLoginResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("role", role);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseLoginResponse(String json) {

        progressBar.setVisibility(View.GONE);

        try {
            JSONObject response = new JSONObject(json);

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

            }else {
                Commons.thisUser = null;
            }

            String langRemember = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_LANG_REMEMBER, "");
            String readTerms = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_READ_TERMS, "");
            if(langRemember.length() == 0){
                Intent intent = new Intent(getApplicationContext(), ChooseLangActivity.class);
                startActivity(intent);
            }else {
                if(readTerms.length() == 0){
                    Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}









































