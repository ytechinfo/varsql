package com.varsql.web.dto.db;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.varsql.web.dto.valid.ValidUrlDirectYn;
import com.varsql.web.model.entity.db.DBConnectionEntity;
import com.varsql.web.util.ConvertUtils;

import lombok.Getter;
import lombok.Setter;

/**
 *
 *
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: Vtconnection.java
* @DESC		: 커넥션 정보.
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2018. 2. 19. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Getter
@Setter
@ValidUrlDirectYn
public class DBConnectionRequestDTO{
	@Size(max=5)
	private String vconnid;

	@NotEmpty
	@Size(max=250)
	private String vname;

	@Size(max=45)
	private String vserverip;

	@Range(min=-1, max=65535)
	private int vport;

	@Size(max=250)
	private String vdatabasename;

	@Size(max=250)
	private String vurl;

	@NotEmpty
	@Size(max=100)
	private String vdriver;

	@Size(max=100)
	private String vid;

	@Size(max=500)
	private String vpw;

	@Size(max=500)
	private String confirmPw;

	@Size(max=1)
	private String urlDirectYn;

	private String vdbversion;

	private String userId;

	private String poolInit;

	private String vdbschema;

	@NotEmpty
	@Size(max=1)
	private String useYn;

	@NotEmpty
	@Size(max=1)
	private String basetableYn;

	@NotEmpty
	@Size(max=1)
	private String lazyloadYn;

	@NotEmpty
	@Size(max=1)
	private String schemaViewYn;

	@NotNull
	@Range(min=-1, max=1000)
	private int maxActive;

	@NotNull
	@Range(min=-1, max=1000)
	private int minIdle;

	@NotNull
	@Range(min=-1, max=1000000)
	private int timeout;

	@NotNull
	@Range(min=-1, max=100000000)
	private long exportcount;

	@NotNull
	@Range(min=-1, max=100000000)
	private long maxSelectCount;

	@NotEmpty
	@Size(max=1)
	private String useColumnLabel;

	private boolean passwordChange;

	public void setPasswordChange(String passwordChange) {
		this.passwordChange = Boolean.parseBoolean(passwordChange);
	}

	public DBConnectionEntity toEntity() {
		return DBConnectionEntity.builder()
				.vconnid(vconnid)
				.vname(vname)
				.vserverip(vserverip)
				.vport(vport)
				.vdatabasename(vdatabasename)
				.vurl(vurl)
				.vdriver(vdriver)
				.vid(vid)
				.vpw(vpw)
				.urlDirectYn(urlDirectYn)
				.vdbversion(ConvertUtils.longValueOf(vdbversion))
				.vdbschema(vdbschema)
				.useYn(useYn)
				.basetableYn(basetableYn)
				.schemaViewYn(schemaViewYn)
				.lazyloadYn(lazyloadYn)
				.maxActive(maxActive)
				.minIdle(minIdle)
				.timeout(timeout)
				.exportcount(exportcount)
				.maxSelectCount(maxSelectCount)
				.useColumnLabel(useColumnLabel)
				.build();
	}
}