package com.singbon.entity;

import java.io.Serializable;

/**
 * 智慧校园管理员
 * 
 * @author 郝威
 * 
 */
public class SmartSchoolAdmin implements Serializable {

	private static final long serialVersionUID = -4349415810057681133L;

	private Integer id;
	private Integer companyId;
	private String loginName;
	private String adminName;
	private String loginPwd;
	private String deptIds;
	private Integer isAdmin;
	private String remark;
	private Integer weixinLimit;
	private Integer remainingWeixin;
	private String lastWeixinTime;
	private Integer shortLimit;
	private Integer remainingShort;
	private String lastShortTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getDeptIds() {
		return deptIds;
	}

	public void setDeptIds(String deptIds) {
		this.deptIds = deptIds;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getWeixinLimit() {
		return weixinLimit;
	}

	public void setWeixinLimit(Integer weixinLimit) {
		this.weixinLimit = weixinLimit;
	}

	public Integer getShortLimit() {
		return shortLimit;
	}

	public void setShortLimit(Integer shortLimit) {
		this.shortLimit = shortLimit;
	}

	public Integer getRemainingWeixin() {
		return remainingWeixin;
	}

	public void setRemainingWeixin(Integer remainingWeixin) {
		this.remainingWeixin = remainingWeixin;
	}

	public String getLastWeixinTime() {
		return lastWeixinTime;
	}

	public void setLastWeixinTime(String lastWeixinTime) {
		this.lastWeixinTime = lastWeixinTime;
	}

	public Integer getRemainingShort() {
		return remainingShort;
	}

	public void setRemainingShort(Integer remainingShort) {
		this.remainingShort = remainingShort;
	}

	public String getLastShortTime() {
		return lastShortTime;
	}

	public void setLastShortTime(String lastShortTime) {
		this.lastShortTime = lastShortTime;
	}

	public Integer getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}

}
