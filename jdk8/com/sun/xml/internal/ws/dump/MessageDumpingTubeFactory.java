package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import javax.xml.ws.WebServiceException;

public final class MessageDumpingTubeFactory implements TubeFactory {
   public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
      MessageDumpingFeature messageDumpingFeature = (MessageDumpingFeature)context.getBinding().getFeature(MessageDumpingFeature.class);
      return (Tube)(messageDumpingFeature != null ? new MessageDumpingTube(context.getTubelineHead(), messageDumpingFeature) : context.getTubelineHead());
   }

   public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
      MessageDumpingFeature messageDumpingFeature = (MessageDumpingFeature)context.getEndpoint().getBinding().getFeature(MessageDumpingFeature.class);
      return (Tube)(messageDumpingFeature != null ? new MessageDumpingTube(context.getTubelineHead(), messageDumpingFeature) : context.getTubelineHead());
   }
}
