package com.lcc.uestc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;

import com.lcc.uestc.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public abstract class BaseActivity extends AppCompatActivity {
	protected ActionBar actionBar;

	private MaterialDialog materialDialog;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ButterKnife.bind(this);

        if(toolbar != null)
            setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

	public int getColorPrimary(){
		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
		return typedValue.data;
	}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

	protected abstract int getLayoutId();
	private Toast toast;

	public void toast(CharSequence msg) {
		if (toast == null)
			toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		toast.setText(msg);
		toast.show();
	}

	public void showProgressDialog(String msg)
	{
		if(materialDialog == null)
		{
			materialDialog = new MaterialDialog(this);
		}

		materialDialog.setMessage(msg);
		materialDialog.show();
	}

	public void hideProgressDialog()
	{
		if(materialDialog != null)
		{
			materialDialog.dismiss();
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
