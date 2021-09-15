package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;

public class AddressingFeatureConfigurator implements PolicyFeatureConfigurator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingFeatureConfigurator.class);
   private static final QName[] ADDRESSING_ASSERTIONS;

   public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
      LOGGER.entering(new Object[]{key, policyMap});
      Collection<WebServiceFeature> features = new LinkedList();
      if (key != null && policyMap != null) {
         Policy policy = policyMap.getEndpointEffectivePolicy(key);
         QName[] var5 = ADDRESSING_ASSERTIONS;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            QName addressingAssertionQName = var5[var7];
            if (policy != null && policy.contains(addressingAssertionQName)) {
               Iterator assertions = policy.iterator();

               while(assertions.hasNext()) {
                  AssertionSet assertionSet = (AssertionSet)assertions.next();
                  Iterator policyAssertion = assertionSet.iterator();

                  while(policyAssertion.hasNext()) {
                     PolicyAssertion assertion = (PolicyAssertion)policyAssertion.next();
                     if (assertion.getName().equals(addressingAssertionQName)) {
                        WebServiceFeature feature = AddressingVersion.getFeature(addressingAssertionQName.getNamespaceURI(), true, !assertion.isOptional());
                        if (LOGGER.isLoggable(Level.FINE)) {
                           LOGGER.fine("Added addressing feature \"" + feature + "\" for element \"" + key + "\"");
                        }

                        features.add(feature);
                     }
                  }
               }
            }
         }

         if (policy != null && policy.contains(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
            Iterator var15 = policy.iterator();

            while(var15.hasNext()) {
               AssertionSet assertions = (AssertionSet)var15.next();
               Iterator var17 = assertions.iterator();

               while(var17.hasNext()) {
                  PolicyAssertion assertion = (PolicyAssertion)var17.next();
                  if (assertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
                     NestedPolicy nestedPolicy = assertion.getNestedPolicy();
                     boolean requiresAnonymousResponses = false;
                     boolean requiresNonAnonymousResponses = false;
                     if (nestedPolicy != null) {
                        requiresAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                        requiresNonAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
                     }

                     if (requiresAnonymousResponses && requiresNonAnonymousResponses) {
                        throw new WebServiceException("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
                     }

                     AddressingFeature feature;
                     try {
                        if (requiresAnonymousResponses) {
                           feature = new AddressingFeature(true, !assertion.isOptional(), AddressingFeature.Responses.ANONYMOUS);
                        } else if (requiresNonAnonymousResponses) {
                           feature = new AddressingFeature(true, !assertion.isOptional(), AddressingFeature.Responses.NON_ANONYMOUS);
                        } else {
                           feature = new AddressingFeature(true, !assertion.isOptional());
                        }
                     } catch (NoSuchMethodError var14) {
                        throw (PolicyException)LOGGER.logSevereException(new PolicyException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(AddressingFeature.class))), var14));
                     }

                     if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Added addressing feature \"" + feature + "\" for element \"" + key + "\"");
                     }

                     features.add(feature);
                  }
               }
            }
         }
      }

      LOGGER.exiting(features);
      return features;
   }

   private static String toJar(String url) {
      if (!url.startsWith("jar:")) {
         return url;
      } else {
         url = url.substring(4);
         return url.substring(0, url.lastIndexOf(33));
      }
   }

   static {
      ADDRESSING_ASSERTIONS = new QName[]{new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing")};
   }
}
