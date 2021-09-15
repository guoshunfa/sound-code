package javax.sql;

import java.sql.SQLException;
import java.util.EventObject;

public class ConnectionEvent extends EventObject {
   private SQLException ex = null;
   static final long serialVersionUID = -4843217645290030002L;

   public ConnectionEvent(PooledConnection var1) {
      super(var1);
   }

   public ConnectionEvent(PooledConnection var1, SQLException var2) {
      super(var1);
      this.ex = var2;
   }

   public SQLException getSQLException() {
      return this.ex;
   }
}
