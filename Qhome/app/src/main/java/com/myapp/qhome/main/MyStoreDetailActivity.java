package com.myapp.qhome.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.opengl.Visibility;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.QhomeApp;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CompanyPriceListAdapter;
import com.myapp.qhome.adapters.PictureListAdapter;
import com.myapp.qhome.adapters.ProductListAdapter;
import com.myapp.qhome.adapters.RatingListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.comparators.ProductChainedComparator;
import com.myapp.qhome.comparators.ProductNameComparator;
import com.myapp.qhome.comparators.ProductPriceComparator;
import com.myapp.qhome.models.CompanyPrice;
import com.myapp.qhome.models.Picture;
import com.myapp.qhome.models.Product;
import com.myapp.qhome.models.Rating;
import com.myapp.qhome.models.Store;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class MyStoreDetailActivity extends BaseActivity {

    EditText nameBox, descriptionBox, productNameBox, productDescBox, productARNameBox, productARDescriptionBox, productPriceBox;
    EditText storeARNameBox, storeARDescriptionBox;
    EditText ui_edtsearch;
    TextView title, categoryBox, logoButton, storeNameBox;
    TextView storeARCategoryBox, productCategoryBox, productARCategoryBox;
    ImageView categoryButton, statusBox;
    TextView editButton;
    RoundedImageView logo, storeLogoBox;
    AVLoadingIndicatorView progressBar;
    boolean editF = false;

    CheckBox companyCheckBox;
    public int priceId = 0;

    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<String> arCategoryList = new ArrayList<>();

    ArrayList<String> selectedCategoryList = new ArrayList<>();
    ArrayList<String> selectedARCategoryList = new ArrayList<>();

    ViewPager pager;
    PageIndicatorView pageIndicatorView;
    ArrayList<Picture> pictures = new ArrayList<>();
    PictureListAdapter adapter = new PictureListAdapter(this);

    GridView productList;
    ArrayList<Product> products = new ArrayList<>();
    ProductListAdapter productListAdapter = new ProductListAdapter(this);

    ListView ratingsList;
    ArrayList<Rating> ratings = new ArrayList<>();
    RatingListAdapter ratingListAdapter = new RatingListAdapter(this);

    ArrayList<CompanyPrice> prices = new ArrayList<>();
    CompanyPriceListAdapter priceListAdapter = new CompanyPriceListAdapter(this);

    ListView listView;
    LinearLayout companyPriceLayout;
    ImageView cancelButton;

    FrameLayout darkBg;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_store_detail);

        Commons.myStoreDetailActivity = this;

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        nameBox = (EditText)findViewById(R.id.storeNameBox);
        descriptionBox = (EditText)findViewById(R.id.descriptionBox);
        categoryBox = (TextView) findViewById(R.id.storeCategoryBox);
        statusBox = (ImageView) findViewById(R.id.statusBox);
        logoButton = (TextView) findViewById(R.id.btn_add_logo);
        editButton = (TextView) findViewById(R.id.btn_edit);
        editButton.setPaintFlags(editButton.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        editButton.setTypeface(bold);
        categoryButton = (ImageView) findViewById(R.id.btn_category);
        logo = (RoundedImageView) findViewById(R.id.logo);

        storeARNameBox = (EditText)findViewById(R.id.storeARNameBox);
        storeARCategoryBox = (TextView) findViewById(R.id.storeARCategoryBox);
        storeARDescriptionBox = (EditText)findViewById(R.id.storeARDescriptionBox);

        storeLogoBox = (RoundedImageView) findViewById(R.id.store_logo);
        storeNameBox = (TextView) findViewById(R.id.store_name);
        productNameBox = (EditText)findViewById(R.id.productNameBox);
        productPriceBox = (EditText)findViewById(R.id.productPriceBox);
        productCategoryBox = (TextView) findViewById(R.id.productCategoryBox);
        productDescBox = (EditText)findViewById(R.id.productDescBox);

        productARNameBox = (EditText) findViewById(R.id.productARNameBox);
        productARCategoryBox = (TextView) findViewById(R.id.productARCategoryBox);
        productARDescriptionBox = (EditText)findViewById(R.id.productARDescriptionBox);

        title = (TextView)findViewById(R.id.title);
        title.setTypeface(bold);

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
                productListAdapter.filter(text);

            }
        });

        productList = (GridView)findViewById(R.id.list);
        ratingsList = (ListView) findViewById(R.id.ratingsList);

        if(Commons.store.getCategory2().length() == 0)((ImageView)findViewById(R.id.btn_filter)).setVisibility(View.GONE);
        else ((ImageView)findViewById(R.id.btn_filter)).setVisibility(View.VISIBLE);

        detail();

        setupUI(findViewById(R.id.activity), this);

        pager = findViewById(R.id.viewPager);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);

        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption5)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption6)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption7)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption8)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption9)).setTypeface(normal);

        ((RatingBar)findViewById(R.id.ratingbar)).setRating(Commons.store.getRatings());
        ((TextView)findViewById(R.id.ratings)).setText(String.valueOf(Commons.store.getRatings()));
        ((TextView)findViewById(R.id.reviews)).setText(String.valueOf(Commons.store.getReviews()));
        ((TextView)findViewById(R.id.reviews_cap)).setTypeface(normal);

        companyCheckBox = (CheckBox)findViewById(R.id.companyBox);
        if(Commons.store.getPriceId() > 0){
            companyCheckBox.setChecked(true);
        }else {
            companyCheckBox.setChecked(false);
        }

        priceId = Commons.store.getPriceId();

        listView = (ListView)findViewById(R.id.plist);
        companyPriceLayout = (LinearLayout)findViewById(R.id.compriceBox) ;
        cancelButton = (ImageView)findViewById(R.id.btn_cancel);

        darkBg = (FrameLayout)findViewById(R.id.bg_dark);

        companyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(editF){
                    if (isChecked){
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
                        companyPriceLayout.setAnimation(animation);
                        companyPriceLayout.setVisibility(View.VISIBLE);
                        darkBg.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(Commons.store.getPriceId() > 0){
                        companyCheckBox.setChecked(true);
                    }else {
                        companyCheckBox.setChecked(false);
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPriceLayout();
                companyCheckBox.setChecked(false);
                priceId = 0;
            }
        });

        getComprices();
    }

    public void dismissPriceLayout(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
        companyPriceLayout.setAnimation(animation);
        companyPriceLayout.setVisibility(View.GONE);
        darkBg.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Commons.myStoreDetailActivity = null;
    }

    public void delStore(View view){
        showAlertDialogForQuestion(getString(R.string.warning), getString(R.string.delete_store_text), this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                return null;
            }
        });
    }

    public void show(View view){
        if(Commons.store.getStatus().equals("approved"))showToast(getString(R.string.approved));
        else if(Commons.store.getStatus().equals("declined"))showToast(getString(R.string.declined));
        else showToast(getString(R.string.processing));
    }

    public void back(View view){
        onBackPressed();
    }

    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        imageFile = null;
        ((View)findViewById(R.id.indicator_detail)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_new_product)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_products)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_ratings)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_detail)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_new_product)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_products)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_ratings)).setTypeface(normal);

        ((TextView)findViewById(R.id.txt_detail)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_new_product)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_products)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_ratings)).setTextColor(getColor(R.color.lightPrimary));

        ((FrameLayout)findViewById(R.id.detailFrame)).setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.newProductFrame)).setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.productsFrame)).setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.ratingsFrame)).setVisibility(View.GONE);
    }

    private void tabFocus(LinearLayout tabLayout){
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView)findViewById(R.id.hScrollView);
        int x = tabLayout.getLeft();
        int y = tabLayout.getTop();
        horizontalScrollView.scrollTo(x, y);
    }

    public void selDetail(View view){
        detail();
    }

    private void detail(){
        clearIndicators();
        editF = false;
        ((View)findViewById(R.id.indicator_detail)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_detail)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_detail)).setTextColor(getColor(R.color.colorPrimary));
        ((FrameLayout)findViewById(R.id.detailFrame)).setVisibility(View.VISIBLE);
        tabFocus(((LinearLayout)findViewById(R.id.lyt_detail)));

        Picasso.with(getApplicationContext()).load(Commons.store.getLogo_url()).into(logo);
        logoButton.setVisibility(View.GONE);
        editButton.setText(getString(R.string.edit));
        nameBox.setText(Commons.store.getName());
        nameBox.setEnabled(false);
        descriptionBox.setText(Commons.store.getDescription());
        descriptionBox.setEnabled(false);

        String cat2 = "";
        String cap = "";
        if(Commons.store.getCategory2().length() > 0) {
            cap = "1.";
            cat2 = " 2." + Commons.store.getCategory2();
        }
        categoryBox.setText(cap + Commons.store.getCategory() + cat2);
        categoryList.clear();
        categoryList.add(Commons.store.getCategory());
        categoryList.add(Commons.store.getCategory2());
        categoryButton.setVisibility(View.GONE);

        title.setText(getString(R.string.my_store));
        ((TextView)findViewById(R.id.likes)).setText(String.valueOf(Commons.store.getLikes()));

        storeARNameBox.setText(Commons.store.getAr_name());
        storeARNameBox.setEnabled(false);
        String aCat2 = "";
        if(Commons.store.getAr_category2().length() > 0) {
            if(Commons.lang.equals("ar")){
                storeARCategoryBox.setText("1." + Commons.store.getAr_category() + " 2." + Commons.store.getAr_category2());
            }else {
                storeARCategoryBox.setText(Commons.store.getAr_category2() + ".2 " + Commons.store.getAr_category() + ".1");
            }
        }else {
            storeARCategoryBox.setText(Commons.store.getAr_category());
        }

        arCategoryList.clear();
        arCategoryList.add(Commons.store.getAr_category());
        arCategoryList.add(Commons.store.getAr_category2());
        storeARDescriptionBox.setText(Commons.store.getAr_description());
        storeARDescriptionBox.setEnabled(false);

        if(Commons.store.getStatus().equals("approved")){
            if(Commons.lang.equals("ar"))statusBox.setImageResource(R.drawable.ic_checked_flip);
            else statusBox.setImageResource(R.drawable.ic_checked);
        }
        else if(Commons.store.getStatus().equals("declined"))statusBox.setImageResource(R.drawable.cancelicon_marron);
        else statusBox.setImageResource(R.drawable.ic_flow);

    }

    public void selNewProduct(View view){
        if(Commons.store.getStatus().equals("approved")){
            clearIndicators();
            ((View)findViewById(R.id.indicator_new_product)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.txt_new_product)).setTypeface(bold);
            ((TextView)findViewById(R.id.txt_new_product)).setTextColor(getColor(R.color.colorPrimary));
            ((FrameLayout)findViewById(R.id.newProductFrame)).setVisibility(View.VISIBLE);
            tabFocus(((LinearLayout)findViewById(R.id.lyt_new_product)));
            Picasso.with(getApplicationContext()).load(Commons.store.getLogo_url()).into(storeLogoBox);
            if(Commons.lang.equals("ar"))storeNameBox.setText(Commons.store.getAr_name());
            else storeNameBox.setText(Commons.store.getName());

            title.setText(getString(R.string.my_store));

        }else {
            if(Commons.store.getStatus().equals("declined"))
                showAlertDialog(getString(R.string.warning), getString(R.string.store_declined_text), this, null);
            else showAlertDialog(getString(R.string.warning), getString(R.string.store_processing_text), this, null);
        }
    }

    public void edit(View view){
        if(!editF){
            editF = true;
            editButton.setText(getString(R.string.submit));
            logoButton.setVisibility(View.VISIBLE);
            categoryButton.setVisibility(View.VISIBLE);
            nameBox.setEnabled(true);
            descriptionBox.setEnabled(true);
            storeARNameBox.setEnabled(true);
            storeARDescriptionBox.setEnabled(true);
        }else {
            submit();
        }
    }

    public void selProducts(View view){
        clearIndicators();
        ((View)findViewById(R.id.indicator_products)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_products)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_products)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_products)));

        if(Commons.lang.equals("ar"))title.setText(Commons.store.getAr_name());
        else title.setText(Commons.store.getName());

        ((FrameLayout)findViewById(R.id.productsFrame)).setVisibility(View.VISIBLE);
        getStoreProducts();
    }

    public void selRatings(View view){
        clearIndicators();
        ((View)findViewById(R.id.indicator_ratings)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_ratings)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_ratings)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_ratings)));

        if(Commons.lang.equals("ar"))title.setText(Commons.store.getAr_name());
        else title.setText(Commons.store.getName());

        ((FrameLayout)findViewById(R.id.ratingsFrame)).setVisibility(View.VISIBLE);
        getRatings();
    }

    public void openDialog(View view){
        selectedCategoryList.clear();
        selectedARCategoryList.clear();
        AlertDialog.Builder alertdialogbuilder;
        String[] items = {"Food", "Drinks", "Sweets", "Stationery", "Accessories", "Perfumes", "Others"};
        String[] ar_items = {"طعام", "مشروبات", "حلويات", "ادوات مكتبيه", "مستلزمات", "العطور", "الآخرين"};

        boolean[] Selectedtruefalse = new boolean[]{false, false, false, false, false, false, false};

        alertdialogbuilder = new AlertDialog.Builder(MyStoreDetailActivity.this);
        alertdialogbuilder.setTitle(getString(R.string.choose_category));
//        alertdialogbuilder.setMessage(getString(R.string.select_2_items));
        alertdialogbuilder.setMultiChoiceItems(Commons.lang.equals("ar")?ar_items:items, Selectedtruefalse, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    if(selectedCategoryList.size() >= 2){
                        showToast(getString(R.string.select_2_items));
                    }else {
                        selectedCategoryList.add(items[which]);
                        selectedARCategoryList.add(ar_items[which]);
                        Log.d("SELECTED!!!", String.valueOf(selectedCategoryList));
                    }
                }else {
                    if(selectedCategoryList.contains(items[which])){
                        selectedCategoryList.remove(items[which]);
                        selectedARCategoryList.remove(ar_items[which]);
                    }
                }
            }
        });

        alertdialogbuilder.setCancelable(false);
        alertdialogbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedCategoryList.size() == 0){
                    showToast(getString(R.string.choose_category));
                    return;
                }

                categoryList.clear();
                arCategoryList.clear();
                categoryList.addAll(selectedCategoryList);
                arCategoryList.addAll(selectedARCategoryList);

                categoryBox.setText("");
                storeARCategoryBox.setText("");
                if(selectedCategoryList.size() == 1){
                    categoryBox.setText(selectedCategoryList.get(0));
                    storeARCategoryBox.setText(selectedARCategoryList.get(0));
                }else {
                    categoryBox.setText("1." + selectedCategoryList.get(0) + " 2." + selectedCategoryList.get(1));
                    storeARCategoryBox.setText("1." + selectedARCategoryList.get(0) + " 2." + selectedARCategoryList.get(1));
                }
            }
        });

        alertdialogbuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategoryList.clear();
                selectedARCategoryList.clear();
            }
        });
        AlertDialog dialog = alertdialogbuilder.create();
        dialog.show();

    }

    public void openStoreCategory(View view){
        String[] categoryItems = {};
        String[] arCategoryItems = {};
        if(Commons.store.getCategory2().length() == 0){
            categoryItems = new String[]{Commons.store.getCategory()};
            arCategoryItems = new String[]{Commons.store.getAr_category()};
        }else {
            categoryItems = new String[]{Commons.store.getCategory(), Commons.store.getCategory2()};
            arCategoryItems = new String[]{Commons.store.getAr_category(), Commons.store.getAr_category2()};
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyStoreDetailActivity.this);
        String[] finalCategoryItems = categoryItems;
        String[] finalArCategoryItems = arCategoryItems;
        mBuilder.setSingleChoiceItems(Commons.lang.equals("ar")?arCategoryItems:categoryItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                productCategoryBox.setText(finalCategoryItems[i]);
                productARCategoryBox.setText(finalArCategoryItems[i]);
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void showOptions(View view){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    Bitmap bitmap;
    File imageFile = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if(((FrameLayout)findViewById(R.id.newProductFrame)).getVisibility() == View.GONE){
                    //From here you can load the image however you need to, I recommend using the Glide library
                    imageFile = new File(resultUri.getPath());
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        ((RoundedImageView)findViewById(R.id.logo)).setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
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
                        if(((LinearLayout)findViewById(R.id.imageFrame)).getVisibility() == View.GONE)
                            ((LinearLayout)findViewById(R.id.imageFrame)).setVisibility(View.VISIBLE);
                        if(((ImageView)findViewById(R.id.virtual)).getVisibility() == View.VISIBLE)
                            ((ImageView)findViewById(R.id.virtual)).setVisibility(View.GONE);
                        pager.setCurrentItem(pictures.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void submit(){

        String catt2 = "";
        if(Commons.store.getCategory2().length() > 0) catt2 = ", " + Commons.store.getCategory2();
        if(imageFile == null
                && nameBox.getText().toString().trim().equals(Commons.store.getName())
                && storeARNameBox.getText().toString().trim().equals(Commons.store.getAr_name())
                && categoryBox.getText().toString().trim().equals(Commons.store.getCategory() + catt2)
                && descriptionBox.getText().toString().trim().equals(Commons.store.getDescription())
                && storeARDescriptionBox.getText().toString().trim().equals(Commons.store.getAr_description())
        ){
            return;
        }

        if(nameBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_store_name));
            return;
        }

        if(categoryBox.getText().length() == 0){
            showToast(getString(R.string.choose_category));
            return;
        }

        if(descriptionBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_description));
            return;
        }

        String cat1 = "", cat2 = "", acat1 = "", acat2 = "";
        if(categoryList.size() == 1){
            cat1 = categoryList.get(0);
            acat1 = arCategoryList.get(0);
        }else {
            cat1 = categoryList.get(0);
            acat1 = arCategoryList.get(0);
            cat2 = categoryList.get(1);
            acat2 = arCategoryList.get(1);
        }

        if(imageFile != null)
            uploadStoreToSever(imageFile, nameBox.getText().toString().trim(), cat1, cat2, descriptionBox.getText().toString().trim(),
                    storeARNameBox.getText().toString().trim(), acat1, acat2, storeARDescriptionBox.getText().toString().trim());
        else
            uploadStoreToSeverWithoutLogo(nameBox.getText().toString().trim(), cat1, cat2, descriptionBox.getText().toString().trim(),
                    storeARNameBox.getText().toString().trim(), acat1, acat2, storeARDescriptionBox.getText().toString().trim());
    }

    public void uploadStoreToSever(File logo, String stname, String stcategory, String stcategory2, String desc,
                                   String astname, String astcategory, String astcategory2, String adesc) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "updateStore")
                .addMultipartFile("file", logo)
                .addMultipartParameter("store_id", String.valueOf(Commons.store.getIdx()))
                .addMultipartParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addMultipartParameter("name", stname)
                .addMultipartParameter("category", stcategory)
                .addMultipartParameter("category2", stcategory2)
                .addMultipartParameter("description", desc)
                .addMultipartParameter("ar_name", astname)
                .addMultipartParameter("ar_category", astcategory)
                .addMultipartParameter("ar_category2", astcategory2)
                .addMultipartParameter("ar_description", adesc)
                .addMultipartParameter("price_id", String.valueOf(priceId))
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.d("UPLOADED!!!", String.valueOf(bytesUploaded) + "/" + String.valueOf(totalBytes));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                showAlertDialog(getString(R.string.account_progress), getString(R.string.account_progress_text), MyStoreDetailActivity.this, new Callable<Void>() {
                                    public Void call() {
                                        finish();
                                        return null;
                                    }
                                });
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

    public void uploadStoreToSeverWithoutLogo(String stname, String stcategory, String stcategory2, String desc,
                                              String astname, String astcategory, String astcategory2, String adesc) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "updateStore")
                .addMultipartParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addMultipartParameter("store_id", String.valueOf(Commons.store.getIdx()))
                .addMultipartParameter("name", stname)
                .addMultipartParameter("category", stcategory)
                .addMultipartParameter("category2", stcategory2)
                .addMultipartParameter("description", desc)
                .addMultipartParameter("ar_name", astname)
                .addMultipartParameter("ar_category", astcategory)
                .addMultipartParameter("ar_category2", astcategory2)
                .addMultipartParameter("ar_description", adesc)
                .addMultipartParameter("price_id", String.valueOf(priceId))
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.d("UPLOADED!!!", String.valueOf(bytesUploaded) + "/" + String.valueOf(totalBytes));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                showAlertDialog(getString(R.string.account_progress), getString(R.string.account_progress_text), MyStoreDetailActivity.this, new Callable<Void>() {
                                    public Void call() {
                                        finish();
                                        return null;
                                    }
                                });
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

    public void delCurrentItem(View view){
        if(pictures.size() > 0){
            pictures.remove(pager.getCurrentItem());
            if(pictures.size() == 0) {
                ((LinearLayout) findViewById(R.id.imageFrame)).setVisibility(View.GONE);
                if(((ImageView)findViewById(R.id.virtual)).getVisibility() == View.GONE)
                    ((ImageView)findViewById(R.id.virtual)).setVisibility(View.VISIBLE);
            }
            adapter.setDatas(pictures);
            adapter.notifyDataSetChanged();
            pager.setAdapter(adapter);
        }
    }

    public void submitProduct(View view){
        if(pictures.size() == 0){
            showToast(getString(R.string.load_pictures));
            return;
        }

        if(productNameBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_product_name));
            return;
        }

        if(productARCategoryBox.getText().length() == 0){
            showToast(getString(R.string.choose_category));
            return;
        }

        if(productPriceBox.getText().toString().trim().length() == 0 || Double.parseDouble(productPriceBox.getText().toString()) == 0){
            showToast(getString(R.string.enter_product_price));
            return;
        }

        if(productDescBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_product_description));
            return;
        }

        ArrayList<File> files = new ArrayList<>();
        for(Picture picture:pictures){
            files.add(picture.getFile());
        }

        uploadProductToSever(files, productNameBox.getText().toString().trim(), productCategoryBox.getText().toString(), productPriceBox.getText().toString(), productDescBox.getText().toString().trim(),
                productARNameBox.getText().toString().trim(), productARCategoryBox.getText().toString(), productARDescriptionBox.getText().toString().trim());
    }

    public void uploadProductToSever(ArrayList<File> files, String pname, String pcategory, String pprice, String pdesc, String apname, String apcategory, String apdesc) {
        progressBar.setVisibility(View.VISIBLE);
//        ((LinearLayout)findViewById(R.id.progressLayout)).setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "uploadProduct")
                .addMultipartFileList("files", files)
                .addMultipartParameter("store_id", String.valueOf(Commons.store.getIdx()))
                .addMultipartParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addMultipartParameter("name", pname)
                .addMultipartParameter("category", pcategory)
                .addMultipartParameter("price", pprice)
                .addMultipartParameter("unit", "qr")
                .addMultipartParameter("description", pdesc)
                .addMultipartParameter("ar_name", apname)
                .addMultipartParameter("ar_category", apcategory)
                .addMultipartParameter("ar_description", apdesc)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.d("UPLOADED!!!", String.valueOf((int)bytesUploaded*100/totalBytes));
//                        ((TextView)findViewById(R.id.progressText)).setText(String.valueOf((int)bytesUploaded*100/totalBytes));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
//                        ((LinearLayout)findViewById(R.id.progressLayout)).setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                pictures.clear();
                                ((LinearLayout) findViewById(R.id.imageFrame)).setVisibility(View.GONE);
                                if(((ImageView)findViewById(R.id.virtual)).getVisibility() == View.GONE)
                                    ((ImageView)findViewById(R.id.virtual)).setVisibility(View.VISIBLE);
                                productNameBox.setText("");
                                productDescBox.setText("");
                                showToast(getString(R.string.uploaded));
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
//                        ((LinearLayout)findViewById(R.id.progressLayout)).setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });
    }

    private void getStoreProducts() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getProducts";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseStoreProductsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
            }
        }) {

        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseStoreProductsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                products.clear();
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Product product = new Product();
                    product.setIdx(object.getInt("id"));
                    product.setStoreId(object.getInt("store_id"));
                    product.setUserId(object.getInt("member_id"));
                    product.setName(object.getString("name"));
                    product.setPicture_url(object.getString("picture_url"));
                    product.setCategory(object.getString("category"));
                    product.setPrice(Double.parseDouble(object.getString("price")));
                    product.setNew_price(Double.parseDouble(object.getString("new_price")));
                    product.setUnit(object.getString("unit"));
                    product.setDescription(object.getString("description"));
                    product.setRegistered_time(object.getString("registered_time"));
                    product.setStatus(object.getString("status"));
                    if(object.getString("isLiked").equals("yes"))
                        product.setLiked(true);
                    else if(object.getString("isLiked").equals("no"))
                        product.setLiked(false);
                    else if(object.getString("isLiked").length() == 0)
                        product.setLiked(false);

                    product.setAr_name(object.getString("ar_name"));
                    product.setAr_category(object.getString("ar_category"));
                    product.setAr_description(object.getString("ar_description"));

                    Log.d("Store IDs!!!", String.valueOf(product.getStoreId()) + "/" + String.valueOf(Commons.store.getIdx()));

                    if(product.getStoreId() == Commons.store.getIdx()){
                        if(product.getNew_price() == 0){
                            if(product.getPrice() >= Commons.minPriceVal && product.getPrice() <= Commons.maxPriceVal)
                                products.add(product);
                        }else {
                            if(product.getNew_price() >= Commons.minPriceVal && product.getNew_price() <= Commons.maxPriceVal)
                                products.add(product);
                        }
                    }
                }

                Collections.sort(products, new ProductChainedComparator(
                        new ProductNameComparator(),
                        new ProductPriceComparator()
                ));

                if(products.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                productListAdapter.setDatas(products);
                productList.setAdapter(productListAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRatings() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getRatings";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRatingsResponse(response);
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
                params.put("store_id", String.valueOf(Commons.store.getIdx()));
                return params;
            }
        };
        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        QhomeApp.getInstance().addToRequestQueue(post, url);
    }

    public void parseRatingsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject response = new JSONObject(json);
            String success = response.getString("result_code");
            Log.d("Rcode=====> :",success);
            if (success.equals("0")) {
                ratings.clear();
                JSONArray dataArr = response.getJSONArray("data");
                for(int i=0; i<dataArr.length(); i++) {
                    JSONObject object = (JSONObject) dataArr.get(i);
                    Rating rating = new Rating();
                    rating.setIdx(object.getInt("id"));
                    rating.setStoreId(object.getInt("store_id"));
                    rating.setUserId(object.getInt("member_id"));
                    rating.setUserName(object.getString("member_name"));
                    rating.setUserPictureUrl(object.getString("member_photo"));
                    rating.setRating(Float.parseFloat(object.getString("rating")));
                    rating.setSubject(object.getString("subject"));
                    rating.setDescription(object.getString("description"));
                    rating.setDate(object.getString("date_time"));
                    rating.setLang(object.getString("lang"));

                    ratings.add(rating);
                }

                if(ratings.isEmpty())
                    ((FrameLayout)findViewById(R.id.no_result_ratings)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result_ratings)).setVisibility(View.GONE);
                ratingListAdapter.setDatas(ratings);
                ratingListAdapter.notifyDataSetChanged();
                ratingsList.setAdapter(ratingListAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void openFilter(View view){
        String[] categoryItems = {};
        String[] arCategoryItems = {};
        if(Commons.store.getCategory2().length() == 0){
            categoryItems = new String[]{Commons.store.getCategory(), "All Products"};
            arCategoryItems = new String[]{Commons.store.getAr_category(), "جميع المنتجات"};
        }else {
            categoryItems = new String[]{Commons.store.getCategory(), Commons.store.getCategory2(), "All Products"};
            arCategoryItems = new String[]{Commons.store.getAr_category(), Commons.store.getAr_category2(), "جميع المنتجات"};
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyStoreDetailActivity.this);
        String[] finalCategoryItems = categoryItems;
        String[] finalArCategoryItems = arCategoryItems;
        mBuilder.setSingleChoiceItems(Commons.lang.equals("ar")?arCategoryItems:categoryItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == finalCategoryItems.length - 1){
                    getStoreProducts();
                }else {
                    if(Commons.lang.equals("ar"))filterProductsByCategory(finalArCategoryItems[i]);
                    else filterProductsByCategory(finalCategoryItems[i]);
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void filterProductsByCategory(String category){
        ArrayList<Product> productArrayList = new ArrayList<>();
        for(Product product:products){
            if(product.getCategory().equals(category) || product.getAr_category().equals(category))
                productArrayList.add(product);
        }
        if(productArrayList.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
        productListAdapter.setDatas(productArrayList);
        productListAdapter.notifyDataSetChanged();
        productList.setAdapter(productListAdapter);
    }

    Product prod = null;

    public void deleteProduct(Product product){
        prod = product;
        showAlertDialogForQuestion(getString(R.string.warning), getString(R.string.del_text), this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                String url = ReqConst.SERVER_URL + "proddelete";
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
                        params.put("product_id", String.valueOf(product.getIdx()));
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
                int index = products.indexOf(prod);
                products.remove(index);

                productListAdapter.setDatas(products);
                if(products.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                productListAdapter.setDatas(products);
                productList.setAdapter(productListAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getComprices(){
        prices.clear();
        AndroidNetworking.post(ReqConst.SERVER_URL + "getComprices")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                JSONArray jsonArray = response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    CompanyPrice price = new CompanyPrice();
                                    price.setId(jsonObject.getInt("id"));
                                    price.setPrice(Double.parseDouble(jsonObject.getString("price")));
                                    price.setDescription(jsonObject.getString("description"));
                                    prices.add(price);
                                }
                                priceListAdapter.setDatas(prices);
                                listView.setAdapter(priceListAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                    }
                });
    }

}






































