package com.varsql.web.model.base;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * -----------------------------------------------------------------------------
* @fileName		: BaseAuditorModel.java
* @desc		: model audit model
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 20. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@SuppressWarnings("serial")
@Setter
@Getter
@MappedSuperclass
public abstract class AabstractAuditorModel extends AbstractRegAuditorModel{
	
	@JsonIgnore
    @LastModifiedBy
    @Column(name="UPD_ID")
    private String updId;

	@JsonIgnore
    @LastModifiedDate
    @Column(name="UPD_DT")
    private LocalDateTime updDt;

}