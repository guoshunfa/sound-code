package org.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class Utils {
   private Utils() {
   }

   public static byte[] readBytesFromStream(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      byte[] var2 = new byte[1024];

      int var3;
      do {
         var3 = var0.read(var2);
         if (var3 == -1) {
            break;
         }

         var1.write(var2, 0, var3);
      } while(var3 >= 1024);

      return var1.toByteArray();
   }

   static Set<Node> toNodeSet(Iterator<Node> var0) {
      HashSet var1 = new HashSet();

      while(true) {
         Node var2;
         do {
            if (!var0.hasNext()) {
               return var1;
            }

            var2 = (Node)var0.next();
            var1.add(var2);
         } while(var2.getNodeType() != 1);

         NamedNodeMap var3 = var2.getAttributes();
         int var4 = 0;

         for(int var5 = var3.getLength(); var4 < var5; ++var4) {
            var1.add(var3.item(var4));
         }
      }
   }

   public static String parseIdFromSameDocumentURI(String var0) {
      if (var0.length() == 0) {
         return null;
      } else {
         String var1 = var0.substring(1);
         if (var1 != null && var1.startsWith("xpointer(id(")) {
            int var2 = var1.indexOf(39);
            int var3 = var1.indexOf(39, var2 + 1);
            var1 = var1.substring(var2 + 1, var3);
         }

         return var1;
      }
   }

   public static boolean sameDocumentURI(String var0) {
      return var0 != null && (var0.length() == 0 || var0.charAt(0) == '#');
   }

   static boolean secureValidation(XMLCryptoContext var0) {
      return var0 == null ? false : getBoolean(var0, "org.jcp.xml.dsig.secureValidation");
   }

   private static boolean getBoolean(XMLCryptoContext var0, String var1) {
      Boolean var2 = (Boolean)var0.getProperty(var1);
      return var2 != null && var2;
   }
}
