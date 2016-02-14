package com.lcc.uestc.activity;

import android.os.Bundle;

import com.lcc.uestc.R;
import com.lcc.uestc.fragment.SettingFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(R.id.conatiner, new SettingFragment()).commit();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_setting;
    }

}
