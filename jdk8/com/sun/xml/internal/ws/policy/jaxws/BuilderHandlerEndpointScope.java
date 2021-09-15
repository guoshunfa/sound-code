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

final class BuilderHandlerEndpointScope extends BuilderHandler {
   private final QName service;
   private final QName port;

   BuilderHandlerEndpointScope(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject, QName service, QName port) {
      super(policyURIs, policyStore, policySubject);
      this.service = service;
      this.port = port;
   }

   protected void doPopulate(PolicyMapExtender policyMapExtender) throws PolicyException {
      PolicyMapKey mapKey = PolicyMap.createWsdlEndpointScopeKey(this.service, this.port);
      Iterator var3 = this.getPolicySubjects().iterator();

      while(var3.hasNext()) {
         PolicySubject subject = (PolicySubject)var3.next();
         policyMapExtender.putEndpointSubject(mapKey, subject);
      }

   }

   public String toString() {
      return this.service.toString() + ":" + this.port.toString();
   }
}
