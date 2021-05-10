package com.myapp.qhome.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends BaseActivity {

    EditText emailBox;
    TextView title, requestButton, caption;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setupUI(findViewById(R.id.activity), this);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        emailBox = (EditText)findViewById(R.id.emailBox);

        title = (TextView)findViewById(R.id.title);
        caption = (TextView)findViewById(R.id.caption);
        requestButton = (TextView)findViewById(R.id.btn_request);

        title.setTypeface(bold);
        caption.setTypeface(normal);
        emailBox.setTypeface(normal);
        requestButton.setTypeface(bold);

    }

    public void requestPassword(View view){
        if(emailBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_email));
            return;
        }
        requestPassword(emailBox.getText().toString().trim());
    }

    private void requestPassword(String email) {
        String url = ReqConst.SERVER_URL + "forgotpassword";
        progressBar.setVisibility(View.VISIBLE);

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
                progressBar.setVisibility(View.GONE);
                showToast(getString(R.string.server_issue));
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
                showToast(getString(R.string.check_email));
                finish();
            } else if(success.equals("1")){
                showToast(getString(R.string.unregistered_user));
            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    public void back(View view){
        onBackPressed();
    }
}

































