package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.util.StringTokenizer;
import javax.xml.transform.TransformerException;

public class XMLDeclarationParser {
   private String m_encoding;
   private PushbackReader m_pushbackReader;
   private boolean m_hasHeader;
   private String xmlDecl = null;
   static String gt16 = null;
   static String utf16Decl = null;

   public XMLDeclarationParser(PushbackReader pr) {
      this.m_pushbackReader = pr;
      this.m_encoding = "utf-8";
      this.m_hasHeader = false;
   }

   public String getEncoding() {
      return this.m_encoding;
   }

   public String getXmlDeclaration() {
      return this.xmlDecl;
   }

   public void parse() throws TransformerException, IOException {
      int c = false;
      int index = 0;
      char[] aChar = new char['\uffff'];
      StringBuffer xmlDeclStr = new StringBuffer();

      int c;
      while((c = this.m_pushbackReader.read()) != -1) {
         aChar[index] = (char)c;
         xmlDeclStr.append((char)c);
         ++index;
         if (c == 62) {
            break;
         }
      }

      String decl = xmlDeclStr.toString();
      boolean utf16 = false;
      boolean utf8 = false;
      int xmlIndex = decl.indexOf(utf16Decl);
      if (xmlIndex > -1) {
         utf16 = true;
      } else {
         xmlIndex = decl.indexOf("<?xml");
         if (xmlIndex > -1) {
            utf8 = true;
         }
      }

      if (!utf16 && !utf8) {
         this.m_pushbackReader.unread(aChar, 0, index);
      } else {
         this.m_hasHeader = true;
         if (utf16) {
            this.xmlDecl = new String(decl.getBytes(), "utf-16");
            this.xmlDecl = this.xmlDecl.substring(this.xmlDecl.indexOf("<"));
         } else {
            this.xmlDecl = decl;
         }

         if (xmlIndex != 0) {
            throw new IOException("Unexpected characters before XML declaration");
         } else {
            int versionIndex = this.xmlDecl.indexOf("version");
            if (versionIndex == -1) {
               throw new IOException("Mandatory 'version' attribute Missing in XML declaration");
            } else {
               int encodingIndex = this.xmlDecl.indexOf("encoding");
               if (encodingIndex != -1) {
                  if (versionIndex > encodingIndex) {
                     throw new IOException("The 'version' attribute should preceed the 'encoding' attribute in an XML Declaration");
                  } else {
                     int stdAloneIndex = this.xmlDecl.indexOf("standalone");
                     if (stdAloneIndex > -1 && (stdAloneIndex < versionIndex || stdAloneIndex < encodingIndex)) {
                        throw new IOException("The 'standalone' attribute should be the last attribute in an XML Declaration");
                     } else {
                        int eqIndex = this.xmlDecl.indexOf("=", encodingIndex);
                        if (eqIndex == -1) {
                           throw new IOException("Missing '=' character after 'encoding' in XML declaration");
                        } else {
                           this.m_encoding = this.parseEncoding(this.xmlDecl, eqIndex);
                           if (this.m_encoding.startsWith("\"")) {
                              this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("\"") + 1, this.m_encoding.lastIndexOf("\""));
                           } else if (this.m_encoding.startsWith("'")) {
                              this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("'") + 1, this.m_encoding.lastIndexOf("'"));
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void writeTo(Writer wr) throws IOException {
      if (this.m_hasHeader) {
         wr.write(this.xmlDecl.toString());
      }
   }

   private String parseEncoding(String xmlDeclFinal, int eqIndex) throws IOException {
      StringTokenizer strTok = new StringTokenizer(xmlDeclFinal.substring(eqIndex + 1));
      if (strTok.hasMoreTokens()) {
         String encodingTok = strTok.nextToken();
         int indexofQ = encodingTok.indexOf("?");
         return indexofQ > -1 ? encodingTok.substring(0, indexofQ) : encodingTok;
      } else {
         throw new IOException("Error parsing 'encoding' attribute in XML declaration");
      }
   }

   static {
      try {
         gt16 = new String(">".getBytes("utf-16"));
         utf16Decl = new String("<?xml".getBytes("utf-16"));
      } catch (Exception var1) {
      }

   }
}
