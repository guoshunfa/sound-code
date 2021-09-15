package java.rmi.dgc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;

public interface DGC extends Remote {
   Lease dirty(ObjID[] var1, long var2, Lease var4) throws RemoteException;

   void clean(ObjID[] var1, long var2, VMID var4, boolean var5) throws RemoteException;
}
