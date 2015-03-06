/**
 * 
 */
package com.ljk.mytest.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 字符串工具类
 */
public class StringUtils {

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	/**
	 * 判断给定字符串是否空白串。
	 * 空白串是指由空格、制表符、回车符、换行符组成的字符串
	 * 若输入字符串为null或空字符串，返回true
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 将字符串转化成JSON
	 */
	public static JSONObject String2Json(String s) {
		JSONObject json = null;
		try {
			json = new JSONObject(s);
		} catch (JSONException e) {
			Log.e("String2Json", "JSONException: " + Log.getStackTraceString(e));
		} catch (Exception e) {
			Log.e("String2Json", "Exception: " + Log.getStackTraceString(e));
		}
		
		return json;
	}
	
	
	/**
	 * 读取文件
	 * @param filename
	 * @return String
	 */
	public static String ReadFile(String filename) {
		try {
			FileInputStream is = new FileInputStream(filename);
			InputStreamReader isReader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isReader);
			StringBuffer sb = new StringBuffer();
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			
			is.close();
			isReader.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			Log.e("ReadFile", "FileNotFoundException: " + Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e("ReadFile", "IOException: " + Log.getStackTraceString(e));
		}
		
		return null;
	}
	
	/**
	 * 写文件
	 * @param filename, content
	 * @return boolean
	 */
	public static boolean WriteFile(String filename, String content) {
		FileOutputStream os;
		try {
			os = new FileOutputStream(filename);
			OutputStreamWriter osWriter = new OutputStreamWriter(os);
			osWriter.write(content);
			osWriter.flush();
			osWriter.close();
			os.close();
			
			return true;
		} catch (FileNotFoundException e) {
			Log.e("WriteFile", "FileNotFoundException: " + Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e("WriteFile", "IOException: " + Log.getStackTraceString(e));
		}
		
		return false;
	}
	
}

