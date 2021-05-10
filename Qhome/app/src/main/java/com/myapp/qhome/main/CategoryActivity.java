package com.myapp.qhome.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CategoryListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.classes.CustomGridView;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.models.Category;

import java.util.ArrayList;
import java.util.Locale;

public class CategoryActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    public LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView cap;
    CustomGridView list;
    public FrameLayout others;
    ArrayList<Category> categories = new ArrayList<>();
    CategoryListAdapter adapter = new CategoryListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Commons.categoryActivity = this;
        others = (FrameLayout)findViewById(R.id.others);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);

        cap = (TextView)findViewById(R.id.caption);
//        cap.setTypeface(bold);

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

        int[] logos = {
                R.drawable.food2,
                R.drawable.drinks1,
                R.drawable.sweets1,
                R.drawable.stationery2,
                R.drawable.accessories,
                R.drawable.perfumes2,
        };

        String[] names = {
                getString(R.string.food),
                getString(R.string.drinks),
                getString(R.string.sweets),
                getString(R.string.stationery),
                getString(R.string.accessories),
                getString(R.string.perfumes),
        };

        for(int i=0; i<logos.length; i++){
            Category category = new Category();
            category.setIdx(i + 1);
            category.setName(names[i]);
            category.setLogo_res(logos[i]);

            categories.add(category);
        }

        adapter.setDatas(categories);
        list.setAdapter(adapter);

        setupUI(findViewById(R.id.activity), this);

    }

    public void search(View view){
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        cap.setVisibility(View.GONE);
    }

    public void cancelSearch(View view){
        cancelButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        cap.setVisibility(View.VISIBLE);
        ui_edtsearch.setText("");
    }

    public void toFilter(View view){
        Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
        startActivity(intent);
    }

    public void getOtherStores(View view){
        Intent intent = new Intent(getApplicationContext(), StoreListActivity.class);
        intent.putExtra("category", getString(R.string.others));
        startActivity(intent);
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}






































