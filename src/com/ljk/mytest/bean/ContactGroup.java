package com.ljk.mytest.bean;

import java.util.List;

/**
 * 联系人群组 
 */
public class ContactGroup {
	private String mGroupName;
	private String mGroupId;
	private List<Contact> mContactList;
	
	public String getGroupName() {
		return mGroupName;
	}
	
	public void setGroupName(String groupname) {
		mGroupName = groupname;
	}
	
	public String getGroupId() {
		return mGroupId;
	}
	
	public void setGroupId(String groupId) {
		mGroupId = groupId;
	}
	
	public List<Contact> getContactList() {
		return mContactList;
	}
	
	public void setContactList(List<Contact> contactList) {
		mContactList = contactList;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{ groupname: " + mGroupName).append(", ");
		sb.append("groupid:" + mGroupId).append(", ");
		sb.append("members: [");
		for (Contact contact : mContactList) {
			sb.append(contact.toString()).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("] }");
		return sb.toString();
	}
	
	
	public static class Contact {
		public String uid;
		public String nickname;
		
		
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


		@Override
		public String toString() {
			return "{ uid: " + uid + ", nickname: " + nickname + " }";
		}
	}
	
}
