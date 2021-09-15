package javax.management.remote.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {
   String getVersion() throws RemoteException;

   RMIConnection newClient(Object var1) throws IOException;
}
