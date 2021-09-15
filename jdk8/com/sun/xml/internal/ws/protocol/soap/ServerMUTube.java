package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.Set;
import javax.xml.namespace.QName;

public class ServerMUTube extends MUTube {
   private ServerTubeAssemblerContext tubeContext;
   private final Set<String> roles;
   private final Set<QName> handlerKnownHeaders;

   public ServerMUTube(ServerTubeAssemblerContext tubeContext, Tube next) {
      super(tubeContext.getEndpoint().getBinding(), next);
      this.tubeContext = tubeContext;
      HandlerConfiguration handlerConfig = this.binding.getHandlerConfig();
      this.roles = handlerConfig.getRoles();
      this.handlerKnownHeaders = this.binding.getKnownHeaders();
   }

   protected ServerMUTube(ServerMUTube that, TubeCloner cloner) {
      super((MUTube)that, (TubeCloner)cloner);
      this.tubeContext = that.tubeContext;
      this.roles = that.roles;
      this.handlerKnownHeaders = that.handlerKnownHeaders;
   }

   public NextAction processRequest(Packet request) {
      Set<QName> misUnderstoodHeaders = this.getMisUnderstoodHeaders(request.getMessage().getHeaders(), this.roles, this.handlerKnownHeaders);
      return misUnderstoodHeaders != null && !misUnderstoodHeaders.isEmpty() ? this.doReturnWith(request.createServerResponse(this.createMUSOAPFaultMessage(misUnderstoodHeaders), this.tubeContext.getWsdlModel(), this.tubeContext.getSEIModel(), this.tubeContext.getEndpoint().getBinding())) : this.doInvoke(super.next, request);
   }

   public ServerMUTube copy(TubeCloner cloner) {
      return new ServerMUTube(this, cloner);
   }
}
