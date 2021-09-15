package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.BMMimeMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParameterList;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.SharedInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public abstract class MessageImpl extends SOAPMessage implements SOAPConstants {
   public static final String CONTENT_ID = "Content-ID";
   public static final String CONTENT_LOCATION = "Content-Location";
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
   protected static final int PLAIN_XML_FLAG = 1;
   protected static final int MIME_MULTIPART_FLAG = 2;
   protected static final int SOAP1_1_FLAG = 4;
   protected static final int SOAP1_2_FLAG = 8;
   protected static final int MIME_MULTIPART_XOP_SOAP1_1_FLAG = 6;
   protected static final int MIME_MULTIPART_XOP_SOAP1_2_FLAG = 10;
   protected static final int XOP_FLAG = 13;
   protected static final int FI_ENCODED_FLAG = 16;
   protected MimeHeaders headers;
   protected ContentType contentType;
   protected SOAPPartImpl soapPartImpl;
   protected FinalArrayList attachments;
   protected boolean saved;
   protected byte[] messageBytes;
   protected int messageByteCount;
   protected HashMap properties;
   protected MimeMultipart multiPart;
   protected boolean attachmentsInitialized;
   protected boolean isFastInfoset;
   protected boolean acceptFastInfoset;
   protected MimeMultipart mmp;
   private boolean optimizeAttachmentProcessing;
   private InputStream inputStreamAfterSaveChanges;
   private static boolean switchOffBM = false;
   private static boolean switchOffLazyAttachment = false;
   private static boolean useMimePull = false;
   private boolean lazyAttachments;
   private static final Iterator nullIter;

   private static boolean isSoap1_1Type(String primary, String sub) {
      return primary.equalsIgnoreCase("text") && sub.equalsIgnoreCase("xml") || primary.equalsIgnoreCase("text") && sub.equalsIgnoreCase("xml-soap") || primary.equals("application") && sub.equals("fastinfoset");
   }

   private static boolean isEqualToSoap1_1Type(String type) {
      return type.startsWith("text/xml") || type.startsWith("application/fastinfoset");
   }

   private static boolean isSoap1_2Type(String primary, String sub) {
      return primary.equals("application") && (sub.equals("soap+xml") || sub.equals("soap+fastinfoset"));
   }

   private static boolean isEqualToSoap1_2Type(String type) {
      return type.startsWith("application/soap+xml") || type.startsWith("application/soap+fastinfoset");
   }

   protected MessageImpl() {
      this(false, false);
      this.attachmentsInitialized = true;
   }

   protected MessageImpl(boolean isFastInfoset, boolean acceptFastInfoset) {
      this.saved = false;
      this.properties = new HashMap();
      this.multiPart = null;
      this.attachmentsInitialized = false;
      this.isFastInfoset = false;
      this.acceptFastInfoset = false;
      this.mmp = null;
      this.optimizeAttachmentProcessing = true;
      this.inputStreamAfterSaveChanges = null;
      this.lazyAttachments = false;
      this.isFastInfoset = isFastInfoset;
      this.acceptFastInfoset = acceptFastInfoset;
      this.headers = new MimeHeaders();
      this.headers.setHeader("Accept", this.getExpectedAcceptHeader());
      this.contentType = new ContentType();
   }

   protected MessageImpl(SOAPMessage msg) {
      this.saved = false;
      this.properties = new HashMap();
      this.multiPart = null;
      this.attachmentsInitialized = false;
      this.isFastInfoset = false;
      this.acceptFastInfoset = false;
      this.mmp = null;
      this.optimizeAttachmentProcessing = true;
      this.inputStreamAfterSaveChanges = null;
      this.lazyAttachments = false;
      if (!(msg instanceof MessageImpl)) {
      }

      MessageImpl src = (MessageImpl)msg;
      this.headers = src.headers;
      this.soapPartImpl = src.soapPartImpl;
      this.attachments = src.attachments;
      this.saved = src.saved;
      this.messageBytes = src.messageBytes;
      this.messageByteCount = src.messageByteCount;
      this.properties = src.properties;
      this.contentType = src.contentType;
   }

   protected static boolean isSoap1_1Content(int stat) {
      return (stat & 4) != 0;
   }

   protected static boolean isSoap1_2Content(int stat) {
      return (stat & 8) != 0;
   }

   private static boolean isMimeMultipartXOPSoap1_2Package(ContentType contentType) {
      String type = contentType.getParameter("type");
      if (type == null) {
         return false;
      } else {
         type = type.toLowerCase();
         if (!type.startsWith("application/xop+xml")) {
            return false;
         } else {
            String startinfo = contentType.getParameter("start-info");
            if (startinfo == null) {
               return false;
            } else {
               startinfo = startinfo.toLowerCase();
               return isEqualToSoap1_2Type(startinfo);
            }
         }
      }
   }

   private static boolean isMimeMultipartXOPSoap1_1Package(ContentType contentType) {
      String type = contentType.getParameter("type");
      if (type == null) {
         return false;
      } else {
         type = type.toLowerCase();
         if (!type.startsWith("application/xop+xml")) {
            return false;
         } else {
            String startinfo = contentType.getParameter("start-info");
            if (startinfo == null) {
               return false;
            } else {
               startinfo = startinfo.toLowerCase();
               return isEqualToSoap1_1Type(startinfo);
            }
         }
      }
   }

   private static boolean isSOAPBodyXOPPackage(ContentType contentType) {
      String primary = contentType.getPrimaryType();
      String sub = contentType.getSubType();
      if (primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("xop+xml")) {
         String type = getTypeParameter(contentType);
         return isEqualToSoap1_2Type(type) || isEqualToSoap1_1Type(type);
      } else {
         return false;
      }
   }

   protected MessageImpl(MimeHeaders headers, InputStream in) throws SOAPExceptionImpl {
      this.saved = false;
      this.properties = new HashMap();
      this.multiPart = null;
      this.attachmentsInitialized = false;
      this.isFastInfoset = false;
      this.acceptFastInfoset = false;
      this.mmp = null;
      this.optimizeAttachmentProcessing = true;
      this.inputStreamAfterSaveChanges = null;
      this.lazyAttachments = false;
      this.contentType = parseContentType(headers);
      this.init(headers, identifyContentType(this.contentType), this.contentType, in);
   }

   private static ContentType parseContentType(MimeHeaders headers) throws SOAPExceptionImpl {
      if (headers != null) {
         String ct = getContentType(headers);
         if (ct == null) {
            log.severe("SAAJ0532.soap.no.Content-Type");
            throw new SOAPExceptionImpl("Absent Content-Type");
         } else {
            try {
               return new ContentType(ct);
            } catch (Throwable var3) {
               log.severe("SAAJ0535.soap.cannot.internalize.message");
               throw new SOAPExceptionImpl("Unable to internalize message", var3);
            }
         }
      } else {
         log.severe("SAAJ0550.soap.null.headers");
         throw new SOAPExceptionImpl("Cannot create message: Headers can't be null");
      }
   }

   protected MessageImpl(MimeHeaders headers, ContentType contentType, int stat, InputStream in) throws SOAPExceptionImpl {
      this.saved = false;
      this.properties = new HashMap();
      this.multiPart = null;
      this.attachmentsInitialized = false;
      this.isFastInfoset = false;
      this.acceptFastInfoset = false;
      this.mmp = null;
      this.optimizeAttachmentProcessing = true;
      this.inputStreamAfterSaveChanges = null;
      this.lazyAttachments = false;
      this.init(headers, stat, contentType, in);
   }

   private void init(MimeHeaders headers, int stat, final ContentType contentType, final InputStream in) throws SOAPExceptionImpl {
      this.headers = headers;

      try {
         if ((stat & 16) > 0) {
            this.isFastInfoset = this.acceptFastInfoset = true;
         }

         StringTokenizer soapMessagePart;
         if (!this.isFastInfoset) {
            String[] values = headers.getHeader("Accept");
            if (values != null) {
               label152:
               for(int i = 0; i < values.length; ++i) {
                  soapMessagePart = new StringTokenizer(values[i], ",");

                  String token;
                  do {
                     if (!soapMessagePart.hasMoreTokens()) {
                        continue label152;
                     }

                     token = soapMessagePart.nextToken().trim();
                  } while(!token.equalsIgnoreCase("application/fastinfoset") && !token.equalsIgnoreCase("application/soap+fastinfoset"));

                  this.acceptFastInfoset = true;
               }
            }
         }

         if (!this.isCorrectSoapVersion(stat)) {
            log.log(Level.SEVERE, (String)"SAAJ0533.soap.incorrect.Content-Type", (Object[])(new String[]{contentType.toString(), this.getExpectedContentType()}));
            throw new SOAPVersionMismatchException("Cannot create message: incorrect content-type for SOAP version. Got: " + contentType + " Expected: " + this.getExpectedContentType());
         }

         if ((stat & 1) != 0) {
            if (this.isFastInfoset) {
               this.getSOAPPart().setContent(FastInfosetReflection.FastInfosetSource_new(in));
            } else {
               this.initCharsetProperty(contentType);
               this.getSOAPPart().setContent(new StreamSource(in));
            }
         } else {
            if ((stat & 2) == 0) {
               log.severe("SAAJ0534.soap.unknown.Content-Type");
               throw new SOAPExceptionImpl("Unrecognized Content-Type");
            }

            DataSource ds = new DataSource() {
               public InputStream getInputStream() {
                  return in;
               }

               public OutputStream getOutputStream() {
                  return null;
               }

               public String getContentType() {
                  return contentType.toString();
               }

               public String getName() {
                  return "";
               }
            };
            this.multiPart = null;
            if (useMimePull) {
               this.multiPart = new MimePullMultipart(ds, contentType);
            } else if (switchOffBM) {
               this.multiPart = new MimeMultipart(ds, contentType);
            } else {
               this.multiPart = new BMMimeMultipart(ds, contentType);
            }

            String startParam = contentType.getParameter("start");
            soapMessagePart = null;
            InputStream soapPartInputStream = null;
            String contentID = null;
            String contentIDNoAngle = null;
            MimeBodyPart soapMessagePart;
            if (!switchOffBM && !switchOffLazyAttachment) {
               if (useMimePull) {
                  MimePullMultipart mpMultipart = (MimePullMultipart)this.multiPart;
                  MIMEPart sp = mpMultipart.readAndReturnSOAPPart();
                  soapMessagePart = new MimeBodyPart(sp);
                  soapPartInputStream = sp.readOnce();
               } else {
                  BMMimeMultipart bmMultipart = (BMMimeMultipart)this.multiPart;
                  InputStream stream = bmMultipart.initStream();
                  SharedInputStream sin = null;
                  if (stream instanceof SharedInputStream) {
                     sin = (SharedInputStream)stream;
                  }

                  String boundary = "--" + contentType.getParameter("boundary");
                  byte[] bndbytes = ASCIIUtility.getBytes(boundary);
                  if (startParam == null) {
                     soapMessagePart = bmMultipart.getNextPart(stream, bndbytes, sin);
                     bmMultipart.removeBodyPart(soapMessagePart);
                  } else {
                     MimeBodyPart bp = null;

                     try {
                        while(!startParam.equals(contentID) && !startParam.equals(contentIDNoAngle)) {
                           bp = bmMultipart.getNextPart(stream, bndbytes, sin);
                           contentID = bp.getContentID();
                           contentIDNoAngle = contentID != null ? contentID.replaceFirst("^<", "").replaceFirst(">$", "") : null;
                        }

                        soapMessagePart = bp;
                        bmMultipart.removeBodyPart(bp);
                     } catch (Exception var18) {
                        throw new SOAPExceptionImpl(var18);
                     }
                  }
               }
            } else {
               int i;
               if (startParam == null) {
                  soapMessagePart = this.multiPart.getBodyPart(0);

                  for(i = 1; i < this.multiPart.getCount(); ++i) {
                     this.initializeAttachment(this.multiPart, i);
                  }
               } else {
                  soapMessagePart = this.multiPart.getBodyPart(startParam);

                  for(i = 0; i < this.multiPart.getCount(); ++i) {
                     contentID = this.multiPart.getBodyPart(i).getContentID();
                     contentIDNoAngle = contentID != null ? contentID.replaceFirst("^<", "").replaceFirst(">$", "") : null;
                     if (!startParam.equals(contentID) && !startParam.equals(contentIDNoAngle)) {
                        this.initializeAttachment(this.multiPart, i);
                     }
                  }
               }
            }

            if (soapPartInputStream == null && soapMessagePart != null) {
               soapPartInputStream = soapMessagePart.getInputStream();
            }

            ContentType soapPartCType = new ContentType(soapMessagePart.getContentType());
            this.initCharsetProperty(soapPartCType);
            String baseType = soapPartCType.getBaseType().toLowerCase();
            if (!isEqualToSoap1_1Type(baseType) && !isEqualToSoap1_2Type(baseType) && !isSOAPBodyXOPPackage(soapPartCType)) {
               log.log(Level.SEVERE, "SAAJ0549.soap.part.invalid.Content-Type", new Object[]{baseType});
               throw new SOAPExceptionImpl("Bad Content-Type for SOAP Part : " + baseType);
            }

            SOAPPart soapPart = this.getSOAPPart();
            this.setMimeHeaders(soapPart, soapMessagePart);
            soapPart.setContent((Source)(this.isFastInfoset ? FastInfosetReflection.FastInfosetSource_new(soapPartInputStream) : new StreamSource(soapPartInputStream)));
         }
      } catch (Throwable var19) {
         log.severe("SAAJ0535.soap.cannot.internalize.message");
         throw new SOAPExceptionImpl("Unable to internalize message", var19);
      }

      this.needsSave();
   }

   public boolean isFastInfoset() {
      return this.isFastInfoset;
   }

   public boolean acceptFastInfoset() {
      return this.acceptFastInfoset;
   }

   public void setIsFastInfoset(boolean value) {
      if (value != this.isFastInfoset) {
         this.isFastInfoset = value;
         if (this.isFastInfoset) {
            this.acceptFastInfoset = true;
         }

         this.saved = false;
      }

   }

   public Object getProperty(String property) {
      return (String)this.properties.get(property);
   }

   public void setProperty(String property, Object value) {
      this.verify(property, value);
      this.properties.put(property, value);
   }

   private void verify(String property, Object value) {
      if (property.equalsIgnoreCase("javax.xml.soap.write-xml-declaration")) {
         if (!"true".equals(value) && !"false".equals(value)) {
            throw new RuntimeException(property + " must have value false or true");
         } else {
            try {
               EnvelopeImpl env = (EnvelopeImpl)this.getSOAPPart().getEnvelope();
               if ("true".equalsIgnoreCase((String)value)) {
                  env.setOmitXmlDecl("no");
               } else if ("false".equalsIgnoreCase((String)value)) {
                  env.setOmitXmlDecl("yes");
               }

            } catch (Exception var4) {
               log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[]{var4.getMessage(), "javax.xml.soap.write-xml-declaration"});
               throw new RuntimeException(var4);
            }
         }
      } else {
         if (property.equalsIgnoreCase("javax.xml.soap.character-set-encoding")) {
            try {
               ((EnvelopeImpl)this.getSOAPPart().getEnvelope()).setCharsetEncoding((String)value);
            } catch (Exception var5) {
               log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[]{var5.getMessage(), "javax.xml.soap.character-set-encoding"});
               throw new RuntimeException(var5);
            }
         }

      }
   }

   protected abstract boolean isCorrectSoapVersion(int var1);

   protected abstract String getExpectedContentType();

   protected abstract String getExpectedAcceptHeader();

   static int identifyContentType(ContentType ct) throws SOAPExceptionImpl {
      String primary = ct.getPrimaryType().toLowerCase();
      String sub = ct.getSubType().toLowerCase();
      if (primary.equals("multipart")) {
         if (sub.equals("related")) {
            String type = getTypeParameter(ct);
            if (isEqualToSoap1_1Type(type)) {
               return (type.equals("application/fastinfoset") ? 16 : 0) | 2 | 4;
            } else if (isEqualToSoap1_2Type(type)) {
               return (type.equals("application/soap+fastinfoset") ? 16 : 0) | 2 | 8;
            } else if (isMimeMultipartXOPSoap1_1Package(ct)) {
               return 6;
            } else if (isMimeMultipartXOPSoap1_2Package(ct)) {
               return 10;
            } else {
               log.severe("SAAJ0536.soap.content-type.mustbe.multipart");
               throw new SOAPExceptionImpl("Content-Type needs to be Multipart/Related and with \"type=text/xml\" or \"type=application/soap+xml\"");
            }
         } else {
            log.severe("SAAJ0537.soap.invalid.content-type");
            throw new SOAPExceptionImpl("Invalid Content-Type: " + primary + '/' + sub);
         }
      } else if (isSoap1_1Type(primary, sub)) {
         return (primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("fastinfoset") ? 16 : 0) | 1 | 4;
      } else if (!isSoap1_2Type(primary, sub)) {
         if (isSOAPBodyXOPPackage(ct)) {
            return 13;
         } else {
            log.severe("SAAJ0537.soap.invalid.content-type");
            throw new SOAPExceptionImpl("Invalid Content-Type:" + primary + '/' + sub + ". Is this an error message instead of a SOAP response?");
         }
      } else {
         return (primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("soap+fastinfoset") ? 16 : 0) | 1 | 8;
      }
   }

   private static String getTypeParameter(ContentType contentType) {
      String p = contentType.getParameter("type");
      return p != null ? p.toLowerCase() : "text/xml";
   }

   public MimeHeaders getMimeHeaders() {
      return this.headers;
   }

   static final String getContentType(MimeHeaders headers) {
      String[] values = headers.getHeader("Content-Type");
      return values == null ? null : values[0];
   }

   public String getContentType() {
      return getContentType(this.headers);
   }

   public void setContentType(String type) {
      this.headers.setHeader("Content-Type", type);
      this.needsSave();
   }

   private ContentType contentType() {
      ContentType ct = null;

      try {
         String currentContent = this.getContentType();
         if (currentContent == null) {
            return this.contentType;
         }

         ct = new ContentType(currentContent);
      } catch (Exception var3) {
      }

      return ct;
   }

   public String getBaseType() {
      return this.contentType().getBaseType();
   }

   public void setBaseType(String type) {
      ContentType ct = this.contentType();
      ct.setParameter("type", type);
      this.headers.setHeader("Content-Type", ct.toString());
      this.needsSave();
   }

   public String getAction() {
      return this.contentType().getParameter("action");
   }

   public void setAction(String action) {
      ContentType ct = this.contentType();
      ct.setParameter("action", action);
      this.headers.setHeader("Content-Type", ct.toString());
      this.needsSave();
   }

   public String getCharset() {
      return this.contentType().getParameter("charset");
   }

   public void setCharset(String charset) {
      ContentType ct = this.contentType();
      ct.setParameter("charset", charset);
      this.headers.setHeader("Content-Type", ct.toString());
      this.needsSave();
   }

   private final void needsSave() {
      this.saved = false;
   }

   public boolean saveRequired() {
      return !this.saved;
   }

   public String getContentDescription() {
      String[] values = this.headers.getHeader("Content-Description");
      return values != null && values.length > 0 ? values[0] : null;
   }

   public void setContentDescription(String description) {
      this.headers.setHeader("Content-Description", description);
      this.needsSave();
   }

   public abstract SOAPPart getSOAPPart();

   public void removeAllAttachments() {
      try {
         this.initializeAllAttachments();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }

      if (this.attachments != null) {
         this.attachments.clear();
         this.needsSave();
      }

   }

   public int countAttachments() {
      try {
         this.initializeAllAttachments();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }

      return this.attachments != null ? this.attachments.size() : 0;
   }

   public void addAttachmentPart(AttachmentPart attachment) {
      try {
         this.initializeAllAttachments();
         this.optimizeAttachmentProcessing = true;
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }

      if (this.attachments == null) {
         this.attachments = new FinalArrayList();
      }

      this.attachments.add(attachment);
      this.needsSave();
   }

   public Iterator getAttachments() {
      try {
         this.initializeAllAttachments();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }

      return this.attachments == null ? nullIter : this.attachments.iterator();
   }

   private void setFinalContentType(String charset) {
      ContentType ct = this.contentType();
      if (ct == null) {
         ct = new ContentType();
      }

      String[] split = this.getExpectedContentType().split("/");
      ct.setPrimaryType(split[0]);
      ct.setSubType(split[1]);
      ct.setParameter("charset", charset);
      this.headers.setHeader("Content-Type", ct.toString());
   }

   public Iterator getAttachments(MimeHeaders headers) {
      try {
         this.initializeAllAttachments();
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }

      return (Iterator)(this.attachments == null ? nullIter : new MessageImpl.MimeMatchingIterator(headers));
   }

   public void removeAttachments(MimeHeaders headers) {
      try {
         this.initializeAllAttachments();
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }

      if (this.attachments != null) {
         MessageImpl.MimeMatchingIterator it = new MessageImpl.MimeMatchingIterator(headers);

         while(it.hasNext()) {
            int index = this.attachments.indexOf(it.next());
            this.attachments.set(index, (Object)null);
         }

         FinalArrayList f = new FinalArrayList();

         for(int i = 0; i < this.attachments.size(); ++i) {
            if (this.attachments.get(i) != null) {
               f.add(this.attachments.get(i));
            }
         }

         this.attachments = f;
      }
   }

   public AttachmentPart createAttachmentPart() {
      return new AttachmentPartImpl();
   }

   public AttachmentPart getAttachment(SOAPElement element) throws SOAPException {
      try {
         this.initializeAllAttachments();
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }

      String hrefAttr = element.getAttribute("href");
      String uri;
      if ("".equals(hrefAttr)) {
         Node node = this.getValueNodeStrict(element);
         String swaRef = null;
         if (node != null) {
            swaRef = node.getValue();
         }

         if (swaRef == null || "".equals(swaRef)) {
            return null;
         }

         uri = swaRef;
      } else {
         uri = hrefAttr;
      }

      return this.getAttachmentPart(uri);
   }

   private Node getValueNodeStrict(SOAPElement element) {
      Node node = (Node)element.getFirstChild();
      if (node != null) {
         return node.getNextSibling() == null && node.getNodeType() == 3 ? node : null;
      } else {
         return null;
      }
   }

   private AttachmentPart getAttachmentPart(String uri) throws SOAPException {
      try {
         AttachmentPart _part;
         MimeHeaders headersToMatch;
         Iterator i;
         if (uri.startsWith("cid:")) {
            uri = '<' + uri.substring("cid:".length()) + '>';
            headersToMatch = new MimeHeaders();
            headersToMatch.addHeader("Content-ID", uri);
            i = this.getAttachments(headersToMatch);
            _part = i == null ? null : (AttachmentPart)i.next();
         } else {
            headersToMatch = new MimeHeaders();
            headersToMatch.addHeader("Content-Location", uri);
            i = this.getAttachments(headersToMatch);
            _part = i == null ? null : (AttachmentPart)i.next();
         }

         if (_part == null) {
            Iterator j = this.getAttachments();

            while(j.hasNext()) {
               AttachmentPart p = (AttachmentPart)j.next();
               String cl = p.getContentId();
               if (cl != null) {
                  int eqIndex = cl.indexOf("=");
                  if (eqIndex > -1) {
                     cl = cl.substring(1, eqIndex);
                     if (cl.equalsIgnoreCase(uri)) {
                        _part = p;
                        break;
                     }
                  }
               }
            }
         }

         return _part;
      } catch (Exception var7) {
         log.log(Level.SEVERE, "SAAJ0590.soap.unable.to.locate.attachment", new Object[]{uri});
         throw new SOAPExceptionImpl(var7);
      }
   }

   private final InputStream getHeaderBytes() throws IOException {
      SOAPPartImpl sp = (SOAPPartImpl)this.getSOAPPart();
      return sp.getContentAsStream();
   }

   private String convertToSingleLine(String contentType) {
      StringBuffer buffer = new StringBuffer();

      for(int i = 0; i < contentType.length(); ++i) {
         char c = contentType.charAt(i);
         if (c != '\r' && c != '\n' && c != '\t') {
            buffer.append(c);
         }
      }

      return buffer.toString();
   }

   private MimeMultipart getMimeMessage() throws SOAPException {
      try {
         SOAPPartImpl soapPart = (SOAPPartImpl)this.getSOAPPart();
         MimeBodyPart mimeSoapPart = soapPart.getMimePart();
         ContentType soapPartCtype = new ContentType(this.getExpectedContentType());
         if (!this.isFastInfoset) {
            soapPartCtype.setParameter("charset", this.initCharset());
         }

         mimeSoapPart.setHeader("Content-Type", soapPartCtype.toString());
         MimeMultipart headerAndBody = null;
         Iterator eachAttachment;
         if (!switchOffBM && !switchOffLazyAttachment && this.multiPart != null && !this.attachmentsInitialized) {
            headerAndBody = new BMMimeMultipart();
            ((MimeMultipart)headerAndBody).addBodyPart(mimeSoapPart);
            if (this.attachments != null) {
               eachAttachment = this.attachments.iterator();

               while(eachAttachment.hasNext()) {
                  ((MimeMultipart)headerAndBody).addBodyPart(((AttachmentPartImpl)eachAttachment.next()).getMimePart());
               }
            }

            InputStream in = ((BMMimeMultipart)this.multiPart).getInputStream();
            if (!((BMMimeMultipart)this.multiPart).lastBodyPartFound() && !((BMMimeMultipart)this.multiPart).isEndOfStream()) {
               ((BMMimeMultipart)headerAndBody).setInputStream(in);
               ((BMMimeMultipart)headerAndBody).setBoundary(((BMMimeMultipart)this.multiPart).getBoundary());
               ((BMMimeMultipart)headerAndBody).setLazyAttachments(this.lazyAttachments);
            }
         } else {
            headerAndBody = new MimeMultipart();
            ((MimeMultipart)headerAndBody).addBodyPart(mimeSoapPart);
            eachAttachment = this.getAttachments();

            while(eachAttachment.hasNext()) {
               ((MimeMultipart)headerAndBody).addBodyPart(((AttachmentPartImpl)eachAttachment.next()).getMimePart());
            }
         }

         ContentType contentType = ((MimeMultipart)headerAndBody).getContentType();
         ParameterList l = contentType.getParameterList();
         l.set("type", this.getExpectedContentType());
         l.set("boundary", contentType.getParameter("boundary"));
         ContentType nct = new ContentType("multipart", "related", l);
         this.headers.setHeader("Content-Type", this.convertToSingleLine(nct.toString()));
         return (MimeMultipart)headerAndBody;
      } catch (SOAPException var8) {
         throw var8;
      } catch (Throwable var9) {
         log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
         throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", var9);
      }
   }

   private String initCharset() {
      String charset = null;
      String[] cts = this.getMimeHeaders().getHeader("Content-Type");
      if (cts != null && cts[0] != null) {
         charset = this.getCharsetString(cts[0]);
      }

      if (charset == null) {
         charset = (String)this.getProperty("javax.xml.soap.character-set-encoding");
      }

      return charset != null ? charset : "utf-8";
   }

   private String getCharsetString(String s) {
      try {
         int index = s.indexOf(";");
         if (index < 0) {
            return null;
         } else {
            ParameterList pl = new ParameterList(s.substring(index));
            return pl.get("charset");
         }
      } catch (Exception var4) {
         return null;
      }
   }

   public void saveChanges() throws SOAPException {
      String charset = this.initCharset();
      int attachmentCount = this.attachments == null ? 0 : this.attachments.size();
      if (attachmentCount == 0 && !switchOffBM && !switchOffLazyAttachment && !this.attachmentsInitialized && this.multiPart != null) {
         attachmentCount = 1;
      }

      try {
         if (attachmentCount == 0 && !this.hasXOPContent()) {
            InputStream in;
            try {
               in = this.getHeaderBytes();
               this.optimizeAttachmentProcessing = false;
               if (SOAPPartImpl.lazyContentLength) {
                  this.inputStreamAfterSaveChanges = in;
               }
            } catch (IOException var5) {
               log.severe("SAAJ0539.soap.cannot.get.header.stream");
               throw new SOAPExceptionImpl("Unable to get header stream in saveChanges: ", var5);
            }

            if (in instanceof ByteInputStream) {
               ByteInputStream bIn = (ByteInputStream)in;
               this.messageBytes = bIn.getBytes();
               this.messageByteCount = bIn.getCount();
            }

            this.setFinalContentType(charset);
            if (this.messageByteCount > 0) {
               this.headers.setHeader("Content-Length", Integer.toString(this.messageByteCount));
            }
         } else if (this.hasXOPContent()) {
            this.mmp = this.getXOPMessage();
         } else {
            this.mmp = this.getMimeMessage();
         }
      } catch (Throwable var6) {
         log.severe("SAAJ0540.soap.err.saving.multipart.msg");
         throw new SOAPExceptionImpl("Error during saving a multipart message", var6);
      }

      this.saved = true;
   }

   private MimeMultipart getXOPMessage() throws SOAPException {
      try {
         MimeMultipart headerAndBody = new MimeMultipart();
         SOAPPartImpl soapPart = (SOAPPartImpl)this.getSOAPPart();
         MimeBodyPart mimeSoapPart = soapPart.getMimePart();
         ContentType soapPartCtype = new ContentType("application/xop+xml");
         soapPartCtype.setParameter("type", this.getExpectedContentType());
         String charset = this.initCharset();
         soapPartCtype.setParameter("charset", charset);
         mimeSoapPart.setHeader("Content-Type", soapPartCtype.toString());
         headerAndBody.addBodyPart(mimeSoapPart);
         Iterator eachAttachement = this.getAttachments();

         while(eachAttachement.hasNext()) {
            headerAndBody.addBodyPart(((AttachmentPartImpl)eachAttachement.next()).getMimePart());
         }

         ContentType contentType = headerAndBody.getContentType();
         ParameterList l = contentType.getParameterList();
         l.set("start-info", this.getExpectedContentType());
         l.set("type", "application/xop+xml");
         if (this.isCorrectSoapVersion(8)) {
            String action = this.getAction();
            if (action != null) {
               l.set("action", action);
            }
         }

         l.set("boundary", contentType.getParameter("boundary"));
         ContentType nct = new ContentType("Multipart", "Related", l);
         this.headers.setHeader("Content-Type", this.convertToSingleLine(nct.toString()));
         return headerAndBody;
      } catch (SOAPException var9) {
         throw var9;
      } catch (Throwable var10) {
         log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
         throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", var10);
      }
   }

   private boolean hasXOPContent() throws ParseException {
      String type = this.getContentType();
      if (type == null) {
         return false;
      } else {
         ContentType ct = new ContentType(type);
         return isMimeMultipartXOPSoap1_1Package(ct) || isMimeMultipartXOPSoap1_2Package(ct) || isSOAPBodyXOPPackage(ct);
      }
   }

   public void writeTo(OutputStream out) throws SOAPException, IOException {
      if (this.saveRequired()) {
         this.optimizeAttachmentProcessing = true;
         this.saveChanges();
      }

      if (!this.optimizeAttachmentProcessing) {
         if (SOAPPartImpl.lazyContentLength && this.messageByteCount <= 0) {
            byte[] buf = new byte[1024];

            int length;
            for(boolean var3 = false; (length = this.inputStreamAfterSaveChanges.read(buf)) != -1; this.messageByteCount += length) {
               out.write(buf, 0, length);
            }

            if (this.messageByteCount > 0) {
               this.headers.setHeader("Content-Length", Integer.toString(this.messageByteCount));
            }
         } else {
            out.write(this.messageBytes, 0, this.messageByteCount);
         }
      } else {
         try {
            if (this.hasXOPContent()) {
               this.mmp.writeTo(out);
            } else {
               this.mmp.writeTo(out);
               if (!switchOffBM && !switchOffLazyAttachment && this.multiPart != null && !this.attachmentsInitialized) {
                  ((BMMimeMultipart)this.multiPart).setInputStream(((BMMimeMultipart)this.mmp).getInputStream());
               }
            }
         } catch (Exception var4) {
            log.severe("SAAJ0540.soap.err.saving.multipart.msg");
            throw new SOAPExceptionImpl("Error during saving a multipart message", var4);
         }
      }

      if (this.isCorrectSoapVersion(4)) {
         String[] soapAction = this.headers.getHeader("SOAPAction");
         if (soapAction == null || soapAction.length == 0) {
            this.headers.setHeader("SOAPAction", "\"\"");
         }
      }

      this.messageBytes = null;
      this.needsSave();
   }

   public SOAPBody getSOAPBody() throws SOAPException {
      SOAPBody body = this.getSOAPPart().getEnvelope().getBody();
      return body;
   }

   public SOAPHeader getSOAPHeader() throws SOAPException {
      SOAPHeader hdr = this.getSOAPPart().getEnvelope().getHeader();
      return hdr;
   }

   private void initializeAllAttachments() throws MessagingException, SOAPException {
      if (!switchOffBM && !switchOffLazyAttachment) {
         if (!this.attachmentsInitialized && this.multiPart != null) {
            if (this.attachments == null) {
               this.attachments = new FinalArrayList();
            }

            int count = this.multiPart.getCount();

            for(int i = 0; i < count; ++i) {
               this.initializeAttachment(this.multiPart.getBodyPart(i));
            }

            this.attachmentsInitialized = true;
            this.needsSave();
         }
      }
   }

   private void initializeAttachment(MimeBodyPart mbp) throws SOAPException {
      AttachmentPartImpl attachmentPart = new AttachmentPartImpl();
      DataHandler attachmentHandler = mbp.getDataHandler();
      attachmentPart.setDataHandler(attachmentHandler);
      AttachmentPartImpl.copyMimeHeaders(mbp, attachmentPart);
      this.attachments.add(attachmentPart);
   }

   private void initializeAttachment(MimeMultipart multiPart, int i) throws Exception {
      MimeBodyPart currentBodyPart = multiPart.getBodyPart(i);
      AttachmentPartImpl attachmentPart = new AttachmentPartImpl();
      DataHandler attachmentHandler = currentBodyPart.getDataHandler();
      attachmentPart.setDataHandler(attachmentHandler);
      AttachmentPartImpl.copyMimeHeaders(currentBodyPart, attachmentPart);
      this.addAttachmentPart(attachmentPart);
   }

   private void setMimeHeaders(SOAPPart soapPart, MimeBodyPart soapMessagePart) throws Exception {
      soapPart.removeAllMimeHeaders();
      List headers = soapMessagePart.getAllHeaders();
      int sz = headers.size();

      for(int i = 0; i < sz; ++i) {
         Header h = (Header)headers.get(i);
         soapPart.addMimeHeader(h.getName(), h.getValue());
      }

   }

   private void initCharsetProperty(ContentType contentType) {
      String charset = contentType.getParameter("charset");
      if (charset != null) {
         ((SOAPPartImpl)this.getSOAPPart()).setSourceCharsetEncoding(charset);
         if (!charset.equalsIgnoreCase("utf-8")) {
            this.setProperty("javax.xml.soap.character-set-encoding", charset);
         }
      }

   }

   public void setLazyAttachments(boolean flag) {
      this.lazyAttachments = flag;
   }

   static {
      String s = SAAJUtil.getSystemProperty("saaj.mime.optimization");
      if (s != null && s.equals("false")) {
         switchOffBM = true;
      }

      s = SAAJUtil.getSystemProperty("saaj.lazy.mime.optimization");
      if (s != null && s.equals("false")) {
         switchOffLazyAttachment = true;
      }

      useMimePull = SAAJUtil.getSystemBoolean("saaj.use.mimepull");
      nullIter = Collections.EMPTY_LIST.iterator();
   }

   private class MimeMatchingIterator implements Iterator {
      private Iterator iter;
      private MimeHeaders headers;
      private Object nextAttachment;

      public MimeMatchingIterator(MimeHeaders headers) {
         this.headers = headers;
         this.iter = MessageImpl.this.attachments.iterator();
      }

      public boolean hasNext() {
         if (this.nextAttachment == null) {
            this.nextAttachment = this.nextMatch();
         }

         return this.nextAttachment != null;
      }

      public Object next() {
         if (this.nextAttachment != null) {
            Object ret = this.nextAttachment;
            this.nextAttachment = null;
            return ret;
         } else {
            return this.hasNext() ? this.nextAttachment : null;
         }
      }

      Object nextMatch() {
         while(true) {
            if (this.iter.hasNext()) {
               AttachmentPartImpl ap = (AttachmentPartImpl)this.iter.next();
               if (!ap.hasAllHeaders(this.headers)) {
                  continue;
               }

               return ap;
            }

            return null;
         }
      }

      public void remove() {
         this.iter.remove();
      }
   }
}
