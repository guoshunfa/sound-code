package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.SimpleAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class ManagementAssertion extends SimpleAssertion {
   protected static final QName MANAGEMENT_ATTRIBUTE_QNAME = new QName("management");
   protected static final QName MONITORING_ATTRIBUTE_QNAME = new QName("monitoring");
   private static final QName ID_ATTRIBUTE_QNAME = new QName("id");
   private static final QName START_ATTRIBUTE_QNAME = new QName("start");
   private static final Logger LOGGER = Logger.getLogger(ManagementAssertion.class);

   protected static <T extends ManagementAssertion> T getAssertion(QName name, PolicyMap policyMap, QName serviceName, QName portName, Class<T> type) throws WebServiceException {
      try {
         PolicyAssertion assertion = null;
         if (policyMap != null) {
            PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(serviceName, portName);
            Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (policy != null) {
               Iterator<AssertionSet> assertionSets = policy.iterator();
               if (assertionSets.hasNext()) {
                  AssertionSet assertionSet = (AssertionSet)assertionSets.next();
                  Iterator<PolicyAssertion> assertions = assertionSet.get(name).iterator();
                  if (assertions.hasNext()) {
                     assertion = (PolicyAssertion)assertions.next();
                  }
               }
            }
         }

         return assertion == null ? null : (ManagementAssertion)assertion.getImplementation(type);
      } catch (PolicyException var11) {
         throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1001_FAILED_ASSERTION(name), var11));
      }
   }

   protected ManagementAssertion(QName name, AssertionData data, Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
      super(data, assertionParameters);
      if (!name.equals(data.getName())) {
         throw (AssertionCreationException)LOGGER.logSevereException(new AssertionCreationException(data, ManagementMessages.WSM_1002_EXPECTED_MANAGEMENT_ASSERTION(name)));
      } else if (this.isManagementEnabled() && !data.containsAttribute(ID_ATTRIBUTE_QNAME)) {
         throw (AssertionCreationException)LOGGER.logSevereException(new AssertionCreationException(data, ManagementMessages.WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(name)));
      }
   }

   public String getId() {
      return this.getAttributeValue(ID_ATTRIBUTE_QNAME);
   }

   public String getStart() {
      return this.getAttributeValue(START_ATTRIBUTE_QNAME);
   }

   public abstract boolean isManagementEnabled();

   public ManagementAssertion.Setting monitoringAttribute() {
      String monitoring = this.getAttributeValue(MONITORING_ATTRIBUTE_QNAME);
      ManagementAssertion.Setting result = ManagementAssertion.Setting.NOT_SET;
      if (monitoring != null) {
         if (!monitoring.trim().toLowerCase().equals("on") && !Boolean.parseBoolean(monitoring)) {
            result = ManagementAssertion.Setting.OFF;
         } else {
            result = ManagementAssertion.Setting.ON;
         }
      }

      return result;
   }

   public static enum Setting {
      NOT_SET,
      OFF,
      ON;
   }
}
