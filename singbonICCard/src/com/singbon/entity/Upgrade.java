package com.singbon.entity;

import java.io.Serializable;

/**
 * 系统升级
 * 
 * @author 郝威
 * 
 */
public class Upgrade implements Serializable {

	private static final long serialVersionUID = -4349415810057681133L;
	private Integer id;
	private String truename;
	private String filename;
	private Integer filesize;
	private String uploadTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getFilesize() {
		return filesize;
	}

	public void setFilesize(Integer filesize) {
		this.filesize = filesize;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

}
