package javax.sql.rowset;

import java.sql.SQLException;
import java.util.Collection;
import javax.sql.RowSet;

public interface JoinRowSet extends WebRowSet {
   int CROSS_JOIN = 0;
   int INNER_JOIN = 1;
   int LEFT_OUTER_JOIN = 2;
   int RIGHT_OUTER_JOIN = 3;
   int FULL_JOIN = 4;

   void addRowSet(Joinable var1) throws SQLException;

   void addRowSet(RowSet var1, int var2) throws SQLException;

   void addRowSet(RowSet var1, String var2) throws SQLException;

   void addRowSet(RowSet[] var1, int[] var2) throws SQLException;

   void addRowSet(RowSet[] var1, String[] var2) throws SQLException;

   Collection<?> getRowSets() throws SQLException;

   String[] getRowSetNames() throws SQLException;

   CachedRowSet toCachedRowSet() throws SQLException;

   boolean supportsCrossJoin();

   boolean supportsInnerJoin();

   boolean supportsLeftOuterJoin();

   boolean supportsRightOuterJoin();

   boolean supportsFullJoin();

   void setJoinType(int var1) throws SQLException;

   String getWhereClause() throws SQLException;

   int getJoinType() throws SQLException;
}
