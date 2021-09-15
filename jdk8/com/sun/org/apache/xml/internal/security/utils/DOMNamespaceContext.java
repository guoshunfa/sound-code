package com.sun.org.apache.xml.internal.security.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DOMNamespaceContext implements NamespaceContext {
   private Map<String, String> namespaceMap = new HashMap();

   public DOMNamespaceContext(Node var1) {
      this.addNamespaces(var1);
   }

   public String getNamespaceURI(String var1) {
      return (String)this.namespaceMap.get(var1);
   }

   public String getPrefix(String var1) {
      Iterator var2 = this.namespaceMap.keySet().iterator();

      String var3;
      String var4;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (String)var2.next();
         var4 = (String)this.namespaceMap.get(var3);
      } while(!var4.equals(var1));

      return var3;
   }

   public Iterator<String> getPrefixes(String var1) {
      return this.namespaceMap.keySet().iterator();
   }

   private void addNamespaces(Node var1) {
      if (var1.getParentNode() != null) {
         this.addNamespaces(var1.getParentNode());
      }

      if (var1 instanceof Element) {
         Element var2 = (Element)var1;
         NamedNodeMap var3 = var2.getAttributes();

         for(int var4 = 0; var4 < var3.getLength(); ++var4) {
            Attr var5 = (Attr)var3.item(var4);
            if ("xmlns".equals(var5.getPrefix())) {
               this.namespaceMap.put(var5.getLocalName(), var5.getValue());
            }
         }
      }

   }
}
