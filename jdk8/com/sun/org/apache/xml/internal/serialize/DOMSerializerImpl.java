package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.AbortException;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMNormalizer;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

public class DOMSerializerImpl implements LSSerializer, DOMConfiguration {
   private XMLSerializer serializer;
   private XML11Serializer xml11Serializer;
   private DOMStringList fRecognizedParameters;
   protected short features = 0;
   protected static final short NAMESPACES = 1;
   protected static final short WELLFORMED = 2;
   protected static final short ENTITIES = 4;
   protected static final short CDATA = 8;
   protected static final short SPLITCDATA = 16;
   protected static final short COMMENTS = 32;
   protected static final short DISCARDDEFAULT = 64;
   protected static final short INFOSET = 128;
   protected static final short XMLDECL = 256;
   protected static final short NSDECL = 512;
   protected static final short DOM_ELEMENT_CONTENT_WHITESPACE = 1024;
   protected static final short FORMAT_PRETTY_PRINT = 2048;
   private DOMErrorHandler fErrorHandler = null;
   private final DOMErrorImpl fError = new DOMErrorImpl();
   private final DOMLocatorImpl fLocator = new DOMLocatorImpl();

   public DOMSerializerImpl() {
      this.features = (short)(this.features | 1);
      this.features = (short)(this.features | 4);
      this.features = (short)(this.features | 32);
      this.features = (short)(this.features | 8);
      this.features = (short)(this.features | 16);
      this.features = (short)(this.features | 2);
      this.features = (short)(this.features | 512);
      this.features = (short)(this.features | 1024);
      this.features = (short)(this.features | 64);
      this.features = (short)(this.features | 256);
      this.serializer = new XMLSerializer();
      this.initSerializer(this.serializer);
   }

   public DOMConfiguration getDomConfig() {
      return this;
   }

   public void setParameter(String name, Object value) throws DOMException {
      if (value instanceof Boolean) {
         boolean state = (Boolean)value;
         if (name.equalsIgnoreCase("infoset")) {
            if (state) {
               this.features = (short)(this.features & -5);
               this.features = (short)(this.features & -9);
               this.features = (short)(this.features | 1);
               this.features = (short)(this.features | 512);
               this.features = (short)(this.features | 2);
               this.features = (short)(this.features | 32);
            }
         } else if (name.equalsIgnoreCase("xml-declaration")) {
            this.features = (short)(state ? this.features | 256 : this.features & -257);
         } else if (name.equalsIgnoreCase("namespaces")) {
            this.features = (short)(state ? this.features | 1 : this.features & -2);
            this.serializer.fNamespaces = state;
         } else if (name.equalsIgnoreCase("split-cdata-sections")) {
            this.features = (short)(state ? this.features | 16 : this.features & -17);
         } else if (name.equalsIgnoreCase("discard-default-content")) {
            this.features = (short)(state ? this.features | 64 : this.features & -65);
         } else if (name.equalsIgnoreCase("well-formed")) {
            this.features = (short)(state ? this.features | 2 : this.features & -3);
         } else if (name.equalsIgnoreCase("entities")) {
            this.features = (short)(state ? this.features | 4 : this.features & -5);
         } else if (name.equalsIgnoreCase("cdata-sections")) {
            this.features = (short)(state ? this.features | 8 : this.features & -9);
         } else if (name.equalsIgnoreCase("comments")) {
            this.features = (short)(state ? this.features | 32 : this.features & -33);
         } else if (name.equalsIgnoreCase("format-pretty-print")) {
            this.features = (short)(state ? this.features | 2048 : this.features & -2049);
         } else {
            String msg;
            if (!name.equalsIgnoreCase("canonical-form") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("validate") && !name.equalsIgnoreCase("check-character-normalization") && !name.equalsIgnoreCase("datatype-normalization")) {
               if (name.equalsIgnoreCase("namespace-declarations")) {
                  this.features = (short)(state ? this.features | 512 : this.features & -513);
                  this.serializer.fNamespacePrefixes = state;
               } else {
                  if (!name.equalsIgnoreCase("element-content-whitespace") && !name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                     msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{name});
                     throw new DOMException((short)9, msg);
                  }

                  if (!state) {
                     msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
                     throw new DOMException((short)9, msg);
                  }
               }
            } else if (state) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
               throw new DOMException((short)9, msg);
            }
         }
      } else {
         String msg;
         if (!name.equalsIgnoreCase("error-handler")) {
            if (!name.equalsIgnoreCase("resource-resolver") && !name.equalsIgnoreCase("schema-location") && !name.equalsIgnoreCase("schema-type") && (!name.equalsIgnoreCase("normalize-characters") || value == null)) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{name});
               throw new DOMException((short)8, msg);
            }

            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
            throw new DOMException((short)9, msg);
         }

         if (value != null && !(value instanceof DOMErrorHandler)) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
            throw new DOMException((short)17, msg);
         }

         this.fErrorHandler = (DOMErrorHandler)value;
      }

   }

   public boolean canSetParameter(String name, Object state) {
      if (state == null) {
         return true;
      } else if (state instanceof Boolean) {
         boolean value = (Boolean)state;
         if (!name.equalsIgnoreCase("namespaces") && !name.equalsIgnoreCase("split-cdata-sections") && !name.equalsIgnoreCase("discard-default-content") && !name.equalsIgnoreCase("xml-declaration") && !name.equalsIgnoreCase("well-formed") && !name.equalsIgnoreCase("infoset") && !name.equalsIgnoreCase("entities") && !name.equalsIgnoreCase("cdata-sections") && !name.equalsIgnoreCase("comments") && !name.equalsIgnoreCase("namespace-declarations") && !name.equalsIgnoreCase("format-pretty-print")) {
            if (!name.equalsIgnoreCase("canonical-form") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("validate") && !name.equalsIgnoreCase("check-character-normalization") && !name.equalsIgnoreCase("datatype-normalization")) {
               if (!name.equalsIgnoreCase("element-content-whitespace") && !name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                  return false;
               } else {
                  return value;
               }
            } else {
               return !value;
            }
         } else {
            return true;
         }
      } else if (name.equalsIgnoreCase("error-handler") && state == null || state instanceof DOMErrorHandler) {
         return true;
      } else {
         return false;
      }
   }

   public DOMStringList getParameterNames() {
      if (this.fRecognizedParameters == null) {
         Vector parameters = new Vector();
         parameters.add("namespaces");
         parameters.add("split-cdata-sections");
         parameters.add("discard-default-content");
         parameters.add("xml-declaration");
         parameters.add("canonical-form");
         parameters.add("validate-if-schema");
         parameters.add("validate");
         parameters.add("check-character-normalization");
         parameters.add("datatype-normalization");
         parameters.add("format-pretty-print");
         parameters.add("well-formed");
         parameters.add("infoset");
         parameters.add("namespace-declarations");
         parameters.add("element-content-whitespace");
         parameters.add("entities");
         parameters.add("cdata-sections");
         parameters.add("comments");
         parameters.add("ignore-unknown-character-denormalizations");
         parameters.add("error-handler");
         this.fRecognizedParameters = new DOMStringListImpl(parameters);
      }

      return this.fRecognizedParameters;
   }

   public Object getParameter(String name) throws DOMException {
      if (name.equalsIgnoreCase("normalize-characters")) {
         return null;
      } else if (name.equalsIgnoreCase("comments")) {
         return (this.features & 32) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("namespaces")) {
         return (this.features & 1) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("xml-declaration")) {
         return (this.features & 256) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("cdata-sections")) {
         return (this.features & 8) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("entities")) {
         return (this.features & 4) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("split-cdata-sections")) {
         return (this.features & 16) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("well-formed")) {
         return (this.features & 2) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("namespace-declarations")) {
         return (this.features & 512) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("format-pretty-print")) {
         return (this.features & 2048) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (!name.equalsIgnoreCase("element-content-whitespace") && !name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
         if (name.equalsIgnoreCase("discard-default-content")) {
            return (this.features & 64) != 0 ? Boolean.TRUE : Boolean.FALSE;
         } else if (name.equalsIgnoreCase("infoset")) {
            return (this.features & 4) == 0 && (this.features & 8) == 0 && (this.features & 1) != 0 && (this.features & 512) != 0 && (this.features & 2) != 0 && (this.features & 32) != 0 ? Boolean.TRUE : Boolean.FALSE;
         } else if (!name.equalsIgnoreCase("canonical-form") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("check-character-normalization") && !name.equalsIgnoreCase("validate") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("datatype-normalization")) {
            if (name.equalsIgnoreCase("error-handler")) {
               return this.fErrorHandler;
            } else {
               String msg;
               if (!name.equalsIgnoreCase("resource-resolver") && !name.equalsIgnoreCase("schema-location") && !name.equalsIgnoreCase("schema-type")) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{name});
                  throw new DOMException((short)8, msg);
               } else {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
                  throw new DOMException((short)9, msg);
               }
            }
         } else {
            return Boolean.FALSE;
         }
      } else {
         return Boolean.TRUE;
      }
   }

   public String writeToString(Node wnode) throws DOMException, LSException {
      Document doc = wnode.getNodeType() == 9 ? (Document)wnode : wnode.getOwnerDocument();
      java.lang.reflect.Method getVersion = null;
      XMLSerializer ser = null;
      String ver = null;

      try {
         getVersion = doc.getClass().getMethod("getXmlVersion");
         if (getVersion != null) {
            ver = (String)getVersion.invoke(doc, (Object[])null);
         }
      } catch (Exception var13) {
      }

      if (ver != null && ver.equals("1.1")) {
         if (this.xml11Serializer == null) {
            this.xml11Serializer = new XML11Serializer();
            this.initSerializer(this.xml11Serializer);
         }

         this.copySettings(this.serializer, this.xml11Serializer);
         ser = this.xml11Serializer;
      } else {
         ser = this.serializer;
      }

      StringWriter destination = new StringWriter();

      try {
         this.prepareForSerialization((XMLSerializer)ser, wnode);
         ((XMLSerializer)ser)._format.setEncoding("UTF-16");
         ((XMLSerializer)ser).setOutputCharStream(destination);
         if (wnode.getNodeType() == 9) {
            ((XMLSerializer)ser).serialize((Document)wnode);
         } else if (wnode.getNodeType() == 11) {
            ((XMLSerializer)ser).serialize((DocumentFragment)wnode);
         } else if (wnode.getNodeType() == 1) {
            ((XMLSerializer)ser).serialize((Element)wnode);
         } else {
            if (wnode.getNodeType() != 3 && wnode.getNodeType() != 8 && wnode.getNodeType() != 5 && wnode.getNodeType() != 4 && wnode.getNodeType() != 7) {
               String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unable-to-serialize-node", (Object[])null);
               if (((XMLSerializer)ser).fDOMErrorHandler != null) {
                  DOMErrorImpl error = new DOMErrorImpl();
                  error.fType = "unable-to-serialize-node";
                  error.fMessage = msg;
                  error.fSeverity = 3;
                  ((XMLSerializer)ser).fDOMErrorHandler.handleError(error);
               }

               throw new LSException((short)82, msg);
            }

            ((XMLSerializer)ser).serialize(wnode);
         }
      } catch (LSException var9) {
         throw var9;
      } catch (AbortException var10) {
         return null;
      } catch (RuntimeException var11) {
         throw (LSException)(new LSException((short)82, var11.toString())).initCause(var11);
      } catch (IOException var12) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "STRING_TOO_LONG", new Object[]{var12.getMessage()});
         throw (DOMException)(new DOMException((short)2, msg)).initCause(var12);
      }

      return destination.toString();
   }

   public void setNewLine(String newLine) {
      this.serializer._format.setLineSeparator(newLine);
   }

   public String getNewLine() {
      return this.serializer._format.getLineSeparator();
   }

   public LSSerializerFilter getFilter() {
      return this.serializer.fDOMFilter;
   }

   public void setFilter(LSSerializerFilter filter) {
      this.serializer.fDOMFilter = filter;
   }

   private void initSerializer(XMLSerializer ser) {
      ser.fNSBinder = new NamespaceSupport();
      ser.fLocalNSBinder = new NamespaceSupport();
      ser.fSymbolTable = new SymbolTable();
   }

   private void copySettings(XMLSerializer src, XMLSerializer dest) {
      dest.fDOMErrorHandler = this.fErrorHandler;
      dest._format.setEncoding(src._format.getEncoding());
      dest._format.setLineSeparator(src._format.getLineSeparator());
      dest.fDOMFilter = src.fDOMFilter;
   }

   public boolean write(Node node, LSOutput destination) throws LSException {
      if (node == null) {
         return false;
      } else {
         java.lang.reflect.Method getVersion = null;
         XMLSerializer ser = null;
         String ver = null;
         Document fDocument = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();

         try {
            getVersion = fDocument.getClass().getMethod("getXmlVersion");
            if (getVersion != null) {
               ver = (String)getVersion.invoke(fDocument, (Object[])null);
            }
         } catch (Exception var20) {
         }

         if (ver != null && ver.equals("1.1")) {
            if (this.xml11Serializer == null) {
               this.xml11Serializer = new XML11Serializer();
               this.initSerializer(this.xml11Serializer);
            }

            this.copySettings(this.serializer, this.xml11Serializer);
            ser = this.xml11Serializer;
         } else {
            ser = this.serializer;
         }

         String encoding = null;
         if ((encoding = destination.getEncoding()) == null) {
            java.lang.reflect.Method getEncoding;
            try {
               getEncoding = fDocument.getClass().getMethod("getInputEncoding");
               if (getEncoding != null) {
                  encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
               }
            } catch (Exception var19) {
            }

            if (encoding == null) {
               try {
                  getEncoding = fDocument.getClass().getMethod("getXmlEncoding");
                  if (getEncoding != null) {
                     encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
                  }
               } catch (Exception var18) {
               }

               if (encoding == null) {
                  encoding = "UTF-8";
               }
            }
         }

         DOMErrorImpl error;
         try {
            this.prepareForSerialization((XMLSerializer)ser, node);
            ((XMLSerializer)ser)._format.setEncoding(encoding);
            OutputStream outputStream = destination.getByteStream();
            Writer writer = destination.getCharacterStream();
            String uri = destination.getSystemId();
            if (writer == null) {
               if (outputStream == null) {
                  String msg;
                  if (uri == null) {
                     msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "no-output-specified", (Object[])null);
                     if (((XMLSerializer)ser).fDOMErrorHandler != null) {
                        DOMErrorImpl error = new DOMErrorImpl();
                        error.fType = "no-output-specified";
                        error.fMessage = msg;
                        error.fSeverity = 3;
                        ((XMLSerializer)ser).fDOMErrorHandler.handleError(error);
                     }

                     throw new LSException((short)82, msg);
                  }

                  msg = XMLEntityManager.expandSystemId(uri, (String)null, true);
                  URL url = new URL(msg != null ? msg : uri);
                  OutputStream out = null;
                  String protocol = url.getProtocol();
                  String host = url.getHost();
                  if (protocol.equals("file") && (host == null || host.length() == 0 || host.equals("localhost"))) {
                     out = new FileOutputStream(this.getPathWithoutEscapes(url.getFile()));
                  } else {
                     URLConnection urlCon = url.openConnection();
                     urlCon.setDoInput(false);
                     urlCon.setDoOutput(true);
                     urlCon.setUseCaches(false);
                     if (urlCon instanceof HttpURLConnection) {
                        HttpURLConnection httpCon = (HttpURLConnection)urlCon;
                        httpCon.setRequestMethod("PUT");
                     }

                     out = urlCon.getOutputStream();
                  }

                  ((XMLSerializer)ser).setOutputByteStream((OutputStream)out);
               } else {
                  ((XMLSerializer)ser).setOutputByteStream(outputStream);
               }
            } else {
               ((XMLSerializer)ser).setOutputCharStream(writer);
            }

            if (node.getNodeType() == 9) {
               ((XMLSerializer)ser).serialize((Document)node);
            } else if (node.getNodeType() == 11) {
               ((XMLSerializer)ser).serialize((DocumentFragment)node);
            } else if (node.getNodeType() == 1) {
               ((XMLSerializer)ser).serialize((Element)node);
            } else {
               if (node.getNodeType() != 3 && node.getNodeType() != 8 && node.getNodeType() != 5 && node.getNodeType() != 4 && node.getNodeType() != 7) {
                  return false;
               }

               ((XMLSerializer)ser).serialize(node);
            }

            return true;
         } catch (UnsupportedEncodingException var21) {
            if (((XMLSerializer)ser).fDOMErrorHandler != null) {
               error = new DOMErrorImpl();
               error.fException = var21;
               error.fType = "unsupported-encoding";
               error.fMessage = var21.getMessage();
               error.fSeverity = 3;
               ((XMLSerializer)ser).fDOMErrorHandler.handleError(error);
            }

            throw new LSException((short)82, DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unsupported-encoding", (Object[])null));
         } catch (LSException var22) {
            throw var22;
         } catch (AbortException var23) {
            return false;
         } catch (RuntimeException var24) {
            throw (LSException)DOMUtil.createLSException((short)82, var24).fillInStackTrace();
         } catch (Exception var25) {
            if (((XMLSerializer)ser).fDOMErrorHandler != null) {
               error = new DOMErrorImpl();
               error.fException = var25;
               error.fMessage = var25.getMessage();
               error.fSeverity = 2;
               ((XMLSerializer)ser).fDOMErrorHandler.handleError(error);
            }

            throw (LSException)DOMUtil.createLSException((short)82, var25).fillInStackTrace();
         }
      }
   }

   public boolean writeToURI(Node node, String URI) throws LSException {
      if (node == null) {
         return false;
      } else {
         java.lang.reflect.Method getXmlVersion = null;
         XMLSerializer ser = null;
         String ver = null;
         String encoding = null;
         Document fDocument = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();

         try {
            getXmlVersion = fDocument.getClass().getMethod("getXmlVersion");
            if (getXmlVersion != null) {
               ver = (String)getXmlVersion.invoke(fDocument, (Object[])null);
            }
         } catch (Exception var17) {
         }

         if (ver != null && ver.equals("1.1")) {
            if (this.xml11Serializer == null) {
               this.xml11Serializer = new XML11Serializer();
               this.initSerializer(this.xml11Serializer);
            }

            this.copySettings(this.serializer, this.xml11Serializer);
            ser = this.xml11Serializer;
         } else {
            ser = this.serializer;
         }

         java.lang.reflect.Method getEncoding;
         try {
            getEncoding = fDocument.getClass().getMethod("getInputEncoding");
            if (getEncoding != null) {
               encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
            }
         } catch (Exception var16) {
         }

         if (encoding == null) {
            try {
               getEncoding = fDocument.getClass().getMethod("getXmlEncoding");
               if (getEncoding != null) {
                  encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
               }
            } catch (Exception var15) {
            }

            if (encoding == null) {
               encoding = "UTF-8";
            }
         }

         try {
            this.prepareForSerialization((XMLSerializer)ser, node);
            ((XMLSerializer)ser)._format.setEncoding(encoding);
            String expanded = XMLEntityManager.expandSystemId(URI, (String)null, true);
            URL url = new URL(expanded != null ? expanded : URI);
            OutputStream out = null;
            String protocol = url.getProtocol();
            String host = url.getHost();
            if (!protocol.equals("file") || host != null && host.length() != 0 && !host.equals("localhost")) {
               URLConnection urlCon = url.openConnection();
               urlCon.setDoInput(false);
               urlCon.setDoOutput(true);
               urlCon.setUseCaches(false);
               if (urlCon instanceof HttpURLConnection) {
                  HttpURLConnection httpCon = (HttpURLConnection)urlCon;
                  httpCon.setRequestMethod("PUT");
               }

               out = urlCon.getOutputStream();
            } else {
               out = new FileOutputStream(this.getPathWithoutEscapes(url.getFile()));
            }

            ((XMLSerializer)ser).setOutputByteStream((OutputStream)out);
            if (node.getNodeType() == 9) {
               ((XMLSerializer)ser).serialize((Document)node);
            } else if (node.getNodeType() == 11) {
               ((XMLSerializer)ser).serialize((DocumentFragment)node);
            } else if (node.getNodeType() == 1) {
               ((XMLSerializer)ser).serialize((Element)node);
            } else {
               if (node.getNodeType() != 3 && node.getNodeType() != 8 && node.getNodeType() != 5 && node.getNodeType() != 4 && node.getNodeType() != 7) {
                  return false;
               }

               ((XMLSerializer)ser).serialize(node);
            }

            return true;
         } catch (LSException var18) {
            throw var18;
         } catch (AbortException var19) {
            return false;
         } catch (RuntimeException var20) {
            throw (LSException)DOMUtil.createLSException((short)82, var20).fillInStackTrace();
         } catch (Exception var21) {
            if (((XMLSerializer)ser).fDOMErrorHandler != null) {
               DOMErrorImpl error = new DOMErrorImpl();
               error.fException = var21;
               error.fMessage = var21.getMessage();
               error.fSeverity = 2;
               ((XMLSerializer)ser).fDOMErrorHandler.handleError(error);
            }

            throw (LSException)DOMUtil.createLSException((short)82, var21).fillInStackTrace();
         }
      }
   }

   private void prepareForSerialization(XMLSerializer ser, Node node) {
      ser.reset();
      ser.features = this.features;
      ser.fDOMErrorHandler = this.fErrorHandler;
      ser.fNamespaces = (this.features & 1) != 0;
      ser.fNamespacePrefixes = (this.features & 512) != 0;
      ser._format.setOmitComments((this.features & 32) == 0);
      ser._format.setOmitXMLDeclaration((this.features & 256) == 0);
      ser._format.setIndenting((this.features & 2048) != 0);
      if ((this.features & 2) != 0) {
         Node root = node;
         boolean verifyNames = true;
         Document document = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();

         try {
            java.lang.reflect.Method versionChanged = document.getClass().getMethod("isXMLVersionChanged()");
            if (versionChanged != null) {
               verifyNames = (Boolean)versionChanged.invoke(document, (Object[])null);
            }
         } catch (Exception var9) {
         }

         Node next;
         if (node.getFirstChild() == null) {
            this.verify(node, verifyNames, false);
         } else {
            for(; node != null; node = next) {
               this.verify(node, verifyNames, false);
               next = node.getFirstChild();

               while(next == null) {
                  next = node.getNextSibling();
                  if (next == null) {
                     node = node.getParentNode();
                     if (root == node) {
                        next = null;
                        break;
                     }

                     next = node.getNextSibling();
                  }
               }
            }
         }
      }

   }

   private void verify(Node node, boolean verifyNames, boolean xml11Version) {
      int type = node.getNodeType();
      this.fLocator.fRelatedNode = node;
      boolean wellformed;
      switch(type) {
      case 1:
         if (verifyNames) {
            if ((this.features & 1) != 0) {
               wellformed = CoreDocumentImpl.isValidQName(node.getPrefix(), node.getLocalName(), xml11Version);
            } else {
               wellformed = CoreDocumentImpl.isXMLName(node.getNodeName(), xml11Version);
            }

            if (!wellformed && !wellformed && this.fErrorHandler != null) {
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[]{"Element", node.getNodeName()});
               DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "wf-invalid-character-in-node-name");
            }
         }

         NamedNodeMap attributes = node.hasAttributes() ? node.getAttributes() : null;
         if (attributes != null) {
            for(int i = 0; i < attributes.getLength(); ++i) {
               Attr attr = (Attr)attributes.item(i);
               this.fLocator.fRelatedNode = attr;
               DOMNormalizer.isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributes, attr, attr.getValue(), xml11Version);
               if (verifyNames) {
                  wellformed = CoreDocumentImpl.isXMLName(attr.getNodeName(), xml11Version);
                  if (!wellformed) {
                     String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[]{"Attr", node.getNodeName()});
                     DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "wf-invalid-character-in-node-name");
                  }
               }
            }
         }
      case 2:
      case 6:
      case 9:
      case 10:
      default:
         break;
      case 3:
         DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), xml11Version);
         break;
      case 4:
         DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), xml11Version);
         break;
      case 5:
         if (verifyNames && (this.features & 4) != 0) {
            CoreDocumentImpl.isXMLName(node.getNodeName(), xml11Version);
         }
         break;
      case 7:
         ProcessingInstruction pinode = (ProcessingInstruction)node;
         String target = pinode.getTarget();
         if (verifyNames) {
            if (xml11Version) {
               wellformed = XML11Char.isXML11ValidName(target);
            } else {
               wellformed = XMLChar.isValidName(target);
            }

            if (!wellformed) {
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[]{"Element", node.getNodeName()});
               DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "wf-invalid-character-in-node-name");
            }
         }

         DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, pinode.getData(), xml11Version);
         break;
      case 8:
         if ((this.features & 32) != 0) {
            DOMNormalizer.isCommentWF(this.fErrorHandler, this.fError, this.fLocator, ((Comment)node).getData(), xml11Version);
         }
      }

   }

   private String getPathWithoutEscapes(String origPath) {
      if (origPath != null && origPath.length() != 0 && origPath.indexOf(37) != -1) {
         StringTokenizer tokenizer = new StringTokenizer(origPath, "%");
         StringBuffer result = new StringBuffer(origPath.length());
         int size = tokenizer.countTokens();
         result.append(tokenizer.nextToken());

         for(int i = 1; i < size; ++i) {
            String token = tokenizer.nextToken();
            result.append((char)Integer.valueOf(token.substring(0, 2), 16));
            result.append(token.substring(2));
         }

         return result.toString();
      } else {
         return origPath;
      }
   }
}
