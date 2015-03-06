package com.ljk.mytest.bean;

import java.io.Serializable;

import com.ljk.mytest.utils.Constant;

public class Report implements Serializable {
	/** 报告指令id */
	private long _id;
	/** 报告指令状态 */
	private int type;
	/** 报告指令发送用户id */
	private String uid;
	/** 报告指令发送用户昵称 */
	private String nickname;
	/** 报告指令发送时间（毫秒） */
	private Long time;
	/** 报告指令内容 */
	private String content;
	/** 报告指令是否已读 */
	private int read;
	/** 报告指令发送or接收状态 */
	private int status;
	
	public Report() {
		
		status=Constant.REPORT_SEND_OK;
		type=Constant.REPORT_IN;
		read=Constant.REPORT_STATUS_UNREAD;
		
	}
	
	public Report(long _id, int type, String uid, String nickname, Long time,
			String content, int read, int status) {
		super();
		this._id = _id;
		this.type = type;
		this.uid = uid;
		this.nickname = nickname;
		this.time = time;
		this.content = content;
		this.read = read;
		this.status = status;
	}
	
	public long getId() {
		return _id;
	}
	
	public void setId(long _id) {
		this._id = _id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public Long getTime() {
		return time;
	}
	
	public void setTime(Long time) {
		this.time = time;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getRead() {
		return read;
	}
	
	public void setRead(int read) {
		this.read = read;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
}
