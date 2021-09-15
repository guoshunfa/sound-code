package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public interface UtilDelegate {
   RemoteException mapSystemException(SystemException var1);

   void writeAny(OutputStream var1, Object var2);

   Object readAny(InputStream var1);

   void writeRemoteObject(OutputStream var1, Object var2);

   void writeAbstractObject(OutputStream var1, Object var2);

   void registerTarget(Tie var1, Remote var2);

   void unexportObject(Remote var1) throws NoSuchObjectException;

   Tie getTie(Remote var1);

   ValueHandler createValueHandler();

   String getCodebase(Class var1);

   Class loadClass(String var1, String var2, ClassLoader var3) throws ClassNotFoundException;

   boolean isLocal(Stub var1) throws RemoteException;

   RemoteException wrapException(Throwable var1);

   Object copyObject(Object var1, ORB var2) throws RemoteException;

   Object[] copyObjects(Object[] var1, ORB var2) throws RemoteException;
}
