package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PortableRemoteObjectDelegate {
   void exportObject(Remote var1) throws RemoteException;

   Remote toStub(Remote var1) throws NoSuchObjectException;

   void unexportObject(Remote var1) throws NoSuchObjectException;

   Object narrow(Object var1, Class var2) throws ClassCastException;

   void connect(Remote var1, Remote var2) throws RemoteException;
}
