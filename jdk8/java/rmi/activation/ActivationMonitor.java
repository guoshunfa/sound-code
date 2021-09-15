package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ActivationMonitor extends Remote {
   void inactiveObject(ActivationID var1) throws UnknownObjectException, RemoteException;

   void activeObject(ActivationID var1, MarshalledObject<? extends Remote> var2) throws UnknownObjectException, RemoteException;

   void inactiveGroup(ActivationGroupID var1, long var2) throws UnknownGroupException, RemoteException;
}
