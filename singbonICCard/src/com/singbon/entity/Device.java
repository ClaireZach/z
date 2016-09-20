package com.singbon.entity;

import java.io.Serializable;

/**
 * 终端设备
 * 
 * @author 郝威
 * 
 */
public class Device implements Serializable {

	private static final long serialVersionUID = -4349415810057681133L;

	private Integer id;
	private Integer companyId;
	private Integer deptId;
	private Integer paramGroupId;
	private String deviceName;
	private Integer deviceNum;
	private Integer deviceType;
	private Integer transferId;
	private Integer enable;
	private Integer lastRecordNO = 0;
	private Integer entranceType;
	/**
	 * 监控用
	 */
	private Integer isOnline;
	private String sn;
	private String transferSn;

	/**
	 * 升级用，文件类型
	 */
	private String upgradeType;

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

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Integer getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(Integer deviceNum) {
		this.deviceNum = deviceNum;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Integer isOnline) {
		this.isOnline = isOnline;
	}

	public Integer getLastRecordNO() {
		return lastRecordNO;
	}

	public void setLastRecordNO(Integer lastRecordNO) {
		this.lastRecordNO = lastRecordNO;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public Integer getParamGroupId() {
		return paramGroupId;
	}

	public void setParamGroupId(Integer paramGroupId) {
		this.paramGroupId = paramGroupId;
	}

	public Integer getTransferId() {
		return transferId;
	}

	public void setTransferId(Integer transferId) {
		this.transferId = transferId;
	}

	public String getTransferSn() {
		return transferSn;
	}

	public void setTransferSn(String transferSn) {
		this.transferSn = transferSn;
	}

	public String getUpgradeType() {
		// BS消费机，BS水控机，BS中转通信器，BS门禁机，BS考勤门禁机，BS中转消费机，BS中转水控机，BS中转门禁机，BS中转考勤门禁机
		if (this.deviceType == 1) {
			upgradeType = "BS中转通信器";
		} else if (this.deviceType == 2) {
			upgradeType = "BS消费机";
			if (this.transferId != null && this.transferId != 0) {
				upgradeType = "BS中转消费机";
			}
		} else if (this.deviceType == 3) {
			upgradeType = "BS水控机";
			if (this.transferId != null && this.transferId != 0) {
				upgradeType = "BS中转水控机";
			}
		} else if (this.deviceType == 8) {
			upgradeType = "BS读卡机";
		}
		return upgradeType;
	}

	public void setUpgradeType(String upgradeType) {
		this.upgradeType = upgradeType;
	}

	public Integer getEntranceType() {
		return entranceType;
	}

	public void setEntranceType(Integer entranceType) {
		this.entranceType = entranceType;
	}

}
