package com.singbon.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.singbon.dao.BaseDAO;
import com.singbon.dao.CompanyDAO;
import com.singbon.dao.ConsumeRecordDAO;
import com.singbon.dao.systemManager.systemSetting.EntranceTimeDAO;

public class JdbcUtil implements ApplicationContextAware {

	public static BaseDAO baseDAO;
	public static ConsumeRecordDAO consumeRecordDAO;
	public static EntranceTimeDAO entranceTimeDAO;
	public static CompanyDAO companyDAO;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		JdbcUtil.baseDAO = (BaseDAO) context.getBean("baseDAO");
		JdbcUtil.consumeRecordDAO = (ConsumeRecordDAO) context.getBean("consumeRecordDAO");
		JdbcUtil.entranceTimeDAO = (EntranceTimeDAO) context.getBean("entranceTimeDAO");
		JdbcUtil.companyDAO = (CompanyDAO) context.getBean("companyDAO");
	}
}
