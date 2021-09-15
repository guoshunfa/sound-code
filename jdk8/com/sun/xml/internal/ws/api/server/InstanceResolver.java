package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.server.SingletonResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InstanceResolver<T> {
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server");

   @NotNull
   public abstract T resolve(@NotNull Packet var1);

   public void postInvoke(@NotNull Packet request, @NotNull T servant) {
   }

   public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
      this.start(wsc);
   }

   /** @deprecated */
   public void start(@NotNull WebServiceContext wsc) {
   }

   public void dispose() {
   }

   public static <T> InstanceResolver<T> createSingleton(T singleton) {
      assert singleton != null;

      InstanceResolver ir = createFromInstanceResolverAnnotation(singleton.getClass());
      if (ir == null) {
         ir = new SingletonResolver(singleton);
      }

      return (InstanceResolver)ir;
   }

   /** @deprecated */
   public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> clazz, boolean bool) {
      return createDefault(clazz);
   }

   public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> clazz) {
      InstanceResolver<T> ir = createFromInstanceResolverAnnotation(clazz);
      if (ir == null) {
         ir = new SingletonResolver(createNewInstance(clazz));
      }

      return (InstanceResolver)ir;
   }

   public static <T> InstanceResolver<T> createFromInstanceResolverAnnotation(@NotNull Class<T> clazz) {
      Annotation[] var1 = clazz.getAnnotations();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Annotation a = var1[var3];
         InstanceResolverAnnotation ira = (InstanceResolverAnnotation)a.annotationType().getAnnotation(InstanceResolverAnnotation.class);
         if (ira != null) {
            Class ir = ira.value();

            try {
               return (InstanceResolver)ir.getConstructor(Class.class).newInstance(clazz);
            } catch (InstantiationException var8) {
               throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            } catch (IllegalAccessException var9) {
               throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            } catch (InvocationTargetException var10) {
               throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            } catch (NoSuchMethodException var11) {
               throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            }
         }
      }

      return null;
   }

   protected static <T> T createNewInstance(Class<T> cl) {
      try {
         return cl.newInstance();
      } catch (InstantiationException var2) {
         logger.log(Level.SEVERE, (String)var2.getMessage(), (Throwable)var2);
         throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(cl), new Object[0]);
      } catch (IllegalAccessException var3) {
         logger.log(Level.SEVERE, (String)var3.getMessage(), (Throwable)var3);
         throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(cl), new Object[0]);
      }
   }

   @NotNull
   public Invoker createInvoker() {
      return new Invoker() {
         public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
            InstanceResolver.this.start(wsc, endpoint);
         }

         public void dispose() {
            InstanceResolver.this.dispose();
         }

         public Object invoke(Packet p, Method m, Object... args) throws InvocationTargetException, IllegalAccessException {
            Object t = InstanceResolver.this.resolve(p);

            Object var5;
            try {
               var5 = MethodUtil.invoke(t, m, args);
            } finally {
               InstanceResolver.this.postInvoke(p, t);
            }

            return var5;
         }

         public <U> U invokeProvider(@NotNull Packet p, U arg) {
            Object t = InstanceResolver.this.resolve(p);

            Object var4;
            try {
               var4 = ((Provider)t).invoke(arg);
            } finally {
               InstanceResolver.this.postInvoke(p, t);
            }

            return var4;
         }

         public String toString() {
            return "Default Invoker over " + InstanceResolver.this.toString();
         }
      };
   }
}
