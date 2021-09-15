package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

final class SyncMethodHandler extends MethodHandler {
   final boolean isVoid;
   final boolean isOneway;
   final JavaMethodImpl javaMethod;

   SyncMethodHandler(SEIStub owner, JavaMethodImpl jm) {
      super(owner, jm.getMethod());
      this.javaMethod = jm;
      this.isVoid = Void.TYPE.equals(jm.getMethod().getReturnType());
      this.isOneway = jm.getMEP().isOneWay();
   }

   Object invoke(Object proxy, Object[] args) throws Throwable {
      return this.invoke(proxy, args, this.owner.requestContext, this.owner);
   }

   Object invoke(Object proxy, Object[] args, RequestContext rc, ResponseContextReceiver receiver) throws Throwable {
      JavaCallInfo call = this.owner.databinding.createJavaCallInfo(this.method, args);
      Packet req = (Packet)this.owner.databinding.serializeRequest(call);
      Packet reply = this.owner.doProcess(req, rc, receiver);
      Message msg = reply.getMessage();
      if (msg == null) {
         if (this.isOneway && this.isVoid) {
            return null;
         } else {
            throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
         }
      } else {
         Object var9;
         try {
            call = this.owner.databinding.deserializeResponse(reply, call);
            if (call.getException() != null) {
               throw call.getException();
            }

            var9 = call.getReturnValue();
         } catch (JAXBException var14) {
            throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[]{var14});
         } catch (XMLStreamException var15) {
            throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[]{var15});
         } finally {
            if (reply.transportBackChannel != null) {
               reply.transportBackChannel.close();
            }

         }

         return var9;
      }
   }

   ValueGetterFactory getValueGetterFactory() {
      return ValueGetterFactory.SYNC;
   }
}
