package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.ModelTranslator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

abstract class BuilderHandler {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(BuilderHandler.class);
   Map<String, PolicySourceModel> policyStore;
   Collection<String> policyURIs;
   Object policySubject;

   BuilderHandler(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject) {
      this.policyStore = policyStore;
      this.policyURIs = policyURIs;
      this.policySubject = policySubject;
   }

   final void populate(PolicyMapExtender policyMapExtender) throws PolicyException {
      if (null == policyMapExtender) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL()));
      } else {
         this.doPopulate(policyMapExtender);
      }
   }

   protected abstract void doPopulate(PolicyMapExtender var1) throws PolicyException;

   final Collection<Policy> getPolicies() throws PolicyException {
      if (null == this.policyURIs) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1004_POLICY_URIS_CAN_NOT_BE_NULL()));
      } else if (null == this.policyStore) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1010_NO_POLICIES_DEFINED()));
      } else {
         Collection<Policy> result = new ArrayList(this.policyURIs.size());
         Iterator var2 = this.policyURIs.iterator();

         while(var2.hasNext()) {
            String policyURI = (String)var2.next();
            PolicySourceModel sourceModel = (PolicySourceModel)this.policyStore.get(policyURI);
            if (sourceModel == null) {
               throw (PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(policyURI)));
            }

            result.add(ModelTranslator.getTranslator().translate(sourceModel));
         }

         return result;
      }
   }

   final Collection<PolicySubject> getPolicySubjects() throws PolicyException {
      Collection<Policy> policies = this.getPolicies();
      Collection<PolicySubject> result = new ArrayList(policies.size());
      Iterator var3 = policies.iterator();

      while(var3.hasNext()) {
         Policy policy = (Policy)var3.next();
         result.add(new PolicySubject(this.policySubject, policy));
      }

      return result;
   }
}
