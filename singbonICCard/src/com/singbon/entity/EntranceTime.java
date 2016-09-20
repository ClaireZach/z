package com.singbon.entity;

import java.io.Serializable;

/**
 * 进出门时间段
 * 
 * @author 郝威
 * 
 */
public class EntranceTime implements Serializable {

	private static final long serialVersionUID = -4349415810057681133L;
	private Integer id;
	private Integer companyId;
	private String beginTime;
	private String endTime;
	private Integer status;
	private String remark;

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

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
