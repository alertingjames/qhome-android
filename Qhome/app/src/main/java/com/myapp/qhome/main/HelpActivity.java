package com.myapp.qhome.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.Firebase;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpActivity extends BaseActivity {

    Firebase ref;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);

        setupUI(findViewById(R.id.activity), this);

        ((TextView)findViewById(R.id.spacer)).setTypeface(bold);
        ((EditText)findViewById(R.id.messageBox)).setTypeface(normal);
        ((TextView)findViewById(R.id.or)).setTypeface(bold);
    }

    public void openMail(View view){
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.ADMIN_EMAIL});
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, "Share As...");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.ADMIN_EMAIL});
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    public void openMessageBox(View view){

        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }

        if(((LinearLayout)findViewById(R.id.messageFrame)).getVisibility() == View.VISIBLE) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
        ((LinearLayout)findViewById(R.id.messageFrame)).startAnimation(animation);
        ((LinearLayout)findViewById(R.id.messageFrame)).setVisibility(View.VISIBLE);
        ((View)findViewById(R.id.bg_dark)).setVisibility(View.VISIBLE);
        ((EditText)findViewById(R.id.messageBox)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(((EditText)findViewById(R.id.messageBox)).getText().length() > 0){
                    ((FrameLayout)findViewById(R.id.sendButtonFrame)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.spacer)).setVisibility(View.GONE);
                }else {
                    ((FrameLayout)findViewById(R.id.sendButtonFrame)).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.spacer)).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void dismisFrame(View view){
        dismisFrame();
    }

    private void dismisFrame(){
        if(((LinearLayout)findViewById(R.id.messageFrame)).getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
            ((LinearLayout)findViewById(R.id.messageFrame)).startAnimation(animation);
            ((LinearLayout)findViewById(R.id.messageFrame)).setVisibility(View.GONE);
            ((View)findViewById(R.id.bg_dark)).setVisibility(View.GONE);
            ((EditText)findViewById(R.id.messageBox)).setText("");
        }
    }

    public void submitMessage(View view){
        if(((EditText)findViewById(R.id.messageBox)).getText().toString().trim().length() == 0){
            showToast(getString(R.string.write_something));
            return;
        }
        submitMessage();
    }

//    private void submitMessage(){
//        progressBar.setVisibility(View.VISIBLE);
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("msg", ((EditText)findViewById(R.id.messageBox)).getText().toString().trim());
//        map.put("date", String.valueOf(new Date().getTime()));
//        map.put("fromid", String.valueOf(Commons.thisUser.get_idx()));
//        map.put("fromname", Commons.thisUser.get_name());
//        map.put("type", "user");
//        map.put("id", String.valueOf(Commons.thisUser.get_idx()));
//
//        ref.push().setValue(map);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                progressBar.setVisibility(View.GONE);
//                showToast(getString(R.string.message_sent));
//                dismisFrame();
//            }
//        }, 1000);
//    }

    public void back(View view){
        onBackPressed();
    }

    private void submitMessage() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "contactAdmin";
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
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("message", ((EditText)findViewById(R.id.messageBox)).getText().toString().trim());
                params.put("type", "user");
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
                showToast(getString(R.string.message_sent));
                dismisFrame();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}






































