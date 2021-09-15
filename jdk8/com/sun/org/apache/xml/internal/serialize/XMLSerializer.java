package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLSerializer extends BaseMarkupSerializer {
   protected static final boolean DEBUG = false;
   protected NamespaceSupport fNSBinder;
   protected NamespaceSupport fLocalNSBinder;
   protected SymbolTable fSymbolTable;
   protected static final String PREFIX = "NS";
   protected boolean fNamespaces = false;
   protected boolean fNamespacePrefixes = true;
   private boolean fPreserveSpace;

   public XMLSerializer() {
      super(new OutputFormat("xml", (String)null, false));
   }

   public XMLSerializer(OutputFormat format) {
      super(format != null ? format : new OutputFormat("xml", (String)null, false));
      this._format.setMethod("xml");
   }

   public XMLSerializer(Writer writer, OutputFormat format) {
      super(format != null ? format : new OutputFormat("xml", (String)null, false));
      this._format.setMethod("xml");
      this.setOutputCharStream(writer);
   }

   public XMLSerializer(OutputStream output, OutputFormat format) {
      super(format != null ? format : new OutputFormat("xml", (String)null, false));
      this._format.setMethod("xml");
      this.setOutputByteStream(output);
   }

   public void setOutputFormat(OutputFormat format) {
      super.setOutputFormat(format != null ? format : new OutputFormat("xml", (String)null, false));
   }

   public void setNamespaces(boolean namespaces) {
      this.fNamespaces = namespaces;
      if (this.fNSBinder == null) {
         this.fNSBinder = new NamespaceSupport();
         this.fLocalNSBinder = new NamespaceSupport();
         this.fSymbolTable = new SymbolTable();
      }

   }

   public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws SAXException {
      boolean var10 = false;

      try {
         String prefix;
         if (this._printer == null) {
            prefix = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", (Object[])null);
            throw new IllegalStateException(prefix);
         } else {
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
               if (!this._started) {
                  this.startDocument(localName != null && localName.length() != 0 ? localName : rawName);
               }
            } else {
               if (state.empty) {
                  this._printer.printText('>');
               }

               if (state.inCData) {
                  this._printer.printText("]]>");
                  state.inCData = false;
               }

               if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment)) {
                  this._printer.breakLine();
               }
            }

            boolean preserveSpace = state.preserveSpace;
            attrs = this.extractNamespaces(attrs);
            if (rawName == null || rawName.length() == 0) {
               if (localName == null) {
                  prefix = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoName", (Object[])null);
                  throw new SAXException(prefix);
               }

               if (namespaceURI != null && !namespaceURI.equals("")) {
                  prefix = this.getPrefix(namespaceURI);
                  if (prefix != null && prefix.length() > 0) {
                     rawName = prefix + ":" + localName;
                  } else {
                     rawName = localName;
                  }
               } else {
                  rawName = localName;
               }

               var10 = true;
            }

            this._printer.printText('<');
            this._printer.printText(rawName);
            this._printer.indent();
            String name;
            String value;
            if (attrs != null) {
               for(int i = 0; i < attrs.getLength(); ++i) {
                  this._printer.printSpace();
                  name = attrs.getQName(i);
                  if (name != null && name.length() == 0) {
                     name = attrs.getLocalName(i);
                     String attrURI = attrs.getURI(i);
                     if (attrURI != null && attrURI.length() != 0 && (namespaceURI == null || namespaceURI.length() == 0 || !attrURI.equals(namespaceURI))) {
                        prefix = this.getPrefix(attrURI);
                        if (prefix != null && prefix.length() > 0) {
                           name = prefix + ":" + name;
                        }
                     }
                  }

                  value = attrs.getValue(i);
                  if (value == null) {
                     value = "";
                  }

                  this._printer.printText(name);
                  this._printer.printText("=\"");
                  this.printEscaped(value);
                  this._printer.printText('"');
                  if (name.equals("xml:space")) {
                     if (value.equals("preserve")) {
                        preserveSpace = true;
                     } else {
                        preserveSpace = this._format.getPreserveSpace();
                     }
                  }
               }
            }

            if (this._prefixes != null) {
               Iterator var14 = this._prefixes.entrySet().iterator();

               while(var14.hasNext()) {
                  Map.Entry<String, String> entry = (Map.Entry)var14.next();
                  this._printer.printSpace();
                  value = (String)entry.getKey();
                  name = (String)entry.getValue();
                  if (name.length() == 0) {
                     this._printer.printText("xmlns=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  } else {
                     this._printer.printText("xmlns:");
                     this._printer.printText(name);
                     this._printer.printText("=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  }
               }
            }

            state = this.enterElementState(namespaceURI, localName, rawName, preserveSpace);
            name = localName != null && localName.length() != 0 ? namespaceURI + "^" + localName : rawName;
            state.doCData = this._format.isCDataElement(name);
            state.unescaped = this._format.isNonEscapingElement(name);
         }
      } catch (IOException var13) {
         throw new SAXException(var13);
      }
   }

   public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
      try {
         this.endElementIO(namespaceURI, localName, rawName);
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void endElementIO(String namespaceURI, String localName, String rawName) throws IOException {
      this._printer.unindent();
      ElementState state = this.getElementState();
      if (state.empty) {
         this._printer.printText("/>");
      } else {
         if (state.inCData) {
            this._printer.printText("]]>");
         }

         if (this._indenting && !state.preserveSpace && (state.afterElement || state.afterComment)) {
            this._printer.breakLine();
         }

         this._printer.printText("</");
         this._printer.printText(state.rawName);
         this._printer.printText('>');
      }

      state = this.leaveElementState();
      state.afterElement = true;
      state.afterComment = false;
      state.empty = false;
      if (this.isDocumentState()) {
         this._printer.flush();
      }

   }

   public void startElement(String tagName, AttributeList attrs) throws SAXException {
      try {
         if (this._printer == null) {
            String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", (Object[])null);
            throw new IllegalStateException(msg);
         } else {
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
               if (!this._started) {
                  this.startDocument(tagName);
               }
            } else {
               if (state.empty) {
                  this._printer.printText('>');
               }

               if (state.inCData) {
                  this._printer.printText("]]>");
                  state.inCData = false;
               }

               if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment)) {
                  this._printer.breakLine();
               }
            }

            boolean preserveSpace = state.preserveSpace;
            this._printer.printText('<');
            this._printer.printText(tagName);
            this._printer.indent();
            if (attrs != null) {
               for(int i = 0; i < attrs.getLength(); ++i) {
                  this._printer.printSpace();
                  String name = attrs.getName(i);
                  String value = attrs.getValue(i);
                  if (value != null) {
                     this._printer.printText(name);
                     this._printer.printText("=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  }

                  if (name.equals("xml:space")) {
                     if (value.equals("preserve")) {
                        preserveSpace = true;
                     } else {
                        preserveSpace = this._format.getPreserveSpace();
                     }
                  }
               }
            }

            state = this.enterElementState((String)null, (String)null, tagName, preserveSpace);
            state.doCData = this._format.isCDataElement(tagName);
            state.unescaped = this._format.isNonEscapingElement(tagName);
         }
      } catch (IOException var9) {
         throw new SAXException(var9);
      }
   }

   public void endElement(String tagName) throws SAXException {
      this.endElement((String)null, (String)null, tagName);
   }

   protected void startDocument(String rootTagName) throws IOException {
      String dtd = this._printer.leaveDTD();
      if (!this._started) {
         if (!this._format.getOmitXMLDeclaration()) {
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

         if (!this._format.getOmitDocumentType()) {
            if (this._docTypeSystemId != null) {
               this._printer.printText("<!DOCTYPE ");
               this._printer.printText(rootTagName);
               if (this._docTypePublicId == null) {
                  this._printer.printText(" SYSTEM ");
                  this.printDoctypeURL(this._docTypeSystemId);
               } else {
                  this._printer.printText(" PUBLIC ");
                  this.printDoctypeURL(this._docTypePublicId);
                  if (this._indenting) {
                     this._printer.breakLine();

                     for(int i = 0; i < 18 + rootTagName.length(); ++i) {
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
               this._printer.printText(rootTagName);
               this._printer.printText(" [");
               this.printText(dtd, true, true);
               this._printer.printText("]>");
               this._printer.breakLine();
            }
         }
      }

      this._started = true;
      this.serializePreRoot();
   }

   protected void serializeElement(Element elem) throws IOException {
      if (this.fNamespaces) {
         this.fLocalNSBinder.reset();
         this.fNSBinder.pushContext();
      }

      String tagName = elem.getTagName();
      ElementState state = this.getElementState();
      if (this.isDocumentState()) {
         if (!this._started) {
            this.startDocument(tagName);
         }
      } else {
         if (state.empty) {
            this._printer.printText('>');
         }

         if (state.inCData) {
            this._printer.printText("]]>");
            state.inCData = false;
         }

         if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment)) {
            this._printer.breakLine();
         }
      }

      this.fPreserveSpace = state.preserveSpace;
      int length = 0;
      NamedNodeMap attrMap = null;
      if (elem.hasAttributes()) {
         attrMap = elem.getAttributes();
         length = attrMap.getLength();
      }

      Attr attr;
      int i;
      String name;
      String value;
      if (!this.fNamespaces) {
         this._printer.printText('<');
         this._printer.printText(tagName);
         this._printer.indent();

         for(i = 0; i < length; ++i) {
            attr = (Attr)attrMap.item(i);
            name = attr.getName();
            value = attr.getValue();
            if (value == null) {
               value = "";
            }

            this.printAttribute(name, value, attr.getSpecified(), attr);
         }
      } else {
         String prefix;
         String uri;
         String localpart;
         boolean continueProcess;
         for(i = 0; i < length; ++i) {
            attr = (Attr)attrMap.item(i);
            uri = attr.getNamespaceURI();
            if (uri != null && uri.equals(NamespaceContext.XMLNS_URI)) {
               value = attr.getNodeValue();
               if (value == null) {
                  value = XMLSymbols.EMPTY_STRING;
               }

               if (value.equals(NamespaceContext.XMLNS_URI)) {
                  if (this.fDOMErrorHandler != null) {
                     localpart = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", (Object[])null);
                     this.modifyDOMError(localpart, (short)2, (String)null, attr);
                     continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                     if (!continueProcess) {
                        throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", (Object[])null));
                     }
                  }
               } else {
                  prefix = attr.getPrefix();
                  prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
                  localpart = this.fSymbolTable.addSymbol(attr.getLocalName());
                  if (prefix == XMLSymbols.PREFIX_XMLNS) {
                     value = this.fSymbolTable.addSymbol(value);
                     if (value.length() != 0) {
                        this.fNSBinder.declarePrefix(localpart, value);
                     }
                  } else {
                     value = this.fSymbolTable.addSymbol(value);
                     this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, value);
                  }
               }
            }
         }

         uri = elem.getNamespaceURI();
         prefix = elem.getPrefix();
         if (uri != null && prefix != null && uri.length() == 0 && prefix.length() != 0) {
            prefix = null;
            this._printer.printText('<');
            this._printer.printText(elem.getLocalName());
            this._printer.indent();
         } else {
            this._printer.printText('<');
            this._printer.printText(tagName);
            this._printer.indent();
         }

         if (uri == null) {
            if (elem.getLocalName() == null) {
               if (this.fDOMErrorHandler != null) {
                  localpart = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[]{elem.getNodeName()});
                  this.modifyDOMError(localpart, (short)2, (String)null, elem);
                  continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                  if (!continueProcess) {
                     throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", (Object[])null));
                  }
               }
            } else {
               uri = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
               if (uri != null && uri.length() > 0) {
                  if (this.fNamespacePrefixes) {
                     this.printNamespaceAttr(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                  }

                  this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                  this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
               }
            }
         } else {
            uri = this.fSymbolTable.addSymbol(uri);
            prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
            if (this.fNSBinder.getURI(prefix) != uri) {
               if (this.fNamespacePrefixes) {
                  this.printNamespaceAttr(prefix, uri);
               }

               this.fLocalNSBinder.declarePrefix(prefix, uri);
               this.fNSBinder.declarePrefix(prefix, uri);
            }
         }

         for(i = 0; i < length; ++i) {
            attr = (Attr)attrMap.item(i);
            value = attr.getValue();
            name = attr.getNodeName();
            uri = attr.getNamespaceURI();
            if (uri != null && uri.length() == 0) {
               uri = null;
               name = attr.getLocalName();
            }

            if (value == null) {
               value = XMLSymbols.EMPTY_STRING;
            }

            if (uri != null) {
               prefix = attr.getPrefix();
               prefix = prefix == null ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix);
               localpart = this.fSymbolTable.addSymbol(attr.getLocalName());
               if (uri != null && uri.equals(NamespaceContext.XMLNS_URI)) {
                  prefix = attr.getPrefix();
                  prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
                  localpart = this.fSymbolTable.addSymbol(attr.getLocalName());
                  String localUri;
                  if (prefix == XMLSymbols.PREFIX_XMLNS) {
                     localUri = this.fLocalNSBinder.getURI(localpart);
                     value = this.fSymbolTable.addSymbol(value);
                     if (value.length() != 0 && localUri == null) {
                        if (this.fNamespacePrefixes) {
                           this.printNamespaceAttr(localpart, value);
                        }

                        this.fLocalNSBinder.declarePrefix(localpart, value);
                     }
                  } else {
                     uri = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                     localUri = this.fLocalNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                     value = this.fSymbolTable.addSymbol(value);
                     if (localUri == null && this.fNamespacePrefixes) {
                        this.printNamespaceAttr(XMLSymbols.EMPTY_STRING, value);
                     }
                  }
               } else {
                  uri = this.fSymbolTable.addSymbol(uri);
                  String declaredURI = this.fNSBinder.getURI(prefix);
                  if (prefix == XMLSymbols.EMPTY_STRING || declaredURI != uri) {
                     name = attr.getNodeName();
                     String declaredPrefix = this.fNSBinder.getPrefix(uri);
                     if (declaredPrefix != null && declaredPrefix != XMLSymbols.EMPTY_STRING) {
                        name = declaredPrefix + ":" + localpart;
                     } else {
                        if (prefix == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(prefix) != null) {
                           int counter = 1;
                           SymbolTable var10000 = this.fSymbolTable;
                           StringBuilder var10001 = (new StringBuilder()).append("NS");
                           int var19 = counter + 1;

                           for(prefix = var10000.addSymbol(var10001.append((int)counter).toString()); this.fLocalNSBinder.getURI(prefix) != null; prefix = this.fSymbolTable.addSymbol("NS" + var19++)) {
                           }

                           name = prefix + ":" + localpart;
                        }

                        if (this.fNamespacePrefixes) {
                           this.printNamespaceAttr(prefix, uri);
                        }

                        value = this.fSymbolTable.addSymbol(value);
                        this.fLocalNSBinder.declarePrefix(prefix, value);
                        this.fNSBinder.declarePrefix(prefix, uri);
                     }
                  }

                  this.printAttribute(name, value == null ? XMLSymbols.EMPTY_STRING : value, attr.getSpecified(), attr);
               }
            } else if (attr.getLocalName() == null) {
               if (this.fDOMErrorHandler != null) {
                  localpart = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[]{attr.getNodeName()});
                  this.modifyDOMError(localpart, (short)2, (String)null, attr);
                  continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                  if (!continueProcess) {
                     throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", (Object[])null));
                  }
               }

               this.printAttribute(name, value, attr.getSpecified(), attr);
            } else {
               this.printAttribute(name, value, attr.getSpecified(), attr);
            }
         }
      }

      if (elem.hasChildNodes()) {
         state = this.enterElementState((String)null, (String)null, tagName, this.fPreserveSpace);
         state.doCData = this._format.isCDataElement(tagName);
         state.unescaped = this._format.isNonEscapingElement(tagName);

         for(Node child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
            this.serializeNode(child);
         }

         if (this.fNamespaces) {
            this.fNSBinder.popContext();
         }

         this.endElementIO((String)null, (String)null, tagName);
      } else {
         if (this.fNamespaces) {
            this.fNSBinder.popContext();
         }

         this._printer.unindent();
         this._printer.printText("/>");
         state.afterElement = true;
         state.afterComment = false;
         state.empty = false;
         if (this.isDocumentState()) {
            this._printer.flush();
         }
      }

   }

   private void printNamespaceAttr(String prefix, String uri) throws IOException {
      this._printer.printSpace();
      if (prefix == XMLSymbols.EMPTY_STRING) {
         this._printer.printText(XMLSymbols.PREFIX_XMLNS);
      } else {
         this._printer.printText("xmlns:" + prefix);
      }

      this._printer.printText("=\"");
      this.printEscaped(uri);
      this._printer.printText('"');
   }

   private void printAttribute(String name, String value, boolean isSpecified, Attr attr) throws IOException {
      if (isSpecified || (this.features & 64) == 0) {
         if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 2) != 0) {
            short code = this.fDOMFilter.acceptNode(attr);
            switch(code) {
            case 2:
            case 3:
               return;
            }
         }

         this._printer.printSpace();
         this._printer.printText(name);
         this._printer.printText("=\"");
         this.printEscaped(value);
         this._printer.printText('"');
      }

      if (name.equals("xml:space")) {
         if (value.equals("preserve")) {
            this.fPreserveSpace = true;
         } else {
            this.fPreserveSpace = this._format.getPreserveSpace();
         }
      }

   }

   protected String getEntityRef(int ch) {
      switch(ch) {
      case 34:
         return "quot";
      case 38:
         return "amp";
      case 39:
         return "apos";
      case 60:
         return "lt";
      case 62:
         return "gt";
      default:
         return null;
      }
   }

   private Attributes extractNamespaces(Attributes attrs) throws SAXException {
      if (attrs == null) {
         return null;
      } else {
         int length = attrs.getLength();
         AttributesImpl attrsOnly = new AttributesImpl(attrs);

         for(int i = length - 1; i >= 0; --i) {
            String rawName = attrsOnly.getQName(i);
            if (rawName.startsWith("xmlns")) {
               if (rawName.length() == 5) {
                  this.startPrefixMapping("", attrs.getValue(i));
                  attrsOnly.removeAttribute(i);
               } else if (rawName.charAt(5) == ':') {
                  this.startPrefixMapping(rawName.substring(6), attrs.getValue(i));
                  attrsOnly.removeAttribute(i);
               }
            }
         }

         return attrsOnly;
      }
   }

   protected void printEscaped(String source) throws IOException {
      int length = source.length();

      for(int i = 0; i < length; ++i) {
         int ch = source.charAt(i);
         if (!XMLChar.isValid(ch)) {
            ++i;
            if (i < length) {
               this.surrogates(ch, source.charAt(i));
            } else {
               this.fatalError("The character '" + (char)ch + "' is an invalid XML character");
            }
         } else if (ch != '\n' && ch != '\r' && ch != '\t') {
            if (ch == '<') {
               this._printer.printText("&lt;");
            } else if (ch == '&') {
               this._printer.printText("&amp;");
            } else if (ch == '"') {
               this._printer.printText("&quot;");
            } else if (ch >= ' ' && this._encodingInfo.isPrintable((char)ch)) {
               this._printer.printText((char)ch);
            } else {
               this.printHex(ch);
            }
         } else {
            this.printHex(ch);
         }
      }

   }

   protected void printXMLChar(int ch) throws IOException {
      if (ch == 13) {
         this.printHex(ch);
      } else if (ch == 60) {
         this._printer.printText("&lt;");
      } else if (ch == 38) {
         this._printer.printText("&amp;");
      } else if (ch == 62) {
         this._printer.printText("&gt;");
      } else if (ch != 10 && ch != 9 && (ch < 32 || !this._encodingInfo.isPrintable((char)ch))) {
         this.printHex(ch);
      } else {
         this._printer.printText((char)ch);
      }

   }

   protected void printText(String text, boolean preserveSpace, boolean unescaped) throws IOException {
      int length = text.length();
      int index;
      char ch;
      if (preserveSpace) {
         for(index = 0; index < length; ++index) {
            ch = text.charAt(index);
            if (!XMLChar.isValid(ch)) {
               ++index;
               if (index < length) {
                  this.surrogates(ch, text.charAt(index));
               } else {
                  this.fatalError("The character '" + ch + "' is an invalid XML character");
               }
            } else if (unescaped) {
               this._printer.printText(ch);
            } else {
               this.printXMLChar(ch);
            }
         }
      } else {
         for(index = 0; index < length; ++index) {
            ch = text.charAt(index);
            if (!XMLChar.isValid(ch)) {
               ++index;
               if (index < length) {
                  this.surrogates(ch, text.charAt(index));
               } else {
                  this.fatalError("The character '" + ch + "' is an invalid XML character");
               }
            } else if (unescaped) {
               this._printer.printText(ch);
            } else {
               this.printXMLChar(ch);
            }
         }
      }

   }

   protected void printText(char[] chars, int start, int length, boolean preserveSpace, boolean unescaped) throws IOException {
      char ch;
      if (preserveSpace) {
         while(length-- > 0) {
            ch = chars[start++];
            if (!XMLChar.isValid(ch)) {
               if (length-- > 0) {
                  this.surrogates(ch, chars[start++]);
               } else {
                  this.fatalError("The character '" + ch + "' is an invalid XML character");
               }
            } else if (unescaped) {
               this._printer.printText(ch);
            } else {
               this.printXMLChar(ch);
            }
         }
      } else {
         while(length-- > 0) {
            ch = chars[start++];
            if (!XMLChar.isValid(ch)) {
               if (length-- > 0) {
                  this.surrogates(ch, chars[start++]);
               } else {
                  this.fatalError("The character '" + ch + "' is an invalid XML character");
               }
            } else if (unescaped) {
               this._printer.printText(ch);
            } else {
               this.printXMLChar(ch);
            }
         }
      }

   }

   protected void checkUnboundNamespacePrefixedNode(Node node) throws IOException {
      Node next;
      if (this.fNamespaces) {
         for(Node child = node.getFirstChild(); child != null; child = next) {
            next = child.getNextSibling();
            String prefix = child.getPrefix();
            prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
            if (this.fNSBinder.getURI(prefix) == null && prefix != null) {
               this.fatalError("The replacement text of the entity node '" + node.getNodeName() + "' contains an element node '" + child.getNodeName() + "' with an undeclared prefix '" + prefix + "'.");
            }

            if (child.getNodeType() == 1) {
               NamedNodeMap attrs = child.getAttributes();

               for(int i = 0; i < attrs.getLength(); ++i) {
                  String attrPrefix = attrs.item(i).getPrefix();
                  attrPrefix = attrPrefix != null && attrPrefix.length() != 0 ? this.fSymbolTable.addSymbol(attrPrefix) : XMLSymbols.EMPTY_STRING;
                  if (this.fNSBinder.getURI(attrPrefix) == null && attrPrefix != null) {
                     this.fatalError("The replacement text of the entity node '" + node.getNodeName() + "' contains an element node '" + child.getNodeName() + "' with an attribute '" + attrs.item(i).getNodeName() + "' an undeclared prefix '" + attrPrefix + "'.");
                  }
               }
            }

            if (child.hasChildNodes()) {
               this.checkUnboundNamespacePrefixedNode(child);
            }
         }
      }

   }

   public boolean reset() {
      super.reset();
      if (this.fNSBinder != null) {
         this.fNSBinder.reset();
         this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
      }

      return true;
   }
}
