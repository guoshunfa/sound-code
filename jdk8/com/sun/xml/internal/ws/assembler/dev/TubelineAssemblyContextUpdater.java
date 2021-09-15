package com.sun.xml.internal.ws.assembler.dev;

import javax.xml.ws.WebServiceException;

public interface TubelineAssemblyContextUpdater {
   void prepareContext(ClientTubelineAssemblyContext var1) throws WebServiceException;

   void prepareContext(ServerTubelineAssemblyContext var1) throws WebServiceException;
}
