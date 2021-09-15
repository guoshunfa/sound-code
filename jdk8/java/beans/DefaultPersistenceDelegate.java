package java.beans;

import java.awt.Component;
import java.awt.event.ComponentListener;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EventListener;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class DefaultPersistenceDelegate extends PersistenceDelegate {
   private static final String[] EMPTY = new String[0];
   private final String[] constructor;
   private Boolean definesEquals;

   public DefaultPersistenceDelegate() {
      this.constructor = EMPTY;
   }

   public DefaultPersistenceDelegate(String[] var1) {
      this.constructor = var1 == null ? EMPTY : (String[])var1.clone();
   }

   private static boolean definesEquals(Class<?> var0) {
      try {
         return var0 == var0.getMethod("equals", Object.class).getDeclaringClass();
      } catch (NoSuchMethodException var2) {
         return false;
      }
   }

   private boolean definesEquals(Object var1) {
      if (this.definesEquals != null) {
         return this.definesEquals == Boolean.TRUE;
      } else {
         boolean var2 = definesEquals(var1.getClass());
         this.definesEquals = var2 ? Boolean.TRUE : Boolean.FALSE;
         return var2;
      }
   }

   protected boolean mutatesTo(Object var1, Object var2) {
      return this.constructor.length != 0 && this.definesEquals(var1) ? var1.equals(var2) : super.mutatesTo(var1, var2);
   }

   protected Expression instantiate(Object var1, Encoder var2) {
      int var3 = this.constructor.length;
      Class var4 = var1.getClass();
      Object[] var5 = new Object[var3];

      for(int var6 = 0; var6 < var3; ++var6) {
         try {
            Method var7 = this.findMethod(var4, this.constructor[var6]);
            var5[var6] = MethodUtil.invoke(var7, var1, new Object[0]);
         } catch (Exception var8) {
            var2.getExceptionListener().exceptionThrown(var8);
         }
      }

      return new Expression(var1, var1.getClass(), "new", var5);
   }

   private Method findMethod(Class<?> var1, String var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("Property name is null");
      } else {
         PropertyDescriptor var3 = getPropertyDescriptor(var1, var2);
         if (var3 == null) {
            throw new IllegalStateException("Could not find property by the name " + var2);
         } else {
            Method var4 = var3.getReadMethod();
            if (var4 == null) {
               throw new IllegalStateException("Could not find getter for the property " + var2);
            } else {
               return var4;
            }
         }
      }
   }

   private void doProperty(Class<?> var1, PropertyDescriptor var2, Object var3, Object var4, Encoder var5) throws Exception {
      Method var6 = var2.getReadMethod();
      Method var7 = var2.getWriteMethod();
      if (var6 != null && var7 != null) {
         Expression var8 = new Expression(var3, var6.getName(), new Object[0]);
         Expression var9 = new Expression(var4, var6.getName(), new Object[0]);
         Object var10 = var8.getValue();
         Object var11 = var9.getValue();
         var5.writeExpression(var8);
         if (!Objects.equals(var11, var5.get(var10))) {
            Object[] var12 = (Object[])((Object[])var2.getValue("enumerationValues"));
            if (var12 instanceof Object[] && Array.getLength(var12) % 3 == 0) {
               Object[] var13 = (Object[])((Object[])var12);

               for(int var14 = 0; var14 < var13.length; var14 += 3) {
                  try {
                     Field var15 = var1.getField((String)var13[var14]);
                     if (var15.get((Object)null).equals(var10)) {
                        var5.remove(var10);
                        var5.writeExpression(new Expression(var10, var15, "get", new Object[]{null}));
                     }
                  } catch (Exception var16) {
                  }
               }
            }

            invokeStatement(var3, var7.getName(), new Object[]{var10}, var5);
         }
      }

   }

   static void invokeStatement(Object var0, String var1, Object[] var2, Encoder var3) {
      var3.writeStatement(new Statement(var0, var1, var2));
   }

   private void initBean(Class<?> var1, Object var2, Object var3, Encoder var4) {
      Field[] var5 = var1.getFields();
      int var6 = var5.length;

      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         Field var8 = var5[var7];
         if (ReflectUtil.isPackageAccessible(var8.getDeclaringClass())) {
            int var9 = var8.getModifiers();
            if (!Modifier.isFinal(var9) && !Modifier.isStatic(var9) && !Modifier.isTransient(var9)) {
               try {
                  Expression var10 = new Expression(var8, "get", new Object[]{var2});
                  Expression var11 = new Expression(var8, "get", new Object[]{var3});
                  Object var12 = var10.getValue();
                  Object var13 = var11.getValue();
                  var4.writeExpression(var10);
                  if (!Objects.equals(var13, var4.get(var12))) {
                     var4.writeStatement(new Statement(var8, "set", new Object[]{var2, var12}));
                  }
               } catch (Exception var20) {
                  var4.getExceptionListener().exceptionThrown(var20);
               }
            }
         }
      }

      BeanInfo var21;
      try {
         var21 = Introspector.getBeanInfo(var1);
      } catch (IntrospectionException var19) {
         return;
      }

      PropertyDescriptor[] var22 = var21.getPropertyDescriptors();
      var7 = var22.length;

      int var24;
      for(var24 = 0; var24 < var7; ++var24) {
         PropertyDescriptor var25 = var22[var24];
         if (!var25.isTransient()) {
            try {
               this.doProperty(var1, var25, var2, var3, var4);
            } catch (Exception var18) {
               var4.getExceptionListener().exceptionThrown(var18);
            }
         }
      }

      if (Component.class.isAssignableFrom(var1)) {
         EventSetDescriptor[] var23 = var21.getEventSetDescriptors();
         var7 = var23.length;

         for(var24 = 0; var24 < var7; ++var24) {
            EventSetDescriptor var26 = var23[var24];
            if (!var26.isTransient()) {
               Class var27 = var26.getListenerType();
               if (var27 != ComponentListener.class && (var27 != ChangeListener.class || var1 != JMenuItem.class)) {
                  EventListener[] var28 = new EventListener[0];
                  EventListener[] var29 = new EventListener[0];

                  try {
                     Method var30 = var26.getGetListenerMethod();
                     var28 = (EventListener[])((EventListener[])MethodUtil.invoke(var30, var2, new Object[0]));
                     var29 = (EventListener[])((EventListener[])MethodUtil.invoke(var30, var3, new Object[0]));
                  } catch (Exception var17) {
                     try {
                        Method var14 = var1.getMethod("getListeners", Class.class);
                        var28 = (EventListener[])((EventListener[])MethodUtil.invoke(var14, var2, new Object[]{var27}));
                        var29 = (EventListener[])((EventListener[])MethodUtil.invoke(var14, var3, new Object[]{var27}));
                     } catch (Exception var16) {
                        return;
                     }
                  }

                  String var31 = var26.getAddListenerMethod().getName();

                  for(int var32 = var29.length; var32 < var28.length; ++var32) {
                     invokeStatement(var2, var31, new Object[]{var28[var32]}, var4);
                  }

                  String var33 = var26.getRemoveListenerMethod().getName();

                  for(int var15 = var28.length; var15 < var29.length; ++var15) {
                     invokeStatement(var2, var33, new Object[]{var29[var15]}, var4);
                  }
               }
            }
         }

      }
   }

   protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
      super.initialize(var1, var2, var3, var4);
      if (var2.getClass() == var1) {
         this.initBean(var1, var2, var3, var4);
      }

   }

   private static PropertyDescriptor getPropertyDescriptor(Class<?> var0, String var1) {
      try {
         PropertyDescriptor[] var2 = Introspector.getBeanInfo(var0).getPropertyDescriptors();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PropertyDescriptor var5 = var2[var4];
            if (var1.equals(var5.getName())) {
               return var5;
            }
         }
      } catch (IntrospectionException var6) {
      }

      return null;
   }
}
