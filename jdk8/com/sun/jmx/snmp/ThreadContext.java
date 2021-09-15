package com.sun.jmx.snmp;

public class ThreadContext implements Cloneable {
   private ThreadContext previous;
   private String key;
   private Object value;
   private static ThreadLocal<ThreadContext> localContext = new ThreadLocal();

   private ThreadContext(ThreadContext var1, String var2, Object var3) {
      this.previous = var1;
      this.key = var2;
      this.value = var3;
   }

   public static Object get(String var0) throws IllegalArgumentException {
      ThreadContext var1 = contextContaining(var0);
      return var1 == null ? null : var1.value;
   }

   public static boolean contains(String var0) throws IllegalArgumentException {
      return contextContaining(var0) != null;
   }

   private static ThreadContext contextContaining(String var0) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("null key");
      } else {
         for(ThreadContext var1 = getContext(); var1 != null; var1 = var1.previous) {
            if (var0.equals(var1.key)) {
               return var1;
            }
         }

         return null;
      }
   }

   public static ThreadContext push(String var0, Object var1) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("null key");
      } else {
         ThreadContext var2 = getContext();
         if (var2 == null) {
            var2 = new ThreadContext((ThreadContext)null, (String)null, (Object)null);
         }

         ThreadContext var3 = new ThreadContext(var2, var0, var1);
         setContext(var3);
         return var2;
      }
   }

   public static ThreadContext getThreadContext() {
      return getContext();
   }

   public static void restore(ThreadContext var0) throws NullPointerException, IllegalArgumentException {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         for(ThreadContext var1 = getContext(); var1 != var0; var1 = var1.previous) {
            if (var1 == null) {
               throw new IllegalArgumentException("Restored context is not contained in current context");
            }
         }

         if (var0.key == null) {
            var0 = null;
         }

         setContext(var0);
      }
   }

   public void setInitialContext(ThreadContext var1) throws IllegalArgumentException {
      if (getContext() != null) {
         throw new IllegalArgumentException("previous context not empty");
      } else {
         setContext(var1);
      }
   }

   private static ThreadContext getContext() {
      return (ThreadContext)localContext.get();
   }

   private static void setContext(ThreadContext var0) {
      localContext.set(var0);
   }
}
