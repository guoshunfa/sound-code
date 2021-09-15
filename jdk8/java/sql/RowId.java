package java.sql;

public interface RowId {
   boolean equals(Object var1);

   byte[] getBytes();

   String toString();

   int hashCode();
}
