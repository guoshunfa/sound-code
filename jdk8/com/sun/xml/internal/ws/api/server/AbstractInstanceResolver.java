package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class AbstractInstanceResolver<T> extends InstanceResolver<T> {
   protected static ResourceInjector getResourceInjector(WSEndpoint endpoint) {
      ResourceInjector ri = (ResourceInjector)endpoint.getContainer().getSPI(ResourceInjector.class);
      if (ri == null) {
         ri = ResourceInjector.STANDALONE;
      }

      return ri;
   }

   protected static void invokeMethod(@Nullable final Method method, final Object instance, final Object... args) {
      if (method != null) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  if (!method.isAccessible()) {
                     method.setAccessible(true);
                  }

                  MethodUtil.invoke(instance, method, args);
                  return null;
               } catch (IllegalAccessException var2) {
                  throw new ServerRtException("server.rt.err", new Object[]{var2});
               } catch (InvocationTargetException var3) {
                  throw new ServerRtException("server.rt.err", new Object[]{var3});
               }
            }
         });
      }
   }

   @Nullable
   protected final Method findAnnotatedMethod(Class clazz, Class<? extends Annotation> annType) {
      boolean once = false;
      Method r = null;
      Method[] var5 = clazz.getDeclaredMethods();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Method method = var5[var7];
         if (method.getAnnotation(annType) != null) {
            if (once) {
               throw new ServerRtException(ServerMessages.ANNOTATION_ONLY_ONCE(annType), new Object[0]);
            }

            if (method.getParameterTypes().length != 0) {
               throw new ServerRtException(ServerMessages.NOT_ZERO_PARAMETERS(method), new Object[0]);
            }

            r = method;
            once = true;
         }
      }

      return r;
   }
}
