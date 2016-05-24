package com.leon.weibook.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.leon.weibook.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 最近对话的列表表格，这里称之为Rooms
 * Created by Leon on 2016/5/14 0014.
 */
public class RoomsTable {

	private static final String ROOMS_TABLE = "rooms";
	private static final String ROOM_CONVID = "convid";
	private static final String ROOM_UNREAD_COUNT = "unread_count";
	private static final String ROOM_ID = "id";
	public static final String ROOM_CONVID_INDEX = "convid_index";

	private DBHelper dbHelper;
	//Creates a new, empty map with the default initial table size (16).
	private static Map<String, RoomsTable> roomsTableInstances = new ConcurrentHashMap<>();

	/**
	 * 内部私有静态类SQL
	 * 用于构建各种sql语句
	 */
	private static class SQL {
		//创建rooms表，包含主键roomid，字段convid和字段unread_count
		private static final String CREATE_ROOMS_TABLE = "CREATE TABLE IF NOT EXISTS " +
				ROOMS_TABLE + "(" + ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ROOM_CONVID + " VARCHAR(63) UNIQUE NOT NULL, " + ROOM_UNREAD_COUNT +
				" INTEGER DEFAULT 0)";
		//创建rooms表的字段convid的索引convid_index，
		private static final String CREATE_ROOM_CONVID_INDEX = "CREATE UNIQUE INDEX IF NOT EXISTS "
				+ ROOM_CONVID_INDEX + " on " + ROOMS_TABLE + " ( " + ROOM_CONVID + " ) ";
		//根据convid来更新未读信息unread_count（+1）
		private static final String UPDATE_ROOMS_INCREASE_UNREAD_COUNT_WHERE_CONVID = "UPDATE "
				+ ROOMS_TABLE + " SET " + ROOM_UNREAD_COUNT + " = " + ROOM_UNREAD_COUNT
				+ " + 1 WHERE " + ROOM_CONVID + " =?";
		//删除rooms表
		private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + ROOMS_TABLE;
	}

	private RoomsTable(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	/**
	 * 通过userId来实例化一个RoomsTable（单例模式）
	 * @param context
	 * @param userId
	 * @return
	 */
	public synchronized static RoomsTable getInstanceByUserId(Context context, String userId) {
		RoomsTable roomsTable = roomsTableInstances.get(userId);
		if (roomsTable == null) {
			roomsTable = new RoomsTable(new DBHelper(context.getApplicationContext(), userId));
		}
		return roomsTable;
	}

	/**
	 * 创建表和索引
	 * @param db
	 */
	static void createTableAndIndex(SQLiteDatabase db) {
		db.execSQL(SQL.CREATE_ROOMS_TABLE);
		db.execSQL(SQL.CREATE_ROOM_CONVID_INDEX);
	}

	/**
	 * 删除表
	 * @param db
	 */
	static void dropTable(SQLiteDatabase db) {
		db.execSQL(SQL.DROP_TABLE);
	}

	/**
	 *
	 * @param columns
	 * @return
	 */
	private static String getWhereClause(String... columns) {
		List<String> conditions = new ArrayList<String>();
		for (String column : columns) {
			conditions.add(column + " = ? ");
		}
		return TextUtils.join(" and ", conditions);
	}

	/**
	 * 获取rooms表里所有room
	 * @return
	 */
	public List<Room> selectRooms() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query(ROOMS_TABLE, null, null, null, null, null, null);
		List<Room> rooms = new ArrayList<>();
		while (c.moveToNext()) {
			Room room = createRoomByCursor(c);
			rooms.add(room);
		}
		c.close();
		db.close();
		return rooms;
	}

	/**
	 * 通过光标来创建room
	 * @param c
	 * @return
	 */
	private Room createRoomByCursor(Cursor c) {
		Room room = new Room();
		room.setConversationId(c.getString(c.getColumnIndex(ROOM_CONVID)));
		room.setUnreadCount(c.getInt(c.getColumnIndex(ROOM_UNREAD_COUNT)));
		return room;
	}

	/**
	 * 往rooms表中插入room
	 * @param convid
	 */
	public void insertRoom(String convid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(ROOM_CONVID, convid);
		db.insertWithOnConflict(ROOMS_TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
	}

	/**
	 * 删除rooms表中的room
	 * @param convid
	 */
	public void deleteRoom(String convid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(ROOMS_TABLE, getWhereClause(ROOM_CONVID), new String[]{convid});
	}

	/**
	 * 此处的消息未读数量仅仅指的是本机的未读消息数量，并没有存储到 server 端
	 * 在收到消息时消息未读数量 + 1
	 * @param convid
	 */
	public void increaseUnreadCount(String convid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(SQL.UPDATE_ROOMS_INCREASE_UNREAD_COUNT_WHERE_CONVID, new String[]{convid});
	}

	/**
	 * 在 ChatItemHolder 中显示时清除未读消息数量
	 * @param convid
	 */
	public void clearUnread(String convid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(ROOM_UNREAD_COUNT, 0);
		db.update(ROOMS_TABLE, cv, getWhereClause(ROOM_CONVID),
				new String[]{convid});
	}

}
