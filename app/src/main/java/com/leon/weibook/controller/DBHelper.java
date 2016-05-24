package com.leon.weibook.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leon.weibook.util.LogUtils;

/**
 *
 * Created by Leon on 2016/5/14 0014.
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final int DB_VER = 6;
	private String userId;

	private DBHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	DBHelper(Context context, String userId) {
		this(context, "chat_" + userId + ".db3", DB_VER);
		this.userId = userId;
	}

	/**
	 * 当数据库被首次创建时执行该方法，一般将创建表等初始化操作在该方法中执行。
	 * @param db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	/**
	 * 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (newVersion) {
			case 6 :
				RoomsTable.dropTable(db);
				RoomsTable.createTableAndIndex(db);
			case 2:
			case 1:
				break;
		}
	}

	/**
	 * 该方法会在每次打开数据库时被调用
	 * @param db
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		LogUtils.d("chat db path", db.getPath());
		RoomsTable.createTableAndIndex(db);
	}
}
