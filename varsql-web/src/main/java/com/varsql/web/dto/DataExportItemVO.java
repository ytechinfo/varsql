package com.varsql.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * export object info
* 
* @fileName	: DataExportItemVO.java
* @author	: ytkim
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DataExportItemVO {
	
	private String name;
	
	private String condition;
}
