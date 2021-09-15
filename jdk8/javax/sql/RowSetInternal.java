package javax.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowSetInternal {
   Object[] getParams() throws SQLException;

   Connection getConnection() throws SQLException;

   void setMetaData(RowSetMetaData var1) throws SQLException;

   ResultSet getOriginal() throws SQLException;

   ResultSet getOriginalRow() throws SQLException;
}
