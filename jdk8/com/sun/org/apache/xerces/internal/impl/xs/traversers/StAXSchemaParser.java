package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.util.JAXPNamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.StAXLocationWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.w3c.dom.Document;

final class StAXSchemaParser {
   private static final int CHUNK_SIZE = 1024;
   private static final int CHUNK_MASK = 1023;
   private final char[] fCharBuffer = new char[1024];
   private SymbolTable fSymbolTable;
   private SchemaDOMParser fSchemaDOMParser;
   private final StAXLocationWrapper fLocationWrapper = new StAXLocationWrapper();
   private final JAXPNamespaceContextWrapper fNamespaceContext;
   private final QName fElementQName;
   private final QName fAttributeQName;
   private final XMLAttributesImpl fAttributes;
   private final XMLString fTempString;
   private final ArrayList fDeclaredPrefixes;
   private final XMLStringBuffer fStringBuffer;
   private int fDepth;

   public StAXSchemaParser() {
      this.fNamespaceContext = new JAXPNamespaceContextWrapper(this.fSymbolTable);
      this.fElementQName = new QName();
      this.fAttributeQName = new QName();
      this.fAttributes = new XMLAttributesImpl();
      this.fTempString = new XMLString();
      this.fDeclaredPrefixes = new ArrayList();
      this.fStringBuffer = new XMLStringBuffer();
      this.fNamespaceContext.setDeclaredPrefixes(this.fDeclaredPrefixes);
   }

   public void reset(SchemaDOMParser schemaDOMParser, SymbolTable symbolTable) {
      this.fSchemaDOMParser = schemaDOMParser;
      this.fSymbolTable = symbolTable;
      this.fNamespaceContext.setSymbolTable(this.fSymbolTable);
      this.fNamespaceContext.reset();
   }

   public Document getDocument() {
      return this.fSchemaDOMParser.getDocument();
   }

   public void parse(XMLEventReader input) throws XMLStreamException, XNIException {
      XMLEvent currentEvent = input.peek();
      if (currentEvent != null) {
         int eventType = currentEvent.getEventType();
         if (eventType != 7 && eventType != 1) {
            throw new XMLStreamException();
         }

         this.fLocationWrapper.setLocation(currentEvent.getLocation());
         this.fSchemaDOMParser.startDocument(this.fLocationWrapper, (String)null, this.fNamespaceContext, (Augmentations)null);

         label35:
         while(input.hasNext()) {
            currentEvent = input.nextEvent();
            eventType = currentEvent.getEventType();
            switch(eventType) {
            case 1:
               ++this.fDepth;
               StartElement start = currentEvent.asStartElement();
               this.fillQName(this.fElementQName, start.getName());
               this.fLocationWrapper.setLocation(start.getLocation());
               this.fNamespaceContext.setNamespaceContext(start.getNamespaceContext());
               this.fillXMLAttributes(start);
               this.fillDeclaredPrefixes(start);
               this.addNamespaceDeclarations();
               this.fNamespaceContext.pushContext();
               this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, (Augmentations)null);
               break;
            case 2:
               EndElement end = currentEvent.asEndElement();
               this.fillQName(this.fElementQName, end.getName());
               this.fillDeclaredPrefixes(end);
               this.fLocationWrapper.setLocation(end.getLocation());
               this.fSchemaDOMParser.endElement(this.fElementQName, (Augmentations)null);
               this.fNamespaceContext.popContext();
               --this.fDepth;
               if (this.fDepth <= 0) {
                  break label35;
               }
               break;
            case 3:
               ProcessingInstruction pi = (ProcessingInstruction)currentEvent;
               this.fillProcessingInstruction(pi.getData());
               this.fSchemaDOMParser.processingInstruction(pi.getTarget(), this.fTempString, (Augmentations)null);
               break;
            case 4:
               this.sendCharactersToSchemaParser(currentEvent.asCharacters().getData(), false);
            case 5:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               break;
            case 6:
               this.sendCharactersToSchemaParser(currentEvent.asCharacters().getData(), true);
               break;
            case 7:
               ++this.fDepth;
               break;
            case 12:
               this.fSchemaDOMParser.startCDATA((Augmentations)null);
               this.sendCharactersToSchemaParser(currentEvent.asCharacters().getData(), false);
               this.fSchemaDOMParser.endCDATA((Augmentations)null);
            }
         }

         this.fLocationWrapper.setLocation((Location)null);
         this.fNamespaceContext.setNamespaceContext((NamespaceContext)null);
         this.fSchemaDOMParser.endDocument((Augmentations)null);
      }

   }

   public void parse(XMLStreamReader input) throws XMLStreamException, XNIException {
      if (input.hasNext()) {
         int eventType = input.getEventType();
         if (eventType != 7 && eventType != 1) {
            throw new XMLStreamException();
         }

         this.fLocationWrapper.setLocation(input.getLocation());
         this.fSchemaDOMParser.startDocument(this.fLocationWrapper, (String)null, this.fNamespaceContext, (Augmentations)null);
         boolean first = true;

         label40:
         while(input.hasNext()) {
            if (!first) {
               eventType = input.next();
            } else {
               first = false;
            }

            switch(eventType) {
            case 1:
               ++this.fDepth;
               this.fLocationWrapper.setLocation(input.getLocation());
               this.fNamespaceContext.setNamespaceContext(input.getNamespaceContext());
               this.fillQName(this.fElementQName, input.getNamespaceURI(), input.getLocalName(), input.getPrefix());
               this.fillXMLAttributes(input);
               this.fillDeclaredPrefixes(input);
               this.addNamespaceDeclarations();
               this.fNamespaceContext.pushContext();
               this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, (Augmentations)null);
               break;
            case 2:
               this.fLocationWrapper.setLocation(input.getLocation());
               this.fNamespaceContext.setNamespaceContext(input.getNamespaceContext());
               this.fillQName(this.fElementQName, input.getNamespaceURI(), input.getLocalName(), input.getPrefix());
               this.fillDeclaredPrefixes(input);
               this.fSchemaDOMParser.endElement(this.fElementQName, (Augmentations)null);
               this.fNamespaceContext.popContext();
               --this.fDepth;
               if (this.fDepth <= 0) {
                  break label40;
               }
               break;
            case 3:
               this.fillProcessingInstruction(input.getPIData());
               this.fSchemaDOMParser.processingInstruction(input.getPITarget(), this.fTempString, (Augmentations)null);
               break;
            case 4:
               this.fTempString.setValues(input.getTextCharacters(), input.getTextStart(), input.getTextLength());
               this.fSchemaDOMParser.characters(this.fTempString, (Augmentations)null);
            case 5:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               break;
            case 6:
               this.fTempString.setValues(input.getTextCharacters(), input.getTextStart(), input.getTextLength());
               this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, (Augmentations)null);
               break;
            case 7:
               ++this.fDepth;
               break;
            case 12:
               this.fSchemaDOMParser.startCDATA((Augmentations)null);
               this.fTempString.setValues(input.getTextCharacters(), input.getTextStart(), input.getTextLength());
               this.fSchemaDOMParser.characters(this.fTempString, (Augmentations)null);
               this.fSchemaDOMParser.endCDATA((Augmentations)null);
            }
         }

         this.fLocationWrapper.setLocation((Location)null);
         this.fNamespaceContext.setNamespaceContext((NamespaceContext)null);
         this.fSchemaDOMParser.endDocument((Augmentations)null);
      }

   }

   private void sendCharactersToSchemaParser(String str, boolean whitespace) {
      if (str != null) {
         int length = str.length();
         int remainder = length & 1023;
         if (remainder > 0) {
            str.getChars(0, remainder, this.fCharBuffer, 0);
            this.fTempString.setValues(this.fCharBuffer, 0, remainder);
            if (whitespace) {
               this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, (Augmentations)null);
            } else {
               this.fSchemaDOMParser.characters(this.fTempString, (Augmentations)null);
            }
         }

         int i = remainder;

         while(i < length) {
            int var10001 = i;
            i += 1024;
            str.getChars(var10001, i, this.fCharBuffer, 0);
            this.fTempString.setValues(this.fCharBuffer, 0, 1024);
            if (whitespace) {
               this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, (Augmentations)null);
            } else {
               this.fSchemaDOMParser.characters(this.fTempString, (Augmentations)null);
            }
         }
      }

   }

   private void fillProcessingInstruction(String data) {
      int dataLength = data.length();
      char[] charBuffer = this.fCharBuffer;
      if (charBuffer.length < dataLength) {
         charBuffer = data.toCharArray();
      } else {
         data.getChars(0, dataLength, charBuffer, 0);
      }

      this.fTempString.setValues(charBuffer, 0, dataLength);
   }

   private void fillXMLAttributes(StartElement event) {
      this.fAttributes.removeAllAttributes();
      Iterator attrs = event.getAttributes();

      while(attrs.hasNext()) {
         Attribute attr = (Attribute)attrs.next();
         this.fillQName(this.fAttributeQName, attr.getName());
         String type = attr.getDTDType();
         int idx = this.fAttributes.getLength();
         this.fAttributes.addAttributeNS(this.fAttributeQName, type != null ? type : XMLSymbols.fCDATASymbol, attr.getValue());
         this.fAttributes.setSpecified(idx, attr.isSpecified());
      }

   }

   private void fillXMLAttributes(XMLStreamReader input) {
      this.fAttributes.removeAllAttributes();
      int len = input.getAttributeCount();

      for(int i = 0; i < len; ++i) {
         this.fillQName(this.fAttributeQName, input.getAttributeNamespace(i), input.getAttributeLocalName(i), input.getAttributePrefix(i));
         String type = input.getAttributeType(i);
         this.fAttributes.addAttributeNS(this.fAttributeQName, type != null ? type : XMLSymbols.fCDATASymbol, input.getAttributeValue(i));
         this.fAttributes.setSpecified(i, input.isAttributeSpecified(i));
      }

   }

   private void addNamespaceDeclarations() {
      String prefix = null;
      String localpart = null;
      String rawname = null;
      String nsPrefix = null;
      String nsURI = null;
      Iterator iter = this.fDeclaredPrefixes.iterator();

      while(iter.hasNext()) {
         nsPrefix = (String)iter.next();
         nsURI = this.fNamespaceContext.getURI(nsPrefix);
         if (nsPrefix.length() > 0) {
            prefix = XMLSymbols.PREFIX_XMLNS;
            localpart = nsPrefix;
            this.fStringBuffer.clear();
            this.fStringBuffer.append(prefix);
            this.fStringBuffer.append(':');
            this.fStringBuffer.append(nsPrefix);
            rawname = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
         } else {
            prefix = XMLSymbols.EMPTY_STRING;
            localpart = XMLSymbols.PREFIX_XMLNS;
            rawname = XMLSymbols.PREFIX_XMLNS;
         }

         this.fAttributeQName.setValues(prefix, localpart, rawname, com.sun.org.apache.xerces.internal.xni.NamespaceContext.XMLNS_URI);
         this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, nsURI != null ? nsURI : XMLSymbols.EMPTY_STRING);
      }

   }

   private void fillDeclaredPrefixes(StartElement event) {
      this.fillDeclaredPrefixes(event.getNamespaces());
   }

   private void fillDeclaredPrefixes(EndElement event) {
      this.fillDeclaredPrefixes(event.getNamespaces());
   }

   private void fillDeclaredPrefixes(Iterator namespaces) {
      this.fDeclaredPrefixes.clear();

      while(namespaces.hasNext()) {
         Namespace ns = (Namespace)namespaces.next();
         String prefix = ns.getPrefix();
         this.fDeclaredPrefixes.add(prefix != null ? prefix : "");
      }

   }

   private void fillDeclaredPrefixes(XMLStreamReader reader) {
      this.fDeclaredPrefixes.clear();
      int len = reader.getNamespaceCount();

      for(int i = 0; i < len; ++i) {
         String prefix = reader.getNamespacePrefix(i);
         this.fDeclaredPrefixes.add(prefix != null ? prefix : "");
      }

   }

   private void fillQName(QName toFill, javax.xml.namespace.QName toCopy) {
      this.fillQName(toFill, toCopy.getNamespaceURI(), toCopy.getLocalPart(), toCopy.getPrefix());
   }

   final void fillQName(QName toFill, String uri, String localpart, String prefix) {
      uri = uri != null && uri.length() > 0 ? this.fSymbolTable.addSymbol(uri) : null;
      localpart = localpart != null ? this.fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING;
      prefix = prefix != null && prefix.length() > 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
      String raw = localpart;
      if (prefix != XMLSymbols.EMPTY_STRING) {
         this.fStringBuffer.clear();
         this.fStringBuffer.append(prefix);
         this.fStringBuffer.append(':');
         this.fStringBuffer.append(localpart);
         raw = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
      }

      toFill.setValues(prefix, localpart, raw, uri);
   }
}
