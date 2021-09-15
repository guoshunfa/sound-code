package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class ElementProxy {
   protected static final Logger log = Logger.getLogger(ElementProxy.class.getName());
   protected Element constructionElement = null;
   protected String baseURI = null;
   protected Document doc = null;
   private static Map<String, String> prefixMappings = new ConcurrentHashMap();

   public ElementProxy() {
   }

   public ElementProxy(Document var1) {
      if (var1 == null) {
         throw new RuntimeException("Document is null");
      } else {
         this.doc = var1;
         this.constructionElement = this.createElementForFamilyLocal(this.doc, this.getBaseNamespace(), this.getBaseLocalName());
      }
   }

   public ElementProxy(Element var1, String var2) throws XMLSecurityException {
      if (var1 == null) {
         throw new XMLSecurityException("ElementProxy.nullElement");
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "setElement(\"" + var1.getTagName() + "\", \"" + var2 + "\")");
         }

         this.doc = var1.getOwnerDocument();
         this.constructionElement = var1;
         this.baseURI = var2;
         this.guaranteeThatElementInCorrectSpace();
      }
   }

   public abstract String getBaseNamespace();

   public abstract String getBaseLocalName();

   protected Element createElementForFamilyLocal(Document var1, String var2, String var3) {
      Element var4 = null;
      if (var2 == null) {
         var4 = var1.createElementNS((String)null, var3);
      } else {
         String var5 = this.getBaseNamespace();
         String var6 = getDefaultPrefix(var5);
         if (var6 != null && var6.length() != 0) {
            var4 = var1.createElementNS(var2, var6 + ":" + var3);
            var4.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var6, var2);
         } else {
            var4 = var1.createElementNS(var2, var3);
            var4.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", var2);
         }
      }

      return var4;
   }

   public static Element createElementForFamily(Document var0, String var1, String var2) {
      Element var3 = null;
      String var4 = getDefaultPrefix(var1);
      if (var1 == null) {
         var3 = var0.createElementNS((String)null, var2);
      } else if (var4 != null && var4.length() != 0) {
         var3 = var0.createElementNS(var1, var4 + ":" + var2);
         var3.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var4, var1);
      } else {
         var3 = var0.createElementNS(var1, var2);
         var3.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", var1);
      }

      return var3;
   }

   public void setElement(Element var1, String var2) throws XMLSecurityException {
      if (var1 == null) {
         throw new XMLSecurityException("ElementProxy.nullElement");
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "setElement(" + var1.getTagName() + ", \"" + var2 + "\"");
         }

         this.doc = var1.getOwnerDocument();
         this.constructionElement = var1;
         this.baseURI = var2;
      }
   }

   public final Element getElement() {
      return this.constructionElement;
   }

   public final NodeList getElementPlusReturns() {
      HelperNodeList var1 = new HelperNodeList();
      var1.appendChild(this.doc.createTextNode("\n"));
      var1.appendChild(this.getElement());
      var1.appendChild(this.doc.createTextNode("\n"));
      return var1;
   }

   public Document getDocument() {
      return this.doc;
   }

   public String getBaseURI() {
      return this.baseURI;
   }

   void guaranteeThatElementInCorrectSpace() throws XMLSecurityException {
      String var1 = this.getBaseLocalName();
      String var2 = this.getBaseNamespace();
      String var3 = this.constructionElement.getLocalName();
      String var4 = this.constructionElement.getNamespaceURI();
      if (!var2.equals(var4) && !var1.equals(var3)) {
         Object[] var5 = new Object[]{var4 + ":" + var3, var2 + ":" + var1};
         throw new XMLSecurityException("xml.WrongElement", var5);
      }
   }

   public void addBigIntegerElement(BigInteger var1, String var2) {
      if (var1 != null) {
         Element var3 = XMLUtils.createElementInSignatureSpace(this.doc, var2);
         Base64.fillElementWithBigInteger(var3, var1);
         this.constructionElement.appendChild(var3);
         XMLUtils.addReturnToElement(this.constructionElement);
      }

   }

   public void addBase64Element(byte[] var1, String var2) {
      if (var1 != null) {
         Element var3 = Base64.encodeToElement(this.doc, var2, var1);
         this.constructionElement.appendChild(var3);
         if (!XMLUtils.ignoreLineBreaks()) {
            this.constructionElement.appendChild(this.doc.createTextNode("\n"));
         }
      }

   }

   public void addTextElement(String var1, String var2) {
      Element var3 = XMLUtils.createElementInSignatureSpace(this.doc, var2);
      Text var4 = this.doc.createTextNode(var1);
      var3.appendChild(var4);
      this.constructionElement.appendChild(var3);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addBase64Text(byte[] var1) {
      if (var1 != null) {
         Text var2 = XMLUtils.ignoreLineBreaks() ? this.doc.createTextNode(Base64.encode(var1)) : this.doc.createTextNode("\n" + Base64.encode(var1) + "\n");
         this.constructionElement.appendChild(var2);
      }

   }

   public void addText(String var1) {
      if (var1 != null) {
         Text var2 = this.doc.createTextNode(var1);
         this.constructionElement.appendChild(var2);
      }

   }

   public BigInteger getBigIntegerFromChildElement(String var1, String var2) throws Base64DecodingException {
      return Base64.decodeBigIntegerFromText(XMLUtils.selectNodeText(this.constructionElement.getFirstChild(), var2, var1, 0));
   }

   /** @deprecated */
   @Deprecated
   public byte[] getBytesFromChildElement(String var1, String var2) throws XMLSecurityException {
      Element var3 = XMLUtils.selectNode(this.constructionElement.getFirstChild(), var2, var1, 0);
      return Base64.decode(var3);
   }

   public String getTextFromChildElement(String var1, String var2) {
      return XMLUtils.selectNode(this.constructionElement.getFirstChild(), var2, var1, 0).getTextContent();
   }

   public byte[] getBytesFromTextChild() throws XMLSecurityException {
      return Base64.decode(XMLUtils.getFullTextChildrenFromElement(this.constructionElement));
   }

   public String getTextFromTextChild() {
      return XMLUtils.getFullTextChildrenFromElement(this.constructionElement);
   }

   public int length(String var1, String var2) {
      int var3 = 0;

      for(Node var4 = this.constructionElement.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
         if (var2.equals(var4.getLocalName()) && var1.equals(var4.getNamespaceURI())) {
            ++var3;
         }
      }

      return var3;
   }

   public void setXPathNamespaceContext(String var1, String var2) throws XMLSecurityException {
      if (var1 != null && var1.length() != 0) {
         if (var1.equals("xmlns")) {
            throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
         } else {
            String var3;
            if (var1.startsWith("xmlns:")) {
               var3 = var1;
            } else {
               var3 = "xmlns:" + var1;
            }

            Attr var4 = this.constructionElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", var3);
            if (var4 != null) {
               if (!var4.getNodeValue().equals(var2)) {
                  Object[] var5 = new Object[]{var3, this.constructionElement.getAttributeNS((String)null, var3)};
                  throw new XMLSecurityException("namespacePrefixAlreadyUsedByOtherURI", var5);
               }
            } else {
               this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", var3, var2);
            }
         }
      } else {
         throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
      }
   }

   public static void setDefaultPrefix(String var0, String var1) throws XMLSecurityException {
      JavaUtils.checkRegisterPermission();
      if (prefixMappings.containsValue(var1)) {
         String var2 = (String)prefixMappings.get(var0);
         if (!var2.equals(var1)) {
            Object[] var3 = new Object[]{var1, var0, var2};
            throw new XMLSecurityException("prefix.AlreadyAssigned", var3);
         }
      }

      if ("http://www.w3.org/2000/09/xmldsig#".equals(var0)) {
         XMLUtils.setDsPrefix(var1);
      }

      if ("http://www.w3.org/2001/04/xmlenc#".equals(var0)) {
         XMLUtils.setXencPrefix(var1);
      }

      prefixMappings.put(var0, var1);
   }

   public static void registerDefaultPrefixes() throws XMLSecurityException {
      setDefaultPrefix("http://www.w3.org/2000/09/xmldsig#", "ds");
      setDefaultPrefix("http://www.w3.org/2001/04/xmlenc#", "xenc");
      setDefaultPrefix("http://www.w3.org/2009/xmlenc11#", "xenc11");
      setDefaultPrefix("http://www.xmlsecurity.org/experimental#", "experimental");
      setDefaultPrefix("http://www.w3.org/2002/04/xmldsig-filter2", "dsig-xpath-old");
      setDefaultPrefix("http://www.w3.org/2002/06/xmldsig-filter2", "dsig-xpath");
      setDefaultPrefix("http://www.w3.org/2001/10/xml-exc-c14n#", "ec");
      setDefaultPrefix("http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter", "xx");
   }

   public static String getDefaultPrefix(String var0) {
      return (String)prefixMappings.get(var0);
   }
}
