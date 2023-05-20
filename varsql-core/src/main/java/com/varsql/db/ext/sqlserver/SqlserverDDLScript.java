package com.varsql.db.ext.sqlserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.varsql.core.db.DBVenderType;
import com.varsql.core.db.MetaControlBean;
import com.varsql.core.db.datatype.DataType;
import com.varsql.core.db.ddl.script.AbstractDDLScript;
import com.varsql.core.db.mybatis.SQLManager;
import com.varsql.core.db.valueobject.DatabaseParamInfo;
import com.varsql.core.db.valueobject.ddl.DDLCreateOption;
import com.varsql.core.db.valueobject.ddl.DDLInfo;
import com.varsql.core.db.valueobject.ddl.DDLTemplateParam;
import com.varsql.core.sql.SQLTemplateCode;
import com.varsql.core.sql.format.VarsqlFormatterUtil;
import com.varsql.core.sql.template.SQLTemplateFactory;

/**
 *
 * @FileName  : SqlserverDDLScript.java
 * @프로그램 설명 : sqlserver ddl
 * @Date      : 2019. 1. 20.
 * @작성자      : ytkim
 * @변경이력 :
 */
public class SqlserverDDLScript extends AbstractDDLScript {
	private final Logger logger = LoggerFactory.getLogger(SqlserverDDLScript.class);

	public SqlserverDDLScript(MetaControlBean dbInstanceFactory){
		super(dbInstanceFactory, DBVenderType.SQLSERVER);
		
		addDefaultValueToVenderValue("CURRENT_TIMESTAMP", "getdate()");
	}

	@Override
	public List<DDLInfo> getTables(DatabaseParamInfo dataParamInfo, DDLCreateOption ddlOption, String ...objNmArr) throws Exception {

		SqlSession client = SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid());

		List<DDLInfo> reval = new ArrayList<DDLInfo>();
		DDLInfo ddlInfo;

		for(String objNm : objNmArr){

			ddlInfo = new DDLInfo();
			ddlInfo.setName(objNm);
			dataParamInfo.setObjectName(objNm);
			
			DDLTemplateParam param = DDLTemplateParam.builder()
				.dbType(dataParamInfo.getDbType())
				.schema(dataParamInfo.getSchema())
				.objectName(objNm)
				.ddlOpt(ddlOption)
				.columnList(client.selectList("tableScriptColumn", dataParamInfo))
				.keyList(client.selectList("tableConstraints", dataParamInfo))
				.commentsList(client.selectList("tableColumnComments",dataParamInfo))
			.build();
			
			param.setSourceText(SQLTemplateFactory.getInstance().sqlRender(DBVenderType.SQLSERVER, SQLTemplateCode.TABLE.constraintKey, param));
			
			ddlInfo.setCreateScript(SQLTemplateFactory.getInstance().sqlRender(this.dbType, SQLTemplateCode.TABLE.create, param));
			reval.add(ddlInfo);
		}

		return reval;
	}

	@Override
	public List<DDLInfo> getViews(DatabaseParamInfo dataParamInfo, DDLCreateOption ddlOption, String ...objNmArr) throws Exception {
		SqlSession sqlSesseion = SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid());
		List<DDLInfo> reval = new ArrayList<DDLInfo>();
		DDLInfo ddlInfo;

		for (String objNm : objNmArr) {

			ddlInfo = new DDLInfo();
			ddlInfo.setName(objNm);

			dataParamInfo.setObjectName(objNm);

			StringBuilder sourceSb = new StringBuilder();
			List<String> scrList = sqlSesseion.selectList("objectScriptSource", dataParamInfo);
			for (int i = 0; i < scrList.size(); i++) {
				sourceSb.append(scrList.get(i));
			}

			DDLTemplateParam param = DDLTemplateParam.builder()
					.dbType(dataParamInfo.getDbType())
					.schema(dataParamInfo.getSchema())
					.objectName(objNm)
					.ddlOpt(ddlOption)
					.sourceText(StringUtils.trim(sourceSb.toString()))
				.build();

			ddlInfo.setCreateScript(VarsqlFormatterUtil.ddlFormat(SQLTemplateFactory.getInstance().sqlRender(this.dbType, SQLTemplateCode.VIEW.create, param), dbType));
			reval.add(ddlInfo);
		}

		return reval;
	}

	@Override
	public List<DDLInfo> getIndexs(DatabaseParamInfo dataParamInfo, DDLCreateOption ddlOption, String ...objNmArr)	throws Exception {

		SqlSession sqlSesseion = SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid());

		List<DDLInfo> reval = new ArrayList<DDLInfo>();
		DDLInfo ddlInfo;
		for (String objNm : objNmArr) {

			ddlInfo = new DDLInfo();
			ddlInfo.setName(objNm);
			dataParamInfo.setObjectName(objNm);

			DDLTemplateParam param = DDLTemplateParam.builder()
				.dbType(dataParamInfo.getDbType())
				.schema(dataParamInfo.getSchema())
				.objectName(objNm)
				.ddlOpt(ddlOption)
				.items(sqlSesseion.selectList("indexScriptSource", dataParamInfo))
			.build();

			ddlInfo.setCreateScript(VarsqlFormatterUtil.ddlFormat(SQLTemplateFactory.getInstance().sqlRender(this.dbType, SQLTemplateCode.INDEX.create, param), dbType));

			reval.add(ddlInfo);
		}

		return reval;
	}

	@Override
	public List<DDLInfo> getFunctions(DatabaseParamInfo dataParamInfo, DDLCreateOption ddlOption, String ...objNmArr) throws Exception {
		logger.debug(" Function DDL Generation...");

		SqlSession sqlSesseion = SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid());

		List<DDLInfo> reval = new ArrayList<DDLInfo>();
		DDLInfo ddlInfo;
		
		for (String objNm : objNmArr) {

			ddlInfo = new DDLInfo();
			ddlInfo.setName(objNm);

			dataParamInfo.setObjectName(objNm);
			
			StringBuilder sourceSb = new StringBuilder();
			List<String> srcList = sqlSesseion.selectList("objectScriptSource", dataParamInfo);
			for (int j = 0; j < srcList.size(); j++) {
				sourceSb.append(srcList.get(j));
			}

			DDLTemplateParam param = DDLTemplateParam.builder()
				.dbType(dataParamInfo.getDbType())
				.schema(dataParamInfo.getSchema())
				.objectName(objNm)
				.ddlOpt(ddlOption)
				.sourceText(StringUtils.trim(sourceSb.toString()))
			.build();

			ddlInfo.setCreateScript(SQLTemplateFactory.getInstance().sqlRender(this.dbType, SQLTemplateCode.FUNCTION.create, param));
			reval.add(ddlInfo);
		}

		return reval;
	}

	@Override
	public List<DDLInfo> getProcedures(DatabaseParamInfo dataParamInfo, DDLCreateOption ddlOption, String ...objNmArr)	throws Exception {

		SqlSession sqlSesseion = SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid());

		logger.debug(" Procedure DDL Generation...");

		List<DDLInfo> reval = new ArrayList<DDLInfo>();
		DDLInfo ddlInfo;
		for (String objNm : objNmArr) {

			ddlInfo = new DDLInfo();
			ddlInfo.setName(objNm);
			dataParamInfo.setObjectName(objNm);
			
			StringBuilder sourceSb = new StringBuilder();
			List<String> srcList = sqlSesseion.selectList("objectScriptSource", dataParamInfo);
			for (int j = 0; j < srcList.size(); j++) {
				sourceSb.append(srcList.get(j));
			}

			DDLTemplateParam param = DDLTemplateParam.builder()
					.dbType(dataParamInfo.getDbType())
					.schema(dataParamInfo.getSchema())
					.objectName(objNm)
					.ddlOpt(ddlOption)
					.sourceText(StringUtils.trim(sourceSb.toString()))
				.build();

			ddlInfo.setCreateScript(SQLTemplateFactory.getInstance().sqlRender(this.dbType, SQLTemplateCode.PROCEDURE.create, param));
			
			
			reval.add(ddlInfo);
		}

		return reval;
	}

	/**
	 *
	 * @Method Name  : getTrigger
	 * @Method 설명 : trigger ddl
	 * @Method override : @see com.varsql.core.db.ddl.script.DDLScriptImpl#getTrigger(com.varsql.core.db.valueobject.DatabaseParamInfo, java.lang.String[])
	 * @작성자   : ytkim
	 * @작성일   : 2018. 9. 19.
	 * @변경이력  :
	 * @param dataParamInfo
	 * @param objNmArr
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<DDLInfo> getTriggers(DatabaseParamInfo dataParamInfo, DDLCreateOption ddlOption, String ...objNmArr) throws Exception {
		logger.debug("Trigger DDL Generation...");

		SqlSession sqlSesseion = SQLManager.getInstance().sqlSessionTemplate(dataParamInfo.getVconnid());

		List<DDLInfo> reval = new ArrayList<DDLInfo>();
		DDLInfo ddlInfo;
		for (String objNm : objNmArr) {

			ddlInfo = new DDLInfo();
			ddlInfo.setName(objNm);
			dataParamInfo.setObjectName(objNm);

			StringBuilder sourceSb = new StringBuilder();
			List<String> srcList = sqlSesseion.selectList("objectScriptSource", dataParamInfo);
			for (int j = 0; j < srcList.size(); j++) {
				sourceSb.append(srcList.get(j));
			}

			DDLTemplateParam param = DDLTemplateParam.builder()
				.dbType(dataParamInfo.getDbType())
				.schema(dataParamInfo.getSchema())
				.objectName(objNm)
				.ddlOpt(ddlOption)
				.sourceText(StringUtils.trim(sourceSb.toString()))
			.build();

			ddlInfo.setCreateScript(SQLTemplateFactory.getInstance().sqlRender(this.dbType, SQLTemplateCode.TRIGGER.create, param));
			reval.add(ddlInfo);
		}

		return reval;
	}
	
	@Override
	public String getStandardDefaultValue(String val, DataType type) {
		if(val.startsWith("((")) {
			val = val.replaceFirst("\\(\\(", "");
			val = val.substring(0,val.length()-2);
		}else if(val.startsWith("(")) {
			
			val = val.replaceFirst("\\(", "");
			val = val.substring(0,val.length()-1);
			
			String strLower = val.toLowerCase();
			if(strLower.startsWith("convert")) {
				int firstCommaIdx = val.indexOf(",");
				val = val.substring(firstCommaIdx+1, val.length()-1);
				int secondCommaIdx = val.indexOf(",");
				if(secondCommaIdx > -1) {
					val = val.substring(0, secondCommaIdx);
				}
			}
		}
		
		return super.getStandardDefaultValue(val, type);
	}
}