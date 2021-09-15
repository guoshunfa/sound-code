package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MtomFeatureConfigurator implements PolicyFeatureConfigurator {
   public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
      Collection<WebServiceFeature> features = new LinkedList();
      if (key != null && policyMap != null) {
         Policy policy = policyMap.getEndpointEffectivePolicy(key);
         if (null != policy && policy.contains(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION)) {
            Iterator assertions = policy.iterator();

            while(assertions.hasNext()) {
               AssertionSet assertionSet = (AssertionSet)assertions.next();
               Iterator policyAssertion = assertionSet.iterator();

               while(policyAssertion.hasNext()) {
                  PolicyAssertion assertion = (PolicyAssertion)policyAssertion.next();
                  if (EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION.equals(assertion.getName())) {
                     features.add(new MTOMFeature(true));
                  }
               }
            }
         }
      }

      return features;
   }
}
