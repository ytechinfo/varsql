package com.varsql.core.sql.resultset.handler;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.varsql.core.sql.beans.GridColumnInfo;

/**
 *
 * @FileName  : ResultSetHandlerImpl.java
 * @프로그램 설명 : resi;t set handler
 * @Date      : 2015. 6. 18.
 * @작성자      : ytkim
 * @변경이력 :
 */
public abstract class ResultSetHandlerImpl implements ResultSetHandler{

	final private SimpleDateFormat timestampSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	final private SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd");
	final private SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm:ss.SSS");

	protected ResultSetHandlerImpl(){}

	@SuppressWarnings("unchecked")
	@Override
	public Map getDataValue(Map rowMap, String keyName, String columnName, ResultSet rs, int colIdx, String varsqlType ,String columnTypeName) throws SQLException {

		switch (varsqlType) {
			case "number":
				rowMap.put(keyName, getNumber(rs, colIdx));
				break;
			case "string":
				rowMap.put(keyName, getString(rs, colIdx));
				break;
			case "clob":
				rowMap.put(keyName, getClob(rs, colIdx));
				break;
			case "blob":
				rowMap.put(keyName, getBlob(rs, colIdx));
				break;
			case "timestamp":
				rowMap.put(keyName, getTimeStamp(rs, colIdx));
				break;
			case "date":
				rowMap.put(keyName, getDate(rs, colIdx));
				break;
			case "time":
				rowMap.put(keyName, getTime(rs, colIdx));
				break;
			case "sqlxml":
				rowMap.put(keyName, getSQLXML(rs, colIdx));
				break;
			case "binary":
				rowMap.put(keyName, "[Binary]"+columnTypeName);
				break;
			case "nclob":
				rowMap.put(keyName, getNCLOB(rs, colIdx));
				break;
			case "raw":
				rowMap.put(keyName, "[raw]"+ columnTypeName);
				break;
			default:
				rowMap.put(keyName, getObject(rs, colIdx));
				break;
		}

		return rowMap;
	}

	public Map getDataValue(ResultSet rs, Map rowMap, GridColumnInfo columnInfo) throws SQLException {

		String keyName = columnInfo.getKey();
		String columnTypeName = columnInfo.getDbType();
		int colIdx = columnInfo.getNo();

		switch (columnInfo.getType()) {
			case "number":
				rowMap.put(keyName, getNumber(rs, colIdx));
				break;
			case "string":
				rowMap.put(keyName, getString(rs, colIdx));
				break;
			case "clob":
				rowMap.put(keyName, getClob(rs, colIdx));
				break;
			case "blob":
				rowMap.put(keyName, getBlob(rs, colIdx));
				break;
			case "timestamp":
				rowMap.put(keyName, getTimeStamp(rs, colIdx));
				break;
			case "date":
				rowMap.put(keyName, getDate(rs, colIdx));
				break;
			case "time":
				rowMap.put(keyName, getTime(rs, colIdx));
				break;
			case "sqlxml":
				rowMap.put(keyName, getSQLXML(rs, colIdx));
				break;
			case "binary":
				rowMap.put(keyName, "[Binary]"+columnTypeName);
				break;
			case "nclob":
				rowMap.put(keyName, getNCLOB(rs, colIdx));
				break;
			case "raw":
				rowMap.put(keyName, "[raw]"+ columnTypeName);
				break;
			default:
				rowMap.put(keyName, getObject(rs, colIdx));
				break;
		}

		return rowMap;
	};


	@Override
	public Number getNumber(ResultSet rs, int columnIdx) throws SQLException {
		return (Number)rs.getObject(columnIdx);
	}

	@Override
	public Number getNumber(ResultSet rs, String columnName) throws SQLException {
		return (Number)rs.getObject(columnName);
	}

	@Override
	public String getNumberToString(ResultSet rs, int columnIdx)throws SQLException  {
		return rs.getObject(columnIdx)+"";
	}
	@Override
	public String getNumberToString(ResultSet rs, String columnName)throws SQLException  {
		return rs.getObject(columnName)+"";
	}

	@Override
	public String getClob(ResultSet rs, int columnIdx)throws SQLException  {
		return getClob(rs.getCharacterStream(columnIdx));
	}

	@Override
	public String getClob(ResultSet rs, String columnName)throws SQLException  {
		return getClob(rs.getCharacterStream(columnName));
	}
	private String getClob(Reader val)throws SQLException  {
		char[] buffer  = null;
		int byteRead=-1;

		if(isNull(val)) return null;

		StringBuffer output = new StringBuffer();
		try{
			buffer = new char[1024];
			while((byteRead=val.read(buffer,0,1024))!=-1){
				output.append(buffer,0,byteRead);
			}
			val.close();
			return output.toString();
		}catch(Exception e){
			return "Clob" +e.getMessage();
		}finally {
			if(val !=null) try{val.close();}catch(Exception e1){}
		}
	}

	@Override
	public String getNCLOB(ResultSet rs, int columnIdx)throws SQLException  {
		return getNCLOB( rs.getNClob(columnIdx));
	}

	@Override
	public String getNCLOB(ResultSet rs, String columnName)throws SQLException  {
		return getNCLOB( rs.getNClob(columnName));
	}

	private String getNCLOB(NClob val) throws SQLException  {
		if(isNull(val)) return null;
		return getClob(val.getCharacterStream());
	}

	@Override
	public String getBlob(ResultSet rs, int columnIdx)throws SQLException  {
		return getBlob(rs.getBlob(columnIdx));
	}
	@Override
	public String getBlob(ResultSet rs, String columnName)throws SQLException  {
		return getBlob(rs.getBlob(columnName));
	}

	private String getBlob(Blob val) {
		if(isNull(val)) return null;

		return "blob";
	}
	@Override
	public String getTimeStamp(ResultSet rs, int columnIdx)throws SQLException  {
		return getTimeStamp(rs.getTimestamp(columnIdx));
	}
	@Override
	public String getTimeStamp(ResultSet rs, String columnName)throws SQLException  {
		return getTimeStamp(rs.getTimestamp(columnName));
	}

	private String getTimeStamp(Timestamp val){
		if(isNull(val)) return null;
		return timestampSDF.format(val);
	}

	@Override
	public String getDate(ResultSet rs, int columnIdx) throws SQLException {
		return getDate(rs.getDate(columnIdx));
	}
	@Override
	public String getDate(ResultSet rs, String columnName) throws SQLException {
		return getDate(rs.getDate(columnName));
	}

	private String getDate(Date val) {
		if(isNull(val)) return null;
		return dateSDF.format(val);
	}

	@Override
	public String getTime(ResultSet rs, int columnIdx) throws SQLException {
		return getTime(rs.getTime(columnIdx));
	}

	@Override
	public String getTime(ResultSet rs, String columnName) throws SQLException {
		return getTime(rs.getTime(columnName));
	}

	private String getTime(Time val){
		if(isNull(val)) return null;
		return timeSDF.format(val);
	}

	@Override
	public String getString(ResultSet rs, int columnIdx) throws SQLException {
		return rs.getString(columnIdx);
	}

	@Override
	public String getString(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public String getObject(ResultSet rs, int columnIdx) throws SQLException {
		return getObject(rs.getObject(columnIdx));
	}
	@Override
	public String getObject(ResultSet rs, String columnName) throws SQLException {
		return getObject(rs.getObject(columnName));
	}

	private String getObject(Object val) {
		if(isNull(val)) return null;
		return val+"";
	}

	@Override
	public String getSQLXML(ResultSet rs, int columnIdx) throws SQLException {
		return getSQLXML(rs.getSQLXML(columnIdx));
	}

	@Override
	public String getSQLXML(ResultSet rs, String columnName) throws SQLException {
		return getSQLXML(rs.getSQLXML(columnName));
	}

	private String getSQLXML(SQLXML val) throws SQLException {
		if(isNull(val)) return null;

		return val.getString();
	}

	@Override
	public String getBinary(ResultSet rs, int columnIdx) throws SQLException {
		return getBinary(rs.getBinaryStream(columnIdx));
	}

	@Override
	public String getBinary(ResultSet rs, String columnName) throws SQLException {
		return getBinary(rs.getBinaryStream(columnName));
	}

	@Override
	public String getRAW(ResultSet rs, int columnIdx) throws SQLException {
		return getRawVal(rs.getObject(columnIdx));
	}

	@Override
	public String getRAW(ResultSet rs, String columnName) throws SQLException {
		return getRawVal(rs.getObject(columnName));
	}

	private String getRawVal(Object obj) {
		return "RAW";
	}

	private String getBinary(InputStream val) {
		if(isNull(val)) return null;
		return "BINARY";
	}


	private boolean isNull(Object obj){
		return obj ==null ? true:false;
	}
}
