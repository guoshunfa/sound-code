package com.sun.xml.internal.stream.buffer.sax;

import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

public class SAXBufferProcessor extends AbstractProcessor implements XMLReader {
   protected EntityResolver _entityResolver;
   protected DTDHandler _dtdHandler;
   protected ContentHandler _contentHandler;
   protected ErrorHandler _errorHandler;
   protected LexicalHandler _lexicalHandler;
   protected boolean _namespacePrefixesFeature;
   protected AttributesHolder _attributes;
   protected String[] _namespacePrefixes;
   protected int _namespacePrefixesIndex;
   protected int[] _namespaceAttributesStartingStack;
   protected int[] _namespaceAttributesStack;
   protected int _namespaceAttributesStackIndex;
   private static final DefaultWithLexicalHandler DEFAULT_LEXICAL_HANDLER = new DefaultWithLexicalHandler();

   public SAXBufferProcessor() {
      this._entityResolver = DEFAULT_LEXICAL_HANDLER;
      this._dtdHandler = DEFAULT_LEXICAL_HANDLER;
      this._contentHandler = DEFAULT_LEXICAL_HANDLER;
      this._errorHandler = DEFAULT_LEXICAL_HANDLER;
      this._lexicalHandler = DEFAULT_LEXICAL_HANDLER;
      this._namespacePrefixesFeature = false;
      this._attributes = new AttributesHolder();
      this._namespacePrefixes = new String[16];
      this._namespaceAttributesStartingStack = new int[16];
      this._namespaceAttributesStack = new int[16];
   }

   /** @deprecated */
   public SAXBufferProcessor(XMLStreamBuffer buffer) {
      this._entityResolver = DEFAULT_LEXICAL_HANDLER;
      this._dtdHandler = DEFAULT_LEXICAL_HANDLER;
      this._contentHandler = DEFAULT_LEXICAL_HANDLER;
      this._errorHandler = DEFAULT_LEXICAL_HANDLER;
      this._lexicalHandler = DEFAULT_LEXICAL_HANDLER;
      this._namespacePrefixesFeature = false;
      this._attributes = new AttributesHolder();
      this._namespacePrefixes = new String[16];
      this._namespaceAttributesStartingStack = new int[16];
      this._namespaceAttributesStack = new int[16];
      this.setXMLStreamBuffer(buffer);
   }

   public SAXBufferProcessor(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
      this._entityResolver = DEFAULT_LEXICAL_HANDLER;
      this._dtdHandler = DEFAULT_LEXICAL_HANDLER;
      this._contentHandler = DEFAULT_LEXICAL_HANDLER;
      this._errorHandler = DEFAULT_LEXICAL_HANDLER;
      this._lexicalHandler = DEFAULT_LEXICAL_HANDLER;
      this._namespacePrefixesFeature = false;
      this._attributes = new AttributesHolder();
      this._namespacePrefixes = new String[16];
      this._namespaceAttributesStartingStack = new int[16];
      this._namespaceAttributesStack = new int[16];
      this.setXMLStreamBuffer(buffer, produceFragmentEvent);
   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/features/namespaces")) {
         return true;
      } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
         return this._namespacePrefixesFeature;
      } else if (name.equals("http://xml.org/sax/features/external-general-entities")) {
         return true;
      } else if (name.equals("http://xml.org/sax/features/external-parameter-entities")) {
         return true;
      } else if (name.equals("http://xml.org/sax/features/string-interning")) {
         return this._stringInterningFeature;
      } else {
         throw new SAXNotRecognizedException("Feature not supported: " + name);
      }
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/features/namespaces")) {
         if (!value) {
            throw new SAXNotSupportedException(name + ":" + value);
         }
      } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
         this._namespacePrefixesFeature = value;
      } else if (!name.equals("http://xml.org/sax/features/external-general-entities") && !name.equals("http://xml.org/sax/features/external-parameter-entities")) {
         if (!name.equals("http://xml.org/sax/features/string-interning")) {
            throw new SAXNotRecognizedException("Feature not supported: " + name);
         }

         if (value != this._stringInterningFeature) {
            throw new SAXNotSupportedException(name + ":" + value);
         }
      }

   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
         return this.getLexicalHandler();
      } else {
         throw new SAXNotRecognizedException("Property not recognized: " + name);
      }
   }

   public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
         if (value instanceof LexicalHandler) {
            this.setLexicalHandler((LexicalHandler)value);
         } else {
            throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
         }
      } else {
         throw new SAXNotRecognizedException("Property not recognized: " + name);
      }
   }

   public void setEntityResolver(EntityResolver resolver) {
      this._entityResolver = resolver;
   }

   public EntityResolver getEntityResolver() {
      return this._entityResolver;
   }

   public void setDTDHandler(DTDHandler handler) {
      this._dtdHandler = handler;
   }

   public DTDHandler getDTDHandler() {
      return this._dtdHandler;
   }

   public void setContentHandler(ContentHandler handler) {
      this._contentHandler = handler;
   }

   public ContentHandler getContentHandler() {
      return this._contentHandler;
   }

   public void setErrorHandler(ErrorHandler handler) {
      this._errorHandler = handler;
   }

   public ErrorHandler getErrorHandler() {
      return this._errorHandler;
   }

   public void setLexicalHandler(LexicalHandler handler) {
      this._lexicalHandler = handler;
   }

   public LexicalHandler getLexicalHandler() {
      return this._lexicalHandler;
   }

   public void parse(InputSource input) throws IOException, SAXException {
      this.process();
   }

   public void parse(String systemId) throws IOException, SAXException {
      this.process();
   }

   /** @deprecated */
   public final void process(XMLStreamBuffer buffer) throws SAXException {
      this.setXMLStreamBuffer(buffer);
      this.process();
   }

   public final void process(XMLStreamBuffer buffer, boolean produceFragmentEvent) throws SAXException {
      this.setXMLStreamBuffer(buffer);
      this.process();
   }

   /** @deprecated */
   public void setXMLStreamBuffer(XMLStreamBuffer buffer) {
      this.setBuffer(buffer);
   }

   public void setXMLStreamBuffer(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
      if (!produceFragmentEvent && this._treeCount > 1) {
         throw new IllegalStateException("Can't write a forest to a full XML infoset");
      } else {
         this.setBuffer(buffer, produceFragmentEvent);
      }
   }

   public final void process() throws SAXException {
      if (!this._fragmentMode) {
         LocatorImpl nullLocator = new LocatorImpl();
         nullLocator.setSystemId(this._buffer.getSystemId());
         nullLocator.setLineNumber(-1);
         nullLocator.setColumnNumber(-1);
         this._contentHandler.setDocumentLocator(nullLocator);
         this._contentHandler.startDocument();
      }

      while(this._treeCount > 0) {
         int item = this.readEiiState();
         String localName;
         String localName;
         switch(item) {
         case 1:
            this.processDocument();
            --this._treeCount;
            break;
         case 2:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            throw this.reportFatalError("Illegal state for DIIs: " + item);
         case 3:
            this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope());
            --this._treeCount;
            break;
         case 4:
            localName = this.readStructureString();
            localName = this.readStructureString();
            String localName = this.readStructureString();
            this.processElement(localName, localName, this.getQName(localName, localName), this.isInscope());
            --this._treeCount;
            break;
         case 5:
            localName = this.readStructureString();
            localName = this.readStructureString();
            this.processElement(localName, localName, localName, this.isInscope());
            --this._treeCount;
            break;
         case 6:
            localName = this.readStructureString();
            this.processElement("", localName, localName, this.isInscope());
            --this._treeCount;
            break;
         case 12:
            this.processCommentAsCharArraySmall();
            break;
         case 13:
            this.processCommentAsCharArrayMedium();
            break;
         case 14:
            this.processCommentAsCharArrayCopy();
            break;
         case 15:
            this.processComment(this.readContentString());
            break;
         case 16:
            this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
            break;
         case 17:
            return;
         }
      }

      if (!this._fragmentMode) {
         this._contentHandler.endDocument();
      }

   }

   private void processCommentAsCharArraySmall() throws SAXException {
      int length = this.readStructure();
      int start = this.readContentCharactersBuffer(length);
      this.processComment(this._contentCharactersBuffer, start, length);
   }

   private SAXParseException reportFatalError(String msg) throws SAXException {
      SAXParseException spe = new SAXParseException(msg, (Locator)null);
      if (this._errorHandler != null) {
         this._errorHandler.fatalError(spe);
      }

      return spe;
   }

   private boolean isInscope() {
      return this._buffer.getInscopeNamespaces().size() > 0;
   }

   private void processDocument() throws SAXException {
      while(true) {
         int item = this.readEiiState();
         String localName;
         String localName;
         switch(item) {
         case 3:
            this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope());
            break;
         case 4:
            localName = this.readStructureString();
            localName = this.readStructureString();
            String localName = this.readStructureString();
            this.processElement(localName, localName, this.getQName(localName, localName), this.isInscope());
            break;
         case 5:
            localName = this.readStructureString();
            localName = this.readStructureString();
            this.processElement(localName, localName, localName, this.isInscope());
            break;
         case 6:
            localName = this.readStructureString();
            this.processElement("", localName, localName, this.isInscope());
            break;
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            throw this.reportFatalError("Illegal state for child of DII: " + item);
         case 12:
            this.processCommentAsCharArraySmall();
            break;
         case 13:
            this.processCommentAsCharArrayMedium();
            break;
         case 14:
            this.processCommentAsCharArrayCopy();
            break;
         case 15:
            this.processComment(this.readContentString());
            break;
         case 16:
            this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
            break;
         case 17:
            return;
         }
      }
   }

   protected void processElement(String uri, String localName, String qName, boolean inscope) throws SAXException {
      boolean hasAttributes = false;
      boolean hasNamespaceAttributes = false;
      int item = this.peekStructure();
      Set<String> prefixSet = inscope ? new HashSet() : Collections.emptySet();
      if ((item & 240) == 64) {
         this.cacheNamespacePrefixStartingIndex();
         hasNamespaceAttributes = true;
         item = this.processNamespaceAttributes(item, inscope, (Set)prefixSet);
      }

      if (inscope) {
         this.readInscopeNamespaces((Set)prefixSet);
      }

      if ((item & 240) == 48) {
         hasAttributes = true;
         this.processAttributes(item);
      }

      this._contentHandler.startElement(uri, localName, qName, this._attributes);
      if (hasAttributes) {
         this._attributes.clear();
      }

      do {
         item = this.readEiiState();
         String s;
         String s;
         int length;
         int start;
         switch(item) {
         case 3:
            this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), false);
            break;
         case 4:
            s = this.readStructureString();
            s = this.readStructureString();
            String ln = this.readStructureString();
            this.processElement(s, ln, this.getQName(s, ln), false);
            break;
         case 5:
            s = this.readStructureString();
            s = this.readStructureString();
            this.processElement(s, s, s, false);
            break;
         case 6:
            s = this.readStructureString();
            this.processElement("", s, s, false);
            break;
         case 7:
            length = this.readStructure();
            start = this.readContentCharactersBuffer(length);
            this._contentHandler.characters(this._contentCharactersBuffer, start, length);
            break;
         case 8:
            length = this.readStructure16();
            start = this.readContentCharactersBuffer(length);
            this._contentHandler.characters(this._contentCharactersBuffer, start, length);
            break;
         case 9:
            char[] ch = this.readContentCharactersCopy();
            this._contentHandler.characters(ch, 0, ch.length);
            break;
         case 10:
            s = this.readContentString();
            this._contentHandler.characters(s.toCharArray(), 0, s.length());
            break;
         case 11:
            CharSequence c = (CharSequence)this.readContentObject();
            s = c.toString();
            this._contentHandler.characters(s.toCharArray(), 0, s.length());
            break;
         case 12:
            this.processCommentAsCharArraySmall();
            break;
         case 13:
            this.processCommentAsCharArrayMedium();
            break;
         case 14:
            this.processCommentAsCharArrayCopy();
            break;
         case 16:
            this.processProcessingInstruction(this.readStructureString(), this.readStructureString());
         case 17:
            break;
         case 104:
            this.processComment(this.readContentString());
            break;
         default:
            throw this.reportFatalError("Illegal state for child of EII: " + item);
         }
      } while(item != 17);

      this._contentHandler.endElement(uri, localName, qName);
      if (hasNamespaceAttributes) {
         this.processEndPrefixMapping();
      }

   }

   private void readInscopeNamespaces(Set<String> prefixSet) throws SAXException {
      Iterator var2 = this._buffer.getInscopeNamespaces().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, String> e = (Map.Entry)var2.next();
         String key = fixNull((String)e.getKey());
         if (!prefixSet.contains(key)) {
            this.processNamespaceAttribute(key, (String)e.getValue());
         }
      }

   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   private void processCommentAsCharArrayCopy() throws SAXException {
      char[] ch = this.readContentCharactersCopy();
      this.processComment(ch, 0, ch.length);
   }

   private void processCommentAsCharArrayMedium() throws SAXException {
      int length = this.readStructure16();
      int start = this.readContentCharactersBuffer(length);
      this.processComment(this._contentCharactersBuffer, start, length);
   }

   private void processEndPrefixMapping() throws SAXException {
      int end = this._namespaceAttributesStack[--this._namespaceAttributesStackIndex];
      int start = this._namespaceAttributesStackIndex >= 0 ? this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] : 0;

      for(int i = end - 1; i >= start; --i) {
         this._contentHandler.endPrefixMapping(this._namespacePrefixes[i]);
      }

      this._namespacePrefixesIndex = start;
   }

   private int processNamespaceAttributes(int item, boolean collectPrefixes, Set<String> prefixSet) throws SAXException {
      do {
         String prefix;
         switch(getNIIState(item)) {
         case 1:
            this.processNamespaceAttribute("", "");
            if (collectPrefixes) {
               prefixSet.add("");
            }
            break;
         case 2:
            prefix = this.readStructureString();
            this.processNamespaceAttribute(prefix, "");
            if (collectPrefixes) {
               prefixSet.add(prefix);
            }
            break;
         case 3:
            prefix = this.readStructureString();
            this.processNamespaceAttribute(prefix, this.readStructureString());
            if (collectPrefixes) {
               prefixSet.add(prefix);
            }
            break;
         case 4:
            this.processNamespaceAttribute("", this.readStructureString());
            if (collectPrefixes) {
               prefixSet.add("");
            }
            break;
         default:
            throw this.reportFatalError("Illegal state: " + item);
         }

         this.readStructure();
         item = this.peekStructure();
      } while((item & 240) == 64);

      this.cacheNamespacePrefixIndex();
      return item;
   }

   private void processAttributes(int item) throws SAXException {
      do {
         String ln;
         String ln;
         switch(getAIIState(item)) {
         case 1:
            this._attributes.addAttributeWithQName(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
            break;
         case 2:
            ln = this.readStructureString();
            ln = this.readStructureString();
            String ln = this.readStructureString();
            this._attributes.addAttributeWithQName(ln, ln, this.getQName(ln, ln), this.readStructureString(), this.readContentString());
            break;
         case 3:
            ln = this.readStructureString();
            ln = this.readStructureString();
            this._attributes.addAttributeWithQName(ln, ln, ln, this.readStructureString(), this.readContentString());
            break;
         case 4:
            ln = this.readStructureString();
            this._attributes.addAttributeWithQName("", ln, ln, this.readStructureString(), this.readContentString());
            break;
         default:
            throw this.reportFatalError("Illegal state: " + item);
         }

         this.readStructure();
         item = this.peekStructure();
      } while((item & 240) == 48);

   }

   private void processNamespaceAttribute(String prefix, String uri) throws SAXException {
      this._contentHandler.startPrefixMapping(prefix, uri);
      if (this._namespacePrefixesFeature) {
         if (prefix != "") {
            this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", prefix, this.getQName("xmlns", prefix), "CDATA", uri);
         } else {
            this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA", uri);
         }
      }

      this.cacheNamespacePrefix(prefix);
   }

   private void cacheNamespacePrefix(String prefix) {
      if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
         String[] namespaceAttributes = new String[this._namespacePrefixesIndex * 3 / 2 + 1];
         System.arraycopy(this._namespacePrefixes, 0, namespaceAttributes, 0, this._namespacePrefixesIndex);
         this._namespacePrefixes = namespaceAttributes;
      }

      this._namespacePrefixes[this._namespacePrefixesIndex++] = prefix;
   }

   private void cacheNamespacePrefixIndex() {
      if (this._namespaceAttributesStackIndex == this._namespaceAttributesStack.length) {
         int[] namespaceAttributesStack = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
         System.arraycopy(this._namespaceAttributesStack, 0, namespaceAttributesStack, 0, this._namespaceAttributesStackIndex);
         this._namespaceAttributesStack = namespaceAttributesStack;
      }

      this._namespaceAttributesStack[this._namespaceAttributesStackIndex++] = this._namespacePrefixesIndex;
   }

   private void cacheNamespacePrefixStartingIndex() {
      if (this._namespaceAttributesStackIndex == this._namespaceAttributesStartingStack.length) {
         int[] namespaceAttributesStart = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
         System.arraycopy(this._namespaceAttributesStartingStack, 0, namespaceAttributesStart, 0, this._namespaceAttributesStackIndex);
         this._namespaceAttributesStartingStack = namespaceAttributesStart;
      }

      this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] = this._namespacePrefixesIndex;
   }

   private void processComment(String s) throws SAXException {
      this.processComment(s.toCharArray(), 0, s.length());
   }

   private void processComment(char[] ch, int start, int length) throws SAXException {
      this._lexicalHandler.comment(ch, start, length);
   }

   private void processProcessingInstruction(String target, String data) throws SAXException {
      this._contentHandler.processingInstruction(target, data);
   }
}
