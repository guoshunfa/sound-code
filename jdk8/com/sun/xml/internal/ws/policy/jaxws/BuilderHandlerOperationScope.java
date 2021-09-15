package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

final class BuilderHandlerOperationScope extends BuilderHandler {
   private final QName service;
   private final QName port;
   private final QName operation;

   BuilderHandlerOperationScope(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject, QName service, QName port, QName operation) {
      super(policyURIs, policyStore, policySubject);
      this.service = service;
      this.port = port;
      this.operation = operation;
   }

   protected void doPopulate(PolicyMapExtender policyMapExtender) throws PolicyException {
      PolicyMapKey mapKey = PolicyMap.createWsdlOperationScopeKey(this.service, this.port, this.operation);
      Iterator var3 = this.getPolicySubjects().iterator();

      while(var3.hasNext()) {
         PolicySubject subject = (PolicySubject)var3.next();
         policyMapExtender.putOperationSubject(mapKey, subject);
      }

   }
}
