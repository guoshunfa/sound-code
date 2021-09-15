package com.sun.xml.internal.ws.policy.sourcemodel.attach;

import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelUnmarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ExternalAttachmentsUnmarshaller {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ExternalAttachmentsUnmarshaller.class);
   public static final URI BINDING_ID;
   public static final URI BINDING_OPERATION_ID;
   public static final URI BINDING_OPERATION_INPUT_ID;
   public static final URI BINDING_OPERATION_OUTPUT_ID;
   public static final URI BINDING_OPERATION_FAULT_ID;
   private static final QName POLICY_ATTACHMENT;
   private static final QName APPLIES_TO;
   private static final QName POLICY;
   private static final QName URI;
   private static final QName POLICIES;
   private static final ContextClassloaderLocal<XMLInputFactory> XML_INPUT_FACTORY;
   private static final PolicyModelUnmarshaller POLICY_UNMARSHALLER;
   private final Map<URI, Policy> map = new HashMap();
   private URI currentUri = null;
   private Policy currentPolicy = null;

   public static Map<URI, Policy> unmarshal(Reader source) throws PolicyException {
      LOGGER.entering(new Object[]{source});

      try {
         XMLEventReader reader = ((XMLInputFactory)XML_INPUT_FACTORY.get()).createXMLEventReader(source);
         ExternalAttachmentsUnmarshaller instance = new ExternalAttachmentsUnmarshaller();
         Map<URI, Policy> map = instance.unmarshal(reader, (StartElement)null);
         LOGGER.exiting(map);
         return Collections.unmodifiableMap(map);
      } catch (XMLStreamException var4) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0086_FAILED_CREATE_READER(source)), var4);
      }
   }

   private Map<URI, Policy> unmarshal(XMLEventReader reader, StartElement parentElement) throws PolicyException {
      XMLEvent event = null;

      while(reader.hasNext()) {
         try {
            event = reader.peek();
            switch(event.getEventType()) {
            case 1:
               StartElement element = event.asStartElement();
               this.processStartTag(element, parentElement, reader, this.map);
               break;
            case 2:
               this.processEndTag(event.asEndElement(), parentElement);
               reader.nextEvent();
               return this.map;
            case 3:
            case 6:
            default:
               throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0087_UNKNOWN_EVENT(event)));
            case 4:
               this.processCharacters(event.asCharacters(), parentElement, this.map);
               reader.nextEvent();
               break;
            case 5:
            case 7:
               reader.nextEvent();
               break;
            case 8:
               return this.map;
            }
         } catch (XMLStreamException var6) {
            Location location = event == null ? null : event.getLocation();
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(location)), var6);
         }
      }

      return this.map;
   }

   private void processStartTag(StartElement element, StartElement parent, XMLEventReader reader, Map<URI, Policy> map) throws PolicyException {
      try {
         QName name = element.getName();
         if (parent == null) {
            if (!name.equals(POLICIES)) {
               throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<Policies>", name, element.getLocation())));
            }
         } else {
            QName parentName = parent.getName();
            if (parentName.equals(POLICIES)) {
               if (!name.equals(POLICY_ATTACHMENT)) {
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<PolicyAttachment>", name, element.getLocation())));
               }
            } else if (parentName.equals(POLICY_ATTACHMENT)) {
               if (name.equals(POLICY)) {
                  this.readPolicy(reader);
                  return;
               }

               if (!name.equals(APPLIES_TO)) {
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<AppliesTo> or <Policy>", name, element.getLocation())));
               }
            } else {
               if (!parentName.equals(APPLIES_TO)) {
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0090_UNEXPECTED_ELEMENT(name, element.getLocation())));
               }

               if (!name.equals(URI)) {
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<URI>", name, element.getLocation())));
               }
            }
         }

         reader.nextEvent();
         this.unmarshal(reader, element);
      } catch (XMLStreamException var7) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(element.getLocation()), var7));
      }
   }

   private void readPolicy(XMLEventReader reader) throws PolicyException {
      PolicySourceModel policyModel = POLICY_UNMARSHALLER.unmarshalModel(reader);
      PolicyModelTranslator translator = PolicyModelTranslator.getTranslator();
      Policy policy = translator.translate(policyModel);
      if (this.currentUri != null) {
         this.map.put(this.currentUri, policy);
         this.currentUri = null;
         this.currentPolicy = null;
      } else {
         this.currentPolicy = policy;
      }

   }

   private void processEndTag(EndElement element, StartElement startElement) throws PolicyException {
      this.checkEndTagName(startElement.getName(), element);
   }

   private void checkEndTagName(QName expectedName, EndElement element) throws PolicyException {
      QName actualName = element.getName();
      if (!expectedName.equals(actualName)) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0091_END_ELEMENT_NO_MATCH(expectedName, element, element.getLocation())));
      }
   }

   private void processCharacters(Characters chars, StartElement currentElement, Map<URI, Policy> map) throws PolicyException {
      if (!chars.isWhiteSpace()) {
         String data = chars.getData();
         if (currentElement != null && URI.equals(currentElement.getName())) {
            this.processUri(chars, map);
         } else {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0092_CHARACTER_DATA_UNEXPECTED(currentElement, data, chars.getLocation())));
         }
      }
   }

   private void processUri(Characters chars, Map<URI, Policy> map) throws PolicyException {
      String data = chars.getData().trim();

      try {
         URI uri = new URI(data);
         if (this.currentPolicy != null) {
            map.put(uri, this.currentPolicy);
            this.currentUri = null;
            this.currentPolicy = null;
         } else {
            this.currentUri = uri;
         }

      } catch (URISyntaxException var5) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0093_INVALID_URI(data, chars.getLocation())), var5);
      }
   }

   static {
      try {
         BINDING_ID = new URI("urn:uuid:c9bef600-0d7a-11de-abc1-0002a5d5c51b");
         BINDING_OPERATION_ID = new URI("urn:uuid:62e66b60-0d7b-11de-a1a2-0002a5d5c51b");
         BINDING_OPERATION_INPUT_ID = new URI("urn:uuid:730d8d20-0d7b-11de-84e9-0002a5d5c51b");
         BINDING_OPERATION_OUTPUT_ID = new URI("urn:uuid:85b0f980-0d7b-11de-8e9d-0002a5d5c51b");
         BINDING_OPERATION_FAULT_ID = new URI("urn:uuid:917cb060-0d7b-11de-9e80-0002a5d5c51b");
      } catch (URISyntaxException var1) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0094_INVALID_URN()), var1);
      }

      POLICY_ATTACHMENT = new QName("http://www.w3.org/ns/ws-policy", "PolicyAttachment");
      APPLIES_TO = new QName("http://www.w3.org/ns/ws-policy", "AppliesTo");
      POLICY = new QName("http://www.w3.org/ns/ws-policy", "Policy");
      URI = new QName("http://www.w3.org/ns/ws-policy", "URI");
      POLICIES = new QName("http://java.sun.com/xml/ns/metro/management", "Policies");
      XML_INPUT_FACTORY = new ContextClassloaderLocal<XMLInputFactory>() {
         protected XMLInputFactory initialValue() throws Exception {
            return XMLInputFactory.newInstance();
         }
      };
      POLICY_UNMARSHALLER = PolicyModelUnmarshaller.getXmlUnmarshaller();
   }
}
