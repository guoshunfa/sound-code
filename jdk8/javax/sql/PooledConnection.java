package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface PooledConnection {
   Connection getConnection() throws SQLException;

   void close() throws SQLException;

   void addConnectionEventListener(ConnectionEventListener var1);

   void removeConnectionEventListener(ConnectionEventListener var1);

   void addStatementEventListener(StatementEventListener var1);

   void removeStatementEventListener(StatementEventListener var1);
}
