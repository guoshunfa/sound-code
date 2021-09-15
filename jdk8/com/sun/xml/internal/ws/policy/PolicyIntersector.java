package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class PolicyIntersector {
   private static final PolicyIntersector STRICT_INTERSECTOR;
   private static final PolicyIntersector LAX_INTERSECTOR;
   private static final PolicyLogger LOGGER;
   private PolicyIntersector.CompatibilityMode mode;

   private PolicyIntersector(PolicyIntersector.CompatibilityMode intersectionMode) {
      this.mode = intersectionMode;
   }

   public static PolicyIntersector createStrictPolicyIntersector() {
      return STRICT_INTERSECTOR;
   }

   public static PolicyIntersector createLaxPolicyIntersector() {
      return LAX_INTERSECTOR;
   }

   public Policy intersect(Policy... policies) {
      if (policies != null && policies.length != 0) {
         if (policies.length == 1) {
            return policies[0];
         } else {
            boolean found = false;
            boolean allPoliciesEmpty = true;
            NamespaceVersion latestVersion = null;
            Policy[] var5 = policies;
            int var6 = policies.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Policy tested = var5[var7];
               if (tested.isEmpty()) {
                  found = true;
               } else {
                  if (tested.isNull()) {
                     found = true;
                  }

                  allPoliciesEmpty = false;
               }

               if (latestVersion == null) {
                  latestVersion = tested.getNamespaceVersion();
               } else if (latestVersion.compareTo(tested.getNamespaceVersion()) < 0) {
                  latestVersion = tested.getNamespaceVersion();
               }

               if (found && !allPoliciesEmpty) {
                  return Policy.createNullPolicy(latestVersion, (String)null, (String)null);
               }
            }

            latestVersion = latestVersion != null ? latestVersion : NamespaceVersion.getLatestVersion();
            if (allPoliciesEmpty) {
               return Policy.createEmptyPolicy(latestVersion, (String)null, (String)null);
            } else {
               List<AssertionSet> finalAlternatives = new LinkedList(policies[0].getContent());
               Queue<AssertionSet> testedAlternatives = new LinkedList();
               List<AssertionSet> alternativesToMerge = new ArrayList(2);

               for(int i = 1; i < policies.length; ++i) {
                  Collection<AssertionSet> currentAlternatives = policies[i].getContent();
                  testedAlternatives.clear();
                  testedAlternatives.addAll(finalAlternatives);
                  finalAlternatives.clear();

                  AssertionSet testedAlternative;
                  while((testedAlternative = (AssertionSet)testedAlternatives.poll()) != null) {
                     Iterator var11 = currentAlternatives.iterator();

                     while(var11.hasNext()) {
                        AssertionSet currentAlternative = (AssertionSet)var11.next();
                        if (testedAlternative.isCompatibleWith(currentAlternative, this.mode)) {
                           alternativesToMerge.add(testedAlternative);
                           alternativesToMerge.add(currentAlternative);
                           finalAlternatives.add(AssertionSet.createMergedAssertionSet(alternativesToMerge));
                           alternativesToMerge.clear();
                        }
                     }
                  }
               }

               return Policy.createPolicy(latestVersion, (String)null, (String)null, finalAlternatives);
            }
         }
      } else {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED()));
      }
   }

   static {
      STRICT_INTERSECTOR = new PolicyIntersector(PolicyIntersector.CompatibilityMode.STRICT);
      LAX_INTERSECTOR = new PolicyIntersector(PolicyIntersector.CompatibilityMode.LAX);
      LOGGER = PolicyLogger.getLogger(PolicyIntersector.class);
   }

   static enum CompatibilityMode {
      STRICT,
      LAX;
   }
}
