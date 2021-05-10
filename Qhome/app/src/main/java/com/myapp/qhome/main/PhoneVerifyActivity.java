package com.myapp.qhome.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
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

public class PhoneVerifyActivity extends BaseActivity {

    AVLoadingIndicatorView progressBar;
    TextView backButton, verifyButton;
    String email = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        backButton = (TextView)findViewById(R.id.btn_back);
        verifyButton = (TextView)findViewById(R.id.btn_verify);

        backButton.setTypeface(bold);
        verifyButton.setTypeface(bold);

        email = getIntent().getStringExtra("email");

//        StartLoginPage(LoginType.PHONE);

    }

    public void back(View view){
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    public void verify(View view){
        StartLoginPage(LoginType.PHONE);
    }

    final int REQUEST_CODE = 999;

    private void StartLoginPage(LoginType loginType) {
        if (loginType == LoginType.EMAIL) {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.EMAIL,
                            AccountKitActivity.ResponseType.CODE); // Use token when 'Enable client Access Token Flow' is YES
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        } else if (loginType == LoginType.PHONE) {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.PHONE,
                            AccountKitActivity.ResponseType.CODE); // Use token when 'Enable client Access Token Flow' is YES
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                showToast(toastMessage);
                return;

            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
                return;
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));

                    AccountKit.logOut();
                    verify(email);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void verify(String email) {

        progressBar.setVisibility(View.VISIBLE);

        String url = ReqConst.SERVER_URL + "verify";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseVerifyEmailResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast(getString(R.string.server_issue));
                progressBar.setVisibility(View.GONE);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseVerifyEmailResponse(String json) {

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

                showToast(getString(R.string.verified));
                Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_EMAIL, Commons.thisUser.get_email());
                Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_ROLE, Commons.thisUser.get_role());

                Commons.homeActivity.navigationView.getMenu().getItem(0).setVisible(false);
                Commons.homeActivity.navigationView.getMenu().getItem(6).setVisible(true);
                if(Commons.thisUser.get_role().equals("producer")){
                    Intent intent = new Intent(getApplicationContext(), RegisterStoreActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }else {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_out, R.anim.left_in);
                    finish();
                }
            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }
}























