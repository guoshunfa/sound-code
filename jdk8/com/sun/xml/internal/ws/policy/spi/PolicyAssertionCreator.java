package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public interface PolicyAssertionCreator {
   String[] getSupportedDomainNamespaceURIs();

   PolicyAssertion createAssertion(AssertionData var1, Collection<PolicyAssertion> var2, AssertionSet var3, PolicyAssertionCreator var4) throws AssertionCreationException;
}
