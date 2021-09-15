package sun.management;

import com.sun.management.DiagnosticCommandMBean;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import sun.misc.JavaNioAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.nio.ch.FileChannelImpl;
import sun.util.logging.LoggingSupport;

public class ManagementFactoryHelper {
   private static VMManagement jvm;
   private static ClassLoadingImpl classMBean = null;
   private static MemoryImpl memoryMBean = null;
   private static ThreadImpl threadMBean = null;
   private static RuntimeImpl runtimeMBean = null;
   private static CompilationImpl compileMBean = null;
   private static OperatingSystemImpl osMBean = null;
   private static List<BufferPoolMXBean> bufferPools = null;
   private static final String BUFFER_POOL_MXBEAN_NAME = "java.nio:type=BufferPool";
   private static HotSpotDiagnostic hsDiagMBean = null;
   private static HotspotRuntime hsRuntimeMBean = null;
   private static HotspotClassLoading hsClassMBean = null;
   private static HotspotThread hsThreadMBean = null;
   private static HotspotCompilation hsCompileMBean = null;
   private static HotspotMemory hsMemoryMBean = null;
   private static DiagnosticCommandImpl hsDiagCommandMBean = null;
   private static final String HOTSPOT_CLASS_LOADING_MBEAN_NAME = "sun.management:type=HotspotClassLoading";
   private static final String HOTSPOT_COMPILATION_MBEAN_NAME = "sun.management:type=HotspotCompilation";
   private static final String HOTSPOT_MEMORY_MBEAN_NAME = "sun.management:type=HotspotMemory";
   private static final String HOTSPOT_RUNTIME_MBEAN_NAME = "sun.management:type=HotspotRuntime";
   private static final String HOTSPOT_THREAD_MBEAN_NAME = "sun.management:type=HotspotThreading";
   static final String HOTSPOT_DIAGNOSTIC_COMMAND_MBEAN_NAME = "com.sun.management:type=DiagnosticCommand";
   private static final int JMM_THREAD_STATE_FLAG_MASK = -1048576;
   private static final int JMM_THREAD_STATE_FLAG_SUSPENDED = 1048576;
   private static final int JMM_THREAD_STATE_FLAG_NATIVE = 4194304;

   private ManagementFactoryHelper() {
   }

   public static synchronized ClassLoadingMXBean getClassLoadingMXBean() {
      if (classMBean == null) {
         classMBean = new ClassLoadingImpl(jvm);
      }

      return classMBean;
   }

   public static synchronized MemoryMXBean getMemoryMXBean() {
      if (memoryMBean == null) {
         memoryMBean = new MemoryImpl(jvm);
      }

      return memoryMBean;
   }

   public static synchronized ThreadMXBean getThreadMXBean() {
      if (threadMBean == null) {
         threadMBean = new ThreadImpl(jvm);
      }

      return threadMBean;
   }

   public static synchronized RuntimeMXBean getRuntimeMXBean() {
      if (runtimeMBean == null) {
         runtimeMBean = new RuntimeImpl(jvm);
      }

      return runtimeMBean;
   }

   public static synchronized CompilationMXBean getCompilationMXBean() {
      if (compileMBean == null && jvm.getCompilerName() != null) {
         compileMBean = new CompilationImpl(jvm);
      }

      return compileMBean;
   }

   public static synchronized OperatingSystemMXBean getOperatingSystemMXBean() {
      if (osMBean == null) {
         osMBean = new OperatingSystemImpl(jvm);
      }

      return osMBean;
   }

   public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
      MemoryPoolMXBean[] var0 = MemoryImpl.getMemoryPools();
      ArrayList var1 = new ArrayList(var0.length);
      MemoryPoolMXBean[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MemoryPoolMXBean var5 = var2[var4];
         var1.add(var5);
      }

      return var1;
   }

   public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
      MemoryManagerMXBean[] var0 = MemoryImpl.getMemoryManagers();
      ArrayList var1 = new ArrayList(var0.length);
      MemoryManagerMXBean[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MemoryManagerMXBean var5 = var2[var4];
         var1.add(var5);
      }

      return var1;
   }

   public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
      MemoryManagerMXBean[] var0 = MemoryImpl.getMemoryManagers();
      ArrayList var1 = new ArrayList(var0.length);
      MemoryManagerMXBean[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MemoryManagerMXBean var5 = var2[var4];
         if (GarbageCollectorMXBean.class.isInstance(var5)) {
            var1.add(GarbageCollectorMXBean.class.cast(var5));
         }
      }

      return var1;
   }

   public static PlatformLoggingMXBean getPlatformLoggingMXBean() {
      return LoggingSupport.isAvailable() ? ManagementFactoryHelper.PlatformLoggingImpl.instance : null;
   }

   public static synchronized List<BufferPoolMXBean> getBufferPoolMXBeans() {
      if (bufferPools == null) {
         bufferPools = new ArrayList(2);
         bufferPools.add(createBufferPoolMXBean(SharedSecrets.getJavaNioAccess().getDirectBufferPool()));
         bufferPools.add(createBufferPoolMXBean(FileChannelImpl.getMappedBufferPool()));
      }

      return bufferPools;
   }

   private static BufferPoolMXBean createBufferPoolMXBean(final JavaNioAccess.BufferPool var0) {
      return new BufferPoolMXBean() {
         private volatile ObjectName objname;

         public ObjectName getObjectName() {
            ObjectName var1 = this.objname;
            if (var1 == null) {
               synchronized(this) {
                  var1 = this.objname;
                  if (var1 == null) {
                     var1 = Util.newObjectName("java.nio:type=BufferPool,name=" + var0.getName());
                     this.objname = var1;
                  }
               }
            }

            return var1;
         }

         public String getName() {
            return var0.getName();
         }

         public long getCount() {
            return var0.getCount();
         }

         public long getTotalCapacity() {
            return var0.getTotalCapacity();
         }

         public long getMemoryUsed() {
            return var0.getMemoryUsed();
         }
      };
   }

   public static synchronized HotSpotDiagnosticMXBean getDiagnosticMXBean() {
      if (hsDiagMBean == null) {
         hsDiagMBean = new HotSpotDiagnostic();
      }

      return hsDiagMBean;
   }

   public static synchronized HotspotRuntimeMBean getHotspotRuntimeMBean() {
      if (hsRuntimeMBean == null) {
         hsRuntimeMBean = new HotspotRuntime(jvm);
      }

      return hsRuntimeMBean;
   }

   public static synchronized HotspotClassLoadingMBean getHotspotClassLoadingMBean() {
      if (hsClassMBean == null) {
         hsClassMBean = new HotspotClassLoading(jvm);
      }

      return hsClassMBean;
   }

   public static synchronized HotspotThreadMBean getHotspotThreadMBean() {
      if (hsThreadMBean == null) {
         hsThreadMBean = new HotspotThread(jvm);
      }

      return hsThreadMBean;
   }

   public static synchronized HotspotMemoryMBean getHotspotMemoryMBean() {
      if (hsMemoryMBean == null) {
         hsMemoryMBean = new HotspotMemory(jvm);
      }

      return hsMemoryMBean;
   }

   public static synchronized DiagnosticCommandMBean getDiagnosticCommandMBean() {
      if (hsDiagCommandMBean == null && jvm.isRemoteDiagnosticCommandsSupported()) {
         hsDiagCommandMBean = new DiagnosticCommandImpl(jvm);
      }

      return hsDiagCommandMBean;
   }

   public static synchronized HotspotCompilationMBean getHotspotCompilationMBean() {
      if (hsCompileMBean == null) {
         hsCompileMBean = new HotspotCompilation(jvm);
      }

      return hsCompileMBean;
   }

   private static void addMBean(final MBeanServer var0, final Object var1, String var2) {
      try {
         final ObjectName var3 = Util.newObjectName(var2);
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws MBeanRegistrationException, NotCompliantMBeanException {
               try {
                  var0.registerMBean(var1, var3);
                  return null;
               } catch (InstanceAlreadyExistsException var2) {
                  return null;
               }
            }
         });
      } catch (PrivilegedActionException var6) {
         throw Util.newException(var6.getException());
      }
   }

   public static HashMap<ObjectName, DynamicMBean> getPlatformDynamicMBeans() {
      HashMap var0 = new HashMap();
      DiagnosticCommandMBean var1 = getDiagnosticCommandMBean();
      if (var1 != null) {
         var0.put(Util.newObjectName("com.sun.management:type=DiagnosticCommand"), var1);
      }

      return var0;
   }

   static void registerInternalMBeans(MBeanServer var0) {
      addMBean(var0, getHotspotClassLoadingMBean(), "sun.management:type=HotspotClassLoading");
      addMBean(var0, getHotspotMemoryMBean(), "sun.management:type=HotspotMemory");
      addMBean(var0, getHotspotRuntimeMBean(), "sun.management:type=HotspotRuntime");
      addMBean(var0, getHotspotThreadMBean(), "sun.management:type=HotspotThreading");
      if (getCompilationMXBean() != null) {
         addMBean(var0, getHotspotCompilationMBean(), "sun.management:type=HotspotCompilation");
      }

   }

   private static void unregisterMBean(final MBeanServer var0, String var1) {
      try {
         final ObjectName var2 = Util.newObjectName(var1);
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws MBeanRegistrationException, RuntimeOperationsException {
               try {
                  var0.unregisterMBean(var2);
               } catch (InstanceNotFoundException var2x) {
               }

               return null;
            }
         });
      } catch (PrivilegedActionException var4) {
         throw Util.newException(var4.getException());
      }
   }

   static void unregisterInternalMBeans(MBeanServer var0) {
      unregisterMBean(var0, "sun.management:type=HotspotClassLoading");
      unregisterMBean(var0, "sun.management:type=HotspotMemory");
      unregisterMBean(var0, "sun.management:type=HotspotRuntime");
      unregisterMBean(var0, "sun.management:type=HotspotThreading");
      if (getCompilationMXBean() != null) {
         unregisterMBean(var0, "sun.management:type=HotspotCompilation");
      }

   }

   public static boolean isThreadSuspended(int var0) {
      return (var0 & 1048576) != 0;
   }

   public static boolean isThreadRunningNative(int var0) {
      return (var0 & 4194304) != 0;
   }

   public static Thread.State toThreadState(int var0) {
      int var1 = var0 & 1048575;
      return VM.toThreadState(var1);
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("management");
            return null;
         }
      });
      jvm = new VMManagementImpl();
   }

   static class PlatformLoggingImpl implements ManagementFactoryHelper.LoggingMXBean {
      static final PlatformLoggingMXBean instance = new ManagementFactoryHelper.PlatformLoggingImpl();
      static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
      private volatile ObjectName objname;

      public ObjectName getObjectName() {
         ObjectName var1 = this.objname;
         if (var1 == null) {
            synchronized(this) {
               var1 = this.objname;
               if (var1 == null) {
                  var1 = Util.newObjectName("java.util.logging:type=Logging");
                  this.objname = var1;
               }
            }
         }

         return var1;
      }

      public List<String> getLoggerNames() {
         return LoggingSupport.getLoggerNames();
      }

      public String getLoggerLevel(String var1) {
         return LoggingSupport.getLoggerLevel(var1);
      }

      public void setLoggerLevel(String var1, String var2) {
         LoggingSupport.setLoggerLevel(var1, var2);
      }

      public String getParentLoggerName(String var1) {
         return LoggingSupport.getParentLoggerName(var1);
      }
   }

   public interface LoggingMXBean extends PlatformLoggingMXBean, java.util.logging.LoggingMXBean {
   }
}
