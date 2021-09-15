package javax.sql.rowset;

import java.sql.SQLException;

public interface Joinable {
   void setMatchColumn(int var1) throws SQLException;

   void setMatchColumn(int[] var1) throws SQLException;

   void setMatchColumn(String var1) throws SQLException;

   void setMatchColumn(String[] var1) throws SQLException;

   int[] getMatchColumnIndexes() throws SQLException;

   String[] getMatchColumnNames() throws SQLException;

   void unsetMatchColumn(int var1) throws SQLException;

   void unsetMatchColumn(int[] var1) throws SQLException;

   void unsetMatchColumn(String var1) throws SQLException;

   void unsetMatchColumn(String[] var1) throws SQLException;
}
