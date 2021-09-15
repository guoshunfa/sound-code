package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.Message;
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

abstract class SEIMethodHandler extends MethodHandler {
   private BodyBuilder bodyBuilder;
   private MessageFiller[] inFillers;
   protected String soapAction;
   protected boolean isOneWay;
   protected JavaMethodImpl javaMethod;
   protected Map<QName, CheckedExceptionImpl> checkedExceptions;

   SEIMethodHandler(SEIStub owner) {
      super(owner, (Method)null);
   }

   SEIMethodHandler(SEIStub owner, JavaMethodImpl method) {
      super(owner, (Method)null);
      this.checkedExceptions = new HashMap();
      Iterator var3 = method.getCheckedExceptions().iterator();

      while(var3.hasNext()) {
         CheckedExceptionImpl ce = (CheckedExceptionImpl)var3.next();
         this.checkedExceptions.put(ce.getBond().getTypeInfo().tagName, ce);
      }

      if (method.getInputAction() != null && !method.getBinding().getSOAPAction().equals("")) {
         this.soapAction = method.getInputAction();
      } else {
         this.soapAction = method.getBinding().getSOAPAction();
      }

      this.javaMethod = method;
      List<ParameterImpl> rp = method.getRequestParameters();
      BodyBuilder tmpBodyBuilder = null;
      List<MessageFiller> fillers = new ArrayList();
      Iterator var6 = rp.iterator();

      while(var6.hasNext()) {
         ParameterImpl param = (ParameterImpl)var6.next();
         ValueGetter getter = this.getValueGetterFactory().get(param);
         switch(param.getInBinding().kind) {
         case BODY:
            if (param.isWrapperStyle()) {
               if (param.getParent().getBinding().isRpcLit()) {
                  tmpBodyBuilder = new BodyBuilder.RpcLit((WrapperParameter)param, owner.soapVersion, this.getValueGetterFactory());
               } else {
                  tmpBodyBuilder = new BodyBuilder.DocLit((WrapperParameter)param, owner.soapVersion, this.getValueGetterFactory());
               }
            } else {
               tmpBodyBuilder = new BodyBuilder.Bare(param, owner.soapVersion, getter);
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
         switch(owner.soapVersion) {
         case SOAP_11:
            tmpBodyBuilder = BodyBuilder.EMPTY_SOAP11;
            break;
         case SOAP_12:
            tmpBodyBuilder = BodyBuilder.EMPTY_SOAP12;
            break;
         default:
            throw new AssertionError();
         }
      }

      this.bodyBuilder = (BodyBuilder)tmpBodyBuilder;
      this.inFillers = (MessageFiller[])fillers.toArray(new MessageFiller[fillers.size()]);
      this.isOneWay = method.getMEP().isOneWay();
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
            builders.add(new ResponseBuilder.Header(this.owner.soapVersion, param, setter));
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

   Message createRequestMessage(Object[] args) {
      Message msg = this.bodyBuilder.createMessage(args);
      MessageFiller[] var3 = this.inFillers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         MessageFiller filler = var3[var5];
         filler.fillIn(args, msg);
      }

      return msg;
   }

   abstract ValueGetterFactory getValueGetterFactory();
}
