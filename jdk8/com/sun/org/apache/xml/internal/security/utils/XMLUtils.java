package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class XMLUtils {
   private static boolean ignoreLineBreaks = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.getBoolean("com.sun.org.apache.xml.internal.security.ignoreLineBreaks");
      }
   });
   private static volatile String dsPrefix = "ds";
   private static volatile String ds11Prefix = "dsig11";
   private static volatile String xencPrefix = "xenc";
   private static volatile String xenc11Prefix = "xenc11";
   private static final Logger log = Logger.getLogger(XMLUtils.class.getName());

   private XMLUtils() {
   }

   public static void setDsPrefix(String var0) {
      JavaUtils.checkRegisterPermission();
      dsPrefix = var0;
   }

   public static void setDs11Prefix(String var0) {
      JavaUtils.checkRegisterPermission();
      ds11Prefix = var0;
   }

   public static void setXencPrefix(String var0) {
      JavaUtils.checkRegisterPermission();
      xencPrefix = var0;
   }

   public static void setXenc11Prefix(String var0) {
      JavaUtils.checkRegisterPermission();
      xenc11Prefix = var0;
   }

   public static Element getNextElement(Node var0) {
      Node var1;
      for(var1 = var0; var1 != null && var1.getNodeType() != 1; var1 = var1.getNextSibling()) {
      }

      return (Element)var1;
   }

   public static void getSet(Node var0, Set<Node> var1, Node var2, boolean var3) {
      if (var2 == null || !isDescendantOrSelf(var2, var0)) {
         getSetRec(var0, var1, var2, var3);
      }
   }

   private static void getSetRec(Node var0, Set<Node> var1, Node var2, boolean var3) {
      if (var0 != var2) {
         switch(var0.getNodeType()) {
         case 1:
            var1.add(var0);
            Element var4 = (Element)var0;
            if (var4.hasAttributes()) {
               NamedNodeMap var5 = var4.getAttributes();

               for(int var6 = 0; var6 < var5.getLength(); ++var6) {
                  var1.add(var5.item(var6));
               }
            }
         case 9:
            for(Node var7 = var0.getFirstChild(); var7 != null; var7 = var7.getNextSibling()) {
               if (var7.getNodeType() == 3) {
                  var1.add(var7);

                  while(var7 != null && var7.getNodeType() == 3) {
                     var7 = var7.getNextSibling();
                  }

                  if (var7 == null) {
                     return;
                  }
               }

               getSetRec(var7, var1, var2, var3);
            }

            return;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            var1.add(var0);
            return;
         case 8:
            if (var3) {
               var1.add(var0);
            }

            return;
         case 10:
         }
      }
   }

   public static void outputDOM(Node var0, OutputStream var1) {
      outputDOM(var0, var1, false);
   }

   public static void outputDOM(Node var0, OutputStream var1, boolean var2) {
      try {
         if (var2) {
            var1.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
         }

         var1.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(var0));
      } catch (IOException var4) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var4.getMessage(), (Throwable)var4);
         }
      } catch (InvalidCanonicalizerException var5) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var5.getMessage(), (Throwable)var5);
         }
      } catch (CanonicalizationException var6) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var6.getMessage(), (Throwable)var6);
         }
      }

   }

   public static void outputDOMc14nWithComments(Node var0, OutputStream var1) {
      try {
         var1.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(var0));
      } catch (IOException var3) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var3.getMessage(), (Throwable)var3);
         }
      } catch (InvalidCanonicalizerException var4) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var4.getMessage(), (Throwable)var4);
         }
      } catch (CanonicalizationException var5) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var5.getMessage(), (Throwable)var5);
         }
      }

   }

   public static String getFullTextChildrenFromElement(Element var0) {
      StringBuilder var1 = new StringBuilder();

      for(Node var2 = var0.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if (var2.getNodeType() == 3) {
            var1.append(((Text)var2).getData());
         }
      }

      return var1.toString();
   }

   public static Element createElementInSignatureSpace(Document var0, String var1) {
      if (var0 == null) {
         throw new RuntimeException("Document is null");
      } else {
         return dsPrefix != null && dsPrefix.length() != 0 ? var0.createElementNS("http://www.w3.org/2000/09/xmldsig#", dsPrefix + ":" + var1) : var0.createElementNS("http://www.w3.org/2000/09/xmldsig#", var1);
      }
   }

   public static Element createElementInSignature11Space(Document var0, String var1) {
      if (var0 == null) {
         throw new RuntimeException("Document is null");
      } else {
         return ds11Prefix != null && ds11Prefix.length() != 0 ? var0.createElementNS("http://www.w3.org/2009/xmldsig11#", ds11Prefix + ":" + var1) : var0.createElementNS("http://www.w3.org/2009/xmldsig11#", var1);
      }
   }

   public static Element createElementInEncryptionSpace(Document var0, String var1) {
      if (var0 == null) {
         throw new RuntimeException("Document is null");
      } else {
         return xencPrefix != null && xencPrefix.length() != 0 ? var0.createElementNS("http://www.w3.org/2001/04/xmlenc#", xencPrefix + ":" + var1) : var0.createElementNS("http://www.w3.org/2001/04/xmlenc#", var1);
      }
   }

   public static Element createElementInEncryption11Space(Document var0, String var1) {
      if (var0 == null) {
         throw new RuntimeException("Document is null");
      } else {
         return xenc11Prefix != null && xenc11Prefix.length() != 0 ? var0.createElementNS("http://www.w3.org/2009/xmlenc11#", xenc11Prefix + ":" + var1) : var0.createElementNS("http://www.w3.org/2009/xmlenc11#", var1);
      }
   }

   public static boolean elementIsInSignatureSpace(Element var0, String var1) {
      if (var0 == null) {
         return false;
      } else {
         return "http://www.w3.org/2000/09/xmldsig#".equals(var0.getNamespaceURI()) && var0.getLocalName().equals(var1);
      }
   }

   public static boolean elementIsInSignature11Space(Element var0, String var1) {
      if (var0 == null) {
         return false;
      } else {
         return "http://www.w3.org/2009/xmldsig11#".equals(var0.getNamespaceURI()) && var0.getLocalName().equals(var1);
      }
   }

   public static boolean elementIsInEncryptionSpace(Element var0, String var1) {
      if (var0 == null) {
         return false;
      } else {
         return "http://www.w3.org/2001/04/xmlenc#".equals(var0.getNamespaceURI()) && var0.getLocalName().equals(var1);
      }
   }

   public static boolean elementIsInEncryption11Space(Element var0, String var1) {
      if (var0 == null) {
         return false;
      } else {
         return "http://www.w3.org/2009/xmlenc11#".equals(var0.getNamespaceURI()) && var0.getLocalName().equals(var1);
      }
   }

   public static Document getOwnerDocument(Node var0) {
      if (var0.getNodeType() == 9) {
         return (Document)var0;
      } else {
         try {
            return var0.getOwnerDocument();
         } catch (NullPointerException var2) {
            throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + var2.getMessage() + "\"");
         }
      }
   }

   public static Document getOwnerDocument(Set<Node> var0) {
      NullPointerException var1 = null;
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Node var3 = (Node)var2.next();
         short var4 = var3.getNodeType();
         if (var4 == 9) {
            return (Document)var3;
         }

         try {
            if (var4 == 2) {
               return ((Attr)var3).getOwnerElement().getOwnerDocument();
            }

            return var3.getOwnerDocument();
         } catch (NullPointerException var6) {
            var1 = var6;
         }
      }

      throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + (var1 == null ? "" : var1.getMessage()) + "\"");
   }

   public static Element createDSctx(Document var0, String var1, String var2) {
      if (var1 != null && var1.trim().length() != 0) {
         Element var3 = var0.createElementNS((String)null, "namespaceContext");
         var3.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var1.trim(), var2);
         return var3;
      } else {
         throw new IllegalArgumentException("You must supply a prefix");
      }
   }

   public static void addReturnToElement(Element var0) {
      if (!ignoreLineBreaks) {
         Document var1 = var0.getOwnerDocument();
         var0.appendChild(var1.createTextNode("\n"));
      }

   }

   public static void addReturnToElement(Document var0, HelperNodeList var1) {
      if (!ignoreLineBreaks) {
         var1.appendChild(var0.createTextNode("\n"));
      }

   }

   public static void addReturnBeforeChild(Element var0, Node var1) {
      if (!ignoreLineBreaks) {
         Document var2 = var0.getOwnerDocument();
         var0.insertBefore(var2.createTextNode("\n"), var1);
      }

   }

   public static Set<Node> convertNodelistToSet(NodeList var0) {
      if (var0 == null) {
         return new HashSet();
      } else {
         int var1 = var0.getLength();
         HashSet var2 = new HashSet(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.add(var0.item(var3));
         }

         return var2;
      }
   }

   public static void circumventBug2650(Document var0) {
      Element var1 = var0.getDocumentElement();
      Attr var2 = var1.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
      if (var2 == null) {
         var1.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
      }

      circumventBug2650internal(var0);
   }

   private static void circumventBug2650internal(Node var0) {
      Node var1 = null;
      Node var2 = null;

      while(true) {
         switch(var0.getNodeType()) {
         case 1:
            Element var4 = (Element)var0;
            if (!var4.hasChildNodes()) {
               break;
            }

            if (var4.hasAttributes()) {
               NamedNodeMap var5 = var4.getAttributes();
               int var6 = var5.getLength();

               for(Node var7 = var4.getFirstChild(); var7 != null; var7 = var7.getNextSibling()) {
                  if (var7.getNodeType() == 1) {
                     Element var8 = (Element)var7;

                     for(int var9 = 0; var9 < var6; ++var9) {
                        Attr var10 = (Attr)var5.item(var9);
                        if ("http://www.w3.org/2000/xmlns/".equals(var10.getNamespaceURI()) && !var8.hasAttributeNS("http://www.w3.org/2000/xmlns/", var10.getLocalName())) {
                           var8.setAttributeNS("http://www.w3.org/2000/xmlns/", var10.getName(), var10.getNodeValue());
                        }
                     }
                  }
               }
            }
         case 5:
         case 9:
            var1 = var0;
            var2 = var0.getFirstChild();
         }

         while(var2 == null && var1 != null) {
            var2 = var1.getNextSibling();
            var1 = var1.getParentNode();
         }

         if (var2 == null) {
            return;
         }

         var0 = var2;
         var2 = var2.getNextSibling();
      }
   }

   public static Element selectDsNode(Node var0, String var1, int var2) {
      for(; var0 != null; var0 = var0.getNextSibling()) {
         if ("http://www.w3.org/2000/09/xmldsig#".equals(var0.getNamespaceURI()) && var0.getLocalName().equals(var1)) {
            if (var2 == 0) {
               return (Element)var0;
            }

            --var2;
         }
      }

      return null;
   }

   public static Element selectDs11Node(Node var0, String var1, int var2) {
      for(; var0 != null; var0 = var0.getNextSibling()) {
         if ("http://www.w3.org/2009/xmldsig11#".equals(var0.getNamespaceURI()) && var0.getLocalName().equals(var1)) {
            if (var2 == 0) {
               return (Element)var0;
            }

            --var2;
         }
      }

      return null;
   }

   public static Element selectXencNode(Node var0, String var1, int var2) {
      for(; var0 != null; var0 = var0.getNextSibling()) {
         if ("http://www.w3.org/2001/04/xmlenc#".equals(var0.getNamespaceURI()) && var0.getLocalName().equals(var1)) {
            if (var2 == 0) {
               return (Element)var0;
            }

            --var2;
         }
      }

      return null;
   }

   public static Text selectDsNodeText(Node var0, String var1, int var2) {
      Element var3 = selectDsNode(var0, var1, var2);
      if (var3 == null) {
         return null;
      } else {
         Node var4;
         for(var4 = var3.getFirstChild(); var4 != null && var4.getNodeType() != 3; var4 = var4.getNextSibling()) {
         }

         return (Text)var4;
      }
   }

   public static Text selectDs11NodeText(Node var0, String var1, int var2) {
      Element var3 = selectDs11Node(var0, var1, var2);
      if (var3 == null) {
         return null;
      } else {
         Node var4;
         for(var4 = var3.getFirstChild(); var4 != null && var4.getNodeType() != 3; var4 = var4.getNextSibling()) {
         }

         return (Text)var4;
      }
   }

   public static Text selectNodeText(Node var0, String var1, String var2, int var3) {
      Element var4 = selectNode(var0, var1, var2, var3);
      if (var4 == null) {
         return null;
      } else {
         Node var5;
         for(var5 = var4.getFirstChild(); var5 != null && var5.getNodeType() != 3; var5 = var5.getNextSibling()) {
         }

         return (Text)var5;
      }
   }

   public static Element selectNode(Node var0, String var1, String var2, int var3) {
      for(; var0 != null; var0 = var0.getNextSibling()) {
         if (var0.getNamespaceURI() != null && var0.getNamespaceURI().equals(var1) && var0.getLocalName().equals(var2)) {
            if (var3 == 0) {
               return (Element)var0;
            }

            --var3;
         }
      }

      return null;
   }

   public static Element[] selectDsNodes(Node var0, String var1) {
      return selectNodes(var0, "http://www.w3.org/2000/09/xmldsig#", var1);
   }

   public static Element[] selectDs11Nodes(Node var0, String var1) {
      return selectNodes(var0, "http://www.w3.org/2009/xmldsig11#", var1);
   }

   public static Element[] selectNodes(Node var0, String var1, String var2) {
      ArrayList var3;
      for(var3 = new ArrayList(); var0 != null; var0 = var0.getNextSibling()) {
         if (var0.getNamespaceURI() != null && var0.getNamespaceURI().equals(var1) && var0.getLocalName().equals(var2)) {
            var3.add((Element)var0);
         }
      }

      return (Element[])var3.toArray(new Element[var3.size()]);
   }

   public static Set<Node> excludeNodeFromSet(Node var0, Set<Node> var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Node var4 = (Node)var3.next();
         if (!isDescendantOrSelf(var0, var4)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public static String getStrFromNode(Node var0) {
      if (var0.getNodeType() == 3) {
         StringBuilder var1 = new StringBuilder();

         for(Node var2 = var0.getParentNode().getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
            if (var2.getNodeType() == 3) {
               var1.append(((Text)var2).getData());
            }
         }

         return var1.toString();
      } else if (var0.getNodeType() == 2) {
         return ((Attr)var0).getNodeValue();
      } else {
         return var0.getNodeType() == 7 ? ((ProcessingInstruction)var0).getNodeValue() : null;
      }
   }

   public static boolean isDescendantOrSelf(Node var0, Node var1) {
      if (var0 == var1) {
         return true;
      } else {
         Object var2 = var1;

         while(var2 != null) {
            if (var2 == var0) {
               return true;
            }

            if (((Node)var2).getNodeType() == 2) {
               var2 = ((Attr)var2).getOwnerElement();
            } else {
               var2 = ((Node)var2).getParentNode();
            }
         }

         return false;
      }
   }

   public static boolean ignoreLineBreaks() {
      return ignoreLineBreaks;
   }

   public static String getAttributeValue(Element var0, String var1) {
      Attr var2 = var0.getAttributeNodeNS((String)null, var1);
      return var2 == null ? null : var2.getValue();
   }

   public static boolean protectAgainstWrappingAttack(Node var0, String var1) {
      Node var2 = var0.getParentNode();
      Node var3 = null;
      Element var4 = null;
      String var5 = var1.trim();
      if (!var5.isEmpty() && var5.charAt(0) == '#') {
         var5 = var5.substring(1);
      }

      while(var0 != null) {
         if (var0.getNodeType() == 1) {
            Element var6 = (Element)var0;
            NamedNodeMap var7 = var6.getAttributes();
            if (var7 != null) {
               for(int var8 = 0; var8 < var7.getLength(); ++var8) {
                  Attr var9 = (Attr)var7.item(var8);
                  if (var9.isId() && var5.equals(var9.getValue())) {
                     if (var4 != null) {
                        log.log(Level.FINE, "Multiple elements with the same 'Id' attribute value!");
                        return false;
                     }

                     var4 = var9.getOwnerElement();
                  }
               }
            }
         }

         var3 = var0;
         var0 = var0.getFirstChild();
         if (var0 == null) {
            var0 = var3.getNextSibling();
         }

         while(var0 == null) {
            var3 = var3.getParentNode();
            if (var3 == var2) {
               return true;
            }

            var0 = var3.getNextSibling();
         }
      }

      return true;
   }

   public static boolean protectAgainstWrappingAttack(Node var0, Element var1, String var2) {
      Node var3 = var0.getParentNode();
      Node var4 = null;
      String var5 = var2.trim();
      if (!var5.isEmpty() && var5.charAt(0) == '#') {
         var5 = var5.substring(1);
      }

      while(var0 != null) {
         if (var0.getNodeType() == 1) {
            Element var6 = (Element)var0;
            NamedNodeMap var7 = var6.getAttributes();
            if (var7 != null) {
               for(int var8 = 0; var8 < var7.getLength(); ++var8) {
                  Attr var9 = (Attr)var7.item(var8);
                  if (var9.isId() && var5.equals(var9.getValue()) && var6 != var1) {
                     log.log(Level.FINE, "Multiple elements with the same 'Id' attribute value!");
                     return false;
                  }
               }
            }
         }

         var4 = var0;
         var0 = var0.getFirstChild();
         if (var0 == null) {
            var0 = var4.getNextSibling();
         }

         while(var0 == null) {
            var4 = var4.getParentNode();
            if (var4 == var3) {
               return true;
            }

            var0 = var4.getNextSibling();
         }
      }

      return true;
   }
}
