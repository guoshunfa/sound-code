package com.sun.org.apache.xml.internal.security.c14n.helper;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class C14nHelper {
   private C14nHelper() {
   }

   public static boolean namespaceIsRelative(Attr var0) {
      return !namespaceIsAbsolute(var0);
   }

   public static boolean namespaceIsRelative(String var0) {
      return !namespaceIsAbsolute(var0);
   }

   public static boolean namespaceIsAbsolute(Attr var0) {
      return namespaceIsAbsolute(var0.getValue());
   }

   public static boolean namespaceIsAbsolute(String var0) {
      if (var0.length() == 0) {
         return true;
      } else {
         return var0.indexOf(58) > 0;
      }
   }

   public static void assertNotRelativeNS(Attr var0) throws CanonicalizationException {
      if (var0 != null) {
         String var1 = var0.getNodeName();
         boolean var2 = var1.equals("xmlns");
         boolean var3 = var1.startsWith("xmlns:");
         if ((var2 || var3) && namespaceIsRelative(var0)) {
            String var4 = var0.getOwnerElement().getTagName();
            String var5 = var0.getValue();
            Object[] var6 = new Object[]{var4, var1, var5};
            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", var6);
         }
      }
   }

   public static void checkTraversability(Document var0) throws CanonicalizationException {
      if (!var0.isSupported("Traversal", "2.0")) {
         Object[] var1 = new Object[]{var0.getImplementation().getClass().getName()};
         throw new CanonicalizationException("c14n.Canonicalizer.TraversalNotSupported", var1);
      }
   }

   public static void checkForRelativeNamespace(Element var0) throws CanonicalizationException {
      if (var0 == null) {
         throw new CanonicalizationException("Called checkForRelativeNamespace() on null");
      } else {
         NamedNodeMap var1 = var0.getAttributes();

         for(int var2 = 0; var2 < var1.getLength(); ++var2) {
            assertNotRelativeNS((Attr)var1.item(var2));
         }

      }
   }
}
