package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class SDDocumentSource {
   public abstract XMLStreamReader read(XMLInputFactory var1) throws IOException, XMLStreamException;

   public abstract XMLStreamReader read() throws IOException, XMLStreamException;

   public abstract URL getSystemId();

   public static SDDocumentSource create(final URL url) {
      return new SDDocumentSource() {
         private final URL systemId = url;

         public XMLStreamReader read(XMLInputFactory xif) throws IOException, XMLStreamException {
            InputStream is = url.openStream();
            return new TidyXMLStreamReader(xif.createXMLStreamReader(this.systemId.toExternalForm(), is), is);
         }

         public XMLStreamReader read() throws IOException, XMLStreamException {
            InputStream is = url.openStream();
            return new TidyXMLStreamReader(XMLStreamReaderFactory.create(this.systemId.toExternalForm(), is, false), is);
         }

         public URL getSystemId() {
            return this.systemId;
         }
      };
   }

   public static SDDocumentSource create(final URL systemId, final XMLStreamBuffer xsb) {
      return new SDDocumentSource() {
         public XMLStreamReader read(XMLInputFactory xif) throws XMLStreamException {
            return xsb.readAsXMLStreamReader();
         }

         public XMLStreamReader read() throws XMLStreamException {
            return xsb.readAsXMLStreamReader();
         }

         public URL getSystemId() {
            return systemId;
         }
      };
   }
}
