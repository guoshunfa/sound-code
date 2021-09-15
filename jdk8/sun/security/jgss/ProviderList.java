package sun.security.jgss;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.action.GetPropertyAction;
import sun.security.jgss.spi.MechanismFactory;
import sun.security.jgss.wrapper.NativeGSSFactory;
import sun.security.jgss.wrapper.SunNativeProvider;

public final class ProviderList {
   private static final String PROV_PROP_PREFIX = "GssApiMechanism.";
   private static final int PROV_PROP_PREFIX_LEN = "GssApiMechanism.".length();
   private static final String SPI_MECH_FACTORY_TYPE = "sun.security.jgss.spi.MechanismFactory";
   private static final String DEFAULT_MECH_PROP = "sun.security.jgss.mechanism";
   public static final Oid DEFAULT_MECH_OID;
   private ArrayList<ProviderList.PreferencesEntry> preferences = new ArrayList(5);
   private HashMap<ProviderList.PreferencesEntry, MechanismFactory> factories = new HashMap(5);
   private HashSet<Oid> mechs = new HashSet(5);
   private final GSSCaller caller;

   public ProviderList(GSSCaller var1, boolean var2) {
      this.caller = var1;
      Provider[] var3;
      if (var2) {
         var3 = new Provider[]{new SunNativeProvider()};
      } else {
         var3 = Security.getProviders();
      }

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Provider var5 = var3[var4];

         try {
            this.addProviderAtEnd(var5, (Oid)null);
         } catch (GSSException var7) {
            GSSUtil.debug("Error in adding provider " + var5.getName() + ": " + var7);
         }
      }

   }

   private boolean isMechFactoryProperty(String var1) {
      return var1.startsWith("GssApiMechanism.") || var1.regionMatches(true, 0, "GssApiMechanism.", 0, PROV_PROP_PREFIX_LEN);
   }

   private Oid getOidFromMechFactoryProperty(String var1) throws GSSException {
      String var2 = var1.substring(PROV_PROP_PREFIX_LEN);
      return new Oid(var2);
   }

   public synchronized MechanismFactory getMechFactory(Oid var1) throws GSSException {
      if (var1 == null) {
         var1 = DEFAULT_MECH_OID;
      }

      return this.getMechFactory((Oid)var1, (Provider)null);
   }

   public synchronized MechanismFactory getMechFactory(Oid var1, Provider var2) throws GSSException {
      if (var1 == null) {
         var1 = DEFAULT_MECH_OID;
      }

      if (var2 == null) {
         Iterator var5 = this.preferences.iterator();

         while(var5.hasNext()) {
            ProviderList.PreferencesEntry var4 = (ProviderList.PreferencesEntry)var5.next();
            if (var4.impliesMechanism(var1)) {
               MechanismFactory var6 = this.getMechFactory(var4, var1);
               if (var6 != null) {
                  return var6;
               }
            }
         }

         throw new GSSExceptionImpl(2, var1);
      } else {
         ProviderList.PreferencesEntry var3 = new ProviderList.PreferencesEntry(var2, var1);
         return this.getMechFactory(var3, var1);
      }
   }

   private MechanismFactory getMechFactory(ProviderList.PreferencesEntry var1, Oid var2) throws GSSException {
      Provider var3 = var1.getProvider();
      ProviderList.PreferencesEntry var4 = new ProviderList.PreferencesEntry(var3, var2);
      MechanismFactory var5 = (MechanismFactory)this.factories.get(var4);
      if (var5 == null) {
         String var6 = "GssApiMechanism." + var2.toString();
         String var7 = var3.getProperty(var6);
         if (var7 != null) {
            var5 = getMechFactoryImpl(var3, var7, var2, this.caller);
            this.factories.put(var4, var5);
         } else if (var1.getOid() != null) {
            throw new GSSExceptionImpl(2, "Provider " + var3.getName() + " does not support mechanism " + var2);
         }
      }

      return var5;
   }

   private static MechanismFactory getMechFactoryImpl(Provider var0, String var1, Oid var2, GSSCaller var3) throws GSSException {
      try {
         Class var4 = Class.forName("sun.security.jgss.spi.MechanismFactory");
         ClassLoader var5 = var0.getClass().getClassLoader();
         Class var6;
         if (var5 != null) {
            var6 = var5.loadClass(var1);
         } else {
            var6 = Class.forName(var1);
         }

         if (var4.isAssignableFrom(var6)) {
            Constructor var7 = var6.getConstructor(GSSCaller.class);
            MechanismFactory var8 = (MechanismFactory)((MechanismFactory)var7.newInstance(var3));
            if (var8 instanceof NativeGSSFactory) {
               ((NativeGSSFactory)var8).setMech(var2);
            }

            return var8;
         } else {
            throw createGSSException(var0, var1, "is not a sun.security.jgss.spi.MechanismFactory", (Exception)null);
         }
      } catch (ClassNotFoundException var9) {
         throw createGSSException(var0, var1, "cannot be created", var9);
      } catch (NoSuchMethodException var10) {
         throw createGSSException(var0, var1, "cannot be created", var10);
      } catch (InvocationTargetException var11) {
         throw createGSSException(var0, var1, "cannot be created", var11);
      } catch (InstantiationException var12) {
         throw createGSSException(var0, var1, "cannot be created", var12);
      } catch (IllegalAccessException var13) {
         throw createGSSException(var0, var1, "cannot be created", var13);
      } catch (SecurityException var14) {
         throw createGSSException(var0, var1, "cannot be created", var14);
      }
   }

   private static GSSException createGSSException(Provider var0, String var1, String var2, Exception var3) {
      String var4 = var1 + " configured by " + var0.getName() + " for GSS-API Mechanism Factory ";
      return new GSSExceptionImpl(2, var4 + var2, var3);
   }

   public Oid[] getMechs() {
      return (Oid[])this.mechs.toArray(new Oid[0]);
   }

   public synchronized void addProviderAtFront(Provider var1, Oid var2) throws GSSException {
      ProviderList.PreferencesEntry var3 = new ProviderList.PreferencesEntry(var1, var2);
      Iterator var6 = this.preferences.iterator();

      while(var6.hasNext()) {
         ProviderList.PreferencesEntry var4 = (ProviderList.PreferencesEntry)var6.next();
         if (var3.implies(var4)) {
            var6.remove();
         }
      }

      boolean var5;
      if (var2 == null) {
         var5 = this.addAllMechsFromProvider(var1);
      } else {
         String var7 = var2.toString();
         if (var1.getProperty("GssApiMechanism." + var7) == null) {
            throw new GSSExceptionImpl(2, "Provider " + var1.getName() + " does not support " + var7);
         }

         this.mechs.add(var2);
         var5 = true;
      }

      if (var5) {
         this.preferences.add(0, var3);
      }

   }

   public synchronized void addProviderAtEnd(Provider var1, Oid var2) throws GSSException {
      ProviderList.PreferencesEntry var3 = new ProviderList.PreferencesEntry(var1, var2);
      Iterator var6 = this.preferences.iterator();

      ProviderList.PreferencesEntry var4;
      do {
         if (!var6.hasNext()) {
            boolean var5;
            if (var2 == null) {
               var5 = this.addAllMechsFromProvider(var1);
            } else {
               String var7 = var2.toString();
               if (var1.getProperty("GssApiMechanism." + var7) == null) {
                  throw new GSSExceptionImpl(2, "Provider " + var1.getName() + " does not support " + var7);
               }

               this.mechs.add(var2);
               var5 = true;
            }

            if (var5) {
               this.preferences.add(var3);
            }

            return;
         }

         var4 = (ProviderList.PreferencesEntry)var6.next();
      } while(!var4.implies(var3));

   }

   private boolean addAllMechsFromProvider(Provider var1) {
      boolean var3 = false;
      Enumeration var4 = var1.keys();

      while(var4.hasMoreElements()) {
         String var2 = (String)var4.nextElement();
         if (this.isMechFactoryProperty(var2)) {
            try {
               Oid var5 = this.getOidFromMechFactoryProperty(var2);
               this.mechs.add(var5);
               var3 = true;
            } catch (GSSException var6) {
               GSSUtil.debug("Ignore the invalid property " + var2 + " from provider " + var1.getName());
            }
         }
      }

      return var3;
   }

   static {
      Oid var0 = null;
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.jgss.mechanism")));
      if (var1 != null) {
         var0 = GSSUtil.createOid(var1);
      }

      DEFAULT_MECH_OID = var0 == null ? GSSUtil.GSS_KRB5_MECH_OID : var0;
   }

   private static final class PreferencesEntry {
      private Provider p;
      private Oid oid;

      PreferencesEntry(Provider var1, Oid var2) {
         this.p = var1;
         this.oid = var2;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ProviderList.PreferencesEntry)) {
            return false;
         } else {
            ProviderList.PreferencesEntry var2 = (ProviderList.PreferencesEntry)var1;
            if (this.p.getName().equals(var2.p.getName())) {
               if (this.oid != null && var2.oid != null) {
                  return this.oid.equals(var2.oid);
               } else {
                  return this.oid == null && var2.oid == null;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         byte var1 = 17;
         int var2 = 37 * var1 + this.p.getName().hashCode();
         if (this.oid != null) {
            var2 = 37 * var2 + this.oid.hashCode();
         }

         return var2;
      }

      boolean implies(Object var1) {
         if (!(var1 instanceof ProviderList.PreferencesEntry)) {
            return false;
         } else {
            ProviderList.PreferencesEntry var2 = (ProviderList.PreferencesEntry)var1;
            return this.equals(var2) || this.p.getName().equals(var2.p.getName()) && this.oid == null;
         }
      }

      Provider getProvider() {
         return this.p;
      }

      Oid getOid() {
         return this.oid;
      }

      boolean impliesMechanism(Oid var1) {
         return this.oid == null || this.oid.equals(var1);
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer("<");
         var1.append(this.p.getName());
         var1.append(", ");
         var1.append((Object)this.oid);
         var1.append(">");
         return var1.toString();
      }
   }
}
