package java.rmi.server;

import java.rmi.RemoteException;

/** @deprecated */
@Deprecated
public class SkeletonNotFoundException extends RemoteException {
   private static final long serialVersionUID = -7860299673822761231L;

   public SkeletonNotFoundException(String var1) {
      super(var1);
   }

   public SkeletonNotFoundException(String var1, Exception var2) {
      super(var1, var2);
   }
}
