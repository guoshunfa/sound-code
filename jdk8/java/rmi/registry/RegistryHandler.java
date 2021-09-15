package java.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

/** @deprecated */
@Deprecated
public interface RegistryHandler {
   /** @deprecated */
   @Deprecated
   Registry registryStub(String var1, int var2) throws RemoteException, UnknownHostException;

   /** @deprecated */
   @Deprecated
   Registry registryImpl(int var1) throws RemoteException;
}
