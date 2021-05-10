package com.myapp.qhome.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
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
import com.myapp.qhome.adapters.FavoritesAdapter;
import com.myapp.qhome.adapters.MyStoreListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.Order;
import com.myapp.qhome.models.Store;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class FavoritesActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    GridView list;
    AVLoadingIndicatorView progressBar;

    ArrayList<Store> stores = new ArrayList<>();
    FavoritesAdapter adapter = new FavoritesAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

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

        list = (GridView) findViewById(R.id.list);

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
        getFavoriteStores();
    }

    private void getFavoriteStores() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "favoriteStores";
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
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
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
                stores.clear();
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

                    stores.add(store);
                }

                if(stores.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                adapter.setDatas(stores);
                list.setAdapter(adapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Store pStore = null;

    public void deleteFavStore(Store store){
        pStore = store;
        showAlertDialogForQuestion(getString(R.string.warning), getString(R.string.del_text), this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                String url = ReqConst.SERVER_URL + "unLikeStore";
                StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("REST response========>", response);
                        VolleyLog.v("Response:%n %s", response.toString());
                        parseDeleteResponse(response);

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
                        params.put("store_id", String.valueOf(store.getIdx()));
                        params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                        return params;
                    }
                };

                post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                        0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                QhomeApp.getInstance().addToRequestQueue(post, url);

                return null;
            }
        });
    }

    public void parseDeleteResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                int index = stores.indexOf(pStore);
                stores.remove(index);

                adapter.setDatas(stores);
                if(adapter.getCount() == 0){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


















































