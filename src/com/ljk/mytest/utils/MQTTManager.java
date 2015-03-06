package com.ljk.mytest.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ljk.mytest.application.AppContext;


import android.content.Context;
import android.util.Log;

public class MQTTManager {
	private static MQTTManager instanceManager;
	private static MqttClient client;
	private MqttConnectOptions options;
	private static Context mContext;
	private Lock lock=new ReentrantLock();

	public MqttClient getClient() {
		return client;

	}

	public void connect(){
		reconnect();
	}


	public void reconnect(){
		try {
			lock.lock();
			if (client!=null) {
				options=new MqttConnectOptions();
				options.setCleanSession(true);
				options.setConnectionTimeout(10);
				options.setKeepAliveInterval(20);	
			}
			client.connect(options);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}


	private MQTTManager(Context mContext){
		this.mContext=mContext;
	}

	public static MQTTManager  getInstance(Context context){
		if (instanceManager==null) {
			instanceManager=new MQTTManager(context.getApplicationContext());
			try {
				client= new MqttClient(Constant.SERVER_HOST,"main"+AppContext.mContext.getDeviceId(), new MemoryPersistence());
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instanceManager;

	}

	/** 发送信息 */
	public static boolean sendMessage(String topic, String content) {
		MqttTopic mTopic = client.getTopic(topic);
		try {
			MqttDeliveryToken token = mTopic.publish(content.getBytes(), 1, false);
			int retry_times = 0;
			while (retry_times < 3 && !token.isComplete()) {
				token.waitForCompletion(1000);
				retry_times++;
			}
			if (token.isComplete())
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/** 发送信息 */
	public static void sendMessage(String topic, final String content,boolean isT) {
		final MqttTopic mTopic = client.getTopic(topic);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					MqttDeliveryToken token = mTopic.publish(content.getBytes(), 2, false);
					int retry_times = 0;
					while (retry_times < 3 && !token.isComplete()) {
						token.waitForCompletion(1000);
						retry_times++;
					}
					if (token.isComplete())
						return;
				} catch (Exception e) {
					return;
				}	
			}
		}).start();

	}

	/** 断开连接 */
	public void disconnect() {
		if (client != null) {
			try {
				client.disconnect();
				client = null;
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
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
	 * 将字符串转化成JSON
	 */
	public static JSONArray String2array(double[] data) {
		JSONArray array=null;
		try {
			array=new JSONArray(data);
		} catch (JSONException e) {
			Log.e("String2Json", "JSONException: " + Log.getStackTraceString(e));
		} catch (Exception e) {
			Log.e("String2Json", "Exception: " + Log.getStackTraceString(e));
		}

		return array;
	}


}
