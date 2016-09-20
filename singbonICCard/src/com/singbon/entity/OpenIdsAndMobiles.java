package com.singbon.entity;

import java.io.Serializable;

/**
 * openIds mobiles
 * 
 * @author 郝威
 * 
 */
public class OpenIdsAndMobiles implements Serializable {

	private static final long serialVersionUID = -4349415810057681133L;
	private Integer id;
	private Integer companyId;
	private String loginName;
	private Integer messageType;
	private long userId;
	private Integer weixinCharge;
	private Integer version;
	private Integer result;
	private String username;
	private String openIds;
	private String mobiles;

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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOpenIds() {
		return openIds;
	}

	public void setOpenIds(String openIds) {
		this.openIds = openIds;
	}

	public String getMobiles() {
		return mobiles;
	}

	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public Integer getWeixinCharge() {
		return weixinCharge;
	}

	public void setWeixinCharge(Integer weixinCharge) {
		this.weixinCharge = weixinCharge;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
