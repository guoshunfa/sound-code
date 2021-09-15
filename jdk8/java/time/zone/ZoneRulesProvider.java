package java.time.zone;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ZoneRulesProvider {
   private static final CopyOnWriteArrayList<ZoneRulesProvider> PROVIDERS = new CopyOnWriteArrayList();
   private static final ConcurrentMap<String, ZoneRulesProvider> ZONES = new ConcurrentHashMap(512, 0.75F, 2);

   public static Set<String> getAvailableZoneIds() {
      return new HashSet(ZONES.keySet());
   }

   public static ZoneRules getRules(String var0, boolean var1) {
      Objects.requireNonNull(var0, (String)"zoneId");
      return getProvider(var0).provideRules(var0, var1);
   }

   public static NavigableMap<String, ZoneRules> getVersions(String var0) {
      Objects.requireNonNull(var0, (String)"zoneId");
      return getProvider(var0).provideVersions(var0);
   }

   private static ZoneRulesProvider getProvider(String var0) {
      ZoneRulesProvider var1 = (ZoneRulesProvider)ZONES.get(var0);
      if (var1 == null) {
         if (ZONES.isEmpty()) {
            throw new ZoneRulesException("No time-zone data files registered");
         } else {
            throw new ZoneRulesException("Unknown time-zone ID: " + var0);
         }
      } else {
         return var1;
      }
   }

   public static void registerProvider(ZoneRulesProvider var0) {
      Objects.requireNonNull(var0, (String)"provider");
      registerProvider0(var0);
      PROVIDERS.add(var0);
   }

   private static void registerProvider0(ZoneRulesProvider var0) {
      Iterator var1 = var0.provideZoneIds().iterator();

      String var2;
      ZoneRulesProvider var3;
      do {
         if (!var1.hasNext()) {
            return;
         }

         var2 = (String)var1.next();
         Objects.requireNonNull(var2, (String)"zoneId");
         var3 = (ZoneRulesProvider)ZONES.putIfAbsent(var2, var0);
      } while(var3 == null);

      throw new ZoneRulesException("Unable to register zone as one already registered with that ID: " + var2 + ", currently loading from provider: " + var0);
   }

   public static boolean refresh() {
      boolean var0 = false;

      ZoneRulesProvider var2;
      for(Iterator var1 = PROVIDERS.iterator(); var1.hasNext(); var0 |= var2.provideRefresh()) {
         var2 = (ZoneRulesProvider)var1.next();
      }

      return var0;
   }

   protected ZoneRulesProvider() {
   }

   protected abstract Set<String> provideZoneIds();

   protected abstract ZoneRules provideRules(String var1, boolean var2);

   protected abstract NavigableMap<String, ZoneRules> provideVersions(String var1);

   protected boolean provideRefresh() {
      return false;
   }

   static {
      final ArrayList var0 = new ArrayList();
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            String var1 = System.getProperty("java.time.zone.DefaultZoneRulesProvider");
            if (var1 != null) {
               try {
                  Class var2 = Class.forName(var1, true, ClassLoader.getSystemClassLoader());
                  ZoneRulesProvider var3 = (ZoneRulesProvider)ZoneRulesProvider.class.cast(var2.newInstance());
                  ZoneRulesProvider.registerProvider(var3);
                  var0.add(var3);
               } catch (Exception var4) {
                  throw new Error(var4);
               }
            } else {
               ZoneRulesProvider.registerProvider(new TzdbZoneRulesProvider());
            }

            return null;
         }
      });
      ServiceLoader var1 = ServiceLoader.load(ZoneRulesProvider.class, ClassLoader.getSystemClassLoader());
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ZoneRulesProvider var3;
         try {
            var3 = (ZoneRulesProvider)var2.next();
         } catch (ServiceConfigurationError var7) {
            if (var7.getCause() instanceof SecurityException) {
               continue;
            }

            throw var7;
         }

         boolean var4 = false;
         Iterator var5 = var0.iterator();

         while(var5.hasNext()) {
            ZoneRulesProvider var6 = (ZoneRulesProvider)var5.next();
            if (var6.getClass() == var3.getClass()) {
               var4 = true;
            }
         }

         if (!var4) {
            registerProvider0(var3);
            var0.add(var3);
         }
      }

      PROVIDERS.addAll(var0);
   }
}
