package javax.sql.rowset;

import java.sql.SQLException;

public interface FilteredRowSet extends WebRowSet {
   void setFilter(Predicate var1) throws SQLException;

   Predicate getFilter();
}
