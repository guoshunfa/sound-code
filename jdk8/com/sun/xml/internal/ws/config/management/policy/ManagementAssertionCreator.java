package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;
import javax.xml.namespace.QName;

public class ManagementAssertionCreator implements PolicyAssertionCreator {
   public String[] getSupportedDomainNamespaceURIs() {
      return new String[]{"http://java.sun.com/xml/ns/metro/management"};
   }

   public PolicyAssertion createAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative, PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
      QName name = data.getName();
      if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(name)) {
         return new ManagedServiceAssertion(data, assertionParameters);
      } else {
         return (PolicyAssertion)(ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(name) ? new ManagedClientAssertion(data, assertionParameters) : defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, (PolicyAssertionCreator)null));
      }
   }
}
