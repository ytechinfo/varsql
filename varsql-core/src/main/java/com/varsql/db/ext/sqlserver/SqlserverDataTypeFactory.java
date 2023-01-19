package com.varsql.db.ext.sqlserver;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Hex;

import com.varsql.core.db.datatype.AbstractDataTypeFactory;
import com.varsql.core.db.datatype.DBColumnMetaInfo;
import com.varsql.core.db.datatype.DataExceptionReturnType;
import com.varsql.core.db.datatype.DataType;
import com.varsql.core.db.datatype.DataTypeHandler;
import com.varsql.core.db.datatype.DefaultDataType;
import com.varsql.core.db.datatype.VenderDataType;
import com.varsql.core.db.datatype.handler.ResultSetHandler;

/**
 * 
 * @FileName  : SqlserverDataTypeFactory.java
 * @프로그램 설명 : sqlserver data type
 * @Date      : 2022. 3. 18. 
 * @작성자      : ytkim
 * @변경이력 :
 */
public class SqlserverDataTypeFactory extends AbstractDataTypeFactory{
	
	public SqlserverDataTypeFactory() {
		
		addDataType(new VenderDataType("TEXT", DefaultDataType.CLOB.getTypeCode(), DBColumnMetaInfo.TEXT));
		addDataType(new VenderDataType("NTEXT", DefaultDataType.CLOB.getTypeCode(), DBColumnMetaInfo.TEXT));
		addDataType(new VenderDataType("DATETIME2", DefaultDataType.DATETIME.getTypeCode(), DBColumnMetaInfo.DATE));
		addDataType(new VenderDataType("TIMESTAMP", DefaultDataType.VARBINARY.getTypeCode(), DBColumnMetaInfo.OTHER, DataTypeHandler.builder().resultSetHandler(new ResultSetHandler() {
			@Override
			public Object getValue(DataType dataType, ResultSet rs, int columnIndex, DataExceptionReturnType dert) throws SQLException {
				byte[] val = rs.getBytes(columnIndex);
				
				if(val == null) return null;
				
				try {
					return Hex.encodeHex(val);
				}catch(Exception e) {
					return rs.getObject(columnIndex).toString();
				}
			}
		}).build(), true));
		
	}
}