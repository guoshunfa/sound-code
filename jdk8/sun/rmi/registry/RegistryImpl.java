package sun.rmi.registry;

import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationID;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.security.cert.Certificate;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import sun.misc.ObjectInputFilter;
import sun.misc.URLClassPath;
import sun.rmi.runtime.Log;
import sun.rmi.server.LoaderHandler;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.transport.LiveRef;

public class RegistryImpl extends RemoteServer implements Registry {
   private static final long serialVersionUID = 4666870661827494597L;
   private Hashtable<String, Remote> bindings;
   private static Hashtable<InetAddress, InetAddress> allowedAccessCache = new Hashtable(3);
   private static RegistryImpl registry;
   private static ObjID id = new ObjID(0);
   private static ResourceBundle resources = null;
   private static final String REGISTRY_FILTER_PROPNAME = "sun.rmi.registry.registryFilter";
   private static final int REGISTRY_MAX_DEPTH = 20;
   private static final int REGISTRY_MAX_ARRAY_SIZE = 1000000;
   private static final ObjectInputFilter registryFilter = (ObjectInputFilter)AccessController.doPrivileged(RegistryImpl::initRegistryFilter);

   private static ObjectInputFilter initRegistryFilter() {
      ObjectInputFilter var0 = null;
      String var1 = System.getProperty("sun.rmi.registry.registryFilter");
      if (var1 == null) {
         var1 = Security.getProperty("sun.rmi.registry.registryFilter");
      }

      if (var1 != null) {
         var0 = ObjectInputFilter.Config.createFilter2(var1);
         Log var2 = Log.getLog("sun.rmi.registry", "registry", -1);
         if (var2.isLoggable(Log.BRIEF)) {
            var2.log(Log.BRIEF, "registryFilter = " + var0);
         }
      }

      return var0;
   }

   public RegistryImpl(int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3) throws RemoteException {
      this(var1, var2, var3, RegistryImpl::registryFilter);
   }

   public RegistryImpl(final int var1, final RMIClientSocketFactory var2, final RMIServerSocketFactory var3, final ObjectInputFilter var4) throws RemoteException {
      this.bindings = new Hashtable(101);
      if (var1 == 1099 && System.getSecurityManager() != null) {
         try {
            AccessController.doPrivileged((PrivilegedExceptionAction)(new PrivilegedExceptionAction<Void>() {
               public Void run() throws RemoteException {
                  LiveRef var1x = new LiveRef(RegistryImpl.id, var1, var2, var3);
                  RegistryImpl.this.setup(new UnicastServerRef2(var1x, var4));
                  return null;
               }
            }), (AccessControlContext)null, new SocketPermission("localhost:" + var1, "listen,accept"));
         } catch (PrivilegedActionException var6) {
            throw (RemoteException)var6.getException();
         }
      } else {
         LiveRef var5 = new LiveRef(id, var1, var2, var3);
         this.setup(new UnicastServerRef2(var5, var4));
      }

   }

   public RegistryImpl(final int var1) throws RemoteException {
      this.bindings = new Hashtable(101);
      if (var1 == 1099 && System.getSecurityManager() != null) {
         try {
            AccessController.doPrivileged((PrivilegedExceptionAction)(new PrivilegedExceptionAction<Void>() {
               public Void run() throws RemoteException {
                  LiveRef var1x = new LiveRef(RegistryImpl.id, var1);
                  RegistryImpl.this.setup(new UnicastServerRef(var1x, (var0) -> {
                     return RegistryImpl.registryFilter(var0);
                  }));
                  return null;
               }
            }), (AccessControlContext)null, new SocketPermission("localhost:" + var1, "listen,accept"));
         } catch (PrivilegedActionException var3) {
            throw (RemoteException)var3.getException();
         }
      } else {
         LiveRef var2 = new LiveRef(id, var1);
         this.setup(new UnicastServerRef(var2, RegistryImpl::registryFilter));
      }

   }

   private void setup(UnicastServerRef var1) throws RemoteException {
      this.ref = var1;
      var1.exportObject(this, (Object)null, true);
   }

   public Remote lookup(String var1) throws RemoteException, NotBoundException {
      synchronized(this.bindings) {
         Remote var3 = (Remote)this.bindings.get(var1);
         if (var3 == null) {
            throw new NotBoundException(var1);
         } else {
            return var3;
         }
      }
   }

   public void bind(String var1, Remote var2) throws RemoteException, AlreadyBoundException, AccessException {
      synchronized(this.bindings) {
         Remote var4 = (Remote)this.bindings.get(var1);
         if (var4 != null) {
            throw new AlreadyBoundException(var1);
         } else {
            this.bindings.put(var1, var2);
         }
      }
   }

   public void unbind(String var1) throws RemoteException, NotBoundException, AccessException {
      synchronized(this.bindings) {
         Remote var3 = (Remote)this.bindings.get(var1);
         if (var3 == null) {
            throw new NotBoundException(var1);
         } else {
            this.bindings.remove(var1);
         }
      }
   }

   public void rebind(String var1, Remote var2) throws RemoteException, AccessException {
      this.bindings.put(var1, var2);
   }

   public String[] list() throws RemoteException {
      synchronized(this.bindings) {
         int var3 = this.bindings.size();
         String[] var1 = new String[var3];
         Enumeration var4 = this.bindings.keys();

         while(true) {
            --var3;
            if (var3 < 0) {
               return var1;
            }

            var1[var3] = (String)var4.nextElement();
         }
      }
   }

   public static void checkAccess(String var0) throws AccessException {
      try {
         final String var1 = getClientHost();

         final InetAddress var2;
         try {
            var2 = (InetAddress)AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
               public InetAddress run() throws UnknownHostException {
                  return InetAddress.getByName(var1);
               }
            });
         } catch (PrivilegedActionException var5) {
            throw (UnknownHostException)var5.getException();
         }

         if (allowedAccessCache.get(var2) == null) {
            if (var2.isAnyLocalAddress()) {
               throw new AccessException(var0 + " disallowed; origin unknown");
            }

            try {
               AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                  public Void run() throws IOException {
                     (new ServerSocket(0, 10, var2)).close();
                     RegistryImpl.allowedAccessCache.put(var2, var2);
                     return null;
                  }
               });
            } catch (PrivilegedActionException var4) {
               throw new AccessException(var0 + " disallowed; origin " + var2 + " is non-local host");
            }
         }
      } catch (ServerNotActiveException var6) {
      } catch (UnknownHostException var7) {
         throw new AccessException(var0 + " disallowed; origin is unknown host");
      }

   }

   public static ObjID getID() {
      return id;
   }

   private static String getTextResource(String var0) {
      if (resources == null) {
         try {
            resources = ResourceBundle.getBundle("sun.rmi.registry.resources.rmiregistry");
         } catch (MissingResourceException var4) {
         }

         if (resources == null) {
            return "[missing resource file: " + var0 + "]";
         }
      }

      String var1 = null;

      try {
         var1 = resources.getString(var0);
      } catch (MissingResourceException var3) {
      }

      return var1 == null ? "[missing resource: " + var0 + "]" : var1;
   }

   private static ObjectInputFilter.Status registryFilter(ObjectInputFilter.FilterInfo var0) {
      if (registryFilter != null) {
         ObjectInputFilter.Status var1 = registryFilter.checkInput(var0);
         if (var1 != ObjectInputFilter.Status.UNDECIDED) {
            return var1;
         }
      }

      if (var0.depth() > 20L) {
         return ObjectInputFilter.Status.REJECTED;
      } else {
         Class var2 = var0.serialClass();
         if (var2 != null) {
            if (!var2.isArray()) {
               return String.class != var2 && !Number.class.isAssignableFrom(var2) && !Remote.class.isAssignableFrom(var2) && !Proxy.class.isAssignableFrom(var2) && !UnicastRef.class.isAssignableFrom(var2) && !RMIClientSocketFactory.class.isAssignableFrom(var2) && !RMIServerSocketFactory.class.isAssignableFrom(var2) && !ActivationID.class.isAssignableFrom(var2) && !UID.class.isAssignableFrom(var2) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.ALLOWED;
            } else {
               return var0.arrayLength() >= 0L && var0.arrayLength() > 1000000L ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED;
            }
         } else {
            return ObjectInputFilter.Status.UNDECIDED;
         }
      }
   }

   public static void main(String[] var0) {
      if (System.getSecurityManager() == null) {
         System.setSecurityManager(new RMISecurityManager());
      }

      try {
         String var1 = System.getProperty("env.class.path");
         if (var1 == null) {
            var1 = ".";
         }

         URL[] var2 = URLClassPath.pathToURLs(var1);
         URLClassLoader var3 = new URLClassLoader(var2);
         LoaderHandler.registerCodebaseLoader(var3);
         Thread.currentThread().setContextClassLoader(var3);
         final int var4 = var0.length >= 1 ? Integer.parseInt(var0[0]) : 1099;

         try {
            registry = (RegistryImpl)AccessController.doPrivileged(new PrivilegedExceptionAction<RegistryImpl>() {
               public RegistryImpl run() throws RemoteException {
                  return new RegistryImpl(var4);
               }
            }, getAccessControlContext(var4));
         } catch (PrivilegedActionException var6) {
            throw (RemoteException)var6.getException();
         }

         while(true) {
            while(true) {
               try {
                  Thread.sleep(Long.MAX_VALUE);
               } catch (InterruptedException var7) {
               }
            }
         }
      } catch (NumberFormatException var8) {
         System.err.println(MessageFormat.format(getTextResource("rmiregistry.port.badnumber"), var0[0]));
         System.err.println(MessageFormat.format(getTextResource("rmiregistry.usage"), "rmiregistry"));
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      System.exit(1);
   }

   private static AccessControlContext getAccessControlContext(int var0) {
      PermissionCollection var1 = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
         public PermissionCollection run() {
            CodeSource var1 = new CodeSource((URL)null, (Certificate[])null);
            Policy var2 = Policy.getPolicy();
            return (PermissionCollection)(var2 != null ? var2.getPermissions(var1) : new Permissions());
         }
      });
      var1.add(new SocketPermission("*", "connect,accept"));
      var1.add(new SocketPermission("localhost:" + var0, "listen,accept"));
      var1.add(new RuntimePermission("accessClassInPackage.sun.jvmstat.*"));
      var1.add(new RuntimePermission("accessClassInPackage.sun.jvm.hotspot.*"));
      var1.add(new FilePermission("<<ALL FILES>>", "read"));
      ProtectionDomain var2 = new ProtectionDomain(new CodeSource((URL)null, (Certificate[])null), var1);
      return new AccessControlContext(new ProtectionDomain[]{var2});
   }
}
