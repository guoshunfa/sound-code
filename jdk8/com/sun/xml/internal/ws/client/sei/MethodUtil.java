package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

class MethodUtil {
   private static final Logger LOGGER = Logger.getLogger(MethodUtil.class.getName());
   private static final Method INVOKE_METHOD;

   static Object invoke(Object target, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
      if (INVOKE_METHOD != null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Invoking method using sun.reflect.misc.MethodUtil");
         }

         try {
            return INVOKE_METHOD.invoke((Object)null, method, target, args);
         } catch (InvocationTargetException var4) {
            throw unwrapException(var4);
         }
      } else {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Invoking method directly, probably non-Oracle JVM");
         }

         return method.invoke(target, args);
      }
   }

   private static InvocationTargetException unwrapException(InvocationTargetException ite) {
      Throwable targetException = ite.getTargetException();
      if (targetException != null && targetException instanceof InvocationTargetException) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Unwrapping invocation target exception");
         }

         return (InvocationTargetException)targetException;
      } else {
         return ite;
      }
   }

   static {
      Method method;
      try {
         Class<?> clazz = Class.forName("sun.reflect.misc.MethodUtil");
         method = clazz.getMethod("invoke", Method.class, Object.class, Object[].class);
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil found; it will be used to invoke methods.");
         }
      } catch (Throwable var2) {
         method = null;
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil not found, probably non-Oracle JVM");
         }
      }

      INVOKE_METHOD = method;
   }
}
