package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOMFeature;

public class MtomPolicyMapConfigurator implements PolicyMapConfigurator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(MtomPolicyMapConfigurator.class);

   public Collection<PolicySubject> update(PolicyMap policyMap, SEIModel model, WSBinding wsBinding) throws PolicyException {
      LOGGER.entering(new Object[]{policyMap, model, wsBinding});
      Collection<PolicySubject> subjects = new ArrayList();
      if (policyMap != null) {
         MTOMFeature mtomFeature = (MTOMFeature)wsBinding.getFeature(MTOMFeature.class);
         if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("mtomFeature = " + mtomFeature);
         }

         if (mtomFeature != null && mtomFeature.isEnabled()) {
            QName bindingName = model.getBoundPortTypeName();
            WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(bindingName);
            Policy mtomPolicy = this.createMtomPolicy(bindingName);
            PolicySubject mtomPolicySubject = new PolicySubject(wsdlSubject, mtomPolicy);
            subjects.add(mtomPolicySubject);
            if (LOGGER.isLoggable(Level.FINEST)) {
               LOGGER.fine("Added MTOM policy with ID \"" + mtomPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
            }
         }
      }

      LOGGER.exiting(subjects);
      return subjects;
   }

   private Policy createMtomPolicy(QName bindingName) {
      ArrayList<AssertionSet> assertionSets = new ArrayList(1);
      ArrayList<PolicyAssertion> assertions = new ArrayList(1);
      assertions.add(new MtomPolicyMapConfigurator.MtomAssertion());
      assertionSets.add(AssertionSet.createAssertionSet(assertions));
      return Policy.createPolicy((String)null, bindingName.getLocalPart() + "_MTOM_Policy", assertionSets);
   }

   static class MtomAssertion extends PolicyAssertion {
      private static final AssertionData mtomData;

      MtomAssertion() {
         super(mtomData, (Collection)null, (AssertionSet)null);
      }

      static {
         mtomData = AssertionData.createAssertionData(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);
         mtomData.setOptionalAttribute(true);
      }
   }
}
