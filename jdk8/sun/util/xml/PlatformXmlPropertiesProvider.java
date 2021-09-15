package sun.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import sun.util.spi.XmlPropertiesProvider;

public class PlatformXmlPropertiesProvider extends XmlPropertiesProvider {
   private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
   private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
   private static final String EXTERNAL_XML_VERSION = "1.0";

   public void load(Properties var1, InputStream var2) throws IOException, InvalidPropertiesFormatException {
      Document var3 = null;

      try {
         var3 = getLoadingDoc(var2);
      } catch (SAXException var6) {
         throw new InvalidPropertiesFormatException(var6);
      }

      Element var4 = var3.getDocumentElement();
      String var5 = var4.getAttribute("version");
      if (var5.compareTo("1.0") > 0) {
         throw new InvalidPropertiesFormatException("Exported Properties file format version " + var5 + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
      } else {
         importProperties(var1, var4);
      }
   }

   static Document getLoadingDoc(InputStream var0) throws SAXException, IOException {
      DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
      var1.setIgnoringElementContentWhitespace(true);
      var1.setValidating(true);
      var1.setCoalescing(true);
      var1.setIgnoringComments(true);

      try {
         DocumentBuilder var2 = var1.newDocumentBuilder();
         var2.setEntityResolver(new PlatformXmlPropertiesProvider.Resolver());
         var2.setErrorHandler(new PlatformXmlPropertiesProvider.EH());
         InputSource var3 = new InputSource(var0);
         return var2.parse(var3);
      } catch (ParserConfigurationException var4) {
         throw new Error(var4);
      }
   }

   static void importProperties(Properties var0, Element var1) {
      NodeList var2 = var1.getChildNodes();
      int var3 = var2.getLength();
      int var4 = var3 > 0 && var2.item(0).getNodeName().equals("comment") ? 1 : 0;

      for(int var5 = var4; var5 < var3; ++var5) {
         Element var6 = (Element)var2.item(var5);
         if (var6.hasAttribute("key")) {
            Node var7 = var6.getFirstChild();
            String var8 = var7 == null ? "" : var7.getNodeValue();
            var0.setProperty(var6.getAttribute("key"), var8);
         }
      }

   }

   public void store(Properties var1, OutputStream var2, String var3, String var4) throws IOException {
      try {
         Charset.forName(var4);
      } catch (UnsupportedCharsetException | IllegalCharsetNameException var16) {
         throw new UnsupportedEncodingException(var4);
      }

      DocumentBuilderFactory var5 = DocumentBuilderFactory.newInstance();
      DocumentBuilder var6 = null;

      try {
         var6 = var5.newDocumentBuilder();
      } catch (ParserConfigurationException var18) {
         assert false;
      }

      Document var7 = var6.newDocument();
      Element var8 = (Element)var7.appendChild(var7.createElement("properties"));
      if (var3 != null) {
         Element var9 = (Element)var8.appendChild(var7.createElement("comment"));
         var9.appendChild(var7.createTextNode(var3));
      }

      synchronized(var1) {
         Iterator var10 = var1.entrySet().iterator();

         while(var10.hasNext()) {
            Map.Entry var11 = (Map.Entry)var10.next();
            Object var12 = var11.getKey();
            Object var13 = var11.getValue();
            if (var12 instanceof String && var13 instanceof String) {
               Element var14 = (Element)var8.appendChild(var7.createElement("entry"));
               var14.setAttribute("key", (String)var12);
               var14.appendChild(var7.createTextNode((String)var13));
            }
         }
      }

      emitDocument(var7, var2, var4);
   }

   static void emitDocument(Document var0, OutputStream var1, String var2) throws IOException {
      TransformerFactory var3 = TransformerFactory.newInstance();
      Transformer var4 = null;

      try {
         var4 = var3.newTransformer();
         var4.setOutputProperty("doctype-system", "http://java.sun.com/dtd/properties.dtd");
         var4.setOutputProperty("indent", "yes");
         var4.setOutputProperty("method", "xml");
         var4.setOutputProperty("encoding", var2);
      } catch (TransformerConfigurationException var9) {
         assert false;
      }

      DOMSource var5 = new DOMSource(var0);
      StreamResult var6 = new StreamResult(var1);

      try {
         var4.transform(var5, var6);
      } catch (TransformerException var8) {
         throw new IOException(var8);
      }
   }

   private static class EH implements ErrorHandler {
      private EH() {
      }

      public void error(SAXParseException var1) throws SAXException {
         throw var1;
      }

      public void fatalError(SAXParseException var1) throws SAXException {
         throw var1;
      }

      public void warning(SAXParseException var1) throws SAXException {
         throw var1;
      }

      // $FF: synthetic method
      EH(Object var1) {
         this();
      }
   }

   private static class Resolver implements EntityResolver {
      private Resolver() {
      }

      public InputSource resolveEntity(String var1, String var2) throws SAXException {
         if (var2.equals("http://java.sun.com/dtd/properties.dtd")) {
            InputSource var3 = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
            var3.setSystemId("http://java.sun.com/dtd/properties.dtd");
            return var3;
         } else {
            throw new SAXException("Invalid system identifier: " + var2);
         }
      }

      // $FF: synthetic method
      Resolver(Object var1) {
         this();
      }
   }
}
