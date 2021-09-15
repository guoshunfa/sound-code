package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ActivationInstantiator extends Remote {
   MarshalledObject<? extends Remote> newInstance(ActivationID var1, ActivationDesc var2) throws ActivationException, RemoteException;
}
