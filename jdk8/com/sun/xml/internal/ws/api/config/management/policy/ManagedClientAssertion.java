package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ManagedClientAssertion extends ManagementAssertion {
   public static final QName MANAGED_CLIENT_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedClient");
   private static final Logger LOGGER = Logger.getLogger(ManagedClientAssertion.class);

   public static ManagedClientAssertion getAssertion(WSPortInfo portInfo) throws WebServiceException {
      if (portInfo == null) {
         return null;
      } else {
         LOGGER.entering(portInfo);
         PolicyMap policyMap = portInfo.getPolicyMap();
         ManagedClientAssertion assertion = (ManagedClientAssertion)ManagementAssertion.getAssertion(MANAGED_CLIENT_QNAME, policyMap, portInfo.getServiceName(), portInfo.getPortName(), ManagedClientAssertion.class);
         LOGGER.exiting(assertion);
         return assertion;
      }
   }

   public ManagedClientAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
      super(MANAGED_CLIENT_QNAME, data, assertionParameters);
   }

   public boolean isManagementEnabled() {
      String management = this.getAttributeValue(MANAGEMENT_ATTRIBUTE_QNAME);
      if (management != null && (management.trim().toLowerCase().equals("on") || Boolean.parseBoolean(management))) {
         LOGGER.warning(ManagementMessages.WSM_1006_CLIENT_MANAGEMENT_ENABLED());
      }

      return false;
   }
}
