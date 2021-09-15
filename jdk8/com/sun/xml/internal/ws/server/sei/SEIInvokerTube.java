package com.sun.xml.internal.ws.server.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;

public class SEIInvokerTube extends com.sun.xml.internal.ws.server.InvokerTube {
   private final WSBinding binding;
   private final AbstractSEIModelImpl model;

   public SEIInvokerTube(AbstractSEIModelImpl model, com.sun.xml.internal.ws.api.server.Invoker invoker, WSBinding binding) {
      super(invoker);
      this.binding = binding;
      this.model = model;
   }

   @NotNull
   public NextAction processRequest(@NotNull Packet req) {
      JavaCallInfo call = this.model.getDatabinding().deserializeRequest(req);
      if (call.getException() == null) {
         try {
            if (req.getMessage().isOneWay(this.model.getPort()) && req.transportBackChannel != null) {
               req.transportBackChannel.close();
            }

            Object ret = this.getInvoker(req).invoke(req, call.getMethod(), call.getParameters());
            call.setReturnValue(ret);
         } catch (InvocationTargetException var4) {
            call.setException(var4);
         } catch (Exception var5) {
            call.setException(var5);
         }
      } else if (call.getException() instanceof DispatchException) {
         DispatchException e = (DispatchException)call.getException();
         return this.doReturnWith(req.createServerResponse(e.fault, (WSDLPort)this.model.getPort(), (SEIModel)null, (WSBinding)this.binding));
      }

      Packet res = (Packet)this.model.getDatabinding().serializeResponse(call);
      res = req.relateServerResponse(res, req.endpoint.getPort(), this.model, req.endpoint.getBinding());

      assert res != null;

      return this.doReturnWith(res);
   }

   @NotNull
   public NextAction processResponse(@NotNull Packet response) {
      return this.doReturnWith(response);
   }

   @NotNull
   public NextAction processException(@NotNull Throwable t) {
      return this.doThrow(t);
   }
}
