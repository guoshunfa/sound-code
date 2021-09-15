package java.sql;

import java.util.Map;

public interface Ref {
   String getBaseTypeName() throws SQLException;

   Object getObject(Map<String, Class<?>> var1) throws SQLException;

   Object getObject() throws SQLException;

   void setObject(Object var1) throws SQLException;
}
