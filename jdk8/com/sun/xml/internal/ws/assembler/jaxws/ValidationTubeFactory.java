package com.sun.xml.internal.ws.assembler.jaxws;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import javax.xml.ws.WebServiceException;

public final class ValidationTubeFactory implements TubeFactory {
   public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
      return context.getWrappedContext().createValidationTube(context.getTubelineHead());
   }

   public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
      return context.getWrappedContext().createValidationTube(context.getTubelineHead());
   }
}
