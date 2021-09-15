package com.sun.jndi.url.rmi;

import com.sun.jndi.rmi.registry.RegistryContext;
import com.sun.jndi.toolkit.url.GenericURLContext;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class rmiURLContext extends GenericURLContext {
   public rmiURLContext(Hashtable<?, ?> var1) {
      super(var1);
   }

   protected ResolveResult getRootURLContext(String var1, Hashtable<?, ?> var2) throws NamingException {
      if (!var1.startsWith("rmi:")) {
         throw new IllegalArgumentException("rmiURLContext: name is not an RMI URL: " + var1);
      } else {
         String var3 = null;
         int var4 = -1;
         String var5 = null;
         int var6 = 4;
         if (var1.startsWith("//", var6)) {
            var6 += 2;
            int var7 = var1.indexOf(47, var6);
            if (var7 < 0) {
               var7 = var1.length();
            }

            int var8;
            if (var1.startsWith("[", var6)) {
               var8 = var1.indexOf(93, var6 + 1);
               if (var8 < 0 || var8 > var7) {
                  throw new IllegalArgumentException("rmiURLContext: name is an Invalid URL: " + var1);
               }

               var3 = var1.substring(var6, var8 + 1);
               var6 = var8 + 1;
            } else {
               var8 = var1.indexOf(58, var6);
               int var9 = var8 >= 0 && var8 <= var7 ? var8 : var7;
               if (var6 < var9) {
                  var3 = var1.substring(var6, var9);
               }

               var6 = var9;
            }

            if (var6 + 1 < var7) {
               if (!var1.startsWith(":", var6)) {
                  throw new IllegalArgumentException("rmiURLContext: name is an Invalid URL: " + var1);
               }

               ++var6;
               var4 = Integer.parseInt(var1.substring(var6, var7));
            }

            var6 = var7;
         }

         if ("".equals(var3)) {
            var3 = null;
         }

         if (var1.startsWith("/", var6)) {
            ++var6;
         }

         if (var6 < var1.length()) {
            var5 = var1.substring(var6);
         }

         CompositeName var11 = new CompositeName();
         if (var5 != null) {
            var11.add(var5);
         }

         RegistryContext var10 = new RegistryContext(var3, var4, var2);
         return new ResolveResult(var10, var11);
      }
   }
}
