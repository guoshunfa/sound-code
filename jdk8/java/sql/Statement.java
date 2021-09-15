package java.sql;

public interface Statement extends Wrapper, AutoCloseable {
   int CLOSE_CURRENT_RESULT = 1;
   int KEEP_CURRENT_RESULT = 2;
   int CLOSE_ALL_RESULTS = 3;
   int SUCCESS_NO_INFO = -2;
   int EXECUTE_FAILED = -3;
   int RETURN_GENERATED_KEYS = 1;
   int NO_GENERATED_KEYS = 2;

   ResultSet executeQuery(String var1) throws SQLException;

   int executeUpdate(String var1) throws SQLException;

   void close() throws SQLException;

   int getMaxFieldSize() throws SQLException;

   void setMaxFieldSize(int var1) throws SQLException;

   int getMaxRows() throws SQLException;

   void setMaxRows(int var1) throws SQLException;

   void setEscapeProcessing(boolean var1) throws SQLException;

   int getQueryTimeout() throws SQLException;

   void setQueryTimeout(int var1) throws SQLException;

   void cancel() throws SQLException;

   SQLWarning getWarnings() throws SQLException;

   void clearWarnings() throws SQLException;

   void setCursorName(String var1) throws SQLException;

   boolean execute(String var1) throws SQLException;

   ResultSet getResultSet() throws SQLException;

   int getUpdateCount() throws SQLException;

   boolean getMoreResults() throws SQLException;

   void setFetchDirection(int var1) throws SQLException;

   int getFetchDirection() throws SQLException;

   void setFetchSize(int var1) throws SQLException;

   int getFetchSize() throws SQLException;

   int getResultSetConcurrency() throws SQLException;

   int getResultSetType() throws SQLException;

   void addBatch(String var1) throws SQLException;

   void clearBatch() throws SQLException;

   int[] executeBatch() throws SQLException;

   Connection getConnection() throws SQLException;

   boolean getMoreResults(int var1) throws SQLException;

   ResultSet getGeneratedKeys() throws SQLException;

   int executeUpdate(String var1, int var2) throws SQLException;

   int executeUpdate(String var1, int[] var2) throws SQLException;

   int executeUpdate(String var1, String[] var2) throws SQLException;

   boolean execute(String var1, int var2) throws SQLException;

   boolean execute(String var1, int[] var2) throws SQLException;

   boolean execute(String var1, String[] var2) throws SQLException;

   int getResultSetHoldability() throws SQLException;

   boolean isClosed() throws SQLException;

   void setPoolable(boolean var1) throws SQLException;

   boolean isPoolable() throws SQLException;

   void closeOnCompletion() throws SQLException;

   boolean isCloseOnCompletion() throws SQLException;

   default long getLargeUpdateCount() throws SQLException {
      throw new UnsupportedOperationException("getLargeUpdateCount not implemented");
   }

   default void setLargeMaxRows(long var1) throws SQLException {
      throw new UnsupportedOperationException("setLargeMaxRows not implemented");
   }

   default long getLargeMaxRows() throws SQLException {
      return 0L;
   }

   default long[] executeLargeBatch() throws SQLException {
      throw new UnsupportedOperationException("executeLargeBatch not implemented");
   }

   default long executeLargeUpdate(String var1) throws SQLException {
      throw new UnsupportedOperationException("executeLargeUpdate not implemented");
   }

   default long executeLargeUpdate(String var1, int var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented");
   }

   default long executeLargeUpdate(String var1, int[] var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented");
   }

   default long executeLargeUpdate(String var1, String[] var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented");
   }
}
