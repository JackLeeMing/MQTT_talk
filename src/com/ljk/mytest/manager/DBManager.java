package com.ljk.mytest.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQLite数据库管理类
 * 
 * 主要负责数据库资源的初始化,开启,关闭,以及获得DatabaseHelper帮助类操作
 * 
 */
public class DBManager {
	private int version = 1;
	private String databaseName;

	// 本地Context对象
	private Context mContext = null;

	private static DBManager dbManager = null;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	private DBManager(Context context) {
		super();
		this.mContext = context;
	}

	public static DBManager getInstance(Context context, String databaseName) {
		if (dbManager == null) {
			dbManager = new DBManager(context);
		}
		dbManager.databaseName = databaseName;
		return dbManager;
	}

	/**
	 * 关闭数据库 注意:当事务成功或者一次性操作完毕时候再关闭
	 */
	public void closeDatabase(SQLiteDatabase dataBase, Cursor cursor) {
		if (dataBase != null) {
			dataBase.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	/**
	 * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
	 * 
	 * getWritableDatabase: Once opened successfully, the database is cached, 
	 * so you can call this method every time you need to write to the database.
	 * 当调用getWritableDatabase()或getReadableDatabase()时，
	 * 若数据不存在，则会调用DBManager onCreate()
	 */
	public SQLiteDatabase openDatabase() {
		return getDatabaseHelper().getWritableDatabase();
	}

	/**
	 * 获取DataBaseHelper
	 * 
	 */
	public DataBaseHelper getDatabaseHelper() {
		return new DataBaseHelper(mContext, this.databaseName, null,
				this.version);
	}

}
