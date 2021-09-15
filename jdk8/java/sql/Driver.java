package java.sql;

import java.util.Properties;
import java.util.logging.Logger;

public interface Driver {
   Connection connect(String var1, Properties var2) throws SQLException;

   boolean acceptsURL(String var1) throws SQLException;

   DriverPropertyInfo[] getPropertyInfo(String var1, Properties var2) throws SQLException;

   int getMajorVersion();

   int getMinorVersion();

   boolean jdbcCompliant();

   Logger getParentLogger() throws SQLFeatureNotSupportedException;
}
