package javax.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EventObject;

public class StatementEvent extends EventObject {
   static final long serialVersionUID = -8089573731826608315L;
   private SQLException exception;
   private PreparedStatement statement;

   public StatementEvent(PooledConnection var1, PreparedStatement var2) {
      super(var1);
      this.statement = var2;
      this.exception = null;
   }

   public StatementEvent(PooledConnection var1, PreparedStatement var2, SQLException var3) {
      super(var1);
      this.statement = var2;
      this.exception = var3;
   }

   public PreparedStatement getStatement() {
      return this.statement;
   }

   public SQLException getSQLException() {
      return this.exception;
   }
}
