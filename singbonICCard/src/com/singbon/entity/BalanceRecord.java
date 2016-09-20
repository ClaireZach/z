package com.singbon.entity;

import java.io.Serializable;

public class BalanceRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer companyId;
	private Integer deptId;
	private Integer operId;
	private String deptName;
	private String operName;
	private Integer recordTypes;
	private Integer totalOpCount;
	private Long totalOpFare;
	private Integer discountFare;
	private Integer honchonCount;
	private Long honchonFare;
	private String beginDate;
	private String endDate;
	private Integer froms;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
	public Integer getTotalOpCount() {
		return totalOpCount;
	}
	public void setTotalOpCount(Integer totalOpCount) {
		this.totalOpCount = totalOpCount;
	}
	public Long getTotalOpFare() {
		return totalOpFare;
	}
	public void setTotalOpFare(Long totalOpFare) {
		this.totalOpFare = totalOpFare;
	}
	
	
	public Integer getDiscountFare() {
		return discountFare;
	}
	public void setDiscountFare(Integer discountFare) {
		this.discountFare = discountFare;
	}
	public Integer getOperId() {
		return operId;
	}
	public void setOperId(Integer operId) {
		this.operId = operId;
	}
	public Integer getHonchonCount() {
		return honchonCount;
	}
	public void setHonchonCount(Integer honchonCount) {
		this.honchonCount = honchonCount;
	}
	public Long getHonchonFare() {
		return honchonFare;
	}
	public void setHonchonFare(Long honchonFare) {
		this.honchonFare = honchonFare;
	}
	public Integer getRecordTypes() {
		return recordTypes;
	}
	public void setRecordTypes(Integer recordTypes) {
		this.recordTypes = recordTypes;
	}
	
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Integer getFroms() {
		return froms;
	}
	public void setFroms(Integer froms) {
		this.froms = froms;
	}
	
	

}
