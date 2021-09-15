package sun.security.jca;

import java.security.Provider;

public class Providers {
   private static final ThreadLocal<ProviderList> threadLists = new InheritableThreadLocal();
   private static volatile int threadListsUsed;
   private static volatile ProviderList providerList;
   private static final String BACKUP_PROVIDER_CLASSNAME = "sun.security.provider.VerificationProvider";
   private static final String[] jarVerificationProviders;

   private Providers() {
   }

   public static Provider getSunProvider() {
      try {
         Class var0 = Class.forName(jarVerificationProviders[0]);
         return (Provider)var0.newInstance();
      } catch (Exception var3) {
         try {
            Class var1 = Class.forName("sun.security.provider.VerificationProvider");
            return (Provider)var1.newInstance();
         } catch (Exception var2) {
            throw new RuntimeException("Sun provider not found", var3);
         }
      }
   }

   public static Object startJarVerification() {
      ProviderList var0 = getProviderList();
      ProviderList var1 = var0.getJarList(jarVerificationProviders);
      return beginThreadProviderList(var1);
   }

   public static void stopJarVerification(Object var0) {
      endThreadProviderList((ProviderList)var0);
   }

   public static ProviderList getProviderList() {
      ProviderList var0 = getThreadProviderList();
      if (var0 == null) {
         var0 = getSystemProviderList();
      }

      return var0;
   }

   public static void setProviderList(ProviderList var0) {
      if (getThreadProviderList() == null) {
         setSystemProviderList(var0);
      } else {
         changeThreadProviderList(var0);
      }

   }

   public static ProviderList getFullProviderList() {
      Class var1 = Providers.class;
      ProviderList var0;
      synchronized(Providers.class) {
         var0 = getThreadProviderList();
         if (var0 != null) {
            ProviderList var2 = var0.removeInvalid();
            if (var2 != var0) {
               changeThreadProviderList(var2);
               var0 = var2;
            }

            return var0;
         }
      }

      var0 = getSystemProviderList();
      ProviderList var5 = var0.removeInvalid();
      if (var5 != var0) {
         setSystemProviderList(var5);
         var0 = var5;
      }

      return var0;
   }

   private static ProviderList getSystemProviderList() {
      return providerList;
   }

   private static void setSystemProviderList(ProviderList var0) {
      providerList = var0;
   }

   public static ProviderList getThreadProviderList() {
      return threadListsUsed == 0 ? null : (ProviderList)threadLists.get();
   }

   private static void changeThreadProviderList(ProviderList var0) {
      threadLists.set(var0);
   }

   public static synchronized ProviderList beginThreadProviderList(ProviderList var0) {
      if (ProviderList.debug != null) {
         ProviderList.debug.println("ThreadLocal providers: " + var0);
      }

      ProviderList var1 = (ProviderList)threadLists.get();
      ++threadListsUsed;
      threadLists.set(var0);
      return var1;
   }

   public static synchronized void endThreadProviderList(ProviderList var0) {
      if (var0 == null) {
         if (ProviderList.debug != null) {
            ProviderList.debug.println("Disabling ThreadLocal providers");
         }

         threadLists.remove();
      } else {
         if (ProviderList.debug != null) {
            ProviderList.debug.println("Restoring previous ThreadLocal providers: " + var0);
         }

         threadLists.set(var0);
      }

      --threadListsUsed;
   }

   static {
      providerList = ProviderList.EMPTY;
      providerList = ProviderList.fromSecurityProperties();
      jarVerificationProviders = new String[]{"sun.security.provider.Sun", "sun.security.rsa.SunRsaSign", "sun.security.ec.SunEC", "sun.security.provider.VerificationProvider"};
   }
}
