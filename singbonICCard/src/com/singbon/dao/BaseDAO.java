package com.singbon.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.singbon.entity.OpenIdsAndMobiles;

/**
 * 基础数据库操作类
 * 
 * @author 郝威
 * 
 */
@Repository
public interface BaseDAO {

	/**
	 * 添加对象
	 * 
	 * @param obj
	 * @return
	 */
	public Object insert(Object obj) throws Exception;

	/**
	 * 修改对象
	 * 
	 * @param obj
	 * @return
	 */
	public void update(Object obj) throws Exception;

	/**
	 * 删除对象
	 * 
	 * @param obj
	 * @return
	 */
	public void delete(Object obj) throws Exception;

	/**
	 * 根据主键获取对象
	 * 
	 * @param id
	 * @return
	 */
	public Object selectById(@Param("id") Integer id);

	/**
	 * 根据object获取对象
	 * 
	 * @param id
	 * @return
	 */
	public Object selectByObject(Object object);

	/**
	 * 根据公司id查询单个对象
	 * 
	 * @return
	 */
	public Object selectByCompanyId(Integer companyId);

	/**
	 * 根据公司id查询
	 * 
	 * @param id
	 * @return
	 */
	public List<?> selectListByCompanyId(Integer companyId);

	/**
	 * 自定义sql查询多条
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> selectListBySql(@Param("sql") String sql);

	/**
	 * 自定义sql查询单个
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map selectBySql(@Param("sql") String sql);

	/**
	 * 自定义sql更新
	 * 
	 * @param id
	 * @return
	 */
	public Integer updateSql(@Param("sql") String sql);

	/**
	 * 初始化微信短信系统启动
	 * 
	 * @param id
	 * @return
	 */
	public void initWeixinShortSystemStart();

	/**
	 * 初始化微信短信过凌晨
	 * 
	 * @param id
	 * @return
	 */
	public void initWeixinShortNextDay();

	/**
	 * 查询微信openId和关联手机号
	 * 
	 * @param openIdsAndMobiles
	 */
	public void selectOpenIdsAndMobiles(OpenIdsAndMobiles openIdsAndMobiles) throws Exception;

}
