package com.ljk.mytest.receive_ui;


import com.ljk.mytest.bean.Report;
import com.ljk.mytest.service.TalkService;
import com.ljk.mytest.utils.Constant;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
	public Context mContext;
	public SharedPreferences mPreferences;
	public NotificationManager mNotificationManager;
	protected boolean mRegister=false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		int sdkLevel = Build.VERSION.SDK_INT;
		/**
		 * MODE_MULTI_PROCESS这个值是一个标志，在Android 2.3及以前，这个标志位都是默认开启的，
		 * 允许多个进程访问同一个SharedPrecferences对象。而以后的Android版本，必须通过明确的将
		 * MODE_MULTI_PROCESS这个值传递给mode参数，才能开启多进程访问。
		 * 设置多进程可读，目的是允许放置于System的service能够读取该配置
		 */
		int mode = (sdkLevel > Build.VERSION_CODES.HONEYCOMB) ? 
				Context.MODE_MULTI_PROCESS : Context.MODE_WORLD_READABLE;
		mPreferences = getSharedPreferences(Constant.LOGIN_SETTINGS, mode);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	public void saveLoginConfig(String loginConfig,String sd) {
		mPreferences.edit()
		.putString(Constant.UID, loginConfig)
		.commit();
		mPreferences.edit()
		.putString(Constant.UID2, sd)
		.commit();
	}

	public String[] getLoginConfig() {
		String uid = mPreferences.getString(Constant.UID, "");
		String uid2 = mPreferences.getString(Constant.UID2, "");
		return new String[]{uid,uid2};
	}

	public static void startService(Context context){
		new ServiceTask(context, true).start();

	}

	public static void stopService(Context context){
		new ServiceTask(context, false).start();
	}

	private static class ServiceTask extends Thread{
		private Context mContext;
		private boolean mStart;
		public ServiceTask( Context context, boolean start){
			this.mContext=context;
			this.mStart=start;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Intent intent=new Intent(mContext,TalkService.class);
			if (mStart) {
				mContext.startService(intent);
			}else {
				mContext.stopService(intent);
			}
		}
	}

	BroadcastReceiver broadcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Report report=(Report) intent.getSerializableExtra("report_key");
			if (report!=null) {
				receiveReport(report);
			}

		}

	};

	protected abstract void receiveReport(Report report);
	public abstract void refreshBageView();

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (!mRegister) {
			IntentFilter rtFilter = new IntentFilter(Constant.NEW_REPORT_ACTION);
			registerReceiver(broadcastReceiver, rtFilter);
			mRegister= true;
		}
		refreshBageView();
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mRegister) {
			unregisterReceiver(broadcastReceiver);
			mRegister=false;
		}
		super.onDestroy();
	}

}
