package com.singbon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.singbon.dao.BalanceRecordDAO;
import com.singbon.dao.BaseDAO;

@Service
public class BalanceRecordService extends BaseService {

	@Autowired
	private BalanceRecordDAO balanceRecordDAO;

	@Override
	public BaseDAO getBaseDAO() {
		return balanceRecordDAO;
	}

	public void insert() {
		balanceRecordDAO.insert();
	}

}
