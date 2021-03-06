package com.myapp.qhome.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
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
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.firebase.client.Firebase;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.CompanyPrice;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyActivity extends BaseActivity {

    Firebase ref;
    TextView priceBox;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);

        setupUI(findViewById(R.id.activity), this);

        ((TextView)findViewById(R.id.spacer)).setTypeface(bold);
        ((EditText)findViewById(R.id.messageBox)).setTypeface(normal);
        ((TextView)findViewById(R.id.or)).setTypeface(bold);

        priceBox = (TextView)findViewById(R.id.priceBox);
        priceBox.setTypeface(normal);
        ((TextView)findViewById(R.id.caption)).setTypeface(normal);
        getComprices();
    }

    public void openDialer(View view){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.CADMIN_PHONE));
        startActivity(intent);
    }

    public void openWhatsapp(View view){
        openWhatsappContact(Constants.CADMIN_PHONE);
    }

    private void openWhatsappContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    public void openMail(View view){
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.CADMIN_EMAIL});
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
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.CADMIN_PHONE});
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
        String url = ReqConst.SERVER_URL + "touchCompany";
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

    private void getComprices(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getComprices")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                JSONArray jsonArray = response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    CompanyPrice price = new CompanyPrice();
                                    price.setId(jsonObject.getInt("id"));
                                    price.setPrice(Double.parseDouble(jsonObject.getString("price")));
                                    price.setDescription(jsonObject.getString("description"));
                                    String priceStr = String.valueOf(price.getPrice()) + " " + price.getDescription();
                                    priceBox.setText(priceBox.getText().toString() + ((i == 0)? "- ":"\n- ") + priceStr);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                    }
                });
    }
}






































