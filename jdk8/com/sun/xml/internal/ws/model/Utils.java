package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Utils {
   private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
   static final Navigator<Type, Class, Field, Method> REFLECTION_NAVIGATOR;

   private Utils() {
   }

   static {
      try {
         final Class refNav = Class.forName("com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator");
         Method getInstance = (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
            public Method run() {
               try {
                  Method getInstance = refNav.getDeclaredMethod("getInstance");
                  getInstance.setAccessible(true);
                  return getInstance;
               } catch (NoSuchMethodException var2) {
                  throw new IllegalStateException("ReflectionNavigator.getInstance can't be found");
               }
            }
         });
         REFLECTION_NAVIGATOR = (Navigator)getInstance.invoke((Object)null);
      } catch (ClassNotFoundException var2) {
         throw new IllegalStateException("Can't find ReflectionNavigator class");
      } catch (InvocationTargetException var3) {
         throw new IllegalStateException("ReflectionNavigator.getInstance throws the exception");
      } catch (IllegalAccessException var4) {
         throw new IllegalStateException("ReflectionNavigator.getInstance method is inaccessible");
      } catch (SecurityException var5) {
         LOGGER.log(Level.FINE, (String)"Unable to access ReflectionNavigator.getInstance", (Throwable)var5);
         throw var5;
      }
   }
}
