package javax.sql;

import java.sql.SQLException;

public interface RowSetWriter {
   boolean writeData(RowSetInternal var1) throws SQLException;
}
