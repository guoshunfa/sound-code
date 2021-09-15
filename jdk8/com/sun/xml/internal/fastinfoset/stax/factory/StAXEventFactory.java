package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.stax.events.AttributeBase;
import com.sun.xml.internal.fastinfoset.stax.events.CharactersEvent;
import com.sun.xml.internal.fastinfoset.stax.events.CommentEvent;
import com.sun.xml.internal.fastinfoset.stax.events.DTDEvent;
import com.sun.xml.internal.fastinfoset.stax.events.EndDocumentEvent;
import com.sun.xml.internal.fastinfoset.stax.events.EndElementEvent;
import com.sun.xml.internal.fastinfoset.stax.events.EntityReferenceEvent;
import com.sun.xml.internal.fastinfoset.stax.events.NamespaceBase;
import com.sun.xml.internal.fastinfoset.stax.events.ProcessingInstructionEvent;
import com.sun.xml.internal.fastinfoset.stax.events.StartDocumentEvent;
import com.sun.xml.internal.fastinfoset.stax.events.StartElementEvent;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

public class StAXEventFactory extends XMLEventFactory {
   Location location = null;

   public void setLocation(Location location) {
      this.location = location;
   }

   public Attribute createAttribute(String prefix, String namespaceURI, String localName, String value) {
      AttributeBase attr = new AttributeBase(prefix, namespaceURI, localName, value, (String)null);
      if (this.location != null) {
         attr.setLocation(this.location);
      }

      return attr;
   }

   public Attribute createAttribute(String localName, String value) {
      AttributeBase attr = new AttributeBase(localName, value);
      if (this.location != null) {
         attr.setLocation(this.location);
      }

      return attr;
   }

   public Attribute createAttribute(QName name, String value) {
      AttributeBase attr = new AttributeBase(name, value);
      if (this.location != null) {
         attr.setLocation(this.location);
      }

      return attr;
   }

   public Namespace createNamespace(String namespaceURI) {
      NamespaceBase event = new NamespaceBase(namespaceURI);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public Namespace createNamespace(String prefix, String namespaceURI) {
      NamespaceBase event = new NamespaceBase(prefix, namespaceURI);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public StartElement createStartElement(QName name, Iterator attributes, Iterator namespaces) {
      return this.createStartElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attributes, namespaces);
   }

   public StartElement createStartElement(String prefix, String namespaceUri, String localName) {
      StartElementEvent event = new StartElementEvent(prefix, namespaceUri, localName);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces) {
      return this.createStartElement(prefix, namespaceUri, localName, attributes, namespaces, (NamespaceContext)null);
   }

   public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces, NamespaceContext context) {
      StartElementEvent elem = new StartElementEvent(prefix, namespaceUri, localName);
      elem.addAttributes(attributes);
      elem.addNamespaces(namespaces);
      elem.setNamespaceContext(context);
      if (this.location != null) {
         elem.setLocation(this.location);
      }

      return elem;
   }

   public EndElement createEndElement(QName name, Iterator namespaces) {
      return this.createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), namespaces);
   }

   public EndElement createEndElement(String prefix, String namespaceUri, String localName) {
      EndElementEvent event = new EndElementEvent(prefix, namespaceUri, localName);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public EndElement createEndElement(String prefix, String namespaceUri, String localName, Iterator namespaces) {
      EndElementEvent event = new EndElementEvent(prefix, namespaceUri, localName);
      if (namespaces != null) {
         while(namespaces.hasNext()) {
            event.addNamespace((Namespace)namespaces.next());
         }
      }

      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public Characters createCharacters(String content) {
      CharactersEvent charEvent = new CharactersEvent(content);
      if (this.location != null) {
         charEvent.setLocation(this.location);
      }

      return charEvent;
   }

   public Characters createCData(String content) {
      CharactersEvent charEvent = new CharactersEvent(content, true);
      if (this.location != null) {
         charEvent.setLocation(this.location);
      }

      return charEvent;
   }

   public Characters createSpace(String content) {
      CharactersEvent event = new CharactersEvent(content);
      event.setSpace(true);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public Characters createIgnorableSpace(String content) {
      CharactersEvent event = new CharactersEvent(content, false);
      event.setSpace(true);
      event.setIgnorable(true);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public StartDocument createStartDocument() {
      StartDocumentEvent event = new StartDocumentEvent();
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public StartDocument createStartDocument(String encoding) {
      StartDocumentEvent event = new StartDocumentEvent(encoding);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public StartDocument createStartDocument(String encoding, String version) {
      StartDocumentEvent event = new StartDocumentEvent(encoding, version);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public StartDocument createStartDocument(String encoding, String version, boolean standalone) {
      StartDocumentEvent event = new StartDocumentEvent(encoding, version);
      event.setStandalone(standalone);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public EndDocument createEndDocument() {
      EndDocumentEvent event = new EndDocumentEvent();
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public EntityReference createEntityReference(String name, EntityDeclaration entityDeclaration) {
      EntityReferenceEvent event = new EntityReferenceEvent(name, entityDeclaration);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }

   public Comment createComment(String text) {
      CommentEvent charEvent = new CommentEvent(text);
      if (this.location != null) {
         charEvent.setLocation(this.location);
      }

      return charEvent;
   }

   public DTD createDTD(String dtd) {
      DTDEvent dtdEvent = new DTDEvent(dtd);
      if (this.location != null) {
         dtdEvent.setLocation(this.location);
      }

      return dtdEvent;
   }

   public ProcessingInstruction createProcessingInstruction(String target, String data) {
      ProcessingInstructionEvent event = new ProcessingInstructionEvent(target, data);
      if (this.location != null) {
         event.setLocation(this.location);
      }

      return event;
   }
}
