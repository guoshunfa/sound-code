package javax.naming.spi;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import com.sun.naming.internal.VersionHelper;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

public class NamingManager {
   static final VersionHelper helper = VersionHelper.getVersionHelper();
   private static ObjectFactoryBuilder object_factory_builder = null;
   private static final String defaultPkgPrefix = "com.sun.jndi.url";
   private static InitialContextFactoryBuilder initctx_factory_builder = null;
   public static final String CPE = "java.naming.spi.CannotProceedException";

   NamingManager() {
   }

   public static synchronized void setObjectFactoryBuilder(ObjectFactoryBuilder var0) throws NamingException {
      if (object_factory_builder != null) {
         throw new IllegalStateException("ObjectFactoryBuilder already set");
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkSetFactory();
         }

         object_factory_builder = var0;
      }
   }

   static synchronized ObjectFactoryBuilder getObjectFactoryBuilder() {
      return object_factory_builder;
   }

   static ObjectFactory getObjectFactoryFromReference(Reference var0, String var1) throws IllegalAccessException, InstantiationException, MalformedURLException {
      Class var2 = null;

      try {
         var2 = helper.loadClass(var1);
      } catch (ClassNotFoundException var6) {
      }

      String var3;
      if (var2 == null && (var3 = var0.getFactoryClassLocation()) != null) {
         try {
            var2 = helper.loadClass(var1, var3);
         } catch (ClassNotFoundException var5) {
         }
      }

      return var2 != null ? (ObjectFactory)var2.newInstance() : null;
   }

   private static Object createObjectFromFactories(Object var0, Name var1, Context var2, Hashtable<?, ?> var3) throws Exception {
      FactoryEnumeration var4 = ResourceManager.getFactories("java.naming.factory.object", var3, var2);
      if (var4 == null) {
         return null;
      } else {
         ObjectFactory var5;
         Object var6;
         for(var6 = null; var6 == null && var4.hasMore(); var6 = var5.getObjectInstance(var0, var1, var2, var3)) {
            var5 = (ObjectFactory)var4.next();
         }

         return var6;
      }
   }

   private static String getURLScheme(String var0) {
      int var1 = var0.indexOf(58);
      int var2 = var0.indexOf(47);
      return var1 <= 0 || var2 != -1 && var1 >= var2 ? null : var0.substring(0, var1);
   }

   public static Object getObjectInstance(Object var0, Name var1, Context var2, Hashtable<?, ?> var3) throws Exception {
      ObjectFactoryBuilder var5 = getObjectFactoryBuilder();
      ObjectFactory var4;
      if (var5 != null) {
         var4 = var5.createObjectFactory(var0, var3);
         return var4.getObjectInstance(var0, var1, var2, var3);
      } else {
         Reference var6 = null;
         if (var0 instanceof Reference) {
            var6 = (Reference)var0;
         } else if (var0 instanceof Referenceable) {
            var6 = ((Referenceable)((Referenceable)var0)).getReference();
         }

         Object var7;
         if (var6 != null) {
            String var8 = var6.getFactoryClassName();
            if (var8 != null) {
               var4 = getObjectFactoryFromReference(var6, var8);
               if (var4 != null) {
                  return var4.getObjectInstance(var6, var1, var2, var3);
               }

               return var0;
            }

            var7 = processURLAddrs(var6, var1, var2, var3);
            if (var7 != null) {
               return var7;
            }
         }

         var7 = createObjectFromFactories(var0, var1, var2, var3);
         return var7 != null ? var7 : var0;
      }
   }

   static Object processURLAddrs(Reference var0, Name var1, Context var2, Hashtable<?, ?> var3) throws NamingException {
      for(int var4 = 0; var4 < var0.size(); ++var4) {
         RefAddr var5 = var0.get(var4);
         if (var5 instanceof StringRefAddr && var5.getType().equalsIgnoreCase("URL")) {
            String var6 = (String)var5.getContent();
            Object var7 = processURL(var6, var1, var2, var3);
            if (var7 != null) {
               return var7;
            }
         }
      }

      return null;
   }

   private static Object processURL(Object var0, Name var1, Context var2, Hashtable<?, ?> var3) throws NamingException {
      Object var4;
      if (var0 instanceof String) {
         String var5 = (String)var0;
         String var6 = getURLScheme(var5);
         if (var6 != null) {
            var4 = getURLObject(var6, var0, var1, var2, var3);
            if (var4 != null) {
               return var4;
            }
         }
      }

      if (var0 instanceof String[]) {
         String[] var8 = (String[])((String[])var0);

         for(int var9 = 0; var9 < var8.length; ++var9) {
            String var7 = getURLScheme(var8[var9]);
            if (var7 != null) {
               var4 = getURLObject(var7, var0, var1, var2, var3);
               if (var4 != null) {
                  return var4;
               }
            }
         }
      }

      return null;
   }

   static Context getContext(Object var0, Name var1, Context var2, Hashtable<?, ?> var3) throws NamingException {
      if (var0 instanceof Context) {
         return (Context)var0;
      } else {
         Object var4;
         try {
            var4 = getObjectInstance(var0, var1, var2, var3);
         } catch (NamingException var7) {
            throw var7;
         } catch (Exception var8) {
            NamingException var6 = new NamingException();
            var6.setRootCause(var8);
            throw var6;
         }

         return var4 instanceof Context ? (Context)var4 : null;
      }
   }

   static Resolver getResolver(Object var0, Name var1, Context var2, Hashtable<?, ?> var3) throws NamingException {
      if (var0 instanceof Resolver) {
         return (Resolver)var0;
      } else {
         Object var4;
         try {
            var4 = getObjectInstance(var0, var1, var2, var3);
         } catch (NamingException var7) {
            throw var7;
         } catch (Exception var8) {
            NamingException var6 = new NamingException();
            var6.setRootCause(var8);
            throw var6;
         }

         return var4 instanceof Resolver ? (Resolver)var4 : null;
      }
   }

   public static Context getURLContext(String var0, Hashtable<?, ?> var1) throws NamingException {
      Object var2 = getURLObject(var0, (Object)null, (Name)null, (Context)null, var1);
      return var2 instanceof Context ? (Context)var2 : null;
   }

   private static Object getURLObject(String var0, Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws NamingException {
      ObjectFactory var5 = (ObjectFactory)ResourceManager.getFactory("java.naming.factory.url.pkgs", var4, var3, "." + var0 + "." + var0 + "URLContextFactory", "com.sun.jndi.url");
      if (var5 == null) {
         return null;
      } else {
         try {
            return var5.getObjectInstance(var1, var2, var3, var4);
         } catch (NamingException var8) {
            throw var8;
         } catch (Exception var9) {
            NamingException var7 = new NamingException();
            var7.setRootCause(var9);
            throw var7;
         }
      }
   }

   private static synchronized InitialContextFactoryBuilder getInitialContextFactoryBuilder() {
      return initctx_factory_builder;
   }

   public static Context getInitialContext(Hashtable<?, ?> var0) throws NamingException {
      InitialContextFactoryBuilder var2 = getInitialContextFactoryBuilder();
      InitialContextFactory var1;
      if (var2 == null) {
         String var3 = var0 != null ? (String)var0.get("java.naming.factory.initial") : null;
         if (var3 == null) {
            NoInitialContextException var4 = new NoInitialContextException("Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial");
            throw var4;
         }

         try {
            var1 = (InitialContextFactory)helper.loadClass(var3).newInstance();
         } catch (Exception var6) {
            NoInitialContextException var5 = new NoInitialContextException("Cannot instantiate class: " + var3);
            var5.setRootCause(var6);
            throw var5;
         }
      } else {
         var1 = var2.createInitialContextFactory(var0);
      }

      return var1.getInitialContext(var0);
   }

   public static synchronized void setInitialContextFactoryBuilder(InitialContextFactoryBuilder var0) throws NamingException {
      if (initctx_factory_builder != null) {
         throw new IllegalStateException("InitialContextFactoryBuilder already set");
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkSetFactory();
         }

         initctx_factory_builder = var0;
      }
   }

   public static boolean hasInitialContextFactoryBuilder() {
      return getInitialContextFactoryBuilder() != null;
   }

   public static Context getContinuationContext(CannotProceedException var0) throws NamingException {
      Hashtable var1 = var0.getEnvironment();
      if (var1 == null) {
         var1 = new Hashtable(7);
      } else {
         var1 = (Hashtable)var1.clone();
      }

      var1.put("java.naming.spi.CannotProceedException", var0);
      ContinuationContext var2 = new ContinuationContext(var0, var1);
      return var2.getTargetContext();
   }

   public static Object getStateToBind(Object var0, Name var1, Context var2, Hashtable<?, ?> var3) throws NamingException {
      FactoryEnumeration var4 = ResourceManager.getFactories("java.naming.factory.state", var3, var2);
      if (var4 == null) {
         return var0;
      } else {
         StateFactory var5;
         Object var6;
         for(var6 = null; var6 == null && var4.hasMore(); var6 = var5.getStateToBind(var0, var1, var2, var3)) {
            var5 = (StateFactory)var4.next();
         }

         return var6 != null ? var6 : var0;
      }
   }
}
