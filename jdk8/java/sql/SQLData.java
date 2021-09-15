package java.sql;

public interface SQLData {
   String getSQLTypeName() throws SQLException;

   void readSQL(SQLInput var1, String var2) throws SQLException;

   void writeSQL(SQLOutput var1) throws SQLException;
}
