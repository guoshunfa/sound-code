package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.sei.StubAsyncHandler;
import com.sun.xml.internal.ws.client.sei.StubHandler;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.server.sei.TieHandler;
import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import com.sun.xml.internal.ws.wsdl.writer.WSDLGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceFeature;

public final class DatabindingImpl implements Databinding {
   AbstractSEIModelImpl seiModel;
   Map<Method, StubHandler> stubHandlers;
   Map<JavaMethodImpl, TieHandler> wsdlOpMap = new HashMap();
   Map<Method, TieHandler> tieHandlers = new HashMap();
   OperationDispatcher operationDispatcher;
   OperationDispatcher operationDispatcherNoWsdl;
   boolean clientConfig = false;
   Codec codec;
   MessageContextFactory packetFactory = null;

   public DatabindingImpl(DatabindingProviderImpl p, DatabindingConfig config) {
      RuntimeModeler modeler = new RuntimeModeler(config);
      modeler.setClassLoader(config.getClassLoader());
      this.seiModel = modeler.buildRuntimeModel();
      WSDLPort wsdlport = config.getWsdlPort();
      this.packetFactory = new MessageContextFactory(this.seiModel.getWSBinding().getFeatures());
      this.clientConfig = this.isClientConfig(config);
      if (this.clientConfig) {
         this.initStubHandlers();
      }

      this.seiModel.setDatabinding(this);
      if (wsdlport != null) {
         this.freeze(wsdlport);
      }

      if (this.operationDispatcher == null) {
         this.operationDispatcherNoWsdl = new OperationDispatcher((WSDLPort)null, this.seiModel.getWSBinding(), this.seiModel);
      }

      Iterator var5 = this.seiModel.getJavaMethods().iterator();

      while(var5.hasNext()) {
         JavaMethodImpl jm = (JavaMethodImpl)var5.next();
         if (!jm.isAsync()) {
            TieHandler th = new TieHandler(jm, this.seiModel.getWSBinding(), this.packetFactory);
            this.wsdlOpMap.put(jm, th);
            this.tieHandlers.put(th.getMethod(), th);
         }
      }

   }

   private boolean isClientConfig(DatabindingConfig config) {
      if (config.getContractClass() == null) {
         return false;
      } else if (!config.getContractClass().isInterface()) {
         return false;
      } else {
         return config.getEndpointClass() == null || config.getEndpointClass().isInterface();
      }
   }

   public void freeze(WSDLPort port) {
      if (!this.clientConfig) {
         synchronized(this) {
            if (this.operationDispatcher == null) {
               this.operationDispatcher = port == null ? null : new OperationDispatcher(port, this.seiModel.getWSBinding(), this.seiModel);
            }

         }
      }
   }

   public SEIModel getModel() {
      return this.seiModel;
   }

   private void initStubHandlers() {
      this.stubHandlers = new HashMap();
      Map<ActionBasedOperationSignature, JavaMethodImpl> syncs = new HashMap();
      Iterator var2 = this.seiModel.getJavaMethods().iterator();

      JavaMethodImpl jm;
      while(var2.hasNext()) {
         jm = (JavaMethodImpl)var2.next();
         if (!jm.getMEP().isAsync) {
            StubHandler handler = new StubHandler(jm, this.packetFactory);
            syncs.put(jm.getOperationSignature(), jm);
            this.stubHandlers.put(jm.getMethod(), handler);
         }
      }

      var2 = this.seiModel.getJavaMethods().iterator();

      while(true) {
         JavaMethodImpl sync;
         do {
            if (!var2.hasNext()) {
               return;
            }

            jm = (JavaMethodImpl)var2.next();
            sync = (JavaMethodImpl)syncs.get(jm.getOperationSignature());
         } while(jm.getMEP() != MEP.ASYNC_CALLBACK && jm.getMEP() != MEP.ASYNC_POLL);

         Method m = jm.getMethod();
         StubAsyncHandler handler = new StubAsyncHandler(jm, sync, this.packetFactory);
         this.stubHandlers.put(m, handler);
      }
   }

   JavaMethodImpl resolveJavaMethod(Packet req) throws DispatchException {
      WSDLOperationMapping m = req.getWSDLOperationMapping();
      if (m == null) {
         synchronized(this) {
            m = this.operationDispatcher != null ? this.operationDispatcher.getWSDLOperationMapping(req) : this.operationDispatcherNoWsdl.getWSDLOperationMapping(req);
         }
      }

      return (JavaMethodImpl)m.getJavaMethod();
   }

   public JavaCallInfo deserializeRequest(Packet req) {
      com.sun.xml.internal.ws.api.databinding.JavaCallInfo call = new com.sun.xml.internal.ws.api.databinding.JavaCallInfo();

      try {
         JavaMethodImpl wsdlOp = this.resolveJavaMethod(req);
         TieHandler tie = (TieHandler)this.wsdlOpMap.get(wsdlOp);
         call.setMethod(tie.getMethod());
         Object[] args = tie.readRequest(req.getMessage());
         call.setParameters(args);
      } catch (DispatchException var6) {
         call.setException(var6);
      }

      return call;
   }

   public JavaCallInfo deserializeResponse(Packet res, JavaCallInfo call) {
      StubHandler stubHandler = (StubHandler)this.stubHandlers.get(call.getMethod());

      try {
         return stubHandler.readResponse(res, call);
      } catch (Throwable var5) {
         call.setException(var5);
         return call;
      }
   }

   public WebServiceFeature[] getFeatures() {
      return null;
   }

   public Packet serializeRequest(JavaCallInfo call) {
      StubHandler stubHandler = (StubHandler)this.stubHandlers.get(call.getMethod());
      Packet p = stubHandler.createRequestPacket(call);
      p.setState(Packet.State.ClientRequest);
      return p;
   }

   public Packet serializeResponse(JavaCallInfo call) {
      Method method = call.getMethod();
      Message message = null;
      if (method != null) {
         TieHandler th = (TieHandler)this.tieHandlers.get(method);
         if (th != null) {
            return th.serializeResponse(call);
         }
      }

      if (call.getException() instanceof DispatchException) {
         message = ((DispatchException)call.getException()).fault;
      }

      Packet p = (Packet)this.packetFactory.createContext(message);
      p.setState(Packet.State.ServerResponse);
      return p;
   }

   public ClientCallBridge getClientBridge(Method method) {
      return (ClientCallBridge)this.stubHandlers.get(method);
   }

   public void generateWSDL(WSDLGenInfo info) {
      WSDLGenerator wsdlGen = new WSDLGenerator(this.seiModel, info.getWsdlResolver(), this.seiModel.getWSBinding(), info.getContainer(), this.seiModel.getEndpointClass(), info.isInlineSchemas(), info.isSecureXmlProcessingDisabled(), info.getExtensions());
      wsdlGen.doGeneration();
   }

   public EndpointCallBridge getEndpointBridge(Packet req) throws DispatchException {
      JavaMethodImpl wsdlOp = this.resolveJavaMethod(req);
      return (EndpointCallBridge)this.wsdlOpMap.get(wsdlOp);
   }

   Codec getCodec() {
      if (this.codec == null) {
         this.codec = ((BindingImpl)this.seiModel.getWSBinding()).createCodec();
      }

      return this.codec;
   }

   public ContentType encode(Packet packet, OutputStream out) throws IOException {
      return this.getCodec().encode(packet, out);
   }

   public void decode(InputStream in, String ct, Packet p) throws IOException {
      this.getCodec().decode(in, ct, p);
   }

   public JavaCallInfo createJavaCallInfo(Method method, Object[] args) {
      return new com.sun.xml.internal.ws.api.databinding.JavaCallInfo(method, args);
   }

   public JavaCallInfo deserializeResponse(MessageContext message, JavaCallInfo call) {
      return this.deserializeResponse((Packet)message, call);
   }

   public JavaCallInfo deserializeRequest(MessageContext message) {
      return this.deserializeRequest((Packet)message);
   }

   public MessageContextFactory getMessageContextFactory() {
      return this.packetFactory;
   }
}
