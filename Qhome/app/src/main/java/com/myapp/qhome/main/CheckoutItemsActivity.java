package com.myapp.qhome.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CheckoutItemListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;

public class CheckoutItemsActivity extends BaseActivity {

    CheckoutItemListAdapter adapter = new CheckoutItemListAdapter(this);
    ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_items);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        list = (ListView)findViewById(R.id.list);

        adapter.setDatas(Commons.cartItems);
        list.setAdapter(adapter);
    }

    public void back(View view){
        onBackPressed();
    }
}
