package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSSerializerFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract class BaseMarkupSerializer implements ContentHandler, DocumentHandler, LexicalHandler, DTDHandler, DeclHandler, DOMSerializer, Serializer {
   protected short features = -1;
   protected DOMErrorHandler fDOMErrorHandler;
   protected final DOMErrorImpl fDOMError = new DOMErrorImpl();
   protected LSSerializerFilter fDOMFilter;
   protected EncodingInfo _encodingInfo;
   private ElementState[] _elementStates = new ElementState[10];
   private int _elementStateCount;
   private Vector _preRoot;
   protected boolean _started;
   private boolean _prepared;
   protected Map<String, String> _prefixes;
   protected String _docTypePublicId;
   protected String _docTypeSystemId;
   protected OutputFormat _format;
   protected Printer _printer;
   protected boolean _indenting;
   protected final StringBuffer fStrBuffer = new StringBuffer(40);
   private Writer _writer;
   private OutputStream _output;
   protected Node fCurrentNode = null;

   protected BaseMarkupSerializer(OutputFormat format) {
      for(int i = 0; i < this._elementStates.length; ++i) {
         this._elementStates[i] = new ElementState();
      }

      this._format = format;
   }

   public DocumentHandler asDocumentHandler() throws IOException {
      this.prepare();
      return this;
   }

   public ContentHandler asContentHandler() throws IOException {
      this.prepare();
      return this;
   }

   public DOMSerializer asDOMSerializer() throws IOException {
      this.prepare();
      return this;
   }

   public void setOutputByteStream(OutputStream output) {
      if (output == null) {
         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[]{"output"});
         throw new NullPointerException(msg);
      } else {
         this._output = output;
         this._writer = null;
         this.reset();
      }
   }

   public void setOutputCharStream(Writer writer) {
      if (writer == null) {
         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[]{"writer"});
         throw new NullPointerException(msg);
      } else {
         this._writer = writer;
         this._output = null;
         this.reset();
      }
   }

   public void setOutputFormat(OutputFormat format) {
      if (format == null) {
         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ArgumentIsNull", new Object[]{"format"});
         throw new NullPointerException(msg);
      } else {
         this._format = format;
         this.reset();
      }
   }

   public boolean reset() {
      if (this._elementStateCount > 1) {
         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResetInMiddle", (Object[])null);
         throw new IllegalStateException(msg);
      } else {
         this._prepared = false;
         this.fCurrentNode = null;
         this.fStrBuffer.setLength(0);
         return true;
      }
   }

   protected void prepare() throws IOException {
      if (!this._prepared) {
         if (this._writer == null && this._output == null) {
            String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", (Object[])null);
            throw new IOException(msg);
         } else {
            this._encodingInfo = this._format.getEncodingInfo();
            if (this._output != null) {
               this._writer = this._encodingInfo.getWriter(this._output);
            }

            if (this._format.getIndenting()) {
               this._indenting = true;
               this._printer = new IndentPrinter(this._writer, this._format);
            } else {
               this._indenting = false;
               this._printer = new Printer(this._writer, this._format);
            }

            this._elementStateCount = 0;
            ElementState state = this._elementStates[0];
            state.namespaceURI = null;
            state.localName = null;
            state.rawName = null;
            state.preserveSpace = this._format.getPreserveSpace();
            state.empty = true;
            state.afterElement = false;
            state.afterComment = false;
            state.doCData = state.inCData = false;
            state.prefixes = null;
            this._docTypePublicId = this._format.getDoctypePublic();
            this._docTypeSystemId = this._format.getDoctypeSystem();
            this._started = false;
            this._prepared = true;
         }
      }
   }

   public void serialize(Element elem) throws IOException {
      this.reset();
      this.prepare();
      this.serializeNode(elem);
      this._printer.flush();
      if (this._printer.getException() != null) {
         throw this._printer.getException();
      }
   }

   public void serialize(Node node) throws IOException {
      this.reset();
      this.prepare();
      this.serializeNode(node);
      this.serializePreRoot();
      this._printer.flush();
      if (this._printer.getException() != null) {
         throw this._printer.getException();
      }
   }

   public void serialize(DocumentFragment frag) throws IOException {
      this.reset();
      this.prepare();
      this.serializeNode(frag);
      this._printer.flush();
      if (this._printer.getException() != null) {
         throw this._printer.getException();
      }
   }

   public void serialize(Document doc) throws IOException {
      this.reset();
      this.prepare();
      this.serializeNode(doc);
      this.serializePreRoot();
      this._printer.flush();
      if (this._printer.getException() != null) {
         throw this._printer.getException();
      }
   }

   public void startDocument() throws SAXException {
      try {
         this.prepare();
      } catch (IOException var2) {
         throw new SAXException(var2.toString());
      }
   }

   public void characters(char[] chars, int start, int length) throws SAXException {
      try {
         ElementState state = this.content();
         int saveIndent;
         if (!state.inCData && !state.doCData) {
            if (state.preserveSpace) {
               saveIndent = this._printer.getNextIndent();
               this._printer.setNextIndent(0);
               this.printText(chars, start, length, true, state.unescaped);
               this._printer.setNextIndent(saveIndent);
            } else {
               this.printText(chars, start, length, false, state.unescaped);
            }
         } else {
            if (!state.inCData) {
               this._printer.printText("<![CDATA[");
               state.inCData = true;
            }

            saveIndent = this._printer.getNextIndent();
            this._printer.setNextIndent(0);
            int end = start + length;

            for(int index = start; index < end; ++index) {
               char ch = chars[index];
               if (ch == ']' && index + 2 < end && chars[index + 1] == ']' && chars[index + 2] == '>') {
                  this._printer.printText("]]]]><![CDATA[>");
                  index += 2;
               } else if (!XMLChar.isValid(ch)) {
                  ++index;
                  if (index < end) {
                     this.surrogates(ch, chars[index]);
                  } else {
                     this.fatalError("The character '" + ch + "' is an invalid XML character");
                  }
               } else if ((ch < ' ' || !this._encodingInfo.isPrintable(ch) || ch == 247) && ch != '\n' && ch != '\r' && ch != '\t') {
                  this._printer.printText("]]>&#x");
                  this._printer.printText(Integer.toHexString(ch));
                  this._printer.printText(";<![CDATA[");
               } else {
                  this._printer.printText(ch);
               }
            }

            this._printer.setNextIndent(saveIndent);
         }

      } catch (IOException var9) {
         throw new SAXException(var9);
      }
   }

   public void ignorableWhitespace(char[] chars, int start, int length) throws SAXException {
      try {
         this.content();
         if (this._indenting) {
            this._printer.setThisIndent(0);

            for(int i = start; length-- > 0; ++i) {
               this._printer.printText(chars[i]);
            }
         }

      } catch (IOException var6) {
         throw new SAXException(var6);
      }
   }

   public final void processingInstruction(String target, String code) throws SAXException {
      try {
         this.processingInstructionIO(target, code);
      } catch (IOException var4) {
         throw new SAXException(var4);
      }
   }

   public void processingInstructionIO(String target, String code) throws IOException {
      ElementState state = this.content();
      int index = target.indexOf("?>");
      if (index >= 0) {
         this.fStrBuffer.append("<?").append(target.substring(0, index));
      } else {
         this.fStrBuffer.append("<?").append(target);
      }

      if (code != null) {
         this.fStrBuffer.append(' ');
         index = code.indexOf("?>");
         if (index >= 0) {
            this.fStrBuffer.append(code.substring(0, index));
         } else {
            this.fStrBuffer.append(code);
         }
      }

      this.fStrBuffer.append("?>");
      if (this.isDocumentState()) {
         if (this._preRoot == null) {
            this._preRoot = new Vector();
         }

         this._preRoot.addElement(this.fStrBuffer.toString());
      } else {
         this._printer.indent();
         this.printText(this.fStrBuffer.toString(), true, true);
         this._printer.unindent();
         if (this._indenting) {
            state.afterElement = true;
         }
      }

      this.fStrBuffer.setLength(0);
   }

   public void comment(char[] chars, int start, int length) throws SAXException {
      try {
         this.comment(new String(chars, start, length));
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void comment(String text) throws IOException {
      if (!this._format.getOmitComments()) {
         ElementState state = this.content();
         int index = text.indexOf("-->");
         if (index >= 0) {
            this.fStrBuffer.append("<!--").append(text.substring(0, index)).append("-->");
         } else {
            this.fStrBuffer.append("<!--").append(text).append("-->");
         }

         if (this.isDocumentState()) {
            if (this._preRoot == null) {
               this._preRoot = new Vector();
            }

            this._preRoot.addElement(this.fStrBuffer.toString());
         } else {
            if (this._indenting && !state.preserveSpace) {
               this._printer.breakLine();
            }

            this._printer.indent();
            this.printText(this.fStrBuffer.toString(), true, true);
            this._printer.unindent();
            if (this._indenting) {
               state.afterElement = true;
            }
         }

         this.fStrBuffer.setLength(0);
         state.afterComment = true;
         state.afterElement = false;
      }
   }

   public void startCDATA() {
      ElementState state = this.getElementState();
      state.doCData = true;
   }

   public void endCDATA() {
      ElementState state = this.getElementState();
      state.doCData = false;
   }

   public void startNonEscaping() {
      ElementState state = this.getElementState();
      state.unescaped = true;
   }

   public void endNonEscaping() {
      ElementState state = this.getElementState();
      state.unescaped = false;
   }

   public void startPreserving() {
      ElementState state = this.getElementState();
      state.preserveSpace = true;
   }

   public void endPreserving() {
      ElementState state = this.getElementState();
      state.preserveSpace = false;
   }

   public void endDocument() throws SAXException {
      try {
         this.serializePreRoot();
         this._printer.flush();
      } catch (IOException var2) {
         throw new SAXException(var2);
      }
   }

   public void startEntity(String name) {
   }

   public void endEntity(String name) {
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void skippedEntity(String name) throws SAXException {
      try {
         this.endCDATA();
         this.content();
         this._printer.printText('&');
         this._printer.printText(name);
         this._printer.printText(';');
      } catch (IOException var3) {
         throw new SAXException(var3);
      }
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (this._prefixes == null) {
         this._prefixes = new HashMap();
      }

      this._prefixes.put(uri, prefix == null ? "" : prefix);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public final void startDTD(String name, String publicId, String systemId) throws SAXException {
      try {
         this._printer.enterDTD();
         this._docTypePublicId = publicId;
         this._docTypeSystemId = systemId;
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void endDTD() {
   }

   public void elementDecl(String name, String model) throws SAXException {
      try {
         this._printer.enterDTD();
         this._printer.printText("<!ELEMENT ");
         this._printer.printText(name);
         this._printer.printText(' ');
         this._printer.printText(model);
         this._printer.printText('>');
         if (this._indenting) {
            this._printer.breakLine();
         }

      } catch (IOException var4) {
         throw new SAXException(var4);
      }
   }

   public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
      try {
         this._printer.enterDTD();
         this._printer.printText("<!ATTLIST ");
         this._printer.printText(eName);
         this._printer.printText(' ');
         this._printer.printText(aName);
         this._printer.printText(' ');
         this._printer.printText(type);
         if (valueDefault != null) {
            this._printer.printText(' ');
            this._printer.printText(valueDefault);
         }

         if (value != null) {
            this._printer.printText(" \"");
            this.printEscaped(value);
            this._printer.printText('"');
         }

         this._printer.printText('>');
         if (this._indenting) {
            this._printer.breakLine();
         }

      } catch (IOException var7) {
         throw new SAXException(var7);
      }
   }

   public void internalEntityDecl(String name, String value) throws SAXException {
      try {
         this._printer.enterDTD();
         this._printer.printText("<!ENTITY ");
         this._printer.printText(name);
         this._printer.printText(" \"");
         this.printEscaped(value);
         this._printer.printText("\">");
         if (this._indenting) {
            this._printer.breakLine();
         }

      } catch (IOException var4) {
         throw new SAXException(var4);
      }
   }

   public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
      try {
         this._printer.enterDTD();
         this.unparsedEntityDecl(name, publicId, systemId, (String)null);
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
      try {
         this._printer.enterDTD();
         if (publicId == null) {
            this._printer.printText("<!ENTITY ");
            this._printer.printText(name);
            this._printer.printText(" SYSTEM ");
            this.printDoctypeURL(systemId);
         } else {
            this._printer.printText("<!ENTITY ");
            this._printer.printText(name);
            this._printer.printText(" PUBLIC ");
            this.printDoctypeURL(publicId);
            this._printer.printText(' ');
            this.printDoctypeURL(systemId);
         }

         if (notationName != null) {
            this._printer.printText(" NDATA ");
            this._printer.printText(notationName);
         }

         this._printer.printText('>');
         if (this._indenting) {
            this._printer.breakLine();
         }

      } catch (IOException var6) {
         throw new SAXException(var6);
      }
   }

   public void notationDecl(String name, String publicId, String systemId) throws SAXException {
      try {
         this._printer.enterDTD();
         if (publicId != null) {
            this._printer.printText("<!NOTATION ");
            this._printer.printText(name);
            this._printer.printText(" PUBLIC ");
            this.printDoctypeURL(publicId);
            if (systemId != null) {
               this._printer.printText(' ');
               this.printDoctypeURL(systemId);
            }
         } else {
            this._printer.printText("<!NOTATION ");
            this._printer.printText(name);
            this._printer.printText(" SYSTEM ");
            this.printDoctypeURL(systemId);
         }

         this._printer.printText('>');
         if (this._indenting) {
            this._printer.breakLine();
         }

      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   protected void serializeNode(Node node) throws IOException {
      this.fCurrentNode = node;
      Node child;
      String text;
      short code;
      short code;
      switch(node.getNodeType()) {
      case 1:
         if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 1) != 0) {
            code = this.fDOMFilter.acceptNode(node);
            switch(code) {
            case 2:
               return;
            case 3:
               for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  this.serializeNode(child);
               }

               return;
            }
         }

         this.serializeElement((Element)node);
      case 2:
      case 6:
      case 10:
      default:
         break;
      case 3:
         text = node.getNodeValue();
         if (text != null) {
            if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 4) != 0) {
               code = this.fDOMFilter.acceptNode(node);
               switch(code) {
               case 2:
               case 3:
                  break;
               default:
                  this.characters(text);
               }
            } else if (!this._indenting || this.getElementState().preserveSpace || text.replace('\n', ' ').trim().length() != 0) {
               this.characters(text);
            }
         }
         break;
      case 4:
         text = node.getNodeValue();
         if ((this.features & 8) != 0) {
            if (text != null) {
               if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 8) != 0) {
                  code = this.fDOMFilter.acceptNode(node);
                  switch(code) {
                  case 2:
                  case 3:
                     return;
                  }
               }

               this.startCDATA();
               this.characters(text);
               this.endCDATA();
            }
         } else {
            this.characters(text);
         }
         break;
      case 5:
         this.endCDATA();
         this.content();
         if ((this.features & 4) == 0 && node.getFirstChild() != null) {
            for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               this.serializeNode(child);
            }

            return;
         } else {
            if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 16) != 0) {
               code = this.fDOMFilter.acceptNode(node);
               switch(code) {
               case 2:
                  return;
               case 3:
                  for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                     this.serializeNode(child);
                  }

                  return;
               }
            }

            this.checkUnboundNamespacePrefixedNode(node);
            this._printer.printText("&");
            this._printer.printText(node.getNodeName());
            this._printer.printText(";");
            break;
         }
      case 7:
         if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 64) != 0) {
            code = this.fDOMFilter.acceptNode(node);
            switch(code) {
            case 2:
            case 3:
               return;
            }
         }

         this.processingInstructionIO(node.getNodeName(), node.getNodeValue());
         break;
      case 8:
         if (!this._format.getOmitComments()) {
            text = node.getNodeValue();
            if (text != null) {
               if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 128) != 0) {
                  code = this.fDOMFilter.acceptNode(node);
                  switch(code) {
                  case 2:
                  case 3:
                     return;
                  }
               }

               this.comment(text);
            }
         }
         break;
      case 9:
         this.serializeDocument();
         DocumentType docType = ((Document)node).getDoctype();
         if (docType != null) {
            DOMImplementation var3 = ((Document)node).getImplementation();

            try {
               this._printer.enterDTD();
               this._docTypePublicId = docType.getPublicId();
               this._docTypeSystemId = docType.getSystemId();
               String internal = docType.getInternalSubset();
               if (internal != null && internal.length() > 0) {
                  this._printer.printText(internal);
               }

               this.endDTD();
            } catch (NoSuchMethodError var15) {
               Class docTypeClass = docType.getClass();
               String docTypePublicId = null;
               String docTypeSystemId = null;

               java.lang.reflect.Method getSystemId;
               try {
                  getSystemId = docTypeClass.getMethod("getPublicId", (Class[])null);
                  if (getSystemId.getReturnType().equals(String.class)) {
                     docTypePublicId = (String)getSystemId.invoke(docType, (Object[])null);
                  }
               } catch (Exception var14) {
               }

               try {
                  getSystemId = docTypeClass.getMethod("getSystemId", (Class[])null);
                  if (getSystemId.getReturnType().equals(String.class)) {
                     docTypeSystemId = (String)getSystemId.invoke(docType, (Object[])null);
                  }
               } catch (Exception var13) {
               }

               this._printer.enterDTD();
               this._docTypePublicId = docTypePublicId;
               this._docTypeSystemId = docTypeSystemId;
               this.endDTD();
            }

            this.serializeDTD(docType.getName());
         }

         this._started = true;
      case 11:
         for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            this.serializeNode(child);
         }
      }

   }

   protected void serializeDocument() throws IOException {
      String dtd = this._printer.leaveDTD();
      if (!this._started && !this._format.getOmitXMLDeclaration()) {
         StringBuffer buffer = new StringBuffer("<?xml version=\"");
         if (this._format.getVersion() != null) {
            buffer.append(this._format.getVersion());
         } else {
            buffer.append("1.0");
         }

         buffer.append('"');
         String format_encoding = this._format.getEncoding();
         if (format_encoding != null) {
            buffer.append(" encoding=\"");
            buffer.append(format_encoding);
            buffer.append('"');
         }

         if (this._format.getStandalone() && this._docTypeSystemId == null && this._docTypePublicId == null) {
            buffer.append(" standalone=\"yes\"");
         }

         buffer.append("?>");
         this._printer.printText(buffer);
         this._printer.breakLine();
      }

      this.serializePreRoot();
   }

   protected void serializeDTD(String name) throws IOException {
      String dtd = this._printer.leaveDTD();
      if (!this._format.getOmitDocumentType()) {
         if (this._docTypeSystemId != null) {
            this._printer.printText("<!DOCTYPE ");
            this._printer.printText(name);
            if (this._docTypePublicId == null) {
               this._printer.printText(" SYSTEM ");
               this.printDoctypeURL(this._docTypeSystemId);
            } else {
               this._printer.printText(" PUBLIC ");
               this.printDoctypeURL(this._docTypePublicId);
               if (this._indenting) {
                  this._printer.breakLine();

                  for(int i = 0; i < 18 + name.length(); ++i) {
                     this._printer.printText(" ");
                  }
               } else {
                  this._printer.printText(" ");
               }

               this.printDoctypeURL(this._docTypeSystemId);
            }

            if (dtd != null && dtd.length() > 0) {
               this._printer.printText(" [");
               this.printText(dtd, true, true);
               this._printer.printText(']');
            }

            this._printer.printText(">");
            this._printer.breakLine();
         } else if (dtd != null && dtd.length() > 0) {
            this._printer.printText("<!DOCTYPE ");
            this._printer.printText(name);
            this._printer.printText(" [");
            this.printText(dtd, true, true);
            this._printer.printText("]>");
            this._printer.breakLine();
         }
      }

   }

   protected ElementState content() throws IOException {
      ElementState state = this.getElementState();
      if (!this.isDocumentState()) {
         if (state.inCData && !state.doCData) {
            this._printer.printText("]]>");
            state.inCData = false;
         }

         if (state.empty) {
            this._printer.printText('>');
            state.empty = false;
         }

         state.afterElement = false;
         state.afterComment = false;
      }

      return state;
   }

   protected void characters(String text) throws IOException {
      ElementState state = this.content();
      if (!state.inCData && !state.doCData) {
         if (state.preserveSpace) {
            int saveIndent = this._printer.getNextIndent();
            this._printer.setNextIndent(0);
            this.printText(text, true, state.unescaped);
            this._printer.setNextIndent(saveIndent);
         } else {
            this.printText(text, false, state.unescaped);
         }
      } else {
         if (!state.inCData) {
            this._printer.printText("<![CDATA[");
            state.inCData = true;
         }

         int saveIndent = this._printer.getNextIndent();
         this._printer.setNextIndent(0);
         this.printCDATAText(text);
         this._printer.setNextIndent(saveIndent);
      }

   }

   protected abstract String getEntityRef(int var1);

   protected abstract void serializeElement(Element var1) throws IOException;

   protected void serializePreRoot() throws IOException {
      if (this._preRoot != null) {
         for(int i = 0; i < this._preRoot.size(); ++i) {
            this.printText((String)this._preRoot.elementAt(i), true, true);
            if (this._indenting) {
               this._printer.breakLine();
            }
         }

         this._preRoot.removeAllElements();
      }

   }

   protected void printCDATAText(String text) throws IOException {
      int length = text.length();

      for(int index = 0; index < length; ++index) {
         char ch = text.charAt(index);
         if (ch == ']' && index + 2 < length && text.charAt(index + 1) == ']' && text.charAt(index + 2) == '>') {
            if (this.fDOMErrorHandler != null) {
               String msg;
               if ((this.features & 16) == 0) {
                  msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "EndingCDATA", (Object[])null);
                  if ((this.features & 2) != 0) {
                     this.modifyDOMError(msg, (short)3, "wf-invalid-character", this.fCurrentNode);
                     this.fDOMErrorHandler.handleError(this.fDOMError);
                     throw new LSException((short)82, msg);
                  }

                  this.modifyDOMError(msg, (short)2, "cdata-section-not-splitted", this.fCurrentNode);
                  if (!this.fDOMErrorHandler.handleError(this.fDOMError)) {
                     throw new LSException((short)82, msg);
                  }
               } else {
                  msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SplittingCDATA", (Object[])null);
                  this.modifyDOMError(msg, (short)1, (String)null, this.fCurrentNode);
                  this.fDOMErrorHandler.handleError(this.fDOMError);
               }
            }

            this._printer.printText("]]]]><![CDATA[>");
            index += 2;
         } else if (!XMLChar.isValid(ch)) {
            ++index;
            if (index < length) {
               this.surrogates(ch, text.charAt(index));
            } else {
               this.fatalError("The character '" + ch + "' is an invalid XML character");
            }
         } else if ((ch < ' ' || !this._encodingInfo.isPrintable(ch) || ch == 247) && ch != '\n' && ch != '\r' && ch != '\t') {
            this._printer.printText("]]>&#x");
            this._printer.printText(Integer.toHexString(ch));
            this._printer.printText(";<![CDATA[");
         } else {
            this._printer.printText(ch);
         }
      }

   }

   protected void surrogates(int high, int low) throws IOException {
      if (XMLChar.isHighSurrogate(high)) {
         if (!XMLChar.isLowSurrogate(low)) {
            this.fatalError("The character '" + (char)low + "' is an invalid XML character");
         } else {
            int supplemental = XMLChar.supplemental((char)high, (char)low);
            if (!XMLChar.isValid(supplemental)) {
               this.fatalError("The character '" + (char)supplemental + "' is an invalid XML character");
            } else if (this.content().inCData) {
               this._printer.printText("]]>&#x");
               this._printer.printText(Integer.toHexString(supplemental));
               this._printer.printText(";<![CDATA[");
            } else {
               this.printHex(supplemental);
            }
         }
      } else {
         this.fatalError("The character '" + (char)high + "' is an invalid XML character");
      }

   }

   protected void printText(char[] chars, int start, int length, boolean preserveSpace, boolean unescaped) throws IOException {
      char ch;
      if (preserveSpace) {
         while(true) {
            while(length-- > 0) {
               ch = chars[start];
               ++start;
               if (ch != '\n' && ch != '\r' && !unescaped) {
                  this.printEscaped(ch);
               } else {
                  this._printer.printText(ch);
               }
            }

            return;
         }
      } else {
         while(true) {
            while(length-- > 0) {
               ch = chars[start];
               ++start;
               if (ch != ' ' && ch != '\f' && ch != '\t' && ch != '\n' && ch != '\r') {
                  if (unescaped) {
                     this._printer.printText(ch);
                  } else {
                     this.printEscaped(ch);
                  }
               } else {
                  this._printer.printSpace();
               }
            }

            return;
         }
      }
   }

   protected void printText(String text, boolean preserveSpace, boolean unescaped) throws IOException {
      int index;
      char ch;
      if (preserveSpace) {
         for(index = 0; index < text.length(); ++index) {
            ch = text.charAt(index);
            if (ch != '\n' && ch != '\r' && !unescaped) {
               this.printEscaped(ch);
            } else {
               this._printer.printText(ch);
            }
         }
      } else {
         for(index = 0; index < text.length(); ++index) {
            ch = text.charAt(index);
            if (ch != ' ' && ch != '\f' && ch != '\t' && ch != '\n' && ch != '\r') {
               if (unescaped) {
                  this._printer.printText(ch);
               } else {
                  this.printEscaped(ch);
               }
            } else {
               this._printer.printSpace();
            }
         }
      }

   }

   protected void printDoctypeURL(String url) throws IOException {
      this._printer.printText('"');

      for(int i = 0; i < url.length(); ++i) {
         if (url.charAt(i) != '"' && url.charAt(i) >= ' ' && url.charAt(i) <= 127) {
            this._printer.printText(url.charAt(i));
         } else {
            this._printer.printText('%');
            this._printer.printText(Integer.toHexString(url.charAt(i)));
         }
      }

      this._printer.printText('"');
   }

   protected void printEscaped(int ch) throws IOException {
      String charRef = this.getEntityRef(ch);
      if (charRef != null) {
         this._printer.printText('&');
         this._printer.printText(charRef);
         this._printer.printText(';');
      } else if ((ch < 32 || !this._encodingInfo.isPrintable((char)ch) || ch == 247) && ch != 10 && ch != 13 && ch != 9) {
         this.printHex(ch);
      } else if (ch < 65536) {
         this._printer.printText((char)ch);
      } else {
         this._printer.printText((char)((ch - 65536 >> 10) + '\ud800'));
         this._printer.printText((char)((ch - 65536 & 1023) + '\udc00'));
      }

   }

   final void printHex(int ch) throws IOException {
      this._printer.printText("&#x");
      this._printer.printText(Integer.toHexString(ch));
      this._printer.printText(';');
   }

   protected void printEscaped(String source) throws IOException {
      for(int i = 0; i < source.length(); ++i) {
         int ch = source.charAt(i);
         if ((ch & 'ﰀ') == 55296 && i + 1 < source.length()) {
            int lowch = source.charAt(i + 1);
            if ((lowch & 'ﰀ') == 56320) {
               ch = 65536 + (ch - '\ud800' << 10) + lowch - '\udc00';
               ++i;
            }
         }

         this.printEscaped(ch);
      }

   }

   protected ElementState getElementState() {
      return this._elementStates[this._elementStateCount];
   }

   protected ElementState enterElementState(String namespaceURI, String localName, String rawName, boolean preserveSpace) {
      if (this._elementStateCount + 1 == this._elementStates.length) {
         ElementState[] newStates = new ElementState[this._elementStates.length + 10];

         int i;
         for(i = 0; i < this._elementStates.length; ++i) {
            newStates[i] = this._elementStates[i];
         }

         for(i = this._elementStates.length; i < newStates.length; ++i) {
            newStates[i] = new ElementState();
         }

         this._elementStates = newStates;
      }

      ++this._elementStateCount;
      ElementState state = this._elementStates[this._elementStateCount];
      state.namespaceURI = namespaceURI;
      state.localName = localName;
      state.rawName = rawName;
      state.preserveSpace = preserveSpace;
      state.empty = true;
      state.afterElement = false;
      state.afterComment = false;
      state.doCData = state.inCData = false;
      state.unescaped = false;
      state.prefixes = this._prefixes;
      this._prefixes = null;
      return state;
   }

   protected ElementState leaveElementState() {
      if (this._elementStateCount > 0) {
         this._prefixes = null;
         --this._elementStateCount;
         return this._elementStates[this._elementStateCount];
      } else {
         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "Internal", (Object[])null);
         throw new IllegalStateException(msg);
      }
   }

   protected boolean isDocumentState() {
      return this._elementStateCount == 0;
   }

   protected String getPrefix(String namespaceURI) {
      String prefix;
      if (this._prefixes != null) {
         prefix = (String)this._prefixes.get(namespaceURI);
         if (prefix != null) {
            return prefix;
         }
      }

      if (this._elementStateCount == 0) {
         return null;
      } else {
         for(int i = this._elementStateCount; i > 0; --i) {
            if (this._elementStates[i].prefixes != null) {
               prefix = (String)this._elementStates[i].prefixes.get(namespaceURI);
               if (prefix != null) {
                  return prefix;
               }
            }
         }

         return null;
      }
   }

   protected DOMError modifyDOMError(String message, short severity, String type, Node node) {
      this.fDOMError.reset();
      this.fDOMError.fMessage = message;
      this.fDOMError.fType = type;
      this.fDOMError.fSeverity = severity;
      this.fDOMError.fLocator = new DOMLocatorImpl(-1, -1, -1, node, (String)null);
      return this.fDOMError;
   }

   protected void fatalError(String message) throws IOException {
      if (this.fDOMErrorHandler != null) {
         this.modifyDOMError(message, (short)3, (String)null, this.fCurrentNode);
         this.fDOMErrorHandler.handleError(this.fDOMError);
      } else {
         throw new IOException(message);
      }
   }

   protected void checkUnboundNamespacePrefixedNode(Node node) throws IOException {
   }
}
