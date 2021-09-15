package javax.management.remote.rmi;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.internal.ClientCommunicatorAdmin;
import com.sun.jmx.remote.internal.ClientListenerInfo;
import com.sun.jmx.remote.internal.ClientNotifForwarder;
import com.sun.jmx.remote.internal.IIOPHelper;
import com.sun.jmx.remote.internal.ProxyRef;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.MarshalException;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.UnmarshalException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerDelegate;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.remote.JMXAddressable;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.NotificationResult;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.server.UnicastRef2;
import sun.rmi.transport.LiveRef;

public class RMIConnector implements JMXConnector, Serializable, JMXAddressable {
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnector");
   private static final long serialVersionUID = 817323035842634473L;
   private static final String rmiServerImplStubClassName = RMIServer.class.getName() + "Impl_Stub";
   private static final Class<?> rmiServerImplStubClass;
   private static final String rmiConnectionImplStubClassName = RMIConnection.class.getName() + "Impl_Stub";
   private static final Class<?> rmiConnectionImplStubClass;
   private static final String pRefClassName = "com.sun.jmx.remote.internal.PRef";
   private static final Constructor<?> proxyRefConstructor;
   private static final String iiopConnectionStubClassName = "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub";
   private static final String proxyStubClassName = "com.sun.jmx.remote.protocol.iiop.ProxyStub";
   private static final String ProxyInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.ProxyInputStream";
   private static final String pInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.PInputStream";
   private static final Class<?> proxyStubClass;
   private static final byte[] base64ToInt;
   private final RMIServer rmiServer;
   private final JMXServiceURL jmxServiceURL;
   private transient Map<String, Object> env;
   private transient ClassLoader defaultClassLoader;
   private transient RMIConnection connection;
   private transient String connectionId;
   private transient long clientNotifSeqNo;
   private transient WeakHashMap<Subject, WeakReference<MBeanServerConnection>> rmbscMap;
   private transient WeakReference<MBeanServerConnection> nullSubjectConnRef;
   private transient RMIConnector.RMINotifClient rmiNotifClient;
   private transient long clientNotifCounter;
   private transient boolean connected;
   private transient boolean terminated;
   private transient Exception closeException;
   private transient NotificationBroadcasterSupport connectionBroadcaster;
   private transient ClientCommunicatorAdmin communicatorAdmin;
   private static volatile WeakReference<Object> orb;

   private RMIConnector(RMIServer var1, JMXServiceURL var2, Map<String, ?> var3) {
      this.clientNotifSeqNo = 0L;
      this.nullSubjectConnRef = null;
      this.clientNotifCounter = 0L;
      if (var1 == null && var2 == null) {
         throw new IllegalArgumentException("rmiServer and jmxServiceURL both null");
      } else {
         this.initTransients();
         this.rmiServer = var1;
         this.jmxServiceURL = var2;
         if (var3 == null) {
            this.env = Collections.emptyMap();
         } else {
            EnvHelp.checkAttributes(var3);
            this.env = Collections.unmodifiableMap(var3);
         }

      }
   }

   public RMIConnector(JMXServiceURL var1, Map<String, ?> var2) {
      this((RMIServer)null, var1, var2);
   }

   public RMIConnector(RMIServer var1, Map<String, ?> var2) {
      this(var1, (JMXServiceURL)null, var2);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.getClass().getName());
      var1.append(":");
      if (this.rmiServer != null) {
         var1.append(" rmiServer=").append(this.rmiServer.toString());
      }

      if (this.jmxServiceURL != null) {
         if (this.rmiServer != null) {
            var1.append(",");
         }

         var1.append(" jmxServiceURL=").append(this.jmxServiceURL.toString());
      }

      return var1.toString();
   }

   public JMXServiceURL getAddress() {
      return this.jmxServiceURL;
   }

   public void connect() throws IOException {
      this.connect((Map)null);
   }

   public synchronized void connect(Map<String, ?> var1) throws IOException {
      boolean var2 = logger.traceOn();
      String var3 = var2 ? "[" + this.toString() + "]" : null;
      if (this.terminated) {
         logger.trace("connect", var3 + " already closed.");
         throw new IOException("Connector closed");
      } else if (this.connected) {
         logger.trace("connect", var3 + " already connected.");
      } else {
         try {
            if (var2) {
               logger.trace("connect", var3 + " connecting...");
            }

            HashMap var4 = new HashMap(this.env == null ? Collections.emptyMap() : this.env);
            if (var1 != null) {
               EnvHelp.checkAttributes(var1);
               var4.putAll(var1);
            }

            if (var2) {
               logger.trace("connect", var3 + " finding stub...");
            }

            RMIServer var17 = this.rmiServer != null ? this.rmiServer : this.findRMIServer(this.jmxServiceURL, var4);
            String var6 = (String)var4.get("jmx.remote.x.check.stub");
            boolean var7 = EnvHelp.computeBooleanFromString(var6);
            if (var7) {
               checkStub(var17, rmiServerImplStubClass);
            }

            if (var2) {
               logger.trace("connect", var3 + " connecting stub...");
            }

            var17 = connectStub(var17, var4);
            var3 = var2 ? "[" + this.toString() + "]" : null;
            if (var2) {
               logger.trace("connect", var3 + " getting connection...");
            }

            Object var8 = var4.get("jmx.remote.credentials");

            try {
               this.connection = getConnection(var17, var8, var7);
            } catch (RemoteException var13) {
               if (this.jmxServiceURL != null) {
                  String var10 = this.jmxServiceURL.getProtocol();
                  String var11 = this.jmxServiceURL.getURLPath();
                  if ("rmi".equals(var10) && var11.startsWith("/jndi/iiop:")) {
                     MalformedURLException var12 = new MalformedURLException("Protocol is rmi but JNDI scheme is iiop: " + this.jmxServiceURL);
                     var12.initCause(var13);
                     throw var12;
                  }
               }

               throw var13;
            }

            if (var2) {
               logger.trace("connect", var3 + " getting class loader...");
            }

            this.defaultClassLoader = EnvHelp.resolveClientClassLoader(var4);
            var4.put("jmx.remote.default.class.loader", this.defaultClassLoader);
            this.rmiNotifClient = new RMIConnector.RMINotifClient(this.defaultClassLoader, var4);
            this.env = var4;
            long var9 = EnvHelp.getConnectionCheckPeriod(var4);
            this.communicatorAdmin = new RMIConnector.RMIClientCommunicatorAdmin(var9);
            this.connected = true;
            this.connectionId = this.getConnectionId();
            JMXConnectionNotification var18 = new JMXConnectionNotification("jmx.remote.connection.opened", this, this.connectionId, (long)(this.clientNotifSeqNo++), "Successful connection", (Object)null);
            this.sendNotification(var18);
            if (var2) {
               logger.trace("connect", var3 + " done...");
            }

         } catch (IOException var14) {
            if (var2) {
               logger.trace("connect", var3 + " failed to connect: " + var14);
            }

            throw var14;
         } catch (RuntimeException var15) {
            if (var2) {
               logger.trace("connect", var3 + " failed to connect: " + var15);
            }

            throw var15;
         } catch (NamingException var16) {
            String var5 = "Failed to retrieve RMIServer stub: " + var16;
            if (var2) {
               logger.trace("connect", var3 + " " + var5);
            }

            throw (IOException)EnvHelp.initCause(new IOException(var5), var16);
         }
      }
   }

   public synchronized String getConnectionId() throws IOException {
      if (!this.terminated && this.connected) {
         return this.connection.getConnectionId();
      } else {
         if (logger.traceOn()) {
            logger.trace("getConnectionId", "[" + this.toString() + "] not connected.");
         }

         throw new IOException("Not connected");
      }
   }

   public synchronized MBeanServerConnection getMBeanServerConnection() throws IOException {
      return this.getMBeanServerConnection((Subject)null);
   }

   public synchronized MBeanServerConnection getMBeanServerConnection(Subject var1) throws IOException {
      if (this.terminated) {
         if (logger.traceOn()) {
            logger.trace("getMBeanServerConnection", "[" + this.toString() + "] already closed.");
         }

         throw new IOException("Connection closed");
      } else if (!this.connected) {
         if (logger.traceOn()) {
            logger.trace("getMBeanServerConnection", "[" + this.toString() + "] is not connected.");
         }

         throw new IOException("Not connected");
      } else {
         return this.getConnectionWithSubject(var1);
      }
   }

   public void addConnectionNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      if (var1 == null) {
         throw new NullPointerException("listener");
      } else {
         this.connectionBroadcaster.addNotificationListener(var1, var2, var3);
      }
   }

   public void removeConnectionNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      if (var1 == null) {
         throw new NullPointerException("listener");
      } else {
         this.connectionBroadcaster.removeNotificationListener(var1);
      }
   }

   public void removeConnectionNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      if (var1 == null) {
         throw new NullPointerException("listener");
      } else {
         this.connectionBroadcaster.removeNotificationListener(var1, var2, var3);
      }
   }

   private void sendNotification(Notification var1) {
      this.connectionBroadcaster.sendNotification(var1);
   }

   public synchronized void close() throws IOException {
      this.close(false);
   }

   private synchronized void close(boolean var1) throws IOException {
      boolean var2 = logger.traceOn();
      boolean var3 = logger.debugOn();
      String var4 = var2 ? "[" + this.toString() + "]" : null;
      if (!var1) {
         if (this.terminated) {
            if (this.closeException == null) {
               if (var2) {
                  logger.trace("close", var4 + " already closed.");
               }

               return;
            }
         } else {
            this.terminated = true;
         }
      }

      if (this.closeException != null && var2 && var2) {
         logger.trace("close", var4 + " had failed: " + this.closeException);
         logger.trace("close", var4 + " attempting to close again.");
      }

      String var5 = null;
      if (this.connected) {
         var5 = this.connectionId;
      }

      this.closeException = null;
      if (var2) {
         logger.trace("close", var4 + " closing.");
      }

      if (this.communicatorAdmin != null) {
         this.communicatorAdmin.terminate();
      }

      if (this.rmiNotifClient != null) {
         try {
            this.rmiNotifClient.terminate();
            if (var2) {
               logger.trace("close", var4 + " RMI Notification client terminated.");
            }
         } catch (RuntimeException var9) {
            this.closeException = var9;
            if (var2) {
               logger.trace("close", var4 + " Failed to terminate RMI Notification client: " + var9);
            }

            if (var3) {
               logger.debug("close", (Throwable)var9);
            }
         }
      }

      if (this.connection != null) {
         try {
            this.connection.close();
            if (var2) {
               logger.trace("close", var4 + " closed.");
            }
         } catch (NoSuchObjectException var7) {
         } catch (IOException var8) {
            this.closeException = var8;
            if (var2) {
               logger.trace("close", var4 + " Failed to close RMIServer: " + var8);
            }

            if (var3) {
               logger.debug("close", (Throwable)var8);
            }
         }
      }

      this.rmbscMap.clear();
      if (var5 != null) {
         JMXConnectionNotification var6 = new JMXConnectionNotification("jmx.remote.connection.closed", this, var5, (long)(this.clientNotifSeqNo++), "Client has been closed", (Object)null);
         this.sendNotification(var6);
      }

      if (this.closeException != null) {
         if (var2) {
            logger.trace("close", var4 + " failed to close: " + this.closeException);
         }

         if (this.closeException instanceof IOException) {
            throw (IOException)this.closeException;
         } else if (this.closeException instanceof RuntimeException) {
            throw (RuntimeException)this.closeException;
         } else {
            IOException var10 = new IOException("Failed to close: " + this.closeException);
            throw (IOException)EnvHelp.initCause(var10, this.closeException);
         }
      }
   }

   private Integer addListenerWithSubject(ObjectName var1, MarshalledObject<NotificationFilter> var2, Subject var3, boolean var4) throws InstanceNotFoundException, IOException {
      boolean var5 = logger.debugOn();
      if (var5) {
         logger.debug("addListenerWithSubject", "(ObjectName,MarshalledObject,Subject)");
      }

      ObjectName[] var6 = new ObjectName[]{var1};
      MarshalledObject[] var7 = (MarshalledObject[])Util.cast(new MarshalledObject[]{var2});
      Subject[] var8 = new Subject[]{var3};
      Integer[] var9 = this.addListenersWithSubjects(var6, var7, var8, var4);
      if (var5) {
         logger.debug("addListenerWithSubject", "listenerID=" + var9[0]);
      }

      return var9[0];
   }

   private Integer[] addListenersWithSubjects(ObjectName[] var1, MarshalledObject<NotificationFilter>[] var2, Subject[] var3, boolean var4) throws InstanceNotFoundException, IOException {
      boolean var5 = logger.debugOn();
      if (var5) {
         logger.debug("addListenersWithSubjects", "(ObjectName[],MarshalledObject[],Subject[])");
      }

      ClassLoader var6 = this.pushDefaultClassLoader();
      Integer[] var7 = null;

      try {
         var7 = this.connection.addNotificationListeners(var1, var2, var3);
      } catch (NoSuchObjectException var13) {
         if (!var4) {
            throw var13;
         }

         this.communicatorAdmin.gotIOException(var13);
         var7 = this.connection.addNotificationListeners(var1, var2, var3);
      } catch (IOException var14) {
         this.communicatorAdmin.gotIOException(var14);
      } finally {
         this.popDefaultClassLoader(var6);
      }

      if (var5) {
         logger.debug("addListenersWithSubjects", "registered " + (var7 == null ? 0 : var7.length) + " listener(s)");
      }

      return var7;
   }

   static RMIServer connectStub(RMIServer var0, Map<String, ?> var1) throws IOException {
      if (IIOPHelper.isStub(var0)) {
         try {
            IIOPHelper.getOrb(var0);
         } catch (UnsupportedOperationException var3) {
            IIOPHelper.connect(var0, resolveOrb(var1));
         }
      }

      return var0;
   }

   static Object resolveOrb(Map<String, ?> var0) throws IOException {
      Object var1;
      if (var0 != null) {
         var1 = var0.get("java.naming.corba.orb");
         if (var1 != null && !IIOPHelper.isOrb(var1)) {
            throw new IllegalArgumentException("java.naming.corba.orb must be an instance of org.omg.CORBA.ORB.");
         }

         if (var1 != null) {
            return var1;
         }
      }

      var1 = orb == null ? null : orb.get();
      if (var1 != null) {
         return var1;
      } else {
         Object var2 = IIOPHelper.createOrb((String[])null, (Properties)null);
         orb = new WeakReference(var2);
         return var2;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.rmiServer == null && this.jmxServiceURL == null) {
         throw new InvalidObjectException("rmiServer and jmxServiceURL both null");
      } else {
         this.initTransients();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.rmiServer == null && this.jmxServiceURL == null) {
         throw new InvalidObjectException("rmiServer and jmxServiceURL both null.");
      } else {
         connectStub(this.rmiServer, this.env);
         var1.defaultWriteObject();
      }
   }

   private void initTransients() {
      this.rmbscMap = new WeakHashMap();
      this.connected = false;
      this.terminated = false;
      this.connectionBroadcaster = new NotificationBroadcasterSupport();
   }

   private static void checkStub(Remote var0, Class<?> var1) {
      if (var0.getClass() != var1) {
         if (!Proxy.isProxyClass(var0.getClass())) {
            throw new SecurityException("Expecting a " + var1.getName() + " stub!");
         }

         InvocationHandler var2 = Proxy.getInvocationHandler(var0);
         if (var2.getClass() != RemoteObjectInvocationHandler.class) {
            throw new SecurityException("Expecting a dynamic proxy instance with a " + RemoteObjectInvocationHandler.class.getName() + " invocation handler!");
         }

         var0 = (Remote)var2;
      }

      RemoteRef var5 = ((RemoteObject)var0).getRef();
      if (var5.getClass() != UnicastRef2.class) {
         throw new SecurityException("Expecting a " + UnicastRef2.class.getName() + " remote reference in stub!");
      } else {
         LiveRef var3 = ((UnicastRef2)var5).getLiveRef();
         RMIClientSocketFactory var4 = var3.getClientSocketFactory();
         if (var4 == null || var4.getClass() != SslRMIClientSocketFactory.class) {
            throw new SecurityException("Expecting a " + SslRMIClientSocketFactory.class.getName() + " RMI client socket factory in stub!");
         }
      }
   }

   private RMIServer findRMIServer(JMXServiceURL var1, Map<String, Object> var2) throws NamingException, IOException {
      boolean var3 = RMIConnectorServer.isIiopURL(var1, true);
      if (var3) {
         var2.put("java.naming.corba.orb", resolveOrb(var2));
      }

      String var4 = var1.getURLPath();
      int var5 = var4.indexOf(59);
      if (var5 < 0) {
         var5 = var4.length();
      }

      if (var4.startsWith("/jndi/")) {
         return this.findRMIServerJNDI(var4.substring(6, var5), var2, var3);
      } else if (var4.startsWith("/stub/")) {
         return this.findRMIServerJRMP(var4.substring(6, var5), var2, var3);
      } else if (var4.startsWith("/ior/")) {
         if (!IIOPHelper.isAvailable()) {
            throw new IOException("iiop protocol not available");
         } else {
            return this.findRMIServerIIOP(var4.substring(5, var5), var2, var3);
         }
      } else {
         String var6 = "URL path must begin with /jndi/ or /stub/ or /ior/: " + var4;
         throw new MalformedURLException(var6);
      }
   }

   private RMIServer findRMIServerJNDI(String var1, Map<String, ?> var2, boolean var3) throws NamingException {
      InitialContext var4 = new InitialContext(EnvHelp.mapToHashtable(var2));
      Object var5 = var4.lookup(var1);
      var4.close();
      return var3 ? narrowIIOPServer(var5) : narrowJRMPServer(var5);
   }

   private static RMIServer narrowJRMPServer(Object var0) {
      return (RMIServer)var0;
   }

   private static RMIServer narrowIIOPServer(Object var0) {
      try {
         return (RMIServer)IIOPHelper.narrow(var0, RMIServer.class);
      } catch (ClassCastException var2) {
         if (logger.traceOn()) {
            logger.trace("narrowIIOPServer", "Failed to narrow objref=" + var0 + ": " + var2);
         }

         if (logger.debugOn()) {
            logger.debug("narrowIIOPServer", (Throwable)var2);
         }

         return null;
      }
   }

   private RMIServer findRMIServerIIOP(String var1, Map<String, ?> var2, boolean var3) {
      Object var4 = var2.get("java.naming.corba.orb");
      Object var5 = IIOPHelper.stringToObject(var4, var1);
      return (RMIServer)IIOPHelper.narrow(var5, RMIServer.class);
   }

   private RMIServer findRMIServerJRMP(String var1, Map<String, ?> var2, boolean var3) throws IOException {
      byte[] var4;
      try {
         var4 = base64ToByteArray(var1);
      } catch (IllegalArgumentException var11) {
         throw new MalformedURLException("Bad BASE64 encoding: " + var11.getMessage());
      }

      ByteArrayInputStream var5 = new ByteArrayInputStream(var4);
      ClassLoader var6 = EnvHelp.resolveClientClassLoader(var2);
      Object var7 = var6 == null ? new ObjectInputStream(var5) : new RMIConnector.ObjectInputStreamWithLoader(var5, var6);

      Object var8;
      try {
         var8 = ((ObjectInputStream)var7).readObject();
      } catch (ClassNotFoundException var10) {
         throw new MalformedURLException("Class not found: " + var10);
      }

      return (RMIServer)var8;
   }

   private MBeanServerConnection getConnectionWithSubject(Subject var1) {
      Object var2 = null;
      if (var1 == null) {
         if (this.nullSubjectConnRef == null || (var2 = (MBeanServerConnection)this.nullSubjectConnRef.get()) == null) {
            var2 = new RMIConnector.RemoteMBeanServerConnection((Subject)null);
            this.nullSubjectConnRef = new WeakReference(var2);
         }
      } else {
         WeakReference var3 = (WeakReference)this.rmbscMap.get(var1);
         if (var3 == null || (var2 = (MBeanServerConnection)var3.get()) == null) {
            var2 = new RMIConnector.RemoteMBeanServerConnection(var1);
            this.rmbscMap.put(var1, new WeakReference(var2));
         }
      }

      return (MBeanServerConnection)var2;
   }

   private static RMIConnection shadowJrmpStub(RemoteObject var0) throws InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
      RemoteRef var1 = var0.getRef();
      RemoteRef var2 = (RemoteRef)proxyRefConstructor.newInstance(var1);
      Constructor var3 = rmiConnectionImplStubClass.getConstructor(RemoteRef.class);
      Object[] var4 = new Object[]{var2};
      RMIConnection var5 = (RMIConnection)var3.newInstance(var4);
      return var5;
   }

   private static RMIConnection shadowIiopStub(Object var0) throws InstantiationException, IllegalAccessException {
      Object var1 = null;

      try {
         var1 = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() throws Exception {
               return RMIConnector.proxyStubClass.newInstance();
            }
         });
      } catch (PrivilegedActionException var3) {
         throw new InternalError();
      }

      IIOPHelper.setDelegate(var1, IIOPHelper.getDelegate(var0));
      return (RMIConnection)var1;
   }

   private static RMIConnection getConnection(RMIServer var0, Object var1, boolean var2) throws IOException {
      RMIConnection var3 = var0.newClient(var1);
      if (var2) {
         checkStub(var3, rmiConnectionImplStubClass);
      }

      try {
         if (var3.getClass() == rmiConnectionImplStubClass) {
            return shadowJrmpStub((RemoteObject)var3);
         }

         if (var3.getClass().getName().equals("org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub")) {
            return shadowIiopStub(var3);
         }

         logger.trace("getConnection", "Did not wrap " + var3.getClass() + " to foil stack search for classes: class loading semantics may be incorrect");
      } catch (Exception var5) {
         logger.error("getConnection", "Could not wrap " + var3.getClass() + " to foil stack search for classes: class loading semantics may be incorrect: " + var5);
         logger.debug("getConnection", (Throwable)var5);
      }

      return var3;
   }

   private static byte[] base64ToByteArray(String var0) {
      int var1 = var0.length();
      int var2 = var1 / 4;
      if (4 * var2 != var1) {
         throw new IllegalArgumentException("String length must be a multiple of four.");
      } else {
         int var3 = 0;
         int var4 = var2;
         if (var1 != 0) {
            if (var0.charAt(var1 - 1) == '=') {
               ++var3;
               var4 = var2 - 1;
            }

            if (var0.charAt(var1 - 2) == '=') {
               ++var3;
            }
         }

         byte[] var5 = new byte[3 * var2 - var3];
         int var6 = 0;
         int var7 = 0;

         int var8;
         int var9;
         int var10;
         for(var8 = 0; var8 < var4; ++var8) {
            var9 = base64toInt(var0.charAt(var6++));
            var10 = base64toInt(var0.charAt(var6++));
            int var11 = base64toInt(var0.charAt(var6++));
            int var12 = base64toInt(var0.charAt(var6++));
            var5[var7++] = (byte)(var9 << 2 | var10 >> 4);
            var5[var7++] = (byte)(var10 << 4 | var11 >> 2);
            var5[var7++] = (byte)(var11 << 6 | var12);
         }

         if (var3 != 0) {
            var8 = base64toInt(var0.charAt(var6++));
            var9 = base64toInt(var0.charAt(var6++));
            var5[var7++] = (byte)(var8 << 2 | var9 >> 4);
            if (var3 == 1) {
               var10 = base64toInt(var0.charAt(var6++));
               var5[var7++] = (byte)(var9 << 4 | var10 >> 2);
            }
         }

         return var5;
      }
   }

   private static int base64toInt(char var0) {
      byte var1;
      if (var0 >= base64ToInt.length) {
         var1 = -1;
      } else {
         var1 = base64ToInt[var0];
      }

      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal character " + var0);
      } else {
         return var1;
      }
   }

   private ClassLoader pushDefaultClassLoader() {
      final Thread var1 = Thread.currentThread();
      ClassLoader var2 = var1.getContextClassLoader();
      if (this.defaultClassLoader != null) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               var1.setContextClassLoader(RMIConnector.this.defaultClassLoader);
               return null;
            }
         });
      }

      return var2;
   }

   private void popDefaultClassLoader(final ClassLoader var1) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            Thread.currentThread().setContextClassLoader(var1);
            return null;
         }
      });
   }

   private static String objects(Object[] var0) {
      return var0 == null ? "null" : Arrays.asList(var0).toString();
   }

   private static String strings(String[] var0) {
      return objects(var0);
   }

   static String getAttributesNames(AttributeList var0) {
      return var0 != null ? (String)var0.asList().stream().map(Attribute::getName).collect(Collectors.joining(", ", "[", "]")) : "[]";
   }

   static {
      final byte[] var1 = NoCallStackClassLoader.stringToBytes("Êþº¾\u0000\u0000\u0000.\u0000\u0017\n\u0000\u0005\u0000\r\t\u0000\u0004\u0000\u000e\u000b\u0000\u000f\u0000\u0010\u0007\u0000\u0011\u0007\u0000\u0012\u0001\u0000\u0006<init>\u0001\u0000\u001e(Ljava/rmi/server/RemoteRef;)V\u0001\u0000\u0004Code\u0001\u0000\u0006invoke\u0001\u0000S(Ljava/rmi/Remote;Ljava/lang/reflect/Method;[Ljava/lang/Object;J)Ljava/lang/Object;\u0001\u0000\nExceptions\u0007\u0000\u0013\f\u0000\u0006\u0000\u0007\f\u0000\u0014\u0000\u0015\u0007\u0000\u0016\f\u0000\t\u0000\n\u0001\u0000 com/sun/jmx/remote/internal/PRef\u0001\u0000$com/sun/jmx/remote/internal/ProxyRef\u0001\u0000\u0013java/lang/Exception\u0001\u0000\u0003ref\u0001\u0000\u001bLjava/rmi/server/RemoteRef;\u0001\u0000\u0019java/rmi/server/RemoteRef\u0000!\u0000\u0004\u0000\u0005\u0000\u0000\u0000\u0000\u0000\u0002\u0000\u0001\u0000\u0006\u0000\u0007\u0000\u0001\u0000\b\u0000\u0000\u0000\u0012\u0000\u0002\u0000\u0002\u0000\u0000\u0000\u0006*+·\u0000\u0001±\u0000\u0000\u0000\u0000\u0000\u0001\u0000\t\u0000\n\u0000\u0002\u0000\b\u0000\u0000\u0000\u001b\u0000\u0006\u0000\u0006\u0000\u0000\u0000\u000f*´\u0000\u0002+,-\u0016\u0004¹\u0000\u0003\u0006\u0000°\u0000\u0000\u0000\u0000\u0000\u000b\u0000\u0000\u0000\u0004\u0000\u0001\u0000\f\u0000\u0000");
      PrivilegedExceptionAction var2 = new PrivilegedExceptionAction<Constructor<?>>() {
         public Constructor<?> run() throws Exception {
            Class var1x = RMIConnector.class;
            ClassLoader var2 = var1x.getClassLoader();
            ProtectionDomain var3 = var1x.getProtectionDomain();
            String[] var4 = new String[]{ProxyRef.class.getName()};
            NoCallStackClassLoader var5 = new NoCallStackClassLoader("com.sun.jmx.remote.internal.PRef", var1, var4, var2, var3);
            Class var6 = var5.loadClass("com.sun.jmx.remote.internal.PRef");
            return var6.getConstructor(RemoteRef.class);
         }
      };

      Class var3;
      try {
         var3 = Class.forName(rmiServerImplStubClassName);
      } catch (Exception var12) {
         logger.error("<clinit>", "Failed to instantiate " + rmiServerImplStubClassName + ": " + var12);
         logger.debug("<clinit>", (Throwable)var12);
         var3 = null;
      }

      rmiServerImplStubClass = var3;

      Class var4;
      Constructor var5;
      try {
         var4 = Class.forName(rmiConnectionImplStubClassName);
         var5 = (Constructor)AccessController.doPrivileged(var2);
      } catch (Exception var11) {
         logger.error("<clinit>", "Failed to initialize proxy reference constructor for " + rmiConnectionImplStubClassName + ": " + var11);
         logger.debug("<clinit>", (Throwable)var11);
         var4 = null;
         var5 = null;
      }

      rmiConnectionImplStubClass = var4;
      proxyRefConstructor = var5;
      byte[] var13 = NoCallStackClassLoader.stringToBytes("Êþº¾\u0000\u0000\u00003\u0000+\n\u0000\f\u0000\u0018\u0007\u0000\u0019\n\u0000\f\u0000\u001a\n\u0000\u0002\u0000\u001b\u0007\u0000\u001c\n\u0000\u0005\u0000\u001d\n\u0000\u0005\u0000\u001e\n\u0000\u0005\u0000\u001f\n\u0000\u0002\u0000 \n\u0000\f\u0000!\u0007\u0000\"\u0007\u0000#\u0001\u0000\u0006<init>\u0001\u0000\u0003()V\u0001\u0000\u0004Code\u0001\u0000\u0007_invoke\u0001\u0000K(Lorg/omg/CORBA/portable/OutputStream;)Lorg/omg/CORBA/portable/InputStream;\u0001\u0000\rStackMapTable\u0007\u0000\u001c\u0001\u0000\nExceptions\u0007\u0000$\u0001\u0000\r_releaseReply\u0001\u0000'(Lorg/omg/CORBA/portable/InputStream;)V\f\u0000\r\u0000\u000e\u0001\u0000-com/sun/jmx/remote/protocol/iiop/PInputStream\f\u0000\u0010\u0000\u0011\f\u0000\r\u0000\u0017\u0001\u0000+org/omg/CORBA/portable/ApplicationException\f\u0000%\u0000&\f\u0000'\u0000(\f\u0000\r\u0000)\f\u0000*\u0000&\f\u0000\u0016\u0000\u0017\u0001\u0000*com/sun/jmx/remote/protocol/iiop/ProxyStub\u0001\u0000<org/omg/stub/javax/management/remote/rmi/_RMIConnection_Stub\u0001\u0000)org/omg/CORBA/portable/RemarshalException\u0001\u0000\u000egetInputStream\u0001\u0000&()Lorg/omg/CORBA/portable/InputStream;\u0001\u0000\u0005getId\u0001\u0000\u0014()Ljava/lang/String;\u0001\u00009(Ljava/lang/String;Lorg/omg/CORBA/portable/InputStream;)V\u0001\u0000\u0015getProxiedInputStream\u0000!\u0000\u000b\u0000\f\u0000\u0000\u0000\u0000\u0000\u0003\u0000\u0001\u0000\r\u0000\u000e\u0000\u0001\u0000\u000f\u0000\u0000\u0000\u0011\u0000\u0001\u0000\u0001\u0000\u0000\u0000\u0005*·\u0000\u0001±\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u0010\u0000\u0011\u0000\u0002\u0000\u000f\u0000\u0000\u0000G\u0000\u0004\u0000\u0004\u0000\u0000\u0000'»\u0000\u0002Y*+·\u0000\u0003·\u0000\u0004°M»\u0000\u0002Y,¶\u0000\u0006·\u0000\u0004N»\u0000\u0005Y,¶\u0000\u0007-·\u0000\b¿\u0000\u0001\u0000\u0000\u0000\f\u0000\r\u0000\u0005\u0000\u0001\u0000\u0012\u0000\u0000\u0000\u0006\u0000\u0001M\u0007\u0000\u0013\u0000\u0014\u0000\u0000\u0000\u0006\u0000\u0002\u0000\u0005\u0000\u0015\u0000\u0001\u0000\u0016\u0000\u0017\u0000\u0001\u0000\u000f\u0000\u0000\u0000'\u0000\u0002\u0000\u0002\u0000\u0000\u0000\u0012+Æ\u0000\u000b+À\u0000\u0002¶\u0000\tL*+·\u0000\n±\u0000\u0000\u0000\u0001\u0000\u0012\u0000\u0000\u0000\u0003\u0000\u0001\f\u0000\u0000");
      byte[] var14 = NoCallStackClassLoader.stringToBytes("Êþº¾\u0000\u0000\u00003\u0000\u001e\n\u0000\u0007\u0000\u000f\t\u0000\u0006\u0000\u0010\n\u0000\u0011\u0000\u0012\n\u0000\u0006\u0000\u0013\n\u0000\u0014\u0000\u0015\u0007\u0000\u0016\u0007\u0000\u0017\u0001\u0000\u0006<init>\u0001\u0000'(Lorg/omg/CORBA/portable/InputStream;)V\u0001\u0000\u0004Code\u0001\u0000\bread_any\u0001\u0000\u0015()Lorg/omg/CORBA/Any;\u0001\u0000\nread_value\u0001\u0000)(Ljava/lang/Class;)Ljava/io/Serializable;\f\u0000\b\u0000\t\f\u0000\u0018\u0000\u0019\u0007\u0000\u001a\f\u0000\u000b\u0000\f\f\u0000\u001b\u0000\u001c\u0007\u0000\u001d\f\u0000\r\u0000\u000e\u0001\u0000-com/sun/jmx/remote/protocol/iiop/PInputStream\u0001\u00001com/sun/jmx/remote/protocol/iiop/ProxyInputStream\u0001\u0000\u0002in\u0001\u0000$Lorg/omg/CORBA/portable/InputStream;\u0001\u0000\"org/omg/CORBA/portable/InputStream\u0001\u0000\u0006narrow\u0001\u0000*()Lorg/omg/CORBA_2_3/portable/InputStream;\u0001\u0000&org/omg/CORBA_2_3/portable/InputStream\u0000!\u0000\u0006\u0000\u0007\u0000\u0000\u0000\u0000\u0000\u0003\u0000\u0001\u0000\b\u0000\t\u0000\u0001\u0000\n\u0000\u0000\u0000\u0012\u0000\u0002\u0000\u0002\u0000\u0000\u0000\u0006*+·\u0000\u0001±\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u000b\u0000\f\u0000\u0001\u0000\n\u0000\u0000\u0000\u0014\u0000\u0001\u0000\u0001\u0000\u0000\u0000\b*´\u0000\u0002¶\u0000\u0003°\u0000\u0000\u0000\u0000\u0000\u0001\u0000\r\u0000\u000e\u0000\u0001\u0000\n\u0000\u0000\u0000\u0015\u0000\u0002\u0000\u0002\u0000\u0000\u0000\t*¶\u0000\u0004+¶\u0000\u0005°\u0000\u0000\u0000\u0000\u0000\u0000");
      final String[] var15 = new String[]{"com.sun.jmx.remote.protocol.iiop.ProxyStub", "com.sun.jmx.remote.protocol.iiop.PInputStream"};
      final byte[][] var16 = new byte[][]{var13, var14};
      final String[] var6 = new String[]{"org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub", "com.sun.jmx.remote.protocol.iiop.ProxyInputStream"};
      if (IIOPHelper.isAvailable()) {
         PrivilegedExceptionAction var7 = new PrivilegedExceptionAction<Class<?>>() {
            public Class<?> run() throws Exception {
               Class var1 = RMIConnector.class;
               ClassLoader var2 = var1.getClassLoader();
               ProtectionDomain var3 = var1.getProtectionDomain();
               NoCallStackClassLoader var4 = new NoCallStackClassLoader(var15, var16, var6, var2, var3);
               return var4.loadClass("com.sun.jmx.remote.protocol.iiop.ProxyStub");
            }
         };

         Class var8;
         try {
            var8 = (Class)AccessController.doPrivileged(var7);
         } catch (Exception var10) {
            logger.error("<clinit>", "Unexpected exception making shadow IIOP stub class: " + var10);
            logger.debug("<clinit>", (Throwable)var10);
            var8 = null;
         }

         proxyStubClass = var8;
      } else {
         proxyStubClass = null;
      }

      base64ToInt = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};
      orb = null;
   }

   private static final class ObjectInputStreamWithLoader extends ObjectInputStream {
      private final ClassLoader loader;

      ObjectInputStreamWithLoader(InputStream var1, ClassLoader var2) throws IOException {
         super(var1);
         this.loader = var2;
      }

      protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
         String var2 = var1.getName();
         ReflectUtil.checkPackageAccess(var2);
         return Class.forName(var2, false, this.loader);
      }
   }

   private class RMIClientCommunicatorAdmin extends ClientCommunicatorAdmin {
      public RMIClientCommunicatorAdmin(long var2) {
         super(var2);
      }

      public void gotIOException(IOException var1) throws IOException {
         if (var1 instanceof NoSuchObjectException) {
            super.gotIOException(var1);
         } else {
            try {
               RMIConnector.this.connection.getDefaultDomain((Subject)null);
            } catch (IOException var8) {
               boolean var3 = false;
               synchronized(this) {
                  if (!RMIConnector.this.terminated) {
                     RMIConnector.this.terminated = true;
                     var3 = true;
                  }
               }

               if (var3) {
                  JMXConnectionNotification var4 = new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, (long)(RMIConnector.this.clientNotifSeqNo++), "Failed to communicate with the server: " + var1.toString(), var1);
                  RMIConnector.this.sendNotification(var4);

                  try {
                     RMIConnector.this.close(true);
                  } catch (Exception var6) {
                  }
               }
            }

            if (var1 instanceof ServerException) {
               Throwable var2 = ((ServerException)var1).detail;
               if (var2 instanceof IOException) {
                  throw (IOException)var2;
               }

               if (var2 instanceof RuntimeException) {
                  throw (RuntimeException)var2;
               }
            }

            throw var1;
         }
      }

      public void reconnectNotificationListeners(ClientListenerInfo[] var1) throws IOException {
         int var2 = var1.length;
         ClientListenerInfo[] var4 = new ClientListenerInfo[var2];
         Subject[] var5 = new Subject[var2];
         ObjectName[] var6 = new ObjectName[var2];
         NotificationListener[] var7 = new NotificationListener[var2];
         NotificationFilter[] var8 = new NotificationFilter[var2];
         MarshalledObject[] var9 = (MarshalledObject[])Util.cast(new MarshalledObject[var2]);
         Object[] var10 = new Object[var2];

         int var3;
         for(var3 = 0; var3 < var2; ++var3) {
            var5[var3] = var1[var3].getDelegationSubject();
            var6[var3] = var1[var3].getObjectName();
            var7[var3] = var1[var3].getListener();
            var8[var3] = var1[var3].getNotificationFilter();
            var9[var3] = new MarshalledObject(var8[var3]);
            var10[var3] = var1[var3].getHandback();
         }

         try {
            Integer[] var15 = RMIConnector.this.addListenersWithSubjects(var6, var9, var5, false);

            for(var3 = 0; var3 < var2; ++var3) {
               var4[var3] = new ClientListenerInfo(var15[var3], var6[var3], var7[var3], var8[var3], var10[var3], var5[var3]);
            }

            RMIConnector.this.rmiNotifClient.postReconnection(var4);
         } catch (InstanceNotFoundException var14) {
            int var11 = 0;

            for(var3 = 0; var3 < var2; ++var3) {
               try {
                  Integer var12 = RMIConnector.this.addListenerWithSubject(var6[var3], new MarshalledObject(var8[var3]), var5[var3], false);
                  var4[var11++] = new ClientListenerInfo(var12, var6[var3], var7[var3], var8[var3], var10[var3], var5[var3]);
               } catch (InstanceNotFoundException var13) {
                  RMIConnector.logger.warning("reconnectNotificationListeners", "Can't reconnect listener for " + var6[var3]);
               }
            }

            if (var11 != var2) {
               ClientListenerInfo[] var16 = var4;
               var4 = new ClientListenerInfo[var11];
               System.arraycopy(var16, 0, var4, 0, var11);
            }

            RMIConnector.this.rmiNotifClient.postReconnection(var4);
         }
      }

      protected void checkConnection() throws IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("RMIClientCommunicatorAdmin-checkConnection", "Calling the method getDefaultDomain.");
         }

         RMIConnector.this.connection.getDefaultDomain((Subject)null);
      }

      protected void doStart() throws IOException {
         RMIServer var1;
         try {
            var1 = RMIConnector.this.rmiServer != null ? RMIConnector.this.rmiServer : RMIConnector.this.findRMIServer(RMIConnector.this.jmxServiceURL, RMIConnector.this.env);
         } catch (NamingException var5) {
            throw new IOException("Failed to get a RMI stub: " + var5);
         }

         var1 = RMIConnector.connectStub(var1, RMIConnector.this.env);
         Object var2 = RMIConnector.this.env.get("jmx.remote.credentials");
         RMIConnector.this.connection = var1.newClient(var2);
         ClientListenerInfo[] var3 = RMIConnector.this.rmiNotifClient.preReconnection();
         this.reconnectNotificationListeners(var3);
         RMIConnector.this.connectionId = RMIConnector.this.getConnectionId();
         JMXConnectionNotification var4 = new JMXConnectionNotification("jmx.remote.connection.opened", this, RMIConnector.this.connectionId, (long)(RMIConnector.this.clientNotifSeqNo++), "Reconnected to server", (Object)null);
         RMIConnector.this.sendNotification(var4);
      }

      protected void doStop() {
         try {
            RMIConnector.this.close();
         } catch (IOException var2) {
            RMIConnector.logger.warning("RMIClientCommunicatorAdmin-doStop", "Failed to call the method close():" + var2);
            RMIConnector.logger.debug("RMIClientCommunicatorAdmin-doStop", (Throwable)var2);
         }

      }
   }

   private class RMINotifClient extends ClientNotifForwarder {
      public RMINotifClient(ClassLoader var2, Map<String, ?> var3) {
         super(var2, var3);
      }

      protected NotificationResult fetchNotifs(long var1, int var3, long var4) throws IOException, ClassNotFoundException {
         boolean var6 = false;

         while(true) {
            try {
               return RMIConnector.this.connection.fetchNotifications(var1, var3, var4);
            } catch (IOException var15) {
               IOException var7 = var15;
               this.rethrowDeserializationException(var15);

               try {
                  RMIConnector.this.communicatorAdmin.gotIOException(var7);
               } catch (IOException var14) {
                  boolean var9 = false;
                  synchronized(this) {
                     if (RMIConnector.this.terminated) {
                        throw var7;
                     }

                     if (var6) {
                        var9 = true;
                     }
                  }

                  if (var9) {
                     JMXConnectionNotification var10 = new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, (long)(RMIConnector.this.clientNotifSeqNo++), "Failed to communicate with the server: " + var15.toString(), var15);
                     RMIConnector.this.sendNotification(var10);

                     try {
                        RMIConnector.this.close(true);
                     } catch (Exception var12) {
                     }

                     throw var15;
                  }

                  var6 = true;
               }
            }
         }
      }

      private void rethrowDeserializationException(IOException var1) throws ClassNotFoundException, IOException {
         if (var1 instanceof UnmarshalException) {
            throw var1;
         } else {
            if (var1 instanceof MarshalException) {
               MarshalException var2 = (MarshalException)var1;
               if (var2.detail instanceof NotSerializableException) {
                  throw (NotSerializableException)var2.detail;
               }
            }

         }
      }

      protected Integer addListenerForMBeanRemovedNotif() throws IOException, InstanceNotFoundException {
         NotificationFilterSupport var1 = new NotificationFilterSupport();
         var1.enableType("JMX.mbean.unregistered");
         MarshalledObject var2 = new MarshalledObject(var1);
         ObjectName[] var4 = new ObjectName[]{MBeanServerDelegate.DELEGATE_NAME};
         MarshalledObject[] var5 = (MarshalledObject[])Util.cast(new MarshalledObject[]{var2});
         Subject[] var6 = new Subject[]{null};

         Integer[] var3;
         try {
            var3 = RMIConnector.this.connection.addNotificationListeners(var4, var5, var6);
         } catch (IOException var8) {
            RMIConnector.this.communicatorAdmin.gotIOException(var8);
            var3 = RMIConnector.this.connection.addNotificationListeners(var4, var5, var6);
         }

         return var3[0];
      }

      protected void removeListenerForMBeanRemovedNotif(Integer var1) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
         try {
            RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[]{var1}, (Subject)null);
         } catch (IOException var3) {
            RMIConnector.this.communicatorAdmin.gotIOException(var3);
            RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[]{var1}, (Subject)null);
         }

      }

      protected void lostNotifs(String var1, long var2) {
         JMXConnectionNotification var5 = new JMXConnectionNotification("jmx.remote.connection.notifs.lost", RMIConnector.this, RMIConnector.this.connectionId, (long)(RMIConnector.this.clientNotifCounter++), var1, var2);
         RMIConnector.this.sendNotification(var5);
      }
   }

   private class RemoteMBeanServerConnection implements MBeanServerConnection {
      private Subject delegationSubject;

      public RemoteMBeanServerConnection() {
         this((Subject)null);
      }

      public RemoteMBeanServerConnection(Subject var2) {
         this.delegationSubject = var2;
      }

      public ObjectInstance createMBean(String var1, ObjectName var2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("createMBean(String,ObjectName)", "className=" + var1 + ", name=" + var2);
         }

         ClassLoader var3 = RMIConnector.this.pushDefaultClassLoader();

         ObjectInstance var5;
         try {
            ObjectInstance var4 = RMIConnector.this.connection.createMBean(var1, var2, this.delegationSubject);
            return var4;
         } catch (IOException var9) {
            RMIConnector.this.communicatorAdmin.gotIOException(var9);
            var5 = RMIConnector.this.connection.createMBean(var1, var2, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var3);
         }

         return var5;
      }

      public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName)", "className=" + var1 + ", name=" + var2 + ", loaderName=" + var3 + ")");
         }

         ClassLoader var4 = RMIConnector.this.pushDefaultClassLoader();

         ObjectInstance var6;
         try {
            ObjectInstance var5 = RMIConnector.this.connection.createMBean(var1, var2, var3, this.delegationSubject);
            return var5;
         } catch (IOException var10) {
            RMIConnector.this.communicatorAdmin.gotIOException(var10);
            var6 = RMIConnector.this.connection.createMBean(var1, var2, var3, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var4);
         }

         return var6;
      }

      public ObjectInstance createMBean(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("createMBean(String,ObjectName,Object[],String[])", "className=" + var1 + ", name=" + var2 + ", signature=" + RMIConnector.strings(var4));
         }

         MarshalledObject var5 = new MarshalledObject(var3);
         ClassLoader var6 = RMIConnector.this.pushDefaultClassLoader();

         ObjectInstance var8;
         try {
            ObjectInstance var7 = RMIConnector.this.connection.createMBean(var1, var2, var5, var4, this.delegationSubject);
            return var7;
         } catch (IOException var12) {
            RMIConnector.this.communicatorAdmin.gotIOException(var12);
            var8 = RMIConnector.this.connection.createMBean(var1, var2, var5, var4, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var6);
         }

         return var8;
      }

      public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Object[] var4, String[] var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "className=" + var1 + ", name=" + var2 + ", loaderName=" + var3 + ", signature=" + RMIConnector.strings(var5));
         }

         MarshalledObject var6 = new MarshalledObject(var4);
         ClassLoader var7 = RMIConnector.this.pushDefaultClassLoader();

         ObjectInstance var9;
         try {
            ObjectInstance var8 = RMIConnector.this.connection.createMBean(var1, var2, var3, var6, var5, this.delegationSubject);
            return var8;
         } catch (IOException var13) {
            RMIConnector.this.communicatorAdmin.gotIOException(var13);
            var9 = RMIConnector.this.connection.createMBean(var1, var2, var3, var6, var5, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var7);
         }

         return var9;
      }

      public void unregisterMBean(ObjectName var1) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("unregisterMBean", "name=" + var1);
         }

         ClassLoader var2 = RMIConnector.this.pushDefaultClassLoader();

         try {
            RMIConnector.this.connection.unregisterMBean(var1, this.delegationSubject);
         } catch (IOException var7) {
            RMIConnector.this.communicatorAdmin.gotIOException(var7);
            RMIConnector.this.connection.unregisterMBean(var1, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var2);
         }

      }

      public ObjectInstance getObjectInstance(ObjectName var1) throws InstanceNotFoundException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("getObjectInstance", "name=" + var1);
         }

         ClassLoader var2 = RMIConnector.this.pushDefaultClassLoader();

         ObjectInstance var4;
         try {
            ObjectInstance var3 = RMIConnector.this.connection.getObjectInstance(var1, this.delegationSubject);
            return var3;
         } catch (IOException var8) {
            RMIConnector.this.communicatorAdmin.gotIOException(var8);
            var4 = RMIConnector.this.connection.getObjectInstance(var1, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var2);
         }

         return var4;
      }

      public Set<ObjectInstance> queryMBeans(ObjectName var1, QueryExp var2) throws IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("queryMBeans", "name=" + var1 + ", query=" + var2);
         }

         MarshalledObject var3 = new MarshalledObject(var2);
         ClassLoader var4 = RMIConnector.this.pushDefaultClassLoader();

         Set var6;
         try {
            Set var5 = RMIConnector.this.connection.queryMBeans(var1, var3, this.delegationSubject);
            return var5;
         } catch (IOException var10) {
            RMIConnector.this.communicatorAdmin.gotIOException(var10);
            var6 = RMIConnector.this.connection.queryMBeans(var1, var3, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var4);
         }

         return var6;
      }

      public Set<ObjectName> queryNames(ObjectName var1, QueryExp var2) throws IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("queryNames", "name=" + var1 + ", query=" + var2);
         }

         MarshalledObject var3 = new MarshalledObject(var2);
         ClassLoader var4 = RMIConnector.this.pushDefaultClassLoader();

         Set var6;
         try {
            Set var5 = RMIConnector.this.connection.queryNames(var1, var3, this.delegationSubject);
            return var5;
         } catch (IOException var10) {
            RMIConnector.this.communicatorAdmin.gotIOException(var10);
            var6 = RMIConnector.this.connection.queryNames(var1, var3, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var4);
         }

         return var6;
      }

      public boolean isRegistered(ObjectName var1) throws IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("isRegistered", "name=" + var1);
         }

         ClassLoader var2 = RMIConnector.this.pushDefaultClassLoader();

         boolean var4;
         try {
            boolean var3 = RMIConnector.this.connection.isRegistered(var1, this.delegationSubject);
            return var3;
         } catch (IOException var8) {
            RMIConnector.this.communicatorAdmin.gotIOException(var8);
            var4 = RMIConnector.this.connection.isRegistered(var1, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var2);
         }

         return var4;
      }

      public Integer getMBeanCount() throws IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("getMBeanCount", "");
         }

         ClassLoader var1 = RMIConnector.this.pushDefaultClassLoader();

         Integer var3;
         try {
            Integer var2 = RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
            return var2;
         } catch (IOException var7) {
            RMIConnector.this.communicatorAdmin.gotIOException(var7);
            var3 = RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var1);
         }

         return var3;
      }

      public Object getAttribute(ObjectName var1, String var2) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("getAttribute", "name=" + var1 + ", attribute=" + var2);
         }

         ClassLoader var3 = RMIConnector.this.pushDefaultClassLoader();

         Object var5;
         try {
            Object var4 = RMIConnector.this.connection.getAttribute(var1, var2, this.delegationSubject);
            return var4;
         } catch (IOException var9) {
            RMIConnector.this.communicatorAdmin.gotIOException(var9);
            var5 = RMIConnector.this.connection.getAttribute(var1, var2, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var3);
         }

         return var5;
      }

      public AttributeList getAttributes(ObjectName var1, String[] var2) throws InstanceNotFoundException, ReflectionException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("getAttributes", "name=" + var1 + ", attributes=" + RMIConnector.strings(var2));
         }

         ClassLoader var3 = RMIConnector.this.pushDefaultClassLoader();

         AttributeList var5;
         try {
            AttributeList var4 = RMIConnector.this.connection.getAttributes(var1, var2, this.delegationSubject);
            return var4;
         } catch (IOException var9) {
            RMIConnector.this.communicatorAdmin.gotIOException(var9);
            var5 = RMIConnector.this.connection.getAttributes(var1, var2, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var3);
         }

         return var5;
      }

      public void setAttribute(ObjectName var1, Attribute var2) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("setAttribute", "name=" + var1 + ", attribute name=" + var2.getName());
         }

         MarshalledObject var3 = new MarshalledObject(var2);
         ClassLoader var4 = RMIConnector.this.pushDefaultClassLoader();

         try {
            RMIConnector.this.connection.setAttribute(var1, var3, this.delegationSubject);
         } catch (IOException var9) {
            RMIConnector.this.communicatorAdmin.gotIOException(var9);
            RMIConnector.this.connection.setAttribute(var1, var3, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var4);
         }

      }

      public AttributeList setAttributes(ObjectName var1, AttributeList var2) throws InstanceNotFoundException, ReflectionException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("setAttributes", "name=" + var1 + ", attribute names=" + RMIConnector.getAttributesNames(var2));
         }

         MarshalledObject var3 = new MarshalledObject(var2);
         ClassLoader var4 = RMIConnector.this.pushDefaultClassLoader();

         AttributeList var6;
         try {
            AttributeList var5 = RMIConnector.this.connection.setAttributes(var1, var3, this.delegationSubject);
            return var5;
         } catch (IOException var10) {
            RMIConnector.this.communicatorAdmin.gotIOException(var10);
            var6 = RMIConnector.this.connection.setAttributes(var1, var3, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var4);
         }

         return var6;
      }

      public Object invoke(ObjectName var1, String var2, Object[] var3, String[] var4) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("invoke", "name=" + var1 + ", operationName=" + var2 + ", signature=" + RMIConnector.strings(var4));
         }

         MarshalledObject var5 = new MarshalledObject(var3);
         ClassLoader var6 = RMIConnector.this.pushDefaultClassLoader();

         Object var8;
         try {
            Object var7 = RMIConnector.this.connection.invoke(var1, var2, var5, var4, this.delegationSubject);
            return var7;
         } catch (IOException var12) {
            RMIConnector.this.communicatorAdmin.gotIOException(var12);
            var8 = RMIConnector.this.connection.invoke(var1, var2, var5, var4, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var6);
         }

         return var8;
      }

      public String getDefaultDomain() throws IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("getDefaultDomain", "");
         }

         ClassLoader var1 = RMIConnector.this.pushDefaultClassLoader();

         String var3;
         try {
            String var2 = RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
            return var2;
         } catch (IOException var7) {
            RMIConnector.this.communicatorAdmin.gotIOException(var7);
            var3 = RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var1);
         }

         return var3;
      }

      public String[] getDomains() throws IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("getDomains", "");
         }

         ClassLoader var1 = RMIConnector.this.pushDefaultClassLoader();

         String[] var3;
         try {
            String[] var2 = RMIConnector.this.connection.getDomains(this.delegationSubject);
            return var2;
         } catch (IOException var7) {
            RMIConnector.this.communicatorAdmin.gotIOException(var7);
            var3 = RMIConnector.this.connection.getDomains(this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var1);
         }

         return var3;
      }

      public MBeanInfo getMBeanInfo(ObjectName var1) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("getMBeanInfo", "name=" + var1);
         }

         ClassLoader var2 = RMIConnector.this.pushDefaultClassLoader();

         MBeanInfo var4;
         try {
            MBeanInfo var3 = RMIConnector.this.connection.getMBeanInfo(var1, this.delegationSubject);
            return var3;
         } catch (IOException var8) {
            RMIConnector.this.communicatorAdmin.gotIOException(var8);
            var4 = RMIConnector.this.connection.getMBeanInfo(var1, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var2);
         }

         return var4;
      }

      public boolean isInstanceOf(ObjectName var1, String var2) throws InstanceNotFoundException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("isInstanceOf", "name=" + var1 + ", className=" + var2);
         }

         ClassLoader var3 = RMIConnector.this.pushDefaultClassLoader();

         boolean var5;
         try {
            boolean var4 = RMIConnector.this.connection.isInstanceOf(var1, var2, this.delegationSubject);
            return var4;
         } catch (IOException var9) {
            RMIConnector.this.communicatorAdmin.gotIOException(var9);
            var5 = RMIConnector.this.connection.isInstanceOf(var1, var2, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var3);
         }

         return var5;
      }

      public void addNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + var1 + ", listener=" + var2 + ", filter=" + var3 + ", handback=" + var4);
         }

         MarshalledObject var5 = new MarshalledObject(var3);
         MarshalledObject var6 = new MarshalledObject(var4);
         ClassLoader var7 = RMIConnector.this.pushDefaultClassLoader();

         try {
            RMIConnector.this.connection.addNotificationListener(var1, var2, var5, var6, this.delegationSubject);
         } catch (IOException var12) {
            RMIConnector.this.communicatorAdmin.gotIOException(var12);
            RMIConnector.this.connection.addNotificationListener(var1, var2, var5, var6, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var7);
         }

      }

      public void removeNotificationListener(ObjectName var1, ObjectName var2) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName)", "name=" + var1 + ", listener=" + var2);
         }

         ClassLoader var3 = RMIConnector.this.pushDefaultClassLoader();

         try {
            RMIConnector.this.connection.removeNotificationListener(var1, var2, this.delegationSubject);
         } catch (IOException var8) {
            RMIConnector.this.communicatorAdmin.gotIOException(var8);
            RMIConnector.this.connection.removeNotificationListener(var1, var2, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var3);
         }

      }

      public void removeNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
         if (RMIConnector.logger.debugOn()) {
            RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + var1 + ", listener=" + var2 + ", filter=" + var3 + ", handback=" + var4);
         }

         MarshalledObject var5 = new MarshalledObject(var3);
         MarshalledObject var6 = new MarshalledObject(var4);
         ClassLoader var7 = RMIConnector.this.pushDefaultClassLoader();

         try {
            RMIConnector.this.connection.removeNotificationListener(var1, var2, var5, var6, this.delegationSubject);
         } catch (IOException var12) {
            RMIConnector.this.communicatorAdmin.gotIOException(var12);
            RMIConnector.this.connection.removeNotificationListener(var1, var2, var5, var6, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var7);
         }

      }

      public void addNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, IOException {
         boolean var5 = RMIConnector.logger.debugOn();
         if (var5) {
            RMIConnector.logger.debug("addNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + var1 + ", listener=" + var2 + ", filter=" + var3 + ", handback=" + var4);
         }

         Integer var6 = RMIConnector.this.addListenerWithSubject(var1, new MarshalledObject(var3), this.delegationSubject, true);
         RMIConnector.this.rmiNotifClient.addNotificationListener(var6, var1, var2, var3, var4, this.delegationSubject);
      }

      public void removeNotificationListener(ObjectName var1, NotificationListener var2) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
         boolean var3 = RMIConnector.logger.debugOn();
         if (var3) {
            RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener)", "name=" + var1 + ", listener=" + var2);
         }

         Integer[] var4 = RMIConnector.this.rmiNotifClient.removeNotificationListener(var1, var2);
         if (var3) {
            RMIConnector.logger.debug("removeNotificationListener", "listenerIDs=" + RMIConnector.objects(var4));
         }

         ClassLoader var5 = RMIConnector.this.pushDefaultClassLoader();

         try {
            RMIConnector.this.connection.removeNotificationListeners(var1, var4, this.delegationSubject);
         } catch (IOException var10) {
            RMIConnector.this.communicatorAdmin.gotIOException(var10);
            RMIConnector.this.connection.removeNotificationListeners(var1, var4, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var5);
         }

      }

      public void removeNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
         boolean var5 = RMIConnector.logger.debugOn();
         if (var5) {
            RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + var1 + ", listener=" + var2 + ", filter=" + var3 + ", handback=" + var4);
         }

         Integer var6 = RMIConnector.this.rmiNotifClient.removeNotificationListener(var1, var2, var3, var4);
         if (var5) {
            RMIConnector.logger.debug("removeNotificationListener", "listenerID=" + var6);
         }

         ClassLoader var7 = RMIConnector.this.pushDefaultClassLoader();

         try {
            RMIConnector.this.connection.removeNotificationListeners(var1, new Integer[]{var6}, this.delegationSubject);
         } catch (IOException var12) {
            RMIConnector.this.communicatorAdmin.gotIOException(var12);
            RMIConnector.this.connection.removeNotificationListeners(var1, new Integer[]{var6}, this.delegationSubject);
         } finally {
            RMIConnector.this.popDefaultClassLoader(var7);
         }

      }
   }
}
