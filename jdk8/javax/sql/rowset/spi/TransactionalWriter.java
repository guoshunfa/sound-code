package javax.sql.rowset.spi;

import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.RowSetWriter;

public interface TransactionalWriter extends RowSetWriter {
   void commit() throws SQLException;

   void rollback() throws SQLException;

   void rollback(Savepoint var1) throws SQLException;
}
