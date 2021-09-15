package javax.sql;

import java.sql.SQLException;
import javax.transaction.xa.XAResource;

public interface XAConnection extends PooledConnection {
   XAResource getXAResource() throws SQLException;
}
