package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ManagedServiceAssertion extends ManagementAssertion {
   public static final QName MANAGED_SERVICE_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedService");
   private static final QName COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementations");
   private static final QName COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementation");
   private static final QName CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfiguratorImplementation");
   private static final QName CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigSaverImplementation");
   private static final QName CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigReaderImplementation");
   private static final QName CLASS_NAME_ATTRIBUTE_QNAME = new QName("className");
   private static final QName ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME = new QName("endpointDisposeDelay");
   private static final Logger LOGGER = Logger.getLogger(ManagedServiceAssertion.class);

   public static ManagedServiceAssertion getAssertion(WSEndpoint endpoint) throws WebServiceException {
      LOGGER.entering(endpoint);
      PolicyMap policyMap = endpoint.getPolicyMap();
      ManagedServiceAssertion assertion = (ManagedServiceAssertion)ManagementAssertion.getAssertion(MANAGED_SERVICE_QNAME, policyMap, endpoint.getServiceName(), endpoint.getPortName(), ManagedServiceAssertion.class);
      LOGGER.exiting(assertion);
      return assertion;
   }

   public ManagedServiceAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
      super(MANAGED_SERVICE_QNAME, data, assertionParameters);
   }

   public boolean isManagementEnabled() {
      String management = this.getAttributeValue(MANAGEMENT_ATTRIBUTE_QNAME);
      boolean result = true;
      if (management != null) {
         if (management.trim().toLowerCase().equals("on")) {
            result = true;
         } else {
            result = Boolean.parseBoolean(management);
         }
      }

      return result;
   }

   public long getEndpointDisposeDelay(long defaultDelay) throws WebServiceException {
      long result = defaultDelay;
      String delayText = this.getAttributeValue(ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME);
      if (delayText != null) {
         try {
            result = Long.parseLong(delayText);
         } catch (NumberFormatException var7) {
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(delayText), var7));
         }
      }

      return result;
   }

   public Collection<ManagedServiceAssertion.ImplementationRecord> getCommunicationServerImplementations() {
      Collection<ManagedServiceAssertion.ImplementationRecord> result = new LinkedList();
      Iterator parameters = this.getParametersIterator();

      while(true) {
         PolicyAssertion parameter;
         do {
            if (!parameters.hasNext()) {
               return result;
            }

            parameter = (PolicyAssertion)parameters.next();
         } while(!COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME.equals(parameter.getName()));

         Iterator<PolicyAssertion> implementations = parameter.getParametersIterator();
         if (!implementations.hasNext()) {
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1005_EXPECTED_COMMUNICATION_CHILD()));
         }

         while(implementations.hasNext()) {
            PolicyAssertion implementation = (PolicyAssertion)implementations.next();
            if (!COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME.equals(implementation.getName())) {
               throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1004_EXPECTED_XML_TAG(COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME, implementation.getName())));
            }

            result.add(this.getImplementation(implementation));
         }
      }
   }

   public ManagedServiceAssertion.ImplementationRecord getConfiguratorImplementation() {
      return this.findImplementation(CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME);
   }

   public ManagedServiceAssertion.ImplementationRecord getConfigSaverImplementation() {
      return this.findImplementation(CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME);
   }

   public ManagedServiceAssertion.ImplementationRecord getConfigReaderImplementation() {
      return this.findImplementation(CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME);
   }

   private ManagedServiceAssertion.ImplementationRecord findImplementation(QName implementationName) {
      Iterator parameters = this.getParametersIterator();

      PolicyAssertion parameter;
      do {
         if (!parameters.hasNext()) {
            return null;
         }

         parameter = (PolicyAssertion)parameters.next();
      } while(!implementationName.equals(parameter.getName()));

      return this.getImplementation(parameter);
   }

   private ManagedServiceAssertion.ImplementationRecord getImplementation(PolicyAssertion rootParameter) {
      String className = rootParameter.getAttributeValue(CLASS_NAME_ATTRIBUTE_QNAME);
      HashMap<QName, String> parameterMap = new HashMap();
      Iterator<PolicyAssertion> implementationParameters = rootParameter.getParametersIterator();
      LinkedList nestedParameters = new LinkedList();

      while(true) {
         while(implementationParameters.hasNext()) {
            PolicyAssertion parameterAssertion = (PolicyAssertion)implementationParameters.next();
            QName parameterName = parameterAssertion.getName();
            if (parameterAssertion.hasParameters()) {
               Map<QName, String> nestedParameterMap = new HashMap();

               PolicyAssertion parameter;
               String value;
               for(Iterator parameters = parameterAssertion.getParametersIterator(); parameters.hasNext(); nestedParameterMap.put(parameter.getName(), value)) {
                  parameter = (PolicyAssertion)parameters.next();
                  value = parameter.getValue();
                  if (value != null) {
                     value = value.trim();
                  }
               }

               nestedParameters.add(new ManagedServiceAssertion.NestedParameters(parameterName, nestedParameterMap));
            } else {
               String value = parameterAssertion.getValue();
               if (value != null) {
                  value = value.trim();
               }

               parameterMap.put(parameterName, value);
            }
         }

         return new ManagedServiceAssertion.ImplementationRecord(className, parameterMap, nestedParameters);
      }
   }

   public static class NestedParameters {
      private final QName name;
      private final Map<QName, String> parameters;

      private NestedParameters(QName name, Map<QName, String> parameters) {
         this.name = name;
         this.parameters = parameters;
      }

      public QName getName() {
         return this.name;
      }

      public Map<QName, String> getParameters() {
         return this.parameters;
      }

      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            ManagedServiceAssertion.NestedParameters other = (ManagedServiceAssertion.NestedParameters)obj;
            if (this.name == null) {
               if (other.name != null) {
                  return false;
               }
            } else if (!this.name.equals(other.name)) {
               return false;
            }

            if (this.parameters == other.parameters || this.parameters != null && this.parameters.equals(other.parameters)) {
               return true;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         int hash = 5;
         int hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
         hash = 59 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
         return hash;
      }

      public String toString() {
         StringBuilder text = new StringBuilder("NestedParameters: ");
         text.append("name = \"").append((Object)this.name).append("\", ");
         text.append("parameters = \"").append((Object)this.parameters).append("\"");
         return text.toString();
      }

      // $FF: synthetic method
      NestedParameters(QName x0, Map x1, Object x2) {
         this(x0, x1);
      }
   }

   public static class ImplementationRecord {
      private final String implementation;
      private final Map<QName, String> parameters;
      private final Collection<ManagedServiceAssertion.NestedParameters> nestedParameters;

      protected ImplementationRecord(String implementation, Map<QName, String> parameters, Collection<ManagedServiceAssertion.NestedParameters> nestedParameters) {
         this.implementation = implementation;
         this.parameters = parameters;
         this.nestedParameters = nestedParameters;
      }

      public String getImplementation() {
         return this.implementation;
      }

      public Map<QName, String> getParameters() {
         return this.parameters;
      }

      public Collection<ManagedServiceAssertion.NestedParameters> getNestedParameters() {
         return this.nestedParameters;
      }

      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            ManagedServiceAssertion.ImplementationRecord other;
            label44: {
               other = (ManagedServiceAssertion.ImplementationRecord)obj;
               if (this.implementation == null) {
                  if (other.implementation == null) {
                     break label44;
                  }
               } else if (this.implementation.equals(other.implementation)) {
                  break label44;
               }

               return false;
            }

            if (this.parameters == other.parameters || this.parameters != null && this.parameters.equals(other.parameters)) {
               return this.nestedParameters == other.nestedParameters || this.nestedParameters != null && this.nestedParameters.equals(other.nestedParameters);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         int hash = 3;
         int hash = 53 * hash + (this.implementation != null ? this.implementation.hashCode() : 0);
         hash = 53 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
         hash = 53 * hash + (this.nestedParameters != null ? this.nestedParameters.hashCode() : 0);
         return hash;
      }

      public String toString() {
         StringBuilder text = new StringBuilder("ImplementationRecord: ");
         text.append("implementation = \"").append(this.implementation).append("\", ");
         text.append("parameters = \"").append((Object)this.parameters).append("\", ");
         text.append("nested parameters = \"").append((Object)this.nestedParameters).append("\"");
         return text.toString();
      }
   }
}
