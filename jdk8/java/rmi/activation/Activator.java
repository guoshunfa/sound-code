package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Activator extends Remote {
   MarshalledObject<? extends Remote> activate(ActivationID var1, boolean var2) throws ActivationException, UnknownObjectException, RemoteException;
}
