package java.sql;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public interface Connection extends Wrapper, AutoCloseable {
   int TRANSACTION_NONE = 0;
   int TRANSACTION_READ_UNCOMMITTED = 1;
   int TRANSACTION_READ_COMMITTED = 2;
   int TRANSACTION_REPEATABLE_READ = 4;
   int TRANSACTION_SERIALIZABLE = 8;

   Statement createStatement() throws SQLException;

   PreparedStatement prepareStatement(String var1) throws SQLException;

   CallableStatement prepareCall(String var1) throws SQLException;

   String nativeSQL(String var1) throws SQLException;

   void setAutoCommit(boolean var1) throws SQLException;

   boolean getAutoCommit() throws SQLException;

   void commit() throws SQLException;

   void rollback() throws SQLException;

   void close() throws SQLException;

   boolean isClosed() throws SQLException;

   DatabaseMetaData getMetaData() throws SQLException;

   void setReadOnly(boolean var1) throws SQLException;

   boolean isReadOnly() throws SQLException;

   void setCatalog(String var1) throws SQLException;

   String getCatalog() throws SQLException;

   void setTransactionIsolation(int var1) throws SQLException;

   int getTransactionIsolation() throws SQLException;

   SQLWarning getWarnings() throws SQLException;

   void clearWarnings() throws SQLException;

   Statement createStatement(int var1, int var2) throws SQLException;

   PreparedStatement prepareStatement(String var1, int var2, int var3) throws SQLException;

   CallableStatement prepareCall(String var1, int var2, int var3) throws SQLException;

   Map<String, Class<?>> getTypeMap() throws SQLException;

   void setTypeMap(Map<String, Class<?>> var1) throws SQLException;

   void setHoldability(int var1) throws SQLException;

   int getHoldability() throws SQLException;

   Savepoint setSavepoint() throws SQLException;

   Savepoint setSavepoint(String var1) throws SQLException;

   void rollback(Savepoint var1) throws SQLException;

   void releaseSavepoint(Savepoint var1) throws SQLException;

   Statement createStatement(int var1, int var2, int var3) throws SQLException;

   PreparedStatement prepareStatement(String var1, int var2, int var3, int var4) throws SQLException;

   CallableStatement prepareCall(String var1, int var2, int var3, int var4) throws SQLException;

   PreparedStatement prepareStatement(String var1, int var2) throws SQLException;

   PreparedStatement prepareStatement(String var1, int[] var2) throws SQLException;

   PreparedStatement prepareStatement(String var1, String[] var2) throws SQLException;

   Clob createClob() throws SQLException;

   Blob createBlob() throws SQLException;

   NClob createNClob() throws SQLException;

   SQLXML createSQLXML() throws SQLException;

   boolean isValid(int var1) throws SQLException;

   void setClientInfo(String var1, String var2) throws SQLClientInfoException;

   void setClientInfo(Properties var1) throws SQLClientInfoException;

   String getClientInfo(String var1) throws SQLException;

   Properties getClientInfo() throws SQLException;

   Array createArrayOf(String var1, Object[] var2) throws SQLException;

   Struct createStruct(String var1, Object[] var2) throws SQLException;

   void setSchema(String var1) throws SQLException;

   String getSchema() throws SQLException;

   void abort(Executor var1) throws SQLException;

   void setNetworkTimeout(Executor var1, int var2) throws SQLException;

   int getNetworkTimeout() throws SQLException;
}
