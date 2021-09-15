package java.util.prefs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class XmlSupport {
   private static final String PREFS_DTD_URI = "http://java.sun.com/dtd/preferences.dtd";
   private static final String PREFS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >";
   private static final String EXTERNAL_XML_VERSION = "1.0";
   private static final String MAP_XML_VERSION = "1.0";

   static void export(OutputStream var0, Preferences var1, boolean var2) throws IOException, BackingStoreException {
      if (((AbstractPreferences)var1).isRemoved()) {
         throw new IllegalStateException("Node has been removed");
      } else {
         Document var3 = createPrefsDoc("preferences");
         Element var4 = var3.getDocumentElement();
         var4.setAttribute("EXTERNAL_XML_VERSION", "1.0");
         Element var5 = (Element)var4.appendChild(var3.createElement("root"));
         var5.setAttribute("type", var1.isUserNode() ? "user" : "system");
         ArrayList var6 = new ArrayList();
         Preferences var7 = var1;

         for(Preferences var8 = var1.parent(); var8 != null; var8 = var8.parent()) {
            var6.add(var7);
            var7 = var8;
         }

         Element var9 = var5;

         for(int var10 = var6.size() - 1; var10 >= 0; --var10) {
            var9.appendChild(var3.createElement("map"));
            var9 = (Element)var9.appendChild(var3.createElement("node"));
            var9.setAttribute("name", ((Preferences)var6.get(var10)).name());
         }

         putPreferencesInXml(var9, var3, var1, var2);
         writeDoc(var3, var0);
      }
   }

   private static void putPreferencesInXml(Element var0, Document var1, Preferences var2, boolean var3) throws BackingStoreException {
      Preferences[] var4 = null;
      String[] var5 = null;
      synchronized(((AbstractPreferences)var2).lock) {
         if (((AbstractPreferences)var2).isRemoved()) {
            var0.getParentNode().removeChild(var0);
            return;
         }

         String[] var7 = var2.keys();
         Element var8 = (Element)var0.appendChild(var1.createElement("map"));
         int var9 = 0;

         while(true) {
            if (var9 >= var7.length) {
               if (var3) {
                  var5 = var2.childrenNames();
                  var4 = new Preferences[var5.length];

                  for(var9 = 0; var9 < var5.length; ++var9) {
                     var4[var9] = var2.node(var5[var9]);
                  }
               }
               break;
            }

            Element var10 = (Element)var8.appendChild(var1.createElement("entry"));
            var10.setAttribute("key", var7[var9]);
            var10.setAttribute("value", var2.get(var7[var9], (String)null));
            ++var9;
         }
      }

      if (var3) {
         for(int var6 = 0; var6 < var5.length; ++var6) {
            Element var13 = (Element)var0.appendChild(var1.createElement("node"));
            var13.setAttribute("name", var5[var6]);
            putPreferencesInXml(var13, var1, var4[var6], var3);
         }
      }

   }

   static void importPreferences(InputStream var0) throws IOException, InvalidPreferencesFormatException {
      try {
         Document var1 = loadPrefsDoc(var0);
         String var2 = var1.getDocumentElement().getAttribute("EXTERNAL_XML_VERSION");
         if (var2.compareTo("1.0") > 0) {
            throw new InvalidPreferencesFormatException("Exported preferences file format version " + var2 + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
         } else {
            Element var3 = (Element)var1.getDocumentElement().getChildNodes().item(0);
            Preferences var4 = var3.getAttribute("type").equals("user") ? Preferences.userRoot() : Preferences.systemRoot();
            ImportSubtree(var4, var3);
         }
      } catch (SAXException var5) {
         throw new InvalidPreferencesFormatException(var5);
      }
   }

   private static Document createPrefsDoc(String var0) {
      try {
         DOMImplementation var1 = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
         DocumentType var2 = var1.createDocumentType(var0, (String)null, "http://java.sun.com/dtd/preferences.dtd");
         return var1.createDocument((String)null, var0, var2);
      } catch (ParserConfigurationException var3) {
         throw new AssertionError(var3);
      }
   }

   private static Document loadPrefsDoc(InputStream var0) throws SAXException, IOException {
      DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
      var1.setIgnoringElementContentWhitespace(true);
      var1.setValidating(true);
      var1.setCoalescing(true);
      var1.setIgnoringComments(true);

      try {
         DocumentBuilder var2 = var1.newDocumentBuilder();
         var2.setEntityResolver(new XmlSupport.Resolver());
         var2.setErrorHandler(new XmlSupport.EH());
         return var2.parse(new InputSource(var0));
      } catch (ParserConfigurationException var3) {
         throw new AssertionError(var3);
      }
   }

   private static final void writeDoc(Document var0, OutputStream var1) throws IOException {
      try {
         TransformerFactory var2 = TransformerFactory.newInstance();

         try {
            var2.setAttribute("indent-number", new Integer(2));
         } catch (IllegalArgumentException var4) {
         }

         Transformer var3 = var2.newTransformer();
         var3.setOutputProperty("doctype-system", var0.getDoctype().getSystemId());
         var3.setOutputProperty("indent", "yes");
         var3.transform(new DOMSource(var0), new StreamResult(new BufferedWriter(new OutputStreamWriter(var1, "UTF-8"))));
      } catch (TransformerException var5) {
         throw new AssertionError(var5);
      }
   }

   private static void ImportSubtree(Preferences var0, Element var1) {
      NodeList var2 = var1.getChildNodes();
      int var3 = var2.getLength();
      Preferences[] var4;
      synchronized(((AbstractPreferences)var0).lock) {
         if (((AbstractPreferences)var0).isRemoved()) {
            return;
         }

         Element var6 = (Element)var2.item(0);
         ImportPrefs(var0, var6);
         var4 = new Preferences[var3 - 1];
         int var7 = 1;

         while(true) {
            if (var7 >= var3) {
               break;
            }

            Element var8 = (Element)var2.item(var7);
            var4[var7 - 1] = var0.node(var8.getAttribute("name"));
            ++var7;
         }
      }

      for(int var5 = 1; var5 < var3; ++var5) {
         ImportSubtree(var4[var5 - 1], (Element)var2.item(var5));
      }

   }

   private static void ImportPrefs(Preferences var0, Element var1) {
      NodeList var2 = var1.getChildNodes();
      int var3 = 0;

      for(int var4 = var2.getLength(); var3 < var4; ++var3) {
         Element var5 = (Element)var2.item(var3);
         var0.put(var5.getAttribute("key"), var5.getAttribute("value"));
      }

   }

   static void exportMap(OutputStream var0, Map<String, String> var1) throws IOException {
      Document var2 = createPrefsDoc("map");
      Element var3 = var2.getDocumentElement();
      var3.setAttribute("MAP_XML_VERSION", "1.0");
      Iterator var4 = var1.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         Element var6 = (Element)var3.appendChild(var2.createElement("entry"));
         var6.setAttribute("key", (String)var5.getKey());
         var6.setAttribute("value", (String)var5.getValue());
      }

      writeDoc(var2, var0);
   }

   static void importMap(InputStream var0, Map<String, String> var1) throws IOException, InvalidPreferencesFormatException {
      try {
         Document var2 = loadPrefsDoc(var0);
         Element var3 = var2.getDocumentElement();
         String var4 = var3.getAttribute("MAP_XML_VERSION");
         if (var4.compareTo("1.0") > 0) {
            throw new InvalidPreferencesFormatException("Preferences map file format version " + var4 + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
         } else {
            NodeList var5 = var3.getChildNodes();
            int var6 = 0;

            for(int var7 = var5.getLength(); var6 < var7; ++var6) {
               Element var8 = (Element)var5.item(var6);
               var1.put(var8.getAttribute("key"), var8.getAttribute("value"));
            }

         }
      } catch (SAXException var9) {
         throw new InvalidPreferencesFormatException(var9);
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
         if (var2.equals("http://java.sun.com/dtd/preferences.dtd")) {
            InputSource var3 = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >"));
            var3.setSystemId("http://java.sun.com/dtd/preferences.dtd");
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
