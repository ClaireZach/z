package com.singbon.entity;

import java.io.Serializable;

/**
 * 考勤记录
 * 
 * @author 郝威
 * 
 */
public class AttendanceRecord implements Serializable {

	private static final long serialVersionUID = -4349415810057681133L;

	private Integer id;
	private Integer companyId;
	private Long userId;
	private Long cardNO;
	private String opTime;
	private Integer status;

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCardNO() {
		return cardNO;
	}

	public void setCardNO(Long cardNO) {
		this.cardNO = cardNO;
	}

	public String getOpTime() {
		return opTime;
	}

	public void setOpTime(String opTime) {
		this.opTime = opTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
