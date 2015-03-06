package com.ljk.mytest.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ljk.mytest.application.AppContext;
import com.ljk.mytest.http.ApiClient;
import com.ljk.mytest.manager.AppException;
import com.ljk.mytest.utils.Constant;


public class BssApi extends ApiClient {

	public static final String STATUS_KEY = "status";
	public static final String DATA_KEY = "data";


	/** 
	 * 定检的配置更新标志位
	 * 打开应用后第一次进入定检页面时进入桥梁列表更新 
	 */
	public static boolean confUpdated = false;

	public static JSONObject _post(String url, Map<String, Object> params) throws AppException {
		InputStream is = _post(AppContext.mContext, url, params, null);
		return parseResult(is);
	}

	public static JSONObject _get(String url) throws AppException {
		InputStream is = http_get(AppContext.mContext, url);
		return parseResult(is);
	}

	public static JSONObject _post(String host, String api, Map<String, Object> params) throws AppException {
		InputStream is = _post(AppContext.mContext, host + api, params, null);
		return parseResult(is);
	}

	public static JSONObject _get(String host, String api) throws AppException {
		InputStream is = http_get(AppContext.mContext, host + api);
		return parseResult(is);
	}
	
	public static JSONObject _get2(String host, String api) throws AppException {
		InputStream is = http_get(AppContext.mContext, host + api);
		return parseResult(is);
	}

	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @return
	 * @throws AppException
	 * @throws JSONException 
	 */
	public static JSONObject accountLogin(String username, String password) throws AppException, JSONException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constant.LOGIN_NAME_KEY, username);
		params.put(Constant.LOGIN_PWD_KEY, password);
		return _post(Constant.LOGIN_HOST + Constant.LOGIN_URI, params);
	}


	public static String getProperty(String key) {
		int sdkLevel = Build.VERSION.SDK_INT;
		/**
		 * MODE_MULTI_PROCESS这个值是一个标志，在Android 2.3及以前，这个标志位都是默认开启的，
		 * 允许多个进程访问同一个SharedPrecferences对象。而以后的Android版本，必须通过明确的将
		 * MODE_MULTI_PROCESS这个值传递给mode参数，才能开启多进程访问。
		 * 设置多进程可读，目的是允许放置于System的service能够读取该配置
		 */
		@SuppressWarnings("deprecation")
		int mode = (sdkLevel > Build.VERSION_CODES.HONEYCOMB) ? 
				Context.MODE_MULTI_PROCESS : Context.MODE_WORLD_READABLE;
		SharedPreferences sharedPref = AppContext.mContext.getSharedPreferences(
				Constant.LOGIN_SETTINGS, mode);
		return sharedPref.getString(key, null);
	}

	public static String getUserName() {
		return getProperty(Constant.USERNAME);
	}

	public static String getNickName() {
		return getProperty(Constant.NICKNAME);
	}

	public static String getUID() {
		return getProperty(Constant.UID);
	}
	
	public static String getUID2() {
		return getProperty(Constant.UID2);
	}


	/** 上传文件至Server的方法 */
	public static String uploadFile(String urlstr, String uploadFile) throws AppException {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		Log.i("http-post", urlstr);

		String cookie = getCookie(AppContext.mContext);
		String userAgent = getUserAgent(AppContext.mContext);
		try {
			URL url = new URL(urlstr);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Cookie", cookie);
			con.setRequestProperty("User-Agent", userAgent);
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);

			File file = new File(uploadFile);
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + 
					"name=\"bssimg\";filename=\"" + file.getName() + "\"\r\n");
			ds.writeBytes(end);


			FileInputStream fStream = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			fStream.close();
			ds.flush();
			Log.i("Stream-size", "" + ds.size());
			DataInputStream is = new DataInputStream(con.getInputStream());
			int ch;
			StringBuffer b = new StringBuffer();
			while ((length = is.read(buffer)) != -1) {
				b.append(new String(buffer));
			}

			ds.close();
			// b.toString() post 请求返回的内容，通常用于返回文件名或地址
			JSONObject json = new JSONObject(b.toString());
			Log.e("imagejson", json.toString());
			Log.i("http-post", "done");
			if (json.getBoolean("status")) {
				return json.getString("fid");
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			throw AppException.file(e);
		} catch (MalformedURLException e) {
			throw AppException.url(e);
		} catch (IOException e) {
			throw AppException.io(e);
		}catch (Exception e) {
			throw AppException.run(e);
		}

	}


	/* 上传音频至Server的方法 */
	public static String uploadAudio(String urlstr, String uploadFile) throws AppException {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		Log.i("http-post", urlstr);
		try {
			URL url = new URL(urlstr);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);

			File file = new File(uploadFile);
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"bssfile\";filename=\"" + file.getName() + "\"\r\n");
			ds.writeBytes(end);

			FileInputStream fStream = new FileInputStream(file);

			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;

			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			fStream.close();
			ds.flush();
			Log.i("Stream-size", ""+ds.size());
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}

			ds.close();

			// b.toString() post 请求返回的内容，通常用于返回文件名或地址
			JSONObject json = new JSONObject(b.toString());
			Log.e("autojson", json.toString());
			Log.i("http-post", "done");
			if (json.getBoolean("status")) {
				return json.getString("fid");
			} else {
				return null;
			}

		} catch (FileNotFoundException e) {
			throw AppException.file(e);
		} catch (MalformedURLException e) {
			throw AppException.url(e);
		} catch (IOException e) {
			throw AppException.io(e);
		} catch (Exception e) {
			throw AppException.run(e);
		}
	}

}