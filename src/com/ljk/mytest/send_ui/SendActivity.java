package com.ljk.mytest.send_ui;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.ljk.bss_mymqtt.R;
import com.ljk.mytest.bean.Report;
import com.ljk.mytest.receive_ui.BaseActivity;

public class SendActivity extends BaseActivity {
	private TextView receiveTextView;
	private TextView sendTextView;
	private EditText sendEditText;
	private JSONObject object;
	private int count=150;
	String[] id=new String[]{"545ae3820cb73c28dd29b41d", "54698f070cb73c3a16a9bc86"};
	private String mUId;
	private String toUid;
	String name[]=new String[]{"ljk123","service"};
	private String fName;
	private String toName;
	private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);
		mUId=id[0]; toUid=id[1];
		fName=name[0];toName=name[1];
		receiveTextView=(TextView) findViewById(R.id.save);
		sendEditText=(EditText) findViewById(R.id.edit);
		sendTextView=(TextView) findViewById(R.id.send);
		radioGroup=(RadioGroup) findViewById(R.id.group);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.readio_1:
					mUId=id[0]; toUid=id[1];
					fName=name[0];toName=name[1];
					saveLoginConfig(mUId,toUid);// 

					startService(SendActivity.this);
					break;
				case R.id.readio_2:
					mUId=id[1]; toUid=id[0];
					fName=name[1];toName=name[0];
					saveLoginConfig(mUId,toUid);// 

					startService(SendActivity.this);
					break;
				default:
					mUId=id[0]; toUid=id[1];
					fName=name[0];toName=name[1];
					saveLoginConfig(mUId,toUid);// 

					startService(SendActivity.this);
					break;
				}

			}
		});
	}



	public void send(View view){
		count++;
		String msg=sendEditText.getText().toString();
		sendTextView.setText(msg);
		object=new JSONObject();
		try {
			object.put("body",""+msg).put( "count",""+count).put("ufrom",mUId).put("timestamp", System.currentTimeMillis())
			.put("addon","2015-03-06 17:23:02").put("uto_name",toName).put("uto", toUid).put("ufrom_name", fName);
		} catch (JSONException e) {//54698f070cb73c3a16a9bc86
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent();
		intent.setAction("com.ljk.startSend");
		intent.putExtra("data",object.toString());
		sendBroadcast(intent);
	}

	public void clear(View view){
		sendEditText.setText("");
		sendTextView.setText("");
		receiveTextView.setText("");
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("TAG", "stop");
	}


	@Override
	protected void receiveReport(Report report) {
		// TODO Auto-generated method stub
		if (report!=null) {
			receiveTextView.setText(report.getContent());
		}

	}


	@Override
	public void refreshBageView() {
		// TODO Auto-generated method stub

	}
}
