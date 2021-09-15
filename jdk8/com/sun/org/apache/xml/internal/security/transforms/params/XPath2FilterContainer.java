package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPath2FilterContainer extends ElementProxy implements TransformParam {
   private static final String _ATT_FILTER = "Filter";
   private static final String _ATT_FILTER_VALUE_INTERSECT = "intersect";
   private static final String _ATT_FILTER_VALUE_SUBTRACT = "subtract";
   private static final String _ATT_FILTER_VALUE_UNION = "union";
   public static final String INTERSECT = "intersect";
   public static final String SUBTRACT = "subtract";
   public static final String UNION = "union";
   public static final String _TAG_XPATH2 = "XPath";
   public static final String XPathFilter2NS = "http://www.w3.org/2002/06/xmldsig-filter2";

   private XPath2FilterContainer() {
   }

   private XPath2FilterContainer(Document var1, String var2, String var3) {
      super(var1);
      this.constructionElement.setAttributeNS((String)null, "Filter", var3);
      this.constructionElement.appendChild(var1.createTextNode(var2));
   }

   private XPath2FilterContainer(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
      String var3 = this.constructionElement.getAttributeNS((String)null, "Filter");
      if (!var3.equals("intersect") && !var3.equals("subtract") && !var3.equals("union")) {
         Object[] var4 = new Object[]{"Filter", var3, "intersect, subtract or union"};
         throw new XMLSecurityException("attributeValueIllegal", var4);
      }
   }

   public static XPath2FilterContainer newInstanceIntersect(Document var0, String var1) {
      return new XPath2FilterContainer(var0, var1, "intersect");
   }

   public static XPath2FilterContainer newInstanceSubtract(Document var0, String var1) {
      return new XPath2FilterContainer(var0, var1, "subtract");
   }

   public static XPath2FilterContainer newInstanceUnion(Document var0, String var1) {
      return new XPath2FilterContainer(var0, var1, "union");
   }

   public static NodeList newInstances(Document var0, String[][] var1) {
      HelperNodeList var2 = new HelperNodeList();
      XMLUtils.addReturnToElement(var0, var2);

      for(int var3 = 0; var3 < var1.length; ++var3) {
         String var4 = var1[var3][0];
         String var5 = var1[var3][1];
         if (!var4.equals("intersect") && !var4.equals("subtract") && !var4.equals("union")) {
            throw new IllegalArgumentException("The type(" + var3 + ")=\"" + var4 + "\" is illegal");
         }

         XPath2FilterContainer var6 = new XPath2FilterContainer(var0, var5, var4);
         var2.appendChild(var6.getElement());
         XMLUtils.addReturnToElement(var0, var2);
      }

      return var2;
   }

   public static XPath2FilterContainer newInstance(Element var0, String var1) throws XMLSecurityException {
      return new XPath2FilterContainer(var0, var1);
   }

   public boolean isIntersect() {
      return this.constructionElement.getAttributeNS((String)null, "Filter").equals("intersect");
   }

   public boolean isSubtract() {
      return this.constructionElement.getAttributeNS((String)null, "Filter").equals("subtract");
   }

   public boolean isUnion() {
      return this.constructionElement.getAttributeNS((String)null, "Filter").equals("union");
   }

   public String getXPathFilterStr() {
      return this.getTextFromTextChild();
   }

   public Node getXPathFilterTextNode() {
      NodeList var1 = this.constructionElement.getChildNodes();
      int var2 = var1.getLength();

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var1.item(var3).getNodeType() == 3) {
            return var1.item(var3);
         }
      }

      return null;
   }

   public final String getBaseLocalName() {
      return "XPath";
   }

   public final String getBaseNamespace() {
      return "http://www.w3.org/2002/06/xmldsig-filter2";
   }
}
