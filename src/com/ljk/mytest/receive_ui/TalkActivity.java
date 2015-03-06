package com.ljk.mytest.receive_ui;

import android.os.Bundle;
import android.widget.TextView;

import com.ljk.bss_mymqtt.R;
import com.ljk.mytest.bean.Report;


public class TalkActivity extends BaseActivity {
	private TextView mTextView;
	private TextView countTextView;
	private boolean mm=false;
//MQTT/MSG/545ae3820cb73c28dd29b41d
	@Override
	protected void receiveReport(Report report) {
		// TODO Auto-generated method stub
		if (report!=null) {
			mm=true;
				mTextView.setText(report.getContent()+"\n"+report.getUid()+"\n"+report.getId()+"\n"+report.getNickname());
				refreshBageView();
		}
	
	}

	@Override
	public void refreshBageView() {
		// TODO Auto-generated method stub
		if (mm) {
		countTextView.setText("收到新消息！");	
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mTextView=(TextView) findViewById(R.id.msg);
		countTextView=(TextView) findViewById(R.id.mcount);
		saveLoginConfig("545ae3820cb73c28dd29b41d","");
		//send--54698f070cb73c3a16a9bc86
		startService(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	/*{"data":
	 * {"username":"service",
	 * "level":"master",
	 * "_id":"545ae3820cb73c28dd29b41d",
	 * "email":"service@smartbow.net",
	 * "nickname":"service",
	 * "tel":""},"status"
	 * :true}*/
}
