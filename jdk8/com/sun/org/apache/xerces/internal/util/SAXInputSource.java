package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class SAXInputSource extends XMLInputSource {
   private XMLReader fXMLReader;
   private InputSource fInputSource;

   public SAXInputSource() {
      this((InputSource)null);
   }

   public SAXInputSource(InputSource inputSource) {
      this((XMLReader)null, inputSource);
   }

   public SAXInputSource(XMLReader reader, InputSource inputSource) {
      super(inputSource != null ? inputSource.getPublicId() : null, inputSource != null ? inputSource.getSystemId() : null, (String)null);
      if (inputSource != null) {
         this.setByteStream(inputSource.getByteStream());
         this.setCharacterStream(inputSource.getCharacterStream());
         this.setEncoding(inputSource.getEncoding());
      }

      this.fInputSource = inputSource;
      this.fXMLReader = reader;
   }

   public void setXMLReader(XMLReader reader) {
      this.fXMLReader = reader;
   }

   public XMLReader getXMLReader() {
      return this.fXMLReader;
   }

   public void setInputSource(InputSource inputSource) {
      if (inputSource != null) {
         this.setPublicId(inputSource.getPublicId());
         this.setSystemId(inputSource.getSystemId());
         this.setByteStream(inputSource.getByteStream());
         this.setCharacterStream(inputSource.getCharacterStream());
         this.setEncoding(inputSource.getEncoding());
      } else {
         this.setPublicId((String)null);
         this.setSystemId((String)null);
         this.setByteStream((InputStream)null);
         this.setCharacterStream((Reader)null);
         this.setEncoding((String)null);
      }

      this.fInputSource = inputSource;
   }

   public InputSource getInputSource() {
      return this.fInputSource;
   }

   public void setPublicId(String publicId) {
      super.setPublicId(publicId);
      if (this.fInputSource == null) {
         this.fInputSource = new InputSource();
      }

      this.fInputSource.setPublicId(publicId);
   }

   public void setSystemId(String systemId) {
      super.setSystemId(systemId);
      if (this.fInputSource == null) {
         this.fInputSource = new InputSource();
      }

      this.fInputSource.setSystemId(systemId);
   }

   public void setByteStream(InputStream byteStream) {
      super.setByteStream(byteStream);
      if (this.fInputSource == null) {
         this.fInputSource = new InputSource();
      }

      this.fInputSource.setByteStream(byteStream);
   }

   public void setCharacterStream(Reader charStream) {
      super.setCharacterStream(charStream);
      if (this.fInputSource == null) {
         this.fInputSource = new InputSource();
      }

      this.fInputSource.setCharacterStream(charStream);
   }

   public void setEncoding(String encoding) {
      super.setEncoding(encoding);
      if (this.fInputSource == null) {
         this.fInputSource = new InputSource();
      }

      this.fInputSource.setEncoding(encoding);
   }
}
