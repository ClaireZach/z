package com.singbon.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.singbon.dao.BaseDAO;
import com.singbon.entity.Pagination;

/**
 * 公共通用业务层
 * 
 * @author 郝威
 * 
 */
@Service
public class CommonService extends BaseService {

	@Autowired
	public BaseDAO baseDAO;

	@Override
	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	@SuppressWarnings("rawtypes")
	public List<Map> selectListBySql(String sql) {
		return this.getBaseDAO().selectListBySql(sql);
	}

	@SuppressWarnings("rawtypes")
	public List<Map> selectByPage(String[] countColumns, String[] dataColumns, String fromSql, String whereSql, Pagination pagination) {
		String selectCountSql = "";
		String selectDataSql = "";
		if (dataColumns == null) {
			dataColumns = countColumns;
		}
		int i = 0;
		for (String col : countColumns) {
			int dotIndex = col.indexOf(".");
			if (dotIndex != -1) {
				col = col.substring(dotIndex + 1);
			}
			if (i == 0) {
				selectCountSql += "count(*) " + col + ",";
			} else {
				selectCountSql += "null " + col + ",";
			}
			i++;
		}
		for (String col : dataColumns) {
			selectDataSql += col + ",";
		}
		selectCountSql = selectCountSql.substring(0, selectCountSql.length() - 1);
		selectDataSql = selectDataSql.substring(0, selectDataSql.length() - 1);
		String fullSql = String.format("select %s from %s where %s union (select %s from %s where %s limit %s,%s)", selectCountSql, fromSql, whereSql, selectDataSql, fromSql, whereSql,
				pagination.getOffset(), pagination.getNumPerPage());
		return this.getBaseDAO().selectListBySql(fullSql);
	}

	@SuppressWarnings("rawtypes")
	public List<Map> selectNoPage(String[] countColumns, String[] dataColumns, String fromSql, String whereSql) {
		String selectCountSql = "";
		String selectDataSql = "";
		if (dataColumns == null) {
			dataColumns = countColumns;
		}
		int i = 0;
		for (String col : countColumns) {
			int dotIndex = col.indexOf(".");
			if (dotIndex != -1) {
				col = col.substring(dotIndex + 1);
			}
			if (i == 0) {
				selectCountSql += "count(*) " + col + ",";
			} else {
				selectCountSql += "null " + col + ",";
			}
			i++;
		}
		for (String col : dataColumns) {
			selectDataSql += col + ",";
		}
		selectCountSql = selectCountSql.substring(0, selectCountSql.length() - 1);
		selectDataSql = selectDataSql.substring(0, selectDataSql.length() - 1);
		String fullSql = String.format("select %s from %s where %s union (select %s from %s where %s)", selectCountSql, fromSql, whereSql, selectDataSql, fromSql, whereSql);
		return this.getBaseDAO().selectListBySql(fullSql);
	}
}
