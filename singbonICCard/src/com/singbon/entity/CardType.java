package com.singbon.entity;

import java.util.HashMap;

/**
 * 
 * 卡类型
 * 
 * @author 陈梦
 *
 */
public class CardType {
	static HashMap<Integer, String> map = new HashMap<Integer, String>();

	static {
		map.put(0, "未发卡");
		map.put(241, "正常卡");
		map.put(243, "挂失卡");
		map.put(244, "注销卡");
	}

	public static String getTypeDes(Integer consumeType) {
		if (map.containsKey(consumeType)) {
			return (String) map.get(consumeType);
		}
		return "异常卡";
	}

}
