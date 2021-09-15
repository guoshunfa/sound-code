package com.sun.jndi.rmi.registry;

import com.sun.jndi.url.rmi.rmiURLContextFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public class RegistryContextFactory implements ObjectFactory, InitialContextFactory {
   public static final String ADDRESS_TYPE = "URL";

   public Context getInitialContext(Hashtable<?, ?> var1) throws NamingException {
      if (var1 != null) {
         var1 = (Hashtable)var1.clone();
      }

      return URLToContext(getInitCtxURL(var1), var1);
   }

   public Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws NamingException {
      if (!isRegistryRef(var1)) {
         return null;
      } else {
         Object var5 = URLsToObject(getURLs((Reference)var1), var4);
         if (var5 instanceof RegistryContext) {
            RegistryContext var6 = (RegistryContext)var5;
            var6.reference = (Reference)var1;
         }

         return var5;
      }
   }

   private static Context URLToContext(String var0, Hashtable<?, ?> var1) throws NamingException {
      rmiURLContextFactory var2 = new rmiURLContextFactory();
      Object var3 = var2.getObjectInstance(var0, (Name)null, (Context)null, var1);
      if (var3 instanceof Context) {
         return (Context)var3;
      } else {
         throw new NotContextException(var0);
      }
   }

   private static Object URLsToObject(String[] var0, Hashtable<?, ?> var1) throws NamingException {
      rmiURLContextFactory var2 = new rmiURLContextFactory();
      return var2.getObjectInstance(var0, (Name)null, (Context)null, var1);
   }

   private static String getInitCtxURL(Hashtable<?, ?> var0) {
      String var2 = null;
      if (var0 != null) {
         var2 = (String)var0.get("java.naming.provider.url");
      }

      return var2 != null ? var2 : "rmi:";
   }

   private static boolean isRegistryRef(Object var0) {
      if (!(var0 instanceof Reference)) {
         return false;
      } else {
         String var1 = RegistryContextFactory.class.getName();
         Reference var2 = (Reference)var0;
         return var1.equals(var2.getFactoryClassName());
      }
   }

   private static String[] getURLs(Reference var0) throws NamingException {
      int var1 = 0;
      String[] var2 = new String[var0.size()];
      Enumeration var3 = var0.getAll();

      while(var3.hasMoreElements()) {
         RefAddr var4 = (RefAddr)var3.nextElement();
         if (var4 instanceof StringRefAddr && var4.getType().equals("URL")) {
            var2[var1++] = (String)var4.getContent();
         }
      }

      if (var1 == 0) {
         throw new ConfigurationException("Reference contains no valid addresses");
      } else if (var1 == var0.size()) {
         return var2;
      } else {
         String[] var5 = new String[var1];
         System.arraycopy(var2, 0, var5, 0, var1);
         return var5;
      }
   }
}
