package com.myapp.qhome.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;

import java.text.DecimalFormat;

public class OrderPlacedActivity extends BaseActivity {

    public static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);

        ((TextView)findViewById(R.id.caption)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption5)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption6)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption7)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption8)).setTypeface(normal);
        ((TextView)findViewById(R.id.order_number)).setTypeface(normal);
        ((TextView)findViewById(R.id.order_date)).setTypeface(normal);
        ((TextView)findViewById(R.id.order_status)).setTypeface(normal);
        ((TextView)findViewById(R.id.order_price)).setTypeface(normal);

        String orderID = getIntent().getStringExtra("orderID");
        String orderDate = getIntent().getStringExtra("order_date");

        ((TextView)findViewById(R.id.order_number)).setText(orderID);
        ((TextView)findViewById(R.id.order_date)).setText(orderDate);
        if(Commons.lang.equals("ar")){
            ((TextView)findViewById(R.id.order_price)).setText( getString(R.string.qr)+ " " + df.format(Commons.totalPrice));
        }else {
            ((TextView)findViewById(R.id.order_price)).setText(df.format(Commons.totalPrice) + " " + getString(R.string.qr));
        }

    }

    public void toContactUs(View view){
        Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
        startActivity(intent);
    }

    public void toHome(View view){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
        finish();
    }
}

































