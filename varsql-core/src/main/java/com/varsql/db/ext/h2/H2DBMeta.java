package com.varsql.db.ext.h2;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.varsql.core.db.MetaControlBean;
import com.varsql.core.db.meta.DBMetaImpl;
import com.varsql.core.db.mybatis.SQLManager;
import com.varsql.core.db.mybatis.resultset.handler.IndexInfoHandler;
import com.varsql.core.db.mybatis.resultset.handler.TableInfoHandler;
import com.varsql.core.db.servicemenu.ObjectType;
import com.varsql.core.db.servicemenu.ObjectTypeTabInfo;
import com.varsql.core.db.valueobject.DatabaseParamInfo;
import com.varsql.core.db.valueobject.IndexInfo;
import com.varsql.core.db.valueobject.ObjectInfo;
import com.varsql.core.db.valueobject.ServiceObject;
import com.varsql.core.db.valueobject.TableInfo;
import com.vartech.common.utils.VartechUtils;


/**
 *
 * @FileName : H2DBMeta.java
 * @작성자 	 : ytkim
 * @Date	 : 2014. 2. 13.
 * @프로그램설명:
 * @변경이력	:
 */
public class H2DBMeta extends DBMetaImpl{

	private final Logger logger = LoggerFactory.getLogger(H2DBMeta.class);

	public H2DBMeta(MetaControlBean dbInstanceFactory){
		super(dbInstanceFactory
			, new ServiceObject(ObjectType.FUNCTION)
			, new ServiceObject(ObjectType.INDEX)
			, new ServiceObject(ObjectType.TRIGGER,false,ObjectTypeTabInfo.MetadataTab.INFO ,ObjectTypeTabInfo.MetadataTab.DDL)
			, new ServiceObject(ObjectType.SEQUENCE, false,ObjectTypeTabInfo.MetadataTab.INFO ,ObjectTypeTabInfo.MetadataTab.DDL)
		);
	}

	@Override
	public List getVersion(DatabaseParamInfo dataParamInfo)  {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("dbSystemView" ,dataParamInfo);
	}

	@Override
	public List<TableInfo> getTables(DatabaseParamInfo dataParamInfo) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("tableList" ,dataParamInfo);
	}
	@Override
	public List<TableInfo> getViews(DatabaseParamInfo dataParamInfo) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("viewList" ,dataParamInfo);
	}

	@Override
	public List<TableInfo> getTableMetadata(DatabaseParamInfo dataParamInfo,String... tableNmArr) throws Exception {
		return tableAndColumnsInfo(dataParamInfo,"tableMetadata" ,tableNmArr);
	}

	@Override
	public List<TableInfo> getViewMetadata(DatabaseParamInfo dataParamInfo,String... tableNmArr) throws Exception	{
		return tableAndColumnsInfo(dataParamInfo,"viewMetadata" ,tableNmArr);
	}

	@Override
	public List<ObjectInfo> getFunctions(DatabaseParamInfo dataParamInfo) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("functionList" ,dataParamInfo);
	}
	@Override
	public List<ObjectInfo> getFunctionMetadata(DatabaseParamInfo dataParamInfo, String... objNames) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("functionMeta" ,dataParamInfo);
	}


	@Override
	public List getIndexs(DatabaseParamInfo dataParamInfo) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("indexList" ,dataParamInfo);
	}
	@Override
	public List<IndexInfo> getIndexMetadata(DatabaseParamInfo dataParamInfo, String... indexName) throws Exception {

		IndexInfoHandler handler = new IndexInfoHandler(dbInstanceFactory.getDataTypeImpl());

		if(indexName!=null && indexName.length > 0){
			StringBuilder sb =new StringBuilder();

			List<String> indexNameList = new ArrayList<String>();

			boolean  addFlag = false;
			for (int i = 0; i < indexName.length; i++) {
				sb.append(addFlag ? ",":"" ).append("'").append(indexName[i]).append("'");

				addFlag = true;
				if(i!=0 && (i+1)%1000==0){
					indexNameList.add(sb.toString());
					sb =new StringBuilder();
					addFlag = false;
				}
			}

			if(sb.length() > 0){
				indexNameList.add(sb.toString());
			}

			dataParamInfo.addCustom("objectNameList", indexNameList);
		}

		SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).select("indexMetadata" ,dataParamInfo , handler);

		return handler.getIndexInfoList();
	}

	@Override
	public List getTriggers(DatabaseParamInfo dataParamInfo){
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("triggerList" ,dataParamInfo);
	}

	@Override
	public List getTriggerMetadata(DatabaseParamInfo dataParamInfo, String... triggerArr) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("triggerMetadata" ,dataParamInfo);
	}

	private List<TableInfo> tableAndColumnsInfo (DatabaseParamInfo dataParamInfo, String queryId, String... tableNmArr){

		if(tableNmArr!=null  && tableNmArr.length > 0){
			StringBuilder sb =new StringBuilder();

			List<String> tableInfoList = new ArrayList<String>();

			boolean  addFlag = false;
			for (int i = 0; i < tableNmArr.length; i++) {
				sb.append(addFlag ? ",":"" ).append("'").append(tableNmArr[i]).append("'");

				addFlag = true;
				if(i!=0 && (i+1)%1000==0){
					tableInfoList.add(sb.toString());
					sb =new StringBuilder();
					addFlag = false;
				}
			}

			if(sb.length() > 0){
				tableInfoList.add(sb.toString());
			}

			dataParamInfo.addCustom("objectNameList", tableInfoList);
		}

		SqlSession sqlSession = SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid());


		logger.debug("H2DBMeta tableAndColumnsInfo {} ",VartechUtils.reflectionToString(dataParamInfo));

		TableInfoHandler tableInfoHandler;

		if("viewMetadata".equals(queryId)){
			tableInfoHandler = new TableInfoHandler(dbInstanceFactory.getDataTypeImpl());
		}else{
			tableInfoHandler = new TableInfoHandler(dbInstanceFactory.getDataTypeImpl(), sqlSession.selectList("tableList" ,dataParamInfo));

			if(tableInfoHandler.getTableNameList() !=null  && tableInfoHandler.getTableNameList().size() > 0){
				dataParamInfo.addCustom("objectNameList", tableInfoHandler.getTableNameList());
			}
		}

		sqlSession.select(queryId ,dataParamInfo, tableInfoHandler);

		return tableInfoHandler.getTableInfoList();
	}

	@Override
	public List<ObjectInfo> getSequences(DatabaseParamInfo dataParamInfo) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("sequenceList" ,dataParamInfo);
	}

	@Override
	public List getSequenceMetadata(DatabaseParamInfo dataParamInfo, String... sequenceArr) throws Exception {
		return SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid()).selectList("sequenceMetadata" ,dataParamInfo);
	}
}
