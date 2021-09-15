package com.sun.xml.internal.org.jvnet.fastinfoset;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import java.io.InputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FastInfosetSource extends SAXSource {
   public FastInfosetSource(InputStream inputStream) {
      super(new InputSource(inputStream));
   }

   public XMLReader getXMLReader() {
      XMLReader reader = super.getXMLReader();
      if (reader == null) {
         reader = new SAXDocumentParser();
         this.setXMLReader((XMLReader)reader);
      }

      ((SAXDocumentParser)reader).setInputStream(this.getInputStream());
      return (XMLReader)reader;
   }

   public InputStream getInputStream() {
      return this.getInputSource().getByteStream();
   }

   public void setInputStream(InputStream inputStream) {
      this.setInputSource(new InputSource(inputStream));
   }
}
