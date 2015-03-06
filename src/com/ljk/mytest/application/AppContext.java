/**
 * 
 */
package com.ljk.mytest.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.ljk.mytest.http.AppConfig;
import com.ljk.mytest.utils.StringUtils;

/**
 * @author comger
 * 
 */
public class AppContext extends Application {

	public static final String API_HOST = null;
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	public static AppContext mContext = null;


	@Override
	public void onCreate() {
		super.onCreate();
		/*		NewCrashHandler newCrashHandler =NewCrashHandler.getInstance();
		newCrashHandler.init(getApplicationContext());*/
		mContext = this;
	}

	public static synchronized AppContext getInstance() {  
		return mContext;  
	}  
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public static boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo =cm.getActiveNetworkInfo();
		//自己加的  默認聯網狀態為wifi
		if (networkInfo == null) {
			return false;
		}else{	
			if (networkInfo.isConnectedOrConnecting()) {//网络已经连接
						//判断网络状态
			int nType = networkInfo.getType();
			if (nType == ConnectivityManager.TYPE_MOBILE) {
				return false;

			}else if (nType == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
	}
		//return ni != null && ni.isConnectedOrConnecting();
		return false;
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}

	public boolean containsProperty(String key){
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 获取App唯一标识 （由于字符串过长，无法用作MQTT ClientId）
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 获取设备ID
	 */
	public String getDeviceId() {
		String uniqueID = getProperty(AppConfig.CONF_DEV_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
			// 在主流厂商生产的设备上，有一个很经常的bug，就是每个设备都会产生相同的ANDROID_ID：9774d56d682e549c
			if ("9774d56d682e549c".equals(uniqueID)) {
				uniqueID = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			}

			if (uniqueID.length() > 16) {
				uniqueID = uniqueID.substring(0, 16);
			}

			setProperty(AppConfig.CONF_DEV_UNIQUEID, uniqueID);
		}

		return uniqueID;
	}

	/**
	 * 设置手机网络类型，wifi，cmwap，ctwap，用于联网参数选择
	 * @return
	 */
	public static String setNetworkType() {
		String networkType = "wifi";
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
		if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
			// 当前网络不可用
			return "";
		}

		String info = netWrokInfo.getExtraInfo();
		if ((info != null)
				&& ((info.trim().toLowerCase().equals("cmwap"))
						|| (info.trim().toLowerCase().equals("uniwap"))
						|| (info.trim().toLowerCase().equals("3gwap")) || (info
								.trim().toLowerCase().equals("ctwap")))) {
			// 上网方式为wap
			if (info.trim().toLowerCase().equals("ctwap")) {
				// 电信
				networkType = "ctwap";
			} else {
				networkType = "cmwap";
			}

		}
		return networkType;
	}
	//文件转换
	public static String readFromFile(File src) {
		BufferedReader bufferedReader=null;
		try {
			bufferedReader = new BufferedReader(new FileReader(src));
			StringBuilder stringBuilder = new StringBuilder();
			String content;
			while((content = bufferedReader.readLine() )!=null){
				stringBuilder.append(content);
			}
			return stringBuilder.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			try {
				if ((bufferedReader!=null)) {
					bufferedReader.close();
				}
				if (src.exists()) {
					src.delete();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
