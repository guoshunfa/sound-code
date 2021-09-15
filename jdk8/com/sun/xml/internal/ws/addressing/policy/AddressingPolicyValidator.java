package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class AddressingPolicyValidator implements PolicyAssertionValidator {
   private static final ArrayList<QName> supportedAssertions = new ArrayList();
   private static final PolicyLogger LOGGER;

   public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) {
      return supportedAssertions.contains(assertion.getName()) ? PolicyAssertionValidator.Fitness.SUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
   }

   public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) {
      if (!supportedAssertions.contains(assertion.getName())) {
         return PolicyAssertionValidator.Fitness.UNKNOWN;
      } else {
         if (assertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
            NestedPolicy nestedPolicy = assertion.getNestedPolicy();
            if (nestedPolicy != null) {
               boolean requiresAnonymousResponses = false;
               boolean requiresNonAnonymousResponses = false;
               Iterator var5 = nestedPolicy.getAssertionSet().iterator();

               while(var5.hasNext()) {
                  PolicyAssertion nestedAsser = (PolicyAssertion)var5.next();
                  if (nestedAsser.getName().equals(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION)) {
                     requiresAnonymousResponses = true;
                  } else {
                     if (!nestedAsser.getName().equals(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION)) {
                        LOGGER.warning("Found unsupported assertion:\n" + nestedAsser + "\nnested into assertion:\n" + assertion);
                        return PolicyAssertionValidator.Fitness.UNSUPPORTED;
                     }

                     requiresNonAnonymousResponses = true;
                  }
               }

               if (requiresAnonymousResponses && requiresNonAnonymousResponses) {
                  LOGGER.warning("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
                  return PolicyAssertionValidator.Fitness.INVALID;
               }
            }
         }

         return PolicyAssertionValidator.Fitness.SUPPORTED;
      }
   }

   public String[] declareSupportedDomains() {
      return new String[]{AddressingVersion.MEMBER.policyNsUri, AddressingVersion.W3C.policyNsUri, "http://www.w3.org/2007/05/addressing/metadata"};
   }

   static {
      supportedAssertions.add(new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing"));
      supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
      supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
      supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
      LOGGER = PolicyLogger.getLogger(AddressingPolicyValidator.class);
   }
}
