package javax.rmi;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.net.MalformedURLException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import org.omg.CORBA.INITIALIZE;

public class PortableRemoteObject {
   private static final PortableRemoteObjectDelegate proDelegate = (PortableRemoteObjectDelegate)createDelegate("javax.rmi.CORBA.PortableRemoteObjectClass");
   private static final String PortableRemoteObjectClassKey = "javax.rmi.CORBA.PortableRemoteObjectClass";

   protected PortableRemoteObject() throws RemoteException {
      if (proDelegate != null) {
         exportObject((Remote)this);
      }

   }

   public static void exportObject(Remote var0) throws RemoteException {
      if (proDelegate != null) {
         proDelegate.exportObject(var0);
      }

   }

   public static Remote toStub(Remote var0) throws NoSuchObjectException {
      return proDelegate != null ? proDelegate.toStub(var0) : null;
   }

   public static void unexportObject(Remote var0) throws NoSuchObjectException {
      if (proDelegate != null) {
         proDelegate.unexportObject(var0);
      }

   }

   public static Object narrow(Object var0, Class var1) throws ClassCastException {
      return proDelegate != null ? proDelegate.narrow(var0, var1) : null;
   }

   public static void connect(Remote var0, Remote var1) throws RemoteException {
      if (proDelegate != null) {
         proDelegate.connect(var0, var1);
      }

   }

   private static Object createDelegate(String var0) {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0)));
      if (var1 == null) {
         Properties var2 = getORBPropertiesFile();
         if (var2 != null) {
            var1 = var2.getProperty(var0);
         }
      }

      if (var1 == null) {
         return new com.sun.corba.se.impl.javax.rmi.PortableRemoteObject();
      } else {
         INITIALIZE var3;
         try {
            return loadDelegateClass(var1).newInstance();
         } catch (ClassNotFoundException var4) {
            var3 = new INITIALIZE("Cannot instantiate " + var1);
            var3.initCause(var4);
            throw var3;
         } catch (Exception var5) {
            var3 = new INITIALIZE("Error while instantiating" + var1);
            var3.initCause(var5);
            throw var3;
         }
      }
   }

   private static Class loadDelegateClass(String var0) throws ClassNotFoundException {
      try {
         ClassLoader var1 = Thread.currentThread().getContextClassLoader();
         return Class.forName(var0, false, var1);
      } catch (ClassNotFoundException var5) {
         try {
            return RMIClassLoader.loadClass(var0);
         } catch (MalformedURLException var4) {
            String var2 = "Could not load " + var0 + ": " + var4.toString();
            ClassNotFoundException var3 = new ClassNotFoundException(var2);
            throw var3;
         }
      }
   }

   private static Properties getORBPropertiesFile() {
      return (Properties)AccessController.doPrivileged((PrivilegedAction)(new GetORBPropertiesFileAction()));
   }
}
