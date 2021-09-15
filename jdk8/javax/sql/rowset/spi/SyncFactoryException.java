package javax.sql.rowset.spi;

import java.sql.SQLException;

public class SyncFactoryException extends SQLException {
   static final long serialVersionUID = -4354595476433200352L;

   public SyncFactoryException() {
   }

   public SyncFactoryException(String var1) {
      super(var1);
   }
}
