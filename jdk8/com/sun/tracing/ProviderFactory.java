package com.sun.tracing;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import sun.security.action.GetPropertyAction;
import sun.tracing.MultiplexProviderFactory;
import sun.tracing.NullProviderFactory;
import sun.tracing.PrintStreamProviderFactory;
import sun.tracing.dtrace.DTraceProviderFactory;

public abstract class ProviderFactory {
   protected ProviderFactory() {
   }

   public abstract <T extends Provider> T createProvider(Class<T> var1);

   public static ProviderFactory getDefaultFactory() {
      HashSet var0 = new HashSet();
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("com.sun.tracing.dtrace")));
      if ((var1 == null || !var1.equals("disable")) && DTraceProviderFactory.isSupported()) {
         var0.add(new DTraceProviderFactory());
      }

      var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.tracing.stream")));
      if (var1 != null) {
         String[] var2 = var1.split(",");
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            PrintStream var6 = getPrintStreamFromSpec(var5);
            if (var6 != null) {
               var0.add(new PrintStreamProviderFactory(var6));
            }
         }
      }

      if (var0.size() == 0) {
         return new NullProviderFactory();
      } else {
         return (ProviderFactory)(var0.size() == 1 ? ((ProviderFactory[])var0.toArray(new ProviderFactory[1]))[0] : new MultiplexProviderFactory(var0));
      }
   }

   private static PrintStream getPrintStreamFromSpec(final String var0) {
      try {
         final int var1 = var0.lastIndexOf(46);
         final Class var2 = Class.forName(var0.substring(0, var1));
         Field var3 = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
            public Field run() throws NoSuchFieldException {
               return var2.getField(var0.substring(var1 + 1));
            }
         });
         return (PrintStream)var3.get((Object)null);
      } catch (ClassNotFoundException var4) {
         throw new AssertionError(var4);
      } catch (IllegalAccessException var5) {
         throw new AssertionError(var5);
      } catch (PrivilegedActionException var6) {
         throw new AssertionError(var6);
      }
   }
}
