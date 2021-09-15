package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Element;

public class TransformXSLT extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xslt-19991116";
   static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
   static final String defaultXSLTSpecNSprefix = "xslt";
   static final String XSLTSTYLESHEET = "stylesheet";
   private static Logger log = Logger.getLogger(TransformXSLT.class.getName());

   protected String engineGetURI() {
      return "http://www.w3.org/TR/1999/REC-xslt-19991116";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws IOException, TransformationException {
      Object[] var5;
      try {
         Element var4 = var3.getElement();
         Element var17 = XMLUtils.selectNode(var4.getFirstChild(), "http://www.w3.org/1999/XSL/Transform", "stylesheet", 0);
         if (var17 == null) {
            Object[] var18 = new Object[]{"xslt:stylesheet", "Transform"};
            throw new TransformationException("xml.WrongContent", var18);
         } else {
            TransformerFactory var6 = TransformerFactory.newInstance();
            var6.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            StreamSource var7 = new StreamSource(new ByteArrayInputStream(var1.getBytes()));
            ByteArrayOutputStream var9 = new ByteArrayOutputStream();
            Transformer var10 = var6.newTransformer();
            DOMSource var11 = new DOMSource(var17);
            StreamResult var12 = new StreamResult(var9);
            var10.transform(var11, var12);
            StreamSource var8 = new StreamSource(new ByteArrayInputStream(var9.toByteArray()));
            Transformer var19 = var6.newTransformer(var8);

            try {
               var19.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
            } catch (Exception var13) {
               log.log(Level.WARNING, "Unable to set Xalan line-separator property: " + var13.getMessage());
            }

            if (var2 == null) {
               ByteArrayOutputStream var21 = new ByteArrayOutputStream();
               StreamResult var23 = new StreamResult(var21);
               var19.transform(var7, var23);
               return new XMLSignatureInput(var21.toByteArray());
            } else {
               StreamResult var20 = new StreamResult(var2);
               var19.transform(var7, var20);
               XMLSignatureInput var22 = new XMLSignatureInput((byte[])null);
               var22.setOutputStream(var2);
               return var22;
            }
         }
      } catch (XMLSecurityException var14) {
         var5 = new Object[]{var14.getMessage()};
         throw new TransformationException("generic.EmptyMessage", var5, var14);
      } catch (TransformerConfigurationException var15) {
         var5 = new Object[]{var15.getMessage()};
         throw new TransformationException("generic.EmptyMessage", var5, var15);
      } catch (TransformerException var16) {
         var5 = new Object[]{var16.getMessage()};
         throw new TransformationException("generic.EmptyMessage", var5, var16);
      }
   }
}
