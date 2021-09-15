package org.jcp.xml.dsig.internal.dom;

import java.security.spec.AlgorithmParameterSpec;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtils {
   private DOMUtils() {
   }

   public static Document getOwnerDocument(Node var0) {
      return var0.getNodeType() == 9 ? (Document)var0 : var0.getOwnerDocument();
   }

   public static Element createElement(Document var0, String var1, String var2, String var3) {
      String var4 = var3 != null && var3.length() != 0 ? var3 + ":" + var1 : var1;
      return var0.createElementNS(var2, var4);
   }

   public static void setAttribute(Element var0, String var1, String var2) {
      if (var2 != null) {
         var0.setAttributeNS((String)null, var1, var2);
      }
   }

   public static void setAttributeID(Element var0, String var1, String var2) {
      if (var2 != null) {
         var0.setAttributeNS((String)null, var1, var2);
         var0.setIdAttributeNS((String)null, var1, true);
      }
   }

   public static Element getFirstChildElement(Node var0) {
      Node var1;
      for(var1 = var0.getFirstChild(); var1 != null && var1.getNodeType() != 1; var1 = var1.getNextSibling()) {
      }

      return (Element)var1;
   }

   public static Element getFirstChildElement(Node var0, String var1) throws MarshalException {
      return verifyElement(getFirstChildElement(var0), var1);
   }

   private static Element verifyElement(Element var0, String var1) throws MarshalException {
      if (var0 == null) {
         throw new MarshalException("Missing " + var1 + " element");
      } else {
         String var2 = var0.getLocalName();
         if (!var2.equals(var1)) {
            throw new MarshalException("Invalid element name: " + var2 + ", expected " + var1);
         } else {
            return var0;
         }
      }
   }

   public static Element getLastChildElement(Node var0) {
      Node var1;
      for(var1 = var0.getLastChild(); var1 != null && var1.getNodeType() != 1; var1 = var1.getPreviousSibling()) {
      }

      return (Element)var1;
   }

   public static Element getNextSiblingElement(Node var0) {
      Node var1;
      for(var1 = var0.getNextSibling(); var1 != null && var1.getNodeType() != 1; var1 = var1.getNextSibling()) {
      }

      return (Element)var1;
   }

   public static Element getNextSiblingElement(Node var0, String var1) throws MarshalException {
      return verifyElement(getNextSiblingElement(var0), var1);
   }

   public static String getAttributeValue(Element var0, String var1) {
      Attr var2 = var0.getAttributeNodeNS((String)null, var1);
      return var2 == null ? null : var2.getValue();
   }

   public static Set<Node> nodeSet(NodeList var0) {
      return new DOMUtils.NodeSet(var0);
   }

   public static String getNSPrefix(XMLCryptoContext var0, String var1) {
      return var0 != null ? var0.getNamespacePrefix(var1, var0.getDefaultNamespacePrefix()) : null;
   }

   public static String getSignaturePrefix(XMLCryptoContext var0) {
      return getNSPrefix(var0, "http://www.w3.org/2000/09/xmldsig#");
   }

   public static void removeAllChildren(Node var0) {
      NodeList var1 = var0.getChildNodes();
      int var2 = 0;

      for(int var3 = var1.getLength(); var2 < var3; ++var2) {
         var0.removeChild(var1.item(var2));
      }

   }

   public static boolean nodesEqual(Node var0, Node var1) {
      if (var0 == var1) {
         return true;
      } else {
         return var0.getNodeType() == var1.getNodeType();
      }
   }

   public static void appendChild(Node var0, Node var1) {
      Document var2 = getOwnerDocument(var0);
      if (var1.getOwnerDocument() != var2) {
         var0.appendChild(var2.importNode(var1, true));
      } else {
         var0.appendChild(var1);
      }

   }

   public static boolean paramsEqual(AlgorithmParameterSpec var0, AlgorithmParameterSpec var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 instanceof XPathFilter2ParameterSpec && var1 instanceof XPathFilter2ParameterSpec) {
         return paramsEqual((XPathFilter2ParameterSpec)var0, (XPathFilter2ParameterSpec)var1);
      } else if (var0 instanceof ExcC14NParameterSpec && var1 instanceof ExcC14NParameterSpec) {
         return paramsEqual((ExcC14NParameterSpec)var0, (ExcC14NParameterSpec)var1);
      } else if (var0 instanceof XPathFilterParameterSpec && var1 instanceof XPathFilterParameterSpec) {
         return paramsEqual((XPathFilterParameterSpec)var0, (XPathFilterParameterSpec)var1);
      } else {
         return var0 instanceof XSLTTransformParameterSpec && var1 instanceof XSLTTransformParameterSpec ? paramsEqual((XSLTTransformParameterSpec)var0, (XSLTTransformParameterSpec)var1) : false;
      }
   }

   private static boolean paramsEqual(XPathFilter2ParameterSpec var0, XPathFilter2ParameterSpec var1) {
      List var2 = var0.getXPathList();
      List var3 = var1.getXPathList();
      int var4 = var2.size();
      if (var4 != var3.size()) {
         return false;
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            XPathType var6 = (XPathType)var2.get(var5);
            XPathType var7 = (XPathType)var3.get(var5);
            if (!var6.getExpression().equals(var7.getExpression()) || !var6.getNamespaceMap().equals(var7.getNamespaceMap()) || var6.getFilter() != var7.getFilter()) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean paramsEqual(ExcC14NParameterSpec var0, ExcC14NParameterSpec var1) {
      return var0.getPrefixList().equals(var1.getPrefixList());
   }

   private static boolean paramsEqual(XPathFilterParameterSpec var0, XPathFilterParameterSpec var1) {
      return var0.getXPath().equals(var1.getXPath()) && var0.getNamespaceMap().equals(var1.getNamespaceMap());
   }

   private static boolean paramsEqual(XSLTTransformParameterSpec var0, XSLTTransformParameterSpec var1) {
      XMLStructure var2 = var1.getStylesheet();
      if (!(var2 instanceof javax.xml.crypto.dom.DOMStructure)) {
         return false;
      } else {
         Node var3 = ((javax.xml.crypto.dom.DOMStructure)var2).getNode();
         XMLStructure var4 = var0.getStylesheet();
         Node var5 = ((javax.xml.crypto.dom.DOMStructure)var4).getNode();
         return nodesEqual(var5, var3);
      }
   }

   static class NodeSet extends AbstractSet<Node> {
      private NodeList nl;

      public NodeSet(NodeList var1) {
         this.nl = var1;
      }

      public int size() {
         return this.nl.getLength();
      }

      public Iterator<Node> iterator() {
         return new Iterator<Node>() {
            int index = 0;

            public void remove() {
               throw new UnsupportedOperationException();
            }

            public Node next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return NodeSet.this.nl.item(this.index++);
               }
            }

            public boolean hasNext() {
               return this.index < NodeSet.this.nl.getLength();
            }
         };
      }
   }
}
