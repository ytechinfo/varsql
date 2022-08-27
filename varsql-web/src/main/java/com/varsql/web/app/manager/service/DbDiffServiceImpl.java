package com.varsql.web.app.manager.service;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.varsql.core.db.DBVenderType;
import com.varsql.core.db.MetaControlBean;
import com.varsql.core.db.MetaControlFactory;
import com.varsql.core.db.servicemenu.ObjectType;
import com.varsql.core.db.valueobject.BaseObjectInfo;
import com.varsql.core.db.valueobject.DatabaseInfo;
import com.varsql.core.db.valueobject.DatabaseParamInfo;
import com.varsql.core.db.valueobject.ddl.DDLCreateOption;
import com.varsql.web.model.entity.db.DBConnectionEntity;
import com.varsql.web.repository.db.DBConnectionEntityRepository;
import com.varsql.web.util.DatabaseUtils;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.utils.StringUtils;

import lombok.RequiredArgsConstructor;

/**
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: DbDiffServiceImpl.java
* @DESC		: db 비교
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2018. 1. 23. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
public class DbDiffServiceImpl{

	private final Logger logger = LoggerFactory.getLogger(DbDiffServiceImpl.class);

	final private DBConnectionEntityRepository  dbConnectionEntityRepository;

	/**
	 *
	 * @Method Name  : objectTypeList
	 * @Method 설명 : db object type list (table , view 등등)
	 * @작성자   : ytkim
	 * @작성일   : 2019. 1. 2.
	 * @변경이력  :
	 * @param vconnid
	 * @return
	 * @throws SQLException
	 */
	public ResponseResult objectTypeList(String vconnid) throws SQLException {

		ResponseResult resultObject = new ResponseResult();

		DBConnectionEntity vtConnRVO = dbConnectionEntityRepository.findByVconnid(vconnid);

		if(vtConnRVO==null){
			resultObject.setResultCode(RequestResultCode.ERROR);
			resultObject.setItemList(null);
		}else{
			
			String dbType = vtConnRVO.getDbTypeDriverProvider().getDbType(); 
			
			DBVenderType venderType = DBVenderType.getDBType(dbType);

			MetaControlBean dbMetaEnum= MetaControlFactory.getDbInstanceFactory(venderType);
			resultObject.setItemList(dbMetaEnum.getServiceMenu());
			
			DatabaseParamInfo param = DatabaseUtils.dbConnectionEntityToDatabaseParamInfo(vtConnRVO);
			
			if(venderType.isUseDatabaseName()) {
				resultObject.addCustoms("schemaInfo", dbMetaEnum.getDatabases(param));
			}else {
				resultObject.addCustoms("schemaInfo", dbMetaEnum.getSchemas(param));
			}
		}

		return resultObject;
	}

	/**
	 *
	 * @param objectType
	 * @Method Name  : objectList
	 * @Method 설명 : object list
	 * @작성자   : ytkim
	 * @작성일   : 2018. 12. 19.
	 * @변경이력  :
	 * @param vconnid
	 * @param objectType
	 * @param schema
	 * @param databaseName
	 * @return
	 */
	public ResponseResult objectList(String vconnid, String objectType, String schema, String databaseName) {

		ResponseResult resultObject = new ResponseResult();

		DatabaseInfo databaseInfo = dbConnectionEntityRepository.findDatabaseInfo(vconnid);

		if(databaseInfo==null){
			resultObject.setResultCode(RequestResultCode.ERROR);
			return resultObject;
		}else{
			DatabaseParamInfo dpi = new DatabaseParamInfo(databaseInfo);
			dpi.setSchema(schema);
			dpi.setDatabaseName(StringUtils.isBlank(databaseName) ? databaseInfo.getDatabaseName(): databaseName);
			dpi.setObjectType(objectType);

			MetaControlBean dbMetaEnum= MetaControlFactory.getDbInstanceFactory(dpi.getDbType());
			String objectId = ObjectType.getDBObjectType(objectType).getObjectTypeId();
			if(ObjectType.TABLE.getObjectTypeId().equals(objectId)){
				resultObject.setItemList(dbMetaEnum.getDBObjectMeta(objectId, dpi));
			}else{
				List<BaseObjectInfo> objectList = dbMetaEnum.getDBObjectList(objectId, dpi);

				String[] objectNameArr = new String[objectList.size()];

				int idx =0 ;
				for(BaseObjectInfo boi : objectList){
					//System.out.println("boi.getName() : "+ boi.getName());
					objectNameArr[idx] =boi.getName();
					++idx;
				}
				resultObject.setItemList(dbMetaEnum.getDDLScript(objectId, dpi, new DDLCreateOption(), objectNameArr));
			}
		}

		return resultObject;
	}
}