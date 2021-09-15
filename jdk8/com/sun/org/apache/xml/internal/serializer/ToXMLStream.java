package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public final class ToXMLStream extends ToStream {
   boolean m_cdataTagOpen = false;
   private static CharInfo m_xmlcharInfo = CharInfo.getCharInfoInternal("com.sun.org.apache.xml.internal.serializer.XMLEntities", "xml");

   public ToXMLStream() {
      this.m_charInfo = m_xmlcharInfo;
      this.initCDATA();
      this.m_prefixMap = new NamespaceMappings();
   }

   public void CopyFrom(ToXMLStream xmlListener) {
      this.m_writer = xmlListener.m_writer;
      String encoding = xmlListener.getEncoding();
      this.setEncoding(encoding);
      this.setOmitXMLDeclaration(xmlListener.getOmitXMLDeclaration());
      this.m_ispreserve = xmlListener.m_ispreserve;
      this.m_preserves = xmlListener.m_preserves;
      this.m_isprevtext = xmlListener.m_isprevtext;
      this.m_doIndent = xmlListener.m_doIndent;
      this.setIndentAmount(xmlListener.getIndentAmount());
      this.m_startNewLine = xmlListener.m_startNewLine;
      this.m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
      this.setDoctypeSystem(xmlListener.getDoctypeSystem());
      this.setDoctypePublic(xmlListener.getDoctypePublic());
      this.setStandalone(xmlListener.getStandalone());
      this.setMediaType(xmlListener.getMediaType());
      this.m_maxCharacter = xmlListener.m_maxCharacter;
      this.m_encodingInfo = xmlListener.m_encodingInfo;
      this.m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
      this.m_cdataStartCalled = xmlListener.m_cdataStartCalled;
   }

   public void startDocumentInternal() throws SAXException {
      if (this.m_needToCallStartDocument) {
         super.startDocumentInternal();
         this.m_needToCallStartDocument = false;
         if (this.m_inEntityRef) {
            return;
         }

         this.m_needToOutputDocTypeDecl = true;
         this.m_startNewLine = false;
         if (!this.getOmitXMLDeclaration()) {
            String encoding = Encodings.getMimeEncoding(this.getEncoding());
            String version = this.getVersion();
            if (version == null) {
               version = "1.0";
            }

            String standalone;
            if (this.m_standaloneWasSpecified) {
               standalone = " standalone=\"" + this.getStandalone() + "\"";
            } else {
               standalone = "";
            }

            try {
               Writer writer = this.m_writer;
               writer.write("<?xml version=\"");
               writer.write(version);
               writer.write("\" encoding=\"");
               writer.write(encoding);
               writer.write(34);
               writer.write(standalone);
               writer.write("?>");
               if (this.m_doIndent && (this.m_standaloneWasSpecified || this.getDoctypePublic() != null || this.getDoctypeSystem() != null || this.m_isStandalone)) {
                  writer.write((char[])this.m_lineSep, 0, this.m_lineSepLen);
               }
            } catch (IOException var5) {
               throw new SAXException(var5);
            }
         }
      }

   }

   public void endDocument() throws SAXException {
      this.flushPending();
      if (this.m_doIndent && !this.m_isprevtext) {
         try {
            this.outputLineSep();
         } catch (IOException var2) {
            throw new SAXException(var2);
         }
      }

      this.flushWriter();
      if (this.m_tracer != null) {
         super.fireEndDoc();
      }

   }

   public void startPreserving() throws SAXException {
      this.m_preserves.push(true);
      this.m_ispreserve = true;
   }

   public void endPreserving() throws SAXException {
      this.m_ispreserve = this.m_preserves.isEmpty() ? false : this.m_preserves.pop();
   }

   public void processingInstruction(String target, String data) throws SAXException {
      if (!this.m_inEntityRef) {
         this.flushPending();
         if (target.equals("javax.xml.transform.disable-output-escaping")) {
            this.startNonEscaping();
         } else if (target.equals("javax.xml.transform.enable-output-escaping")) {
            this.endNonEscaping();
         } else {
            try {
               if (this.m_elemContext.m_startTagOpen) {
                  this.closeStartTag();
                  this.m_elemContext.m_startTagOpen = false;
               } else if (this.m_needToCallStartDocument) {
                  this.startDocumentInternal();
               }

               if (this.shouldIndent()) {
                  this.indent();
               }

               Writer writer = this.m_writer;
               writer.write("<?");
               writer.write(target);
               if (data.length() > 0 && !Character.isSpaceChar(data.charAt(0))) {
                  writer.write(32);
               }

               int indexOfQLT = data.indexOf("?>");
               if (indexOfQLT >= 0) {
                  if (indexOfQLT > 0) {
                     writer.write(data.substring(0, indexOfQLT));
                  }

                  writer.write("? >");
                  if (indexOfQLT + 2 < data.length()) {
                     writer.write(data.substring(indexOfQLT + 2));
                  }
               } else {
                  writer.write(data);
               }

               writer.write(63);
               writer.write(62);
               if (this.m_elemContext.m_currentElemDepth <= 0 && this.m_isStandalone) {
                  writer.write((char[])this.m_lineSep, 0, this.m_lineSepLen);
               }

               this.m_startNewLine = true;
            } catch (IOException var5) {
               throw new SAXException(var5);
            }
         }

         if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
         }

      }
   }

   public void entityReference(String name) throws SAXException {
      if (this.m_elemContext.m_startTagOpen) {
         this.closeStartTag();
         this.m_elemContext.m_startTagOpen = false;
      }

      try {
         if (this.shouldIndent()) {
            this.indent();
         }

         Writer writer = this.m_writer;
         writer.write(38);
         writer.write(name);
         writer.write(59);
      } catch (IOException var3) {
         throw new SAXException(var3);
      }

      if (this.m_tracer != null) {
         super.fireEntityReference(name);
      }

   }

   public void addUniqueAttribute(String name, String value, int flags) throws SAXException {
      if (this.m_elemContext.m_startTagOpen) {
         try {
            String patchedName = this.patchName(name);
            Writer writer = this.m_writer;
            if ((flags & 1) > 0 && m_xmlcharInfo.onlyQuotAmpLtGt) {
               writer.write(32);
               writer.write(patchedName);
               writer.write("=\"");
               writer.write(value);
               writer.write(34);
            } else {
               writer.write(32);
               writer.write(patchedName);
               writer.write("=\"");
               this.writeAttrString(writer, value, this.getEncoding());
               writer.write(34);
            }
         } catch (IOException var6) {
            throw new SAXException(var6);
         }
      }

   }

   public void addAttribute(String uri, String localName, String rawName, String type, String value, boolean xslAttribute) throws SAXException {
      if (this.m_elemContext.m_startTagOpen) {
         boolean was_added = this.addAttributeAlways(uri, localName, rawName, type, value, xslAttribute);
         if (was_added && !xslAttribute && !rawName.startsWith("xmlns")) {
            String prefixUsed = this.ensureAttributesNamespaceIsDeclared(uri, localName, rawName);
            if (prefixUsed != null && rawName != null && !rawName.startsWith(prefixUsed)) {
               rawName = prefixUsed + ":" + localName;
            }
         }

         this.addAttributeAlways(uri, localName, rawName, type, value, xslAttribute);
      } else {
         String msg = Utils.messages.createMessage("ER_ILLEGAL_ATTRIBUTE_POSITION", new Object[]{localName});

         try {
            Transformer tran = super.getTransformer();
            ErrorListener errHandler = tran.getErrorListener();
            if (null != errHandler && this.m_sourceLocator != null) {
               errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
            } else {
               System.out.println(msg);
            }
         } catch (Exception var10) {
         }
      }

   }

   public void endElement(String elemName) throws SAXException {
      this.endElement((String)null, (String)null, elemName);
   }

   public void namespaceAfterStartElement(String prefix, String uri) throws SAXException {
      if (this.m_elemContext.m_elementURI == null) {
         String prefix1 = getPrefixPart(this.m_elemContext.m_elementName);
         if (prefix1 == null && "".equals(prefix)) {
            this.m_elemContext.m_elementURI = uri;
         }
      }

      this.startPrefixMapping(prefix, uri, false);
   }

   protected boolean pushNamespace(String prefix, String uri) {
      try {
         if (this.m_prefixMap.pushNamespace(prefix, uri, this.m_elemContext.m_currentElemDepth)) {
            this.startPrefixMapping(prefix, uri);
            return true;
         }
      } catch (SAXException var4) {
      }

      return false;
   }

   public boolean reset() {
      boolean wasReset = false;
      if (super.reset()) {
         this.resetToXMLStream();
         wasReset = true;
      }

      return wasReset;
   }

   private void resetToXMLStream() {
      this.m_cdataTagOpen = false;
   }

   private String getXMLVersion() {
      String xmlVersion = this.getVersion();
      if (xmlVersion != null && !xmlVersion.equals("1.0")) {
         if (xmlVersion.equals("1.1")) {
            xmlVersion = "1.1";
         } else {
            String msg = Utils.messages.createMessage("ER_XML_VERSION_NOT_SUPPORTED", new Object[]{xmlVersion});

            try {
               Transformer tran = super.getTransformer();
               ErrorListener errHandler = tran.getErrorListener();
               if (null != errHandler && this.m_sourceLocator != null) {
                  errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
               } else {
                  System.out.println(msg);
               }
            } catch (Exception var5) {
            }

            xmlVersion = "1.0";
         }
      } else {
         xmlVersion = "1.0";
      }

      return xmlVersion;
   }
}
