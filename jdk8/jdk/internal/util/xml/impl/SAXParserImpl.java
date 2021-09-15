package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.util.xml.SAXParser;

public class SAXParserImpl extends SAXParser {
   private ParserSAX parser = new ParserSAX();

   public XMLReader getXMLReader() throws SAXException {
      return this.parser;
   }

   public boolean isNamespaceAware() {
      return this.parser.mIsNSAware;
   }

   public boolean isValidating() {
      return false;
   }

   public void parse(InputStream var1, DefaultHandler var2) throws SAXException, IOException {
      this.parser.parse(var1, var2);
   }

   public void parse(InputSource var1, DefaultHandler var2) throws SAXException, IOException {
      this.parser.parse(var1, var2);
   }
}
