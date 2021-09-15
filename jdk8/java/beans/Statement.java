package java.beans;

import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.ConstructorFinder;
import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.reflect.misc.MethodUtil;

public class Statement {
   private static Object[] emptyArray = new Object[0];
   static ExceptionListener defaultExceptionListener = new ExceptionListener() {
      public void exceptionThrown(Exception var1) {
         System.err.println((Object)var1);
         System.err.println("Continuing ...");
      }
   };
   private final AccessControlContext acc = AccessController.getContext();
   private final Object target;
   private final String methodName;
   private final Object[] arguments;
   ClassLoader loader;

   @ConstructorProperties({"target", "methodName", "arguments"})
   public Statement(Object var1, String var2, Object[] var3) {
      this.target = var1;
      this.methodName = var2;
      this.arguments = var3 == null ? emptyArray : (Object[])var3.clone();
   }

   public Object getTarget() {
      return this.target;
   }

   public String getMethodName() {
      return this.methodName;
   }

   public Object[] getArguments() {
      return (Object[])this.arguments.clone();
   }

   public void execute() throws Exception {
      this.invoke();
   }

   Object invoke() throws Exception {
      AccessControlContext var1 = this.acc;
      if (var1 == null && System.getSecurityManager() != null) {
         throw new SecurityException("AccessControlContext is not set");
      } else {
         try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws Exception {
                  return Statement.this.invokeInternal();
               }
            }, var1);
         } catch (PrivilegedActionException var3) {
            throw var3.getException();
         }
      }
   }

   private Object invokeInternal() throws Exception {
      Object var1 = this.getTarget();
      String var2 = this.getMethodName();
      if (var1 != null && var2 != null) {
         Object[] var3 = this.getArguments();
         if (var3 == null) {
            var3 = emptyArray;
         }

         if (var1 == Class.class && var2.equals("forName")) {
            return ClassFinder.resolveClass((String)var3[0], this.loader);
         } else {
            Class[] var4 = new Class[var3.length];

            for(int var5 = 0; var5 < var3.length; ++var5) {
               var4[var5] = var3[var5] == null ? null : var3[var5].getClass();
            }

            Object var11 = null;
            if (var1 instanceof Class) {
               if (var2.equals("new")) {
                  var2 = "newInstance";
               }

               if (var2.equals("newInstance") && ((Class)var1).isArray()) {
                  Object var12 = Array.newInstance(((Class)var1).getComponentType(), var3.length);

                  for(int var13 = 0; var13 < var3.length; ++var13) {
                     Array.set(var12, var13, var3[var13]);
                  }

                  return var12;
               }

               if (var2.equals("newInstance") && var3.length != 0) {
                  if (var1 == Character.class && var3.length == 1 && var4[0] == String.class) {
                     return new Character(((String)var3[0]).charAt(0));
                  }

                  try {
                     var11 = ConstructorFinder.findConstructor((Class)var1, var4);
                  } catch (NoSuchMethodException var8) {
                     var11 = null;
                  }
               }

               if (var11 == null && var1 != Class.class) {
                  var11 = getMethod((Class)var1, var2, var4);
               }

               if (var11 == null) {
                  var11 = getMethod(Class.class, var2, var4);
               }
            } else {
               if (var1.getClass().isArray() && (var2.equals("set") || var2.equals("get"))) {
                  int var6 = (Integer)var3[0];
                  if (var2.equals("get")) {
                     return Array.get(var1, var6);
                  }

                  Array.set(var1, var6, var3[1]);
                  return null;
               }

               var11 = getMethod(var1.getClass(), var2, var4);
            }

            if (var11 != null) {
               try {
                  return var11 instanceof Method ? MethodUtil.invoke((Method)var11, var1, var3) : ((Constructor)var11).newInstance(var3);
               } catch (IllegalAccessException var9) {
                  throw new Exception("Statement cannot invoke: " + var2 + " on " + var1.getClass(), var9);
               } catch (InvocationTargetException var10) {
                  Throwable var7 = var10.getTargetException();
                  if (var7 instanceof Exception) {
                     throw (Exception)var7;
                  } else {
                     throw var10;
                  }
               }
            } else {
               throw new NoSuchMethodException(this.toString());
            }
         }
      } else {
         throw new NullPointerException((var1 == null ? "target" : "methodName") + " should not be null");
      }
   }

   String instanceName(Object var1) {
      if (var1 == null) {
         return "null";
      } else {
         return var1.getClass() == String.class ? "\"" + (String)var1 + "\"" : NameGenerator.unqualifiedClassName(var1.getClass());
      }
   }

   public String toString() {
      Object var1 = this.getTarget();
      String var2 = this.getMethodName();
      Object[] var3 = this.getArguments();
      if (var3 == null) {
         var3 = emptyArray;
      }

      StringBuffer var4 = new StringBuffer(this.instanceName(var1) + "." + var2 + "(");
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         var4.append(this.instanceName(var3[var6]));
         if (var6 != var5 - 1) {
            var4.append(", ");
         }
      }

      var4.append(");");
      return var4.toString();
   }

   static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
      try {
         return MethodFinder.findMethod(var0, var1, var2);
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }
}
