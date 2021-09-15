package javax.rmi.CORBA;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.io.SerializablePermission;
import java.net.MalformedURLException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class Util {
   private static final UtilDelegate utilDelegate = (UtilDelegate)createDelegate("javax.rmi.CORBA.UtilClass");
   private static final String UtilClassKey = "javax.rmi.CORBA.UtilClass";
   private static final String ALLOW_CREATEVALUEHANDLER_PROP = "jdk.rmi.CORBA.allowCustomValueHandler";
   private static boolean allowCustomValueHandler = readAllowCustomValueHandlerProperty();

   private static boolean readAllowCustomValueHandlerProperty() {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return Boolean.getBoolean("jdk.rmi.CORBA.allowCustomValueHandler");
         }
      });
   }

   private Util() {
   }

   public static RemoteException mapSystemException(SystemException var0) {
      return utilDelegate != null ? utilDelegate.mapSystemException(var0) : null;
   }

   public static void writeAny(OutputStream var0, Object var1) {
      if (utilDelegate != null) {
         utilDelegate.writeAny(var0, var1);
      }

   }

   public static Object readAny(InputStream var0) {
      return utilDelegate != null ? utilDelegate.readAny(var0) : null;
   }

   public static void writeRemoteObject(OutputStream var0, Object var1) {
      if (utilDelegate != null) {
         utilDelegate.writeRemoteObject(var0, var1);
      }

   }

   public static void writeAbstractObject(OutputStream var0, Object var1) {
      if (utilDelegate != null) {
         utilDelegate.writeAbstractObject(var0, var1);
      }

   }

   public static void registerTarget(Tie var0, Remote var1) {
      if (utilDelegate != null) {
         utilDelegate.registerTarget(var0, var1);
      }

   }

   public static void unexportObject(Remote var0) throws NoSuchObjectException {
      if (utilDelegate != null) {
         utilDelegate.unexportObject(var0);
      }

   }

   public static Tie getTie(Remote var0) {
      return utilDelegate != null ? utilDelegate.getTie(var0) : null;
   }

   public static ValueHandler createValueHandler() {
      isCustomSerializationPermitted();
      return utilDelegate != null ? utilDelegate.createValueHandler() : null;
   }

   public static String getCodebase(Class var0) {
      return utilDelegate != null ? utilDelegate.getCodebase(var0) : null;
   }

   public static Class loadClass(String var0, String var1, ClassLoader var2) throws ClassNotFoundException {
      return utilDelegate != null ? utilDelegate.loadClass(var0, var1, var2) : null;
   }

   public static boolean isLocal(Stub var0) throws RemoteException {
      return utilDelegate != null ? utilDelegate.isLocal(var0) : false;
   }

   public static RemoteException wrapException(Throwable var0) {
      return utilDelegate != null ? utilDelegate.wrapException(var0) : null;
   }

   public static Object[] copyObjects(Object[] var0, ORB var1) throws RemoteException {
      return utilDelegate != null ? utilDelegate.copyObjects(var0, var1) : null;
   }

   public static Object copyObject(Object var0, ORB var1) throws RemoteException {
      return utilDelegate != null ? utilDelegate.copyObject(var0, var1) : null;
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
         return new com.sun.corba.se.impl.javax.rmi.CORBA.Util();
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

   private static void isCustomSerializationPermitted() {
      SecurityManager var0 = System.getSecurityManager();
      if (!allowCustomValueHandler && var0 != null) {
         var0.checkPermission(new SerializablePermission("enableCustomValueHandler"));
      }

   }
}
