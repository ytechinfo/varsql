package com.varsql.app.admin.beans;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

/**
 * 
 * 
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: VtconnectionOption.java
* @DESC		: 커넥션 옵션. 
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2018. 2. 19. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public class VtconnectionOption{
	@NotNull
	@Size(max=5)
	private String vconnid;
	
	private String userId;
	
	private String pollinit;
	
	@NotNull
	@Range(min=-1, max=1000)
	private Integer maxActive;
	
	@NotNull
	@Range(min=-1, max=1000)
	private Integer minIdle; 
	
	@NotNull
	@Range(min=-1, max=1000000)
	private Integer timeout;
	
	@NotNull
	@Range(min=-1, max=100000000)
	private Integer exportCount;
	
	@Size(max=1000)
	private String vquery;

	public String getVconnid() {
		return vconnid;
	}

	public void setVconnid(String vconnid) {
		this.vconnid = vconnid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPollinit() {
		return pollinit;
	}

	public void setPollinit(String pollinit) {
		this.pollinit = pollinit;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getExportCount() {
		return exportCount;
	}

	public void setExportCount(int exportCount) {
		this.exportCount = exportCount;
	}
	
	public String getVquery(){
		return this.vquery;
	}
	public void setVquery(String vquery){
		this.vquery=vquery;
	}

}