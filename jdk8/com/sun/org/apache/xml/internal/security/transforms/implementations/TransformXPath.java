package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import java.io.OutputStream;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformXPath extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xpath-19991116";

   protected String engineGetURI() {
      return "http://www.w3.org/TR/1999/REC-xpath-19991116";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws TransformationException {
      try {
         Element var4 = XMLUtils.selectDsNode(var3.getElement().getFirstChild(), "XPath", 0);
         if (var4 == null) {
            Object[] var10 = new Object[]{"ds:XPath", "Transform"};
            throw new TransformationException("xml.WrongContent", var10);
         } else {
            Node var5 = var4.getChildNodes().item(0);
            String var6 = XMLUtils.getStrFromNode(var5);
            var1.setNeedsToBeExpanded(this.needsCircumvent(var6));
            if (var5 == null) {
               throw new DOMException((short)3, "Text must be in ds:Xpath");
            } else {
               XPathFactory var7 = XPathFactory.newInstance();
               XPathAPI var8 = var7.newXPathAPI();
               var1.addNodeFilter(new TransformXPath.XPathNodeFilter(var4, var5, var6, var8));
               var1.setNodeSet(true);
               return var1;
            }
         }
      } catch (DOMException var9) {
         throw new TransformationException("empty", var9);
      }
   }

   private boolean needsCircumvent(String var1) {
      return var1.indexOf("namespace") != -1 || var1.indexOf("name()") != -1;
   }

   static class XPathNodeFilter implements NodeFilter {
      XPathAPI xPathAPI;
      Node xpathnode;
      Element xpathElement;
      String str;

      XPathNodeFilter(Element var1, Node var2, String var3, XPathAPI var4) {
         this.xpathnode = var2;
         this.str = var3;
         this.xpathElement = var1;
         this.xPathAPI = var4;
      }

      public int isNodeInclude(Node var1) {
         Object[] var3;
         try {
            boolean var2 = this.xPathAPI.evaluate(var1, this.xpathnode, this.str, this.xpathElement);
            return var2 ? 1 : 0;
         } catch (TransformerException var4) {
            var3 = new Object[]{var1};
            throw new XMLSecurityRuntimeException("signature.Transform.node", var3, var4);
         } catch (Exception var5) {
            var3 = new Object[]{var1, var1.getNodeType()};
            throw new XMLSecurityRuntimeException("signature.Transform.nodeAndType", var3, var5);
         }
      }

      public int isNodeIncludeDO(Node var1, int var2) {
         return this.isNodeInclude(var1);
      }
   }
}
