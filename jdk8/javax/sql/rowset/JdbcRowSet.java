package javax.sql.rowset;

import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.RowSet;

public interface JdbcRowSet extends RowSet, Joinable {
   boolean getShowDeleted() throws SQLException;

   void setShowDeleted(boolean var1) throws SQLException;

   RowSetWarning getRowSetWarnings() throws SQLException;

   void commit() throws SQLException;

   boolean getAutoCommit() throws SQLException;

   void setAutoCommit(boolean var1) throws SQLException;

   void rollback() throws SQLException;

   void rollback(Savepoint var1) throws SQLException;
}
