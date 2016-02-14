package com.lcc.uestc.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lcc.uestc.activity.WebViewActivity;

public class ActivityJumpUtils {
	public static void toWebView(Context context, String url, Class<? extends Activity> activity)
	{
		if(url == null || context == null)
		{
			return;
		}
		Intent i = new Intent(context, activity);
		i.putExtra("url", url);
		context.startActivity(i);
	}
	public static void toWebView(Context context,String url)
	{
		toWebView(context,url,WebViewActivity.class);
	}
}
