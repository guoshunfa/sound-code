package com.sun.jndi.url.rmi;

import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

public class rmiURLContextFactory implements ObjectFactory {
   public Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws NamingException {
      if (var1 == null) {
         return new rmiURLContext(var4);
      } else if (var1 instanceof String) {
         return getUsingURL((String)var1, var4);
      } else if (var1 instanceof String[]) {
         return getUsingURLs((String[])((String[])var1), var4);
      } else {
         throw new ConfigurationException("rmiURLContextFactory.getObjectInstance: argument must be an RMI URL String or an array of them");
      }
   }

   private static Object getUsingURL(String var0, Hashtable<?, ?> var1) throws NamingException {
      rmiURLContext var2 = new rmiURLContext(var1);

      Object var3;
      try {
         var3 = var2.lookup(var0);
      } finally {
         var2.close();
      }

      return var3;
   }

   private static Object getUsingURLs(String[] var0, Hashtable<?, ?> var1) throws NamingException {
      if (var0.length == 0) {
         throw new ConfigurationException("rmiURLContextFactory: empty URL array");
      } else {
         rmiURLContext var2 = new rmiURLContext(var1);

         try {
            NamingException var3 = null;
            int var4 = 0;

            while(var4 < var0.length) {
               try {
                  Object var5 = var2.lookup(var0[var4]);
                  return var5;
               } catch (NamingException var9) {
                  var3 = var9;
                  ++var4;
               }
            }

            throw var3;
         } finally {
            var2.close();
         }
      }
   }
}
