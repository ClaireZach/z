package com.singbon.entity;

import java.io.Serializable;
import java.math.BigDecimal;

//用户卡信息
//
//@author 郝威
//

public class User implements Serializable {

	private static final long serialVersionUID = -4349415810057681133L;

	// 卡基本信息
	// 用户帐号
	private Long userId;
	// 所属公司
	private Integer companyId;
	// 所属部门
	private Integer deptId;
	// 用户姓名
	private String username;
	// 姓名简拼
	private String shortName;
	// 用户编号
	private String userNO;
	// 身份证号
	private String cardID;
	// 逻辑卡号
	private Long cardNO;
	// 物理卡号
	private String cardSN;
	// 卡序号(第几张卡)
	private Integer cardSeq;
	// 性别
	private Integer sex;
	// 性别描述
	@SuppressWarnings("unused")
	private String sexDesc;
	private String mobile1;
	private String mobile2;
	// 卡型号(0~15类卡)
	private Integer cardTypeId;
	private String beginDate;
	private String endDate;
	// 消费密码
	private String consumePwd;
	// 制卡日期
	private String cardMakeDate;
	// 卡总额
	private BigDecimal totalFare;
	// 卡状态
	private Integer status;
	// 卡状态描述
	@SuppressWarnings("unused")
	private String statusDesc;

	// 大钱包内容
	// 卡余额
	private BigDecimal oddFare;
	// 上次消费时间
	private String lastConsumeTime;
	// 当日操作金额
	private BigDecimal daySumFare;
	// 卡操作计数器(大钱包)
	private Integer opCount;

	// 小钱包内容
	// 卡补助余额
	private BigDecimal subsidyOddFare;
	// 上次补助消费时间
	private String lastSubsidyConsumeTime;
	// 当日补助操作金额
	private BigDecimal subsidyDaySum;
	// 卡操作计数器(小钱包)
	private Integer subsidyOpCount;
	// 补助版本
	private Integer subsidyVersion;
	// 补助失效日期
	private String subsidyInvalidDate;
	// 卡押金
	private BigDecimal cardDeposit;
	// 赠送金额
	private BigDecimal giveFare;
	// 预发金额
	private BigDecimal preOpFare;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getUserNO() {
		return userNO;
	}

	public void setUserNO(String userNO) {
		this.userNO = userNO;
	}

	public Long getCardNO() {
		return cardNO;
	}

	public void setCardNO(Long cardNO) {
		this.cardNO = cardNO;
	}

	public String getCardSN() {
		return cardSN;
	}

	public void setCardSN(String cardSN) {
		this.cardSN = cardSN;
	}

	public Integer getCardSeq() {
		return cardSeq;
	}

	public void setCardSeq(Integer cardSeq) {
		this.cardSeq = cardSeq;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getSexDesc() {
		if (this.sex == 0) {
			return "男";
		} else {
			return "女";
		}
	}

	public void setSexDesc(String sexDesc) {
		this.sexDesc = sexDesc;
	}

	public Integer getCardTypeId() {
		return cardTypeId;
	}

	public void setCardTypeId(Integer cardTypeId) {
		this.cardTypeId = cardTypeId;
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

	public String getConsumePwd() {
		return consumePwd;
	}

	public void setConsumePwd(String consumePwd) {
		this.consumePwd = consumePwd;
	}

	/**
	 * 0未发卡、241正常、243挂失、244注销卡，其他都是异常卡
	 * 
	 * @return
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 0未发卡、241正常、243挂失、244注销卡，其他都是异常卡
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 0未发卡、241正常、243挂失、244注销卡，其他都是异常卡
	 */
	public String getStatusDesc() {
		String des = null;
		if (status == 0) {
			des = "未发卡";
		} else if (status == 241) {
			des = "正常";
		} else if (status == 243) {
			des = "挂失";
		} else if (status == 244) {
			des = "注销";
		} else {
			des = "异常卡";
		}
		return des;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getLastConsumeTime() {
		return lastConsumeTime;
	}

	public void setLastConsumeTime(String lastConsumeTime) {
		this.lastConsumeTime = lastConsumeTime;
	}

	public BigDecimal getDaySumFare() {
		return daySumFare;
	}

	public void setDaySumFare(BigDecimal daySumFare) {
		this.daySumFare = daySumFare;
	}

	public String getLastSubsidyConsumeTime() {
		return lastSubsidyConsumeTime;
	}

	public void setLastSubsidyConsumeTime(String lastSubsidyConsumeTime) {
		this.lastSubsidyConsumeTime = lastSubsidyConsumeTime;
	}

	public Integer getSubsidyVersion() {
		return subsidyVersion;
	}

	public void setSubsidyVersion(Integer subsidyVersion) {
		this.subsidyVersion = subsidyVersion;
	}

	public String getSubsidyInvalidDate() {
		return subsidyInvalidDate;
	}

	public void setSubsidyInvalidDate(String subsidyInvalidDate) {
		this.subsidyInvalidDate = subsidyInvalidDate;
	}

	public String getCardID() {
		return cardID;
	}

	public void setCardID(String cardID) {
		this.cardID = cardID;
	}

	public String getCardMakeDate() {
		return cardMakeDate;
	}

	public void setCardMakeDate(String cardMakeDate) {
		this.cardMakeDate = cardMakeDate;
	}

	public BigDecimal getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(BigDecimal totalFare) {
		this.totalFare = totalFare;
	}

	public BigDecimal getOddFare() {
		return oddFare;
	}

	public void setOddFare(BigDecimal oddFare) {
		this.oddFare = oddFare;
	}

	public Integer getOpCount() {
		return opCount;
	}

	public void setOpCount(Integer opCount) {
		this.opCount = opCount;
	}

	public BigDecimal getSubsidyOddFare() {
		return subsidyOddFare;
	}

	public void setSubsidyOddFare(BigDecimal subsidyOddFare) {
		this.subsidyOddFare = subsidyOddFare;
	}

	public BigDecimal getSubsidyDaySum() {
		return subsidyDaySum;
	}

	public void setSubsidyDaySum(BigDecimal subsidyDaySum) {
		this.subsidyDaySum = subsidyDaySum;
	}

	public Integer getSubsidyOpCount() {
		return subsidyOpCount;
	}

	public void setSubsidyOpCount(Integer subsidyOpCount) {
		this.subsidyOpCount = subsidyOpCount;
	}

	public BigDecimal getGiveFare() {
		return giveFare;
	}

	public void setGiveFare(BigDecimal giveFare) {
		this.giveFare = giveFare;
	}

	public BigDecimal getPreOpFare() {
		return preOpFare;
	}

	public void setPreOpFare(BigDecimal preOpFare) {
		this.preOpFare = preOpFare;
	}

	public BigDecimal getCardDeposit() {
		return cardDeposit;
	}

	public void setCardDeposit(BigDecimal cardDeposit) {
		this.cardDeposit = cardDeposit;
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}
	
}
