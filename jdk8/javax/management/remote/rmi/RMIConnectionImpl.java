package javax.management.remote.rmi;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;
import com.sun.jmx.remote.internal.ServerNotifForwarder;
import com.sun.jmx.remote.security.JMXSubjectDomainCombiner;
import com.sun.jmx.remote.security.SubjectDelegator;
import com.sun.jmx.remote.util.ClassLoaderWithRepository;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import com.sun.jmx.remote.util.OrderClassLoaders;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.UnmarshalException;
import java.rmi.server.Unreferenced;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
import javax.management.MBeanPermission;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;
import javax.management.remote.JMXServerErrorException;
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;

public class RMIConnectionImpl implements RMIConnection, Unreferenced {
   private static final Object[] NO_OBJECTS = new Object[0];
   private static final String[] NO_STRINGS = new String[0];
   private final Subject subject;
   private final SubjectDelegator subjectDelegator;
   private final boolean removeCallerContext;
   private final AccessControlContext acc;
   private final RMIServerImpl rmiServer;
   private final MBeanServer mbeanServer;
   private final ClassLoader defaultClassLoader;
   private final ClassLoader defaultContextClassLoader;
   private final ClassLoaderWithRepository classLoaderWithRepository;
   private boolean terminated = false;
   private final String connectionId;
   private final ServerCommunicatorAdmin serverCommunicatorAdmin;
   private static final int ADD_NOTIFICATION_LISTENERS = 1;
   private static final int ADD_NOTIFICATION_LISTENER_OBJECTNAME = 2;
   private static final int CREATE_MBEAN = 3;
   private static final int CREATE_MBEAN_PARAMS = 4;
   private static final int CREATE_MBEAN_LOADER = 5;
   private static final int CREATE_MBEAN_LOADER_PARAMS = 6;
   private static final int GET_ATTRIBUTE = 7;
   private static final int GET_ATTRIBUTES = 8;
   private static final int GET_DEFAULT_DOMAIN = 9;
   private static final int GET_DOMAINS = 10;
   private static final int GET_MBEAN_COUNT = 11;
   private static final int GET_MBEAN_INFO = 12;
   private static final int GET_OBJECT_INSTANCE = 13;
   private static final int INVOKE = 14;
   private static final int IS_INSTANCE_OF = 15;
   private static final int IS_REGISTERED = 16;
   private static final int QUERY_MBEANS = 17;
   private static final int QUERY_NAMES = 18;
   private static final int REMOVE_NOTIFICATION_LISTENER = 19;
   private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME = 20;
   private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME_FILTER_HANDBACK = 21;
   private static final int SET_ATTRIBUTE = 22;
   private static final int SET_ATTRIBUTES = 23;
   private static final int UNREGISTER_MBEAN = 24;
   private ServerNotifForwarder serverNotifForwarder;
   private Map<String, ?> env;
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectionImpl");

   public RMIConnectionImpl(RMIServerImpl var1, String var2, final ClassLoader var3, Subject var4, Map<String, ?> var5) {
      if (var1 != null && var2 != null) {
         if (var5 == null) {
            var5 = Collections.emptyMap();
         }

         this.rmiServer = var1;
         this.connectionId = var2;
         this.defaultClassLoader = var3;
         this.subjectDelegator = new SubjectDelegator();
         this.subject = var4;
         if (var4 == null) {
            this.acc = null;
            this.removeCallerContext = false;
         } else {
            this.removeCallerContext = SubjectDelegator.checkRemoveCallerContext(var4);
            if (this.removeCallerContext) {
               this.acc = JMXSubjectDomainCombiner.getDomainCombinerContext(var4);
            } else {
               this.acc = JMXSubjectDomainCombiner.getContext(var4);
            }
         }

         this.mbeanServer = var1.getMBeanServer();
         final ClassLoaderRepository var7 = (ClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction<ClassLoaderRepository>() {
            public ClassLoaderRepository run() {
               return RMIConnectionImpl.this.mbeanServer.getClassLoaderRepository();
            }
         }, withPermissions(new MBeanPermission("*", "getClassLoaderRepository")));
         this.classLoaderWithRepository = (ClassLoaderWithRepository)AccessController.doPrivileged(new PrivilegedAction<ClassLoaderWithRepository>() {
            public ClassLoaderWithRepository run() {
               return new ClassLoaderWithRepository(var7, var3);
            }
         }, withPermissions(new RuntimePermission("createClassLoader")));
         this.defaultContextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               return new RMIConnectionImpl.CombinedClassLoader(Thread.currentThread().getContextClassLoader(), var3);
            }
         });
         this.serverCommunicatorAdmin = new RMIConnectionImpl.RMIServerCommunicatorAdmin(EnvHelp.getServerConnectionTimeout(var5));
         this.env = var5;
      } else {
         throw new NullPointerException("Illegal null argument");
      }
   }

   private static AccessControlContext withPermissions(Permission... var0) {
      Permissions var1 = new Permissions();
      Permission[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Permission var5 = var2[var4];
         var1.add(var5);
      }

      ProtectionDomain var6 = new ProtectionDomain((CodeSource)null, var1);
      return new AccessControlContext(new ProtectionDomain[]{var6});
   }

   private synchronized ServerNotifForwarder getServerNotifFwd() {
      if (this.serverNotifForwarder == null) {
         this.serverNotifForwarder = new ServerNotifForwarder(this.mbeanServer, this.env, this.rmiServer.getNotifBuffer(), this.connectionId);
      }

      return this.serverNotifForwarder;
   }

   public String getConnectionId() throws IOException {
      return this.connectionId;
   }

   public void close() throws IOException {
      boolean var1 = logger.debugOn();
      String var2 = var1 ? "[" + this.toString() + "]" : null;
      synchronized(this) {
         if (this.terminated) {
            if (var1) {
               logger.debug("close", var2 + " already terminated.");
            }

            return;
         }

         if (var1) {
            logger.debug("close", var2 + " closing.");
         }

         this.terminated = true;
         if (this.serverCommunicatorAdmin != null) {
            this.serverCommunicatorAdmin.terminate();
         }

         if (this.serverNotifForwarder != null) {
            this.serverNotifForwarder.terminate();
         }
      }

      this.rmiServer.clientClosed(this);
      if (var1) {
         logger.debug("close", var2 + " closed.");
      }

   }

   public void unreferenced() {
      logger.debug("unreferenced", "called");

      try {
         this.close();
         logger.debug("unreferenced", "done");
      } catch (IOException var2) {
         logger.fine("unreferenced", (Throwable)var2);
      }

   }

   public ObjectInstance createMBean(String var1, ObjectName var2, Subject var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
      try {
         Object[] var4 = new Object[]{var1, var2};
         if (logger.debugOn()) {
            logger.debug("createMBean(String,ObjectName)", "connectionId=" + this.connectionId + ", className=" + var1 + ", name=" + var2);
         }

         return (ObjectInstance)this.doPrivilegedOperation(3, var4, var3);
      } catch (PrivilegedActionException var6) {
         Exception var5 = extractException(var6);
         if (var5 instanceof ReflectionException) {
            throw (ReflectionException)var5;
         } else if (var5 instanceof InstanceAlreadyExistsException) {
            throw (InstanceAlreadyExistsException)var5;
         } else if (var5 instanceof MBeanRegistrationException) {
            throw (MBeanRegistrationException)var5;
         } else if (var5 instanceof MBeanException) {
            throw (MBeanException)var5;
         } else if (var5 instanceof NotCompliantMBeanException) {
            throw (NotCompliantMBeanException)var5;
         } else if (var5 instanceof IOException) {
            throw (IOException)var5;
         } else {
            throw newIOException("Got unexpected server exception: " + var5, var5);
         }
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Subject var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
      try {
         Object[] var5 = new Object[]{var1, var2, var3};
         if (logger.debugOn()) {
            logger.debug("createMBean(String,ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", className=" + var1 + ", name=" + var2 + ", loaderName=" + var3);
         }

         return (ObjectInstance)this.doPrivilegedOperation(5, var5, var4);
      } catch (PrivilegedActionException var7) {
         Exception var6 = extractException(var7);
         if (var6 instanceof ReflectionException) {
            throw (ReflectionException)var6;
         } else if (var6 instanceof InstanceAlreadyExistsException) {
            throw (InstanceAlreadyExistsException)var6;
         } else if (var6 instanceof MBeanRegistrationException) {
            throw (MBeanRegistrationException)var6;
         } else if (var6 instanceof MBeanException) {
            throw (MBeanException)var6;
         } else if (var6 instanceof NotCompliantMBeanException) {
            throw (NotCompliantMBeanException)var6;
         } else if (var6 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var6;
         } else if (var6 instanceof IOException) {
            throw (IOException)var6;
         } else {
            throw newIOException("Got unexpected server exception: " + var6, var6);
         }
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, MarshalledObject var3, String[] var4, Subject var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
      boolean var7 = logger.debugOn();
      if (var7) {
         logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping parameters using classLoaderWithRepository.");
      }

      Object[] var6 = nullIsEmpty((Object[])this.unwrap(var3, this.classLoaderWithRepository, Object[].class, var5));

      try {
         Object[] var8 = new Object[]{var1, var2, var6, nullIsEmpty(var4)};
         if (var7) {
            logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + var1 + ", name=" + var2 + ", signature=" + strings(var4));
         }

         return (ObjectInstance)this.doPrivilegedOperation(4, var8, var5);
      } catch (PrivilegedActionException var10) {
         Exception var9 = extractException(var10);
         if (var9 instanceof ReflectionException) {
            throw (ReflectionException)var9;
         } else if (var9 instanceof InstanceAlreadyExistsException) {
            throw (InstanceAlreadyExistsException)var9;
         } else if (var9 instanceof MBeanRegistrationException) {
            throw (MBeanRegistrationException)var9;
         } else if (var9 instanceof MBeanException) {
            throw (MBeanException)var9;
         } else if (var9 instanceof NotCompliantMBeanException) {
            throw (NotCompliantMBeanException)var9;
         } else if (var9 instanceof IOException) {
            throw (IOException)var9;
         } else {
            throw newIOException("Got unexpected server exception: " + var9, var9);
         }
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, MarshalledObject var4, String[] var5, Subject var6) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
      boolean var8 = logger.debugOn();
      if (var8) {
         logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping params with MBean extended ClassLoader.");
      }

      Object[] var7 = nullIsEmpty((Object[])this.unwrap(var4, this.getClassLoader(var3), this.defaultClassLoader, Object[].class, var6));

      try {
         Object[] var9 = new Object[]{var1, var2, var3, var7, nullIsEmpty(var5)};
         if (var8) {
            logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + var1 + ", name=" + var2 + ", loaderName=" + var3 + ", signature=" + strings(var5));
         }

         return (ObjectInstance)this.doPrivilegedOperation(6, var9, var6);
      } catch (PrivilegedActionException var11) {
         Exception var10 = extractException(var11);
         if (var10 instanceof ReflectionException) {
            throw (ReflectionException)var10;
         } else if (var10 instanceof InstanceAlreadyExistsException) {
            throw (InstanceAlreadyExistsException)var10;
         } else if (var10 instanceof MBeanRegistrationException) {
            throw (MBeanRegistrationException)var10;
         } else if (var10 instanceof MBeanException) {
            throw (MBeanException)var10;
         } else if (var10 instanceof NotCompliantMBeanException) {
            throw (NotCompliantMBeanException)var10;
         } else if (var10 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var10;
         } else if (var10 instanceof IOException) {
            throw (IOException)var10;
         } else {
            throw newIOException("Got unexpected server exception: " + var10, var10);
         }
      }
   }

   public void unregisterMBean(ObjectName var1, Subject var2) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
      try {
         Object[] var3 = new Object[]{var1};
         if (logger.debugOn()) {
            logger.debug("unregisterMBean", "connectionId=" + this.connectionId + ", name=" + var1);
         }

         this.doPrivilegedOperation(24, var3, var2);
      } catch (PrivilegedActionException var5) {
         Exception var4 = extractException(var5);
         if (var4 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var4;
         } else if (var4 instanceof MBeanRegistrationException) {
            throw (MBeanRegistrationException)var4;
         } else if (var4 instanceof IOException) {
            throw (IOException)var4;
         } else {
            throw newIOException("Got unexpected server exception: " + var4, var4);
         }
      }
   }

   public ObjectInstance getObjectInstance(ObjectName var1, Subject var2) throws InstanceNotFoundException, IOException {
      checkNonNull("ObjectName", var1);

      try {
         Object[] var3 = new Object[]{var1};
         if (logger.debugOn()) {
            logger.debug("getObjectInstance", "connectionId=" + this.connectionId + ", name=" + var1);
         }

         return (ObjectInstance)this.doPrivilegedOperation(13, var3, var2);
      } catch (PrivilegedActionException var5) {
         Exception var4 = extractException(var5);
         if (var4 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var4;
         } else if (var4 instanceof IOException) {
            throw (IOException)var4;
         } else {
            throw newIOException("Got unexpected server exception: " + var4, var4);
         }
      }
   }

   public Set<ObjectInstance> queryMBeans(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException {
      boolean var5 = logger.debugOn();
      if (var5) {
         logger.debug("queryMBeans", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader.");
      }

      QueryExp var4 = (QueryExp)this.unwrap(var2, this.defaultContextClassLoader, QueryExp.class, var3);

      try {
         Object[] var6 = new Object[]{var1, var4};
         if (var5) {
            logger.debug("queryMBeans", "connectionId=" + this.connectionId + ", name=" + var1 + ", query=" + var2);
         }

         return (Set)Util.cast(this.doPrivilegedOperation(17, var6, var3));
      } catch (PrivilegedActionException var8) {
         Exception var7 = extractException(var8);
         if (var7 instanceof IOException) {
            throw (IOException)var7;
         } else {
            throw newIOException("Got unexpected server exception: " + var7, var7);
         }
      }
   }

   public Set<ObjectName> queryNames(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException {
      boolean var5 = logger.debugOn();
      if (var5) {
         logger.debug("queryNames", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader.");
      }

      QueryExp var4 = (QueryExp)this.unwrap(var2, this.defaultContextClassLoader, QueryExp.class, var3);

      try {
         Object[] var6 = new Object[]{var1, var4};
         if (var5) {
            logger.debug("queryNames", "connectionId=" + this.connectionId + ", name=" + var1 + ", query=" + var2);
         }

         return (Set)Util.cast(this.doPrivilegedOperation(18, var6, var3));
      } catch (PrivilegedActionException var8) {
         Exception var7 = extractException(var8);
         if (var7 instanceof IOException) {
            throw (IOException)var7;
         } else {
            throw newIOException("Got unexpected server exception: " + var7, var7);
         }
      }
   }

   public boolean isRegistered(ObjectName var1, Subject var2) throws IOException {
      try {
         Object[] var3 = new Object[]{var1};
         return (Boolean)this.doPrivilegedOperation(16, var3, var2);
      } catch (PrivilegedActionException var5) {
         Exception var4 = extractException(var5);
         if (var4 instanceof IOException) {
            throw (IOException)var4;
         } else {
            throw newIOException("Got unexpected server exception: " + var4, var4);
         }
      }
   }

   public Integer getMBeanCount(Subject var1) throws IOException {
      try {
         Object[] var2 = new Object[0];
         if (logger.debugOn()) {
            logger.debug("getMBeanCount", "connectionId=" + this.connectionId);
         }

         return (Integer)this.doPrivilegedOperation(11, var2, var1);
      } catch (PrivilegedActionException var4) {
         Exception var3 = extractException(var4);
         if (var3 instanceof IOException) {
            throw (IOException)var3;
         } else {
            throw newIOException("Got unexpected server exception: " + var3, var3);
         }
      }
   }

   public Object getAttribute(ObjectName var1, String var2, Subject var3) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
      try {
         Object[] var4 = new Object[]{var1, var2};
         if (logger.debugOn()) {
            logger.debug("getAttribute", "connectionId=" + this.connectionId + ", name=" + var1 + ", attribute=" + var2);
         }

         return this.doPrivilegedOperation(7, var4, var3);
      } catch (PrivilegedActionException var6) {
         Exception var5 = extractException(var6);
         if (var5 instanceof MBeanException) {
            throw (MBeanException)var5;
         } else if (var5 instanceof AttributeNotFoundException) {
            throw (AttributeNotFoundException)var5;
         } else if (var5 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var5;
         } else if (var5 instanceof ReflectionException) {
            throw (ReflectionException)var5;
         } else if (var5 instanceof IOException) {
            throw (IOException)var5;
         } else {
            throw newIOException("Got unexpected server exception: " + var5, var5);
         }
      }
   }

   public AttributeList getAttributes(ObjectName var1, String[] var2, Subject var3) throws InstanceNotFoundException, ReflectionException, IOException {
      try {
         Object[] var4 = new Object[]{var1, var2};
         if (logger.debugOn()) {
            logger.debug("getAttributes", "connectionId=" + this.connectionId + ", name=" + var1 + ", attributes=" + strings(var2));
         }

         return (AttributeList)this.doPrivilegedOperation(8, var4, var3);
      } catch (PrivilegedActionException var6) {
         Exception var5 = extractException(var6);
         if (var5 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var5;
         } else if (var5 instanceof ReflectionException) {
            throw (ReflectionException)var5;
         } else if (var5 instanceof IOException) {
            throw (IOException)var5;
         } else {
            throw newIOException("Got unexpected server exception: " + var5, var5);
         }
      }
   }

   public void setAttribute(ObjectName var1, MarshalledObject var2, Subject var3) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
      boolean var5 = logger.debugOn();
      if (var5) {
         logger.debug("setAttribute", "connectionId=" + this.connectionId + " unwrapping attribute with MBean extended ClassLoader.");
      }

      Attribute var4 = (Attribute)this.unwrap(var2, this.getClassLoaderFor(var1), this.defaultClassLoader, Attribute.class, var3);

      try {
         Object[] var6 = new Object[]{var1, var4};
         if (var5) {
            logger.debug("setAttribute", "connectionId=" + this.connectionId + ", name=" + var1 + ", attribute name=" + var4.getName());
         }

         this.doPrivilegedOperation(22, var6, var3);
      } catch (PrivilegedActionException var8) {
         Exception var7 = extractException(var8);
         if (var7 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var7;
         } else if (var7 instanceof AttributeNotFoundException) {
            throw (AttributeNotFoundException)var7;
         } else if (var7 instanceof InvalidAttributeValueException) {
            throw (InvalidAttributeValueException)var7;
         } else if (var7 instanceof MBeanException) {
            throw (MBeanException)var7;
         } else if (var7 instanceof ReflectionException) {
            throw (ReflectionException)var7;
         } else if (var7 instanceof IOException) {
            throw (IOException)var7;
         } else {
            throw newIOException("Got unexpected server exception: " + var7, var7);
         }
      }
   }

   public AttributeList setAttributes(ObjectName var1, MarshalledObject var2, Subject var3) throws InstanceNotFoundException, ReflectionException, IOException {
      boolean var5 = logger.debugOn();
      if (var5) {
         logger.debug("setAttributes", "connectionId=" + this.connectionId + " unwrapping attributes with MBean extended ClassLoader.");
      }

      AttributeList var4 = (AttributeList)this.unwrap(var2, this.getClassLoaderFor(var1), this.defaultClassLoader, AttributeList.class, var3);

      try {
         Object[] var6 = new Object[]{var1, var4};
         if (var5) {
            logger.debug("setAttributes", "connectionId=" + this.connectionId + ", name=" + var1 + ", attribute names=" + RMIConnector.getAttributesNames(var4));
         }

         return (AttributeList)this.doPrivilegedOperation(23, var6, var3);
      } catch (PrivilegedActionException var8) {
         Exception var7 = extractException(var8);
         if (var7 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var7;
         } else if (var7 instanceof ReflectionException) {
            throw (ReflectionException)var7;
         } else if (var7 instanceof IOException) {
            throw (IOException)var7;
         } else {
            throw newIOException("Got unexpected server exception: " + var7, var7);
         }
      }
   }

   public Object invoke(ObjectName var1, String var2, MarshalledObject var3, String[] var4, Subject var5) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
      checkNonNull("ObjectName", var1);
      checkNonNull("Operation name", var2);
      boolean var7 = logger.debugOn();
      if (var7) {
         logger.debug("invoke", "connectionId=" + this.connectionId + " unwrapping params with MBean extended ClassLoader.");
      }

      Object[] var6 = nullIsEmpty((Object[])this.unwrap(var3, this.getClassLoaderFor(var1), this.defaultClassLoader, Object[].class, var5));

      try {
         Object[] var8 = new Object[]{var1, var2, var6, nullIsEmpty(var4)};
         if (var7) {
            logger.debug("invoke", "connectionId=" + this.connectionId + ", name=" + var1 + ", operationName=" + var2 + ", signature=" + strings(var4));
         }

         return this.doPrivilegedOperation(14, var8, var5);
      } catch (PrivilegedActionException var10) {
         Exception var9 = extractException(var10);
         if (var9 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var9;
         } else if (var9 instanceof MBeanException) {
            throw (MBeanException)var9;
         } else if (var9 instanceof ReflectionException) {
            throw (ReflectionException)var9;
         } else if (var9 instanceof IOException) {
            throw (IOException)var9;
         } else {
            throw newIOException("Got unexpected server exception: " + var9, var9);
         }
      }
   }

   public String getDefaultDomain(Subject var1) throws IOException {
      try {
         Object[] var2 = new Object[0];
         if (logger.debugOn()) {
            logger.debug("getDefaultDomain", "connectionId=" + this.connectionId);
         }

         return (String)this.doPrivilegedOperation(9, var2, var1);
      } catch (PrivilegedActionException var4) {
         Exception var3 = extractException(var4);
         if (var3 instanceof IOException) {
            throw (IOException)var3;
         } else {
            throw newIOException("Got unexpected server exception: " + var3, var3);
         }
      }
   }

   public String[] getDomains(Subject var1) throws IOException {
      try {
         Object[] var2 = new Object[0];
         if (logger.debugOn()) {
            logger.debug("getDomains", "connectionId=" + this.connectionId);
         }

         return (String[])((String[])this.doPrivilegedOperation(10, var2, var1));
      } catch (PrivilegedActionException var4) {
         Exception var3 = extractException(var4);
         if (var3 instanceof IOException) {
            throw (IOException)var3;
         } else {
            throw newIOException("Got unexpected server exception: " + var3, var3);
         }
      }
   }

   public MBeanInfo getMBeanInfo(ObjectName var1, Subject var2) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
      checkNonNull("ObjectName", var1);

      try {
         Object[] var3 = new Object[]{var1};
         if (logger.debugOn()) {
            logger.debug("getMBeanInfo", "connectionId=" + this.connectionId + ", name=" + var1);
         }

         return (MBeanInfo)this.doPrivilegedOperation(12, var3, var2);
      } catch (PrivilegedActionException var5) {
         Exception var4 = extractException(var5);
         if (var4 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var4;
         } else if (var4 instanceof IntrospectionException) {
            throw (IntrospectionException)var4;
         } else if (var4 instanceof ReflectionException) {
            throw (ReflectionException)var4;
         } else if (var4 instanceof IOException) {
            throw (IOException)var4;
         } else {
            throw newIOException("Got unexpected server exception: " + var4, var4);
         }
      }
   }

   public boolean isInstanceOf(ObjectName var1, String var2, Subject var3) throws InstanceNotFoundException, IOException {
      checkNonNull("ObjectName", var1);

      try {
         Object[] var4 = new Object[]{var1, var2};
         if (logger.debugOn()) {
            logger.debug("isInstanceOf", "connectionId=" + this.connectionId + ", name=" + var1 + ", className=" + var2);
         }

         return (Boolean)this.doPrivilegedOperation(15, var4, var3);
      } catch (PrivilegedActionException var6) {
         Exception var5 = extractException(var6);
         if (var5 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var5;
         } else if (var5 instanceof IOException) {
            throw (IOException)var5;
         } else {
            throw newIOException("Got unexpected server exception: " + var5, var5);
         }
      }
   }

   public Integer[] addNotificationListeners(ObjectName[] var1, MarshalledObject[] var2, Subject[] var3) throws InstanceNotFoundException, IOException {
      if (var1 != null && var2 != null) {
         Subject[] var4 = var3 != null ? var3 : new Subject[var1.length];
         if (var1.length == var2.length && var2.length == var4.length) {
            int var5;
            for(var5 = 0; var5 < var1.length; ++var5) {
               if (var1[var5] == null) {
                  throw new IllegalArgumentException("Null Object name.");
               }
            }

            var5 = 0;
            NotificationFilter[] var7 = new NotificationFilter[var1.length];
            Integer[] var8 = new Integer[var1.length];
            boolean var9 = logger.debugOn();

            try {
               while(var5 < var1.length) {
                  ClassLoader var6 = this.getClassLoaderFor(var1[var5]);
                  if (var9) {
                     logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
                  }

                  var7[var5] = (NotificationFilter)this.unwrap(var2[var5], var6, this.defaultClassLoader, NotificationFilter.class, var4[var5]);
                  if (var9) {
                     logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + ", name=" + var1[var5] + ", filter=" + var7[var5]);
                  }

                  var8[var5] = (Integer)this.doPrivilegedOperation(1, new Object[]{var1[var5], var7[var5]}, var4[var5]);
                  ++var5;
               }

               return var8;
            } catch (Exception var14) {
               Exception var10 = var14;

               for(int var11 = 0; var11 < var5; ++var11) {
                  try {
                     this.getServerNotifFwd().removeNotificationListener(var1[var11], var8[var11]);
                  } catch (Exception var13) {
                  }
               }

               if (var14 instanceof PrivilegedActionException) {
                  var10 = extractException(var14);
               }

               if (var10 instanceof ClassCastException) {
                  throw (ClassCastException)var10;
               } else if (var10 instanceof IOException) {
                  throw (IOException)var10;
               } else if (var10 instanceof InstanceNotFoundException) {
                  throw (InstanceNotFoundException)var10;
               } else if (var10 instanceof RuntimeException) {
                  throw (RuntimeException)var10;
               } else {
                  throw newIOException("Got unexpected server exception: " + var10, var10);
               }
            }
         } else {
            throw new IllegalArgumentException("The value lengths of 3 parameters are not same.");
         }
      } else {
         throw new IllegalArgumentException("Got null arguments.");
      }
   }

   public void addNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws InstanceNotFoundException, IOException {
      checkNonNull("Target MBean name", var1);
      checkNonNull("Listener MBean name", var2);
      boolean var8 = logger.debugOn();
      ClassLoader var9 = this.getClassLoaderFor(var1);
      if (var8) {
         logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
      }

      NotificationFilter var6 = (NotificationFilter)this.unwrap(var3, var9, this.defaultClassLoader, NotificationFilter.class, var5);
      if (var8) {
         logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader.");
      }

      Object var7 = this.unwrap(var4, var9, this.defaultClassLoader, Object.class, var5);

      try {
         Object[] var10 = new Object[]{var1, var2, var6, var7};
         if (var8) {
            logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + var1 + ", listenerName=" + var2 + ", filter=" + var6 + ", handback=" + var7);
         }

         this.doPrivilegedOperation(2, var10, var5);
      } catch (PrivilegedActionException var12) {
         Exception var11 = extractException(var12);
         if (var11 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var11;
         } else if (var11 instanceof IOException) {
            throw (IOException)var11;
         } else {
            throw newIOException("Got unexpected server exception: " + var11, var11);
         }
      }
   }

   public void removeNotificationListeners(ObjectName var1, Integer[] var2, Subject var3) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      if (var1 != null && var2 != null) {
         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var2[var4] == null) {
               throw new IllegalArgumentException("Null listener ID");
            }
         }

         try {
            Object[] var7 = new Object[]{var1, var2};
            if (logger.debugOn()) {
               logger.debug("removeNotificationListener(ObjectName,Integer[])", "connectionId=" + this.connectionId + ", name=" + var1 + ", listenerIDs=" + objects(var2));
            }

            this.doPrivilegedOperation(19, var7, var3);
         } catch (PrivilegedActionException var6) {
            Exception var5 = extractException(var6);
            if (var5 instanceof InstanceNotFoundException) {
               throw (InstanceNotFoundException)var5;
            } else if (var5 instanceof ListenerNotFoundException) {
               throw (ListenerNotFoundException)var5;
            } else if (var5 instanceof IOException) {
               throw (IOException)var5;
            } else {
               throw newIOException("Got unexpected server exception: " + var5, var5);
            }
         }
      } else {
         throw new IllegalArgumentException("Illegal null parameter");
      }
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, Subject var3) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      checkNonNull("Target MBean name", var1);
      checkNonNull("Listener MBean name", var2);

      try {
         Object[] var4 = new Object[]{var1, var2};
         if (logger.debugOn()) {
            logger.debug("removeNotificationListener(ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", name=" + var1 + ", listenerName=" + var2);
         }

         this.doPrivilegedOperation(20, var4, var3);
      } catch (PrivilegedActionException var6) {
         Exception var5 = extractException(var6);
         if (var5 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var5;
         } else if (var5 instanceof ListenerNotFoundException) {
            throw (ListenerNotFoundException)var5;
         } else if (var5 instanceof IOException) {
            throw (IOException)var5;
         } else {
            throw newIOException("Got unexpected server exception: " + var5, var5);
         }
      }
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      checkNonNull("Target MBean name", var1);
      checkNonNull("Listener MBean name", var2);
      boolean var8 = logger.debugOn();
      ClassLoader var9 = this.getClassLoaderFor(var1);
      if (var8) {
         logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
      }

      NotificationFilter var6 = (NotificationFilter)this.unwrap(var3, var9, this.defaultClassLoader, NotificationFilter.class, var5);
      if (var8) {
         logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader.");
      }

      Object var7 = this.unwrap(var4, var9, this.defaultClassLoader, Object.class, var5);

      try {
         Object[] var10 = new Object[]{var1, var2, var6, var7};
         if (var8) {
            logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + var1 + ", listenerName=" + var2 + ", filter=" + var6 + ", handback=" + var7);
         }

         this.doPrivilegedOperation(21, var10, var5);
      } catch (PrivilegedActionException var12) {
         Exception var11 = extractException(var12);
         if (var11 instanceof InstanceNotFoundException) {
            throw (InstanceNotFoundException)var11;
         } else if (var11 instanceof ListenerNotFoundException) {
            throw (ListenerNotFoundException)var11;
         } else if (var11 instanceof IOException) {
            throw (IOException)var11;
         } else {
            throw newIOException("Got unexpected server exception: " + var11, var11);
         }
      }
   }

   public NotificationResult fetchNotifications(final long var1, final int var3, final long var4) throws IOException {
      if (logger.debugOn()) {
         logger.debug("fetchNotifications", "connectionId=" + this.connectionId + ", timeout=" + var4);
      }

      if (var3 >= 0 && var4 >= 0L) {
         boolean var6 = this.serverCommunicatorAdmin.reqIncoming();

         NotificationResult var13;
         try {
            if (var6) {
               if (logger.debugOn()) {
                  logger.debug("fetchNotifications", "The notification server has been closed, returns null to force the client to stop fetching");
               }

               Object var7 = null;
               return (NotificationResult)var7;
            }

            PrivilegedAction var12 = new PrivilegedAction<NotificationResult>() {
               public NotificationResult run() {
                  return RMIConnectionImpl.this.getServerNotifFwd().fetchNotifs(var1, var4, var3);
               }
            };
            if (this.acc != null) {
               var13 = (NotificationResult)AccessController.doPrivileged(var12, this.acc);
               return var13;
            }

            var13 = (NotificationResult)var12.run();
         } finally {
            this.serverCommunicatorAdmin.rspOutgoing();
         }

         return var13;
      } else {
         throw new IllegalArgumentException("Illegal negative argument");
      }
   }

   public String toString() {
      return super.toString() + ": connectionId=" + this.connectionId;
   }

   private ClassLoader getClassLoader(final ObjectName var1) throws InstanceNotFoundException {
      try {
         return (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
            public ClassLoader run() throws InstanceNotFoundException {
               return RMIConnectionImpl.this.mbeanServer.getClassLoader(var1);
            }
         }, withPermissions(new MBeanPermission("*", "getClassLoader")));
      } catch (PrivilegedActionException var3) {
         throw (InstanceNotFoundException)extractException(var3);
      }
   }

   private ClassLoader getClassLoaderFor(final ObjectName var1) throws InstanceNotFoundException {
      try {
         return (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() throws InstanceNotFoundException {
               return RMIConnectionImpl.this.mbeanServer.getClassLoaderFor(var1);
            }
         }, withPermissions(new MBeanPermission("*", "getClassLoaderFor")));
      } catch (PrivilegedActionException var3) {
         throw (InstanceNotFoundException)extractException(var3);
      }
   }

   private Object doPrivilegedOperation(int var1, Object[] var2, Subject var3) throws PrivilegedActionException, IOException {
      this.serverCommunicatorAdmin.reqIncoming();

      Object var6;
      try {
         AccessControlContext var4;
         if (var3 == null) {
            var4 = this.acc;
         } else {
            if (this.subject == null) {
               throw new SecurityException("Subject delegation cannot be enabled unless an authenticated subject is put in place");
            }

            var4 = this.subjectDelegator.delegatedContext(this.acc, var3, this.removeCallerContext);
         }

         RMIConnectionImpl.PrivilegedOperation var5 = new RMIConnectionImpl.PrivilegedOperation(var1, var2);
         if (var4 == null) {
            try {
               var6 = var5.run();
               return var6;
            } catch (Exception var11) {
               if (var11 instanceof RuntimeException) {
                  throw (RuntimeException)var11;
               }

               throw new PrivilegedActionException(var11);
            }
         }

         var6 = AccessController.doPrivileged((PrivilegedExceptionAction)var5, var4);
      } catch (Error var12) {
         throw new JMXServerErrorException(var12.toString(), var12);
      } finally {
         this.serverCommunicatorAdmin.rspOutgoing();
      }

      return var6;
   }

   private Object doOperation(int var1, Object[] var2) throws Exception {
      switch(var1) {
      case 1:
         return this.getServerNotifFwd().addNotificationListener((ObjectName)var2[0], (NotificationFilter)var2[1]);
      case 2:
         this.mbeanServer.addNotificationListener((ObjectName)var2[0], (ObjectName)var2[1], (NotificationFilter)var2[2], var2[3]);
         return null;
      case 3:
         return this.mbeanServer.createMBean((String)var2[0], (ObjectName)var2[1]);
      case 4:
         return this.mbeanServer.createMBean((String)var2[0], (ObjectName)var2[1], (Object[])((Object[])var2[2]), (String[])((String[])var2[3]));
      case 5:
         return this.mbeanServer.createMBean((String)var2[0], (ObjectName)var2[1], (ObjectName)var2[2]);
      case 6:
         return this.mbeanServer.createMBean((String)var2[0], (ObjectName)var2[1], (ObjectName)var2[2], (Object[])((Object[])var2[3]), (String[])((String[])var2[4]));
      case 7:
         return this.mbeanServer.getAttribute((ObjectName)var2[0], (String)var2[1]);
      case 8:
         return this.mbeanServer.getAttributes((ObjectName)var2[0], (String[])((String[])var2[1]));
      case 9:
         return this.mbeanServer.getDefaultDomain();
      case 10:
         return this.mbeanServer.getDomains();
      case 11:
         return this.mbeanServer.getMBeanCount();
      case 12:
         return this.mbeanServer.getMBeanInfo((ObjectName)var2[0]);
      case 13:
         return this.mbeanServer.getObjectInstance((ObjectName)var2[0]);
      case 14:
         return this.mbeanServer.invoke((ObjectName)var2[0], (String)var2[1], (Object[])((Object[])var2[2]), (String[])((String[])var2[3]));
      case 15:
         return this.mbeanServer.isInstanceOf((ObjectName)var2[0], (String)var2[1]) ? Boolean.TRUE : Boolean.FALSE;
      case 16:
         return this.mbeanServer.isRegistered((ObjectName)var2[0]) ? Boolean.TRUE : Boolean.FALSE;
      case 17:
         return this.mbeanServer.queryMBeans((ObjectName)var2[0], (QueryExp)var2[1]);
      case 18:
         return this.mbeanServer.queryNames((ObjectName)var2[0], (QueryExp)var2[1]);
      case 19:
         this.getServerNotifFwd().removeNotificationListener((ObjectName)var2[0], (Integer[])((Integer[])var2[1]));
         return null;
      case 20:
         this.mbeanServer.removeNotificationListener((ObjectName)var2[0], (ObjectName)var2[1]);
         return null;
      case 21:
         this.mbeanServer.removeNotificationListener((ObjectName)var2[0], (ObjectName)var2[1], (NotificationFilter)var2[2], var2[3]);
         return null;
      case 22:
         this.mbeanServer.setAttribute((ObjectName)var2[0], (Attribute)var2[1]);
         return null;
      case 23:
         return this.mbeanServer.setAttributes((ObjectName)var2[0], (AttributeList)var2[1]);
      case 24:
         this.mbeanServer.unregisterMBean((ObjectName)var2[0]);
         return null;
      default:
         throw new IllegalArgumentException("Invalid operation");
      }
   }

   private <T> T unwrap(MarshalledObject<?> var1, ClassLoader var2, Class<T> var3, Subject var4) throws IOException {
      if (var1 == null) {
         return null;
      } else {
         try {
            ClassLoader var5 = (ClassLoader)AccessController.doPrivileged((PrivilegedExceptionAction)(new RMIConnectionImpl.SetCcl(var2)));

            Object var7;
            try {
               AccessControlContext var15;
               if (var4 == null) {
                  var15 = this.acc;
               } else {
                  if (this.subject == null) {
                     throw new SecurityException("Subject delegation cannot be enabled unless an authenticated subject is put in place");
                  }

                  var15 = this.subjectDelegator.delegatedContext(this.acc, var4, this.removeCallerContext);
               }

               if (var15 == null) {
                  var7 = var3.cast(var1.get());
                  return var7;
               }

               var7 = AccessController.doPrivileged(() -> {
                  return var3.cast(var1.get());
               }, var15);
            } finally {
               AccessController.doPrivileged((PrivilegedExceptionAction)(new RMIConnectionImpl.SetCcl(var5)));
            }

            return var7;
         } catch (PrivilegedActionException var13) {
            Exception var6 = extractException(var13);
            if (var6 instanceof IOException) {
               throw (IOException)var6;
            } else if (var6 instanceof ClassNotFoundException) {
               throw new UnmarshalException(var6.toString(), var6);
            } else {
               logger.warning("unwrap", "Failed to unmarshall object: " + var6);
               logger.debug("unwrap", (Throwable)var6);
               return null;
            }
         } catch (ClassNotFoundException var14) {
            logger.warning("unwrap", "Failed to unmarshall object: " + var14);
            logger.debug("unwrap", (Throwable)var14);
            throw new UnmarshalException(var14.toString(), var14);
         }
      }
   }

   private <T> T unwrap(MarshalledObject<?> var1, final ClassLoader var2, final ClassLoader var3, Class<T> var4, Subject var5) throws IOException {
      if (var1 == null) {
         return null;
      } else {
         try {
            ClassLoader var6 = (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
               public ClassLoader run() throws Exception {
                  return new RMIConnectionImpl.CombinedClassLoader(Thread.currentThread().getContextClassLoader(), new OrderClassLoaders(var2, var3));
               }
            });
            return this.unwrap(var1, var6, var4, var5);
         } catch (PrivilegedActionException var8) {
            Exception var7 = extractException(var8);
            if (var7 instanceof IOException) {
               throw (IOException)var7;
            } else if (var7 instanceof ClassNotFoundException) {
               throw new UnmarshalException(var7.toString(), var7);
            } else {
               logger.warning("unwrap", "Failed to unmarshall object: " + var7);
               logger.debug("unwrap", (Throwable)var7);
               return null;
            }
         }
      }
   }

   private static IOException newIOException(String var0, Throwable var1) {
      IOException var2 = new IOException(var0);
      return (IOException)EnvHelp.initCause(var2, var1);
   }

   private static Exception extractException(Exception var0) {
      while(var0 instanceof PrivilegedActionException) {
         var0 = ((PrivilegedActionException)var0).getException();
      }

      return var0;
   }

   private static Object[] nullIsEmpty(Object[] var0) {
      return var0 == null ? NO_OBJECTS : var0;
   }

   private static String[] nullIsEmpty(String[] var0) {
      return var0 == null ? NO_STRINGS : var0;
   }

   private static void checkNonNull(String var0, Object var1) {
      if (var1 == null) {
         IllegalArgumentException var2 = new IllegalArgumentException(var0 + " must not be null");
         throw new RuntimeOperationsException(var2);
      }
   }

   private static String objects(Object[] var0) {
      return var0 == null ? "null" : Arrays.asList(var0).toString();
   }

   private static String strings(String[] var0) {
      return objects(var0);
   }

   private static final class CombinedClassLoader extends ClassLoader {
      final RMIConnectionImpl.CombinedClassLoader.ClassLoaderWrapper defaultCL;

      private CombinedClassLoader(ClassLoader var1, ClassLoader var2) {
         super(var1);
         this.defaultCL = new RMIConnectionImpl.CombinedClassLoader.ClassLoaderWrapper(var2);
      }

      protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
         ReflectUtil.checkPackageAccess(var1);

         try {
            super.loadClass(var1, var2);
         } catch (Exception var5) {
            for(Object var4 = var5; var4 != null; var4 = ((Throwable)var4).getCause()) {
               if (var4 instanceof SecurityException) {
                  throw var4 == var5 ? (SecurityException)var4 : new SecurityException(((Throwable)var4).getMessage(), var5);
               }
            }
         }

         Class var3 = this.defaultCL.loadClass(var1, var2);
         return var3;
      }

      // $FF: synthetic method
      CombinedClassLoader(ClassLoader var1, ClassLoader var2, Object var3) {
         this(var1, var2);
      }

      private static final class ClassLoaderWrapper extends ClassLoader {
         ClassLoaderWrapper(ClassLoader var1) {
            super(var1);
         }

         protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
            return super.loadClass(var1, var2);
         }
      }
   }

   private static class SetCcl implements PrivilegedExceptionAction<ClassLoader> {
      private final ClassLoader classLoader;

      SetCcl(ClassLoader var1) {
         this.classLoader = var1;
      }

      public ClassLoader run() {
         Thread var1 = Thread.currentThread();
         ClassLoader var2 = var1.getContextClassLoader();
         var1.setContextClassLoader(this.classLoader);
         return var2;
      }
   }

   private class RMIServerCommunicatorAdmin extends ServerCommunicatorAdmin {
      public RMIServerCommunicatorAdmin(long var2) {
         super(var2);
      }

      protected void doStop() {
         try {
            RMIConnectionImpl.this.close();
         } catch (IOException var2) {
            RMIConnectionImpl.logger.warning("RMIServerCommunicatorAdmin-doStop", "Failed to close: " + var2);
            RMIConnectionImpl.logger.debug("RMIServerCommunicatorAdmin-doStop", (Throwable)var2);
         }

      }
   }

   private class PrivilegedOperation implements PrivilegedExceptionAction<Object> {
      private int operation;
      private Object[] params;

      public PrivilegedOperation(int var2, Object[] var3) {
         this.operation = var2;
         this.params = var3;
      }

      public Object run() throws Exception {
         return RMIConnectionImpl.this.doOperation(this.operation, this.params);
      }
   }
}
