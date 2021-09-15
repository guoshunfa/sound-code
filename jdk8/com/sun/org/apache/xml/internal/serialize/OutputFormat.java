package com.sun.org.apache.xml.internal.serialize;

import java.io.UnsupportedEncodingException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;

public class OutputFormat {
   private String _method;
   private String _version;
   private int _indent;
   private String _encoding;
   private EncodingInfo _encodingInfo;
   private boolean _allowJavaNames;
   private String _mediaType;
   private String _doctypeSystem;
   private String _doctypePublic;
   private boolean _omitXmlDeclaration;
   private boolean _omitDoctype;
   private boolean _omitComments;
   private boolean _stripComments;
   private boolean _standalone;
   private String[] _cdataElements;
   private String[] _nonEscapingElements;
   private String _lineSeparator;
   private int _lineWidth;
   private boolean _preserve;
   private boolean _preserveEmptyAttributes;

   public OutputFormat() {
      this._indent = 0;
      this._encoding = "UTF-8";
      this._encodingInfo = null;
      this._allowJavaNames = false;
      this._omitXmlDeclaration = false;
      this._omitDoctype = false;
      this._omitComments = false;
      this._stripComments = false;
      this._standalone = false;
      this._lineSeparator = "\n";
      this._lineWidth = 72;
      this._preserve = false;
      this._preserveEmptyAttributes = false;
   }

   public OutputFormat(String method, String encoding, boolean indenting) {
      this._indent = 0;
      this._encoding = "UTF-8";
      this._encodingInfo = null;
      this._allowJavaNames = false;
      this._omitXmlDeclaration = false;
      this._omitDoctype = false;
      this._omitComments = false;
      this._stripComments = false;
      this._standalone = false;
      this._lineSeparator = "\n";
      this._lineWidth = 72;
      this._preserve = false;
      this._preserveEmptyAttributes = false;
      this.setMethod(method);
      this.setEncoding(encoding);
      this.setIndenting(indenting);
   }

   public OutputFormat(Document doc) {
      this._indent = 0;
      this._encoding = "UTF-8";
      this._encodingInfo = null;
      this._allowJavaNames = false;
      this._omitXmlDeclaration = false;
      this._omitDoctype = false;
      this._omitComments = false;
      this._stripComments = false;
      this._standalone = false;
      this._lineSeparator = "\n";
      this._lineWidth = 72;
      this._preserve = false;
      this._preserveEmptyAttributes = false;
      this.setMethod(whichMethod(doc));
      this.setDoctype(whichDoctypePublic(doc), whichDoctypeSystem(doc));
      this.setMediaType(whichMediaType(this.getMethod()));
   }

   public OutputFormat(Document doc, String encoding, boolean indenting) {
      this(doc);
      this.setEncoding(encoding);
      this.setIndenting(indenting);
   }

   public String getMethod() {
      return this._method;
   }

   public void setMethod(String method) {
      this._method = method;
   }

   public String getVersion() {
      return this._version;
   }

   public void setVersion(String version) {
      this._version = version;
   }

   public int getIndent() {
      return this._indent;
   }

   public boolean getIndenting() {
      return this._indent > 0;
   }

   public void setIndent(int indent) {
      if (indent < 0) {
         this._indent = 0;
      } else {
         this._indent = indent;
      }

   }

   public void setIndenting(boolean on) {
      if (on) {
         this._indent = 4;
         this._lineWidth = 72;
      } else {
         this._indent = 0;
         this._lineWidth = 0;
      }

   }

   public String getEncoding() {
      return this._encoding;
   }

   public void setEncoding(String encoding) {
      this._encoding = encoding;
      this._encodingInfo = null;
   }

   public void setEncoding(EncodingInfo encInfo) {
      this._encoding = encInfo.getIANAName();
      this._encodingInfo = encInfo;
   }

   public EncodingInfo getEncodingInfo() throws UnsupportedEncodingException {
      if (this._encodingInfo == null) {
         this._encodingInfo = Encodings.getEncodingInfo(this._encoding, this._allowJavaNames);
      }

      return this._encodingInfo;
   }

   public void setAllowJavaNames(boolean allow) {
      this._allowJavaNames = allow;
   }

   public boolean setAllowJavaNames() {
      return this._allowJavaNames;
   }

   public String getMediaType() {
      return this._mediaType;
   }

   public void setMediaType(String mediaType) {
      this._mediaType = mediaType;
   }

   public void setDoctype(String publicId, String systemId) {
      this._doctypePublic = publicId;
      this._doctypeSystem = systemId;
   }

   public String getDoctypePublic() {
      return this._doctypePublic;
   }

   public String getDoctypeSystem() {
      return this._doctypeSystem;
   }

   public boolean getOmitComments() {
      return this._omitComments;
   }

   public void setOmitComments(boolean omit) {
      this._omitComments = omit;
   }

   public boolean getOmitDocumentType() {
      return this._omitDoctype;
   }

   public void setOmitDocumentType(boolean omit) {
      this._omitDoctype = omit;
   }

   public boolean getOmitXMLDeclaration() {
      return this._omitXmlDeclaration;
   }

   public void setOmitXMLDeclaration(boolean omit) {
      this._omitXmlDeclaration = omit;
   }

   public boolean getStandalone() {
      return this._standalone;
   }

   public void setStandalone(boolean standalone) {
      this._standalone = standalone;
   }

   public String[] getCDataElements() {
      return this._cdataElements;
   }

   public boolean isCDataElement(String tagName) {
      if (this._cdataElements == null) {
         return false;
      } else {
         for(int i = 0; i < this._cdataElements.length; ++i) {
            if (this._cdataElements[i].equals(tagName)) {
               return true;
            }
         }

         return false;
      }
   }

   public void setCDataElements(String[] cdataElements) {
      this._cdataElements = cdataElements;
   }

   public String[] getNonEscapingElements() {
      return this._nonEscapingElements;
   }

   public boolean isNonEscapingElement(String tagName) {
      if (this._nonEscapingElements == null) {
         return false;
      } else {
         for(int i = 0; i < this._nonEscapingElements.length; ++i) {
            if (this._nonEscapingElements[i].equals(tagName)) {
               return true;
            }
         }

         return false;
      }
   }

   public void setNonEscapingElements(String[] nonEscapingElements) {
      this._nonEscapingElements = nonEscapingElements;
   }

   public String getLineSeparator() {
      return this._lineSeparator;
   }

   public void setLineSeparator(String lineSeparator) {
      if (lineSeparator == null) {
         this._lineSeparator = "\n";
      } else {
         this._lineSeparator = lineSeparator;
      }

   }

   public boolean getPreserveSpace() {
      return this._preserve;
   }

   public void setPreserveSpace(boolean preserve) {
      this._preserve = preserve;
   }

   public int getLineWidth() {
      return this._lineWidth;
   }

   public void setLineWidth(int lineWidth) {
      if (lineWidth <= 0) {
         this._lineWidth = 0;
      } else {
         this._lineWidth = lineWidth;
      }

   }

   public boolean getPreserveEmptyAttributes() {
      return this._preserveEmptyAttributes;
   }

   public void setPreserveEmptyAttributes(boolean preserve) {
      this._preserveEmptyAttributes = preserve;
   }

   public char getLastPrintable() {
      return (char)(this.getEncoding() != null && this.getEncoding().equalsIgnoreCase("ASCII") ? 'ÿ' : '\uffff');
   }

   public static String whichMethod(Document doc) {
      if (doc instanceof HTMLDocument) {
         return "html";
      } else {
         for(Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1) {
               if (node.getNodeName().equalsIgnoreCase("html")) {
                  return "html";
               }

               if (node.getNodeName().equalsIgnoreCase("root")) {
                  return "fop";
               }

               return "xml";
            }

            if (node.getNodeType() == 3) {
               String value = node.getNodeValue();

               for(int i = 0; i < value.length(); ++i) {
                  if (value.charAt(i) != ' ' && value.charAt(i) != '\n' && value.charAt(i) != '\t' && value.charAt(i) != '\r') {
                     return "xml";
                  }
               }
            }
         }

         return "xml";
      }
   }

   public static String whichDoctypePublic(Document doc) {
      DocumentType doctype = doc.getDoctype();
      if (doctype != null) {
         try {
            return doctype.getPublicId();
         } catch (Error var3) {
         }
      }

      return doc instanceof HTMLDocument ? "-//W3C//DTD XHTML 1.0 Strict//EN" : null;
   }

   public static String whichDoctypeSystem(Document doc) {
      DocumentType doctype = doc.getDoctype();
      if (doctype != null) {
         try {
            return doctype.getSystemId();
         } catch (Error var3) {
         }
      }

      return doc instanceof HTMLDocument ? "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" : null;
   }

   public static String whichMediaType(String method) {
      if (method.equalsIgnoreCase("xml")) {
         return "text/xml";
      } else if (method.equalsIgnoreCase("html")) {
         return "text/html";
      } else if (method.equalsIgnoreCase("xhtml")) {
         return "text/html";
      } else if (method.equalsIgnoreCase("text")) {
         return "text/plain";
      } else {
         return method.equalsIgnoreCase("fop") ? "application/pdf" : null;
      }
   }

   public static class Defaults {
      public static final int Indent = 4;
      public static final String Encoding = "UTF-8";
      public static final int LineWidth = 72;
   }

   public static class DTD {
      public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
      public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
      public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
      public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
   }
}
