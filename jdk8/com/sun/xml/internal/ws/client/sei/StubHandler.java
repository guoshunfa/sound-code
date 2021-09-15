package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public class StubHandler implements ClientCallBridge {
   private final BodyBuilder bodyBuilder;
   private final MessageFiller[] inFillers;
   protected final String soapAction;
   protected final boolean isOneWay;
   protected final JavaMethodImpl javaMethod;
   protected final Map<QName, CheckedExceptionImpl> checkedExceptions;
   protected SOAPVersion soapVersion;
   protected ResponseBuilder responseBuilder;
   protected MessageContextFactory packetFactory;

   public StubHandler(JavaMethodImpl method, MessageContextFactory mcf) {
      this.soapVersion = SOAPVersion.SOAP_11;
      this.checkedExceptions = new HashMap();
      Iterator var3 = method.getCheckedExceptions().iterator();

      while(var3.hasNext()) {
         CheckedExceptionImpl ce = (CheckedExceptionImpl)var3.next();
         this.checkedExceptions.put(ce.getBond().getTypeInfo().tagName, ce);
      }

      String soapActionFromBinding = method.getBinding().getSOAPAction();
      if (method.getInputAction() != null && soapActionFromBinding != null && !soapActionFromBinding.equals("")) {
         this.soapAction = method.getInputAction();
      } else {
         this.soapAction = soapActionFromBinding;
      }

      this.javaMethod = method;
      this.packetFactory = mcf;
      this.soapVersion = this.javaMethod.getBinding().getSOAPVersion();
      List<ParameterImpl> rp = method.getRequestParameters();
      BodyBuilder bodyBuilder = null;
      List<MessageFiller> fillers = new ArrayList();
      Iterator var7 = rp.iterator();

      while(var7.hasNext()) {
         ParameterImpl param = (ParameterImpl)var7.next();
         ValueGetter getter = this.getValueGetterFactory().get(param);
         switch(param.getInBinding().kind) {
         case BODY:
            if (param.isWrapperStyle()) {
               if (param.getParent().getBinding().isRpcLit()) {
                  bodyBuilder = new BodyBuilder.RpcLit((WrapperParameter)param, this.soapVersion, this.getValueGetterFactory());
               } else {
                  bodyBuilder = new BodyBuilder.DocLit((WrapperParameter)param, this.soapVersion, this.getValueGetterFactory());
               }
            } else {
               bodyBuilder = new BodyBuilder.Bare(param, this.soapVersion, getter);
            }
            break;
         case HEADER:
            fillers.add(new MessageFiller.Header(param.getIndex(), param.getXMLBridge(), getter));
            break;
         case ATTACHMENT:
            fillers.add(MessageFiller.AttachmentFiller.createAttachmentFiller(param, getter));
         case UNBOUND:
            break;
         default:
            throw new AssertionError();
         }
      }

      if (bodyBuilder == null) {
         switch(this.soapVersion) {
         case SOAP_11:
            bodyBuilder = BodyBuilder.EMPTY_SOAP11;
            break;
         case SOAP_12:
            bodyBuilder = BodyBuilder.EMPTY_SOAP12;
            break;
         default:
            throw new AssertionError();
         }
      }

      this.bodyBuilder = (BodyBuilder)bodyBuilder;
      this.inFillers = (MessageFiller[])fillers.toArray(new MessageFiller[fillers.size()]);
      this.isOneWay = method.getMEP().isOneWay();
      this.responseBuilder = this.buildResponseBuilder(method, ValueSetterFactory.SYNC);
   }

   ResponseBuilder buildResponseBuilder(JavaMethodImpl method, ValueSetterFactory setterFactory) {
      List<ParameterImpl> rp = method.getResponseParameters();
      List<ResponseBuilder> builders = new ArrayList();
      Iterator var5 = rp.iterator();

      while(var5.hasNext()) {
         ParameterImpl param = (ParameterImpl)var5.next();
         ValueSetter setter;
         switch(param.getOutBinding().kind) {
         case BODY:
            if (param.isWrapperStyle()) {
               if (param.getParent().getBinding().isRpcLit()) {
                  builders.add(new ResponseBuilder.RpcLit((WrapperParameter)param, setterFactory));
               } else {
                  builders.add(new ResponseBuilder.DocLit((WrapperParameter)param, setterFactory));
               }
            } else {
               setter = setterFactory.get(param);
               builders.add(new ResponseBuilder.Body(param.getXMLBridge(), setter));
            }
            break;
         case HEADER:
            setter = setterFactory.get(param);
            builders.add(new ResponseBuilder.Header(this.soapVersion, param, setter));
            break;
         case ATTACHMENT:
            setter = setterFactory.get(param);
            builders.add(ResponseBuilder.AttachmentBuilder.createAttachmentBuilder(param, setter));
            break;
         case UNBOUND:
            setter = setterFactory.get(param);
            builders.add(new ResponseBuilder.NullSetter(setter, ResponseBuilder.getVMUninitializedValue(param.getTypeInfo().type)));
            break;
         default:
            throw new AssertionError();
         }
      }

      Object rb;
      switch(builders.size()) {
      case 0:
         rb = ResponseBuilder.NONE;
         break;
      case 1:
         rb = (ResponseBuilder)builders.get(0);
         break;
      default:
         rb = new ResponseBuilder.Composite(builders);
      }

      return (ResponseBuilder)rb;
   }

   public Packet createRequestPacket(JavaCallInfo args) {
      Message msg = this.bodyBuilder.createMessage(args.getParameters());
      MessageFiller[] var3 = this.inFillers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         MessageFiller filler = var3[var5];
         filler.fillIn(args.getParameters(), msg);
      }

      Packet req = (Packet)this.packetFactory.createContext(msg);
      req.setState(Packet.State.ClientRequest);
      req.soapAction = this.soapAction;
      req.expectReply = !this.isOneWay;
      req.getMessage().assertOneWay(this.isOneWay);
      req.setWSDLOperation(this.getOperationName());
      return req;
   }

   ValueGetterFactory getValueGetterFactory() {
      return ValueGetterFactory.SYNC;
   }

   public JavaCallInfo readResponse(Packet p, JavaCallInfo call) throws Throwable {
      Message msg = p.getMessage();
      if (msg.isFault()) {
         SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
         Throwable t = faultBuilder.createException(this.checkedExceptions);
         call.setException(t);
         throw t;
      } else {
         this.initArgs(call.getParameters());
         Object ret = this.responseBuilder.readResponse(msg, call.getParameters());
         call.setReturnValue(ret);
         return call;
      }
   }

   public QName getOperationName() {
      return this.javaMethod.getOperationQName();
   }

   public String getSoapAction() {
      return this.soapAction;
   }

   public boolean isOneWay() {
      return this.isOneWay;
   }

   protected void initArgs(Object[] args) throws Exception {
   }

   public Method getMethod() {
      return this.javaMethod.getMethod();
   }

   public JavaMethod getOperationModel() {
      return this.javaMethod;
   }
}
