package com.myapp.qhome.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.classes.ScrollViewExt;
import com.myapp.qhome.classes.ScrollViewListener;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.preference.PrefConst;
import com.myapp.qhome.preference.Preference;

public class TermsActivity extends BaseActivity implements ScrollViewListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.text)).setTypeface(normal);
        ((TextView)findViewById(R.id.btn_accept)).setTypeface(bold);

        ((ScrollViewExt)findViewById(R.id.scrollView)).setScrollViewListener(this);
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            // do stuff
            ((TextView)findViewById(R.id.btn_accept)).setVisibility(View.VISIBLE);
        }
    }

    public void acceptTerms(View view){
        Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_READ_TERMS, "read");
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
