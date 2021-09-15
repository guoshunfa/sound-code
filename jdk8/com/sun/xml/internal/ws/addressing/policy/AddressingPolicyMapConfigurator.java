package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
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
import java.util.Collections;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;

public class AddressingPolicyMapConfigurator implements PolicyMapConfigurator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingPolicyMapConfigurator.class);

   public Collection<PolicySubject> update(PolicyMap policyMap, SEIModel model, WSBinding wsBinding) throws PolicyException {
      LOGGER.entering(new Object[]{policyMap, model, wsBinding});
      Collection<PolicySubject> subjects = new ArrayList();
      if (policyMap != null) {
         AddressingFeature addressingFeature = (AddressingFeature)wsBinding.getFeature(AddressingFeature.class);
         if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("addressingFeature = " + addressingFeature);
         }

         if (addressingFeature != null && addressingFeature.isEnabled()) {
            this.addWsamAddressing(subjects, policyMap, model, addressingFeature);
         }
      }

      LOGGER.exiting(subjects);
      return subjects;
   }

   private void addWsamAddressing(Collection<PolicySubject> subjects, PolicyMap policyMap, SEIModel model, AddressingFeature addressingFeature) throws PolicyException {
      QName bindingName = model.getBoundPortTypeName();
      WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(bindingName);
      Policy addressingPolicy = this.createWsamAddressingPolicy(bindingName, addressingFeature);
      PolicySubject addressingPolicySubject = new PolicySubject(wsdlSubject, addressingPolicy);
      subjects.add(addressingPolicySubject);
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.fine("Added addressing policy with ID \"" + addressingPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
      }

   }

   private Policy createWsamAddressingPolicy(QName bindingName, AddressingFeature af) {
      ArrayList<AssertionSet> assertionSets = new ArrayList(1);
      ArrayList<PolicyAssertion> assertions = new ArrayList(1);
      AssertionData addressingData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
      if (!af.isRequired()) {
         addressingData.setOptionalAttribute(true);
      }

      try {
         AddressingFeature.Responses responses = af.getResponses();
         AssertionData nestedAsserData;
         AddressingPolicyMapConfigurator.AddressingAssertion nestedAsser;
         if (responses == AddressingFeature.Responses.ANONYMOUS) {
            nestedAsserData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
            nestedAsser = new AddressingPolicyMapConfigurator.AddressingAssertion(nestedAsserData, (AssertionSet)null);
            assertions.add(new AddressingPolicyMapConfigurator.AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
         } else if (responses == AddressingFeature.Responses.NON_ANONYMOUS) {
            nestedAsserData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
            nestedAsser = new AddressingPolicyMapConfigurator.AddressingAssertion(nestedAsserData, (AssertionSet)null);
            assertions.add(new AddressingPolicyMapConfigurator.AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
         } else {
            assertions.add(new AddressingPolicyMapConfigurator.AddressingAssertion(addressingData, AssertionSet.createAssertionSet((Collection)null)));
         }
      } catch (NoSuchMethodError var9) {
         assertions.add(new AddressingPolicyMapConfigurator.AddressingAssertion(addressingData, AssertionSet.createAssertionSet((Collection)null)));
      }

      assertionSets.add(AssertionSet.createAssertionSet(assertions));
      return Policy.createPolicy((String)null, bindingName.getLocalPart() + "_WSAM_Addressing_Policy", assertionSets);
   }

   private static final class AddressingAssertion extends PolicyAssertion {
      AddressingAssertion(AssertionData assertionData, AssertionSet nestedAlternative) {
         super(assertionData, (Collection)null, nestedAlternative);
      }

      AddressingAssertion(AssertionData assertionData) {
         super(assertionData, (Collection)null, (AssertionSet)null);
      }
   }
}
