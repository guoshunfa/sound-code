package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.policy.PolicyMap;

class DefaultServerTubelineAssemblyContext extends TubelineAssemblyContextImpl implements ServerTubelineAssemblyContext {
   @NotNull
   private final ServerTubeAssemblerContext wrappedContext;
   private final PolicyMap policyMap;

   public DefaultServerTubelineAssemblyContext(@NotNull ServerTubeAssemblerContext context) {
      this.wrappedContext = context;
      this.policyMap = context.getEndpoint().getPolicyMap();
   }

   public PolicyMap getPolicyMap() {
      return this.policyMap;
   }

   public boolean isPolicyAvailable() {
      return this.policyMap != null && !this.policyMap.isEmpty();
   }

   @Nullable
   public SEIModel getSEIModel() {
      return this.wrappedContext.getSEIModel();
   }

   @Nullable
   public WSDLPort getWsdlPort() {
      return this.wrappedContext.getWsdlModel();
   }

   @NotNull
   public WSEndpoint getEndpoint() {
      return this.wrappedContext.getEndpoint();
   }

   @NotNull
   public Tube getTerminalTube() {
      return this.wrappedContext.getTerminalTube();
   }

   public boolean isSynchronous() {
      return this.wrappedContext.isSynchronous();
   }

   @NotNull
   public Codec getCodec() {
      return this.wrappedContext.getCodec();
   }

   public void setCodec(@NotNull Codec codec) {
      this.wrappedContext.setCodec(codec);
   }

   public ServerTubeAssemblerContext getWrappedContext() {
      return this.wrappedContext;
   }
}
