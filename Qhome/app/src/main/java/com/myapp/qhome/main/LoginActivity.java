package com.myapp.qhome.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
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

public class LoginActivity extends BaseActivity {

    ImageButton showButton;
    EditText emailBox, passwordBox;
    boolean pwShow = false;
    TextView title, loginButton, signupButton, forgotPwdButton, caption, caption1;
    RadioButton producer, customer;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupUI(findViewById(R.id.activity), this);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        emailBox = (EditText)findViewById(R.id.emailBox);
        passwordBox = (EditText)findViewById(R.id.passwordBox);

        producer = (RadioButton)findViewById(R.id.producer);
        customer = (RadioButton)findViewById(R.id.customer);
        caption1 = (TextView)findViewById(R.id.caption1);

        title = (TextView)findViewById(R.id.title);
        loginButton = (TextView)findViewById(R.id.btn_login);
        caption = (TextView)findViewById(R.id.caption);
        signupButton = (TextView)findViewById(R.id.btn_signup);
        forgotPwdButton = (TextView)findViewById(R.id.btn_forgot_pwd);

        title.setTypeface(bold);
        emailBox.setTypeface(normal);
        loginButton.setTypeface(bold);
        signupButton.setTypeface(bold);
        caption.setTypeface(normal);
        forgotPwdButton.setTypeface(bold);

        caption1.setTypeface(normal);
        producer.setTypeface(bold);
        customer.setTypeface(bold);

        showButton = (ImageButton)findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pwShow){
                    pwShow = true;
                    showButton.setImageResource(R.drawable.eyelock);
                    if(passwordBox.getText().length() > 0){
                        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT);
                        passwordBox.setTypeface(normal);
                    }
                }else {
                    pwShow = false;
                    showButton.setImageResource(R.drawable.eyeunlock);
                    if(passwordBox.getText().length() > 0){
                        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }
            }
        });
    }

    public void goToSignupPage(View view){
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view){
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        onBackPressed();
    }

    public void login(View view){
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

        if(!producer.isChecked() && !customer.isChecked()){
            showToast(getString(R.string.select_role));
            return;
        }

        String role = "";
        if(producer.isChecked())role = "producer";
        if(customer.isChecked())role = "customer";

        login(emailBox.getText().toString().trim(), passwordBox.getText().toString().trim(), role);
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
                showToast(getString(R.string.server_issue));
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

                if (user.get_status().equals("removed")){
                    Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_EMAIL, "");
                    Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_ROLE, "");
                    Commons.thisUser = null;
                    Commons.homeActivity.navigationView.getMenu().getItem(0).setVisible(true);
                    Commons.homeActivity.navigationView.getMenu().getItem(6).setVisible(false);
                    showAlertDialog(getString(R.string.warning), getString(R.string.account_deleted), this, new Callable<Void>() {
                        public Void call() {
                            finish();
                            return null;
                        }
                    });
                    return;
                }

                if(user.get_auth_status().length() == 0){
                    Commons.thisUser = null;
                    showToast(getString(R.string.verify_phone));
                    Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class);
                    intent.putExtra("email", user.get_email());
                    startActivity(intent);
                }else {
                    Commons.thisUser = user;
                    showToast(getString(R.string.login_success));
                    Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_EMAIL, Commons.thisUser.get_email());
                    Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_ROLE, Commons.thisUser.get_role());
                    Commons.homeActivity.navigationView.getMenu().getItem(0).setVisible(false);
                    Commons.homeActivity.navigationView.getMenu().getItem(6).setVisible(true);

                    Commons.homeActivity.getNotifications();
                    finish();
                }

            } else if(success.equals("1")){
                Commons.thisUser = null;
                showToast(getString(R.string.unregistered_user));
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            } else if(success.equals("2")){
                Commons.thisUser = null;
                showToast(getString(R.string.incorrect_password));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}

























