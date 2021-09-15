package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class PolicyScope {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyScope.class);
   private final List<PolicySubject> subjects = new LinkedList();

   PolicyScope(List<PolicySubject> initialSubjects) {
      if (initialSubjects != null && !initialSubjects.isEmpty()) {
         this.subjects.addAll(initialSubjects);
      }

   }

   void attach(PolicySubject subject) {
      if (subject == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL()));
      } else {
         this.subjects.add(subject);
      }
   }

   void dettachAllSubjects() {
      this.subjects.clear();
   }

   Policy getEffectivePolicy(PolicyMerger merger) throws PolicyException {
      LinkedList<Policy> policies = new LinkedList();
      Iterator var3 = this.subjects.iterator();

      while(var3.hasNext()) {
         PolicySubject subject = (PolicySubject)var3.next();
         policies.add(subject.getEffectivePolicy(merger));
      }

      return merger.merge(policies);
   }

   Collection<PolicySubject> getPolicySubjects() {
      return this.subjects;
   }

   public String toString() {
      return this.toString(0, new StringBuffer()).toString();
   }

   StringBuffer toString(int indentLevel, StringBuffer buffer) {
      String indent = PolicyUtils.Text.createIndent(indentLevel);
      buffer.append(indent).append("policy scope {").append(PolicyUtils.Text.NEW_LINE);
      Iterator var4 = this.subjects.iterator();

      while(var4.hasNext()) {
         PolicySubject policySubject = (PolicySubject)var4.next();
         policySubject.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
      }

      buffer.append(indent).append('}');
      return buffer;
   }
}
