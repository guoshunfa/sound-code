package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XmlPolicyModelUnmarshaller extends PolicyModelUnmarshaller {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelUnmarshaller.class);

   protected XmlPolicyModelUnmarshaller() {
   }

   public PolicySourceModel unmarshalModel(Object storage) throws PolicyException {
      XMLEventReader reader = this.createXMLEventReader(storage);
      PolicySourceModel model = null;

      while(true) {
         if (reader.hasNext()) {
            try {
               XMLEvent event = reader.peek();
               switch(event.getEventType()) {
               case 1:
                  if (NamespaceVersion.resolveAsToken(event.asStartElement().getName()) != XmlToken.Policy) {
                     throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
                  }

                  StartElement rootElement = reader.nextEvent().asStartElement();
                  model = this.initializeNewModel(rootElement);
                  this.unmarshalNodeContent(model.getNamespaceVersion(), model.getRootNode(), rootElement.getName(), reader);
                  break;
               case 2:
               case 3:
               case 6:
               default:
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
               case 4:
                  this.processCharacters(ModelNode.Type.POLICY, event.asCharacters(), (StringBuilder)null);
                  reader.nextEvent();
                  continue;
               case 5:
               case 7:
                  reader.nextEvent();
                  continue;
               }
            } catch (XMLStreamException var6) {
               throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), var6));
            }
         }

         return model;
      }
   }

   protected PolicySourceModel createSourceModel(NamespaceVersion nsVersion, String id, String name) {
      return PolicySourceModel.createPolicySourceModel(nsVersion, id, name);
   }

   private PolicySourceModel initializeNewModel(StartElement element) throws PolicyException, XMLStreamException {
      NamespaceVersion nsVersion = NamespaceVersion.resolveVersion(element.getName().getNamespaceURI());
      Attribute policyName = this.getAttributeByName(element, nsVersion.asQName(XmlToken.Name));
      Attribute xmlId = this.getAttributeByName(element, PolicyConstants.XML_ID);
      Attribute policyId = this.getAttributeByName(element, PolicyConstants.WSU_ID);
      if (policyId == null) {
         policyId = xmlId;
      } else if (xmlId != null) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED()));
      }

      PolicySourceModel model = this.createSourceModel(nsVersion, policyId == null ? null : policyId.getValue(), policyName == null ? null : policyName.getValue());
      return model;
   }

   private ModelNode addNewChildNode(NamespaceVersion nsVersion, ModelNode parentNode, StartElement childElement) throws PolicyException {
      QName childElementName = childElement.getName();
      ModelNode childNode;
      if (parentNode.getType() == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
         childNode = parentNode.createChildAssertionParameterNode();
      } else {
         XmlToken token = NamespaceVersion.resolveAsToken(childElementName);
         switch(token) {
         case Policy:
            childNode = parentNode.createChildPolicyNode();
            break;
         case All:
            childNode = parentNode.createChildAllNode();
            break;
         case ExactlyOne:
            childNode = parentNode.createChildExactlyOneNode();
            break;
         case PolicyReference:
            Attribute uri = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.Uri));
            if (uri == null) {
               throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND()));
            }

            try {
               URI reference = new URI(uri.getValue());
               Attribute digest = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.Digest));
               PolicyReferenceData refData;
               if (digest == null) {
                  refData = new PolicyReferenceData(reference);
               } else {
                  Attribute digestAlgorithm = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.DigestAlgorithm));
                  URI algorithmRef = null;
                  if (digestAlgorithm != null) {
                     algorithmRef = new URI(digestAlgorithm.getValue());
                  }

                  refData = new PolicyReferenceData(reference, digest.getValue(), algorithmRef);
               }

               childNode = parentNode.createChildPolicyReferenceNode(refData);
               break;
            } catch (URISyntaxException var13) {
               throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI(), var13));
            }
         default:
            if (parentNode.isDomainSpecific()) {
               childNode = parentNode.createChildAssertionParameterNode();
            } else {
               childNode = parentNode.createChildAssertionNode();
            }
         }
      }

      return childNode;
   }

   private void parseAssertionData(NamespaceVersion nsVersion, String value, ModelNode childNode, StartElement childElement) throws IllegalArgumentException, PolicyException {
      Map<QName, String> attributeMap = new HashMap();
      boolean optional = false;
      boolean ignorable = false;
      Iterator iterator = childElement.getAttributes();

      while(iterator.hasNext()) {
         Attribute nextAttribute = (Attribute)iterator.next();
         QName name = nextAttribute.getName();
         if (attributeMap.containsKey(name)) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(nextAttribute.getName(), childElement.getName())));
         }

         if (nsVersion.asQName(XmlToken.Optional).equals(name)) {
            optional = this.parseBooleanValue(nextAttribute.getValue());
         } else if (nsVersion.asQName(XmlToken.Ignorable).equals(name)) {
            ignorable = this.parseBooleanValue(nextAttribute.getValue());
         } else {
            attributeMap.put(name, nextAttribute.getValue());
         }
      }

      AssertionData nodeData = new AssertionData(childElement.getName(), value, attributeMap, childNode.getType(), optional, ignorable);
      if (nodeData.containsAttribute(PolicyConstants.VISIBILITY_ATTRIBUTE)) {
         String visibilityValue = nodeData.getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE);
         if (!"private".equals(visibilityValue)) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(visibilityValue)));
         }
      }

      childNode.setOrReplaceNodeData(nodeData);
   }

   private Attribute getAttributeByName(StartElement element, QName attributeName) {
      Attribute attribute = element.getAttributeByName(attributeName);
      if (attribute == null) {
         String localAttributeName = attributeName.getLocalPart();
         Iterator iterator = element.getAttributes();

         while(iterator.hasNext()) {
            Attribute nextAttribute = (Attribute)iterator.next();
            QName aName = nextAttribute.getName();
            boolean attributeFoundByWorkaround = aName.equals(attributeName) || aName.getLocalPart().equals(localAttributeName) && (aName.getPrefix() == null || "".equals(aName.getPrefix()));
            if (attributeFoundByWorkaround) {
               attribute = nextAttribute;
               break;
            }
         }
      }

      return attribute;
   }

   private String unmarshalNodeContent(NamespaceVersion nsVersion, ModelNode node, QName nodeElementName, XMLEventReader reader) throws PolicyException {
      StringBuilder valueBuffer = null;

      while(true) {
         if (reader.hasNext()) {
            try {
               XMLEvent xmlParserEvent = reader.nextEvent();
               switch(xmlParserEvent.getEventType()) {
               case 1:
                  StartElement childElement = xmlParserEvent.asStartElement();
                  ModelNode childNode = this.addNewChildNode(nsVersion, node, childElement);
                  String value = this.unmarshalNodeContent(nsVersion, childNode, childElement.getName(), reader);
                  if (childNode.isDomainSpecific()) {
                     this.parseAssertionData(nsVersion, value, childNode, childElement);
                  }
                  continue;
               case 2:
                  this.checkEndTagName(nodeElementName, xmlParserEvent.asEndElement());
                  break;
               case 3:
               default:
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED()));
               case 4:
                  valueBuffer = this.processCharacters(node.getType(), xmlParserEvent.asCharacters(), valueBuffer);
               case 5:
                  continue;
               }
            } catch (XMLStreamException var10) {
               throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), var10));
            }
         }

         return valueBuffer == null ? null : valueBuffer.toString().trim();
      }
   }

   private XMLEventReader createXMLEventReader(Object storage) throws PolicyException {
      if (storage instanceof XMLEventReader) {
         return (XMLEventReader)storage;
      } else if (!(storage instanceof Reader)) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(storage.getClass().getName())));
      } else {
         try {
            return XMLInputFactory.newInstance().createXMLEventReader((Reader)storage);
         } catch (XMLStreamException var3) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE(), var3));
         }
      }
   }

   private void checkEndTagName(QName expected, EndElement element) throws PolicyException {
      QName actual = element.getName();
      if (!expected.equals(actual)) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(expected, actual)));
      }
   }

   private StringBuilder processCharacters(ModelNode.Type currentNodeType, Characters characters, StringBuilder currentValueBuffer) throws PolicyException {
      if (characters.isWhiteSpace()) {
         return currentValueBuffer;
      } else {
         StringBuilder buffer = currentValueBuffer == null ? new StringBuilder() : currentValueBuffer;
         String data = characters.getData();
         if (currentNodeType != ModelNode.Type.ASSERTION && currentNodeType != ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE(currentNodeType, data)));
         } else {
            return buffer.append(data);
         }
      }
   }

   private boolean parseBooleanValue(String value) throws PolicyException {
      if (!"true".equals(value) && !"1".equals(value)) {
         if (!"false".equals(value) && !"0".equals(value)) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0095_INVALID_BOOLEAN_VALUE(value)));
         } else {
            return false;
         }
      } else {
         return true;
      }
   }
}
