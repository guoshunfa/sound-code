package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;

public abstract class Invoker extends com.sun.xml.internal.ws.server.sei.Invoker {
   private static final Method invokeMethod;
   private static final Method asyncInvokeMethod;

   public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
      this.start(wsc);
   }

   /** @deprecated */
   public void start(@NotNull WebServiceContext wsc) {
      throw new IllegalStateException("deprecated version called");
   }

   public void dispose() {
   }

   public <T> T invokeProvider(@NotNull Packet p, T arg) throws IllegalAccessException, InvocationTargetException {
      return this.invoke(p, invokeMethod, new Object[]{arg});
   }

   public <T> void invokeAsyncProvider(@NotNull Packet p, T arg, AsyncProviderCallback cbak, WebServiceContext ctxt) throws IllegalAccessException, InvocationTargetException {
      this.invoke(p, asyncInvokeMethod, new Object[]{arg, cbak, ctxt});
   }

   static {
      try {
         invokeMethod = Provider.class.getMethod("invoke", Object.class);
      } catch (NoSuchMethodException var2) {
         throw new AssertionError(var2);
      }

      try {
         asyncInvokeMethod = AsyncProvider.class.getMethod("invoke", Object.class, AsyncProviderCallback.class, WebServiceContext.class);
      } catch (NoSuchMethodException var1) {
         throw new AssertionError(var1);
      }
   }
}
