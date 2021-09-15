package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.subject.PolicyMapKeyConverter;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.namespace.QName;

public class PolicyMapUtil {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapUtil.class);
   private static final PolicyMerger MERGER = PolicyMerger.getMerger();

   private PolicyMapUtil() {
   }

   public static void rejectAlternatives(PolicyMap map) throws PolicyException {
      Iterator var1 = map.iterator();

      Policy policy;
      do {
         if (!var1.hasNext()) {
            return;
         }

         policy = (Policy)var1.next();
      } while(policy.getNumberOfAssertionSets() <= 1);

      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0035_RECONFIGURE_ALTERNATIVES(policy.getIdOrName())));
   }

   public static void insertPolicies(PolicyMap policyMap, Collection<PolicySubject> policySubjects, QName serviceName, QName portName) throws PolicyException {
      LOGGER.entering(new Object[]{policyMap, policySubjects, serviceName, portName});
      HashMap<WsdlBindingSubject, Collection<Policy>> subjectToPolicies = new HashMap();
      Iterator var5 = policySubjects.iterator();

      while(var5.hasNext()) {
         PolicySubject subject = (PolicySubject)var5.next();
         Object actualSubject = subject.getSubject();
         if (actualSubject instanceof WsdlBindingSubject) {
            WsdlBindingSubject wsdlSubject = (WsdlBindingSubject)actualSubject;
            Collection<Policy> subjectPolicies = new LinkedList();
            subjectPolicies.add(subject.getEffectivePolicy(MERGER));
            Collection<Policy> existingPolicies = (Collection)subjectToPolicies.put(wsdlSubject, subjectPolicies);
            if (existingPolicies != null) {
               subjectPolicies.addAll(existingPolicies);
            }
         }
      }

      PolicyMapKeyConverter converter = new PolicyMapKeyConverter(serviceName, portName);
      Iterator var12 = subjectToPolicies.keySet().iterator();

      while(var12.hasNext()) {
         WsdlBindingSubject wsdlSubject = (WsdlBindingSubject)var12.next();
         PolicySubject newSubject = new PolicySubject(wsdlSubject, (Collection)subjectToPolicies.get(wsdlSubject));
         PolicyMapKey mapKey = converter.getPolicyMapKey(wsdlSubject);
         if (wsdlSubject.isBindingSubject()) {
            policyMap.putSubject(PolicyMap.ScopeType.ENDPOINT, mapKey, newSubject);
         } else if (wsdlSubject.isBindingOperationSubject()) {
            policyMap.putSubject(PolicyMap.ScopeType.OPERATION, mapKey, newSubject);
         } else if (wsdlSubject.isBindingMessageSubject()) {
            switch(wsdlSubject.getMessageType()) {
            case INPUT:
               policyMap.putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, mapKey, newSubject);
               break;
            case OUTPUT:
               policyMap.putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, mapKey, newSubject);
               break;
            case FAULT:
               policyMap.putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, mapKey, newSubject);
            }
         }
      }

      LOGGER.exiting();
   }
}
