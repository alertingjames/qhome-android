package com.myapp.qhome.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.preference.PrefConst;
import com.myapp.qhome.preference.Preference;

public class SettingsActivity extends BaseActivity {

    RadioGroup radioGroup;
    RadioButton radioButton, btn_en, btn_ar;
    Switch notiSwitchButton;
    TextView saveButton;
    LinearLayout notiFrame;
    String selectedLang = "";
    boolean isEnabledNotification = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);

        btn_en = (RadioButton)findViewById(R.id.en);
        btn_ar = (RadioButton)findViewById(R.id.ar);
        saveButton = (TextView)findViewById(R.id.btn_save);
        notiFrame = (LinearLayout)findViewById(R.id.notiFrame);

        if(Commons.thisUser == null)notiFrame.setVisibility(View.GONE);

        btn_en.setTypeface(normal);
        btn_ar.setTypeface(normal);
        saveButton.setTypeface(normal);

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton)findViewById(group.getCheckedRadioButtonId());
                if (radioButton.getText().equals(btn_en.getText())){
                    selectedLang = "en";
                }else if (radioButton.getText().equals(btn_ar.getText())){
                    selectedLang = "ar";
                }

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedLang.equals("en")){
                    Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_LANGUAGE, "en");
                    Commons.lang = "en";
                    setLanguage("en");
                }else if(selectedLang.equals("ar")){
                    Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_LANGUAGE, "ar");
                    Commons.lang = "ar";
                    setLanguage("ar");
                }

                if(isEnabledNotification)Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_NOTIFICATION, true);
                else Preference.getInstance().put(getApplicationContext(), PrefConst.PREF_NOTIFICATION, false);

                Commons.isNotificationEnabled = isEnabledNotification;

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.right_out, R.anim.left_in);
                finish();
            }
        });

        notiSwitchButton = (Switch)findViewById(R.id.notification);
        notiSwitchButton.setTypeface(normal);
        notiSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    isEnabledNotification = true;
                }else {
                    isEnabledNotification = false;
                }
            }
        });

        String lang = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_LANGUAGE, "en");
        if(lang.equals("en")){
            btn_en.setChecked(true);
            Commons.lang = "en";
            selectedLang = "en";
        }else if(lang.equals("ar")){
            btn_ar.setChecked(true);
            Commons.lang = "ar";
            selectedLang = "ar";
        }

        isEnabledNotification = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREF_NOTIFICATION, false);
        if(isEnabledNotification)notiSwitchButton.setChecked(true);
        else notiSwitchButton.setChecked(false);
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
















































