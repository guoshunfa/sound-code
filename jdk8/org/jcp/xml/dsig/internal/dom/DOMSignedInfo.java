package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMSignedInfo extends DOMStructure implements SignedInfo {
   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
   private List<Reference> references;
   private CanonicalizationMethod canonicalizationMethod;
   private SignatureMethod signatureMethod;
   private String id;
   private Document ownerDoc;
   private Element localSiElem;
   private InputStream canonData;

   public DOMSignedInfo(CanonicalizationMethod var1, SignatureMethod var2, List<? extends Reference> var3) {
      if (var1 != null && var2 != null && var3 != null) {
         this.canonicalizationMethod = var1;
         this.signatureMethod = var2;
         this.references = Collections.unmodifiableList(new ArrayList(var3));
         if (this.references.isEmpty()) {
            throw new IllegalArgumentException("list of references must contain at least one entry");
         } else {
            int var4 = 0;

            for(int var5 = this.references.size(); var4 < var5; ++var4) {
               Object var6 = this.references.get(var4);
               if (!(var6 instanceof Reference)) {
                  throw new ClassCastException("list of references contains an illegal type");
               }
            }

         }
      } else {
         throw new NullPointerException();
      }
   }

   public DOMSignedInfo(CanonicalizationMethod var1, SignatureMethod var2, List<? extends Reference> var3, String var4) {
      this(var1, var2, var3);
      this.id = var4;
   }

   public DOMSignedInfo(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      this.localSiElem = var1;
      this.ownerDoc = var1.getOwnerDocument();
      this.id = DOMUtils.getAttributeValue(var1, "Id");
      Element var4 = DOMUtils.getFirstChildElement(var1, "CanonicalizationMethod");
      this.canonicalizationMethod = new DOMCanonicalizationMethod(var4, var2, var3);
      Element var5 = DOMUtils.getNextSiblingElement(var4, "SignatureMethod");
      this.signatureMethod = DOMSignatureMethod.unmarshal(var5);
      boolean var6 = Utils.secureValidation(var2);
      String var7 = this.signatureMethod.getAlgorithm();
      if (var6 && Policy.restrictAlg(var7)) {
         throw new MarshalException("It is forbidden to use algorithm " + var7 + " when secure validation is enabled");
      } else {
         ArrayList var8 = new ArrayList(5);
         Element var9 = DOMUtils.getNextSiblingElement(var5, "Reference");
         var8.add(new DOMReference(var9, var2, var3));

         for(var9 = DOMUtils.getNextSiblingElement(var9); var9 != null; var9 = DOMUtils.getNextSiblingElement(var9)) {
            String var10 = var9.getLocalName();
            if (!var10.equals("Reference")) {
               throw new MarshalException("Invalid element name: " + var10 + ", expected Reference");
            }

            var8.add(new DOMReference(var9, var2, var3));
            if (var6 && Policy.restrictNumReferences(var8.size())) {
               String var11 = "A maximum of " + Policy.maxReferences() + " references per Manifest are allowed when secure validation is enabled";
               throw new MarshalException(var11);
            }
         }

         this.references = Collections.unmodifiableList(var8);
      }
   }

   public CanonicalizationMethod getCanonicalizationMethod() {
      return this.canonicalizationMethod;
   }

   public SignatureMethod getSignatureMethod() {
      return this.signatureMethod;
   }

   public String getId() {
      return this.id;
   }

   public List getReferences() {
      return this.references;
   }

   public InputStream getCanonicalizedData() {
      return this.canonData;
   }

   public void canonicalize(XMLCryptoContext var1, ByteArrayOutputStream var2) throws XMLSignatureException {
      if (var1 == null) {
         throw new NullPointerException("context cannot be null");
      } else {
         UnsyncBufferedOutputStream var3 = new UnsyncBufferedOutputStream(var2);
         DOMSubTreeData var4 = new DOMSubTreeData(this.localSiElem, true);

         try {
            ((DOMCanonicalizationMethod)this.canonicalizationMethod).canonicalize(var4, var1, var3);
         } catch (TransformException var8) {
            throw new XMLSignatureException(var8);
         }

         try {
            var3.flush();
         } catch (IOException var10) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)var10.getMessage(), (Throwable)var10);
            }
         }

         byte[] var5 = var2.toByteArray();
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Canonicalized SignedInfo:");
            StringBuilder var6 = new StringBuilder(var5.length);

            for(int var7 = 0; var7 < var5.length; ++var7) {
               var6.append((char)var5[var7]);
            }

            log.log(Level.FINE, var6.toString());
            log.log(Level.FINE, "Data to be signed/verified:" + Base64.encode(var5));
         }

         this.canonData = new ByteArrayInputStream(var5);

         try {
            var3.close();
         } catch (IOException var9) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)var9.getMessage(), (Throwable)var9);
            }
         }

      }
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      this.ownerDoc = DOMUtils.getOwnerDocument(var1);
      Element var4 = DOMUtils.createElement(this.ownerDoc, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMCanonicalizationMethod var5 = (DOMCanonicalizationMethod)this.canonicalizationMethod;
      var5.marshal(var4, var2, var3);
      ((DOMStructure)this.signatureMethod).marshal(var4, var2, var3);
      Iterator var6 = this.references.iterator();

      while(var6.hasNext()) {
         Reference var7 = (Reference)var6.next();
         ((DOMReference)var7).marshal(var4, var2, var3);
      }

      DOMUtils.setAttributeID(var4, "Id", this.id);
      var1.appendChild(var4);
      this.localSiElem = var4;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SignedInfo)) {
         return false;
      } else {
         SignedInfo var2 = (SignedInfo)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         return this.canonicalizationMethod.equals(var2.getCanonicalizationMethod()) && this.signatureMethod.equals(var2.getSignatureMethod()) && this.references.equals(var2.getReferences()) && var3;
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      var1 = 31 * var1 + this.canonicalizationMethod.hashCode();
      var1 = 31 * var1 + this.signatureMethod.hashCode();
      var1 = 31 * var1 + this.references.hashCode();
      return var1;
   }
}
