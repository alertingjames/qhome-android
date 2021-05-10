package com.myapp.qhome.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
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
import com.myapp.qhome.models.Rating;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends BaseActivity {

    AVLoadingIndicatorView progressBar;
    RatingBar ratingBar;
    TextView ratingBox;
    EditText subjectBox, feedbackBox;
    String lang = "";
    ArrayList<Rating> ratingArrayList = new ArrayList<>();
    DecimalFormat df = new DecimalFormat("0.0");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        ratingBox = (TextView)findViewById(R.id.ratingBox);
        subjectBox = (EditText)findViewById(R.id.subjectBox);
        feedbackBox = (EditText)findViewById(R.id.feedbackBox);

        ((TextView)findViewById(R.id.caption)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rt, boolean fromUser) {
                Float ratingvalue = (Float) ratingBar.getRating();
                ratingBox.setText(String.valueOf(ratingvalue));
            }
        });

        setupUI(findViewById(R.id.activity), this);

        getRatings();
    }

    public void back(View view){
        onBackPressed();
    }

    private void getRatings() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getRatings";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRatingsResponse(response);
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
                params.put("store_id", "0");
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseRatingsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                ratingArrayList.clear();
                float ratings = 0.0f;
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Rating rating = new Rating();
                    rating.setIdx(object.getInt("id"));
                    rating.setStoreId(object.getInt("store_id"));
                    rating.setUserId(object.getInt("member_id"));
                    rating.setUserName(object.getString("member_name"));
                    rating.setUserPictureUrl(object.getString("member_photo"));
                    rating.setRating(Float.parseFloat(object.getString("rating")));
                    rating.setSubject(object.getString("subject"));
                    rating.setDescription(object.getString("description"));
                    rating.setDate(object.getString("date_time"));
                    rating.setLang(object.getString("lang"));

                    ratingArrayList.add(rating);
                    ratings += rating.getRating();
                }

                int reviews = 0;
                float ratingVal = 0.0f;
                if(ratingArrayList.size() > 0){
                    reviews = ratingArrayList.size();
                    ratingVal = ratings/reviews;
                }

                ((TextView)findViewById(R.id.ratings)).setText(df.format(ratingVal));
                ((RatingBar)findViewById(R.id.ratingbar_small)).setRating(ratingVal);
                ((TextView)findViewById(R.id.reviews)).setText(String.valueOf(reviews));

                for(Rating rating:ratingArrayList){
                    if(rating.getStoreId() == 0 && rating.getUserId() == Commons.thisUser.get_idx()){
                        subjectBox.setText(rating.getSubject());
                        ratingBar.setRating(rating.getRating());
                        ratingBox.setText(String.valueOf(rating.getRating()));
                        feedbackBox.setText(rating.getDescription());
                        ((TextView)findViewById(R.id.caption1)).setText(getString(R.string.update_app_feedback));

                        if(rating.getLang().equals("ar")){
                            subjectBox.setGravity(Gravity.END);
                            feedbackBox.setGravity(Gravity.END);
                        }else {
                            subjectBox.setGravity(Gravity.START);
                            feedbackBox.setGravity(Gravity.START);
                        }

                        lang = rating.getLang();

                        return;
                    }

                    if(rating == ratingArrayList.get(ratingArrayList.size() - 1)){
                        ((TextView)findViewById(R.id.caption1)).setText(getString(R.string.feedback_app));
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void submitFeedback(View view){

        if(subjectBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.write_subject));
            return;
        }

        if(ratingBar.getRating() == 0){
            showToast(getString(R.string.please_rate_out_of_5_stars));
            return;
        }

        if(feedbackBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.write_feedback));
            return;
        }

        submitRating();
    }

    private void submitRating() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "placeAppFeedback";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseResponse(response);

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
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("store_id", "0");
                params.put("subject", subjectBox.getText().toString().trim());
                params.put("rating", String.valueOf(ratingBar.getRating()));
                params.put("description", feedbackBox.getText().toString().trim());
                if(lang.length() == 0)params.put("lang", Commons.lang);
                else params.put("lang", lang);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            if (success.equals("0")) {
                showToast(getString(R.string.feedback_submited));
                getRatings();
            }
        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

}










































