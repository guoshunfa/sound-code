package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import javax.xml.namespace.QName;

public class ManagementPolicyValidator implements PolicyAssertionValidator {
   public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) {
      QName assertionName = assertion.getName();
      if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(assertionName)) {
         return PolicyAssertionValidator.Fitness.SUPPORTED;
      } else {
         return ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(assertionName) ? PolicyAssertionValidator.Fitness.UNSUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
      }
   }

   public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) {
      QName assertionName = assertion.getName();
      if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(assertionName)) {
         return PolicyAssertionValidator.Fitness.SUPPORTED;
      } else {
         return ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(assertionName) ? PolicyAssertionValidator.Fitness.UNSUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
      }
   }

   public String[] declareSupportedDomains() {
      return new String[]{"http://java.sun.com/xml/ns/metro/management"};
   }
}
