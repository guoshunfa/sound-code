package java.rmi.server;

import java.rmi.Remote;

/** @deprecated */
@Deprecated
public interface Skeleton {
   /** @deprecated */
   @Deprecated
   void dispatch(Remote var1, RemoteCall var2, int var3, long var4) throws Exception;

   /** @deprecated */
   @Deprecated
   Operation[] getOperations();
}
