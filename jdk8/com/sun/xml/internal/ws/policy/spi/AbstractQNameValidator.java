package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class AbstractQNameValidator implements PolicyAssertionValidator {
   private final Set<String> supportedDomains = new HashSet();
   private final Collection<QName> serverAssertions;
   private final Collection<QName> clientAssertions;

   protected AbstractQNameValidator(Collection<QName> serverSideAssertions, Collection<QName> clientSideAssertions) {
      Iterator var3;
      QName assertion;
      if (serverSideAssertions != null) {
         this.serverAssertions = new HashSet(serverSideAssertions);
         var3 = this.serverAssertions.iterator();

         while(var3.hasNext()) {
            assertion = (QName)var3.next();
            this.supportedDomains.add(assertion.getNamespaceURI());
         }
      } else {
         this.serverAssertions = new HashSet(0);
      }

      if (clientSideAssertions != null) {
         this.clientAssertions = new HashSet(clientSideAssertions);
         var3 = this.clientAssertions.iterator();

         while(var3.hasNext()) {
            assertion = (QName)var3.next();
            this.supportedDomains.add(assertion.getNamespaceURI());
         }
      } else {
         this.clientAssertions = new HashSet(0);
      }

   }

   public String[] declareSupportedDomains() {
      return (String[])this.supportedDomains.toArray(new String[this.supportedDomains.size()]);
   }

   public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) {
      return this.validateAssertion(assertion, this.clientAssertions, this.serverAssertions);
   }

   public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) {
      return this.validateAssertion(assertion, this.serverAssertions, this.clientAssertions);
   }

   private PolicyAssertionValidator.Fitness validateAssertion(PolicyAssertion assertion, Collection<QName> thisSideAssertions, Collection<QName> otherSideAssertions) {
      QName assertionName = assertion.getName();
      if (thisSideAssertions.contains(assertionName)) {
         return PolicyAssertionValidator.Fitness.SUPPORTED;
      } else {
         return otherSideAssertions.contains(assertionName) ? PolicyAssertionValidator.Fitness.UNSUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
      }
   }
}
