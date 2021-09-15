package java.rmi.server;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.UnexpectedException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;
import sun.rmi.server.Util;
import sun.rmi.server.WeakClassHashMap;

public class RemoteObjectInvocationHandler extends RemoteObject implements InvocationHandler {
   private static final long serialVersionUID = 2L;
   private static final boolean allowFinalizeInvocation;
   private static final RemoteObjectInvocationHandler.MethodToHash_Maps methodToHash_Maps;

   public RemoteObjectInvocationHandler(RemoteRef var1) {
      super(var1);
      if (var1 == null) {
         throw new NullPointerException();
      }
   }

   public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
      if (!Proxy.isProxyClass(var1.getClass())) {
         throw new IllegalArgumentException("not a proxy");
      } else if (Proxy.getInvocationHandler(var1) != this) {
         throw new IllegalArgumentException("handler mismatch");
      } else if (var2.getDeclaringClass() == Object.class) {
         return this.invokeObjectMethod(var1, var2, var3);
      } else {
         return "finalize".equals(var2.getName()) && var2.getParameterCount() == 0 && !allowFinalizeInvocation ? null : this.invokeRemoteMethod(var1, var2, var3);
      }
   }

   private Object invokeObjectMethod(Object var1, Method var2, Object[] var3) {
      String var4 = var2.getName();
      if (var4.equals("hashCode")) {
         return this.hashCode();
      } else if (!var4.equals("equals")) {
         if (var4.equals("toString")) {
            return this.proxyToString(var1);
         } else {
            throw new IllegalArgumentException("unexpected Object method: " + var2);
         }
      } else {
         Object var5 = var3[0];
         InvocationHandler var6;
         return var1 == var5 || var5 != null && Proxy.isProxyClass(var5.getClass()) && (var6 = Proxy.getInvocationHandler(var5)) instanceof RemoteObjectInvocationHandler && this.equals(var6);
      }
   }

   private Object invokeRemoteMethod(Object var1, Method var2, Object[] var3) throws Exception {
      try {
         if (!(var1 instanceof Remote)) {
            throw new IllegalArgumentException("proxy not Remote instance");
         } else {
            return this.ref.invoke((Remote)var1, var2, var3, getMethodHash(var2));
         }
      } catch (Exception var12) {
         Object var4 = var12;
         if (!(var12 instanceof RuntimeException)) {
            Class var5 = var1.getClass();

            try {
               var2 = var5.getMethod(var2.getName(), var2.getParameterTypes());
            } catch (NoSuchMethodException var11) {
               throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(var11);
            }

            Class var6 = var12.getClass();
            Class[] var7 = var2.getExceptionTypes();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Class var10 = var7[var9];
               if (var10.isAssignableFrom(var6)) {
                  throw var12;
               }
            }

            var4 = new UnexpectedException("unexpected exception", var12);
         }

         throw (Exception)var4;
      }
   }

   private String proxyToString(Object var1) {
      Class[] var2 = var1.getClass().getInterfaces();
      if (var2.length == 0) {
         return "Proxy[" + this + "]";
      } else {
         String var3 = var2[0].getName();
         if (var3.equals("java.rmi.Remote") && var2.length > 1) {
            var3 = var2[1].getName();
         }

         int var4 = var3.lastIndexOf(46);
         if (var4 >= 0) {
            var3 = var3.substring(var4 + 1);
         }

         return "Proxy[" + var3 + "," + this + "]";
      }
   }

   private void readObjectNoData() throws InvalidObjectException {
      throw new InvalidObjectException("no data in stream; class: " + this.getClass().getName());
   }

   private static long getMethodHash(Method var0) {
      return (Long)((Map)methodToHash_Maps.get(var0.getDeclaringClass())).get(var0);
   }

   static {
      final String var0 = "sun.rmi.server.invocationhandler.allowFinalizeInvocation";
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(var0);
         }
      });
      if ("".equals(var1)) {
         allowFinalizeInvocation = true;
      } else {
         allowFinalizeInvocation = Boolean.parseBoolean(var1);
      }

      methodToHash_Maps = new RemoteObjectInvocationHandler.MethodToHash_Maps();
   }

   private static class MethodToHash_Maps extends WeakClassHashMap<Map<Method, Long>> {
      MethodToHash_Maps() {
      }

      protected Map<Method, Long> computeValue(Class<?> var1) {
         return new WeakHashMap<Method, Long>() {
            public synchronized Long get(Object var1) {
               Long var2 = (Long)super.get(var1);
               if (var2 == null) {
                  Method var3 = (Method)var1;
                  var2 = Util.computeMethodHash(var3);
                  this.put(var3, var2);
               }

               return var2;
            }
         };
      }
   }
}
