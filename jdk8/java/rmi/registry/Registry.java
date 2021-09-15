package java.rmi.registry;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Registry extends Remote {
   int REGISTRY_PORT = 1099;

   Remote lookup(String var1) throws RemoteException, NotBoundException, AccessException;

   void bind(String var1, Remote var2) throws RemoteException, AlreadyBoundException, AccessException;

   void unbind(String var1) throws RemoteException, NotBoundException, AccessException;

   void rebind(String var1, Remote var2) throws RemoteException, AccessException;

   String[] list() throws RemoteException, AccessException;
}
