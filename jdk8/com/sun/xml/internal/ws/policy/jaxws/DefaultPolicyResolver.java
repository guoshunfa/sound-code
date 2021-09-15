package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.AlternativeSelector;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.ValidationProcessor;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.util.Iterator;
import javax.xml.ws.WebServiceException;

public class DefaultPolicyResolver implements PolicyResolver {
   public PolicyMap resolve(PolicyResolver.ServerContext context) {
      PolicyMap map = context.getPolicyMap();
      if (map != null) {
         this.validateServerPolicyMap(map);
      }

      return map;
   }

   public PolicyMap resolve(PolicyResolver.ClientContext context) {
      PolicyMap map = context.getPolicyMap();
      if (map != null) {
         map = this.doAlternativeSelection(map);
      }

      return map;
   }

   private void validateServerPolicyMap(PolicyMap policyMap) {
      try {
         ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
         Iterator var3 = policyMap.iterator();

         while(var3.hasNext()) {
            Policy policy = (Policy)var3.next();
            Iterator var5 = policy.iterator();

            while(var5.hasNext()) {
               AssertionSet assertionSet = (AssertionSet)var5.next();
               Iterator var7 = assertionSet.iterator();

               while(var7.hasNext()) {
                  PolicyAssertion assertion = (PolicyAssertion)var7.next();
                  PolicyAssertionValidator.Fitness validationResult = validationProcessor.validateServerSide(assertion);
                  if (validationResult != PolicyAssertionValidator.Fitness.SUPPORTED) {
                     throw new PolicyException(PolicyMessages.WSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(assertion.getName(), validationResult));
                  }
               }
            }
         }

      } catch (PolicyException var10) {
         throw new WebServiceException(var10);
      }
   }

   private PolicyMap doAlternativeSelection(PolicyMap policyMap) {
      EffectivePolicyModifier modifier = EffectivePolicyModifier.createEffectivePolicyModifier();
      modifier.connect(policyMap);

      try {
         AlternativeSelector.doSelection(modifier);
         return policyMap;
      } catch (PolicyException var4) {
         throw new WebServiceException(var4);
      }
   }
}
