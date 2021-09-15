package java.rmi.activation;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ActivationSystem extends Remote {
   int SYSTEM_PORT = 1098;

   ActivationID registerObject(ActivationDesc var1) throws ActivationException, UnknownGroupException, RemoteException;

   void unregisterObject(ActivationID var1) throws ActivationException, UnknownObjectException, RemoteException;

   ActivationGroupID registerGroup(ActivationGroupDesc var1) throws ActivationException, RemoteException;

   ActivationMonitor activeGroup(ActivationGroupID var1, ActivationInstantiator var2, long var3) throws UnknownGroupException, ActivationException, RemoteException;

   void unregisterGroup(ActivationGroupID var1) throws ActivationException, UnknownGroupException, RemoteException;

   void shutdown() throws RemoteException;

   ActivationDesc setActivationDesc(ActivationID var1, ActivationDesc var2) throws ActivationException, UnknownObjectException, UnknownGroupException, RemoteException;

   ActivationGroupDesc setActivationGroupDesc(ActivationGroupID var1, ActivationGroupDesc var2) throws ActivationException, UnknownGroupException, RemoteException;

   ActivationDesc getActivationDesc(ActivationID var1) throws ActivationException, UnknownObjectException, RemoteException;

   ActivationGroupDesc getActivationGroupDesc(ActivationGroupID var1) throws ActivationException, UnknownGroupException, RemoteException;
}
