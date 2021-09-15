package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl;
import java.lang.reflect.Method;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;

public class DOMUtil {
   protected DOMUtil() {
   }

   public static void copyInto(Node src, Node dest) throws DOMException {
      Document factory = ((Node)dest).getOwnerDocument();
      boolean domimpl = factory instanceof DocumentImpl;
      Node start = src;
      Node parent = src;
      Node place = src;

      while(place != null) {
         Object node;
         node = null;
         int type = place.getNodeType();
         label46:
         switch(type) {
         case 1:
            Element element = factory.createElement(place.getNodeName());
            node = element;
            NamedNodeMap attrs = place.getAttributes();
            int attrCount = attrs.getLength();
            int i = 0;

            while(true) {
               if (i >= attrCount) {
                  break label46;
               }

               Attr attr = (Attr)attrs.item(i);
               String attrName = attr.getNodeName();
               String attrValue = attr.getNodeValue();
               element.setAttribute(attrName, attrValue);
               if (domimpl && !attr.getSpecified()) {
                  ((AttrImpl)element.getAttributeNode(attrName)).setSpecified(false);
               }

               ++i;
            }
         case 2:
         case 6:
         default:
            throw new IllegalArgumentException("can't copy node type, " + type + " (" + place.getNodeName() + ')');
         case 3:
            node = factory.createTextNode(place.getNodeValue());
            break;
         case 4:
            node = factory.createCDATASection(place.getNodeValue());
            break;
         case 5:
            node = factory.createEntityReference(place.getNodeName());
            break;
         case 7:
            node = factory.createProcessingInstruction(place.getNodeName(), place.getNodeValue());
            break;
         case 8:
            node = factory.createComment(place.getNodeValue());
         }

         ((Node)dest).appendChild((Node)node);
         if (place.hasChildNodes()) {
            parent = place;
            place = place.getFirstChild();
            dest = node;
         } else {
            for(place = place.getNextSibling(); place == null && parent != start; dest = ((Node)dest).getParentNode()) {
               place = parent.getNextSibling();
               parent = parent.getParentNode();
            }
         }
      }

   }

   public static Element getFirstChildElement(Node parent) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getFirstVisibleChildElement(Node parent) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1 && !isHidden(child)) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getFirstVisibleChildElement(Node parent, Map<Node, String> hiddenNodes) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1 && !isHidden(child, hiddenNodes)) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getLastChildElement(Node parent) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getLastVisibleChildElement(Node parent) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1 && !isHidden(child)) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getLastVisibleChildElement(Node parent, Map<Node, String> hiddenNodes) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1 && !isHidden(child, hiddenNodes)) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getNextSiblingElement(Node node) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1) {
            return (Element)sibling;
         }
      }

      return null;
   }

   public static Element getNextVisibleSiblingElement(Node node) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1 && !isHidden(sibling)) {
            return (Element)sibling;
         }
      }

      return null;
   }

   public static Element getNextVisibleSiblingElement(Node node, Map<Node, String> hiddenNodes) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1 && !isHidden(sibling, hiddenNodes)) {
            return (Element)sibling;
         }
      }

      return null;
   }

   public static void setHidden(Node node) {
      if (node instanceof NodeImpl) {
         ((NodeImpl)node).setReadOnly(true, false);
      } else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl) {
         ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(true, false);
      }

   }

   public static void setHidden(Node node, Map<Node, String> hiddenNodes) {
      if (node instanceof NodeImpl) {
         ((NodeImpl)node).setReadOnly(true, false);
      } else {
         hiddenNodes.put(node, "");
      }

   }

   public static void setVisible(Node node) {
      if (node instanceof NodeImpl) {
         ((NodeImpl)node).setReadOnly(false, false);
      } else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl) {
         ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(false, false);
      }

   }

   public static void setVisible(Node node, Map<Node, String> hiddenNodes) {
      if (node instanceof NodeImpl) {
         ((NodeImpl)node).setReadOnly(false, false);
      } else {
         hiddenNodes.remove(node);
      }

   }

   public static boolean isHidden(Node node) {
      if (node instanceof NodeImpl) {
         return ((NodeImpl)node).getReadOnly();
      } else {
         return node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl ? ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).getReadOnly() : false;
      }
   }

   public static boolean isHidden(Node node, Map<Node, String> hiddenNodes) {
      return node instanceof NodeImpl ? ((NodeImpl)node).getReadOnly() : hiddenNodes.containsKey(node);
   }

   public static Element getFirstChildElement(Node parent, String elemName) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1 && child.getNodeName().equals(elemName)) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getLastChildElement(Node parent, String elemName) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1 && child.getNodeName().equals(elemName)) {
            return (Element)child;
         }
      }

      return null;
   }

   public static Element getNextSiblingElement(Node node, String elemName) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1 && sibling.getNodeName().equals(elemName)) {
            return (Element)sibling;
         }
      }

      return null;
   }

   public static Element getFirstChildElementNS(Node parent, String uri, String localpart) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1) {
            String childURI = child.getNamespaceURI();
            if (childURI != null && childURI.equals(uri) && child.getLocalName().equals(localpart)) {
               return (Element)child;
            }
         }
      }

      return null;
   }

   public static Element getLastChildElementNS(Node parent, String uri, String localpart) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1) {
            String childURI = child.getNamespaceURI();
            if (childURI != null && childURI.equals(uri) && child.getLocalName().equals(localpart)) {
               return (Element)child;
            }
         }
      }

      return null;
   }

   public static Element getNextSiblingElementNS(Node node, String uri, String localpart) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1) {
            String siblingURI = sibling.getNamespaceURI();
            if (siblingURI != null && siblingURI.equals(uri) && sibling.getLocalName().equals(localpart)) {
               return (Element)sibling;
            }
         }
      }

      return null;
   }

   public static Element getFirstChildElement(Node parent, String[] elemNames) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1) {
            for(int i = 0; i < elemNames.length; ++i) {
               if (child.getNodeName().equals(elemNames[i])) {
                  return (Element)child;
               }
            }
         }
      }

      return null;
   }

   public static Element getLastChildElement(Node parent, String[] elemNames) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1) {
            for(int i = 0; i < elemNames.length; ++i) {
               if (child.getNodeName().equals(elemNames[i])) {
                  return (Element)child;
               }
            }
         }
      }

      return null;
   }

   public static Element getNextSiblingElement(Node node, String[] elemNames) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1) {
            for(int i = 0; i < elemNames.length; ++i) {
               if (sibling.getNodeName().equals(elemNames[i])) {
                  return (Element)sibling;
               }
            }
         }
      }

      return null;
   }

   public static Element getFirstChildElementNS(Node parent, String[][] elemNames) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1) {
            for(int i = 0; i < elemNames.length; ++i) {
               String uri = child.getNamespaceURI();
               if (uri != null && uri.equals(elemNames[i][0]) && child.getLocalName().equals(elemNames[i][1])) {
                  return (Element)child;
               }
            }
         }
      }

      return null;
   }

   public static Element getLastChildElementNS(Node parent, String[][] elemNames) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1) {
            for(int i = 0; i < elemNames.length; ++i) {
               String uri = child.getNamespaceURI();
               if (uri != null && uri.equals(elemNames[i][0]) && child.getLocalName().equals(elemNames[i][1])) {
                  return (Element)child;
               }
            }
         }
      }

      return null;
   }

   public static Element getNextSiblingElementNS(Node node, String[][] elemNames) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1) {
            for(int i = 0; i < elemNames.length; ++i) {
               String uri = sibling.getNamespaceURI();
               if (uri != null && uri.equals(elemNames[i][0]) && sibling.getLocalName().equals(elemNames[i][1])) {
                  return (Element)sibling;
               }
            }
         }
      }

      return null;
   }

   public static Element getFirstChildElement(Node parent, String elemName, String attrName, String attrValue) {
      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1) {
            Element element = (Element)child;
            if (element.getNodeName().equals(elemName) && element.getAttribute(attrName).equals(attrValue)) {
               return element;
            }
         }
      }

      return null;
   }

   public static Element getLastChildElement(Node parent, String elemName, String attrName, String attrValue) {
      for(Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
         if (child.getNodeType() == 1) {
            Element element = (Element)child;
            if (element.getNodeName().equals(elemName) && element.getAttribute(attrName).equals(attrValue)) {
               return element;
            }
         }
      }

      return null;
   }

   public static Element getNextSiblingElement(Node node, String elemName, String attrName, String attrValue) {
      for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
         if (sibling.getNodeType() == 1) {
            Element element = (Element)sibling;
            if (element.getNodeName().equals(elemName) && element.getAttribute(attrName).equals(attrValue)) {
               return element;
            }
         }
      }

      return null;
   }

   public static String getChildText(Node node) {
      if (node == null) {
         return null;
      } else {
         StringBuffer str = new StringBuffer();

         for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            short type = child.getNodeType();
            if (type == 3) {
               str.append(child.getNodeValue());
            } else if (type == 4) {
               str.append(getChildText(child));
            }
         }

         return str.toString();
      }
   }

   public static String getName(Node node) {
      return node.getNodeName();
   }

   public static String getLocalName(Node node) {
      String name = node.getLocalName();
      return name != null ? name : node.getNodeName();
   }

   public static Element getParent(Element elem) {
      Node parent = elem.getParentNode();
      return parent instanceof Element ? (Element)parent : null;
   }

   public static Document getDocument(Node node) {
      return node.getOwnerDocument();
   }

   public static Element getRoot(Document doc) {
      return doc.getDocumentElement();
   }

   public static Attr getAttr(Element elem, String name) {
      return elem.getAttributeNode(name);
   }

   public static Attr getAttrNS(Element elem, String nsUri, String localName) {
      return elem.getAttributeNodeNS(nsUri, localName);
   }

   public static Attr[] getAttrs(Element elem) {
      NamedNodeMap attrMap = elem.getAttributes();
      Attr[] attrArray = new Attr[attrMap.getLength()];

      for(int i = 0; i < attrMap.getLength(); ++i) {
         attrArray[i] = (Attr)attrMap.item(i);
      }

      return attrArray;
   }

   public static String getValue(Attr attribute) {
      return attribute.getValue();
   }

   public static String getAttrValue(Element elem, String name) {
      return elem.getAttribute(name);
   }

   public static String getAttrValueNS(Element elem, String nsUri, String localName) {
      return elem.getAttributeNS(nsUri, localName);
   }

   public static String getPrefix(Node node) {
      return node.getPrefix();
   }

   public static String getNamespaceURI(Node node) {
      return node.getNamespaceURI();
   }

   public static String getAnnotation(Node node) {
      return node instanceof ElementImpl ? ((ElementImpl)node).getAnnotation() : null;
   }

   public static String getSyntheticAnnotation(Node node) {
      return node instanceof ElementImpl ? ((ElementImpl)node).getSyntheticAnnotation() : null;
   }

   public static DOMException createDOMException(short code, Throwable cause) {
      DOMException de = new DOMException(code, cause != null ? cause.getMessage() : null);
      if (cause != null && DOMUtil.ThrowableMethods.fgThrowableMethodsAvailable) {
         try {
            DOMUtil.ThrowableMethods.fgThrowableInitCauseMethod.invoke(de, cause);
         } catch (Exception var4) {
         }
      }

      return de;
   }

   public static LSException createLSException(short code, Throwable cause) {
      LSException lse = new LSException(code, cause != null ? cause.getMessage() : null);
      if (cause != null && DOMUtil.ThrowableMethods.fgThrowableMethodsAvailable) {
         try {
            DOMUtil.ThrowableMethods.fgThrowableInitCauseMethod.invoke(lse, cause);
         } catch (Exception var4) {
         }
      }

      return lse;
   }

   static class ThrowableMethods {
      private static Method fgThrowableInitCauseMethod = null;
      private static boolean fgThrowableMethodsAvailable = false;

      private ThrowableMethods() {
      }

      static {
         try {
            fgThrowableInitCauseMethod = Throwable.class.getMethod("initCause", Throwable.class);
            fgThrowableMethodsAvailable = true;
         } catch (Exception var1) {
            fgThrowableInitCauseMethod = null;
            fgThrowableMethodsAvailable = false;
         }

      }
   }
}
