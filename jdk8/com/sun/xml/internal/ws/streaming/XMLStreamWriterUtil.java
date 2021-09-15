package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.encoding.HasEncoding;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterUtil {
   private XMLStreamWriterUtil() {
   }

   @Nullable
   public static OutputStream getOutputStream(XMLStreamWriter writer) throws XMLStreamException {
      Object obj = null;
      XMLStreamWriter xmlStreamWriter = writer instanceof XMLStreamWriterFactory.HasEncodingWriter ? ((XMLStreamWriterFactory.HasEncodingWriter)writer).getWriter() : writer;
      if (xmlStreamWriter instanceof Map) {
         obj = ((Map)xmlStreamWriter).get("sjsxp-outputstream");
      }

      if (obj == null) {
         try {
            obj = writer.getProperty("com.ctc.wstx.outputUnderlyingStream");
         } catch (Exception var5) {
         }
      }

      if (obj == null) {
         try {
            obj = writer.getProperty("http://java.sun.com/xml/stream/properties/outputstream");
         } catch (Exception var4) {
         }
      }

      if (obj != null) {
         writer.writeCharacters("");
         writer.flush();
         return (OutputStream)obj;
      } else {
         return null;
      }
   }

   @Nullable
   public static String getEncoding(XMLStreamWriter writer) {
      return writer instanceof HasEncoding ? ((HasEncoding)writer).getEncoding() : null;
   }

   public static String encodeQName(XMLStreamWriter writer, QName qname, PrefixFactory prefixFactory) {
      try {
         String namespaceURI = qname.getNamespaceURI();
         String localPart = qname.getLocalPart();
         if (namespaceURI != null && !namespaceURI.equals("")) {
            String prefix = writer.getPrefix(namespaceURI);
            if (prefix == null) {
               prefix = prefixFactory.getPrefix(namespaceURI);
               writer.writeNamespace(prefix, namespaceURI);
            }

            return prefix + ":" + localPart;
         } else {
            return localPart;
         }
      } catch (XMLStreamException var6) {
         throw new RuntimeException(var6);
      }
   }
}
