package java.beans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class EventHandler implements InvocationHandler {
   private Object target;
   private String action;
   private final String eventPropertyName;
   private final String listenerMethodName;
   private final AccessControlContext acc = AccessController.getContext();

   @ConstructorProperties({"target", "action", "eventPropertyName", "listenerMethodName"})
   public EventHandler(Object var1, String var2, String var3, String var4) {
      this.target = var1;
      this.action = var2;
      if (var1 == null) {
         throw new NullPointerException("target must be non-null");
      } else if (var2 == null) {
         throw new NullPointerException("action must be non-null");
      } else {
         this.eventPropertyName = var3;
         this.listenerMethodName = var4;
      }
   }

   public Object getTarget() {
      return this.target;
   }

   public String getAction() {
      return this.action;
   }

   public String getEventPropertyName() {
      return this.eventPropertyName;
   }

   public String getListenerMethodName() {
      return this.listenerMethodName;
   }

   private Object applyGetters(Object var1, String var2) {
      if (var2 != null && !var2.equals("")) {
         int var3 = var2.indexOf(46);
         if (var3 == -1) {
            var3 = var2.length();
         }

         String var4 = var2.substring(0, var3);
         String var5 = var2.substring(Math.min(var3 + 1, var2.length()));

         try {
            Method var6 = null;
            if (var1 != null) {
               var6 = Statement.getMethod(var1.getClass(), "get" + NameGenerator.capitalize(var4));
               if (var6 == null) {
                  var6 = Statement.getMethod(var1.getClass(), "is" + NameGenerator.capitalize(var4));
               }

               if (var6 == null) {
                  var6 = Statement.getMethod(var1.getClass(), var4);
               }
            }

            if (var6 == null) {
               throw new RuntimeException("No method called: " + var4 + " defined on " + var1);
            } else {
               Object var7 = MethodUtil.invoke(var6, var1, new Object[0]);
               return this.applyGetters(var7, var5);
            }
         } catch (Exception var8) {
            throw new RuntimeException("Failed to call method: " + var4 + " on " + var1, var8);
         }
      } else {
         return var1;
      }
   }

   public Object invoke(final Object var1, final Method var2, final Object[] var3) {
      AccessControlContext var4 = this.acc;
      if (var4 == null && System.getSecurityManager() != null) {
         throw new SecurityException("AccessControlContext is not set");
      } else {
         return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               return EventHandler.this.invokeInternal(var1, var2, var3);
            }
         }, var4);
      }
   }

   private Object invokeInternal(Object var1, Method var2, Object[] var3) {
      String var4 = var2.getName();
      if (var2.getDeclaringClass() == Object.class) {
         if (var4.equals("hashCode")) {
            return new Integer(System.identityHashCode(var1));
         }

         if (var4.equals("equals")) {
            return var1 == var3[0] ? Boolean.TRUE : Boolean.FALSE;
         }

         if (var4.equals("toString")) {
            return var1.getClass().getName() + '@' + Integer.toHexString(var1.hashCode());
         }
      }

      if (this.listenerMethodName != null && !this.listenerMethodName.equals(var4)) {
         return null;
      } else {
         Class[] var5 = null;
         Object[] var6 = null;
         if (this.eventPropertyName == null) {
            var6 = new Object[0];
            var5 = new Class[0];
         } else {
            Object var7 = this.applyGetters(var3[0], this.getEventPropertyName());
            var6 = new Object[]{var7};
            var5 = new Class[]{var7 == null ? null : var7.getClass()};
         }

         try {
            int var12 = this.action.lastIndexOf(46);
            if (var12 != -1) {
               this.target = this.applyGetters(this.target, this.action.substring(0, var12));
               this.action = this.action.substring(var12 + 1);
            }

            Method var13 = Statement.getMethod(this.target.getClass(), this.action, var5);
            if (var13 == null) {
               var13 = Statement.getMethod(this.target.getClass(), "set" + NameGenerator.capitalize(this.action), var5);
            }

            if (var13 == null) {
               String var9 = var5.length == 0 ? " with no arguments" : " with argument " + var5[0];
               throw new RuntimeException("No method called " + this.action + " on " + this.target.getClass() + var9);
            } else {
               return MethodUtil.invoke(var13, this.target, var6);
            }
         } catch (IllegalAccessException var10) {
            throw new RuntimeException(var10);
         } catch (InvocationTargetException var11) {
            Throwable var8 = var11.getTargetException();
            throw var8 instanceof RuntimeException ? (RuntimeException)var8 : new RuntimeException(var8);
         }
      }
   }

   public static <T> T create(Class<T> var0, Object var1, String var2) {
      return create(var0, var1, var2, (String)null, (String)null);
   }

   public static <T> T create(Class<T> var0, Object var1, String var2, String var3) {
      return create(var0, var1, var2, var3, (String)null);
   }

   public static <T> T create(Class<T> var0, Object var1, String var2, String var3, String var4) {
      final EventHandler var5 = new EventHandler(var1, var2, var3, var4);
      if (var0 == null) {
         throw new NullPointerException("listenerInterface must be non-null");
      } else {
         final ClassLoader var6 = getClassLoader(var0);
         final Class[] var7 = new Class[]{var0};
         return AccessController.doPrivileged(new PrivilegedAction<T>() {
            public T run() {
               return Proxy.newProxyInstance(var6, var7, var5);
            }
         });
      }
   }

   private static ClassLoader getClassLoader(Class<?> var0) {
      ReflectUtil.checkPackageAccess(var0);
      ClassLoader var1 = var0.getClassLoader();
      if (var1 == null) {
         var1 = Thread.currentThread().getContextClassLoader();
         if (var1 == null) {
            var1 = ClassLoader.getSystemClassLoader();
         }
      }

      return var1;
   }
}
