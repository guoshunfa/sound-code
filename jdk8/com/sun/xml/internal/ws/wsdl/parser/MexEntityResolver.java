package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public final class MexEntityResolver implements XMLEntityResolver {
   private final Map<String, SDDocumentSource> wsdls = new HashMap();

   public MexEntityResolver(List<? extends Source> wsdls) throws IOException {
      Transformer transformer = XmlUtil.newTransformer();
      Iterator var3 = wsdls.iterator();

      while(var3.hasNext()) {
         Source source = (Source)var3.next();
         XMLStreamBufferResult xsbr = new XMLStreamBufferResult();

         try {
            transformer.transform(source, xsbr);
         } catch (TransformerException var8) {
            throw new WebServiceException(var8);
         }

         String systemId = source.getSystemId();
         if (systemId != null) {
            SDDocumentSource doc = SDDocumentSource.create(JAXWSUtils.getFileOrURL(systemId), xsbr.getXMLStreamBuffer());
            this.wsdls.put(systemId, doc);
         }
      }

   }

   public XMLEntityResolver.Parser resolveEntity(String publicId, String systemId) throws SAXException, IOException, XMLStreamException {
      if (systemId != null) {
         SDDocumentSource src = (SDDocumentSource)this.wsdls.get(systemId);
         if (src != null) {
            return new XMLEntityResolver.Parser(src);
         }
      }

      return null;
   }
}
