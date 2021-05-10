package com.myapp.qhome.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.classes.OnSwipeTouchListener;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class PictureViewActivity extends BaseActivity {

    private PhotoView mPhotoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        mPhotoView = findViewById(R.id.iv_photo);

        String url = getIntent().getStringExtra("url");

        Picasso.with(getApplicationContext()).load(url).into(mPhotoView);

    }
}
