package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtil {
   private static DocumentBuilder db;

   public static Document createDom() {
      Class var0 = DOMUtil.class;
      synchronized(DOMUtil.class) {
         if (db == null) {
            try {
               DocumentBuilderFactory dbf = XmlUtil.newDocumentBuilderFactory();
               db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException var3) {
               throw new FactoryConfigurationError(var3);
            }
         }

         return db.newDocument();
      }
   }

   public static void serializeNode(Element node, XMLStreamWriter writer) throws XMLStreamException {
      writeTagWithAttributes(node, writer);
      if (node.hasChildNodes()) {
         NodeList children = node.getChildNodes();

         for(int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            switch(child.getNodeType()) {
            case 1:
               serializeNode((Element)child, writer);
            case 2:
            case 5:
            case 6:
            case 9:
            case 10:
            default:
               break;
            case 3:
               writer.writeCharacters(child.getNodeValue());
               break;
            case 4:
               writer.writeCData(child.getNodeValue());
               break;
            case 7:
               writer.writeProcessingInstruction(child.getNodeValue());
               break;
            case 8:
               writer.writeComment(child.getNodeValue());
            }
         }
      }

      writer.writeEndElement();
   }

   public static void writeTagWithAttributes(Element node, XMLStreamWriter writer) throws XMLStreamException {
      String nodePrefix = fixNull(node.getPrefix());
      String nodeNS = fixNull(node.getNamespaceURI());
      String nodeLocalName = node.getLocalName() == null ? node.getNodeName() : node.getLocalName();
      boolean prefixDecl = isPrefixDeclared(writer, nodeNS, nodePrefix);
      writer.writeStartElement(nodePrefix, nodeLocalName, nodeNS);
      NamedNodeMap attrs;
      int numOfAttributes;
      int i;
      Node attr;
      String attrPrefix;
      String attrNS;
      if (node.hasAttributes()) {
         attrs = node.getAttributes();
         numOfAttributes = attrs.getLength();

         for(i = 0; i < numOfAttributes; ++i) {
            attr = attrs.item(i);
            attrPrefix = fixNull(attr.getNamespaceURI());
            if (attrPrefix.equals("http://www.w3.org/2000/xmlns/")) {
               attrNS = attr.getLocalName().equals("xmlns") ? "" : attr.getLocalName();
               if (attrNS.equals(nodePrefix) && attr.getNodeValue().equals(nodeNS)) {
                  prefixDecl = true;
               }

               if (attrNS.equals("")) {
                  writer.writeDefaultNamespace(attr.getNodeValue());
               } else {
                  writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                  writer.writeNamespace(attr.getLocalName(), attr.getNodeValue());
               }
            }
         }
      }

      if (!prefixDecl) {
         writer.writeNamespace(nodePrefix, nodeNS);
      }

      if (node.hasAttributes()) {
         attrs = node.getAttributes();
         numOfAttributes = attrs.getLength();

         for(i = 0; i < numOfAttributes; ++i) {
            attr = attrs.item(i);
            attrPrefix = fixNull(attr.getPrefix());
            attrNS = fixNull(attr.getNamespaceURI());
            if (!attrNS.equals("http://www.w3.org/2000/xmlns/")) {
               String localName = attr.getLocalName();
               if (localName == null) {
                  localName = attr.getNodeName();
               }

               boolean attrPrefixDecl = isPrefixDeclared(writer, attrNS, attrPrefix);
               if (!attrPrefix.equals("") && !attrPrefixDecl) {
                  writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                  writer.writeNamespace(attrPrefix, attrNS);
               }

               writer.writeAttribute(attrPrefix, attrNS, localName, attr.getNodeValue());
            }
         }
      }

   }

   private static boolean isPrefixDeclared(XMLStreamWriter writer, String nsUri, String prefix) {
      boolean prefixDecl = false;
      NamespaceContext nscontext = writer.getNamespaceContext();
      Iterator prefixItr = nscontext.getPrefixes(nsUri);

      while(prefixItr.hasNext()) {
         if (prefix.equals(prefixItr.next())) {
            prefixDecl = true;
            break;
         }
      }

      return prefixDecl;
   }

   public static Element getFirstChild(Element e, String nsUri, String local) {
      for(Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
         if (n.getNodeType() == 1) {
            Element c = (Element)n;
            if (c.getLocalName().equals(local) && c.getNamespaceURI().equals(nsUri)) {
               return c;
            }
         }
      }

      return null;
   }

   @NotNull
   private static String fixNull(@Nullable String s) {
      return s == null ? "" : s;
   }

   @Nullable
   public static Element getFirstElementChild(Node parent) {
      for(Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
         if (n.getNodeType() == 1) {
            return (Element)n;
         }
      }

      return null;
   }

   @NotNull
   public static List<Element> getChildElements(Node parent) {
      List<Element> elements = new ArrayList();

      for(Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
         if (n.getNodeType() == 1) {
            elements.add((Element)n);
         }
      }

      return elements;
   }
}
