package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.SAXException;

public interface XMLEntityResolver {
   XMLEntityResolver.Parser resolveEntity(String var1, String var2) throws SAXException, IOException, XMLStreamException;

   public static final class Parser {
      public final URL systemId;
      public final XMLStreamReader parser;

      public Parser(URL systemId, XMLStreamReader parser) {
         assert parser != null;

         this.systemId = systemId;
         this.parser = parser;
      }

      public Parser(SDDocumentSource doc) throws IOException, XMLStreamException {
         this.systemId = doc.getSystemId();
         this.parser = doc.read();
      }
   }
}
