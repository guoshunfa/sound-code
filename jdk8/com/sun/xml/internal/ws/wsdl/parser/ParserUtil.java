package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class ParserUtil {
   public static String getAttribute(XMLStreamReader reader, String name) {
      return reader.getAttributeValue((String)null, name);
   }

   public static String getAttribute(XMLStreamReader reader, String nsUri, String name) {
      return reader.getAttributeValue(nsUri, name);
   }

   public static String getAttribute(XMLStreamReader reader, QName name) {
      return reader.getAttributeValue(name.getNamespaceURI(), name.getLocalPart());
   }

   public static QName getQName(XMLStreamReader reader, String tag) {
      String localName = XmlUtil.getLocalPart(tag);
      String pfix = XmlUtil.getPrefix(tag);
      String uri = reader.getNamespaceURI(fixNull(pfix));
      return new QName(uri, localName);
   }

   public static String getMandatoryNonEmptyAttribute(XMLStreamReader reader, String name) {
      String value = reader.getAttributeValue((String)null, name);
      if (value == null) {
         failWithLocalName("client.missing.attribute", reader, name);
      } else if (value.equals("")) {
         failWithLocalName("client.invalidAttributeValue", reader, name);
      }

      return value;
   }

   public static void failWithFullName(String key, XMLStreamReader reader) {
   }

   public static void failWithLocalName(String key, XMLStreamReader reader) {
   }

   public static void failWithLocalName(String key, XMLStreamReader reader, String arg) {
   }

   @NotNull
   private static String fixNull(@Nullable String s) {
      return s == null ? "" : s;
   }
}
