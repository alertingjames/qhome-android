package com.myapp.qhome.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.LinkAddress;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.R;
import com.myapp.qhome.adapters.CompanyPriceListAdapter;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.ReqConst;
import com.myapp.qhome.models.CompanyPrice;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class RegisterStoreActivity extends BaseActivity {

    File imageFile = null;
    AVLoadingIndicatorView progressBar;
    EditText storeNameBox, descriptionBox, storeARNameBox, storeARDescriptionBox;
    TextView storeCategoryBox, storeARCategoryBox;
    CheckBox companyCheckBox;
    FrameLayout darkBg;
    public int priceId = 0;

    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<String> arCategoryList = new ArrayList<>();
    ArrayList<String> selectedCategoryList = new ArrayList<>();
    ArrayList<String> selectedARCategoryList = new ArrayList<>();

    ArrayList<CompanyPrice> prices = new ArrayList<>();
    CompanyPriceListAdapter adapter = new CompanyPriceListAdapter(this);

    ListView listView;
    LinearLayout companyPriceLayout;
    ImageView cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Commons.registerStoreActivity = this;

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        setupUI(findViewById(R.id.activity), this);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.btn_submit)).setTypeface(bold);

        storeNameBox = (EditText)findViewById(R.id.storeNameBox);
        storeCategoryBox = (TextView) findViewById(R.id.storeCategoryBox);
        descriptionBox = (EditText)findViewById(R.id.descriptionBox);

        darkBg = (FrameLayout)findViewById(R.id.bg_dark);

        storeARNameBox = (EditText)findViewById(R.id.storeARNameBox);
        storeARCategoryBox = (TextView) findViewById(R.id.storeARCategoryBox);
        storeARDescriptionBox = (EditText)findViewById(R.id.storeARDescriptionBox);

        companyCheckBox = (CheckBox)findViewById(R.id.companyBox);

        storeNameBox.setTypeface(normal);
        descriptionBox.setTypeface(normal);
        storeCategoryBox.setTypeface(normal);

        ((TextView)findViewById(R.id.caption)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption5)).setTypeface(normal);

        if(Commons.myStores.size() == 0)((TextView)findViewById(R.id.count)).setText("1/2");
        else if(Commons.myStores.size() == 1)((TextView)findViewById(R.id.count)).setText("2/2");

        listView = (ListView)findViewById(R.id.list);
        companyPriceLayout = (LinearLayout)findViewById(R.id.compriceBox) ;
        cancelButton = (ImageView)findViewById(R.id.btn_cancel);

        companyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
                    companyPriceLayout.setAnimation(animation);
                    companyPriceLayout.setVisibility(View.VISIBLE);
                    darkBg.setVisibility(View.VISIBLE);
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

    public void openDialog(View view){
        selectedCategoryList.clear();
        selectedARCategoryList.clear();
        AlertDialog.Builder alertdialogbuilder;
        String[] items = {"Food", "Drinks", "Sweets", "Stationery", "Accessories", "Perfumes", "Others"};
        String[] ar_items = {"طعام", "مشروبات", "حلويات", "ادوات مكتبيه", "مستلزمات", "العطور", "الآخرين"};

        boolean[] Selectedtruefalse = new boolean[]{false, false, false, false, false, false, false};

        alertdialogbuilder = new AlertDialog.Builder(RegisterStoreActivity.this);
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

                storeCategoryBox.setText("");
                storeARCategoryBox.setText("");
                if(selectedCategoryList.size() == 1){
                    storeCategoryBox.setText(selectedCategoryList.get(0));
                    storeARCategoryBox.setText(selectedARCategoryList.get(0));
                }else {
                    storeCategoryBox.setText("1." + selectedCategoryList.get(0) + " 2." + selectedCategoryList.get(1));
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
                imageFile = new File(resultUri.getPath());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    ((RoundedImageView)findViewById(R.id.logo)).setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void submit(View v){
        if(storeNameBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_store_name));
            return;
        }

        if(categoryList.size() == 0){
            showToast(getString(R.string.choose_category));
            return;
        }

        if(descriptionBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_description));
            return;
        }

        if(storeARNameBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_store_name_ar));
            return;
        }

        if(storeARDescriptionBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_store_description_ar));
            return;
        }

        if(imageFile == null){
            showToast(getString(R.string.load_logo));
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

        uploadStoreToSever(imageFile, storeNameBox.getText().toString().trim(), cat1, cat2, descriptionBox.getText().toString().trim(),
                storeARNameBox.getText().toString().trim(), acat1, acat2, storeARDescriptionBox.getText().toString().trim());
    }

    public void uploadStoreToSever(File logo, String stname, String stcategory, String stcategory2, String desc,
                                   String astname, String astcategory, String astcategory2, String adesc) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "registerStore")
                .addMultipartFile("file", logo)
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
                                showAlertDialog(getString(R.string.account_progress), getString(R.string.account_progress_text), RegisterStoreActivity.this, new Callable<Void>() {
                                    public Void call() {
                                        try {
                                            if(Integer.parseInt(response.getString("count")) == 1){
                                                ((TextView)findViewById(R.id.count)).setText("2/2");
                                                ((RoundedImageView)findViewById(R.id.logo)).setImageBitmap(null);
                                                imageFile = null;
                                                storeNameBox.setText("");
                                                storeCategoryBox.setText("");
                                                descriptionBox.setText("");
                                                storeARNameBox.setText("");
                                                storeARCategoryBox.setText("");
                                                storeARDescriptionBox.setText("");
                                            }else {
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                });
                            }else if(result.equals("1")){
                                showToast(getString(R.string.existing_store));
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

    public void back(View view){
        onBackPressed();
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
                                adapter.setDatas(prices);
                                listView.setAdapter(adapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.registerStoreActivity = null;
    }
}

































