package com.sun.rowset.internal;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class XmlResolver implements EntityResolver {
   public InputSource resolveEntity(String var1, String var2) {
      String var3 = var2.substring(var2.lastIndexOf("/"));
      return var2.startsWith("http://java.sun.com/xml/ns/jdbc") ? new InputSource(this.getClass().getResourceAsStream(var3)) : null;
   }
}
