package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.Node;

public final class DOMInputSource extends XMLInputSource {
   private Node fNode;

   public DOMInputSource() {
      this((Node)null);
   }

   public DOMInputSource(Node node) {
      super((String)null, getSystemIdFromNode(node), (String)null);
      this.fNode = node;
   }

   public DOMInputSource(Node node, String systemId) {
      super((String)null, systemId, (String)null);
      this.fNode = node;
   }

   public Node getNode() {
      return this.fNode;
   }

   public void setNode(Node node) {
      this.fNode = node;
   }

   private static String getSystemIdFromNode(Node node) {
      if (node != null) {
         try {
            return node.getBaseURI();
         } catch (NoSuchMethodError var2) {
            return null;
         } catch (Exception var3) {
            return null;
         }
      } else {
         return null;
      }
   }
}
