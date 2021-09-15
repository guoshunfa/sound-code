package com.sun.xml.internal.bind.v2.runtime;

import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class DomPostInitAction implements Runnable {
   private final Node node;
   private final XMLSerializer serializer;

   DomPostInitAction(Node node, XMLSerializer serializer) {
      this.node = node;
      this.serializer = serializer;
   }

   public void run() {
      Set<String> declaredPrefixes = new HashSet();

      for(Node n = this.node; n != null && n.getNodeType() == 1; n = n.getParentNode()) {
         NamedNodeMap atts = n.getAttributes();
         if (atts != null) {
            for(int i = 0; i < atts.getLength(); ++i) {
               Attr a = (Attr)atts.item(i);
               String nsUri = a.getNamespaceURI();
               if (nsUri != null && nsUri.equals("http://www.w3.org/2000/xmlns/")) {
                  String prefix = a.getLocalName();
                  if (prefix != null) {
                     if (prefix.equals("xmlns")) {
                        prefix = "";
                     }

                     String value = a.getValue();
                     if (value != null && declaredPrefixes.add(prefix)) {
                        this.serializer.addInscopeBinding(value, prefix);
                     }
                  }
               }
            }
         }
      }

   }
}
