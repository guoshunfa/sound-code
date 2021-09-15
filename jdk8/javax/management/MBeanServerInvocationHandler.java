package javax.management;

import com.sun.jmx.mbeanserver.MXBeanProxy;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.WeakHashMap;

public class MBeanServerInvocationHandler implements InvocationHandler {
   private static final WeakHashMap<Class<?>, WeakReference<MXBeanProxy>> mxbeanProxies = new WeakHashMap();
   private final MBeanServerConnection connection;
   private final ObjectName objectName;
   private final boolean isMXBean;

   public MBeanServerInvocationHandler(MBeanServerConnection var1, ObjectName var2) {
      this(var1, var2, false);
   }

   public MBeanServerInvocationHandler(MBeanServerConnection var1, ObjectName var2, boolean var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null connection");
      } else if (Proxy.isProxyClass(var1.getClass()) && MBeanServerInvocationHandler.class.isAssignableFrom(Proxy.getInvocationHandler(var1).getClass())) {
         throw new IllegalArgumentException("Wrapping MBeanServerInvocationHandler");
      } else if (var2 == null) {
         throw new IllegalArgumentException("Null object name");
      } else {
         this.connection = var1;
         this.objectName = var2;
         this.isMXBean = var3;
      }
   }

   public MBeanServerConnection getMBeanServerConnection() {
      return this.connection;
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }

   public boolean isMXBean() {
      return this.isMXBean;
   }

   public static <T> T newProxyInstance(MBeanServerConnection var0, ObjectName var1, Class<T> var2, boolean var3) {
      return JMX.newMBeanProxy(var0, var1, var2, var3);
   }

   public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
      Class var4 = var2.getDeclaringClass();
      if (!var4.equals(NotificationBroadcaster.class) && !var4.equals(NotificationEmitter.class)) {
         if (this.shouldDoLocally(var1, var2)) {
            return this.doLocally(var1, var2, var3);
         } else {
            try {
               if (this.isMXBean()) {
                  MXBeanProxy var14 = findMXBeanProxy(var4);
                  return var14.invoke(this.connection, this.objectName, var2, var3);
               } else {
                  String var5 = var2.getName();
                  Class[] var6 = var2.getParameterTypes();
                  Class var7 = var2.getReturnType();
                  int var8 = var3 == null ? 0 : var3.length;
                  if (var5.startsWith("get") && var5.length() > 3 && var8 == 0 && !var7.equals(Void.TYPE)) {
                     return this.connection.getAttribute(this.objectName, var5.substring(3));
                  } else if (!var5.startsWith("is") || var5.length() <= 2 || var8 != 0 || !var7.equals(Boolean.TYPE) && !var7.equals(Boolean.class)) {
                     if (var5.startsWith("set") && var5.length() > 3 && var8 == 1 && var7.equals(Void.TYPE)) {
                        Attribute var15 = new Attribute(var5.substring(3), var3[0]);
                        this.connection.setAttribute(this.objectName, var15);
                        return null;
                     } else {
                        String[] var9 = new String[var6.length];

                        for(int var10 = 0; var10 < var6.length; ++var10) {
                           var9[var10] = var6[var10].getName();
                        }

                        return this.connection.invoke(this.objectName, var5, var3, var9);
                     }
                  } else {
                     return this.connection.getAttribute(this.objectName, var5.substring(2));
                  }
               }
            } catch (MBeanException var11) {
               throw var11.getTargetException();
            } catch (RuntimeMBeanException var12) {
               throw var12.getTargetException();
            } catch (RuntimeErrorException var13) {
               throw var13.getTargetError();
            }
         }
      } else {
         return this.invokeBroadcasterMethod(var1, var2, var3);
      }
   }

   private static MXBeanProxy findMXBeanProxy(Class<?> var0) {
      synchronized(mxbeanProxies) {
         WeakReference var2 = (WeakReference)mxbeanProxies.get(var0);
         MXBeanProxy var3 = var2 == null ? null : (MXBeanProxy)var2.get();
         if (var3 == null) {
            try {
               var3 = new MXBeanProxy(var0);
            } catch (IllegalArgumentException var8) {
               String var5 = "Cannot make MXBean proxy for " + var0.getName() + ": " + var8.getMessage();
               IllegalArgumentException var6 = new IllegalArgumentException(var5, var8.getCause());
               var6.setStackTrace(var8.getStackTrace());
               throw var6;
            }

            mxbeanProxies.put(var0, new WeakReference(var3));
         }

         return var3;
      }
   }

   private Object invokeBroadcasterMethod(Object var1, Method var2, Object[] var3) throws Exception {
      String var4 = var2.getName();
      int var5 = var3 == null ? 0 : var3.length;
      NotificationFilter var7;
      Object var8;
      NotificationListener var10;
      if (var4.equals("addNotificationListener")) {
         if (var5 != 3) {
            String var11 = "Bad arg count to addNotificationListener: " + var5;
            throw new IllegalArgumentException(var11);
         } else {
            var10 = (NotificationListener)var3[0];
            var7 = (NotificationFilter)var3[1];
            var8 = var3[2];
            this.connection.addNotificationListener(this.objectName, var10, var7, var8);
            return null;
         }
      } else if (var4.equals("removeNotificationListener")) {
         var10 = (NotificationListener)var3[0];
         switch(var5) {
         case 1:
            this.connection.removeNotificationListener(this.objectName, var10);
            return null;
         case 3:
            var7 = (NotificationFilter)var3[1];
            var8 = var3[2];
            this.connection.removeNotificationListener(this.objectName, var10, var7, var8);
            return null;
         default:
            String var9 = "Bad arg count to removeNotificationListener: " + var5;
            throw new IllegalArgumentException(var9);
         }
      } else if (var4.equals("getNotificationInfo")) {
         if (var3 != null) {
            throw new IllegalArgumentException("getNotificationInfo has args");
         } else {
            MBeanInfo var6 = this.connection.getMBeanInfo(this.objectName);
            return var6.getNotifications();
         }
      } else {
         throw new IllegalArgumentException("Bad method name: " + var4);
      }
   }

   private boolean shouldDoLocally(Object var1, Method var2) {
      String var3 = var2.getName();
      if ((var3.equals("hashCode") || var3.equals("toString")) && var2.getParameterTypes().length == 0 && isLocal(var1, var2)) {
         return true;
      } else if (var3.equals("equals") && Arrays.equals((Object[])var2.getParameterTypes(), (Object[])(new Class[]{Object.class})) && isLocal(var1, var2)) {
         return true;
      } else {
         return var3.equals("finalize") && var2.getParameterTypes().length == 0;
      }
   }

   private Object doLocally(Object var1, Method var2, Object[] var3) {
      String var4 = var2.getName();
      if (!var4.equals("equals")) {
         if (var4.equals("toString")) {
            return (this.isMXBean() ? "MX" : "M") + "BeanProxy(" + this.connection + "[" + this.objectName + "])";
         } else if (var4.equals("hashCode")) {
            return this.objectName.hashCode() + this.connection.hashCode();
         } else if (var4.equals("finalize")) {
            return null;
         } else {
            throw new RuntimeException("Unexpected method name: " + var4);
         }
      } else if (this == var3[0]) {
         return true;
      } else if (!(var3[0] instanceof Proxy)) {
         return false;
      } else {
         InvocationHandler var5 = Proxy.getInvocationHandler(var3[0]);
         if (var5 != null && var5 instanceof MBeanServerInvocationHandler) {
            MBeanServerInvocationHandler var6 = (MBeanServerInvocationHandler)var5;
            return this.connection.equals(var6.connection) && this.objectName.equals(var6.objectName) && var1.getClass().equals(var3[0].getClass());
         } else {
            return false;
         }
      }
   }

   private static boolean isLocal(Object var0, Method var1) {
      Class[] var2 = var0.getClass().getInterfaces();
      if (var2 == null) {
         return true;
      } else {
         String var3 = var1.getName();
         Class[] var4 = var1.getParameterTypes();
         Class[] var5 = var2;
         int var6 = var2.length;
         int var7 = 0;

         while(var7 < var6) {
            Class var8 = var5[var7];

            try {
               var8.getMethod(var3, var4);
               return false;
            } catch (NoSuchMethodException var10) {
               ++var7;
            }
         }

         return true;
      }
   }
}
