package com.ljk.mytest.utils;

import java.net.MalformedURLException;
import java.util.List;

import com.ljk.mytest.application.AppContext;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * 系统工具类 
 */
public class SystemUtils {
	/** 应用是否在后台 */
	public static boolean isBackground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					Log.i("后台", appProcess.processName);
					return true;
				} else {
					Log.i("前台", appProcess.processName);
					return false;
				}
			}
		}
		return true;	// 进程已被销毁
	}
	
	/** 睡眠 */
	public static void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	/** 获取屏幕的高度 */
	public final static int getWindowsHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	/** 打开软键盘 */
	public static void showKeyboard(View view) {
    	Context context = view.getContext();
    	InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    
	/** 关闭软键盘 */
    public static void hideKeyboard(View view) {
    	Context context = view.getContext();
    	InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    
    
    /**
     * 设置cookie
     * @throws MalformedURLException 
     */
    public static void syncCookie(Context context, String url) throws MalformedURLException {
    	CookieSyncManager.createInstance(context);
    	CookieManager manager = CookieManager.getInstance();
    	manager.setAcceptCookie(true);
    	manager.removeSessionCookie();
    	String cookies = AppContext.mContext.getProperty("cookie");
    	for (String cookie : cookies.split(";")) {
    		String cookieString = cookie + ";" + Constant.COOKIE_EXTRA_STR;//Max-Age=3600;Domain=.thunics.org;Path=/
    		manager.setCookie(url, cookieString);
    	}
    	CookieSyncManager.getInstance().sync();
    }
    
    
    /**
     * 重启应用 
     */
    public static void restart() {
    	AppContext context = AppContext.mContext;
    	Intent intent = context.getPackageManager()
    			.getLaunchIntentForPackage(context.getPackageName());
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	context.startActivity(intent);
    }
    
}
