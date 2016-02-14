package com.lcc.uestc.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.lcc.uestc.R;
import com.lcc.uestc.bean.User;
import com.lcc.uestc.utils.PreferenceUtils;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {
    public static final String NAME = PreferenceUtils.NAME;
    public static final String update_no_snackbar = "update_no_snackbar";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(NAME);
        addPreferencesFromResource(R.xml.fragment_setting);
    }
    MaterialDialog materialDialog;
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference == null)
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        String key = preference.getKey();
        if(key.equals("logout"))
        {
            if(User.getInstance().isLogout())
            {
                Snackbar.make(getActivity().getWindow().getDecorView(),"您未登录,无需退出",Snackbar.LENGTH_LONG)
                        .show();
                return true;
            }
            materialDialog  = new MaterialDialog(getActivity())
                    .setCanceledOnTouchOutside(true)
                    .setTitle("确定退出登录吗？")
                    .setMessage("退出登录将清除本地数据")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            User.getInstance().logout();
                            materialDialog.dismiss();
                        }
                    });
            materialDialog.show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
