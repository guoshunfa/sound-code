package com.sun.jndi.url.iiop;

import com.sun.jndi.cosnaming.CNCtx;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ResolveResult;

public class iiopURLContextFactory implements ObjectFactory {
   public Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws Exception {
      if (var1 == null) {
         return new iiopURLContext(var4);
      } else if (var1 instanceof String) {
         return getUsingURL((String)var1, var4);
      } else if (var1 instanceof String[]) {
         return getUsingURLs((String[])((String[])var1), var4);
      } else {
         throw new IllegalArgumentException("iiopURLContextFactory.getObjectInstance: argument must be a URL String or array of URLs");
      }
   }

   static ResolveResult getUsingURLIgnoreRest(String var0, Hashtable<?, ?> var1) throws NamingException {
      return CNCtx.createUsingURL(var0, var1);
   }

   private static Object getUsingURL(String var0, Hashtable<?, ?> var1) throws NamingException {
      ResolveResult var2 = getUsingURLIgnoreRest(var0, var1);
      Context var3 = (Context)var2.getResolvedObj();

      Object var4;
      try {
         var4 = var3.lookup(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   private static Object getUsingURLs(String[] var0, Hashtable<?, ?> var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         String var3 = var0[var2];

         try {
            Object var4 = getUsingURL(var3, var1);
            if (var4 != null) {
               return var4;
            }
         } catch (NamingException var5) {
         }
      }

      return null;
   }
}
