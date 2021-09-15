package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public abstract class XMLStreamBuffer {
   protected Map<String, String> _inscopeNamespaces = Collections.emptyMap();
   protected boolean _hasInternedStrings;
   protected FragmentedArray<byte[]> _structure;
   protected int _structurePtr;
   protected FragmentedArray<String[]> _structureStrings;
   protected int _structureStringsPtr;
   protected FragmentedArray<char[]> _contentCharactersBuffer;
   protected int _contentCharactersBufferPtr;
   protected FragmentedArray<Object[]> _contentObjects;
   protected int _contentObjectsPtr;
   protected int treeCount;
   protected String systemId;
   private static final ContextClassloaderLocal<TransformerFactory> trnsformerFactory = new ContextClassloaderLocal<TransformerFactory>() {
      protected TransformerFactory initialValue() throws Exception {
         return TransformerFactory.newInstance();
      }
   };

   public final boolean isCreated() {
      return ((byte[])this._structure.getArray())[0] != 144;
   }

   public final boolean isFragment() {
      return this.isCreated() && (((byte[])this._structure.getArray())[this._structurePtr] & 240) != 16;
   }

   public final boolean isElementFragment() {
      return this.isCreated() && (((byte[])this._structure.getArray())[this._structurePtr] & 240) == 32;
   }

   public final boolean isForest() {
      return this.isCreated() && this.treeCount > 1;
   }

   public final String getSystemId() {
      return this.systemId;
   }

   public final Map<String, String> getInscopeNamespaces() {
      return this._inscopeNamespaces;
   }

   public final boolean hasInternedStrings() {
      return this._hasInternedStrings;
   }

   public final StreamReaderBufferProcessor readAsXMLStreamReader() throws XMLStreamException {
      return new StreamReaderBufferProcessor(this);
   }

   public final void writeToXMLStreamWriter(XMLStreamWriter writer, boolean writeAsFragment) throws XMLStreamException {
      StreamWriterBufferProcessor p = new StreamWriterBufferProcessor(this, writeAsFragment);
      p.process(writer);
   }

   /** @deprecated */
   public final void writeToXMLStreamWriter(XMLStreamWriter writer) throws XMLStreamException {
      this.writeToXMLStreamWriter(writer, this.isFragment());
   }

   /** @deprecated */
   public final SAXBufferProcessor readAsXMLReader() {
      return new SAXBufferProcessor(this, this.isFragment());
   }

   public final SAXBufferProcessor readAsXMLReader(boolean produceFragmentEvent) {
      return new SAXBufferProcessor(this, produceFragmentEvent);
   }

   public final void writeTo(ContentHandler handler, boolean produceFragmentEvent) throws SAXException {
      SAXBufferProcessor p = this.readAsXMLReader(produceFragmentEvent);
      p.setContentHandler(handler);
      if (p instanceof LexicalHandler) {
         p.setLexicalHandler((LexicalHandler)handler);
      }

      if (p instanceof DTDHandler) {
         p.setDTDHandler((DTDHandler)handler);
      }

      if (p instanceof ErrorHandler) {
         p.setErrorHandler((ErrorHandler)handler);
      }

      p.process();
   }

   /** @deprecated */
   public final void writeTo(ContentHandler handler) throws SAXException {
      this.writeTo(handler, this.isFragment());
   }

   public final void writeTo(ContentHandler handler, ErrorHandler errorHandler, boolean produceFragmentEvent) throws SAXException {
      SAXBufferProcessor p = this.readAsXMLReader(produceFragmentEvent);
      p.setContentHandler(handler);
      if (p instanceof LexicalHandler) {
         p.setLexicalHandler((LexicalHandler)handler);
      }

      if (p instanceof DTDHandler) {
         p.setDTDHandler((DTDHandler)handler);
      }

      p.setErrorHandler(errorHandler);
      p.process();
   }

   public final void writeTo(ContentHandler handler, ErrorHandler errorHandler) throws SAXException {
      this.writeTo(handler, errorHandler, this.isFragment());
   }

   public final Node writeTo(Node n) throws XMLStreamBufferException {
      try {
         Transformer t = ((TransformerFactory)trnsformerFactory.get()).newTransformer();
         t.transform(new XMLStreamBufferSource(this), new DOMResult(n));
         return n.getLastChild();
      } catch (TransformerException var3) {
         throw new XMLStreamBufferException(var3);
      }
   }

   public static XMLStreamBuffer createNewBufferFromXMLStreamReader(XMLStreamReader reader) throws XMLStreamException {
      MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
      b.createFromXMLStreamReader(reader);
      return b;
   }

   public static XMLStreamBuffer createNewBufferFromXMLReader(XMLReader reader, InputStream in) throws SAXException, IOException {
      MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
      b.createFromXMLReader(reader, in);
      return b;
   }

   public static XMLStreamBuffer createNewBufferFromXMLReader(XMLReader reader, InputStream in, String systemId) throws SAXException, IOException {
      MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
      b.createFromXMLReader(reader, in, systemId);
      return b;
   }

   protected final FragmentedArray<byte[]> getStructure() {
      return this._structure;
   }

   protected final int getStructurePtr() {
      return this._structurePtr;
   }

   protected final FragmentedArray<String[]> getStructureStrings() {
      return this._structureStrings;
   }

   protected final int getStructureStringsPtr() {
      return this._structureStringsPtr;
   }

   protected final FragmentedArray<char[]> getContentCharactersBuffer() {
      return this._contentCharactersBuffer;
   }

   protected final int getContentCharactersBufferPtr() {
      return this._contentCharactersBufferPtr;
   }

   protected final FragmentedArray<Object[]> getContentObjects() {
      return this._contentObjects;
   }

   protected final int getContentObjectsPtr() {
      return this._contentObjectsPtr;
   }
}
