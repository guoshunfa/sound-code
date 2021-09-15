package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;

class DefaultPolicyAssertionCreator implements PolicyAssertionCreator {
   public String[] getSupportedDomainNamespaceURIs() {
      return null;
   }

   public PolicyAssertion createAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative, PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
      return new DefaultPolicyAssertionCreator.DefaultPolicyAssertion(data, assertionParameters, nestedAlternative);
   }

   private static final class DefaultPolicyAssertion extends PolicyAssertion {
      DefaultPolicyAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
         super(data, assertionParameters, nestedAlternative);
      }
   }
}
