package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class PolicyMapBuilder {
   private List<BuilderHandler> policyBuilders = new LinkedList();

   void registerHandler(BuilderHandler builder) {
      if (null != builder) {
         this.policyBuilders.add(builder);
      }

   }

   PolicyMap getPolicyMap(PolicyMapMutator... externalMutators) throws PolicyException {
      return this.getNewPolicyMap(externalMutators);
   }

   private PolicyMap getNewPolicyMap(PolicyMapMutator... externalMutators) throws PolicyException {
      HashSet<PolicyMapMutator> mutators = new HashSet();
      PolicyMapExtender myExtender = PolicyMapExtender.createPolicyMapExtender();
      mutators.add(myExtender);
      if (null != externalMutators) {
         mutators.addAll(Arrays.asList(externalMutators));
      }

      PolicyMap policyMap = PolicyMap.createPolicyMap(mutators);
      Iterator var5 = this.policyBuilders.iterator();

      while(var5.hasNext()) {
         BuilderHandler builder = (BuilderHandler)var5.next();
         builder.populate(myExtender);
      }

      return policyMap;
   }

   void unregisterAll() {
      this.policyBuilders = null;
   }
}
