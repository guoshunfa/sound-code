package javax.sql;

import java.sql.SQLException;

public interface ConnectionPoolDataSource extends CommonDataSource {
   PooledConnection getPooledConnection() throws SQLException;

   PooledConnection getPooledConnection(String var1, String var2) throws SQLException;
}
