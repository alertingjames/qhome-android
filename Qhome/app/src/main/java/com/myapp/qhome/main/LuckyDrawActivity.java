package com.myapp.qhome.main;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.cunoraz.gifview.library.GifView;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Phone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LuckyDrawActivity extends BaseActivity {

    ImageView luckyButton;
    String status = "", winner = "";
    GifView comingsoon;
    LinearLayout congratulations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_draw);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.result)).setTypeface(normal);

        comingsoon = (GifView)findViewById(R.id.comingsoon);
        congratulations = (LinearLayout) findViewById(R.id.congratulations);

        luckyButton = (ImageView)findViewById(R.id.btn_lucky);
        luckyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("info_exists")){
                    showToast(getString(R.string.info_exists));
                    return;
                }

                if(status.equals("winner_exists")){
                    showToast(getString(R.string.sorry) + winner + " " + getString(R.string.winner_exists));
                    return;
                }

                if(status.equals("you_won")){
                    return;
                }

                playProgressBar();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadInfo();
                    }
                }, 3000);
            }
        });
    }

    private void playProgressBar(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        animation.setFillAfter(true);
        luckyButton.startAnimation(animation);
    }

    private void uploadInfo() {
        String url = ReqConst.SERVER_URL + "postLuckyInfo";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUploadinfoResponse(response);
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
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseUploadinfoResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                luckyButton.setAnimation(null);
                showToast(getString(R.string.info_submited));
//                ((TextView)findViewById(R.id.result)).setText(getString(R.string.info_submited));
                comingsoon.setVisibility(View.VISIBLE);
                status = "info_exists";
            }else if(success.equals("1")) {
                JSONObject object = response.getJSONObject("data");
                winner = object.getString("member_name");
                luckyButton.setAnimation(null);
         //       showToast(getString(R.string.sorry) + winner + " " + getString(R.string.winner_exists));
                ((FrameLayout)findViewById(R.id.resultFrame)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.result)).setText(getString(R.string.sorry) + " " + winner + " " + getString(R.string.winner_exists));
                blink(((FrameLayout)findViewById(R.id.resultFrame)));
                status = "winner_exists";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }

    boolean flag = false;
    private void blink(FrameLayout frameLayout){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milisseconds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(flag){
                            frameLayout.setBackgroundResource(R.drawable.red_rect_stroke);
                            flag = false;
                        }else{
                            frameLayout.setBackground(null);
                            flag = true;
                        }
                        blink(frameLayout);
                    }
                });
            }
        }).start();
    }

    private void getInfo() {
        String url = ReqConst.SERVER_URL + "getMyLuckyInfo";
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseProductsResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                ((FrameLayout)findViewById(R.id.resultFrame)).setVisibility(View.GONE);
            }else if(success.equals("1")) {
                JSONObject object = response.getJSONObject("data");
                String status0 = object.getString("status");
                if(status0.equals("won")){
                    status = "you_won";
                    congratulations.setVisibility(View.VISIBLE);
                }else {
                    status = "info_exists";
                    comingsoon.setVisibility(View.VISIBLE);
                }

           //     ((FrameLayout)findViewById(R.id.resultFrame)).setVisibility(View.VISIBLE);
           //     showToast(getString(R.string.info_exists));
           //     ((TextView)findViewById(R.id.result)).setText(getString(R.string.info_exists));
           //     blink(((FrameLayout)findViewById(R.id.resultFrame)));
            }else if(success.equals("2")) {
                JSONObject object = response.getJSONObject("data");
                winner = object.getString("member_name");
                status = "winner_exists";
                ((FrameLayout)findViewById(R.id.resultFrame)).setVisibility(View.VISIBLE);
            //    showToast(getString(R.string.sorry) + winner + " " + getString(R.string.winner_exists));
                ((TextView)findViewById(R.id.result)).setText(getString(R.string.sorry) + " " + winner + " " + getString(R.string.winner_exists));
                blink(((FrameLayout)findViewById(R.id.resultFrame)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}




































