package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature;
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
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

public class SelectOptimalEncodingFeatureConfigurator implements PolicyFeatureConfigurator {
   public static final QName enabled = new QName("enabled");

   public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
      Collection<WebServiceFeature> features = new LinkedList();
      if (key != null && policyMap != null) {
         Policy policy = policyMap.getEndpointEffectivePolicy(key);
         if (null != policy && policy.contains(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION)) {
            Iterator assertions = policy.iterator();

            label38:
            while(assertions.hasNext()) {
               AssertionSet assertionSet = (AssertionSet)assertions.next();
               Iterator policyAssertion = assertionSet.iterator();

               while(true) {
                  PolicyAssertion assertion;
                  do {
                     if (!policyAssertion.hasNext()) {
                        continue label38;
                     }

                     assertion = (PolicyAssertion)policyAssertion.next();
                  } while(!EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION.equals(assertion.getName()));

                  String value = assertion.getAttributeValue(enabled);
                  boolean isSelectOptimalEncodingEnabled = value == null || Boolean.valueOf(value.trim());
                  features.add(new SelectOptimalEncodingFeature(isSelectOptimalEncodingEnabled));
               }
            }
         }
      }

      return features;
   }
}
