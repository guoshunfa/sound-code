package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.namespace.QName;

final class TubelineAssemblyController {
   private final MetroConfigName metroConfigName;

   TubelineAssemblyController(MetroConfigName metroConfigName) {
      this.metroConfigName = metroConfigName;
   }

   Collection<TubeCreator> getTubeCreators(ClientTubelineAssemblyContext context) {
      URI endpointUri;
      if (context.getPortInfo() != null) {
         endpointUri = this.createEndpointComponentUri(context.getPortInfo().getServiceName(), context.getPortInfo().getPortName());
      } else {
         endpointUri = null;
      }

      MetroConfigLoader configLoader = new MetroConfigLoader(context.getContainer(), this.metroConfigName);
      return this.initializeTubeCreators(configLoader.getClientSideTubeFactories(endpointUri));
   }

   Collection<TubeCreator> getTubeCreators(DefaultServerTubelineAssemblyContext context) {
      URI endpointUri;
      if (context.getEndpoint() != null) {
         endpointUri = this.createEndpointComponentUri(context.getEndpoint().getServiceName(), context.getEndpoint().getPortName());
      } else {
         endpointUri = null;
      }

      MetroConfigLoader configLoader = new MetroConfigLoader(context.getEndpoint().getContainer(), this.metroConfigName);
      return this.initializeTubeCreators(configLoader.getEndpointSideTubeFactories(endpointUri));
   }

   private Collection<TubeCreator> initializeTubeCreators(TubeFactoryList tfl) {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      LinkedList<TubeCreator> tubeCreators = new LinkedList();
      Iterator var4 = tfl.getTubeFactoryConfigs().iterator();

      while(var4.hasNext()) {
         TubeFactoryConfig tubeFactoryConfig = (TubeFactoryConfig)var4.next();
         tubeCreators.addFirst(new TubeCreator(tubeFactoryConfig, contextClassLoader));
      }

      return tubeCreators;
   }

   private URI createEndpointComponentUri(@NotNull QName serviceName, @NotNull QName portName) {
      StringBuilder sb = (new StringBuilder(serviceName.getNamespaceURI())).append("#wsdl11.port(").append(serviceName.getLocalPart()).append('/').append(portName.getLocalPart()).append(')');

      try {
         return new URI(sb.toString());
      } catch (URISyntaxException var5) {
         Logger.getLogger(TubelineAssemblyController.class).warning(TubelineassemblyMessages.MASM_0020_ERROR_CREATING_URI_FROM_GENERATED_STRING(sb.toString()), (Throwable)var5);
         return null;
      }
   }
}
