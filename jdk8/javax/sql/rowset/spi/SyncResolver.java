package javax.sql.rowset.spi;

import java.sql.SQLException;
import javax.sql.RowSet;

public interface SyncResolver extends RowSet {
   int UPDATE_ROW_CONFLICT = 0;
   int DELETE_ROW_CONFLICT = 1;
   int INSERT_ROW_CONFLICT = 2;
   int NO_ROW_CONFLICT = 3;

   int getStatus();

   Object getConflictValue(int var1) throws SQLException;

   Object getConflictValue(String var1) throws SQLException;

   void setResolvedValue(int var1, Object var2) throws SQLException;

   void setResolvedValue(String var1, Object var2) throws SQLException;

   boolean nextConflict() throws SQLException;

   boolean previousConflict() throws SQLException;
}
