package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;

public class ResolvingXMLReader extends ResolvingXMLFilter {
   public static boolean namespaceAware = true;
   public static boolean validating = false;

   public ResolvingXMLReader() {
      SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
      spf.setValidating(validating);

      try {
         SAXParser parser = spf.newSAXParser();
         this.setParent(parser.getXMLReader());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public ResolvingXMLReader(CatalogManager manager) {
      super(manager);
      SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
      spf.setValidating(validating);

      try {
         SAXParser parser = spf.newSAXParser();
         this.setParent(parser.getXMLReader());
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }
}
