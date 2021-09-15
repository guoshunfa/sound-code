package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.Iterator;

class NormalizedModelGenerator extends PolicyModelGenerator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(NormalizedModelGenerator.class);
   private final PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator;

   NormalizedModelGenerator(PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator) {
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
         ModelNode exactlyOneNode = rootNode.createChildExactlyOneNode();
         Iterator var5 = policy.iterator();

         while(var5.hasNext()) {
            AssertionSet set = (AssertionSet)var5.next();
            ModelNode alternativeNode = exactlyOneNode.createChildAllNode();
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
      ModelNode exactlyOneNode = nestedPolicyRoot.createChildExactlyOneNode();
      AssertionSet set = policy.getAssertionSet();
      ModelNode alternativeNode = exactlyOneNode.createChildAllNode();
      this.translate(alternativeNode, set);
      return nestedPolicyRoot;
   }
}
