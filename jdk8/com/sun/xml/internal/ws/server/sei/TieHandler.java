package com.sun.xml.internal.ws.server.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;

public final class TieHandler implements EndpointCallBridge {
   private final SOAPVersion soapVersion;
   private final Method method;
   private final int noOfArgs;
   private final JavaMethodImpl javaMethodModel;
   private final Boolean isOneWay;
   private final EndpointArgumentsBuilder argumentsBuilder;
   private final EndpointResponseMessageBuilder bodyBuilder;
   private final MessageFiller[] outFillers;
   protected MessageContextFactory packetFactory;
   private static final Logger LOGGER = Logger.getLogger(TieHandler.class.getName());

   public TieHandler(JavaMethodImpl method, WSBinding binding, MessageContextFactory mcf) {
      this.soapVersion = binding.getSOAPVersion();
      this.method = method.getMethod();
      this.javaMethodModel = method;
      this.argumentsBuilder = this.createArgumentsBuilder();
      List<MessageFiller> fillers = new ArrayList();
      this.bodyBuilder = this.createResponseMessageBuilder(fillers);
      this.outFillers = (MessageFiller[])fillers.toArray(new MessageFiller[fillers.size()]);
      this.isOneWay = method.getMEP().isOneWay();
      this.noOfArgs = this.method.getParameterTypes().length;
      this.packetFactory = mcf;
   }

   private EndpointArgumentsBuilder createArgumentsBuilder() {
      List<ParameterImpl> rp = this.javaMethodModel.getRequestParameters();
      List<EndpointArgumentsBuilder> builders = new ArrayList();
      Iterator var4 = rp.iterator();

      while(var4.hasNext()) {
         ParameterImpl param = (ParameterImpl)var4.next();
         EndpointValueSetter setter = EndpointValueSetter.get(param);
         switch(param.getInBinding().kind) {
         case BODY:
            if (param.isWrapperStyle()) {
               if (param.getParent().getBinding().isRpcLit()) {
                  builders.add(new EndpointArgumentsBuilder.RpcLit((WrapperParameter)param));
               } else {
                  builders.add(new EndpointArgumentsBuilder.DocLit((WrapperParameter)param, WebParam.Mode.OUT));
               }
            } else {
               builders.add(new EndpointArgumentsBuilder.Body(param.getXMLBridge(), setter));
            }
            break;
         case HEADER:
            builders.add(new EndpointArgumentsBuilder.Header(this.soapVersion, param, setter));
            break;
         case ATTACHMENT:
            builders.add(EndpointArgumentsBuilder.AttachmentBuilder.createAttachmentBuilder(param, setter));
            break;
         case UNBOUND:
            builders.add(new EndpointArgumentsBuilder.NullSetter(setter, EndpointArgumentsBuilder.getVMUninitializedValue(param.getTypeInfo().type)));
            break;
         default:
            throw new AssertionError();
         }
      }

      List<ParameterImpl> resp = this.javaMethodModel.getResponseParameters();
      Iterator var13 = resp.iterator();

      while(true) {
         while(var13.hasNext()) {
            ParameterImpl param = (ParameterImpl)var13.next();
            if (param.isWrapperStyle()) {
               WrapperParameter wp = (WrapperParameter)param;
               List<ParameterImpl> children = wp.getWrapperChildren();
               Iterator var9 = children.iterator();

               while(var9.hasNext()) {
                  ParameterImpl p = (ParameterImpl)var9.next();
                  if (p.isOUT() && p.getIndex() != -1) {
                     EndpointValueSetter setter = EndpointValueSetter.get(p);
                     builders.add(new EndpointArgumentsBuilder.NullSetter(setter, (Object)null));
                  }
               }
            } else if (param.isOUT() && param.getIndex() != -1) {
               EndpointValueSetter setter = EndpointValueSetter.get(param);
               builders.add(new EndpointArgumentsBuilder.NullSetter(setter, (Object)null));
            }
         }

         Object argsBuilder;
         switch(builders.size()) {
         case 0:
            argsBuilder = EndpointArgumentsBuilder.NONE;
            break;
         case 1:
            argsBuilder = (EndpointArgumentsBuilder)builders.get(0);
            break;
         default:
            argsBuilder = new EndpointArgumentsBuilder.Composite(builders);
         }

         return (EndpointArgumentsBuilder)argsBuilder;
      }
   }

   private EndpointResponseMessageBuilder createResponseMessageBuilder(List<MessageFiller> fillers) {
      EndpointResponseMessageBuilder tmpBodyBuilder = null;
      List<ParameterImpl> rp = this.javaMethodModel.getResponseParameters();
      Iterator var4 = rp.iterator();

      while(var4.hasNext()) {
         ParameterImpl param = (ParameterImpl)var4.next();
         ValueGetter getter = ValueGetter.get(param);
         switch(param.getOutBinding().kind) {
         case BODY:
            if (param.isWrapperStyle()) {
               if (param.getParent().getBinding().isRpcLit()) {
                  tmpBodyBuilder = new EndpointResponseMessageBuilder.RpcLit((WrapperParameter)param, this.soapVersion);
               } else {
                  tmpBodyBuilder = new EndpointResponseMessageBuilder.DocLit((WrapperParameter)param, this.soapVersion);
               }
            } else {
               tmpBodyBuilder = new EndpointResponseMessageBuilder.Bare(param, this.soapVersion);
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

      if (tmpBodyBuilder == null) {
         switch(this.soapVersion) {
         case SOAP_11:
            tmpBodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP11;
            break;
         case SOAP_12:
            tmpBodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP12;
            break;
         default:
            throw new AssertionError();
         }
      }

      return (EndpointResponseMessageBuilder)tmpBodyBuilder;
   }

   public Object[] readRequest(Message reqMsg) {
      Object[] args = new Object[this.noOfArgs];

      try {
         this.argumentsBuilder.readRequest(reqMsg, args);
         return args;
      } catch (JAXBException var4) {
         throw new WebServiceException(var4);
      } catch (XMLStreamException var5) {
         throw new WebServiceException(var5);
      }
   }

   public Message createResponse(JavaCallInfo call) {
      Message responseMessage;
      if (call.getException() == null) {
         responseMessage = this.isOneWay ? null : this.createResponseMessage(call.getParameters(), call.getReturnValue());
      } else {
         Throwable e = call.getException();
         Throwable serviceException = this.getServiceException(e);
         if (!(e instanceof InvocationTargetException) && serviceException == null) {
            if (e instanceof DispatchException) {
               responseMessage = ((DispatchException)e).fault;
            } else {
               LOGGER.log(Level.SEVERE, e.getMessage(), e);
               responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, (CheckedExceptionImpl)null, (Throwable)e);
            }
         } else if (serviceException != null) {
            LOGGER.log(Level.FINE, serviceException.getMessage(), serviceException);
            responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.javaMethodModel.getCheckedException(serviceException.getClass()), serviceException);
         } else {
            Throwable cause = e.getCause();
            if (cause instanceof ProtocolException) {
               LOGGER.log(Level.FINE, cause.getMessage(), cause);
            } else {
               LOGGER.log(Level.SEVERE, cause.getMessage(), cause);
            }

            responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, (CheckedExceptionImpl)null, (Throwable)cause);
         }
      }

      return responseMessage;
   }

   Throwable getServiceException(Throwable throwable) {
      if (this.javaMethodModel.getCheckedException(throwable.getClass()) != null) {
         return throwable;
      } else {
         if (throwable.getCause() != null) {
            Throwable cause = throwable.getCause();
            if (this.javaMethodModel.getCheckedException(cause.getClass()) != null) {
               return cause;
            }
         }

         return null;
      }
   }

   private Message createResponseMessage(Object[] args, Object returnValue) {
      Message msg = this.bodyBuilder.createMessage(args, returnValue);
      MessageFiller[] var4 = this.outFillers;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         MessageFiller filler = var4[var6];
         filler.fillIn(args, returnValue, msg);
      }

      return msg;
   }

   public Method getMethod() {
      return this.method;
   }

   public JavaCallInfo deserializeRequest(Packet req) {
      com.sun.xml.internal.ws.api.databinding.JavaCallInfo call = new com.sun.xml.internal.ws.api.databinding.JavaCallInfo();
      call.setMethod(this.getMethod());
      Object[] args = this.readRequest(req.getMessage());
      call.setParameters(args);
      return call;
   }

   public Packet serializeResponse(JavaCallInfo call) {
      Message msg = this.createResponse(call);
      Packet p = msg == null ? (Packet)this.packetFactory.createContext() : (Packet)this.packetFactory.createContext(msg);
      p.setState(Packet.State.ServerResponse);
      return p;
   }

   public JavaMethod getOperationModel() {
      return this.javaMethodModel;
   }
}
