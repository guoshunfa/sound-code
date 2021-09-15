package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformBase64Decode;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11_WithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusive;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusiveWithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NWithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformEnvelopedSignature;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath2Filter;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXSLT;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Transform extends SignatureElementProxy {
   private static Logger log = Logger.getLogger(Transform.class.getName());
   private static Map<String, Class<? extends TransformSpi>> transformSpiHash = new ConcurrentHashMap();
   private final TransformSpi transformSpi;

   public Transform(Document var1, String var2) throws InvalidTransformException {
      this(var1, var2, (NodeList)null);
   }

   public Transform(Document var1, String var2, Element var3) throws InvalidTransformException {
      super(var1);
      HelperNodeList var4 = null;
      if (var3 != null) {
         var4 = new HelperNodeList();
         XMLUtils.addReturnToElement(var1, var4);
         var4.appendChild(var3);
         XMLUtils.addReturnToElement(var1, var4);
      }

      this.transformSpi = this.initializeTransform(var2, var4);
   }

   public Transform(Document var1, String var2, NodeList var3) throws InvalidTransformException {
      super(var1);
      this.transformSpi = this.initializeTransform(var2, var3);
   }

   public Transform(Element var1, String var2) throws InvalidTransformException, TransformationException, XMLSecurityException {
      super(var1, var2);
      String var3 = var1.getAttributeNS((String)null, "Algorithm");
      if (var3 != null && var3.length() != 0) {
         Class var9 = (Class)transformSpiHash.get(var3);
         if (var9 == null) {
            Object[] var5 = new Object[]{var3};
            throw new InvalidTransformException("signature.Transform.UnknownTransform", var5);
         } else {
            Object[] var6;
            try {
               this.transformSpi = (TransformSpi)var9.newInstance();
            } catch (InstantiationException var7) {
               var6 = new Object[]{var3};
               throw new InvalidTransformException("signature.Transform.UnknownTransform", var6, var7);
            } catch (IllegalAccessException var8) {
               var6 = new Object[]{var3};
               throw new InvalidTransformException("signature.Transform.UnknownTransform", var6, var8);
            }
         }
      } else {
         Object[] var4 = new Object[]{"Algorithm", "Transform"};
         throw new TransformationException("xml.WrongContent", var4);
      }
   }

   public static void register(String var0, String var1) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, InvalidTransformException {
      JavaUtils.checkRegisterPermission();
      Class var2 = (Class)transformSpiHash.get(var0);
      if (var2 != null) {
         Object[] var4 = new Object[]{var0, var2};
         throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", var4);
      } else {
         Class var3 = ClassLoaderUtils.loadClass(var1, Transform.class);
         transformSpiHash.put(var0, var3);
      }
   }

   public static void register(String var0, Class<? extends TransformSpi> var1) throws AlgorithmAlreadyRegisteredException {
      JavaUtils.checkRegisterPermission();
      Class var2 = (Class)transformSpiHash.get(var0);
      if (var2 != null) {
         Object[] var3 = new Object[]{var0, var2};
         throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", var3);
      } else {
         transformSpiHash.put(var0, var1);
      }
   }

   public static void registerDefaultAlgorithms() {
      transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#base64", TransformBase64Decode.class);
      transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", TransformC14N.class);
      transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", TransformC14NWithComments.class);
      transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11", TransformC14N11.class);
      transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", TransformC14N11_WithComments.class);
      transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", TransformC14NExclusive.class);
      transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", TransformC14NExclusiveWithComments.class);
      transformSpiHash.put("http://www.w3.org/TR/1999/REC-xpath-19991116", TransformXPath.class);
      transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#enveloped-signature", TransformEnvelopedSignature.class);
      transformSpiHash.put("http://www.w3.org/TR/1999/REC-xslt-19991116", TransformXSLT.class);
      transformSpiHash.put("http://www.w3.org/2002/06/xmldsig-filter2", TransformXPath2Filter.class);
   }

   public String getURI() {
      return this.constructionElement.getAttributeNS((String)null, "Algorithm");
   }

   public XMLSignatureInput performTransform(XMLSignatureInput var1) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
      return this.performTransform(var1, (OutputStream)null);
   }

   public XMLSignatureInput performTransform(XMLSignatureInput var1, OutputStream var2) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
      XMLSignatureInput var3 = null;

      Object[] var5;
      try {
         var3 = this.transformSpi.enginePerformTransform(var1, var2, this);
         return var3;
      } catch (ParserConfigurationException var6) {
         var5 = new Object[]{this.getURI(), "ParserConfigurationException"};
         throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", var5, var6);
      } catch (SAXException var7) {
         var5 = new Object[]{this.getURI(), "SAXException"};
         throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", var5, var7);
      }
   }

   public String getBaseLocalName() {
      return "Transform";
   }

   private TransformSpi initializeTransform(String var1, NodeList var2) throws InvalidTransformException {
      this.constructionElement.setAttributeNS((String)null, "Algorithm", var1);
      Class var3 = (Class)transformSpiHash.get(var1);
      if (var3 == null) {
         Object[] var9 = new Object[]{var1};
         throw new InvalidTransformException("signature.Transform.UnknownTransform", var9);
      } else {
         TransformSpi var4 = null;

         Object[] var6;
         try {
            var4 = (TransformSpi)var3.newInstance();
         } catch (InstantiationException var7) {
            var6 = new Object[]{var1};
            throw new InvalidTransformException("signature.Transform.UnknownTransform", var6, var7);
         } catch (IllegalAccessException var8) {
            var6 = new Object[]{var1};
            throw new InvalidTransformException("signature.Transform.UnknownTransform", var6, var8);
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Create URI \"" + var1 + "\" class \"" + var4.getClass() + "\"");
            log.log(Level.FINE, "The NodeList is " + var2);
         }

         if (var2 != null) {
            for(int var5 = 0; var5 < var2.getLength(); ++var5) {
               this.constructionElement.appendChild(var2.item(var5).cloneNode(true));
            }
         }

         return var4;
      }
   }
}
