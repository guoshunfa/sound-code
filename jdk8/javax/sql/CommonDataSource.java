package javax.sql;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public interface CommonDataSource {
   PrintWriter getLogWriter() throws SQLException;

   void setLogWriter(PrintWriter var1) throws SQLException;

   void setLoginTimeout(int var1) throws SQLException;

   int getLoginTimeout() throws SQLException;

   Logger getParentLogger() throws SQLFeatureNotSupportedException;
}
