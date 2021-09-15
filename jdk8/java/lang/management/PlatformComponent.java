package java.lang.management;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.UnixOperatingSystemMXBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.QueryExp;
import sun.management.ManagementFactoryHelper;
import sun.management.Util;

enum PlatformComponent {
   CLASS_LOADING("java.lang.management.ClassLoadingMXBean", "java.lang", "ClassLoading", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<ClassLoadingMXBean>() {
      public List<ClassLoadingMXBean> getMXBeans() {
         return Collections.singletonList(ManagementFactoryHelper.getClassLoadingMXBean());
      }
   }, new PlatformComponent[0]),
   COMPILATION("java.lang.management.CompilationMXBean", "java.lang", "Compilation", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<CompilationMXBean>() {
      public List<CompilationMXBean> getMXBeans() {
         CompilationMXBean var1 = ManagementFactoryHelper.getCompilationMXBean();
         return var1 == null ? Collections.emptyList() : Collections.singletonList(var1);
      }
   }, new PlatformComponent[0]),
   MEMORY("java.lang.management.MemoryMXBean", "java.lang", "Memory", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<MemoryMXBean>() {
      public List<MemoryMXBean> getMXBeans() {
         return Collections.singletonList(ManagementFactoryHelper.getMemoryMXBean());
      }
   }, new PlatformComponent[0]),
   GARBAGE_COLLECTOR("java.lang.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", keyProperties("name"), false, new PlatformComponent.MXBeanFetcher<GarbageCollectorMXBean>() {
      public List<GarbageCollectorMXBean> getMXBeans() {
         return ManagementFactoryHelper.getGarbageCollectorMXBeans();
      }
   }, new PlatformComponent[0]),
   MEMORY_MANAGER("java.lang.management.MemoryManagerMXBean", "java.lang", "MemoryManager", keyProperties("name"), false, new PlatformComponent.MXBeanFetcher<MemoryManagerMXBean>() {
      public List<MemoryManagerMXBean> getMXBeans() {
         return ManagementFactoryHelper.getMemoryManagerMXBeans();
      }
   }, new PlatformComponent[]{GARBAGE_COLLECTOR}),
   MEMORY_POOL("java.lang.management.MemoryPoolMXBean", "java.lang", "MemoryPool", keyProperties("name"), false, new PlatformComponent.MXBeanFetcher<MemoryPoolMXBean>() {
      public List<MemoryPoolMXBean> getMXBeans() {
         return ManagementFactoryHelper.getMemoryPoolMXBeans();
      }
   }, new PlatformComponent[0]),
   OPERATING_SYSTEM("java.lang.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<OperatingSystemMXBean>() {
      public List<OperatingSystemMXBean> getMXBeans() {
         return Collections.singletonList(ManagementFactoryHelper.getOperatingSystemMXBean());
      }
   }, new PlatformComponent[0]),
   RUNTIME("java.lang.management.RuntimeMXBean", "java.lang", "Runtime", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<RuntimeMXBean>() {
      public List<RuntimeMXBean> getMXBeans() {
         return Collections.singletonList(ManagementFactoryHelper.getRuntimeMXBean());
      }
   }, new PlatformComponent[0]),
   THREADING("java.lang.management.ThreadMXBean", "java.lang", "Threading", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<ThreadMXBean>() {
      public List<ThreadMXBean> getMXBeans() {
         return Collections.singletonList(ManagementFactoryHelper.getThreadMXBean());
      }
   }, new PlatformComponent[0]),
   LOGGING("java.lang.management.PlatformLoggingMXBean", "java.util.logging", "Logging", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<PlatformLoggingMXBean>() {
      public List<PlatformLoggingMXBean> getMXBeans() {
         PlatformLoggingMXBean var1 = ManagementFactoryHelper.getPlatformLoggingMXBean();
         return var1 == null ? Collections.emptyList() : Collections.singletonList(var1);
      }
   }, new PlatformComponent[0]),
   BUFFER_POOL("java.lang.management.BufferPoolMXBean", "java.nio", "BufferPool", keyProperties("name"), false, new PlatformComponent.MXBeanFetcher<BufferPoolMXBean>() {
      public List<BufferPoolMXBean> getMXBeans() {
         return ManagementFactoryHelper.getBufferPoolMXBeans();
      }
   }, new PlatformComponent[0]),
   SUN_GARBAGE_COLLECTOR("com.sun.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", keyProperties("name"), false, new PlatformComponent.MXBeanFetcher<com.sun.management.GarbageCollectorMXBean>() {
      public List<com.sun.management.GarbageCollectorMXBean> getMXBeans() {
         return PlatformComponent.getGcMXBeanList(com.sun.management.GarbageCollectorMXBean.class);
      }
   }, new PlatformComponent[0]),
   SUN_OPERATING_SYSTEM("com.sun.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<com.sun.management.OperatingSystemMXBean>() {
      public List<com.sun.management.OperatingSystemMXBean> getMXBeans() {
         return PlatformComponent.getOSMXBeanList(com.sun.management.OperatingSystemMXBean.class);
      }
   }, new PlatformComponent[0]),
   SUN_UNIX_OPERATING_SYSTEM("com.sun.management.UnixOperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<UnixOperatingSystemMXBean>() {
      public List<UnixOperatingSystemMXBean> getMXBeans() {
         return PlatformComponent.getOSMXBeanList(UnixOperatingSystemMXBean.class);
      }
   }, new PlatformComponent[0]),
   HOTSPOT_DIAGNOSTIC("com.sun.management.HotSpotDiagnosticMXBean", "com.sun.management", "HotSpotDiagnostic", defaultKeyProperties(), true, new PlatformComponent.MXBeanFetcher<HotSpotDiagnosticMXBean>() {
      public List<HotSpotDiagnosticMXBean> getMXBeans() {
         return Collections.singletonList(ManagementFactoryHelper.getDiagnosticMXBean());
      }
   }, new PlatformComponent[0]);

   private final String mxbeanInterfaceName;
   private final String domain;
   private final String type;
   private final Set<String> keyProperties;
   private final PlatformComponent.MXBeanFetcher<?> fetcher;
   private final PlatformComponent[] subComponents;
   private final boolean singleton;
   private static Set<String> defaultKeyProps;
   private static Map<String, PlatformComponent> enumMap;
   private static final long serialVersionUID = 6992337162326171013L;

   private static <T extends GarbageCollectorMXBean> List<T> getGcMXBeanList(Class<T> var0) {
      List var1 = ManagementFactoryHelper.getGarbageCollectorMXBeans();
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         GarbageCollectorMXBean var4 = (GarbageCollectorMXBean)var3.next();
         if (var0.isInstance(var4)) {
            var2.add(var0.cast(var4));
         }
      }

      return var2;
   }

   private static <T extends OperatingSystemMXBean> List<T> getOSMXBeanList(Class<T> var0) {
      OperatingSystemMXBean var1 = ManagementFactoryHelper.getOperatingSystemMXBean();
      return var0.isInstance(var1) ? Collections.singletonList(var0.cast(var1)) : Collections.emptyList();
   }

   private PlatformComponent(String var3, String var4, String var5, Set<String> var6, boolean var7, PlatformComponent.MXBeanFetcher<?> var8, PlatformComponent... var9) {
      this.mxbeanInterfaceName = var3;
      this.domain = var4;
      this.type = var5;
      this.keyProperties = var6;
      this.singleton = var7;
      this.fetcher = var8;
      this.subComponents = var9;
   }

   private static Set<String> defaultKeyProperties() {
      if (defaultKeyProps == null) {
         defaultKeyProps = Collections.singleton("type");
      }

      return defaultKeyProps;
   }

   private static Set<String> keyProperties(String... var0) {
      HashSet var1 = new HashSet();
      var1.add("type");
      String[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         var1.add(var5);
      }

      return var1;
   }

   boolean isSingleton() {
      return this.singleton;
   }

   String getMXBeanInterfaceName() {
      return this.mxbeanInterfaceName;
   }

   Class<? extends PlatformManagedObject> getMXBeanInterface() {
      try {
         return Class.forName(this.mxbeanInterfaceName, false, PlatformManagedObject.class.getClassLoader());
      } catch (ClassNotFoundException var2) {
         throw new AssertionError(var2);
      }
   }

   <T extends PlatformManagedObject> List<T> getMXBeans(Class<T> var1) {
      return this.fetcher.getMXBeans();
   }

   <T extends PlatformManagedObject> T getSingletonMXBean(Class<T> var1) {
      if (!this.singleton) {
         throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances");
      } else {
         List var2 = this.getMXBeans(var1);

         assert var2.size() == 1;

         return var2.isEmpty() ? null : (PlatformManagedObject)var2.get(0);
      }
   }

   <T extends PlatformManagedObject> T getSingletonMXBean(MBeanServerConnection var1, Class<T> var2) throws IOException {
      if (!this.singleton) {
         throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances");
      } else {
         assert this.keyProperties.size() == 1;

         String var3 = this.domain + ":type=" + this.type;
         return (PlatformManagedObject)ManagementFactory.newPlatformMXBeanProxy(var1, var3, var2);
      }
   }

   <T extends PlatformManagedObject> List<T> getMXBeans(MBeanServerConnection var1, Class<T> var2) throws IOException {
      ArrayList var3 = new ArrayList();
      Iterator var4 = this.getObjectNames(var1).iterator();

      while(var4.hasNext()) {
         ObjectName var5 = (ObjectName)var4.next();
         var3.add(ManagementFactory.newPlatformMXBeanProxy(var1, var5.getCanonicalName(), var2));
      }

      return var3;
   }

   private Set<ObjectName> getObjectNames(MBeanServerConnection var1) throws IOException {
      String var2 = this.domain + ":type=" + this.type;
      if (this.keyProperties.size() > 1) {
         var2 = var2 + ",*";
      }

      ObjectName var3 = Util.newObjectName(var2);
      Set var4 = var1.queryNames(var3, (QueryExp)null);
      PlatformComponent[] var5 = this.subComponents;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         PlatformComponent var8 = var5[var7];
         var4.addAll(var8.getObjectNames(var1));
      }

      return var4;
   }

   private static synchronized void ensureInitialized() {
      if (enumMap == null) {
         enumMap = new HashMap();
         PlatformComponent[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            PlatformComponent var3 = var0[var2];
            enumMap.put(var3.getMXBeanInterfaceName(), var3);
         }
      }

   }

   static boolean isPlatformMXBean(String var0) {
      ensureInitialized();
      return enumMap.containsKey(var0);
   }

   static <T extends PlatformManagedObject> PlatformComponent getPlatformComponent(Class<T> var0) {
      ensureInitialized();
      String var1 = var0.getName();
      PlatformComponent var2 = (PlatformComponent)enumMap.get(var1);
      return var2 != null && var2.getMXBeanInterface() == var0 ? var2 : null;
   }

   interface MXBeanFetcher<T extends PlatformManagedObject> {
      List<T> getMXBeans();
   }
}
