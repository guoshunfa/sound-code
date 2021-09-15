package com.sun.org.apache.xml.internal.security.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** @deprecated */
@Deprecated
public class IdResolver {
   private IdResolver() {
   }

   public static void registerElementById(Element var0, Attr var1) {
      var0.setIdAttributeNode(var1, true);
   }

   public static Element getElementById(Document var0, String var1) {
      return var0.getElementById(var1);
   }
}
