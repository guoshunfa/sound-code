package java.sql;

import java.util.Map;

public interface Struct {
   String getSQLTypeName() throws SQLException;

   Object[] getAttributes() throws SQLException;

   Object[] getAttributes(Map<String, Class<?>> var1) throws SQLException;
}
