package com.lcc.uestc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.lcc.uestc.Config;
import com.lcc.uestc.R;
import com.lcc.uestc.adapter.ActionBarSpinnerAdapter;
import com.lcc.uestc.adapter.FragmentAdapter;
import com.lcc.uestc.bean.User;
import com.lcc.uestc.data.SimpleCallback;
import com.lcc.uestc.utils.PreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fragmentManager;

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Bind(R.id.navigation_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    TextView username;

    FragmentAdapter adapter;
    public Spinner chooseSpinner;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ActionBarSpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PreferenceUtils.getInstance().destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                spinnerAdapter.refresh(true);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void init() {
        username.setText(User.getInstance().getName());
        actionBar.setDisplayHomeAsUpEnabled(false);

        fragmentManager = getSupportFragmentManager();


        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
        spinnerContainer.setLayoutParams(lp);
        toolbar.addView(spinnerContainer);


        chooseSpinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        spinnerAdapter = new ActionBarSpinnerAdapter(this, chooseSpinner);
        chooseSpinner.setAdapter(spinnerAdapter);

        adapter = new FragmentAdapter(fragmentManager, spinnerAdapter);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                adapter.OnSelect(position);
            }
        });
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        adapter.OnSelect(0);
        if (User.getInstance().isLogout()) {
            EventBus.getDefault().post(Config.NO_LOGIN_EVENT);
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }


    /*@OnClick(R.id.avatar)*/
    public void showInfo() {
        new MaterialDialog(this)
                .setTitle("用户信息")
                .setMessage(User.getInstance().getInfo())
                .setPositiveButton("确定", null)
                .show();
    }

    MaterialDialog login_dialog;

    public void showLoginDialog() {
        if (login_dialog == null) {
            final View login_lauout = LayoutInflater.from(this).inflate(R.layout.login_layout, null);
            login_dialog = new MaterialDialog(this)
                    .setTitle("验证信息")
                    .setCanceledOnTouchOutside(true)
                    .setContentView(login_lauout)
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View t) {
                            View v = login_lauout;
                            TextInputLayout xuehao = (TextInputLayout) v.findViewById(R.id.xuehao);
                            final TextInputLayout password = (TextInputLayout) v.findViewById(R.id.password);
                            final String s = xuehao.getEditText().getText().toString();
                            final String psw = password.getEditText().getText().toString();
                            if (s.equals("") || psw.equals("")) {
                                toast("输入为空");
                                return;
                            }

                            showProgressDialog("正在登录...");
                            User.getInstance().login(s, psw, new SimpleCallback<String>() {
                                @Override
                                public void onSuccess(String data) {
                                    adapter.refresh(true);
                                /*EventBus.getDefault().post(Config.UPDATE_EVENT);*/
                                    toast(User.getInstance().getName() + " 登录成功");
                                    hideProgressDialog();
                                    username.setText(User.getInstance().getName());
                                    login_dialog.dismiss();
                                }

                                @Override
                                public void onFailed(String failReason) {
                                    toast("登录失败" + failReason);
                                    hideProgressDialog();
                                }
                            });
                        }
                    });
        }

        login_dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_login:
                showLoginDialog();
                break;
            case R.id.nav_collection:
                startActivity(new Intent(this, CollectActivity.class));
                break;
            case R.id.nav_home:

                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEvent(Integer event) {
        if (event == Config.NO_LOGIN_EVENT) {
            Snackbar.make(toolbar, "立即登录从信息门户同步信息?", Snackbar.LENGTH_LONG)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLoginDialog();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else
            super.onBackPressed();
    }
}
