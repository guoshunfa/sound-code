package java.sql;

public interface ParameterMetaData extends Wrapper {
   int parameterNoNulls = 0;
   int parameterNullable = 1;
   int parameterNullableUnknown = 2;
   int parameterModeUnknown = 0;
   int parameterModeIn = 1;
   int parameterModeInOut = 2;
   int parameterModeOut = 4;

   int getParameterCount() throws SQLException;

   int isNullable(int var1) throws SQLException;

   boolean isSigned(int var1) throws SQLException;

   int getPrecision(int var1) throws SQLException;

   int getScale(int var1) throws SQLException;

   int getParameterType(int var1) throws SQLException;

   String getParameterTypeName(int var1) throws SQLException;

   String getParameterClassName(int var1) throws SQLException;

   int getParameterMode(int var1) throws SQLException;
}
