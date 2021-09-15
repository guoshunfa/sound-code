package javax.sql.rowset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collection;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

public interface CachedRowSet extends RowSet, Joinable {
   /** @deprecated */
   @Deprecated
   boolean COMMIT_ON_ACCEPT_CHANGES = true;

   void populate(ResultSet var1) throws SQLException;

   void execute(Connection var1) throws SQLException;

   void acceptChanges() throws SyncProviderException;

   void acceptChanges(Connection var1) throws SyncProviderException;

   void restoreOriginal() throws SQLException;

   void release() throws SQLException;

   void undoDelete() throws SQLException;

   void undoInsert() throws SQLException;

   void undoUpdate() throws SQLException;

   boolean columnUpdated(int var1) throws SQLException;

   boolean columnUpdated(String var1) throws SQLException;

   Collection<?> toCollection() throws SQLException;

   Collection<?> toCollection(int var1) throws SQLException;

   Collection<?> toCollection(String var1) throws SQLException;

   SyncProvider getSyncProvider() throws SQLException;

   void setSyncProvider(String var1) throws SQLException;

   int size();

   void setMetaData(RowSetMetaData var1) throws SQLException;

   ResultSet getOriginal() throws SQLException;

   ResultSet getOriginalRow() throws SQLException;

   void setOriginalRow() throws SQLException;

   String getTableName() throws SQLException;

   void setTableName(String var1) throws SQLException;

   int[] getKeyColumns() throws SQLException;

   void setKeyColumns(int[] var1) throws SQLException;

   RowSet createShared() throws SQLException;

   CachedRowSet createCopy() throws SQLException;

   CachedRowSet createCopySchema() throws SQLException;

   CachedRowSet createCopyNoConstraints() throws SQLException;

   RowSetWarning getRowSetWarnings() throws SQLException;

   boolean getShowDeleted() throws SQLException;

   void setShowDeleted(boolean var1) throws SQLException;

   void commit() throws SQLException;

   void rollback() throws SQLException;

   void rollback(Savepoint var1) throws SQLException;

   void rowSetPopulated(RowSetEvent var1, int var2) throws SQLException;

   void populate(ResultSet var1, int var2) throws SQLException;

   void setPageSize(int var1) throws SQLException;

   int getPageSize();

   boolean nextPage() throws SQLException;

   boolean previousPage() throws SQLException;
}
