package javax.sql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface RowSetMetaData extends ResultSetMetaData {
   void setColumnCount(int var1) throws SQLException;

   void setAutoIncrement(int var1, boolean var2) throws SQLException;

   void setCaseSensitive(int var1, boolean var2) throws SQLException;

   void setSearchable(int var1, boolean var2) throws SQLException;

   void setCurrency(int var1, boolean var2) throws SQLException;

   void setNullable(int var1, int var2) throws SQLException;

   void setSigned(int var1, boolean var2) throws SQLException;

   void setColumnDisplaySize(int var1, int var2) throws SQLException;

   void setColumnLabel(int var1, String var2) throws SQLException;

   void setColumnName(int var1, String var2) throws SQLException;

   void setSchemaName(int var1, String var2) throws SQLException;

   void setPrecision(int var1, int var2) throws SQLException;

   void setScale(int var1, int var2) throws SQLException;

   void setTableName(int var1, String var2) throws SQLException;

   void setCatalogName(int var1, String var2) throws SQLException;

   void setColumnType(int var1, int var2) throws SQLException;

   void setColumnTypeName(int var1, String var2) throws SQLException;
}
