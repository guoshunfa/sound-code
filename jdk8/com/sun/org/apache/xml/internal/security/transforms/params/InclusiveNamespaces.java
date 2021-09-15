package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InclusiveNamespaces extends ElementProxy implements TransformParam {
   public static final String _TAG_EC_INCLUSIVENAMESPACES = "InclusiveNamespaces";
   public static final String _ATT_EC_PREFIXLIST = "PrefixList";
   public static final String ExclusiveCanonicalizationNamespace = "http://www.w3.org/2001/10/xml-exc-c14n#";

   public InclusiveNamespaces(Document var1, String var2) {
      this((Document)var1, (Set)prefixStr2Set(var2));
   }

   public InclusiveNamespaces(Document var1, Set<String> var2) {
      super(var1);
      Object var3 = null;
      if (var2 instanceof SortedSet) {
         var3 = (SortedSet)var2;
      } else {
         var3 = new TreeSet(var2);
      }

      StringBuilder var4 = new StringBuilder();
      Iterator var5 = ((SortedSet)var3).iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (var6.equals("xmlns")) {
            var4.append("#default ");
         } else {
            var4.append(var6 + " ");
         }
      }

      this.constructionElement.setAttributeNS((String)null, "PrefixList", var4.toString().trim());
   }

   public InclusiveNamespaces(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public String getInclusiveNamespaces() {
      return this.constructionElement.getAttributeNS((String)null, "PrefixList");
   }

   public static SortedSet<String> prefixStr2Set(String var0) {
      TreeSet var1 = new TreeSet();
      if (var0 != null && var0.length() != 0) {
         String[] var2 = var0.split("\\s");
         String[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (var6.equals("#default")) {
               var1.add("xmlns");
            } else {
               var1.add(var6);
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   public String getBaseNamespace() {
      return "http://www.w3.org/2001/10/xml-exc-c14n#";
   }

   public String getBaseLocalName() {
      return "InclusiveNamespaces";
   }
}
