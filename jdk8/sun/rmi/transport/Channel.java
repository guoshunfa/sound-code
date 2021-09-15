package sun.rmi.transport;

import java.rmi.RemoteException;

public interface Channel {
   Connection newConnection() throws RemoteException;

   Endpoint getEndpoint();

   void free(Connection var1, boolean var2) throws RemoteException;
}
