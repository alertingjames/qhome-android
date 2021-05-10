package com.myapp.qhome.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.androidnetworking.interfaces.UploadProgressListener;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.StoreListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.classes.CustomGridView;
import com.myapp.qhome.classes.CustomTypefaceSpan;
import com.myapp.qhome.classes.OrderStatus;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Store;
import com.myapp.qhome.preference.PrefConst;
import com.myapp.qhome.preference.Preference;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    ImageView searchButton, cancelButton;
    public LinearLayout searchBar, companyButton;
    EditText ui_edtsearch;
    TextView title;
    CustomGridView list;
    NavigationView navigationView;
    public AVLoadingIndicatorView progressBar;
    LinearLayout notiFrame, notiLayout;
    TextView ad1, ad2, ad3, categoriesButton;
    boolean isNotified = false;
    public ImageButton likeButton;
    public int storeId = 0;
    RoundedImageView adImage1, adImage2, adImage3;
    View adView;
    FrameLayout adHolder1, adHolder2, adHolder3;

    StoreListAdapter adapter = new StoreListAdapter(this);

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.SET_TIME,
            android.Manifest.permission.READ_PHONE_STATE,
//            android.Manifest.permission.RECORD_AUDIO,
//            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.WAKE_LOCK,
//            android.Manifest.permission.READ_CALENDAR,
//            android.Manifest.permission.WRITE_CALENDAR,
//            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_CONTACTS,
//            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.CALL_PRIVILEGED,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            android.Manifest.permission.LOCATION_HARDWARE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkAllPermission();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        wakeLock.release();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Commons.homeActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
//        notiFrame = (LinearLayout)findViewById(R.id.notiFrame);
//        notiLayout = (LinearLayout)findViewById(R.id.notiLayout);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        title = (TextView)findViewById(R.id.title);
        title.setTypeface(bold);

        notiFrame = (LinearLayout)findViewById(R.id.notiFrame);
        notiLayout = (LinearLayout)findViewById(R.id.notiLayout);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(true);
        ui_edtsearch.requestFocus();

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

        list = (CustomGridView)findViewById(R.id.list);

        adImage1 = (RoundedImageView)findViewById(R.id.img_ad1) ;
        adImage2 = (RoundedImageView)findViewById(R.id.img_ad2) ;
        adImage3 = (RoundedImageView)findViewById(R.id.img_ad3) ;
        adView = (View)findViewById(R.id.view_ad1);

        ad1 = (TextView)findViewById(R.id.txt_hint1);
        ad2 = (TextView)findViewById(R.id.txt_hint2);
        ad3 = (TextView)findViewById(R.id.txt_hint3);

        adHolder1 = (FrameLayout)findViewById(R.id.adHolder1);
        adHolder2 = (FrameLayout)findViewById(R.id.adHolder2);
        adHolder3 = (FrameLayout)findViewById(R.id.adHolder3);

        categoriesButton = (TextView)findViewById(R.id.btn_category);
        ad1.setTypeface(bold);
        ad2.setTypeface(normal);
        ad3.setTypeface(normal);
        categoriesButton.setTypeface(bold);

        Picasso.with(getApplicationContext())
                .load(R.mipmap.appicon)
                .error(R.mipmap.appicon)
                .placeholder(R.mipmap.appicon)
                .into((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.avatar));
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.name)).setText(getString(R.string.app_name));

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.name)).setTypeface(bold);

        setupUI(findViewById(R.id.activity), this);

        changeMenuFonts();

        if(Commons.thisUser != null && Commons.thisUser.get_auth_status().equals("verified") && !Commons.thisUser.get_status().equals("removed")) {
            navigationView.getMenu().getItem(0).setVisible(false);
//            navigationView.getMenu().getItem(3).setVisible(true);
            navigationView.getMenu().getItem(6).setVisible(true);
        }else {
            if (Commons.thisUser != null && Commons.thisUser.get_status().equals("removed")){
                Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_EMAIL, "");
                Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_ROLE, "");
                Commons.thisUser = null;
                navigationView.getMenu().getItem(0).setVisible(true);
                navigationView.getMenu().getItem(6).setVisible(false);
                showAlertDialog(getString(R.string.warning), getString(R.string.account_deleted), this, new Callable<Void>() {
                    public Void call() {
                        return null;
                    }
                });
            }
            navigationView.getMenu().getItem(0).setVisible(true);
//            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);
        }

        if(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_IMEI, "").length() == 0){
            Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_IMEI, getDeviceIMEI());
        }

        Commons.IMEI = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_IMEI, "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                getNotifications();
                registerGuest();
            }
        }).start();

        companyButton = (LinearLayout)findViewById(R.id.companyButton);
        companyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompanyActivity.class);
                startActivity(intent);
            }
        });
    }

    private void func(){
        Intent intent = new Intent(getApplicationContext(), RegisterStoreActivity.class);
        startActivity(intent);
    }

    public void getNotifications(){
        if(notiLayout.getChildCount() > 0) notiLayout.removeAllViews();
        // if(Commons.isNotificationEnabled && Commons.thisUser != null && Commons.thisUser.get_role().equals("producer"))
        if(Commons.thisUser != null && Commons.thisUser.get_role().equals("producer")) {
            getCustomerNotification();
        }
        if(Commons.thisUser != null){
            getProducerNotification();
        }

        getAdminNotification();
    }

    @Override
    public void onResume() {
        super.onResume();

        Commons.minPriceVal = 0;
        Commons.maxPriceVal = Constants.MAX_PRODUCT_PRICE;
        Commons.priceSort = 0;
        Commons.nameSort = 0;

        Commons.isNotificationEnabled = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_NOTIFICATION, false);
        initOrderStatus();
        getStores();
        getCart();
        getAds();

        if(Commons.thisUser != null){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("HomeActivity:", "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            Log.d("Token!!!", token);
                            uploadNewToken(token);
                        }
                    });
        }
    }

    public void adHere1(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void adHere2(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void adHere3(View view){
        if(Commons.thisUser != null){
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void openCategory(View view){
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        startActivity(intent);
    }

    private void changeMenuFonts(){

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.name)).setTypeface(bold);

        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

    }

    private void applyFontToMenuItem(MenuItem mi) {
        int size = 16;
        float scaledSizeInPixels = size * getResources().getDisplayMetrics().scaledDensity;
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan(mi.getTitle().toString(), bold, scaledSizeInPixels), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
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
        ui_edtsearch.setText("");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        displaySelectedScreen(menuItem.getItemId());
        return false;
    }

    private void displaySelectedScreen(int itemId) {
        switch (itemId) {
            case R.id.login:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.favorite:
                if(Commons.thisUser == null){
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.orders:
                if(Commons.thisUser != null){
                    intent = new Intent(getApplicationContext(), OrdersActivity.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.notifications:
                intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
                break;
            case R.id.manage_account:
                intent = new Intent(getApplicationContext(), ManageAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.help:
                intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.company:
                showAlertDialogCompanyLogin(this);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void logout(){
        Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_EMAIL, "");
        Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_ROLE, "");
        Commons.thisUser = null;
        navigationView.getMenu().getItem(0).setVisible(true);
        navigationView.getMenu().getItem(6).setVisible(false);
        showToast(getString(R.string.logged_out));
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
                    store.setPriceId(Integer.parseInt((object.getString("price_id").equals(""))? "0":object.getString("price_id")));

                    if(Commons.thisUser != null && Commons.thisUser.get_role().equals("producer")){
                        if(store.getUserId() == Commons.thisUser.get_idx()) {
                            Commons.myStores.add(store);
                            if(store.getStatus().equals("approved"))myApprovedStores = myApprovedStores + 1;
                        }
                    }
                    if(store.getStatus().equals("approved"))
                        Commons.stores.add(store);
                }

                if(Commons.thisUser != null && Commons.thisUser.get_role().equals("producer"))Commons.thisUser.set_stores(myApprovedStores);

                if(Commons.stores.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                adapter.setDatas(Commons.stores);
                list.setAdapter(adapter);
//                Log.d("MMMMMMMM!!!", String.valueOf((int)  Math.ceil((double)Commons.stores.size() / 2)));

                if(Commons.thisUser != null){
                    if(Commons.thisUser.get_role().equals("producer") && Commons.thisUser.get_stores() == 0 && Commons.myStores.size() < 2){
                        if(!isNotified){
                            showAlertDialog(getString(R.string.hint), getString(R.string.register_store_text), this, new Callable<Void>() {
                                public Void call() {
                                    func();
                                    return null;
                                }
                            });
                            isNotified = true;
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void likeStore(String storeId) {

        String url = ReqConst.SERVER_URL + "likeStore";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetLikeStoreResponse(response);

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
                params.put("store_id", storeId);
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetLikeStoreResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                if(likeButton != null){
                    likeButton.setBackgroundResource(R.drawable.ic_liked);
                    for(Store store: Commons.stores){
                        if(store.getIdx() == storeId){
                            store.setLiked(true);
                            store.setLikes(store.getLikes() + 1);
                        }
                    }
                }
            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    public void unLikeStore(String storeId) {

        String url = ReqConst.SERVER_URL + "unLikeStore";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetUnlikeStoreResponse(response);

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
                params.put("store_id", storeId);
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetUnlikeStoreResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                if(likeButton != null){
                    likeButton.setBackgroundResource(R.drawable.ic_like);
                    for(Store store: Commons.stores){
                        if(store.getIdx() == storeId){
                            store.setLiked(false);
                            if(store.getLikes() > 0)store.setLikes(store.getLikes() - 1);
                        }
                    }
                }
            } else {
                showToast(getString(R.string.server_issue));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.server_issue));
            e.printStackTrace();
        }
    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    private void initOrderStatus(){
        Commons.orderStatus.initOrderStatus(HomeActivity.this);
    }

    public void showNotiFrame(View view){
        if(notiLayout.getChildCount() > 0){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_in);
            notiFrame.setAnimation(animation);
            notiFrame.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((View)findViewById(R.id.notiBackground)).setVisibility(View.VISIBLE);
                }
            }, 200);
        }
    }

    private void getCustomerNotification(){
        Log.d("Customer NOTI!!!", String.valueOf(Commons.thisUser.get_idx()));

        Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "order/" + String.valueOf(Commons.thisUser.get_idx()));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                try{
                    LayoutInflater inflater = getLayoutInflater();
                    View myLayout = inflater.inflate(R.layout.notification_layout, null);
                    String noti = map.get("msg").toString();   Log.d("Customer Noti!!!", noti);
                    String time = map.get("date").toString();
                    String fromid = map.get("fromid").toString();
                    String fromname = map.get("fromname").toString();
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING,200);
                    noti = "Customer's new order: " + fromname;
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String date = dateFormat.format(new Date(Long.parseLong(time)));
                    ((TextView)myLayout.findViewById(R.id.date)).setText(date);
                    ((TextView)myLayout.findViewById(R.id.name)).setText(fromname);
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    ((TextView)myLayout.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }

                            Intent intent = new Intent(getApplicationContext(), ManageAccountActivity.class);
                            startActivity(intent);
                        }
                    });

                    ((ImageView)myLayout.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });
                    notiLayout.addView(myLayout);
                    ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                    ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getProducerNotification(){

        Log.d("Producer NOTI!!!", String.valueOf(Commons.thisUser.get_idx()));

        Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "order_upgrade/" + String.valueOf(Commons.thisUser.get_idx()));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                try{
                    LayoutInflater inflater = getLayoutInflater();
                    View myLayout = inflater.inflate(R.layout.notification_layout, null);
                    String noti = map.get("msg").toString();     Log.d("Producer Noti!!!", noti);
                    String time = map.get("date").toString();
                    String fromid = map.get("fromid").toString();
                    String fromname = map.get("fromname").toString();
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING,200);
//                    noti = "Customer's new order: " + fromname;
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String date = dateFormat.format(new Date(Long.parseLong(time)));
                    ((TextView)myLayout.findViewById(R.id.date)).setText(date);
                    ((TextView)myLayout.findViewById(R.id.name)).setText(fromname);
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    ((TextView)myLayout.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }

                            Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                            startActivity(intent);
                        }
                    });

                    ((ImageView)myLayout.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });
                    notiLayout.addView(myLayout);
                    ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                    ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getAdminNotification(){

        Firebase ref;
        if(Commons.thisUser != null)ref = new Firebase(ReqConst.FIREBASE_URL + "admin/" + String.valueOf(Commons.thisUser.get_idx()));
        else ref = new Firebase(ReqConst.FIREBASE_URL + "admin/" + Commons.IMEI);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                try{
                    LayoutInflater inflater = getLayoutInflater();
                    View myLayout = inflater.inflate(R.layout.notification_layout, null);
                    String noti = map.get("msg").toString();   Log.d("Customer Noti!!!", noti);
                    String image = map.get("img").toString();
                    String time = map.get("date").toString();
                    String fromid = map.get("fromid").toString();
                    String fromname = map.get("fromname").toString();
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING,200);
                    if(image.length() > 0){
                        ((LinearLayout)myLayout.findViewById(R.id.imageFrame)).setVisibility(View.VISIBLE);
                        Picasso.with(getApplicationContext())
                                .load(image)
                                .error(R.drawable.noresult)
                                .placeholder(R.drawable.noresult)
                                .into((ImageView)myLayout.findViewById(R.id.image));
                        ((ImageView)myLayout.findViewById(R.id.image)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                                intent.putExtra("image", image);
                                ActivityOptionsCompat options = ActivityOptionsCompat.
                                        makeSceneTransitionAnimation(HomeActivity.this, v, getString(R.string.transition));
                                startActivity(intent, options.toBundle());
                            }
                        });

                        ((ImageView)myLayout.findViewById(R.id.btn_download)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                                intent.putExtra("image", image);
                                ActivityOptionsCompat options = ActivityOptionsCompat.
                                        makeSceneTransitionAnimation(HomeActivity.this, v, getString(R.string.transition));
                                startActivity(intent, options.toBundle());
                            }
                        });
                    }else ((LinearLayout)myLayout.findViewById(R.id.imageFrame)).setVisibility(View.GONE);
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String date = dateFormat.format(new Date(Long.parseLong(time)));
                    ((TextView)myLayout.findViewById(R.id.date)).setText(date);
                    ((TextView)myLayout.findViewById(R.id.name)).setText("Qhome");
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    ((TextView)myLayout.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                            if(noti.toLowerCase().contains("store")){
                                getStores();
                            }else if(noti.toLowerCase().contains("lucky") && !noti.toLowerCase().contains("order")){
                                Intent intent = new Intent(getApplicationContext(), LuckyDrawActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                    ((ImageView)myLayout.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });
                    notiLayout.addView(myLayout);
                    ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                    ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void dismissNotiFrame(){
        if(notiFrame.getVisibility() == View.VISIBLE){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_out);
            notiFrame.setAnimation(animation);
            notiFrame.setVisibility(View.GONE);
            ((View)findViewById(R.id.notiBackground)).setVisibility(View.GONE);
        }
    }

    public void dismissNotiFrame(View view){
        dismissNotiFrame();
    }

    public void toCart(View view){
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(intent);
    }

    private void getCart() {
        String url = ReqConst.SERVER_URL + "getCartItems";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetCartResponse(response);
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
                params.put("imei_id", Commons.IMEI);
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetCartResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                Commons.cartItems.clear();
                Commons.cartItemsCount = 0;
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    CartItem cartItem = new CartItem();
                    cartItem.setId(object.getInt("id"));
                    cartItem.setImei_id(object.getString("imei_id"));
                    cartItem.setProducer_id(object.getInt("producer_id"));
                    cartItem.setStore_id(object.getInt("store_id"));
                    cartItem.setStore_name(object.getString("store_name"));
                    cartItem.setAr_store_name(object.getString("ar_store_name"));
                    cartItem.setPicture_url(object.getString("picture_url"));
                    cartItem.setCategory(object.getString("category"));
                    cartItem.setAr_category(object.getString("ar_category"));
                    cartItem.setProduct_id(object.getInt("product_id"));
                    cartItem.setProduct_name(object.getString("product_name"));
                    cartItem.setAr_product_name(object.getString("ar_product_name"));
                    cartItem.setPrice(Double.parseDouble(object.getString("price")));
                    cartItem.setUnit(object.getString("unit"));
                    cartItem.setQuantity(Integer.parseInt(object.getString("quantity")));

                    Commons.cartItems.add(cartItem);
                    Commons.cartItemsCount += cartItem.getQuantity();
                }

                if(Commons.cartItems.size() > 0){
                    ((FrameLayout)findViewById(R.id.countFrame)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count_cart)).setText(String.valueOf(Commons.cartItemsCount));
                }else ((FrameLayout)findViewById(R.id.countFrame)).setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getAds() {
        String url = ReqConst.SERVER_URL + "getAds";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetAdsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
            }
        }) {

        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetAdsResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                JSONObject object = response.getJSONObject("data");
                String adPicture1 = object.getString("adPic1");
                String adPicture2 = object.getString("adPic2");
                String adPicture3 = object.getString("adPic3");

                int adStoreId1 = Integer.parseInt(object.getString("adStore1").length() == 0?"0":object.getString("adStore1"));
                int adStoreId2 = Integer.parseInt(object.getString("adStore2").length() == 0?"0":object.getString("adStore2"));
                int adStoreId3 = Integer.parseInt(object.getString("adStore3").length() == 0?"0":object.getString("adStore3"));

                if(adPicture1.length() > 0){
                    adView.setVisibility(View.GONE);
                    ad1.setVisibility(View.GONE);
                    Picasso.with(getApplicationContext())
                            .load(adPicture1)
                            .error(R.drawable.adbg4)
                            .placeholder(R.drawable.adbg4)
                            .into(adImage1);
                    adHolder1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(Store store:Commons.stores){
                                if(store.getIdx() == adStoreId1){
                                    Commons.store = store;
                                    if(Commons.thisUser != null){
                                        if(store.getUserId() == Commons.thisUser.get_idx()){
                                            Intent intent = new Intent(getApplicationContext(), MyStoreDetailActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    });
                }else {
                    adHolder1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Commons.thisUser != null){
                                Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

                if(adPicture2.length() > 0){
                    ad2.setVisibility(View.GONE);
                    Picasso.with(getApplicationContext())
                            .load(adPicture2)
                            .resize(adImage2.getMeasuredWidth(), adImage2.getMeasuredWidth())
                            .centerCrop()
                            .error(R.drawable.adbg4)
                            .placeholder(R.drawable.adbg4)
                            .into(adImage2);
                    adHolder2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(Store store:Commons.stores){
                                if(store.getIdx() == adStoreId2){
                                    Commons.store = store;
                                    if(Commons.thisUser != null){
                                        if(store.getUserId() == Commons.thisUser.get_idx()){
                                            Intent intent = new Intent(getApplicationContext(), MyStoreDetailActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    });

                }else {
                    if(adPicture3.length() > 0) {
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.adbg4)
                                .resize(adImage3.getMeasuredWidth(), adImage3.getMeasuredWidth())
                                .centerCrop()
                                .error(R.drawable.adbg4)
                                .placeholder(R.drawable.adbg4)
                                .into(adImage2);
                    }
                    adHolder2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Commons.thisUser != null){
                                Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

                if(adPicture3.length() > 0){
                    ad3.setVisibility(View.GONE);
                    Picasso.with(getApplicationContext())
                            .load(adPicture3)
                            .resize(adImage3.getMeasuredWidth(), adImage3.getMeasuredWidth())
                            .centerCrop()
                            .error(R.drawable.adbg5)
                            .placeholder(R.drawable.adbg5)
                            .into(adImage3);
                    adHolder3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(Store store:Commons.stores){
                                if(store.getIdx() == adStoreId3){
                                    Commons.store = store;
                                    if(Commons.thisUser != null){
                                        if(store.getUserId() == Commons.thisUser.get_idx()){
                                            Intent intent = new Intent(getApplicationContext(), MyStoreDetailActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    });
                }else {
                    if(adPicture2.length() > 0) {
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.adbg5)
                                .resize(adImage2.getMeasuredWidth(), adImage2.getMeasuredWidth())
                                .centerCrop()
                                .error(R.drawable.adbg5)
                                .placeholder(R.drawable.adbg5)
                                .into(adImage3);
                    }
                    adHolder3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Commons.thisUser != null){
                                Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerGuest() {
        String url = ReqConst.SERVER_URL + "regGuest";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());
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
                params.put("imei_id", Commons.IMEI);
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void uploadNewToken(String token) {

        String url = ReqConst.SERVER_URL + "uploadfcmtoken";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUploadTokenResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast("Server issue");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("fcm_token", token);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);

    }

    public void parseUploadTokenResponse(String json) {
        try {
            JSONObject response = new JSONObject(json);   Log.d("RESP=====> :",response.toString());

            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

            } else {
                showToast("Server issue");
            }

        } catch (JSONException e) {
            showToast("Server issue");
            e.printStackTrace();
        }
    }

    public void showAlertDialogCompanyLogin(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_comlogin, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        EditText passwordBox = (EditText) view.findViewById(R.id.passwordBox);
        passwordBox.setTypeface(normal);
        TextView submitButton = (TextView)view.findViewById(R.id.btn_submit);
        submitButton.setTypeface(bold);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(passwordBox.getText().toString().length() == 0){
                        showToast(getString(R.string.enter_password));
                        return;
                    }
                    comlogin(passwordBox.getText().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        ImageView cancelButton = (ImageView)view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void comlogin(String pwd){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "comlogin")
                .addBodyParameter("password", pwd)
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
                                Intent intent = new Intent(getApplicationContext(), CAdminActivity.class);
                                startActivity(intent);
                            }else if(result.equals("1")){
                                showToast(getString(R.string.incorrect_password));
                            }else {
                                showToast(getString(R.string.server_issue));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });
    }

}







































