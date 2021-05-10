package com.myapp.qhome.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.MyStoreListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Store;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class MyStoreListActivity extends BaseActivity{

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    ListView list;
    AVLoadingIndicatorView progressBar;

    ArrayList<Store> stores = new ArrayList<>();
    MyStoreListAdapter adapter = new MyStoreListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_store_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        title = (TextView)findViewById(R.id.title);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(true);
        ui_edtsearch.requestFocus();

        title.setTypeface(bold);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().trim().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });

        list = (ListView) findViewById(R.id.list);

        setupUI((FrameLayout)findViewById(R.id.activity), this);

    }

    public void search(View view){
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
    }

    public void cancelSearch(View view){
        cancelButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getStores();
    }

    private void getStores() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getStores";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUploadBusinessResponse(response);
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
                if(Commons.thisUser != null) params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                else params.put("member_id", "0");
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseUploadBusinessResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                Commons.myStores.clear();
                Commons.stores.clear();
                int myApprovedStores = 0;
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Store store = new Store();
                    store.setIdx(object.getInt("id"));
                    store.setUserId(object.getInt("member_id"));
                    store.setName(object.getString("name"));
                    store.setLogo_url(object.getString("logo_url"));
                    store.setCategory(object.getString("category"));
                    store.setCategory2(object.getString("category2"));
                    store.setDescription(object.getString("description"));
                    store.setRatings(Float.parseFloat(object.getString("ratings")));
                    store.setReviews(Integer.parseInt(object.getString("reviews")));
                    store.setRegistered_time(object.getString("registered_time"));
                    store.setStatus(object.getString("status"));
                    store.setLikes(object.getInt("likes"));
                    if(object.getString("isLiked").equals("yes"))
                        store.setLiked(true);
                    else if(object.getString("isLiked").equals("no"))
                        store.setLiked(false);
                    else if(object.getString("isLiked").length() == 0)
                        store.setLiked(false);

                    store.setAr_name(object.getString("ar_name"));
                    store.setAr_category(object.getString("ar_category"));
                    store.setAr_category2(object.getString("ar_category2"));
                    store.setAr_description(object.getString("ar_description"));

                    if(Commons.thisUser != null){
                        if(store.getUserId() == Commons.thisUser.get_idx()) {
                            Commons.myStores.add(store);
                            if(store.getStatus().equals("approved"))myApprovedStores = myApprovedStores + 1;
                        }
                    }
                    if(store.getStatus().equals("approved"))
                        Commons.stores.add(store);
                }

                Commons.thisUser.set_stores(myApprovedStores);

                if(Commons.stores.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                adapter.setDatas(Commons.myStores);
                list.setAdapter(adapter);

                if(Commons.thisUser != null){
                    if(Commons.thisUser.get_role().equals("producer") && Commons.myStores.size() < 2){
                        ((ImageView)findViewById(R.id.btn_add_store)).setVisibility(View.VISIBLE);
                    }else {
                        ((ImageView)findViewById(R.id.btn_add_store)).setVisibility(View.GONE);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void toNewStore(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterStoreActivity.class);
        startActivity(intent);
    }

}




































