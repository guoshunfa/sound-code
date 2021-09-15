package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Transforms extends SignatureElementProxy {
   public static final String TRANSFORM_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
   public static final String TRANSFORM_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
   public static final String TRANSFORM_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
   public static final String TRANSFORM_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
   public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
   public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
   public static final String TRANSFORM_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
   public static final String TRANSFORM_BASE64_DECODE = "http://www.w3.org/2000/09/xmldsig#base64";
   public static final String TRANSFORM_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
   public static final String TRANSFORM_ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
   public static final String TRANSFORM_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
   public static final String TRANSFORM_XPATH2FILTER = "http://www.w3.org/2002/06/xmldsig-filter2";
   private static Logger log = Logger.getLogger(Transforms.class.getName());
   private Element[] transforms;
   private boolean secureValidation;

   protected Transforms() {
   }

   public Transforms(Document var1) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public Transforms(Element var1, String var2) throws DOMException, XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException {
      super(var1, var2);
      int var3 = this.getLength();
      if (var3 == 0) {
         Object[] var4 = new Object[]{"Transform", "Transforms"};
         throw new TransformationException("xml.WrongContent", var4);
      }
   }

   public void setSecureValidation(boolean var1) {
      this.secureValidation = var1;
   }

   public void addTransform(String var1) throws TransformationException {
      try {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Transforms.addTransform(" + var1 + ")");
         }

         Transform var2 = new Transform(this.doc, var1);
         this.addTransform(var2);
      } catch (InvalidTransformException var3) {
         throw new TransformationException("empty", var3);
      }
   }

   public void addTransform(String var1, Element var2) throws TransformationException {
      try {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Transforms.addTransform(" + var1 + ")");
         }

         Transform var3 = new Transform(this.doc, var1, var2);
         this.addTransform(var3);
      } catch (InvalidTransformException var4) {
         throw new TransformationException("empty", var4);
      }
   }

   public void addTransform(String var1, NodeList var2) throws TransformationException {
      try {
         Transform var3 = new Transform(this.doc, var1, var2);
         this.addTransform(var3);
      } catch (InvalidTransformException var4) {
         throw new TransformationException("empty", var4);
      }
   }

   private void addTransform(Transform var1) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Transforms.addTransform(" + var1.getURI() + ")");
      }

      Element var2 = var1.getElement();
      this.constructionElement.appendChild(var2);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public XMLSignatureInput performTransforms(XMLSignatureInput var1) throws TransformationException {
      return this.performTransforms(var1, (OutputStream)null);
   }

   public XMLSignatureInput performTransforms(XMLSignatureInput var1, OutputStream var2) throws TransformationException {
      try {
         int var3 = this.getLength() - 1;

         for(int var4 = 0; var4 < var3; ++var4) {
            Transform var5 = this.item(var4);
            String var6 = var5.getURI();
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Perform the (" + var4 + ")th " + var6 + " transform");
            }

            this.checkSecureValidation(var5);
            var1 = var5.performTransform(var1);
         }

         if (var3 >= 0) {
            Transform var10 = this.item(var3);
            this.checkSecureValidation(var10);
            var1 = var10.performTransform(var1, var2);
         }

         return var1;
      } catch (IOException var7) {
         throw new TransformationException("empty", var7);
      } catch (CanonicalizationException var8) {
         throw new TransformationException("empty", var8);
      } catch (InvalidCanonicalizerException var9) {
         throw new TransformationException("empty", var9);
      }
   }

   private void checkSecureValidation(Transform var1) throws TransformationException {
      String var2 = var1.getURI();
      if (this.secureValidation && "http://www.w3.org/TR/1999/REC-xslt-19991116".equals(var2)) {
         Object[] var3 = new Object[]{var2};
         throw new TransformationException("signature.Transform.ForbiddenTransform", var3);
      }
   }

   public int getLength() {
      if (this.transforms == null) {
         this.transforms = XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Transform");
      }

      return this.transforms.length;
   }

   public Transform item(int var1) throws TransformationException {
      try {
         if (this.transforms == null) {
            this.transforms = XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Transform");
         }

         return new Transform(this.transforms[var1], this.baseURI);
      } catch (XMLSecurityException var3) {
         throw new TransformationException("empty", var3);
      }
   }

   public String getBaseLocalName() {
      return "Transforms";
   }
}
