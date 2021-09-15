package java.rmi.server;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRef extends Externalizable {
   long serialVersionUID = 3632638527362204081L;
   String packagePrefix = "sun.rmi.server";

   Object invoke(Remote var1, Method var2, Object[] var3, long var4) throws Exception;

   /** @deprecated */
   @Deprecated
   RemoteCall newCall(RemoteObject var1, Operation[] var2, int var3, long var4) throws RemoteException;

   /** @deprecated */
   @Deprecated
   void invoke(RemoteCall var1) throws Exception;

   /** @deprecated */
   @Deprecated
   void done(RemoteCall var1) throws RemoteException;

   String getRefClass(ObjectOutput var1);

   int remoteHashCode();

   boolean remoteEquals(RemoteRef var1);

   String remoteToString();
}
