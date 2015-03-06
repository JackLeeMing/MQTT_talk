package com.ljk.mytest.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ljk.mytest.bean.Report;
import com.ljk.mytest.http.BssApi;
import com.ljk.mytest.manager.DBManager;
import com.ljk.mytest.manager.SQLiteTemplate;
import com.ljk.mytest.manager.SQLiteTemplate.RowMapper;
import com.ljk.mytest.utils.Constant;
import com.ljk.mytest.utils.MQTTManager;
import com.ljk.mytest.bean.ContactGroup;
import com.ljk.mytest.bean.ContactGroup.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class ReportManager extends BaseManager {
	private static String TAG = "ReportManager";
	private static ReportManager mManager = null;
	private static DBManager dbManager = null;
	private Context context;
	public static final String tableName = "reports";
	public static final String cantcactName = "report_member";
	
	private ReportManager(Context context) {
		String databaseName ="report_db";
		this.context=context;
		dbManager = DBManager.getInstance(context, databaseName);
	}
	
	public static ReportManager getInstance(Context context) {
		if (mManager == null) {
			mManager = new ReportManager(context.getApplicationContext());
		}
		
		return mManager;
	}
	
	// 保存报告信息到数据库中
	public long save(Report report) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("uid", report.getUid());
		contentValues.put("nickname", report.getNickname());
		contentValues.put("type", report.getType());
		contentValues.put("time", report.getTime());
		contentValues.put("content", report.getContent());
		contentValues.put("read", report.getRead());
		contentValues.put("status", report.getStatus());
		
		return st.insert(tableName, contentValues);
	}
		
	
	public void updateRead(Long id, Integer read) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		ContentValues contentValues = new ContentValues();
		contentValues.put("read", read);
		st.updateById(tableName, id.toString(), contentValues);
	}
	
	public void updateStatus(Long id, Integer status) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		ContentValues contentValues = new ContentValues();
		contentValues.put("status", status);
		st.updateById(tableName, id.toString(), contentValues);
	}
	
	public void updateReadByUID(String uid, Integer read) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		ContentValues contentValues = new ContentValues();
		contentValues.put("read", read);
		st.update(tableName, contentValues, "uid=?", new String[] {uid});
	}
	
	/** 获取与指定用户的聊天记录， 时间为升序 */
	public List<Report> getHisReportsByUID(String uid, int pageNum, int pageSize, boolean reverse) {
		if (uid == null || uid.equals("")) {
			return null;
		}
		
		int fromIndex = (pageNum - 1) * pageSize;
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		List<Report> list = st.queryForList(new RowMapper<Report>() {
			@Override
			public Report mapRow(Cursor cursor, int index) {
				Report report = new Report();
				report.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				report.setContent(cursor.getString(cursor.getColumnIndex("content")));
				report.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				report.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				report.setType(cursor.getInt(cursor.getColumnIndex("type")));
				report.setTime(cursor.getLong(cursor.getColumnIndex("time")));
				report.setRead(cursor.getInt(cursor.getColumnIndex("read")));
				report.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				return report;
			}
		},
		"select _id, content, uid, nickname, type, time, read, status from " 
		+ tableName + " where uid=? order by time limit ?, ?",
		new String[] {"" + uid, "" + fromIndex, "" + pageSize});
		
		if (reverse) {
			Collections.reverse(list);
		}
		
		return list;
	}
	
	//删除信息
	public long delet(Report report){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		String uid = report.getUid();
		return st.deleteById(tableName, uid+"");
		
	}
	/**
	 * 根据最小ID及返回结果数进行查询，主要用于动态加载历史数据
	 */
	public List<Report> getReportsByUID(String uid, long minId, int pageSize, boolean reverse) {
		String sql = "select _id, content, uid, nickname, type, time, read, "
				+ "status from " + tableName;
		
		String[] cond;
		if (minId == -1) {
			// 第一次从数据库中取数据
			sql += " where uid=? order by time desc limit ? ";
			cond = new String[] {uid, "" + pageSize};
		} else if (minId > 0) {
			sql += " where uid=? and _id < ?  order by time desc limit ? ";
			cond = new String[] {uid, "" + minId, "" + pageSize};
		} else {
			return null;
		}
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		List<Report> list = st.queryForList(new RowMapper<Report>() {
			@Override
			public Report mapRow(Cursor cursor, int index) {
				Report report = new Report();
				report.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				report.setContent(cursor.getString(cursor.getColumnIndex("content")));
				report.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				report.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				report.setType(cursor.getInt(cursor.getColumnIndex("type")));
				report.setTime(cursor.getLong(cursor.getColumnIndex("time")));
				report.setRead(cursor.getInt(cursor.getColumnIndex("read")));
				report.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				return report;
			}	
		}, sql, cond);
		
		if (reverse) {
			Collections.reverse(list);
		}
		
		return list;
	}
	
	/* 获取各分组的第一个数据 */
	public List<Report> getReportMainList() {
		String sql = "SELECT _id, type, uid, nickname, content, time, read, status FROM " 
				+ tableName + " where time in (select max(time) from " 
				+ tableName + " group by uid) group by uid order by time desc";
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		List<Report> list = st.queryForList(new RowMapper<Report>() {
			@Override
			public Report mapRow(Cursor cursor, int index) {
				Report report = new Report();
				report.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				report.setType(cursor.getInt(cursor.getColumnIndex("type")));
				report.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				report.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				report.setContent(cursor.getString(cursor.getColumnIndex("content")));
				report.setTime(cursor.getLong(cursor.getColumnIndex("time")));
				report.setRead(cursor.getInt(cursor.getColumnIndex("read")));
				report.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				return report;
			}
		}, sql, null);
		
		return list;
	}
	
	/* *
	 * 查找指定来源的消息记录总数
	 * */
	public int getReportCountByFrom(String uid) {
		if (uid.equals("")) {
			return 0;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		return st.getCount("select _id from " + tableName + " where uid=?", new String[] { "" + uid });
	}
	
	public int getUnReadReportCount() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		return st.getCount("select _id from " + tableName + " where read=" + Constant.REPORT_STATUS_UNREAD, null);
	}
	public int getUnReadReportCount_ff(String id) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager);
		return st.getCount("select _id from " + tableName + " where read=? and uid=?",new String[]{""+Constant.REPORT_STATUS_UNREAD,id});
	}

	/* ----------------------------------------------------------
	 * 请求联系列表任务
	 * ---------------------------------------------------------- */
	public static class ContactListTask extends AsyncTask<String, Integer, JSONObject> {
		private Context mContext;
		private String mHost;
		private String mUri;
		private OnDoneCallBack mCallBack;
		
		public ContactListTask(Context context, OnDoneCallBack callback) {
			mContext = context;
			mHost = Constant.SERVER_HOST;
			mUri = Constant.CONC_LIST_URI;
			mCallBack = callback;
			//http://wuxi.citybms.com/mob/contactlist
		}
		
		public void setHostUri(String host, String uri) {
			mHost = host;
			mUri = uri;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject result = null;
			try {
				result = BssApi._get(mHost, mUri);
				
				
				
			} catch (AppException e) {
				Log.e(TAG, "AppException: " + Log.getStackTraceString(e));
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			
			List<ContactGroup> cgList = null;
			if (result == null) {
				Toast.makeText(mContext, "获取通讯录失败", Toast.LENGTH_LONG).show();
				mCallBack.exec(cgList);
				return;
			}
			
			try {
				cgList = new ArrayList<ContactGroup>();
				JSONArray contactList = result.getJSONArray("groups");
				for (int i = 0; i < contactList.length(); i++) {
					ContactGroup cg = new ContactGroup();
					JSONObject group = contactList.getJSONObject(i);
					cg.setGroupName(group.getString("groupname"));
					cg.setGroupId(group.getString("groupid"));
					
					JSONArray members = group.getJSONArray("members");
					List<Contact> list = new ArrayList<Contact>();
					int count = members.length();
					for (int j = 0; j < count; j++) {
						Contact contact = new Contact();
						JSONObject member = members.getJSONObject(j);
						contact.nickname = member.getString("nickname");
						contact.uid = member.getString("uid");
						list.add(contact);
					}
					cg.setContactList(list);
					cgList.add(cg);
				}
				
				mCallBack.exec(cgList);
			} catch (JSONException e) {
				Log.e(TAG, "JSONException: " + Log.getStackTraceString(e));
				Toast.makeText(mContext, "通讯录加载失败", Toast.LENGTH_LONG).show();
				mCallBack.exec(null);
			}

		}
		
		public interface OnDoneCallBack {
			public void exec(List<ContactGroup> cgList);
		}
	}
	
	
	/* ----------------------------------------------------------
	 * 发送列表
	 * ---------------------------------------------------------- */
	private List<Contact> mSendingList;
	public void setSendingList(List<Contact> sendingList) {
		mSendingList = sendingList;
	}
	
	public List<Contact> getSendingList() {
		return mSendingList;
	}
	
	// 群发任务
	public static class MassSendingTask extends AsyncTask<String, Integer, Integer> {
		private Context mContext;
		
		public MassSendingTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			MqttClient client = MQTTManager.getInstance(mContext).getClient();
			client.getTopic("");
			return null;
		}
		
	}
	
	
	/**
	 * 发送信息任务
	 */
	public class SendMsgThread extends Thread {
		public boolean result = false;
		private String to;
		private String content;
		
		public SendMsgThread(String to, String content) {
			try {
				this.to = URLEncoder.encode(to, "utf-8");
				this.content = URLEncoder.encode(content, "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		
		@Override
		public void run() {
			String uri = String.format(Constant.SEND_REPORT_URI, to, content); 
			try {
				BssApi._get(Constant.SERVER_HOST, uri);
				result = true;
			} catch (AppException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}
	
	/**
	 * 发送信息
	 */
	public boolean sendMessage(final String to, final String content) {
		SendMsgThread thread = new SendMsgThread(to, content);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return false;
		}
		return thread.result;
	}
	
	
	@Override
	public void destroy() {
		mManager = null;
		dbManager = null;
	}
	
	
}
