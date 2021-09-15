package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class TransformBase64Decode extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#base64";

   protected String engineGetURI() {
      return "http://www.w3.org/2000/09/xmldsig#base64";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, Transform var2) throws IOException, CanonicalizationException, TransformationException {
      return this.enginePerformTransform(var1, (OutputStream)null, var2);
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws IOException, CanonicalizationException, TransformationException {
      try {
         if (var1.isElement()) {
            Node var14 = var1.getSubNode();
            if (var1.getSubNode().getNodeType() == 3) {
               var14 = var14.getParentNode();
            }

            StringBuilder var16 = new StringBuilder();
            this.traverseElement((Element)var14, var16);
            if (var2 == null) {
               byte[] var18 = Base64.decode(var16.toString());
               return new XMLSignatureInput(var18);
            } else {
               Base64.decode(var16.toString(), var2);
               XMLSignatureInput var17 = new XMLSignatureInput((byte[])null);
               var17.setOutputStream(var2);
               return var17;
            }
         } else if (!var1.isOctetStream() && !var1.isNodeSet()) {
            try {
               DocumentBuilderFactory var13 = DocumentBuilderFactory.newInstance();
               var13.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
               Document var15 = var13.newDocumentBuilder().parse(var1.getOctetStream());
               Element var6 = var15.getDocumentElement();
               StringBuilder var7 = new StringBuilder();
               this.traverseElement(var6, var7);
               byte[] var8 = Base64.decode(var7.toString());
               return new XMLSignatureInput(var8);
            } catch (ParserConfigurationException var9) {
               throw new TransformationException("c14n.Canonicalizer.Exception", var9);
            } catch (SAXException var10) {
               throw new TransformationException("SAX exception", var10);
            }
         } else if (var2 == null) {
            byte[] var12 = var1.getBytes();
            byte[] var5 = Base64.decode(var12);
            return new XMLSignatureInput(var5);
         } else {
            if (!var1.isByteArray() && !var1.isNodeSet()) {
               Base64.decode((InputStream)(new BufferedInputStream(var1.getOctetStreamReal())), var2);
            } else {
               Base64.decode(var1.getBytes(), var2);
            }

            XMLSignatureInput var4 = new XMLSignatureInput((byte[])null);
            var4.setOutputStream(var2);
            return var4;
         }
      } catch (Base64DecodingException var11) {
         throw new TransformationException("Base64Decoding", var11);
      }
   }

   void traverseElement(Element var1, StringBuilder var2) {
      for(Node var3 = var1.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
         switch(var3.getNodeType()) {
         case 1:
            this.traverseElement((Element)var3, var2);
            break;
         case 3:
            var2.append(((Text)var3).getData());
         }
      }

   }
}
