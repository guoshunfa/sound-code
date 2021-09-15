package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.params.XPath2FilterContainer;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TransformXPath2Filter extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/2002/06/xmldsig-filter2";

   protected String engineGetURI() {
      return "http://www.w3.org/2002/06/xmldsig-filter2";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws TransformationException {
      try {
         ArrayList var4 = new ArrayList();
         ArrayList var5 = new ArrayList();
         ArrayList var6 = new ArrayList();
         Element[] var7 = XMLUtils.selectNodes(var3.getElement().getFirstChild(), "http://www.w3.org/2002/06/xmldsig-filter2", "XPath");
         if (var7.length == 0) {
            Object[] var24 = new Object[]{"http://www.w3.org/2002/06/xmldsig-filter2", "XPath"};
            throw new TransformationException("xml.WrongContent", var24);
         } else {
            Document var8 = null;
            if (var1.getSubNode() != null) {
               var8 = XMLUtils.getOwnerDocument(var1.getSubNode());
            } else {
               var8 = XMLUtils.getOwnerDocument(var1.getNodeSet());
            }

            for(int var9 = 0; var9 < var7.length; ++var9) {
               Element var10 = var7[var9];
               XPath2FilterContainer var11 = XPath2FilterContainer.newInstance(var10, var1.getSourceURI());
               String var12 = XMLUtils.getStrFromNode(var11.getXPathFilterTextNode());
               XPathFactory var13 = XPathFactory.newInstance();
               XPathAPI var14 = var13.newXPathAPI();
               NodeList var15 = var14.selectNodeList(var8, var11.getXPathFilterTextNode(), var12, var11.getElement());
               if (var11.isIntersect()) {
                  var6.add(var15);
               } else if (var11.isSubtract()) {
                  var5.add(var15);
               } else if (var11.isUnion()) {
                  var4.add(var15);
               }
            }

            var1.addNodeFilter(new XPath2NodeFilter(var4, var5, var6));
            var1.setNodeSet(true);
            return var1;
         }
      } catch (TransformerException var16) {
         throw new TransformationException("empty", var16);
      } catch (DOMException var17) {
         throw new TransformationException("empty", var17);
      } catch (CanonicalizationException var18) {
         throw new TransformationException("empty", var18);
      } catch (InvalidCanonicalizerException var19) {
         throw new TransformationException("empty", var19);
      } catch (XMLSecurityException var20) {
         throw new TransformationException("empty", var20);
      } catch (SAXException var21) {
         throw new TransformationException("empty", var21);
      } catch (IOException var22) {
         throw new TransformationException("empty", var22);
      } catch (ParserConfigurationException var23) {
         throw new TransformationException("empty", var23);
      }
   }
}
