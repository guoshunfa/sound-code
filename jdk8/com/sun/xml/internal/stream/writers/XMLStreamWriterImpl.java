package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;

public final class XMLStreamWriterImpl extends AbstractMap implements XMLStreamWriter {
   public static final String START_COMMENT = "<!--";
   public static final String END_COMMENT = "-->";
   public static final String DEFAULT_ENCODING = " encoding=\"utf-8\"";
   public static final String DEFAULT_XMLDECL = "<?xml version=\"1.0\" ?>";
   public static final String DEFAULT_XML_VERSION = "1.0";
   public static final char CLOSE_START_TAG = '>';
   public static final char OPEN_START_TAG = '<';
   public static final String OPEN_END_TAG = "</";
   public static final char CLOSE_END_TAG = '>';
   public static final String START_CDATA = "<![CDATA[";
   public static final String END_CDATA = "]]>";
   public static final String CLOSE_EMPTY_ELEMENT = "/>";
   public static final String SPACE = " ";
   public static final String UTF_8 = "UTF-8";
   public static final String OUTPUTSTREAM_PROPERTY = "sjsxp-outputstream";
   boolean fEscapeCharacters;
   private boolean fIsRepairingNamespace;
   private Writer fWriter;
   private OutputStream fOutputStream;
   private ArrayList fAttributeCache;
   private ArrayList fNamespaceDecls;
   private XMLStreamWriterImpl.NamespaceContextImpl fNamespaceContext;
   private NamespaceSupport fInternalNamespaceContext;
   private Random fPrefixGen;
   private PropertyManager fPropertyManager;
   private boolean fStartTagOpened;
   private boolean fReuse;
   private SymbolTable fSymbolTable;
   private XMLStreamWriterImpl.ElementStack fElementStack;
   private final String DEFAULT_PREFIX;
   private final ReadOnlyIterator fReadOnlyIterator;
   private CharsetEncoder fEncoder;
   HashMap fAttrNamespace;

   public XMLStreamWriterImpl(OutputStream outputStream, PropertyManager props) throws IOException {
      this((Writer)(new OutputStreamWriter(outputStream)), props);
   }

   public XMLStreamWriterImpl(OutputStream outputStream, String encoding, PropertyManager props) throws IOException {
      this(new StreamResult(outputStream), encoding, props);
   }

   public XMLStreamWriterImpl(Writer writer, PropertyManager props) throws IOException {
      this((StreamResult)(new StreamResult(writer)), (String)null, props);
   }

   public XMLStreamWriterImpl(StreamResult sr, String encoding, PropertyManager props) throws IOException {
      this.fEscapeCharacters = true;
      this.fIsRepairingNamespace = false;
      this.fOutputStream = null;
      this.fNamespaceContext = null;
      this.fInternalNamespaceContext = null;
      this.fPrefixGen = null;
      this.fPropertyManager = null;
      this.fStartTagOpened = false;
      this.fSymbolTable = new SymbolTable();
      this.fElementStack = new XMLStreamWriterImpl.ElementStack();
      this.DEFAULT_PREFIX = this.fSymbolTable.addSymbol("");
      this.fReadOnlyIterator = new ReadOnlyIterator();
      this.fEncoder = null;
      this.fAttrNamespace = null;
      this.setOutput(sr, encoding);
      this.fPropertyManager = props;
      this.init();
   }

   private void init() {
      this.fReuse = false;
      this.fNamespaceDecls = new ArrayList();
      this.fPrefixGen = new Random();
      this.fAttributeCache = new ArrayList();
      this.fInternalNamespaceContext = new NamespaceSupport();
      this.fInternalNamespaceContext.reset();
      this.fNamespaceContext = new XMLStreamWriterImpl.NamespaceContextImpl();
      this.fNamespaceContext.internalContext = this.fInternalNamespaceContext;
      Boolean ob = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
      this.fIsRepairingNamespace = ob;
      ob = (Boolean)this.fPropertyManager.getProperty("escapeCharacters");
      this.setEscapeCharacters(ob);
   }

   public void reset() {
      this.reset(false);
   }

   void reset(boolean resetProperties) {
      if (!this.fReuse) {
         throw new IllegalStateException("close() Must be called before calling reset()");
      } else {
         this.fReuse = false;
         this.fNamespaceDecls.clear();
         this.fAttributeCache.clear();
         this.fElementStack.clear();
         this.fInternalNamespaceContext.reset();
         this.fStartTagOpened = false;
         this.fNamespaceContext.userContext = null;
         if (resetProperties) {
            Boolean ob = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
            this.fIsRepairingNamespace = ob;
            ob = (Boolean)this.fPropertyManager.getProperty("escapeCharacters");
            this.setEscapeCharacters(ob);
         }

      }
   }

   public void setOutput(StreamResult sr, String encoding) throws IOException {
      if (sr.getOutputStream() != null) {
         this.setOutputUsingStream(sr.getOutputStream(), encoding);
      } else if (sr.getWriter() != null) {
         this.setOutputUsingWriter(sr.getWriter());
      } else if (sr.getSystemId() != null) {
         this.setOutputUsingStream(new FileOutputStream(sr.getSystemId()), encoding);
      }

   }

   private void setOutputUsingWriter(Writer writer) throws IOException {
      this.fWriter = writer;
      if (writer instanceof OutputStreamWriter) {
         String charset = ((OutputStreamWriter)writer).getEncoding();
         if (charset != null && !charset.equalsIgnoreCase("utf-8")) {
            this.fEncoder = Charset.forName(charset).newEncoder();
         }
      }

   }

   private void setOutputUsingStream(OutputStream os, String encoding) throws IOException {
      this.fOutputStream = os;
      if (encoding != null) {
         if (encoding.equalsIgnoreCase("utf-8")) {
            this.fWriter = new UTF8OutputStreamWriter(os);
         } else {
            this.fWriter = new XMLWriter(new OutputStreamWriter(os, encoding));
            this.fEncoder = Charset.forName(encoding).newEncoder();
         }
      } else {
         encoding = SecuritySupport.getSystemProperty("file.encoding");
         if (encoding != null && encoding.equalsIgnoreCase("utf-8")) {
            this.fWriter = new UTF8OutputStreamWriter(os);
         } else {
            this.fWriter = new XMLWriter(new OutputStreamWriter(os));
         }
      }

   }

   public boolean canReuse() {
      return this.fReuse;
   }

   public void setEscapeCharacters(boolean escape) {
      this.fEscapeCharacters = escape;
   }

   public boolean getEscapeCharacters() {
      return this.fEscapeCharacters;
   }

   public void close() throws XMLStreamException {
      if (this.fWriter != null) {
         try {
            this.fWriter.flush();
         } catch (IOException var2) {
            throw new XMLStreamException(var2);
         }
      }

      this.fWriter = null;
      this.fOutputStream = null;
      this.fNamespaceDecls.clear();
      this.fAttributeCache.clear();
      this.fElementStack.clear();
      this.fInternalNamespaceContext.reset();
      this.fReuse = true;
      this.fStartTagOpened = false;
      this.fNamespaceContext.userContext = null;
   }

   public void flush() throws XMLStreamException {
      try {
         this.fWriter.flush();
      } catch (IOException var2) {
         throw new XMLStreamException(var2);
      }
   }

   public NamespaceContext getNamespaceContext() {
      return this.fNamespaceContext;
   }

   public String getPrefix(String uri) throws XMLStreamException {
      return this.fNamespaceContext.getPrefix(uri);
   }

   public Object getProperty(String str) throws IllegalArgumentException {
      if (str == null) {
         throw new NullPointerException();
      } else if (!this.fPropertyManager.containsProperty(str)) {
         throw new IllegalArgumentException("Property '" + str + "' is not supported");
      } else {
         return this.fPropertyManager.getProperty(str);
      }
   }

   public void setDefaultNamespace(String uri) throws XMLStreamException {
      if (uri != null) {
         uri = this.fSymbolTable.addSymbol(uri);
      }

      if (this.fIsRepairingNamespace) {
         if (this.isDefaultNamespace(uri)) {
            return;
         }

         QName qname = new QName();
         qname.setValues(this.DEFAULT_PREFIX, "xmlns", (String)null, uri);
         this.fNamespaceDecls.add(qname);
      } else {
         this.fInternalNamespaceContext.declarePrefix(this.DEFAULT_PREFIX, uri);
      }

   }

   public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
      this.fNamespaceContext.userContext = namespaceContext;
   }

   public void setPrefix(String prefix, String uri) throws XMLStreamException {
      if (prefix == null) {
         throw new XMLStreamException("Prefix cannot be null");
      } else if (uri == null) {
         throw new XMLStreamException("URI cannot be null");
      } else {
         prefix = this.fSymbolTable.addSymbol(prefix);
         uri = this.fSymbolTable.addSymbol(uri);
         if (this.fIsRepairingNamespace) {
            String tmpURI = this.fInternalNamespaceContext.getURI(prefix);
            if (tmpURI == null || tmpURI != uri) {
               if (!this.checkUserNamespaceContext(prefix, uri)) {
                  QName qname = new QName();
                  qname.setValues(prefix, "xmlns", (String)null, uri);
                  this.fNamespaceDecls.add(qname);
               }
            }
         } else {
            this.fInternalNamespaceContext.declarePrefix(prefix, uri);
         }
      }
   }

   public void writeAttribute(String localName, String value) throws XMLStreamException {
      try {
         if (!this.fStartTagOpened) {
            throw new XMLStreamException("Attribute not associated with any element");
         } else if (this.fIsRepairingNamespace) {
            XMLStreamWriterImpl.Attribute attr = new XMLStreamWriterImpl.Attribute(value);
            attr.setValues((String)null, localName, (String)null, (String)null);
            this.fAttributeCache.add(attr);
         } else {
            this.fWriter.write(" ");
            this.fWriter.write(localName);
            this.fWriter.write("=\"");
            this.writeXMLContent(value, true, true);
            this.fWriter.write("\"");
         }
      } catch (IOException var4) {
         throw new XMLStreamException(var4);
      }
   }

   public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
      try {
         if (!this.fStartTagOpened) {
            throw new XMLStreamException("Attribute not associated with any element");
         } else if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         } else {
            namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
            String prefix = this.fInternalNamespaceContext.getPrefix(namespaceURI);
            if (!this.fIsRepairingNamespace) {
               if (prefix == null) {
                  throw new XMLStreamException("Prefix cannot be null");
               }

               this.writeAttributeWithPrefix(prefix, localName, value);
            } else {
               XMLStreamWriterImpl.Attribute attr = new XMLStreamWriterImpl.Attribute(value);
               attr.setValues((String)null, localName, (String)null, namespaceURI);
               this.fAttributeCache.add(attr);
            }

         }
      } catch (IOException var6) {
         throw new XMLStreamException(var6);
      }
   }

   private void writeAttributeWithPrefix(String prefix, String localName, String value) throws IOException {
      this.fWriter.write(" ");
      if (prefix != null && prefix != "") {
         this.fWriter.write(prefix);
         this.fWriter.write(":");
      }

      this.fWriter.write(localName);
      this.fWriter.write("=\"");
      this.writeXMLContent(value, true, true);
      this.fWriter.write("\"");
   }

   public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
      try {
         if (!this.fStartTagOpened) {
            throw new XMLStreamException("Attribute not associated with any element");
         } else if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         } else if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
         } else {
            if (!this.fIsRepairingNamespace) {
               if (prefix == null || prefix.equals("")) {
                  if (!namespaceURI.equals("")) {
                     throw new XMLStreamException("prefix cannot be null or empty");
                  } else {
                     this.writeAttributeWithPrefix((String)null, localName, value);
                     return;
                  }
               }

               if (!prefix.equals("xml") || !namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
                  prefix = this.fSymbolTable.addSymbol(prefix);
                  namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
                  if (this.fInternalNamespaceContext.containsPrefixInCurrentContext(prefix)) {
                     String tmpURI = this.fInternalNamespaceContext.getURI(prefix);
                     if (tmpURI != null && tmpURI != namespaceURI) {
                        throw new XMLStreamException("Prefix " + prefix + " is already bound to " + tmpURI + ". Trying to rebind it to " + namespaceURI + " is an error.");
                     }
                  }

                  this.fInternalNamespaceContext.declarePrefix(prefix, namespaceURI);
               }

               this.writeAttributeWithPrefix(prefix, localName, value);
            } else {
               if (prefix != null) {
                  prefix = this.fSymbolTable.addSymbol(prefix);
               }

               namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
               XMLStreamWriterImpl.Attribute attr = new XMLStreamWriterImpl.Attribute(value);
               attr.setValues(prefix, localName, (String)null, namespaceURI);
               this.fAttributeCache.add(attr);
            }

         }
      } catch (IOException var6) {
         throw new XMLStreamException(var6);
      }
   }

   public void writeCData(String cdata) throws XMLStreamException {
      try {
         if (cdata == null) {
            throw new XMLStreamException("cdata cannot be null");
         } else {
            if (this.fStartTagOpened) {
               this.closeStartTag();
            }

            this.fWriter.write("<![CDATA[");
            this.fWriter.write(cdata);
            this.fWriter.write("]]>");
         }
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeCharacters(String data) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         this.writeXMLContent(data);
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeCharacters(char[] data, int start, int len) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         this.writeXMLContent(data, start, len, this.fEscapeCharacters);
      } catch (IOException var5) {
         throw new XMLStreamException(var5);
      }
   }

   public void writeComment(String comment) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         this.fWriter.write("<!--");
         if (comment != null) {
            this.fWriter.write(comment);
         }

         this.fWriter.write("-->");
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeDTD(String dtd) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         this.fWriter.write(dtd);
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
      String namespaceURINormalized = null;
      if (namespaceURI == null) {
         namespaceURINormalized = "";
      } else {
         namespaceURINormalized = namespaceURI;
      }

      try {
         if (!this.fStartTagOpened) {
            throw new IllegalStateException("Namespace Attribute not associated with any element");
         } else if (this.fIsRepairingNamespace) {
            QName qname = new QName();
            qname.setValues("", "xmlns", (String)null, namespaceURINormalized);
            this.fNamespaceDecls.add(qname);
         } else {
            namespaceURINormalized = this.fSymbolTable.addSymbol(namespaceURINormalized);
            if (this.fInternalNamespaceContext.containsPrefixInCurrentContext("")) {
               String tmp = this.fInternalNamespaceContext.getURI("");
               if (tmp != null && tmp != namespaceURINormalized) {
                  throw new XMLStreamException("xmlns has been already bound to " + tmp + ". Rebinding it to " + namespaceURINormalized + " is an error");
               }
            }

            this.fInternalNamespaceContext.declarePrefix("", namespaceURINormalized);
            this.writenamespace((String)null, namespaceURINormalized);
         }
      } catch (IOException var4) {
         throw new XMLStreamException(var4);
      }
   }

   public void writeEmptyElement(String localName) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         this.openStartTag();
         this.fElementStack.push((String)null, localName, (String)null, (String)null, true);
         this.fInternalNamespaceContext.pushContext();
         if (!this.fIsRepairingNamespace) {
            this.fWriter.write(localName);
         }

      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
      if (namespaceURI == null) {
         throw new XMLStreamException("NamespaceURI cannot be null");
      } else {
         namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
         String prefix = this.fNamespaceContext.getPrefix(namespaceURI);
         this.writeEmptyElement(prefix, localName, namespaceURI);
      }
   }

   public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      try {
         if (localName == null) {
            throw new XMLStreamException("Local Name cannot be null");
         } else if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         } else {
            if (prefix != null) {
               prefix = this.fSymbolTable.addSymbol(prefix);
            }

            namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
            if (this.fStartTagOpened) {
               this.closeStartTag();
            }

            this.openStartTag();
            this.fElementStack.push(prefix, localName, (String)null, namespaceURI, true);
            this.fInternalNamespaceContext.pushContext();
            if (!this.fIsRepairingNamespace) {
               if (prefix == null) {
                  throw new XMLStreamException("NamespaceURI " + namespaceURI + " has not been bound to any prefix");
               } else {
                  if (prefix != null && prefix != "") {
                     this.fWriter.write(prefix);
                     this.fWriter.write(":");
                  }

                  this.fWriter.write(localName);
               }
            }
         }
      } catch (IOException var5) {
         throw new XMLStreamException(var5);
      }
   }

   public void writeEndDocument() throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         XMLStreamWriterImpl.ElementState elem = null;

         while(!this.fElementStack.empty()) {
            elem = this.fElementStack.pop();
            this.fInternalNamespaceContext.popContext();
            if (!elem.isEmpty) {
               this.fWriter.write("</");
               if (elem.prefix != null && !elem.prefix.equals("")) {
                  this.fWriter.write(elem.prefix);
                  this.fWriter.write(":");
               }

               this.fWriter.write(elem.localpart);
               this.fWriter.write(62);
            }
         }

      } catch (IOException var2) {
         throw new XMLStreamException(var2);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new XMLStreamException("No more elements to write");
      }
   }

   public void writeEndElement() throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         XMLStreamWriterImpl.ElementState currentElement = this.fElementStack.pop();
         if (currentElement == null) {
            throw new XMLStreamException("No element was found to write");
         } else if (!currentElement.isEmpty) {
            this.fWriter.write("</");
            if (currentElement.prefix != null && !currentElement.prefix.equals("")) {
               this.fWriter.write(currentElement.prefix);
               this.fWriter.write(":");
            }

            this.fWriter.write(currentElement.localpart);
            this.fWriter.write(62);
            this.fInternalNamespaceContext.popContext();
         }
      } catch (IOException var2) {
         throw new XMLStreamException(var2);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new XMLStreamException("No element was found to write: " + var3.toString(), var3);
      }
   }

   public void writeEntityRef(String refName) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         this.fWriter.write(38);
         this.fWriter.write(refName);
         this.fWriter.write(59);
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
      String namespaceURINormalized = null;
      if (namespaceURI == null) {
         namespaceURINormalized = "";
      } else {
         namespaceURINormalized = namespaceURI;
      }

      try {
         QName qname = null;
         if (!this.fStartTagOpened) {
            throw new IllegalStateException("Invalid state: start tag is not opened at writeNamespace(" + prefix + ", " + namespaceURINormalized + ")");
         } else if (prefix != null && !prefix.equals("") && !prefix.equals("xmlns")) {
            if (!prefix.equals("xml") || !namespaceURINormalized.equals("http://www.w3.org/XML/1998/namespace")) {
               prefix = this.fSymbolTable.addSymbol(prefix);
               namespaceURINormalized = this.fSymbolTable.addSymbol(namespaceURINormalized);
               String tmp;
               if (this.fIsRepairingNamespace) {
                  tmp = this.fInternalNamespaceContext.getURI(prefix);
                  if (tmp == null || tmp != namespaceURINormalized) {
                     qname = new QName();
                     qname.setValues(prefix, "xmlns", (String)null, namespaceURINormalized);
                     this.fNamespaceDecls.add(qname);
                  }
               } else {
                  if (this.fInternalNamespaceContext.containsPrefixInCurrentContext(prefix)) {
                     tmp = this.fInternalNamespaceContext.getURI(prefix);
                     if (tmp != null && tmp != namespaceURINormalized) {
                        throw new XMLStreamException("prefix " + prefix + " has been already bound to " + tmp + ". Rebinding it to " + namespaceURINormalized + " is an error");
                     }
                  }

                  this.fInternalNamespaceContext.declarePrefix(prefix, namespaceURINormalized);
                  this.writenamespace(prefix, namespaceURINormalized);
               }
            }
         } else {
            this.writeDefaultNamespace(namespaceURINormalized);
         }
      } catch (IOException var6) {
         throw new XMLStreamException(var6);
      }
   }

   private void writenamespace(String prefix, String namespaceURI) throws IOException {
      this.fWriter.write(" xmlns");
      if (prefix != null && prefix != "") {
         this.fWriter.write(":");
         this.fWriter.write(prefix);
      }

      this.fWriter.write("=\"");
      this.writeXMLContent(namespaceURI, true, true);
      this.fWriter.write("\"");
   }

   public void writeProcessingInstruction(String target) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         if (target != null) {
            this.fWriter.write("<?");
            this.fWriter.write(target);
            this.fWriter.write("?>");
            return;
         }
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }

      throw new XMLStreamException("PI target cannot be null");
   }

   public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
      try {
         if (this.fStartTagOpened) {
            this.closeStartTag();
         }

         if (target != null && data != null) {
            this.fWriter.write("<?");
            this.fWriter.write(target);
            this.fWriter.write(" ");
            this.fWriter.write(data);
            this.fWriter.write("?>");
         } else {
            throw new XMLStreamException("PI target cannot be null");
         }
      } catch (IOException var4) {
         throw new XMLStreamException(var4);
      }
   }

   public void writeStartDocument() throws XMLStreamException {
      try {
         this.fWriter.write("<?xml version=\"1.0\" ?>");
      } catch (IOException var2) {
         throw new XMLStreamException(var2);
      }
   }

   public void writeStartDocument(String version) throws XMLStreamException {
      try {
         if (version != null && !version.equals("")) {
            this.fWriter.write("<?xml version=\"");
            this.fWriter.write(version);
            this.fWriter.write("\"");
            this.fWriter.write("?>");
         } else {
            this.writeStartDocument();
         }
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeStartDocument(String encoding, String version) throws XMLStreamException {
      try {
         if (encoding == null && version == null) {
            this.writeStartDocument();
         } else if (encoding == null) {
            this.writeStartDocument(version);
         } else {
            String streamEncoding = null;
            if (this.fWriter instanceof OutputStreamWriter) {
               streamEncoding = ((OutputStreamWriter)this.fWriter).getEncoding();
            } else if (this.fWriter instanceof UTF8OutputStreamWriter) {
               streamEncoding = ((UTF8OutputStreamWriter)this.fWriter).getEncoding();
            } else if (this.fWriter instanceof XMLWriter) {
               streamEncoding = ((OutputStreamWriter)((XMLWriter)this.fWriter).getWriter()).getEncoding();
            }

            if (streamEncoding != null && !streamEncoding.equalsIgnoreCase(encoding)) {
               boolean foundAlias = false;
               Set aliases = Charset.forName(encoding).aliases();
               Iterator it = aliases.iterator();

               while(!foundAlias && it.hasNext()) {
                  if (streamEncoding.equalsIgnoreCase((String)it.next())) {
                     foundAlias = true;
                  }
               }

               if (!foundAlias) {
                  throw new XMLStreamException("Underlying stream encoding '" + streamEncoding + "' and input paramter for writeStartDocument() method '" + encoding + "' do not match.");
               }
            }

            this.fWriter.write("<?xml version=\"");
            if (version != null && !version.equals("")) {
               this.fWriter.write(version);
            } else {
               this.fWriter.write("1.0");
            }

            if (!encoding.equals("")) {
               this.fWriter.write("\" encoding=\"");
               this.fWriter.write(encoding);
            }

            this.fWriter.write("\"?>");
         }
      } catch (IOException var7) {
         throw new XMLStreamException(var7);
      }
   }

   public void writeStartElement(String localName) throws XMLStreamException {
      try {
         if (localName == null) {
            throw new XMLStreamException("Local Name cannot be null");
         } else {
            if (this.fStartTagOpened) {
               this.closeStartTag();
            }

            this.openStartTag();
            this.fElementStack.push((String)null, localName, (String)null, (String)null, false);
            this.fInternalNamespaceContext.pushContext();
            if (!this.fIsRepairingNamespace) {
               this.fWriter.write(localName);
            }
         }
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
      if (localName == null) {
         throw new XMLStreamException("Local Name cannot be null");
      } else if (namespaceURI == null) {
         throw new XMLStreamException("NamespaceURI cannot be null");
      } else {
         namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
         String prefix = null;
         if (!this.fIsRepairingNamespace) {
            prefix = this.fNamespaceContext.getPrefix(namespaceURI);
            if (prefix != null) {
               prefix = this.fSymbolTable.addSymbol(prefix);
            }
         }

         this.writeStartElement(prefix, localName, namespaceURI);
      }
   }

   public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      try {
         if (localName == null) {
            throw new XMLStreamException("Local Name cannot be null");
         } else if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         } else if (!this.fIsRepairingNamespace && prefix == null) {
            throw new XMLStreamException("Prefix cannot be null");
         } else {
            if (this.fStartTagOpened) {
               this.closeStartTag();
            }

            this.openStartTag();
            namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
            if (prefix != null) {
               prefix = this.fSymbolTable.addSymbol(prefix);
            }

            this.fElementStack.push(prefix, localName, (String)null, namespaceURI, false);
            this.fInternalNamespaceContext.pushContext();
            String tmpPrefix = this.fNamespaceContext.getPrefix(namespaceURI);
            if (prefix != null && (tmpPrefix == null || !prefix.equals(tmpPrefix))) {
               this.fInternalNamespaceContext.declarePrefix(prefix, namespaceURI);
            }

            if (!this.fIsRepairingNamespace) {
               if (prefix != null && prefix != "") {
                  this.fWriter.write(prefix);
                  this.fWriter.write(":");
               }

               this.fWriter.write(localName);
            } else if (prefix != null && (tmpPrefix == null || !prefix.equals(tmpPrefix))) {
               QName qname = new QName();
               qname.setValues(prefix, "xmlns", (String)null, namespaceURI);
               this.fNamespaceDecls.add(qname);
            }
         }
      } catch (IOException var6) {
         throw new XMLStreamException(var6);
      }
   }

   private void writeCharRef(int codePoint) throws IOException {
      this.fWriter.write("&#x");
      this.fWriter.write(Integer.toHexString(codePoint));
      this.fWriter.write(59);
   }

   private void writeXMLContent(char[] content, int start, int length, boolean escapeChars) throws IOException {
      if (!escapeChars) {
         this.fWriter.write(content, start, length);
      } else {
         int startWritePos = start;
         int end = start + length;

         for(int index = start; index < end; ++index) {
            char ch = content[index];
            if (this.fEncoder != null && !this.fEncoder.canEncode(ch)) {
               this.fWriter.write(content, startWritePos, index - startWritePos);
               if (index != end - 1 && Character.isSurrogatePair(ch, content[index + 1])) {
                  this.writeCharRef(Character.toCodePoint(ch, content[index + 1]));
                  ++index;
               } else {
                  this.writeCharRef(ch);
               }

               startWritePos = index + 1;
            } else {
               switch(ch) {
               case '&':
                  this.fWriter.write(content, startWritePos, index - startWritePos);
                  this.fWriter.write("&amp;");
                  startWritePos = index + 1;
                  break;
               case '<':
                  this.fWriter.write(content, startWritePos, index - startWritePos);
                  this.fWriter.write("&lt;");
                  startWritePos = index + 1;
                  break;
               case '>':
                  this.fWriter.write(content, startWritePos, index - startWritePos);
                  this.fWriter.write("&gt;");
                  startWritePos = index + 1;
               }
            }
         }

         this.fWriter.write(content, startWritePos, end - startWritePos);
      }
   }

   private void writeXMLContent(String content) throws IOException {
      if (content != null && content.length() > 0) {
         this.writeXMLContent(content, this.fEscapeCharacters, false);
      }

   }

   private void writeXMLContent(String content, boolean escapeChars, boolean escapeDoubleQuotes) throws IOException {
      if (!escapeChars) {
         this.fWriter.write(content);
      } else {
         int startWritePos = 0;
         int end = content.length();

         for(int index = 0; index < end; ++index) {
            char ch = content.charAt(index);
            if (this.fEncoder != null && !this.fEncoder.canEncode(ch)) {
               this.fWriter.write(content, startWritePos, index - startWritePos);
               if (index != end - 1 && Character.isSurrogatePair(ch, content.charAt(index + 1))) {
                  this.writeCharRef(Character.toCodePoint(ch, content.charAt(index + 1)));
                  ++index;
               } else {
                  this.writeCharRef(ch);
               }

               startWritePos = index + 1;
            } else {
               switch(ch) {
               case '"':
                  this.fWriter.write(content, startWritePos, index - startWritePos);
                  if (escapeDoubleQuotes) {
                     this.fWriter.write("&quot;");
                  } else {
                     this.fWriter.write(34);
                  }

                  startWritePos = index + 1;
                  break;
               case '&':
                  this.fWriter.write(content, startWritePos, index - startWritePos);
                  this.fWriter.write("&amp;");
                  startWritePos = index + 1;
                  break;
               case '<':
                  this.fWriter.write(content, startWritePos, index - startWritePos);
                  this.fWriter.write("&lt;");
                  startWritePos = index + 1;
                  break;
               case '>':
                  this.fWriter.write(content, startWritePos, index - startWritePos);
                  this.fWriter.write("&gt;");
                  startWritePos = index + 1;
               }
            }
         }

         this.fWriter.write(content, startWritePos, end - startWritePos);
      }
   }

   private void closeStartTag() throws XMLStreamException {
      try {
         XMLStreamWriterImpl.ElementState currentElement = this.fElementStack.peek();
         if (this.fIsRepairingNamespace) {
            this.repair();
            this.correctPrefix(currentElement, 1);
            if (currentElement.prefix != null && currentElement.prefix != "") {
               this.fWriter.write(currentElement.prefix);
               this.fWriter.write(":");
            }

            this.fWriter.write(currentElement.localpart);
            int len = this.fNamespaceDecls.size();
            QName qname = null;

            for(int i = 0; i < len; ++i) {
               qname = (QName)this.fNamespaceDecls.get(i);
               if (qname != null && this.fInternalNamespaceContext.declarePrefix(qname.prefix, qname.uri)) {
                  this.writenamespace(qname.prefix, qname.uri);
               }
            }

            this.fNamespaceDecls.clear();
            XMLStreamWriterImpl.Attribute attr = null;

            for(int j = 0; j < this.fAttributeCache.size(); ++j) {
               attr = (XMLStreamWriterImpl.Attribute)this.fAttributeCache.get(j);
               if (attr.prefix != null && attr.uri != null && !attr.prefix.equals("") && !attr.uri.equals("")) {
                  String tmp = this.fInternalNamespaceContext.getPrefix(attr.uri);
                  if (tmp == null || tmp != attr.prefix) {
                     tmp = this.getAttrPrefix(attr.uri);
                     if (tmp == null) {
                        if (this.fInternalNamespaceContext.declarePrefix(attr.prefix, attr.uri)) {
                           this.writenamespace(attr.prefix, attr.uri);
                        }
                     } else {
                        this.writenamespace(attr.prefix, attr.uri);
                     }
                  }
               }

               this.writeAttributeWithPrefix(attr.prefix, attr.localpart, attr.value);
            }

            this.fAttrNamespace = null;
            this.fAttributeCache.clear();
         }

         if (currentElement.isEmpty) {
            this.fElementStack.pop();
            this.fInternalNamespaceContext.popContext();
            this.fWriter.write("/>");
         } else {
            this.fWriter.write(62);
         }

         this.fStartTagOpened = false;
      } catch (IOException var7) {
         this.fStartTagOpened = false;
         throw new XMLStreamException(var7);
      }
   }

   private void openStartTag() throws IOException {
      this.fStartTagOpened = true;
      this.fWriter.write(60);
   }

   private void correctPrefix(QName attr, int type) {
      String tmpPrefix = null;
      String prefix = attr.prefix;
      String uri = attr.uri;
      boolean isSpecialCaseURI = false;
      if (prefix == null || prefix.equals("")) {
         if (uri == null) {
            return;
         }

         if (prefix == "" && uri == "") {
            return;
         }

         uri = this.fSymbolTable.addSymbol(uri);
         QName decl = null;

         for(int i = 0; i < this.fNamespaceDecls.size(); ++i) {
            decl = (QName)this.fNamespaceDecls.get(i);
            if (decl != null && decl.uri == attr.uri) {
               attr.prefix = decl.prefix;
               return;
            }
         }

         tmpPrefix = this.fNamespaceContext.getPrefix(uri);
         if (tmpPrefix == "") {
            if (type == 1) {
               return;
            }

            if (type == 10) {
               tmpPrefix = this.getAttrPrefix(uri);
               isSpecialCaseURI = true;
            }
         }

         if (tmpPrefix != null) {
            prefix = this.fSymbolTable.addSymbol(tmpPrefix);
         } else {
            StringBuffer genPrefix = new StringBuffer("zdef");

            for(int i = 0; i < 1; ++i) {
               genPrefix.append(this.fPrefixGen.nextInt());
            }

            prefix = genPrefix.toString();
            prefix = this.fSymbolTable.addSymbol(prefix);
         }

         if (tmpPrefix == null) {
            if (isSpecialCaseURI) {
               this.addAttrNamespace(prefix, uri);
            } else {
               QName qname = new QName();
               qname.setValues(prefix, "xmlns", (String)null, uri);
               this.fNamespaceDecls.add(qname);
               this.fInternalNamespaceContext.declarePrefix(this.fSymbolTable.addSymbol(prefix), uri);
            }
         }
      }

      attr.prefix = prefix;
   }

   private String getAttrPrefix(String uri) {
      return this.fAttrNamespace != null ? (String)this.fAttrNamespace.get(uri) : null;
   }

   private void addAttrNamespace(String prefix, String uri) {
      if (this.fAttrNamespace == null) {
         this.fAttrNamespace = new HashMap();
      }

      this.fAttrNamespace.put(prefix, uri);
   }

   private boolean isDefaultNamespace(String uri) {
      String defaultNamespace = this.fInternalNamespaceContext.getURI(this.DEFAULT_PREFIX);
      return uri == defaultNamespace;
   }

   private boolean checkUserNamespaceContext(String prefix, String uri) {
      if (this.fNamespaceContext.userContext != null) {
         String tmpURI = this.fNamespaceContext.userContext.getNamespaceURI(prefix);
         if (tmpURI != null && tmpURI.equals(uri)) {
            return true;
         }
      }

      return false;
   }

   protected void repair() {
      XMLStreamWriterImpl.Attribute attr = null;
      XMLStreamWriterImpl.Attribute attr2 = null;
      XMLStreamWriterImpl.ElementState currentElement = this.fElementStack.peek();
      this.removeDuplicateDecls();

      int i;
      for(i = 0; i < this.fAttributeCache.size(); ++i) {
         attr = (XMLStreamWriterImpl.Attribute)this.fAttributeCache.get(i);
         if (attr.prefix != null && !attr.prefix.equals("") || attr.uri != null && !attr.uri.equals("")) {
            this.correctPrefix(currentElement, attr);
         }
      }

      if (!this.isDeclared(currentElement) && currentElement.prefix != null && currentElement.uri != null && !currentElement.prefix.equals("") && !currentElement.uri.equals("")) {
         this.fNamespaceDecls.add(currentElement);
      }

      for(i = 0; i < this.fAttributeCache.size(); ++i) {
         attr = (XMLStreamWriterImpl.Attribute)this.fAttributeCache.get(i);

         for(int j = i + 1; j < this.fAttributeCache.size(); ++j) {
            attr2 = (XMLStreamWriterImpl.Attribute)this.fAttributeCache.get(j);
            if (!"".equals(attr.prefix) && !"".equals(attr2.prefix)) {
               this.correctPrefix(attr, attr2);
            }
         }
      }

      this.repairNamespaceDecl(currentElement);
      int i = false;

      for(i = 0; i < this.fAttributeCache.size(); ++i) {
         attr = (XMLStreamWriterImpl.Attribute)this.fAttributeCache.get(i);
         if (attr.prefix != null && attr.prefix.equals("") && attr.uri != null && attr.uri.equals("")) {
            this.repairNamespaceDecl(attr);
         }
      }

      QName qname = null;

      for(i = 0; i < this.fNamespaceDecls.size(); ++i) {
         qname = (QName)this.fNamespaceDecls.get(i);
         if (qname != null) {
            this.fInternalNamespaceContext.declarePrefix(qname.prefix, qname.uri);
         }
      }

      for(i = 0; i < this.fAttributeCache.size(); ++i) {
         attr = (XMLStreamWriterImpl.Attribute)this.fAttributeCache.get(i);
         this.correctPrefix(attr, 10);
      }

   }

   void correctPrefix(QName attr1, QName attr2) {
      String tmpPrefix = null;
      QName decl = null;
      boolean done = false;
      this.checkForNull(attr1);
      this.checkForNull(attr2);
      if (attr1.prefix.equals(attr2.prefix) && !attr1.uri.equals(attr2.uri)) {
         tmpPrefix = this.fNamespaceContext.getPrefix(attr2.uri);
         if (tmpPrefix != null) {
            attr2.prefix = this.fSymbolTable.addSymbol(tmpPrefix);
         } else {
            decl = null;

            for(int n = 0; n < this.fNamespaceDecls.size(); ++n) {
               decl = (QName)this.fNamespaceDecls.get(n);
               if (decl != null && decl.uri == attr2.uri) {
                  attr2.prefix = decl.prefix;
                  return;
               }
            }

            StringBuffer genPrefix = new StringBuffer("zdef");

            for(int k = 0; k < 1; ++k) {
               genPrefix.append(this.fPrefixGen.nextInt());
            }

            tmpPrefix = genPrefix.toString();
            tmpPrefix = this.fSymbolTable.addSymbol(tmpPrefix);
            attr2.prefix = tmpPrefix;
            QName qname = new QName();
            qname.setValues(tmpPrefix, "xmlns", (String)null, attr2.uri);
            this.fNamespaceDecls.add(qname);
         }
      }

   }

   void checkForNull(QName attr) {
      if (attr.prefix == null) {
         attr.prefix = "";
      }

      if (attr.uri == null) {
         attr.uri = "";
      }

   }

   void removeDuplicateDecls() {
      for(int i = 0; i < this.fNamespaceDecls.size(); ++i) {
         QName decl1 = (QName)this.fNamespaceDecls.get(i);
         if (decl1 != null) {
            for(int j = i + 1; j < this.fNamespaceDecls.size(); ++j) {
               QName decl2 = (QName)this.fNamespaceDecls.get(j);
               if (decl2 != null && decl1.prefix.equals(decl2.prefix) && decl1.uri.equals(decl2.uri)) {
                  this.fNamespaceDecls.remove(j);
               }
            }
         }
      }

   }

   void repairNamespaceDecl(QName attr) {
      QName decl = null;

      for(int j = 0; j < this.fNamespaceDecls.size(); ++j) {
         decl = (QName)this.fNamespaceDecls.get(j);
         if (decl != null && attr.prefix != null && attr.prefix.equals(decl.prefix) && !attr.uri.equals(decl.uri)) {
            String tmpURI = this.fNamespaceContext.getNamespaceURI(attr.prefix);
            if (tmpURI != null) {
               if (tmpURI.equals(attr.uri)) {
                  this.fNamespaceDecls.set(j, (Object)null);
               } else {
                  decl.uri = attr.uri;
               }
            }
         }
      }

   }

   boolean isDeclared(QName attr) {
      QName decl = null;

      for(int n = 0; n < this.fNamespaceDecls.size(); ++n) {
         decl = (QName)this.fNamespaceDecls.get(n);
         if (attr.prefix != null && attr.prefix == decl.prefix && decl.uri == attr.uri) {
            return true;
         }
      }

      if (attr.uri != null && this.fNamespaceContext.getPrefix(attr.uri) != null) {
         return true;
      } else {
         return false;
      }
   }

   public int size() {
      return 1;
   }

   public boolean isEmpty() {
      return false;
   }

   public boolean containsKey(Object key) {
      return key.equals("sjsxp-outputstream");
   }

   public Object get(Object key) {
      return key.equals("sjsxp-outputstream") ? this.fOutputStream : null;
   }

   public Set entrySet() {
      throw new UnsupportedOperationException();
   }

   public String toString() {
      return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
   }

   public int hashCode() {
      return this.fElementStack.hashCode();
   }

   public boolean equals(Object obj) {
      return this == obj;
   }

   class NamespaceContextImpl implements NamespaceContext {
      NamespaceContext userContext = null;
      NamespaceSupport internalContext = null;

      public String getNamespaceURI(String prefix) {
         String uri = null;
         if (prefix != null) {
            prefix = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(prefix);
         }

         if (this.internalContext != null) {
            uri = this.internalContext.getURI(prefix);
            if (uri != null) {
               return uri;
            }
         }

         if (this.userContext != null) {
            uri = this.userContext.getNamespaceURI(prefix);
            return uri;
         } else {
            return null;
         }
      }

      public String getPrefix(String uri) {
         String prefix = null;
         if (uri != null) {
            uri = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(uri);
         }

         if (this.internalContext != null) {
            prefix = this.internalContext.getPrefix(uri);
            if (prefix != null) {
               return prefix;
            }
         }

         return this.userContext != null ? this.userContext.getPrefix(uri) : null;
      }

      public Iterator getPrefixes(String uri) {
         Vector prefixes = null;
         Iterator itr = null;
         if (uri != null) {
            uri = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(uri);
         }

         if (this.userContext != null) {
            itr = this.userContext.getPrefixes(uri);
         }

         if (this.internalContext != null) {
            prefixes = this.internalContext.getPrefixes(uri);
         }

         if (prefixes == null && itr != null) {
            return itr;
         } else if (prefixes != null && itr == null) {
            return new ReadOnlyIterator(prefixes.iterator());
         } else if (prefixes != null && itr != null) {
            String ob = null;

            while(itr.hasNext()) {
               ob = (String)itr.next();
               if (ob != null) {
                  ob = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(ob);
               }

               if (!prefixes.contains(ob)) {
                  prefixes.add(ob);
               }
            }

            return new ReadOnlyIterator(prefixes.iterator());
         } else {
            return XMLStreamWriterImpl.this.fReadOnlyIterator;
         }
      }
   }

   class Attribute extends QName {
      String value;

      Attribute(String value) {
         this.value = value;
      }
   }

   class ElementState extends QName {
      public boolean isEmpty = false;

      public ElementState() {
      }

      public ElementState(String prefix, String localpart, String rawname, String uri) {
         super(prefix, localpart, rawname, uri);
      }

      public void setValues(String prefix, String localpart, String rawname, String uri, boolean isEmpty) {
         super.setValues(prefix, localpart, rawname, uri);
         this.isEmpty = isEmpty;
      }
   }

   protected class ElementStack {
      protected XMLStreamWriterImpl.ElementState[] fElements = new XMLStreamWriterImpl.ElementState[10];
      protected short fDepth;

      public ElementStack() {
         for(int i = 0; i < this.fElements.length; ++i) {
            this.fElements[i] = XMLStreamWriterImpl.this.new ElementState();
         }

      }

      public XMLStreamWriterImpl.ElementState push(XMLStreamWriterImpl.ElementState element) {
         if (this.fDepth == this.fElements.length) {
            XMLStreamWriterImpl.ElementState[] array = new XMLStreamWriterImpl.ElementState[this.fElements.length * 2];
            System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
            this.fElements = array;

            for(int i = this.fDepth; i < this.fElements.length; ++i) {
               this.fElements[i] = XMLStreamWriterImpl.this.new ElementState();
            }
         }

         this.fElements[this.fDepth].setValues(element);
         XMLStreamWriterImpl.ElementState[] var10000 = this.fElements;
         short var10003 = this.fDepth;
         this.fDepth = (short)(var10003 + 1);
         return var10000[var10003];
      }

      public XMLStreamWriterImpl.ElementState push(String prefix, String localpart, String rawname, String uri, boolean isEmpty) {
         if (this.fDepth == this.fElements.length) {
            XMLStreamWriterImpl.ElementState[] array = new XMLStreamWriterImpl.ElementState[this.fElements.length * 2];
            System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
            this.fElements = array;

            for(int i = this.fDepth; i < this.fElements.length; ++i) {
               this.fElements[i] = XMLStreamWriterImpl.this.new ElementState();
            }
         }

         this.fElements[this.fDepth].setValues(prefix, localpart, rawname, uri, isEmpty);
         XMLStreamWriterImpl.ElementState[] var10000 = this.fElements;
         short var10003 = this.fDepth;
         this.fDepth = (short)(var10003 + 1);
         return var10000[var10003];
      }

      public XMLStreamWriterImpl.ElementState pop() {
         return this.fElements[--this.fDepth];
      }

      public void clear() {
         this.fDepth = 0;
      }

      public XMLStreamWriterImpl.ElementState peek() {
         return this.fElements[this.fDepth - 1];
      }

      public boolean empty() {
         return this.fDepth <= 0;
      }
   }
}
