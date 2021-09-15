package java.sql;

public interface ResultSetMetaData extends Wrapper {
   int columnNoNulls = 0;
   int columnNullable = 1;
   int columnNullableUnknown = 2;

   int getColumnCount() throws SQLException;

   boolean isAutoIncrement(int var1) throws SQLException;

   boolean isCaseSensitive(int var1) throws SQLException;

   boolean isSearchable(int var1) throws SQLException;

   boolean isCurrency(int var1) throws SQLException;

   int isNullable(int var1) throws SQLException;

   boolean isSigned(int var1) throws SQLException;

   int getColumnDisplaySize(int var1) throws SQLException;

   String getColumnLabel(int var1) throws SQLException;

   String getColumnName(int var1) throws SQLException;

   String getSchemaName(int var1) throws SQLException;

   int getPrecision(int var1) throws SQLException;

   int getScale(int var1) throws SQLException;

   String getTableName(int var1) throws SQLException;

   String getCatalogName(int var1) throws SQLException;

   int getColumnType(int var1) throws SQLException;

   String getColumnTypeName(int var1) throws SQLException;

   boolean isReadOnly(int var1) throws SQLException;

   boolean isWritable(int var1) throws SQLException;

   boolean isDefinitelyWritable(int var1) throws SQLException;

   String getColumnClassName(int var1) throws SQLException;
}
