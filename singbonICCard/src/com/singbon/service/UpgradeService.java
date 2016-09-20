package com.singbon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.singbon.dao.BaseDAO;
import com.singbon.dao.UpgradeDAO;
import com.singbon.entity.Upgrade;

/**
 * 系统升级业务层
 * 
 * @author 郝威
 * 
 */
@Service
public class UpgradeService extends BaseService {

	@Autowired
	public UpgradeDAO upgradeDAO;

	@Override
	public BaseDAO getBaseDAO() {
		return upgradeDAO;
	}

	/**
	 * 查询所有升级文件
	 * 
	 * @return
	 */
	public List<Upgrade> selectList() {
		return this.upgradeDAO.selectList();
	}
}
