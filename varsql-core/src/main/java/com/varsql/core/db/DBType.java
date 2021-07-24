package com.varsql.core.db;

/**
 *
 * @FileName  : DBType.java
 * @프로그램 설명 : db type
 * @Date      : 2019. 11. 26.
 * @작성자      : ytkim
 * @변경이력 :
 */
public enum DBType {
	MYSQL("mysql")
	,DB2("db2")
	,ORACLE("oracle")
	,MSSQL("mssql")
	,MARIADB("mariadb")
	,DERBY("derby")
	,HIVE("hive")
	,HSQLDB("hsqldb")
	,POSTGRESQL("postgresql")
	,INGRES("ingres")
	,H2("h2")
	,TIBERO("tibero")
	,CUBRID("cubrid")
	,SYBASE("sybase")
	,OTHER("other");

	private String dbVenderName;
	private com.alibaba.druid.DbType dbParserType;

	private DBType(String db){
		this.dbVenderName =db;
		this.dbParserType = com.alibaba.druid.DbType.of(db);
		
		if(this.dbParserType ==  null) {
			this.dbParserType = com.alibaba.druid.DbType.other;
		}
	}

	public String getDbVenderName() {
		return dbVenderName;
	}

	public com.alibaba.druid.DbType getDbParser() {
		return dbParserType;
	}

	public static com.alibaba.druid.DbType getDbParser(String db) {
		if(db != null) {
			db = db.toUpperCase();
			for (DBType dbType : values()) {
				if(db.equalsIgnoreCase(dbType.name())) {
					return dbType.dbParserType;
				}
			}
		}
		return DBType.OTHER.dbParserType;
	}

	public static DBType getDBType(String db) {
		if(db != null) {
			db = db.toUpperCase();
			for (DBType dbType : values()) {
				if(db.equalsIgnoreCase(dbType.name())) {
					return dbType;
				}
			}
		}
		return DBType.OTHER;
	}
	
	public boolean equalsName(String type) {
		type = type.toUpperCase();
		for (DBType dbType : values()) {
			if(dbType.name().equals(type)) {
				return true;
			}
		}

		return false;
	}
}
