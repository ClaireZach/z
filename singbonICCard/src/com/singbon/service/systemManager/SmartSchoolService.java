package com.singbon.service.systemManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.singbon.dao.BaseDAO;
import com.singbon.dao.systemManager.SmartSchoolDAO;
import com.singbon.service.BaseService;

/**
 * 智慧校园业务层
 * 
 * @author 郝威
 * 
 */
@Service
public class SmartSchoolService extends BaseService {

	@Autowired
	public SmartSchoolDAO smartSchoolDAO;

	public BaseDAO getBaseDAO() {
		return smartSchoolDAO;
	}

}
