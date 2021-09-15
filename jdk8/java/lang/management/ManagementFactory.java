package java.lang.management;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerPermission;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;
import javax.management.StandardEmitterMBean;
import javax.management.StandardMBean;
import sun.management.ExtendedPlatformComponent;
import sun.management.ManagementFactoryHelper;
import sun.misc.VM;

public class ManagementFactory {
   public static final String CLASS_LOADING_MXBEAN_NAME = "java.lang:type=ClassLoading";
   public static final String COMPILATION_MXBEAN_NAME = "java.lang:type=Compilation";
   public static final String MEMORY_MXBEAN_NAME = "java.lang:type=Memory";
   public static final String OPERATING_SYSTEM_MXBEAN_NAME = "java.lang:type=OperatingSystem";
   public static final String RUNTIME_MXBEAN_NAME = "java.lang:type=Runtime";
   public static final String THREAD_MXBEAN_NAME = "java.lang:type=Threading";
   public static final String GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE = "java.lang:type=GarbageCollector";
   public static final String MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryManager";
   public static final String MEMORY_POOL_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryPool";
   private static MBeanServer platformMBeanServer;
   private static final String NOTIF_EMITTER = "javax.management.NotificationEmitter";

   private ManagementFactory() {
   }

   public static ClassLoadingMXBean getClassLoadingMXBean() {
      return ManagementFactoryHelper.getClassLoadingMXBean();
   }

   public static MemoryMXBean getMemoryMXBean() {
      return ManagementFactoryHelper.getMemoryMXBean();
   }

   public static ThreadMXBean getThreadMXBean() {
      return ManagementFactoryHelper.getThreadMXBean();
   }

   public static RuntimeMXBean getRuntimeMXBean() {
      return ManagementFactoryHelper.getRuntimeMXBean();
   }

   public static CompilationMXBean getCompilationMXBean() {
      return ManagementFactoryHelper.getCompilationMXBean();
   }

   public static OperatingSystemMXBean getOperatingSystemMXBean() {
      return ManagementFactoryHelper.getOperatingSystemMXBean();
   }

   public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
      return ManagementFactoryHelper.getMemoryPoolMXBeans();
   }

   public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
      return ManagementFactoryHelper.getMemoryManagerMXBeans();
   }

   public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
      return ManagementFactoryHelper.getGarbageCollectorMXBeans();
   }

   public static synchronized MBeanServer getPlatformMBeanServer() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         MBeanServerPermission var1 = new MBeanServerPermission("createMBeanServer");
         var0.checkPermission(var1);
      }

      if (platformMBeanServer == null) {
         platformMBeanServer = MBeanServerFactory.createMBeanServer();
         PlatformComponent[] var8 = PlatformComponent.values();
         int var2 = var8.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            PlatformComponent var4 = var8[var3];
            List var5 = var4.getMXBeans(var4.getMXBeanInterface());
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               PlatformManagedObject var7 = (PlatformManagedObject)var6.next();
               if (!platformMBeanServer.isRegistered(var7.getObjectName())) {
                  addMXBean(platformMBeanServer, var7);
               }
            }
         }

         HashMap var9 = ManagementFactoryHelper.getPlatformDynamicMBeans();
         Iterator var10 = var9.entrySet().iterator();

         while(var10.hasNext()) {
            Map.Entry var11 = (Map.Entry)var10.next();
            addDynamicMBean(platformMBeanServer, (DynamicMBean)var11.getValue(), (ObjectName)var11.getKey());
         }

         var10 = ExtendedPlatformComponent.getMXBeans().iterator();

         while(var10.hasNext()) {
            PlatformManagedObject var12 = (PlatformManagedObject)var10.next();
            if (!platformMBeanServer.isRegistered(var12.getObjectName())) {
               addMXBean(platformMBeanServer, var12);
            }
         }
      }

      return platformMBeanServer;
   }

   public static <T> T newPlatformMXBeanProxy(MBeanServerConnection var0, String var1, final Class<T> var2) throws IOException {
      ClassLoader var4 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return var2.getClassLoader();
         }
      });
      if (!VM.isSystemDomainLoader(var4)) {
         throw new IllegalArgumentException(var1 + " is not a platform MXBean");
      } else {
         try {
            ObjectName var5 = new ObjectName(var1);
            String var6 = var2.getName();
            if (!var0.isInstanceOf(var5, var6)) {
               throw new IllegalArgumentException(var1 + " is not an instance of " + var2);
            } else {
               boolean var8 = var0.isInstanceOf(var5, "javax.management.NotificationEmitter");
               return JMX.newMXBeanProxy(var0, var5, var2, var8);
            }
         } catch (MalformedObjectNameException | InstanceNotFoundException var9) {
            throw new IllegalArgumentException(var9);
         }
      }
   }

   public static <T extends PlatformManagedObject> T getPlatformMXBean(Class<T> var0) {
      PlatformComponent var1 = PlatformComponent.getPlatformComponent(var0);
      if (var1 == null) {
         PlatformManagedObject var2 = ExtendedPlatformComponent.getMXBean(var0);
         if (var2 != null) {
            return var2;
         } else {
            throw new IllegalArgumentException(var0.getName() + " is not a platform management interface");
         }
      } else if (!var1.isSingleton()) {
         throw new IllegalArgumentException(var0.getName() + " can have zero or more than one instances");
      } else {
         return var1.getSingletonMXBean(var0);
      }
   }

   public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(Class<T> var0) {
      PlatformComponent var1 = PlatformComponent.getPlatformComponent(var0);
      if (var1 == null) {
         PlatformManagedObject var2 = ExtendedPlatformComponent.getMXBean(var0);
         if (var2 != null) {
            return Collections.singletonList(var2);
         } else {
            throw new IllegalArgumentException(var0.getName() + " is not a platform management interface");
         }
      } else {
         return Collections.unmodifiableList(var1.getMXBeans(var0));
      }
   }

   public static <T extends PlatformManagedObject> T getPlatformMXBean(MBeanServerConnection var0, Class<T> var1) throws IOException {
      PlatformComponent var2 = PlatformComponent.getPlatformComponent(var1);
      if (var2 == null) {
         PlatformManagedObject var3 = ExtendedPlatformComponent.getMXBean(var1);
         if (var3 != null) {
            ObjectName var4 = var3.getObjectName();
            return (PlatformManagedObject)newPlatformMXBeanProxy(var0, var4.getCanonicalName(), var1);
         } else {
            throw new IllegalArgumentException(var1.getName() + " is not a platform management interface");
         }
      } else if (!var2.isSingleton()) {
         throw new IllegalArgumentException(var1.getName() + " can have zero or more than one instances");
      } else {
         return var2.getSingletonMXBean(var0, var1);
      }
   }

   public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(MBeanServerConnection var0, Class<T> var1) throws IOException {
      PlatformComponent var2 = PlatformComponent.getPlatformComponent(var1);
      if (var2 == null) {
         PlatformManagedObject var3 = ExtendedPlatformComponent.getMXBean(var1);
         if (var3 != null) {
            ObjectName var4 = var3.getObjectName();
            PlatformManagedObject var5 = (PlatformManagedObject)newPlatformMXBeanProxy(var0, var4.getCanonicalName(), var1);
            return Collections.singletonList(var5);
         } else {
            throw new IllegalArgumentException(var1.getName() + " is not a platform management interface");
         }
      } else {
         return Collections.unmodifiableList(var2.getMXBeans(var0, var1));
      }
   }

   public static Set<Class<? extends PlatformManagedObject>> getPlatformManagementInterfaces() {
      HashSet var0 = new HashSet();
      PlatformComponent[] var1 = PlatformComponent.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         PlatformComponent var4 = var1[var3];
         var0.add(var4.getMXBeanInterface());
      }

      return Collections.unmodifiableSet(var0);
   }

   private static void addMXBean(final MBeanServer var0, final PlatformManagedObject var1) {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
               Object var1x;
               if (var1 instanceof DynamicMBean) {
                  var1x = (DynamicMBean)DynamicMBean.class.cast(var1);
               } else if (var1 instanceof NotificationEmitter) {
                  var1x = new StandardEmitterMBean(var1, (Class)null, true, (NotificationEmitter)var1);
               } else {
                  var1x = new StandardMBean(var1, (Class)null, true);
               }

               var0.registerMBean(var1x, var1.getObjectName());
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw new RuntimeException(var3.getException());
      }
   }

   private static void addDynamicMBean(final MBeanServer var0, final DynamicMBean var1, final ObjectName var2) {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
               var0.registerMBean(var1, var2);
               return null;
            }
         });
      } catch (PrivilegedActionException var4) {
         throw new RuntimeException(var4.getException());
      }
   }
}
