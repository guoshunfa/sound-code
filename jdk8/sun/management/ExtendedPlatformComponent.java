package sun.management;

import java.lang.management.PlatformManagedObject;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import jdk.internal.cmm.SystemResourcePressureImpl;
import jdk.management.cmm.SystemResourcePressureMXBean;

public final class ExtendedPlatformComponent {
   private static SystemResourcePressureMXBean cmmBeanImpl = null;

   private ExtendedPlatformComponent() {
   }

   private static synchronized SystemResourcePressureMXBean getCMMBean() {
      if (cmmBeanImpl == null) {
         cmmBeanImpl = new SystemResourcePressureImpl();
      }

      return cmmBeanImpl;
   }

   public static List<? extends PlatformManagedObject> getMXBeans() {
      return shouldRegisterCMMBean() ? Collections.singletonList(getCMMBean()) : Collections.emptyList();
   }

   public static <T extends PlatformManagedObject> T getMXBean(Class<T> var0) {
      if (var0 != null && "jdk.management.cmm.SystemResourcePressureMXBean".equals(var0.getName())) {
         if (isUnlockCommercialFeaturesEnabled()) {
            return (PlatformManagedObject)var0.cast(getCMMBean());
         } else {
            throw new IllegalArgumentException("Cooperative Memory Management is a commercial feature which must be unlocked before being used.  To learn more about commercial features and how to unlock them visit http://www.oracle.com/technetwork/java/javaseproducts/");
         }
      } else {
         return null;
      }
   }

   private static boolean shouldRegisterCMMBean() {
      if (!isUnlockCommercialFeaturesEnabled()) {
         return false;
      } else {
         boolean var0 = false;
         Class var1 = null;

         try {
            ClassLoader var2 = ClassLoader.getSystemClassLoader();
            if (var2 == null) {
               return false;
            } else {
               var2 = var2.getParent();
               var1 = Class.forName("com.oracle.exalogic.ExaManager", false, var2);
               Object var3 = var1.getMethod("instance").invoke((Object)null);
               if (var3 != null) {
                  Object var4 = var1.getMethod("isExalogicSystem").invoke(var3);
                  var0 = (Boolean)var4;
               }

               return var0;
            }
         } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException var5) {
            return false;
         }
      }
   }

   private static boolean isUnlockCommercialFeaturesEnabled() {
      Flag var0 = Flag.getFlag("UnlockCommercialFeatures");
      return var0 != null && "true".equals(var0.getVMOption().getValue());
   }
}
