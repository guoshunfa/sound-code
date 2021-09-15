package javax.transaction;

import java.rmi.RemoteException;

public class InvalidTransactionException extends RemoteException {
   public InvalidTransactionException() {
   }

   public InvalidTransactionException(String var1) {
      super(var1);
   }
}
