package com.sun.xml.internal.ws.client.sei;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

public final class SEIStub extends Stub implements InvocationHandler {
   Databinding databinding;
   public final SOAPSEIModel seiModel;
   public final SOAPVersion soapVersion;
   private final Map<Method, MethodHandler> methodHandlers = new HashMap();

   /** @deprecated */
   @Deprecated
   public SEIStub(WSServiceDelegate owner, BindingImpl binding, SOAPSEIModel seiModel, Tube master, WSEndpointReference epr) {
      super(owner, master, binding, seiModel.getPort(), seiModel.getPort().getAddress(), epr);
      this.seiModel = seiModel;
      this.soapVersion = binding.getSOAPVersion();
      this.databinding = seiModel.getDatabinding();
      this.initMethodHandlers();
   }

   public SEIStub(WSPortInfo portInfo, BindingImpl binding, SOAPSEIModel seiModel, WSEndpointReference epr) {
      super(portInfo, binding, seiModel.getPort().getAddress(), epr);
      this.seiModel = seiModel;
      this.soapVersion = binding.getSOAPVersion();
      this.databinding = seiModel.getDatabinding();
      this.initMethodHandlers();
   }

   private void initMethodHandlers() {
      Map<WSDLBoundOperation, JavaMethodImpl> syncs = new HashMap();
      Iterator var2 = this.seiModel.getJavaMethods().iterator();

      JavaMethodImpl jm;
      while(var2.hasNext()) {
         jm = (JavaMethodImpl)var2.next();
         if (!jm.getMEP().isAsync) {
            SyncMethodHandler handler = new SyncMethodHandler(this, jm);
            syncs.put(jm.getOperation(), jm);
            this.methodHandlers.put(jm.getMethod(), handler);
         }
      }

      var2 = this.seiModel.getJavaMethods().iterator();

      while(var2.hasNext()) {
         jm = (JavaMethodImpl)var2.next();
         JavaMethodImpl sync = (JavaMethodImpl)syncs.get(jm.getOperation());
         Method m;
         if (jm.getMEP() == MEP.ASYNC_CALLBACK) {
            m = jm.getMethod();
            CallbackMethodHandler handler = new CallbackMethodHandler(this, m, m.getParameterTypes().length - 1);
            this.methodHandlers.put(m, handler);
         }

         if (jm.getMEP() == MEP.ASYNC_POLL) {
            m = jm.getMethod();
            PollingMethodHandler handler = new PollingMethodHandler(this, m);
            this.methodHandlers.put(m, handler);
         }
      }

   }

   @Nullable
   public OperationDispatcher getOperationDispatcher() {
      if (this.operationDispatcher == null && this.wsdlPort != null) {
         this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, this.seiModel);
      }

      return this.operationDispatcher;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      this.validateInputs(proxy, method);
      Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());

      Object var6;
      try {
         MethodHandler handler = (MethodHandler)this.methodHandlers.get(method);
         if (handler == null) {
            try {
               var6 = method.invoke(this, args);
               return var6;
            } catch (IllegalAccessException var12) {
               throw new AssertionError(var12);
            } catch (IllegalArgumentException var13) {
               throw new AssertionError(var13);
            } catch (InvocationTargetException var14) {
               throw var14.getCause();
            }
         }

         var6 = handler.invoke(proxy, args);
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

      return var6;
   }

   private void validateInputs(Object proxy, Method method) {
      if (proxy != null && Proxy.isProxyClass(proxy.getClass())) {
         Class<?> declaringClass = method.getDeclaringClass();
         if (method == null || declaringClass == null || Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Invoking static method is not allowed!");
         }
      } else {
         throw new IllegalStateException("Passed object is not proxy!");
      }
   }

   public final Packet doProcess(Packet request, RequestContext rc, ResponseContextReceiver receiver) {
      return super.process(request, rc, receiver);
   }

   public final void doProcessAsync(AsyncResponseImpl<?> receiver, Packet request, RequestContext rc, Fiber.CompletionCallback callback) {
      super.processAsync(receiver, request, rc, callback);
   }

   @NotNull
   protected final QName getPortName() {
      return this.wsdlPort.getName();
   }

   public void setOutboundHeaders(Object... headers) {
      if (headers == null) {
         throw new IllegalArgumentException();
      } else {
         Header[] hl = new Header[headers.length];

         for(int i = 0; i < hl.length; ++i) {
            if (headers[i] == null) {
               throw new IllegalArgumentException();
            }

            hl[i] = Headers.create(this.seiModel.getBindingContext(), headers[i]);
         }

         super.setOutboundHeaders(hl);
      }
   }
}
