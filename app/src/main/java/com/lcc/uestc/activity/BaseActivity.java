package com.lcc.uestc.activity;

import android.os.Bundle;
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

	public ActionBar actionBar;
	@Bind(R.id.toolbar)

	public Toolbar toolbar;

	private MaterialDialog materialDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutView());
		ButterKnife.bind(this);
		initToolbar(toolbar);
	}

	public int getColorPrimary(){
		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
		return typedValue.data;
	}

	private void initToolbar(Toolbar toolbar) {
		if(toolbar == null)
			return;
		toolbar.setTitle(R.string.app_name);
		toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
		toolbar.collapseActionView();
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		if(actionBar != null)
			actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
	}
	private Toast toast;
	public void toast(String text)
	{
		if(toast == null)
		{
			toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
		}

		toast.setText(text);
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
	protected abstract int getLayoutView();
}
