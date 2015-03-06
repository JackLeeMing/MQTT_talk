/**
 * 
 */
package com.ljk.mytest.http;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.ljk.mytest.application.AppContext;
import com.ljk.mytest.manager.AppException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;



/**
 * API客户端接口：用于访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	public final static int TIMEOUT_CONNECTION = 20000;
	public final static int TIMEOUT_SOCKET = 20000;
	public final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}

	public static String getCookie(AppContext appContext) {
		if(appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}

	public static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+appContext.getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

	public static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	

	public static GetMethod getHttpGet(String url, String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", AppContext.API_HOST);
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	public static PostMethod getHttpPost(String url, String cookie, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", AppContext.API_HOST);
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}

	public static String _MakeURL(String p_url, Map<String, Object> params) {

		StringBuilder url = new StringBuilder();
		if(!p_url.startsWith("http")){
			url.append(AppContext.API_HOST);
		}
		url.append(p_url);

		if(url.indexOf("?")<0)
			url.append('?');

		for(String name : params.keySet()){
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			//不做URLEncoder处理
			//url.append(URLEncoder.encode(String.valueOf(params.get(name)), UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

	public static void parseCookie(AppContext appContext,HttpClient httpClient){
		Cookie[] cookies = httpClient.getState().getCookies();
        String tmpcookies = "";
        for (Cookie ck : cookies) {
            tmpcookies += ck.toString()+";";
        }
        //保存cookie   
		if(appContext != null && tmpcookies != ""){
			appContext.setProperty("cookie", tmpcookies);
			appCookie = tmpcookies;
		}
	}

	/**
	 * get请求URL
	 * @param url
	 * @throws AppException 
	 */
	public static InputStream http_get(AppContext appContext, String url) throws AppException {	
		//System.out.println("get_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(encodeURL(url, "utf-8"), cookie, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				parseCookie(appContext,httpClient);
				responseBody = httpGet.getResponseBodyAsString();
				//System.out.println("XMLDATA=====>"+responseBody);
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} catch(Exception e) {
				Log.e("http_get", "Exception:" + Log.getStackTraceString(e));
				throw AppException.http(e);
			} finally {
				// 释放连接
				if (httpGet != null) {
					httpGet.releaseConnection();
				}
				httpClient = null;
			}
		}while(time < RETRY_TIME);

		Log.i("http_get", "done:"+url);
		return new ByteArrayInputStream(responseBody.getBytes());
	}

	/**
	 * 公用post方法
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	protected static InputStream _post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException {
		Log.i("http_post", url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;

		//post表单参数处理
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null)
        for(String name : params.keySet()){
        	parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
        	Log.i("http_post_params", String.format("%s==>%s", name,params.get(name)));
        }
        if(files != null)
        for(String file : files.keySet()){
        	try {
				parts[i++] = new FilePart(file, files.get(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	//System.out.println("post_key_file==> "+file);
        }

		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpPost = getHttpPost(encodeURL(url, "utf-8"), cookie, userAgent);     
		        httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		        Log.i("----------Task---------", "状态吗:"+statusCode+"是否成功?");
		        if(statusCode != HttpStatus.SC_OK) {
		        	throw AppException.http(statusCode);
		        }

		        else if(statusCode == HttpStatus.SC_OK) {
		            parseCookie(appContext, httpClient);
		        }
		     	responseBody = httpPost.getResponseBodyAsString();
		        //System.out.println("XMLDATA=====>"+responseBody);
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
        
		Log.i("http_post", "done:"+url);
        return new ByteArrayInputStream(responseBody.getBytes());
	}



	/**
	 * 获取网络图片
	 * @param url
	 * @return
	 */
	protected static Bitmap getNetBitmap(String url) throws AppException {
		//System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
		        InputStream inStream = httpGet.getResponseBodyAsStream();
		        bitmap = BitmapFactory.decodeStream(inStream);
		        inStream.close();
		        break;
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		return bitmap;
	}

	/**
	 * 转成Json格式
	 * @param is
	 * @return
	 * @throws AppException
	 */
	public static JSONObject parseResult(InputStream is) throws AppException{
		StringBuilder document = new StringBuilder();
		JSONObject json = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is), 5 * 1024);
		try{
			String line = null;
			while ((line = reader.readLine()) != null)
				document.append(line);

			reader.close();
			json =  new JSONObject(document.toString());
			Log.i("resaultjson", json.toString());
			if(!json.getBoolean("status")){
				throw AppException.server(new Exception(json.getString("errormsg")));
			}
		}catch (IOException e) {
			// TODO: handle exception
		}catch (JSONException e) {
			// TODO: handle exception
		}
		return json;
	}


	/** 将url中中文进行编码 */
    public static String encodeURL(String url,String encode) throws UnsupportedEncodingException {  
        StringBuilder sb = new StringBuilder();  
        StringBuilder noAsciiPart = new StringBuilder();  
        for (int i = 0; i < url.length(); i++) {  
            char c = url.charAt(i);  
            if (c > 255) {  
                noAsciiPart.append(c);  
            } else {  
                if (noAsciiPart.length() != 0) {  
                    sb.append(URLEncoder.encode(noAsciiPart.toString(), encode));  
                    noAsciiPart.delete(0, noAsciiPart.length());  
                }  
                sb.append(c);  
            }
        }
        if (noAsciiPart.length() != 0) {
        	sb.append(URLEncoder.encode(noAsciiPart.toString(), encode));  
        }
        return sb.toString();  
    }



}