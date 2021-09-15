package javax.transaction;

import java.rmi.RemoteException;

public class TransactionRolledbackException extends RemoteException {
   public TransactionRolledbackException() {
   }

   public TransactionRolledbackException(String var1) {
      super(var1);
   }
}
