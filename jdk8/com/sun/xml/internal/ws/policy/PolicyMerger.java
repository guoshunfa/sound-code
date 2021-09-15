package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public final class PolicyMerger {
   private static final PolicyMerger merger = new PolicyMerger();

   private PolicyMerger() {
   }

   public static PolicyMerger getMerger() {
      return merger;
   }

   public Policy merge(Collection<Policy> policies) {
      if (policies != null && !policies.isEmpty()) {
         if (policies.size() == 1) {
            return (Policy)policies.iterator().next();
         } else {
            Collection<Collection<AssertionSet>> alternativeSets = new LinkedList();
            StringBuilder id = new StringBuilder();
            NamespaceVersion mergedVersion = ((Policy)policies.iterator().next()).getNamespaceVersion();
            Iterator var5 = policies.iterator();

            while(var5.hasNext()) {
               Policy policy = (Policy)var5.next();
               alternativeSets.add(policy.getContent());
               if (mergedVersion.compareTo(policy.getNamespaceVersion()) < 0) {
                  mergedVersion = policy.getNamespaceVersion();
               }

               String policyId = policy.getId();
               if (policyId != null) {
                  if (id.length() > 0) {
                     id.append('-');
                  }

                  id.append(policyId);
               }
            }

            Collection<Collection<AssertionSet>> combinedAlternatives = PolicyUtils.Collections.combine((Collection)null, alternativeSets, false);
            if (combinedAlternatives != null && !combinedAlternatives.isEmpty()) {
               Collection<AssertionSet> mergedSetList = new ArrayList(combinedAlternatives.size());
               Iterator var11 = combinedAlternatives.iterator();

               while(var11.hasNext()) {
                  Collection<AssertionSet> toBeMerged = (Collection)var11.next();
                  mergedSetList.add(AssertionSet.createMergedAssertionSet(toBeMerged));
               }

               return Policy.createPolicy(mergedVersion, (String)null, id.length() == 0 ? null : id.toString(), mergedSetList);
            } else {
               return Policy.createNullPolicy(mergedVersion, (String)null, id.length() == 0 ? null : id.toString());
            }
         }
      } else {
         return null;
      }
   }
}
