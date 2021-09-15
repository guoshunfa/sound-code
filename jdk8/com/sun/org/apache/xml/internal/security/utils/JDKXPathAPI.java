package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JDKXPathAPI implements XPathAPI {
   private javax.xml.xpath.XPathFactory xpf;
   private String xpathStr;
   private XPathExpression xpathExpression;

   public NodeList selectNodeList(Node var1, Node var2, String var3, Node var4) throws TransformerException {
      if (!var3.equals(this.xpathStr) || this.xpathExpression == null) {
         if (this.xpf == null) {
            this.xpf = javax.xml.xpath.XPathFactory.newInstance();

            try {
               this.xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            } catch (XPathFactoryConfigurationException var9) {
               throw new TransformerException("empty", var9);
            }
         }

         XPath var5 = this.xpf.newXPath();
         var5.setNamespaceContext(new DOMNamespaceContext(var4));
         this.xpathStr = var3;

         try {
            this.xpathExpression = var5.compile(this.xpathStr);
         } catch (XPathExpressionException var8) {
            throw new TransformerException("empty", var8);
         }
      }

      try {
         return (NodeList)this.xpathExpression.evaluate((Object)var1, XPathConstants.NODESET);
      } catch (XPathExpressionException var7) {
         throw new TransformerException("empty", var7);
      }
   }

   public boolean evaluate(Node var1, Node var2, String var3, Node var4) throws TransformerException {
      if (!var3.equals(this.xpathStr) || this.xpathExpression == null) {
         if (this.xpf == null) {
            this.xpf = javax.xml.xpath.XPathFactory.newInstance();

            try {
               this.xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            } catch (XPathFactoryConfigurationException var9) {
               throw new TransformerException("empty", var9);
            }
         }

         XPath var5 = this.xpf.newXPath();
         var5.setNamespaceContext(new DOMNamespaceContext(var4));
         this.xpathStr = var3;

         try {
            this.xpathExpression = var5.compile(this.xpathStr);
         } catch (XPathExpressionException var8) {
            throw new TransformerException("empty", var8);
         }
      }

      try {
         Boolean var10 = (Boolean)this.xpathExpression.evaluate((Object)var1, XPathConstants.BOOLEAN);
         return var10;
      } catch (XPathExpressionException var7) {
         throw new TransformerException("empty", var7);
      }
   }

   public void clear() {
      this.xpathStr = null;
      this.xpathExpression = null;
      this.xpf = null;
   }
}
