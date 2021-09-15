package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Tube;
import javax.xml.ws.WebServiceException;

public interface TubeFactory {
   Tube createTube(ClientTubelineAssemblyContext var1) throws WebServiceException;

   Tube createTube(ServerTubelineAssemblyContext var1) throws WebServiceException;
}
