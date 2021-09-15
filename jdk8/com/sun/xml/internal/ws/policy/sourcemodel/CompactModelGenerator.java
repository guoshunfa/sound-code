package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.Iterator;

class CompactModelGenerator extends PolicyModelGenerator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(CompactModelGenerator.class);
   private final PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator;

   CompactModelGenerator(PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator) {
      this.sourceModelCreator = sourceModelCreator;
   }

   public PolicySourceModel translate(Policy policy) throws PolicyException {
      LOGGER.entering(new Object[]{policy});
      PolicySourceModel model = null;
      if (policy == null) {
         LOGGER.fine(LocalizationMessages.WSP_0047_POLICY_IS_NULL_RETURNING());
      } else {
         model = this.sourceModelCreator.create(policy);
         ModelNode rootNode = model.getRootNode();
         int numberOfAssertionSets = policy.getNumberOfAssertionSets();
         if (numberOfAssertionSets > 1) {
            rootNode = rootNode.createChildExactlyOneNode();
         }

         ModelNode alternativeNode = rootNode;
         Iterator var6 = policy.iterator();

         while(var6.hasNext()) {
            AssertionSet set = (AssertionSet)var6.next();
            if (numberOfAssertionSets > 1) {
               alternativeNode = rootNode.createChildAllNode();
            }

            Iterator var8 = set.iterator();

            while(var8.hasNext()) {
               PolicyAssertion assertion = (PolicyAssertion)var8.next();
               AssertionData data = AssertionData.createAssertionData(assertion.getName(), assertion.getValue(), assertion.getAttributes(), assertion.isOptional(), assertion.isIgnorable());
               ModelNode assertionNode = alternativeNode.createChildAssertionNode(data);
               if (assertion.hasNestedPolicy()) {
                  this.translate(assertionNode, assertion.getNestedPolicy());
               }

               if (assertion.hasParameters()) {
                  this.translate(assertionNode, assertion.getParametersIterator());
               }
            }
         }
      }

      LOGGER.exiting(model);
      return model;
   }

   protected ModelNode translate(ModelNode parentAssertion, NestedPolicy policy) {
      ModelNode nestedPolicyRoot = parentAssertion.createChildPolicyNode();
      AssertionSet set = policy.getAssertionSet();
      this.translate(nestedPolicyRoot, set);
      return nestedPolicyRoot;
   }
}
