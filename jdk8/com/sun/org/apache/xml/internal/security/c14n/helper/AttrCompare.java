package com.sun.org.apache.xml.internal.security.c14n.helper;

import java.io.Serializable;
import java.util.Comparator;
import org.w3c.dom.Attr;

public class AttrCompare implements Comparator<Attr>, Serializable {
   private static final long serialVersionUID = -7113259629930576230L;
   private static final int ATTR0_BEFORE_ATTR1 = -1;
   private static final int ATTR1_BEFORE_ATTR0 = 1;
   private static final String XMLNS = "http://www.w3.org/2000/xmlns/";

   public int compare(Attr var1, Attr var2) {
      String var3 = var1.getNamespaceURI();
      String var4 = var2.getNamespaceURI();
      boolean var5 = "http://www.w3.org/2000/xmlns/".equals(var3);
      boolean var6 = "http://www.w3.org/2000/xmlns/".equals(var4);
      String var8;
      String var9;
      if (var5) {
         if (var6) {
            var9 = var1.getLocalName();
            var8 = var2.getLocalName();
            if ("xmlns".equals(var9)) {
               var9 = "";
            }

            if ("xmlns".equals(var8)) {
               var8 = "";
            }

            return var9.compareTo(var8);
         } else {
            return -1;
         }
      } else if (var6) {
         return 1;
      } else if (var3 == null) {
         if (var4 == null) {
            var9 = var1.getName();
            var8 = var2.getName();
            return var9.compareTo(var8);
         } else {
            return -1;
         }
      } else if (var4 == null) {
         return 1;
      } else {
         int var7 = var3.compareTo(var4);
         return var7 != 0 ? var7 : var1.getLocalName().compareTo(var2.getLocalName());
      }
   }
}
