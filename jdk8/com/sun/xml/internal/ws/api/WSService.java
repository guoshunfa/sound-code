package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.ServiceDelegate;

public abstract class WSService extends ServiceDelegate implements ComponentRegistry {
   private final Set<Component> components = new CopyOnWriteArraySet();
   protected static final ThreadLocal<WSService.InitParams> INIT_PARAMS = new ThreadLocal();
   protected static final WSService.InitParams EMPTY_PARAMS = new WSService.InitParams();

   protected WSService() {
   }

   public abstract <T> T getPort(WSEndpointReference var1, Class<T> var2, WebServiceFeature... var3);

   public abstract <T> Dispatch<T> createDispatch(QName var1, WSEndpointReference var2, Class<T> var3, Service.Mode var4, WebServiceFeature... var5);

   public abstract Dispatch<Object> createDispatch(QName var1, WSEndpointReference var2, JAXBContext var3, Service.Mode var4, WebServiceFeature... var5);

   @NotNull
   public abstract Container getContainer();

   @Nullable
   public <S> S getSPI(@NotNull Class<S> spiType) {
      Iterator var2 = this.components.iterator();

      Object s;
      do {
         if (!var2.hasNext()) {
            return this.getContainer().getSPI(spiType);
         }

         Component c = (Component)var2.next();
         s = c.getSPI(spiType);
      } while(s == null);

      return s;
   }

   @NotNull
   public Set<Component> getComponents() {
      return this.components;
   }

   public static WSService create(URL wsdlDocumentLocation, QName serviceName) {
      return new WSServiceDelegate(wsdlDocumentLocation, serviceName, Service.class, new WebServiceFeature[0]);
   }

   public static WSService create(QName serviceName) {
      return create((URL)null, serviceName);
   }

   public static WSService create() {
      return create((URL)null, new QName(WSService.class.getName(), "dummy"));
   }

   public static Service create(URL wsdlDocumentLocation, QName serviceName, WSService.InitParams properties) {
      if (INIT_PARAMS.get() != null) {
         throw new IllegalStateException("someone left non-null InitParams");
      } else {
         INIT_PARAMS.set(properties);

         Service var4;
         try {
            Service svc = Service.create(wsdlDocumentLocation, serviceName);
            if (INIT_PARAMS.get() != null) {
               throw new IllegalStateException("Service " + svc + " didn't recognize InitParams");
            }

            var4 = svc;
         } finally {
            INIT_PARAMS.set((Object)null);
         }

         return var4;
      }
   }

   public static WSService unwrap(final Service svc) {
      return (WSService)AccessController.doPrivileged(new PrivilegedAction<WSService>() {
         public WSService run() {
            try {
               Field f = svc.getClass().getField("delegate");
               f.setAccessible(true);
               Object delegate = f.get(svc);
               if (!(delegate instanceof WSService)) {
                  throw new IllegalArgumentException();
               } else {
                  return (WSService)delegate;
               }
            } catch (NoSuchFieldException var3) {
               AssertionError xx = new AssertionError("Unexpected service API implementation");
               xx.initCause(var3);
               throw xx;
            } catch (IllegalAccessException var4) {
               IllegalAccessError x = new IllegalAccessError(var4.getMessage());
               x.initCause(var4);
               throw x;
            }
         }
      });
   }

   public static final class InitParams {
      private Container container;

      public void setContainer(Container c) {
         this.container = c;
      }

      public Container getContainer() {
         return this.container;
      }
   }
}
