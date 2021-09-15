package javax.sql.rowset;

import java.sql.SQLException;
import javax.sql.RowSet;

public interface Predicate {
   boolean evaluate(RowSet var1);

   boolean evaluate(Object var1, int var2) throws SQLException;

   boolean evaluate(Object var1, String var2) throws SQLException;
}
