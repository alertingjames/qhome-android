package com.myapp.qhome.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.preference.PrefConst;
import com.myapp.qhome.preference.Preference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseLangActivity extends BaseActivity {
    String selectedLang = "";

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
        setContentView(R.layout.activity_choose_lang);

        checkAllPermission();

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_en)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_ar)).setTypeface(normal);
        ((TextView)findViewById(R.id.continueButton)).setTypeface(bold);

        String lang = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_LANGUAGE, "en");
        if(lang.equals("en")){
            resetButtons();
            ((CircleImageView)findViewById(R.id.img_en)).setBorderColor(Color.parseColor("#FFC107"));
            ((CircleImageView)findViewById(R.id.img_en)).setBorderWidth(8);
            selectedLang = "en";
            setLanguage("en");
        }else {
            resetButtons();
            ((CircleImageView)findViewById(R.id.img_ar)).setBorderColor(Color.parseColor("#FFC107"));
            ((CircleImageView)findViewById(R.id.img_ar)).setBorderWidth(8);
            selectedLang = "ar";
            setLanguage("ar");
        }

        String langRemember = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_LANG_REMEMBER, "");
        if(langRemember.length() > 0){
            ((CheckBox)findViewById(R.id.remember)).setChecked(true);
        }else ((CheckBox)findViewById(R.id.remember)).setChecked(false);
    }

    private void resetButtons(){
        ((CircleImageView)findViewById(R.id.img_en)).setBorderColor(Color.parseColor("#FCF8D8"));
        ((CircleImageView)findViewById(R.id.img_en)).setBorderWidth(6);
        ((CircleImageView)findViewById(R.id.img_ar)).setBorderColor(Color.parseColor("#FCF8D8"));
        ((CircleImageView)findViewById(R.id.img_ar)).setBorderWidth(6);
        selectedLang = "";
    }

    public void setupEn(View view){
        resetButtons();
        ((CircleImageView)findViewById(R.id.img_en)).setBorderColor(Color.parseColor("#FFC107"));
        ((CircleImageView)findViewById(R.id.img_en)).setBorderWidth(8);
        selectedLang = "en";
    }

    public void setupAr(View view){
        resetButtons();
        ((CircleImageView)findViewById(R.id.img_ar)).setBorderColor(Color.parseColor("#FFC107"));
        ((CircleImageView)findViewById(R.id.img_ar)).setBorderWidth(8);
        selectedLang = "ar";
    }

    public void next(View view){
        if(selectedLang.equals("en")){
            Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_LANGUAGE, "en");
            Commons.lang = "en";
            setLanguage("en");
        }else if(selectedLang.equals("ar")){
            Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_LANGUAGE, "ar");
            Commons.lang = "ar";
            setLanguage("ar");
        }
        if(((CheckBox)findViewById(R.id.remember)).isChecked())Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_LANG_REMEMBER, "remembered");
        else Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_LANG_REMEMBER, "");
        String readTerms = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_READ_TERMS, "");
        if(readTerms.length() == 0){
            Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Commons.lang.equals("en"))setLanguage("en");
        else setLanguage("ar");
    }
}





















