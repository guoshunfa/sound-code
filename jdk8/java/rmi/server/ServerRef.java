package java.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** @deprecated */
@Deprecated
public interface ServerRef extends RemoteRef {
   long serialVersionUID = -4557750989390278438L;

   RemoteStub exportObject(Remote var1, Object var2) throws RemoteException;

   String getClientHost() throws ServerNotActiveException;
}
