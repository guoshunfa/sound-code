package com.sun.xml.internal.bind.v2.model.util;

import com.sun.xml.internal.bind.v2.TODO;
import javax.xml.namespace.QName;

public class ArrayInfoUtil {
   private ArrayInfoUtil() {
   }

   public static QName calcArrayTypeName(QName n) {
      String uri;
      if (n.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
         TODO.checkSpec("this URI");
         uri = "http://jaxb.dev.java.net/array";
      } else {
         uri = n.getNamespaceURI();
      }

      return new QName(uri, n.getLocalPart() + "Array");
   }
}
