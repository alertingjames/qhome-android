package com.myapp.qhome.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.PictureListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.models.Picture;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadProductActivity extends BaseActivity {

    ViewPager pager;
    PageIndicatorView pageIndicatorView;
    ArrayList<Picture> pictures = new ArrayList<>();
    PictureListAdapter adapter = new PictureListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);

        pager = findViewById(R.id.viewPager);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);

    }

    public void back(View view){
        onBackPressed();
    }

    public void showOptions(View view){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    Bitmap bitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //From here you can load the image however you need to, I recommend using the Glide library
                File imageFile = new File(resultUri.getPath());
                Picture picture = new Picture();
                picture.setFile(imageFile);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    picture.setBitmap(bitmap);
                    picture.setIdx(pictures.size());
                    pictures.add(picture);
                    adapter.setDatas(pictures);
                    adapter.notifyDataSetChanged();
                    pager.setAdapter(adapter);
//                    if(btn_next.getVisibility() ==  View.GONE){
//                        btn_next.setVisibility(View.VISIBLE);
//                    }
                    if(((FrameLayout)findViewById(R.id.imageFrame)).getVisibility() == View.GONE)
                        ((FrameLayout)findViewById(R.id.imageFrame)).setVisibility(View.VISIBLE);
                    pager.setCurrentItem(pictures.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void delCurrentItem(View view){
        if(pictures.size() > 0){
            pictures.remove(pager.getCurrentItem());
            if(pictures.size() == 0)
                ((FrameLayout)findViewById(R.id.imageFrame)).setVisibility(View.GONE);
            adapter.setDatas(pictures);
            adapter.notifyDataSetChanged();
            pager.setAdapter(adapter);
        }
    }
}
















































