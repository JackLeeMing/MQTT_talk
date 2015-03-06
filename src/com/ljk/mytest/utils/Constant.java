package com.ljk.mytest.utils;

/** 
 * TODO<常用的常量设置> 
 * @author  JackLeeMing 
 * @data:  2015-3-6 上午9:35:58 
 * @version:  V1.0 
 */
public class Constant {
	public static final String SERVER_HOST="tcp://59.108.232.150:1883";
	public static final int REPORT_IN=0;
	public static final int REPORT_OUT=1;
	public static final int REPORT_SEND_FAILED=0;
	public static final int REPORT_SEND_OK=1;
	public static final int REPORT_STATUS_READ=1;
	public static final int REPORT_STATUS_UNREAD=0;
	public static final String LOGIN_SETTINGS = "smartbow2_login_settings";
	public static final String TOPIC_RT="MQTT/MSG";
	public static final String NEW_REPORT_ACTION = "new_report";
	public static final String CONC_LIST_URI = "/mob/contactlist";
	public static final String SEND_REPORT_URI = "/manage/message/send?uto=%s&body=%s";
	public static final String APPNAME = "smartbow_bss";
	public static final String APPTTL = "smartbow_bss_operate";
	/** 用户密码提取键 */
	public static final String PASSWORD = "PASSWORD";
	/** 用户昵称提取键 */
	public static final String NICKNAME = "nickname";
	/** 用户UserID提取键 */
	public static final String UID = "uid";
	
	public static final String UID2 = "uid_33";
	
	public static final String CLIENT_ID = "main";
	/** 用户登录验证配置 */
	public static final String LOGIN_HOST = "http://cas.citybms.com";
	//public static final String LOGIN_HOST = "http://cas.citybms.com:8887";
	/** 用户登录验证URI */
	public static final String LOGIN_URI = "/m/login";
	/** 用户名键 */
	public static final String LOGIN_NAME_KEY = "loginname";
	/** 用户密码键 */
	public static final String LOGIN_PWD_KEY = "pwd";
	public static final String USERNAME = "username";
	
	public static final String FRAG_IDX = "fragment_index";

	/** 最新消息提醒 */
	public static final String TICKER = "SmartBow: 您有一条新消息";

	/** 设置cookie所需的额外参数 domain & path */
	public static final String COOKIE_EXTRA_STR = "Max-Age=3600;Domain=.thunics.org;Path=/";
}
