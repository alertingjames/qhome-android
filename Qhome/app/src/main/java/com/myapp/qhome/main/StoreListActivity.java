package com.myapp.qhome.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.adapters.StoreListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.models.Store;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class StoreListActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    GridView list;
    AVLoadingIndicatorView progressBar;
    String category = "";

    ArrayList<Store> stores = new ArrayList<>();
    StoreListAdapter adapter = new StoreListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        category = getIntent().getStringExtra("category");

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        title = (TextView)findViewById(R.id.title);
        title.setTypeface(bold);
        title.setText(category);

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

        list = (GridView)findViewById(R.id.list);

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

        stores.clear();
        for(Store store: Commons.stores){
            if(Commons.lang.equals("ar")){
                if(store.getAr_category().equals(category)||store.getAr_category2().equals(category))stores.add(store);
            }else {
                if(store.getCategory().equals(category)||store.getCategory2().equals(category))stores.add(store);
            }
        }

        Collections.sort(stores);

        if(stores.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

        adapter.setDatas(stores);
        list.setAdapter(adapter);
    }

}



















































