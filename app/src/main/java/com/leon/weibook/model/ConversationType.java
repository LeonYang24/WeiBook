package com.leon.weibook.model;

/**
 * 该类用来描述对话类型。
 * 因为两个人对话可能是单聊，也可能是群聊。所以需要额外的类型区分
 * Created by Leon on 2016/5/13 0013.
 */
public enum ConversationType {

	Single(0), Group(1);

	/**
	 * 创建的时候直接设置 type 字段
	 */
	public static final String TYPE_KEY = "type";

	/**
	 * 查找对话的时候，要加前缀 attr. 其实type保存在conversation的attr中
	 * 登录网站后台，_Conversation 表可看到
	 */
	public static final String ATTR_TYPE_KEY = "attr.type";

	int value;

	ConversationType(int value) {
		this.value = value;
	}

	/**
	 * 该函数用于返回对话类型
	 * @param i 0代表单聊， 1代表群聊  其它也认为是群聊
	 * @return
	 */
	public static ConversationType fromInt(int i) {
		if (i < 2) {
			return values()[i];//values（）表示得到全部的枚举内容，然后以对象数组的形式用foreach输出
		} else {
			return Group;
		}
	}

	public int getValue() {
		return value;
	}

}
