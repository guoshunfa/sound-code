package org.xml.sax.helpers;

import org.xml.sax.Parser;

/** @deprecated */
public class ParserFactory {
   private static SecuritySupport ss = new SecuritySupport();

   private ParserFactory() {
   }

   public static Parser makeParser() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NullPointerException, ClassCastException {
      String className = ss.getSystemProperty("org.xml.sax.parser");
      if (className == null) {
         throw new NullPointerException("No value for sax.parser property");
      } else {
         return makeParser(className);
      }
   }

   public static Parser makeParser(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException {
      return (Parser)NewInstance.newInstance(ss.getContextClassLoader(), className);
   }
}
