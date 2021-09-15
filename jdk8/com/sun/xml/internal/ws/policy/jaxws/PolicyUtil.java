package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.addressing.policy.AddressingFeatureConfigurator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.encoding.policy.FastInfosetFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.MtomFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.SelectOptimalEncodingFeatureConfigurator;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class PolicyUtil {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtil.class);
   private static final Collection<PolicyFeatureConfigurator> CONFIGURATORS = new LinkedList();

   public static <T> void addServiceProviders(Collection<T> providers, Class<T> service) {
      Iterator foundProviders = ServiceFinder.find(service).iterator();

      while(foundProviders.hasNext()) {
         providers.add(foundProviders.next());
      }

   }

   public static void configureModel(WSDLModel model, PolicyMap policyMap) throws PolicyException {
      LOGGER.entering(new Object[]{model, policyMap});
      Iterator var2 = model.getServices().values().iterator();

      while(var2.hasNext()) {
         WSDLService service = (WSDLService)var2.next();
         Iterator var4 = service.getPorts().iterator();

         while(var4.hasNext()) {
            WSDLPort port = (WSDLPort)var4.next();
            Collection<WebServiceFeature> features = getPortScopedFeatures(policyMap, service.getName(), port.getName());
            Iterator var7 = features.iterator();

            while(var7.hasNext()) {
               WebServiceFeature feature = (WebServiceFeature)var7.next();
               port.addFeature(feature);
               port.getBinding().addFeature(feature);
            }
         }
      }

      LOGGER.exiting();
   }

   public static Collection<WebServiceFeature> getPortScopedFeatures(PolicyMap policyMap, QName serviceName, QName portName) {
      LOGGER.entering(new Object[]{policyMap, serviceName, portName});
      ArrayList features = new ArrayList();

      try {
         PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(serviceName, portName);
         Iterator var5 = CONFIGURATORS.iterator();

         while(var5.hasNext()) {
            PolicyFeatureConfigurator configurator = (PolicyFeatureConfigurator)var5.next();
            Collection<WebServiceFeature> additionalFeatures = configurator.getFeatures(key, policyMap);
            if (additionalFeatures != null) {
               features.addAll(additionalFeatures);
            }
         }
      } catch (PolicyException var8) {
         throw new WebServiceException(var8);
      }

      LOGGER.exiting(features);
      return features;
   }

   static {
      CONFIGURATORS.add(new AddressingFeatureConfigurator());
      CONFIGURATORS.add(new MtomFeatureConfigurator());
      CONFIGURATORS.add(new FastInfosetFeatureConfigurator());
      CONFIGURATORS.add(new SelectOptimalEncodingFeatureConfigurator());
      addServiceProviders(CONFIGURATORS, PolicyFeatureConfigurator.class);
   }
}
