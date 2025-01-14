package com.varsql.web.app.database.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.varsql.web.constants.ResourceConfigConstants;
import com.varsql.web.dto.sql.SqlFileRequestDTO;
import com.varsql.web.dto.sql.SqlFileResponseDTO;
import com.varsql.web.model.entity.sql.SqlFileEntity;
import com.varsql.web.model.entity.sql.SqlFileTabEntity;
import com.varsql.web.repository.spec.SqlFileSpec;
import com.varsql.web.repository.sql.SqlFileEntityRepository;
import com.varsql.web.repository.sql.SqlFileTabEntityRepository;
import com.varsql.web.util.VarsqlUtils;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.StringUtils;

/**
 *
 * @FileName  : SQLFileServiceImpl.java
 * @Date      : 2014. 8. 18.
 * @작성자      : ytkim
 * @변경이력 :
 * @프로그램 설명 :
 */
@Service
public class SQLFileServiceImpl{
	private final Logger logger = LoggerFactory.getLogger(SQLFileServiceImpl.class);

	@Autowired
	private SqlFileEntityRepository sqlFileEntityRepository;

	@Autowired
	private SqlFileTabEntityRepository sqlFileTabEntityRepository;

	/**
	 * 쿼리 저장.
	 * @param sqlFileRequestDTO
	 */
	@Transactional(value = ResourceConfigConstants.APP_TRANSMANAGER,rollbackFor=Throwable.class)
	public ResponseResult saveSql(SqlFileRequestDTO sqlFileRequestDTO) {
		ResponseResult result = new ResponseResult();

		String mode = String.valueOf(sqlFileRequestDTO.getCustom().get("mode"));

		SqlFileEntity sqlFileInfo = sqlFileRequestDTO.toEntity();

		if("newfile".equals(mode)){

			sqlFileTabEntityRepository.updateSqlFileTabDisable(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());

			sqlFileInfo = sqlFileEntityRepository.save(sqlFileInfo);
		}else if("moveTab".equals(mode)){

			SqlFileTabEntity moveFileTabInfo= sqlFileTabEntityRepository.findByVconnidAndViewidAndSqlId(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());

			// 이동전 앞에 sqlid 파일 업데이트.
			sqlFileTabEntityRepository.updatePrevIdByPrevId(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId(), moveFileTabInfo.getPrevSqlId());

			if(StringUtils.isBlank(sqlFileRequestDTO.getPrevSqlId())) {
				sqlFileTabEntityRepository.updatePrevIdBySqlId(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getFirstSqlId(), sqlFileRequestDTO.getSqlId());
			}else {
				// 이동 할 위치 이전  sqlid 업데이트
				sqlFileTabEntityRepository.updatePrevIdByPrevId(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getPrevSqlId(), sqlFileRequestDTO.getSqlId());
			}

			moveFileTabInfo.setViewYn(true);
			moveFileTabInfo.setPrevSqlId(sqlFileRequestDTO.getPrevSqlId());
			sqlFileTabEntityRepository.save(moveFileTabInfo);
		}else{

			if("addTab".equals(mode)){
				sqlFileTabEntityRepository.updateSqlFileTabDisable(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());
				SqlFileTabEntity sqlFileTabEntity= SqlFileTabEntity.builder().vconnid(sqlFileRequestDTO.getVconnid()).viewid(sqlFileRequestDTO.getViewid()).sqlId(sqlFileRequestDTO.getSqlId())
					.prevSqlId(sqlFileRequestDTO.getPrevSqlId()).viewYn(true).build();

				sqlFileTabEntity = sqlFileTabEntityRepository.save(sqlFileTabEntity);

			}else if(mode.startsWith("delTab")){
				deleteSqlFileTabInfo(sqlFileRequestDTO, mode);
			}else if("viewTab".equals(mode)){
				sqlFileTabEntityRepository.updateSqlFileTabDisable(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());
				sqlFileTabEntityRepository.updateSqlFileTabEnable(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());
			}else{

				sqlFileInfo = sqlFileEntityRepository.findOne(SqlFileSpec.detailSqlFile(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getSqlId())).orElse(null);
				if(sqlFileInfo != null) {
					if(sqlFileRequestDTO.getSqlCont() !=null) sqlFileInfo.setSqlCont(sqlFileRequestDTO.getSqlCont());
					if(sqlFileRequestDTO.getSqlParam() !=null) sqlFileInfo.setSqlParam(sqlFileRequestDTO.getSqlParam());
					if(sqlFileRequestDTO.getSqlTitle() !=null) sqlFileInfo.setSqlTitle(sqlFileRequestDTO.getSqlTitle());
					if(sqlFileRequestDTO.getEditorCursor() !=null) sqlFileInfo.setEditorCursor(sqlFileRequestDTO.getEditorCursor());

					sqlFileInfo = sqlFileEntityRepository.save(sqlFileInfo);
				}

				if("query_del".equals(mode)){
					deleteSqlFileTabInfo(sqlFileRequestDTO);
				}
			}
		}

		if(sqlFileInfo != null) sqlFileRequestDTO.setSqlId(sqlFileInfo.getSqlId());

		result.setItemOne(sqlFileRequestDTO);

		return result;
	}

	/**
	 *
	 * @Method Name  : saveAllQuery
	 * @Method 설명 : sql 파일 모두저장.
	 * @작성자   : ytkim
	 * @작성일   : 2018. 11. 26.
	 * @변경이력  :
	 * @param sqlParamInfo
	 * @return
	 */
	@Transactional(value = ResourceConfigConstants.APP_TRANSMANAGER,rollbackFor=Throwable.class)
	public ResponseResult saveAllSql(SqlFileRequestDTO sqlParamInfo) {
		Map<String,Object> customParam = (Map<String,Object>)sqlParamInfo.getCustom();

		String sqlIdStr = String.valueOf(customParam.get("sqlIdArr"));

		String[] sqlIdArr= sqlIdStr.split(";");

		SqlFileEntity sfe;
		List<SqlFileEntity> sqlFileInfos = new ArrayList<>();
		for (int i = 0; i < sqlIdArr.length; i++) {
			String sqlId = sqlIdArr[i];

			sfe = sqlFileEntityRepository.findOne(SqlFileSpec.detailSqlFile(sqlParamInfo.getVconnid(), sqlId)).orElse(null);

			if(sfe == null) continue;

			sfe.setSqlId(sqlId);
			sfe.setSqlParam(String.valueOf(customParam.get(sqlId+"_param")));
			sfe.setSqlCont(String.valueOf(customParam.get(sqlId)));
			sfe.setEditorCursor(String.valueOf(customParam.get(sqlId+"_cursor")));

			sqlFileInfos.add(sfe);
		}

		sqlFileEntityRepository.saveAll(sqlFileInfos);

		return VarsqlUtils.getResponseResultItemOne(1);
	}

	/**
	 * 사용자 sql 목록 보기.
	 * @param sqlParamInfo
	 * @return
	 */
	public ResponseResult selectSqlFileList(SqlFileRequestDTO sqlParamInfo) {

		List<SqlFileResponseDTO> files = new ArrayList<>();

		sqlFileEntityRepository.findAll(SqlFileSpec.findVconnSqlFileName(sqlParamInfo.getVconnid(), String.valueOf(sqlParamInfo.getCustom().get("searchVal")))).forEach(item ->{
			files.add(SqlFileResponseDTO.builder()
				.sqlId(item.getSqlId())
				.sqlTitle(item.getSqlTitle())
				.build()
			);
		});

		return VarsqlUtils.getResponseResultItemList(files);
	}

	/**
	 * sql 저장 정보 삭제 .
	 * @param sqlParamInfo
	 * @return
	 */
	@Transactional(value = ResourceConfigConstants.APP_TRANSMANAGER,rollbackFor=Throwable.class)
	public ResponseResult deleteSqlSaveInfo(SqlFileRequestDTO sqlFileRequestDTO) {
		sqlFileEntityRepository.deleteSqlFileInfo(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getSqlId());
		deleteSqlFileTabInfo(sqlFileRequestDTO);

		return VarsqlUtils.getResponseResultItemOne(1);
	}

	/**
	 *
	 * @Method Name  : selectSqlFileTabList
	 * @Method 설명 : sql file tab list
	 * @작성자   : ytkim
	 * @작성일   : 2018. 11. 7.
	 * @변경이력  :
	 * @param sqlParamInfo
	 * @return
	 */
	public ResponseResult selectSqlFileTabList(SqlFileRequestDTO sqlParamInfo) {
		return VarsqlUtils.getResponseResultItemList(sqlFileTabEntityRepository.findSqlFileTab(sqlParamInfo.getVconnid() , sqlParamInfo.getViewid()));
	}

	/**
	 *
	 * @Method Name  : sqlFileDetailInfo
	 * @Method 설명 : sql file 상세보기.
	 * @작성자   : ytkim
	 * @작성일   : 2018. 11. 26.
	 * @변경이력  :
	 * @param sqlParamInfo
	 * @return
	 */
	public ResponseResult sqlFileDetailInfo(SqlFileRequestDTO sqlParamInfo) {
		return VarsqlUtils.getResponseResultItemOne(sqlFileEntityRepository.findOne(SqlFileSpec.detailSqlFile(sqlParamInfo.getVconnid(), sqlParamInfo.getSqlId())).orElse(null));
	}

	private void deleteSqlFileTabInfo(SqlFileRequestDTO sqlFileRequestDTO) {
		deleteSqlFileTabInfo(sqlFileRequestDTO, "delTab");
	}

	private void deleteSqlFileTabInfo(SqlFileRequestDTO sqlFileRequestDTO, String mode) {
		try{
			int tabLen = -1;
			try{
				tabLen = Integer.parseInt(String.valueOf(sqlFileRequestDTO.getCustom().get("len")));
			}catch(Exception e){
				tabLen = -1;
			}

			if("delTab-all".equals(mode) || tabLen ==0) {
				sqlFileTabEntityRepository.deleteAllSqlFileTabInfo(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid());
			}else {
				SqlFileTabEntity sfte = sqlFileTabEntityRepository.findByVconnidAndViewidAndSqlId(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());

				if("delTab-other".equals(mode)) {
					if(sfte != null) {
						sfte.setPrevSqlId(null);
					}
					sqlFileTabEntityRepository.deleteOtherFileTab(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());
				}else {
					sqlFileTabEntityRepository.deleteTabInfo(sqlFileRequestDTO.getVconnid(), sqlFileRequestDTO.getViewid(), sqlFileRequestDTO.getSqlId());
				}

				if(sfte != null) {
					sqlFileTabEntityRepository.updatePrevIdByPrevId(sfte.getVconnid(), sfte.getViewid(), sfte.getSqlId(), sfte.getPrevSqlId());
				}
			}

		}catch(Exception e){
			logger.error("deleteSqlFileTabInfo" ,e);
			throw e;
		}
	}
}