package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StAXEventWriter implements XMLEventWriter {
   private XMLStreamWriter _streamWriter;

   public StAXEventWriter(XMLStreamWriter streamWriter) {
      this._streamWriter = streamWriter;
   }

   public void flush() throws XMLStreamException {
      this._streamWriter.flush();
   }

   public void close() throws XMLStreamException {
      this._streamWriter.close();
   }

   public void add(XMLEventReader eventReader) throws XMLStreamException {
      if (eventReader == null) {
         throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullEventReader"));
      } else {
         while(eventReader.hasNext()) {
            this.add(eventReader.nextEvent());
         }

      }
   }

   public void add(XMLEvent event) throws XMLStreamException {
      int type = event.getEventType();
      QName qname;
      Characters characters;
      switch(type) {
      case 1:
         StartElement startElement = event.asStartElement();
         qname = startElement.getName();
         this._streamWriter.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
         Iterator iterator = startElement.getNamespaces();

         while(iterator.hasNext()) {
            Namespace namespace = (Namespace)iterator.next();
            this._streamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
         }

         Iterator attributes = startElement.getAttributes();

         while(attributes.hasNext()) {
            Attribute attribute = (Attribute)attributes.next();
            QName name = attribute.getName();
            this._streamWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
         }

         return;
      case 2:
         this._streamWriter.writeEndElement();
         break;
      case 3:
         ProcessingInstruction processingInstruction = (ProcessingInstruction)event;
         this._streamWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
         break;
      case 4:
         characters = event.asCharacters();
         if (characters.isCData()) {
            this._streamWriter.writeCData(characters.getData());
         } else {
            this._streamWriter.writeCharacters(characters.getData());
         }
         break;
      case 5:
         Comment comment = (Comment)event;
         this._streamWriter.writeComment(comment.getText());
         break;
      case 6:
      default:
         throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotSupported", new Object[]{Util.getEventTypeString(type)}));
      case 7:
         StartDocument startDocument = (StartDocument)event;
         this._streamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
         break;
      case 8:
         this._streamWriter.writeEndDocument();
         break;
      case 9:
         EntityReference entityReference = (EntityReference)event;
         this._streamWriter.writeEntityRef(entityReference.getName());
         break;
      case 10:
         Attribute attribute = (Attribute)event;
         qname = attribute.getName();
         this._streamWriter.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), attribute.getValue());
         break;
      case 11:
         DTD dtd = (DTD)event;
         this._streamWriter.writeDTD(dtd.getDocumentTypeDeclaration());
         break;
      case 12:
         characters = (Characters)event;
         if (characters.isCData()) {
            this._streamWriter.writeCData(characters.getData());
         }
         break;
      case 13:
         Namespace namespace = (Namespace)event;
         this._streamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
      }

   }

   public String getPrefix(String uri) throws XMLStreamException {
      return this._streamWriter.getPrefix(uri);
   }

   public NamespaceContext getNamespaceContext() {
      return this._streamWriter.getNamespaceContext();
   }

   public void setDefaultNamespace(String uri) throws XMLStreamException {
      this._streamWriter.setDefaultNamespace(uri);
   }

   public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
      this._streamWriter.setNamespaceContext(namespaceContext);
   }

   public void setPrefix(String prefix, String uri) throws XMLStreamException {
      this._streamWriter.setPrefix(prefix, uri);
   }
}
