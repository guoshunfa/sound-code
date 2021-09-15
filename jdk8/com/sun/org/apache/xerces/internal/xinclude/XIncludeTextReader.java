package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class XIncludeTextReader {
   private Reader fReader;
   private XIncludeHandler fHandler;
   private XMLInputSource fSource;
   private XMLErrorReporter fErrorReporter;
   private XMLString fTempString = new XMLString();

   public XIncludeTextReader(XMLInputSource source, XIncludeHandler handler, int bufferSize) throws IOException {
      this.fHandler = handler;
      this.fSource = source;
      this.fTempString = new XMLString(new char[bufferSize + 1], 0, 0);
   }

   public void setErrorReporter(XMLErrorReporter errorReporter) {
      this.fErrorReporter = errorReporter;
   }

   protected Reader getReader(XMLInputSource source) throws IOException {
      if (source.getCharacterStream() != null) {
         return source.getCharacterStream();
      } else {
         InputStream stream = null;
         String encoding = source.getEncoding();
         if (encoding == null) {
            encoding = "UTF-8";
         }

         String javaEncoding;
         if (source.getByteStream() != null) {
            stream = source.getByteStream();
            if (!(stream instanceof BufferedInputStream)) {
               stream = new BufferedInputStream((InputStream)stream, this.fTempString.ch.length);
            }
         } else {
            javaEncoding = XMLEntityManager.expandSystemId(source.getSystemId(), source.getBaseSystemId(), false);
            URL url = new URL(javaEncoding);
            URLConnection urlCon = url.openConnection();
            Iterator propIter;
            if (urlCon instanceof HttpURLConnection && source instanceof HTTPInputSource) {
               HttpURLConnection urlConnection = (HttpURLConnection)urlCon;
               HTTPInputSource httpInputSource = (HTTPInputSource)source;
               propIter = httpInputSource.getHTTPRequestProperties();

               while(propIter.hasNext()) {
                  Map.Entry entry = (Map.Entry)propIter.next();
                  urlConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
               }

               boolean followRedirects = httpInputSource.getFollowHTTPRedirects();
               if (!followRedirects) {
                  XMLEntityManager.setInstanceFollowRedirects(urlConnection, followRedirects);
               }
            }

            stream = new BufferedInputStream(urlCon.getInputStream());
            String rawContentType = urlCon.getContentType();
            int index = rawContentType != null ? rawContentType.indexOf(59) : -1;
            propIter = null;
            String charset = null;
            String contentType;
            if (index != -1) {
               contentType = rawContentType.substring(0, index).trim();
               charset = rawContentType.substring(index + 1).trim();
               if (!charset.startsWith("charset=")) {
                  charset = null;
               } else {
                  charset = charset.substring(8).trim();
                  if (charset.charAt(0) == '"' && charset.charAt(charset.length() - 1) == '"' || charset.charAt(0) == '\'' && charset.charAt(charset.length() - 1) == '\'') {
                     charset = charset.substring(1, charset.length() - 1);
                  }
               }
            } else {
               contentType = rawContentType.trim();
            }

            String detectedEncoding = null;
            if (contentType.equals("text/xml")) {
               if (charset != null) {
                  detectedEncoding = charset;
               } else {
                  detectedEncoding = "US-ASCII";
               }
            } else if (contentType.equals("application/xml")) {
               if (charset != null) {
                  detectedEncoding = charset;
               } else {
                  detectedEncoding = this.getEncodingName((InputStream)stream);
               }
            } else if (contentType.endsWith("+xml")) {
               detectedEncoding = this.getEncodingName((InputStream)stream);
            }

            if (detectedEncoding != null) {
               encoding = detectedEncoding;
            }
         }

         encoding = encoding.toUpperCase(Locale.ENGLISH);
         encoding = this.consumeBOM((InputStream)stream, encoding);
         if (encoding.equals("UTF-8")) {
            return new UTF8Reader((InputStream)stream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
         } else {
            javaEncoding = EncodingMap.getIANA2JavaMapping(encoding);
            if (javaEncoding == null) {
               MessageFormatter aFormatter = this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210");
               Locale aLocale = this.fErrorReporter.getLocale();
               throw new IOException(aFormatter.formatMessage(aLocale, "EncodingDeclInvalid", new Object[]{encoding}));
            } else {
               return (Reader)(javaEncoding.equals("ASCII") ? new ASCIIReader((InputStream)stream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()) : new InputStreamReader((InputStream)stream, javaEncoding));
            }
         }
      }
   }

   protected String getEncodingName(InputStream stream) throws IOException {
      byte[] b4 = new byte[4];
      String encoding = null;
      stream.mark(4);
      int count = stream.read(b4, 0, 4);
      stream.reset();
      if (count == 4) {
         encoding = this.getEncodingName(b4);
      }

      return encoding;
   }

   protected String consumeBOM(InputStream stream, String encoding) throws IOException {
      byte[] b = new byte[3];
      int count = false;
      stream.mark(3);
      int b0;
      int b1;
      int count;
      if (encoding.equals("UTF-8")) {
         count = stream.read(b, 0, 3);
         if (count == 3) {
            b0 = b[0] & 255;
            b1 = b[1] & 255;
            int b2 = b[2] & 255;
            if (b0 != 239 || b1 != 187 || b2 != 191) {
               stream.reset();
            }
         } else {
            stream.reset();
         }
      } else if (encoding.startsWith("UTF-16")) {
         count = stream.read(b, 0, 2);
         if (count == 2) {
            b0 = b[0] & 255;
            b1 = b[1] & 255;
            if (b0 == 254 && b1 == 255) {
               return "UTF-16BE";
            }

            if (b0 == 255 && b1 == 254) {
               return "UTF-16LE";
            }
         }

         stream.reset();
      }

      return encoding;
   }

   protected String getEncodingName(byte[] b4) {
      int b0 = b4[0] & 255;
      int b1 = b4[1] & 255;
      if (b0 == 254 && b1 == 255) {
         return "UTF-16BE";
      } else if (b0 == 255 && b1 == 254) {
         return "UTF-16LE";
      } else {
         int b2 = b4[2] & 255;
         if (b0 == 239 && b1 == 187 && b2 == 191) {
            return "UTF-8";
         } else {
            int b3 = b4[3] & 255;
            if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 60) {
               return "ISO-10646-UCS-4";
            } else if (b0 == 60 && b1 == 0 && b2 == 0 && b3 == 0) {
               return "ISO-10646-UCS-4";
            } else if (b0 == 0 && b1 == 0 && b2 == 60 && b3 == 0) {
               return "ISO-10646-UCS-4";
            } else if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 0) {
               return "ISO-10646-UCS-4";
            } else if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63) {
               return "UTF-16BE";
            } else if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0) {
               return "UTF-16LE";
            } else {
               return b0 == 76 && b1 == 111 && b2 == 167 && b3 == 148 ? "CP037" : null;
            }
         }
      }
   }

   public void parse() throws IOException {
      this.fReader = this.getReader(this.fSource);
      this.fSource = null;

      for(int readSize = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1); readSize != -1; readSize = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1)) {
         for(int i = 0; i < readSize; ++i) {
            char ch = this.fTempString.ch[i];
            if (!this.isValid(ch)) {
               if (XMLChar.isHighSurrogate(ch)) {
                  ++i;
                  int ch2;
                  if (i < readSize) {
                     ch2 = this.fTempString.ch[i];
                  } else {
                     ch2 = this.fReader.read();
                     if (ch2 != -1) {
                        this.fTempString.ch[readSize++] = (char)ch2;
                     }
                  }

                  if (XMLChar.isLowSurrogate(ch2)) {
                     int sup = XMLChar.supplemental(ch, (char)ch2);
                     if (!this.isValid(sup)) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[]{Integer.toString(sup, 16)}, (short)2);
                     }
                  } else {
                     this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[]{Integer.toString(ch2, 16)}, (short)2);
                  }
               } else {
                  this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[]{Integer.toString(ch, 16)}, (short)2);
               }
            }
         }

         if (this.fHandler != null && readSize > 0) {
            this.fTempString.offset = 0;
            this.fTempString.length = readSize;
            this.fHandler.characters(this.fTempString, this.fHandler.modifyAugmentations((Augmentations)null, true));
         }
      }

   }

   public void setInputSource(XMLInputSource source) {
      this.fSource = source;
   }

   public void close() throws IOException {
      if (this.fReader != null) {
         this.fReader.close();
         this.fReader = null;
      }

   }

   protected boolean isValid(int ch) {
      return XMLChar.isValid(ch);
   }

   protected void setBufferSize(int bufferSize) {
      int var10000 = this.fTempString.ch.length;
      ++bufferSize;
      if (var10000 != bufferSize) {
         this.fTempString.ch = new char[bufferSize];
      }

   }
}
