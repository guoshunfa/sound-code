package javax.sql;

import java.sql.SQLException;

public interface RowSetReader {
   void readData(RowSetInternal var1) throws SQLException;
}
