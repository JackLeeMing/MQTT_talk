package com.ljk.mytest.manager;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite数据库辅助类
 * 创建所有该应用需使用到的数据表
 * 提供数据库基本操作功能 
 */
public class DataBaseHelper extends SQLiteOpenHelper {

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 *	若database存在，则数据表不会再创建 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("DATABASE", "EXEC");
		try {
			String sql = "CREATE TABLE [" + ReportManager.cantcactName + "] "//reports
					+ "([_id] NVARCHAR NOT NULL PRIMARY KEY,"
					+ "[groupid] NVARCHAR, "+ "[nickname] NVARCHAR, [groupname] NVARCHAR,[status] NVARCHAR);";
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.i("Database", "table " + ReportManager.cantcactName + " exists.");
		}

		
		
		try {
			String sql = "CREATE TABLE [" + ReportManager.tableName + "] "//reports
					+ "([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
					+ "[type] INTEGER, [content] NVARCHAR, [uid] NVARCHAR, "
					+ "[nickname] NVARCHAR, [time] BIGINT, [read] INTEGER, [status] INTEGER);";
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.i("Database", "table " + ReportManager.tableName + " exists.");
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
