package com.ljk.mytest.service;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONException;
import org.json.JSONObject;

import com.ljk.bss_mymqtt.R;
import com.ljk.mytest.application.AppContext;
import com.ljk.mytest.bean.Report;
import com.ljk.mytest.http.BssApi;
import com.ljk.mytest.manager.ReportManager;
import com.ljk.mytest.receive_ui.TalkActivity;
import com.ljk.mytest.utils.Constant;
import com.ljk.mytest.utils.MQTTManager;
import com.ljk.mytest.utils.SystemUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  JackLeeMing 
 * @data:  2015-3-6 上午9:52:40 
 * @version:  V1.0 
 */

public class TalkService extends Service implements MqttCallback {
	private Context mContext;
	protected MqttClient mClient;
	protected String mUID;
	protected NotificationManager notificationManager;
	private MeReceiver receiver;
	private String sendMuid;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext=this;
		mUID=BssApi.getUID();
		sendMuid=BssApi.getUID2();
		initService();
		Log.i("TAG",mUID);
		Log.i("TAG",sendMuid);
		Log.e("TAG","create");
		//开启广播 接收指令
		IntentFilter filter = new IntentFilter();

		filter.addAction("com.ljk.startSend");

		registerReceiver(receiver, filter);
	}


	private void initService() {
		// TODO Auto-generated method stub
		notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		receiver = new MeReceiver();
		new SubscribeTask().start();
	}


	/**
	 * 订阅任务
	 * 
	 * */

	private  class  SubscribeTask extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			MQTTManager manager=MQTTManager.getInstance(mContext);
			do {
				while (!AppContext.isNetworkConnected()) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				manager.connect();
			} while (manager.getClient()!=null&&!manager.getClient().isConnected());
			mClient=manager.getClient();
			try {
				mClient.subscribe("my/test",1);
				String report_topic=Constant.TOPIC_RT+"/"+mUID;
				mClient.subscribe(report_topic, 2);
				mClient.setCallback(TalkService.this);
			} catch (Exception e) {

			}
		}

	}
	/**
	 * 广播接收
	 * */
	class MeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String data=intent.getStringExtra("data");
			if (data!=null&&!data.equals("")) {
				MQTTManager.sendMessage(Constant.TOPIC_RT+"/"+sendMuid, data, false);
			}
		}

	}

	/**
	 *取消任务
	 * 
	 * */
	private class UnSubscribeTask extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mClient=MQTTManager.getInstance(mContext).getClient();
			try {
				mClient.unsubscribe("my/test");
				String report_topic=Constant.TOPIC_RT+"/"+mUID;
				mClient.unsubscribe(report_topic);
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** 
	 * 报告指令新增信息监听器
	 * 当App处于前台时监听器将通过广播方式发送通知
	 * 当App处于后台时监听器将通过通知栏方式发送通知 
	 */

	public void RtPacketListener(MqttTopic topic,JSONObject report){
		String report_topic=Constant.TOPIC_RT+"/"+mUID;
		if (!report_topic.equals(topic.getName())) {
			return;
		}

		Report newReport=new Report();


		try {
			if (report.has("timestamp")) {
				newReport.setTime((long) report.getDouble("timestamp") * 1000);

			}else{
				newReport.setTime(System.currentTimeMillis());
			}

			newReport.setType(Constant.REPORT_IN);
			newReport.setUid(report.getString("ufrom"));
			newReport.setNickname(report.getString("ufrom_name"));
			newReport.setContent(report.getString("body"));
			newReport.setRead(Constant.REPORT_STATUS_UNREAD);
			newReport.setId(ReportManager.getInstance(mContext).save(newReport));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return;
		}
		boolean bgFlag=SystemUtils.isBackground(mContext);
		if (bgFlag) {
			NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(mContext);
			String nickname=newReport.getNickname();
			String name=nickname==null||nickname.equals("")?newReport.getUid():nickname;
			mBuilder.setContentTitle(name);
			mBuilder.setContentText(newReport.getContent());
			mBuilder.setSmallIcon(R.drawable.ic_launcher);
			mBuilder.setTicker(Constant.TICKER);
			mBuilder.setNumber(1);
			mBuilder.setAutoCancel(true);
			mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
			Intent notificationIntent=new Intent(mContext,TalkActivity.class);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			notificationIntent.setFlags(Intent.FILL_IN_DATA);
			notificationIntent.putExtra(Constant.FRAG_IDX, "哈哈");

			TaskStackBuilder stackBuilder=TaskStackBuilder.create(mContext);
			stackBuilder.addParentStack(TalkActivity.class);
			stackBuilder.addNextIntent(notificationIntent);
			PendingIntent pendingIntent=stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);

			NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(0, mBuilder.build());

		}else{
			Intent broadcastIntent=new Intent(Constant.NEW_REPORT_ACTION);
			broadcastIntent.putExtra("report_key", newReport);
			sendBroadcast(broadcastIntent);
		}

	}


	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		MQTTManager manager=MQTTManager.getInstance(mContext);
		manager.disconnect();
		do {
			while(!AppContext.isNetworkConnected()){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					continue;
				}

			}
			manager.reconnect();
		} while (manager.getClient().isConnected());
		new SubscribeTask().start();
	}

	@Override
	public void deliveryComplete(MqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(MqttTopic topic, MqttMessage msg)
			throws Exception {
		// TODO Auto-generated method stub
		if ("my/test".equals(topic.getName())) {
			return;
		}
		JSONObject json=MQTTManager.String2Json(msg.toString());
		if (json!=null) {
			Log.v("TAG",json.toString());
			RtPacketListener(topic, json);
		}

	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			Thread task=new UnSubscribeTask();
			task.start();
			task.join();
		} catch (Exception e) {
			// TODO: handle exception
		}
		unregisterReceiver(receiver);
	}

}
