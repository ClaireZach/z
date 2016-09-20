package com.singbon.dao;

import java.util.List;

import com.singbon.entity.Upgrade;

/**
 * 系统升级dao层
 * 
 * @author 郝威
 * 
 */
public interface UpgradeDAO extends BaseDAO {

	/**
	 * 查询所有升级文件
	 * 
	 * @return
	 */
	public List<Upgrade> selectList();
}
