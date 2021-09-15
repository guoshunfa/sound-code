package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Manifest extends SignatureElementProxy {
   public static final int MAXIMUM_REFERENCE_COUNT = 30;
   private static Logger log = Logger.getLogger(Manifest.class.getName());
   private List<Reference> references;
   private Element[] referencesEl;
   private boolean[] verificationResults;
   private Map<String, String> resolverProperties;
   private List<ResourceResolver> perManifestResolvers;
   private boolean secureValidation;

   public Manifest(Document var1) {
      super(var1);
      this.verificationResults = null;
      this.resolverProperties = null;
      this.perManifestResolvers = null;
      XMLUtils.addReturnToElement(this.constructionElement);
      this.references = new ArrayList();
   }

   public Manifest(Element var1, String var2) throws XMLSecurityException {
      this(var1, var2, false);
   }

   public Manifest(Element var1, String var2, boolean var3) throws XMLSecurityException {
      super(var1, var2);
      this.verificationResults = null;
      this.resolverProperties = null;
      this.perManifestResolvers = null;
      Attr var4 = var1.getAttributeNodeNS((String)null, "Id");
      if (var4 != null) {
         var1.setIdAttributeNode(var4, true);
      }

      this.secureValidation = var3;
      this.referencesEl = XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Reference");
      int var5 = this.referencesEl.length;
      Object[] var9;
      if (var5 == 0) {
         var9 = new Object[]{"Reference", "Manifest"};
         throw new DOMException((short)4, I18n.translate("xml.WrongContent", var9));
      } else if (var3 && var5 > 30) {
         var9 = new Object[]{var5, 30};
         throw new XMLSecurityException("signature.tooManyReferences", var9);
      } else {
         this.references = new ArrayList(var5);

         for(int var6 = 0; var6 < var5; ++var6) {
            Element var7 = this.referencesEl[var6];
            Attr var8 = var7.getAttributeNodeNS((String)null, "Id");
            if (var8 != null) {
               var7.setIdAttributeNode(var8, true);
            }

            this.references.add((Object)null);
         }

      }
   }

   public void addDocument(String var1, String var2, Transforms var3, String var4, String var5, String var6) throws XMLSignatureException {
      Reference var7 = new Reference(this.doc, var1, var2, this, var3, var4);
      if (var5 != null) {
         var7.setId(var5);
      }

      if (var6 != null) {
         var7.setType(var6);
      }

      this.references.add(var7);
      this.constructionElement.appendChild(var7.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void generateDigestValues() throws XMLSignatureException, ReferenceNotInitializedException {
      for(int var1 = 0; var1 < this.getLength(); ++var1) {
         Reference var2 = (Reference)this.references.get(var1);
         var2.generateDigestValue();
      }

   }

   public int getLength() {
      return this.references.size();
   }

   public Reference item(int var1) throws XMLSecurityException {
      if (this.references.get(var1) == null) {
         Reference var2 = new Reference(this.referencesEl[var1], this.baseURI, this, this.secureValidation);
         this.references.set(var1, var2);
      }

      return (Reference)this.references.get(var1);
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

   public boolean verifyReferences() throws MissingResourceFailureException, XMLSecurityException {
      return this.verifyReferences(false);
   }

   public boolean verifyReferences(boolean var1) throws MissingResourceFailureException, XMLSecurityException {
      if (this.referencesEl == null) {
         this.referencesEl = XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Reference");
      }

      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "verify " + this.referencesEl.length + " References");
         log.log(Level.FINE, "I am " + (var1 ? "" : "not") + " requested to follow nested Manifests");
      }

      if (this.referencesEl.length == 0) {
         throw new XMLSecurityException("empty");
      } else if (this.secureValidation && this.referencesEl.length > 30) {
         Object[] var17 = new Object[]{this.referencesEl.length, 30};
         throw new XMLSecurityException("signature.tooManyReferences", var17);
      } else {
         this.verificationResults = new boolean[this.referencesEl.length];
         boolean var2 = true;

         for(int var3 = 0; var3 < this.referencesEl.length; ++var3) {
            Reference var4 = new Reference(this.referencesEl[var3], this.baseURI, this, this.secureValidation);
            this.references.set(var3, var4);

            try {
               boolean var5 = var4.verify();
               this.setVerificationResult(var3, var5);
               if (!var5) {
                  var2 = false;
               }

               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "The Reference has Type " + var4.getType());
               }

               if (var2 && var1 && var4.typeIsReferenceToManifest()) {
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, "We have to follow a nested Manifest");
                  }

                  try {
                     XMLSignatureInput var18 = var4.dereferenceURIandPerformTransforms((OutputStream)null);
                     Set var7 = var18.getNodeSet();
                     Manifest var8 = null;
                     Iterator var9 = var7.iterator();

                     label96:
                     while(true) {
                        Node var10;
                        do {
                           do {
                              do {
                                 if (!var9.hasNext()) {
                                    break label96;
                                 }

                                 var10 = (Node)var9.next();
                              } while(var10.getNodeType() != 1);
                           } while(!((Element)var10).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#"));
                        } while(!((Element)var10).getLocalName().equals("Manifest"));

                        try {
                           var8 = new Manifest((Element)var10, var18.getSourceURI(), this.secureValidation);
                           break;
                        } catch (XMLSecurityException var12) {
                           if (log.isLoggable(Level.FINE)) {
                              log.log(Level.FINE, (String)var12.getMessage(), (Throwable)var12);
                           }
                        }
                     }

                     if (var8 == null) {
                        throw new MissingResourceFailureException("empty", var4);
                     }

                     var8.perManifestResolvers = this.perManifestResolvers;
                     var8.resolverProperties = this.resolverProperties;
                     boolean var19 = var8.verifyReferences(var1);
                     if (!var19) {
                        var2 = false;
                        log.log(Level.WARNING, "The nested Manifest was invalid (bad)");
                     } else if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "The nested Manifest was valid (good)");
                     }
                  } catch (IOException var13) {
                     throw new ReferenceNotInitializedException("empty", var13);
                  } catch (ParserConfigurationException var14) {
                     throw new ReferenceNotInitializedException("empty", var14);
                  } catch (SAXException var15) {
                     throw new ReferenceNotInitializedException("empty", var15);
                  }
               }
            } catch (ReferenceNotInitializedException var16) {
               Object[] var6 = new Object[]{var4.getURI()};
               throw new MissingResourceFailureException("signature.Verification.Reference.NoInput", var6, var16, var4);
            }
         }

         return var2;
      }
   }

   private void setVerificationResult(int var1, boolean var2) {
      if (this.verificationResults == null) {
         this.verificationResults = new boolean[this.getLength()];
      }

      this.verificationResults[var1] = var2;
   }

   public boolean getVerificationResult(int var1) throws XMLSecurityException {
      if (var1 >= 0 && var1 <= this.getLength() - 1) {
         if (this.verificationResults == null) {
            try {
               this.verifyReferences();
            } catch (Exception var4) {
               throw new XMLSecurityException("generic.EmptyMessage", var4);
            }
         }

         return this.verificationResults[var1];
      } else {
         Object[] var2 = new Object[]{Integer.toString(var1), Integer.toString(this.getLength())};
         IndexOutOfBoundsException var3 = new IndexOutOfBoundsException(I18n.translate("signature.Verification.IndexOutOfBounds", var2));
         throw new XMLSecurityException("generic.EmptyMessage", var3);
      }
   }

   public void addResourceResolver(ResourceResolver var1) {
      if (var1 != null) {
         if (this.perManifestResolvers == null) {
            this.perManifestResolvers = new ArrayList();
         }

         this.perManifestResolvers.add(var1);
      }
   }

   public void addResourceResolver(ResourceResolverSpi var1) {
      if (var1 != null) {
         if (this.perManifestResolvers == null) {
            this.perManifestResolvers = new ArrayList();
         }

         this.perManifestResolvers.add(new ResourceResolver(var1));
      }
   }

   public List<ResourceResolver> getPerManifestResolvers() {
      return this.perManifestResolvers;
   }

   public Map<String, String> getResolverProperties() {
      return this.resolverProperties;
   }

   public void setResolverProperty(String var1, String var2) {
      if (this.resolverProperties == null) {
         this.resolverProperties = new HashMap(10);
      }

      this.resolverProperties.put(var1, var2);
   }

   public String getResolverProperty(String var1) {
      return (String)this.resolverProperties.get(var1);
   }

   public byte[] getSignedContentItem(int var1) throws XMLSignatureException {
      try {
         return this.getReferencedContentAfterTransformsItem(var1).getBytes();
      } catch (IOException var3) {
         throw new XMLSignatureException("empty", var3);
      } catch (CanonicalizationException var4) {
         throw new XMLSignatureException("empty", var4);
      } catch (InvalidCanonicalizerException var5) {
         throw new XMLSignatureException("empty", var5);
      } catch (XMLSecurityException var6) {
         throw new XMLSignatureException("empty", var6);
      }
   }

   public XMLSignatureInput getReferencedContentBeforeTransformsItem(int var1) throws XMLSecurityException {
      return this.item(var1).getContentsBeforeTransformation();
   }

   public XMLSignatureInput getReferencedContentAfterTransformsItem(int var1) throws XMLSecurityException {
      return this.item(var1).getContentsAfterTransformation();
   }

   public int getSignedContentLength() {
      return this.getLength();
   }

   public String getBaseLocalName() {
      return "Manifest";
   }
}
