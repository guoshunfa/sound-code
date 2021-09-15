package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XPathFilterCHGPContainer extends ElementProxy implements TransformParam {
   public static final String TRANSFORM_XPATHFILTERCHGP = "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
   private static final String _TAG_INCLUDE_BUT_SEARCH = "IncludeButSearch";
   private static final String _TAG_EXCLUDE_BUT_SEARCH = "ExcludeButSearch";
   private static final String _TAG_EXCLUDE = "Exclude";
   public static final String _TAG_XPATHCHGP = "XPathAlternative";
   public static final String _ATT_INCLUDESLASH = "IncludeSlashPolicy";
   public static final boolean IncludeSlash = true;
   public static final boolean ExcludeSlash = false;

   private XPathFilterCHGPContainer() {
   }

   private XPathFilterCHGPContainer(Document var1, boolean var2, String var3, String var4, String var5) {
      super(var1);
      if (var2) {
         this.constructionElement.setAttributeNS((String)null, "IncludeSlashPolicy", "true");
      } else {
         this.constructionElement.setAttributeNS((String)null, "IncludeSlashPolicy", "false");
      }

      Element var6;
      if (var3 != null && var3.trim().length() > 0) {
         var6 = ElementProxy.createElementForFamily(var1, this.getBaseNamespace(), "IncludeButSearch");
         var6.appendChild(this.doc.createTextNode(indentXPathText(var3)));
         XMLUtils.addReturnToElement(this.constructionElement);
         this.constructionElement.appendChild(var6);
      }

      if (var4 != null && var4.trim().length() > 0) {
         var6 = ElementProxy.createElementForFamily(var1, this.getBaseNamespace(), "ExcludeButSearch");
         var6.appendChild(this.doc.createTextNode(indentXPathText(var4)));
         XMLUtils.addReturnToElement(this.constructionElement);
         this.constructionElement.appendChild(var6);
      }

      if (var5 != null && var5.trim().length() > 0) {
         var6 = ElementProxy.createElementForFamily(var1, this.getBaseNamespace(), "Exclude");
         var6.appendChild(this.doc.createTextNode(indentXPathText(var5)));
         XMLUtils.addReturnToElement(this.constructionElement);
         this.constructionElement.appendChild(var6);
      }

      XMLUtils.addReturnToElement(this.constructionElement);
   }

   static String indentXPathText(String var0) {
      return var0.length() > 2 && !Character.isWhitespace(var0.charAt(0)) ? "\n" + var0 + "\n" : var0;
   }

   private XPathFilterCHGPContainer(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public static XPathFilterCHGPContainer getInstance(Document var0, boolean var1, String var2, String var3, String var4) {
      return new XPathFilterCHGPContainer(var0, var1, var2, var3, var4);
   }

   public static XPathFilterCHGPContainer getInstance(Element var0, String var1) throws XMLSecurityException {
      return new XPathFilterCHGPContainer(var0, var1);
   }

   private String getXStr(String var1) {
      if (this.length(this.getBaseNamespace(), var1) != 1) {
         return "";
      } else {
         Element var2 = XMLUtils.selectNode(this.constructionElement.getFirstChild(), this.getBaseNamespace(), var1, 0);
         return XMLUtils.getFullTextChildrenFromElement(var2);
      }
   }

   public String getIncludeButSearch() {
      return this.getXStr("IncludeButSearch");
   }

   public String getExcludeButSearch() {
      return this.getXStr("ExcludeButSearch");
   }

   public String getExclude() {
      return this.getXStr("Exclude");
   }

   public boolean getIncludeSlashPolicy() {
      return this.constructionElement.getAttributeNS((String)null, "IncludeSlashPolicy").equals("true");
   }

   private Node getHereContextNode(String var1) {
      return this.length(this.getBaseNamespace(), var1) != 1 ? null : XMLUtils.selectNodeText(this.constructionElement.getFirstChild(), this.getBaseNamespace(), var1, 0);
   }

   public Node getHereContextNodeIncludeButSearch() {
      return this.getHereContextNode("IncludeButSearch");
   }

   public Node getHereContextNodeExcludeButSearch() {
      return this.getHereContextNode("ExcludeButSearch");
   }

   public Node getHereContextNodeExclude() {
      return this.getHereContextNode("Exclude");
   }

   public final String getBaseLocalName() {
      return "XPathAlternative";
   }

   public final String getBaseNamespace() {
      return "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
   }
}
