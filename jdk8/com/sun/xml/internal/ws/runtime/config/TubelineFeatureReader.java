package com.sun.xml.internal.ws.runtime.config;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.config.metro.dev.FeatureReader;
import com.sun.xml.internal.ws.config.metro.util.ParserUtil;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.ws.WebServiceException;

public class TubelineFeatureReader implements FeatureReader {
   private static final Logger LOGGER = Logger.getLogger(TubelineFeatureReader.class);
   private static final QName NAME_ATTRIBUTE_NAME = new QName("name");

   public TubelineFeature parse(XMLEventReader reader) throws WebServiceException {
      try {
         StartElement element = reader.nextEvent().asStartElement();
         boolean attributeEnabled = true;
         Iterator iterator = element.getAttributes();

         while(iterator.hasNext()) {
            Attribute nextAttribute = (Attribute)iterator.next();
            QName attributeName = nextAttribute.getName();
            if (ENABLED_ATTRIBUTE_NAME.equals(attributeName)) {
               attributeEnabled = ParserUtil.parseBooleanValue(nextAttribute.getValue());
            } else if (!NAME_ATTRIBUTE_NAME.equals(attributeName)) {
               throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Unexpected attribute"));
            }
         }

         return this.parseFactories(attributeEnabled, element, reader);
      } catch (XMLStreamException var7) {
         throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", var7));
      }
   }

   private TubelineFeature parseFactories(boolean enabled, StartElement element, XMLEventReader reader) throws WebServiceException {
      int elementRead = 0;

      while(true) {
         if (reader.hasNext()) {
            try {
               XMLEvent event = reader.nextEvent();
               switch(event.getEventType()) {
               case 1:
                  ++elementRead;
                  continue;
               case 2:
                  --elementRead;
                  if (elementRead >= 0) {
                     continue;
                  }

                  EndElement endElement = event.asEndElement();
                  if (!element.getName().equals(endElement.getName())) {
                     throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("End element does not match " + endElement));
                  }
                  break;
               case 3:
               default:
                  throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Unexpected event, was " + event));
               case 4:
                  if (!event.asCharacters().isWhiteSpace()) {
                     throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("No character data allowed, was " + event.asCharacters()));
                  }
               case 5:
                  continue;
               }
            } catch (XMLStreamException var7) {
               throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", var7));
            }
         }

         return new TubelineFeature(enabled);
      }
   }
}
