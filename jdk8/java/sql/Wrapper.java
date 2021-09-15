package java.sql;

public interface Wrapper {
   <T> T unwrap(Class<T> var1) throws SQLException;

   boolean isWrapperFor(Class<?> var1) throws SQLException;
}
