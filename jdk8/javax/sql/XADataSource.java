package javax.sql;

import java.sql.SQLException;

public interface XADataSource extends CommonDataSource {
   XAConnection getXAConnection() throws SQLException;

   XAConnection getXAConnection(String var1, String var2) throws SQLException;
}
