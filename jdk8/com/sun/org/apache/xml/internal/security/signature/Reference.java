package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceNodeSetData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceOctetStreamData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceSubTreeData;
import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.DigesterOutputStream;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class Reference extends SignatureElementProxy {
   public static final String OBJECT_URI = "http://www.w3.org/2000/09/xmldsig#Object";
   public static final String MANIFEST_URI = "http://www.w3.org/2000/09/xmldsig#Manifest";
   public static final int MAXIMUM_TRANSFORM_COUNT = 5;
   private boolean secureValidation;
   private static boolean useC14N11 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11");
      }
   });
   private static final Logger log = Logger.getLogger(Reference.class.getName());
   private Manifest manifest;
   private XMLSignatureInput transformsOutput;
   private Transforms transforms;
   private Element digestMethodElem;
   private Element digestValueElement;
   private ReferenceData referenceData;

   protected Reference(Document var1, String var2, String var3, Manifest var4, Transforms var5, String var6) throws XMLSignatureException {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.baseURI = var2;
      this.manifest = var4;
      this.setURI(var3);
      if (var5 != null) {
         this.transforms = var5;
         this.constructionElement.appendChild(var5.getElement());
         XMLUtils.addReturnToElement(this.constructionElement);
      }

      MessageDigestAlgorithm var7 = MessageDigestAlgorithm.getInstance(this.doc, var6);
      this.digestMethodElem = var7.getElement();
      this.constructionElement.appendChild(this.digestMethodElem);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.digestValueElement = XMLUtils.createElementInSignatureSpace(this.doc, "DigestValue");
      this.constructionElement.appendChild(this.digestValueElement);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   protected Reference(Element var1, String var2, Manifest var3) throws XMLSecurityException {
      this(var1, var2, var3, false);
   }

   protected Reference(Element var1, String var2, Manifest var3, boolean var4) throws XMLSecurityException {
      super(var1, var2);
      this.secureValidation = var4;
      this.baseURI = var2;
      Element var5 = XMLUtils.getNextElement(var1.getFirstChild());
      if ("Transforms".equals(var5.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(var5.getNamespaceURI())) {
         this.transforms = new Transforms(var5, this.baseURI);
         this.transforms.setSecureValidation(var4);
         if (var4 && this.transforms.getLength() > 5) {
            Object[] var6 = new Object[]{this.transforms.getLength(), 5};
            throw new XMLSecurityException("signature.tooManyTransforms", var6);
         }

         var5 = XMLUtils.getNextElement(var5.getNextSibling());
      }

      this.digestMethodElem = var5;
      this.digestValueElement = XMLUtils.getNextElement(this.digestMethodElem.getNextSibling());
      this.manifest = var3;
   }

   public MessageDigestAlgorithm getMessageDigestAlgorithm() throws XMLSignatureException {
      if (this.digestMethodElem == null) {
         return null;
      } else {
         String var1 = this.digestMethodElem.getAttributeNS((String)null, "Algorithm");
         if (var1 == null) {
            return null;
         } else if (this.secureValidation && "http://www.w3.org/2001/04/xmldsig-more#md5".equals(var1)) {
            Object[] var2 = new Object[]{var1};
            throw new XMLSignatureException("signature.signatureAlgorithm", var2);
         } else {
            return MessageDigestAlgorithm.getInstance(this.doc, var1);
         }
      }
   }

   public void setURI(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "URI", var1);
      }

   }

   public String getURI() {
      return this.constructionElement.getAttributeNS((String)null, "URI");
   }

   public void setId(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Id", var1);
         this.constructionElement.setIdAttributeNS((String)null, "Id", true);
      }

   }

   public String getId() {
      return this.constructionElement.getAttributeNS((String)null, "Id");
   }

   public void setType(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Type", var1);
      }

   }

   public String getType() {
      return this.constructionElement.getAttributeNS((String)null, "Type");
   }

   public boolean typeIsReferenceToObject() {
      return "http://www.w3.org/2000/09/xmldsig#Object".equals(this.getType());
   }

   public boolean typeIsReferenceToManifest() {
      return "http://www.w3.org/2000/09/xmldsig#Manifest".equals(this.getType());
   }

   private void setDigestValueElement(byte[] var1) {
      for(Node var2 = this.digestValueElement.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         this.digestValueElement.removeChild(var2);
      }

      String var3 = Base64.encode(var1);
      Text var4 = this.doc.createTextNode(var3);
      this.digestValueElement.appendChild(var4);
   }

   public void generateDigestValue() throws XMLSignatureException, ReferenceNotInitializedException {
      this.setDigestValueElement(this.calculateDigest(false));
   }

   public XMLSignatureInput getContentsBeforeTransformation() throws ReferenceNotInitializedException {
      try {
         Attr var1 = this.constructionElement.getAttributeNodeNS((String)null, "URI");
         ResourceResolver var2 = ResourceResolver.getInstance(var1, this.baseURI, this.manifest.getPerManifestResolvers(), this.secureValidation);
         var2.addProperties(this.manifest.getResolverProperties());
         return var2.resolve(var1, this.baseURI, this.secureValidation);
      } catch (ResourceResolverException var3) {
         throw new ReferenceNotInitializedException("empty", var3);
      }
   }

   private XMLSignatureInput getContentsAfterTransformation(XMLSignatureInput var1, OutputStream var2) throws XMLSignatureException {
      try {
         Transforms var3 = this.getTransforms();
         XMLSignatureInput var4 = null;
         if (var3 != null) {
            var4 = var3.performTransforms(var1, var2);
            this.transformsOutput = var4;
         } else {
            var4 = var1;
         }

         return var4;
      } catch (ResourceResolverException var5) {
         throw new XMLSignatureException("empty", var5);
      } catch (CanonicalizationException var6) {
         throw new XMLSignatureException("empty", var6);
      } catch (InvalidCanonicalizerException var7) {
         throw new XMLSignatureException("empty", var7);
      } catch (TransformationException var8) {
         throw new XMLSignatureException("empty", var8);
      } catch (XMLSecurityException var9) {
         throw new XMLSignatureException("empty", var9);
      }
   }

   public XMLSignatureInput getContentsAfterTransformation() throws XMLSignatureException {
      XMLSignatureInput var1 = this.getContentsBeforeTransformation();
      this.cacheDereferencedElement(var1);
      return this.getContentsAfterTransformation(var1, (OutputStream)null);
   }

   public XMLSignatureInput getNodesetBeforeFirstCanonicalization() throws XMLSignatureException {
      try {
         XMLSignatureInput var1 = this.getContentsBeforeTransformation();
         this.cacheDereferencedElement(var1);
         XMLSignatureInput var2 = var1;
         Transforms var3 = this.getTransforms();
         if (var3 != null) {
            for(int var4 = 0; var4 < var3.getLength(); ++var4) {
               Transform var5 = var3.item(var4);
               String var6 = var5.getURI();
               if (var6.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || var6.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") || var6.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") || var6.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")) {
                  break;
               }

               var2 = var5.performTransform(var2, (OutputStream)null);
            }

            var2.setSourceURI(var1.getSourceURI());
         }

         return var2;
      } catch (IOException var7) {
         throw new XMLSignatureException("empty", var7);
      } catch (ResourceResolverException var8) {
         throw new XMLSignatureException("empty", var8);
      } catch (CanonicalizationException var9) {
         throw new XMLSignatureException("empty", var9);
      } catch (InvalidCanonicalizerException var10) {
         throw new XMLSignatureException("empty", var10);
      } catch (TransformationException var11) {
         throw new XMLSignatureException("empty", var11);
      } catch (XMLSecurityException var12) {
         throw new XMLSignatureException("empty", var12);
      }
   }

   public String getHTMLRepresentation() throws XMLSignatureException {
      try {
         XMLSignatureInput var1 = this.getNodesetBeforeFirstCanonicalization();
         Transforms var2 = this.getTransforms();
         Transform var3 = null;
         if (var2 != null) {
            for(int var4 = 0; var4 < var2.getLength(); ++var4) {
               Transform var5 = var2.item(var4);
               String var6 = var5.getURI();
               if (var6.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || var6.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
                  var3 = var5;
                  break;
               }
            }
         }

         Object var10 = new HashSet();
         if (var3 != null && var3.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
            InclusiveNamespaces var11 = new InclusiveNamespaces(XMLUtils.selectNode(var3.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), this.getBaseURI());
            var10 = InclusiveNamespaces.prefixStr2Set(var11.getInclusiveNamespaces());
         }

         return var1.getHTMLRepresentation((Set)var10);
      } catch (TransformationException var7) {
         throw new XMLSignatureException("empty", var7);
      } catch (InvalidTransformException var8) {
         throw new XMLSignatureException("empty", var8);
      } catch (XMLSecurityException var9) {
         throw new XMLSignatureException("empty", var9);
      }
   }

   public XMLSignatureInput getTransformsOutput() {
      return this.transformsOutput;
   }

   public ReferenceData getReferenceData() {
      return this.referenceData;
   }

   protected XMLSignatureInput dereferenceURIandPerformTransforms(OutputStream var1) throws XMLSignatureException {
      try {
         XMLSignatureInput var2 = this.getContentsBeforeTransformation();
         this.cacheDereferencedElement(var2);
         XMLSignatureInput var3 = this.getContentsAfterTransformation(var2, var1);
         this.transformsOutput = var3;
         return var3;
      } catch (XMLSecurityException var4) {
         throw new ReferenceNotInitializedException("empty", var4);
      }
   }

   private void cacheDereferencedElement(XMLSignatureInput var1) {
      if (var1.isNodeSet()) {
         try {
            final Set var2 = var1.getNodeSet();
            this.referenceData = new ReferenceNodeSetData() {
               public Iterator<Node> iterator() {
                  return new Iterator<Node>() {
                     Iterator<Node> sIterator = var2.iterator();

                     public boolean hasNext() {
                        return this.sIterator.hasNext();
                     }

                     public Node next() {
                        return (Node)this.sIterator.next();
                     }

                     public void remove() {
                        throw new UnsupportedOperationException();
                     }
                  };
               }
            };
         } catch (Exception var4) {
            log.log(Level.WARNING, "cannot cache dereferenced data: " + var4);
         }
      } else if (var1.isElement()) {
         this.referenceData = new ReferenceSubTreeData(var1.getSubNode(), var1.isExcludeComments());
      } else if (var1.isOctetStream() || var1.isByteArray()) {
         try {
            this.referenceData = new ReferenceOctetStreamData(var1.getOctetStream(), var1.getSourceURI(), var1.getMIMEType());
         } catch (IOException var3) {
            log.log(Level.WARNING, "cannot cache dereferenced data: " + var3);
         }
      }

   }

   public Transforms getTransforms() throws XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException {
      return this.transforms;
   }

   public byte[] getReferencedBytes() throws ReferenceNotInitializedException, XMLSignatureException {
      try {
         XMLSignatureInput var1 = this.dereferenceURIandPerformTransforms((OutputStream)null);
         return var1.getBytes();
      } catch (IOException var2) {
         throw new ReferenceNotInitializedException("empty", var2);
      } catch (CanonicalizationException var3) {
         throw new ReferenceNotInitializedException("empty", var3);
      }
   }

   private byte[] calculateDigest(boolean var1) throws ReferenceNotInitializedException, XMLSignatureException {
      UnsyncBufferedOutputStream var2 = null;

      byte[] var6;
      try {
         MessageDigestAlgorithm var3 = this.getMessageDigestAlgorithm();
         var3.reset();
         DigesterOutputStream var4 = new DigesterOutputStream(var3);
         var2 = new UnsyncBufferedOutputStream(var4);
         XMLSignatureInput var5 = this.dereferenceURIandPerformTransforms(var2);
         if (useC14N11 && !var1 && !var5.isOutputStreamSet() && !var5.isOctetStream()) {
            if (this.transforms == null) {
               this.transforms = new Transforms(this.doc);
               this.transforms.setSecureValidation(this.secureValidation);
               this.constructionElement.insertBefore(this.transforms.getElement(), this.digestMethodElem);
            }

            this.transforms.addTransform("http://www.w3.org/2006/12/xml-c14n11");
            var5.updateOutputStream(var2, true);
         } else {
            var5.updateOutputStream(var2);
         }

         var2.flush();
         if (var5.getOctetStreamReal() != null) {
            var5.getOctetStreamReal().close();
         }

         var6 = var4.getDigestValue();
      } catch (XMLSecurityException var16) {
         throw new ReferenceNotInitializedException("empty", var16);
      } catch (IOException var17) {
         throw new ReferenceNotInitializedException("empty", var17);
      } finally {
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var15) {
               throw new ReferenceNotInitializedException("empty", var15);
            }
         }

      }

      return var6;
   }

   public byte[] getDigestValue() throws Base64DecodingException, XMLSecurityException {
      if (this.digestValueElement == null) {
         Object[] var1 = new Object[]{"DigestValue", "http://www.w3.org/2000/09/xmldsig#"};
         throw new XMLSecurityException("signature.Verification.NoSignatureElement", var1);
      } else {
         return Base64.decode(this.digestValueElement);
      }
   }

   public boolean verify() throws ReferenceNotInitializedException, XMLSecurityException {
      byte[] var1 = this.getDigestValue();
      byte[] var2 = this.calculateDigest(true);
      boolean var3 = MessageDigestAlgorithm.isEqual(var1, var2);
      if (!var3) {
         log.log(Level.WARNING, "Verification failed for URI \"" + this.getURI() + "\"");
         log.log(Level.WARNING, "Expected Digest: " + Base64.encode(var1));
         log.log(Level.WARNING, "Actual Digest: " + Base64.encode(var2));
      } else if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Verification successful for URI \"" + this.getURI() + "\"");
      }

      return var3;
   }

   public String getBaseLocalName() {
      return "Reference";
   }
}
