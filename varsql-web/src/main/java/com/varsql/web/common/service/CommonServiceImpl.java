package com.varsql.web.common.service;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.varsql.web.constants.ResourceConfigConstants;
import com.varsql.web.model.entity.app.ExceptionLogEntity;
import com.varsql.web.model.entity.db.DBConnHistEntity;
import com.varsql.web.model.entity.sql.SqlHistoryEntity;
import com.varsql.web.model.entity.sql.SqlStatisticsEntity;
import com.varsql.web.repository.db.DBConnHistEntityRepository;
import com.varsql.web.repository.sql.SqlExceptionLogEntityRepository;
import com.varsql.web.repository.sql.SqlHistoryEntityRepository;
import com.varsql.web.repository.sql.SqlStatisticsEntityRepository;
import com.vartech.common.utils.CommUtils;

/**
 *
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: CommonServiceImpl.java
* @DESC		: 공통 서비스
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2019. 4. 16. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Service
public class CommonServiceImpl{
	private final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
	
	private SqlExceptionLogEntityRepository sqlExceptionLogEntityRepository;

	private SqlHistoryEntityRepository sqlHistoryEntityRepository;

	private SqlStatisticsEntityRepository sqlStatisticsEntityRepository;
	
	private DBConnHistEntityRepository dbConnHistEntityRepository;
	
	public CommonServiceImpl(SqlExceptionLogEntityRepository sqlExceptionLogEntityRepository, SqlHistoryEntityRepository sqlHistoryEntityRepository, SqlStatisticsEntityRepository sqlStatisticsEntityRepository, DBConnHistEntityRepository dbConnHistEntityRepository) {
		this.sqlExceptionLogEntityRepository = sqlExceptionLogEntityRepository; 
		this.sqlHistoryEntityRepository = sqlHistoryEntityRepository; 
		this.sqlStatisticsEntityRepository = sqlStatisticsEntityRepository; 
		this.dbConnHistEntityRepository = dbConnHistEntityRepository; 
	}

	/**
	 *
	 * @Method Name  : insertExceptionLog
	 * @Method 설명 : error insert
	 * @작성자   : ytkim
	 * @작성일   : 2019. 4. 16.
	 * @변경이력  :
	 * @param exceptionType
	 * @param e
	 */
	@Async(ResourceConfigConstants.APP_LOG_TASK_EXECUTOR)
	public void insertExceptionLog(String exceptionType, Throwable e) {
		try{
			String exceptionTitle = e.getMessage();
			
			sqlExceptionLogEntityRepository.save(ExceptionLogEntity.builder()
					.excpType(exceptionType)
					.excpCont(CommUtils.getExceptionStr(e).substring(0 , 2000))
					.excpTitle(exceptionTitle.length() > 1500 ?exceptionTitle.substring(0,1500) :  exceptionTitle)
					.serverId(CommUtils.getHostname()).build());
		}catch(Throwable e1) {
			logger.error("insertExceptionLog Cause : {}", e1.getMessage());
		}
	}

	/**
	 *
	 * @Method Name  : saveSqlHistory
	 * @Method 설명 : sql history 저장.
	 * @작성일   : 2020. 11. 04.
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @param sqlHistoryEntity
	 */
	@Async(ResourceConfigConstants.APP_LOG_TASK_EXECUTOR)
	public void saveSqlHistory(SqlHistoryEntity sqlHistoryEntity) {
		try {
			sqlHistoryEntityRepository.save(sqlHistoryEntity);
		}catch(Throwable e) {
			logger.error(" sqlData sqlHistoryEntity : {}", e.getMessage());
		}
	}

	/**
	 *
	 * @Method Name  : sqlLogInsert
	 * @Method 설명 : 사용자 sql 로그 저장
	 * @작성일   : 2015. 5. 6.
	 * @작성자   : ytkim
	 * @변경이력  :
	 */
	@Async(ResourceConfigConstants.APP_LOG_TASK_EXECUTOR)
	public void sqlLogInsert(List<SqlStatisticsEntity> allSqlStatistics) {
		try{
			if(allSqlStatistics.size() == 1) {
				sqlStatisticsEntityRepository.save(allSqlStatistics.get(0));
			}else if(allSqlStatistics.size() > 1) {
				sqlStatisticsEntityRepository.saveAll(allSqlStatistics);
			}
	    }catch(Exception e){
	    	logger.error(" sqlLogInsert {}", e.getMessage());
	    }
	}
	
	/**
	 *
	 * @Method Name  : saveDbConnectionHistory
	 * @Method 설명 : 사용자 접속 로그 저장
	 * @작성일   : 2015. 5. 6.
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @param dbConnHistEntity
	 */
	@Async(ResourceConfigConstants.APP_LOG_TASK_EXECUTOR)
	public void saveDbConnectionHistory(DBConnHistEntity dbConnHistEntity) {
		try{
			dbConnHistEntityRepository.save(dbConnHistEntity);
		}catch(Exception e){
			logger.error(" saveDbConnectionHistory {}", e.getMessage());
		}
	}
}