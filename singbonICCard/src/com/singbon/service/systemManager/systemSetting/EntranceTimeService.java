package com.singbon.service.systemManager.systemSetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.singbon.dao.BaseDAO;
import com.singbon.dao.systemManager.systemSetting.EntranceTimeDAO;
import com.singbon.service.BaseService;

/**
 * 进出门时间段业务层
 * 
 * @author 郝威
 * 
 */
@Service
public class EntranceTimeService extends BaseService {

	@Autowired
	public EntranceTimeDAO entranceTimeDAO;

	@Override
	public BaseDAO getBaseDAO() {
		return entranceTimeDAO;
	}

	/**
	 * 添加
	 * 
	 * @param orderTime
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void insert(Integer companyId) {
		this.entranceTimeDAO.insert(companyId);
	}

}
