package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class PolicySubject {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicySubject.class);
   private final List<Policy> policies = new LinkedList();
   private final Object subject;

   public PolicySubject(Object subject, Policy policy) throws IllegalArgumentException {
      if (subject != null && policy != null) {
         this.subject = subject;
         this.attach(policy);
      } else {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL(subject, policy)));
      }
   }

   public PolicySubject(Object subject, Collection<Policy> policies) throws IllegalArgumentException {
      if (subject != null && policies != null) {
         if (policies.isEmpty()) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY()));
         } else {
            this.subject = subject;
            this.policies.addAll(policies);
         }
      } else {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL()));
      }
   }

   public void attach(Policy policy) {
      if (policy == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL()));
      } else {
         this.policies.add(policy);
      }
   }

   public Policy getEffectivePolicy(PolicyMerger merger) throws PolicyException {
      return merger.merge(this.policies);
   }

   public Object getSubject() {
      return this.subject;
   }

   public String toString() {
      return this.toString(0, new StringBuffer()).toString();
   }

   StringBuffer toString(int indentLevel, StringBuffer buffer) {
      String indent = PolicyUtils.Text.createIndent(indentLevel);
      String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
      buffer.append(indent).append("policy subject {").append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("subject = '").append(this.subject).append('\'').append(PolicyUtils.Text.NEW_LINE);
      Iterator var5 = this.policies.iterator();

      while(var5.hasNext()) {
         Policy policy = (Policy)var5.next();
         policy.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
      }

      buffer.append(indent).append('}');
      return buffer;
   }
}
