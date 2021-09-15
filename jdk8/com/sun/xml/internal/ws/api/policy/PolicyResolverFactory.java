package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.jaxws.DefaultPolicyResolver;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;

public abstract class PolicyResolverFactory {
   public static final PolicyResolver DEFAULT_POLICY_RESOLVER = new DefaultPolicyResolver();

   public abstract PolicyResolver doCreate();

   public static PolicyResolver create() {
      Iterator var0 = ServiceFinder.find(PolicyResolverFactory.class).iterator();

      PolicyResolver policyResolver;
      do {
         if (!var0.hasNext()) {
            return DEFAULT_POLICY_RESOLVER;
         }

         PolicyResolverFactory factory = (PolicyResolverFactory)var0.next();
         policyResolver = factory.doCreate();
      } while(policyResolver == null);

      return policyResolver;
   }
}
