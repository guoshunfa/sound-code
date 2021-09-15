package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMXMLSignature extends DOMStructure implements XMLSignature {
   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
   private String id;
   private XMLSignature.SignatureValue sv;
   private KeyInfo ki;
   private List<XMLObject> objects;
   private SignedInfo si;
   private Document ownerDoc = null;
   private Element localSigElem = null;
   private Element sigElem = null;
   private boolean validationStatus;
   private boolean validated = false;
   private KeySelectorResult ksr;
   private HashMap<String, XMLStructure> signatureIdMap;

   public DOMXMLSignature(SignedInfo var1, KeyInfo var2, List<? extends XMLObject> var3, String var4, String var5) {
      if (var1 == null) {
         throw new NullPointerException("signedInfo cannot be null");
      } else {
         this.si = var1;
         this.id = var4;
         this.sv = new DOMXMLSignature.DOMSignatureValue(var5);
         if (var3 == null) {
            this.objects = Collections.emptyList();
         } else {
            this.objects = Collections.unmodifiableList(new ArrayList(var3));
            int var6 = 0;

            for(int var7 = this.objects.size(); var6 < var7; ++var6) {
               if (!(this.objects.get(var6) instanceof XMLObject)) {
                  throw new ClassCastException("objs[" + var6 + "] is not an XMLObject");
               }
            }
         }

         this.ki = var2;
      }
   }

   public DOMXMLSignature(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      this.localSigElem = var1;
      this.ownerDoc = this.localSigElem.getOwnerDocument();
      this.id = DOMUtils.getAttributeValue(this.localSigElem, "Id");
      Element var4 = DOMUtils.getFirstChildElement(this.localSigElem, "SignedInfo");
      this.si = new DOMSignedInfo(var4, var2, var3);
      Element var5 = DOMUtils.getNextSiblingElement(var4, "SignatureValue");
      this.sv = new DOMXMLSignature.DOMSignatureValue(var5, var2);
      Element var6 = DOMUtils.getNextSiblingElement(var5);
      if (var6 != null && var6.getLocalName().equals("KeyInfo")) {
         this.ki = new DOMKeyInfo(var6, var2, var3);
         var6 = DOMUtils.getNextSiblingElement(var6);
      }

      if (var6 == null) {
         this.objects = Collections.emptyList();
      } else {
         ArrayList var7;
         for(var7 = new ArrayList(); var6 != null; var6 = DOMUtils.getNextSiblingElement(var6)) {
            String var8 = var6.getLocalName();
            if (!var8.equals("Object")) {
               throw new MarshalException("Invalid element name: " + var8 + ", expected KeyInfo or Object");
            }

            var7.add(new DOMXMLObject(var6, var2, var3));
         }

         this.objects = Collections.unmodifiableList(var7);
      }

   }

   public String getId() {
      return this.id;
   }

   public KeyInfo getKeyInfo() {
      return this.ki;
   }

   public SignedInfo getSignedInfo() {
      return this.si;
   }

   public List getObjects() {
      return this.objects;
   }

   public XMLSignature.SignatureValue getSignatureValue() {
      return this.sv;
   }

   public KeySelectorResult getKeySelectorResult() {
      return this.ksr;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      this.marshal(var1, (Node)null, var2, var3);
   }

   public void marshal(Node var1, Node var2, String var3, DOMCryptoContext var4) throws MarshalException {
      this.ownerDoc = DOMUtils.getOwnerDocument(var1);
      this.sigElem = DOMUtils.createElement(this.ownerDoc, "Signature", "http://www.w3.org/2000/09/xmldsig#", var3);
      if (var3 != null && var3.length() != 0) {
         this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var3, "http://www.w3.org/2000/09/xmldsig#");
      } else {
         this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
      }

      ((DOMSignedInfo)this.si).marshal(this.sigElem, var3, var4);
      ((DOMXMLSignature.DOMSignatureValue)this.sv).marshal(this.sigElem, var3, var4);
      if (this.ki != null) {
         ((DOMKeyInfo)this.ki).marshal(this.sigElem, (Node)null, var3, var4);
      }

      int var5 = 0;

      for(int var6 = this.objects.size(); var5 < var6; ++var5) {
         ((DOMXMLObject)this.objects.get(var5)).marshal(this.sigElem, var3, var4);
      }

      DOMUtils.setAttributeID(this.sigElem, "Id", this.id);
      var1.insertBefore(this.sigElem, var2);
   }

   public boolean validate(XMLValidateContext var1) throws XMLSignatureException {
      if (var1 == null) {
         throw new NullPointerException("validateContext is null");
      } else if (!(var1 instanceof DOMValidateContext)) {
         throw new ClassCastException("validateContext must be of type DOMValidateContext");
      } else if (this.validated) {
         return this.validationStatus;
      } else {
         boolean var2 = this.sv.validate(var1);
         if (!var2) {
            this.validationStatus = false;
            this.validated = true;
            return this.validationStatus;
         } else {
            List var3 = this.si.getReferences();
            boolean var4 = true;
            int var5 = 0;

            int var6;
            for(var6 = var3.size(); var4 && var5 < var6; ++var5) {
               Reference var7 = (Reference)var3.get(var5);
               boolean var8 = var7.validate(var1);
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Reference[" + var7.getURI() + "] is valid: " + var8);
               }

               var4 &= var8;
            }

            if (!var4) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Couldn't validate the References");
               }

               this.validationStatus = false;
               this.validated = true;
               return this.validationStatus;
            } else {
               boolean var19 = true;
               if (Boolean.TRUE.equals(var1.getProperty("org.jcp.xml.dsig.validateManifests"))) {
                  var6 = 0;

                  for(int var20 = this.objects.size(); var19 && var6 < var20; ++var6) {
                     XMLObject var21 = (XMLObject)this.objects.get(var6);
                     List var9 = var21.getContent();
                     int var10 = var9.size();

                     for(int var11 = 0; var19 && var11 < var10; ++var11) {
                        XMLStructure var12 = (XMLStructure)var9.get(var11);
                        if (var12 instanceof Manifest) {
                           if (log.isLoggable(Level.FINE)) {
                              log.log(Level.FINE, "validating manifest");
                           }

                           Manifest var13 = (Manifest)var12;
                           List var14 = var13.getReferences();
                           int var15 = var14.size();

                           for(int var16 = 0; var19 && var16 < var15; ++var16) {
                              Reference var17 = (Reference)var14.get(var16);
                              boolean var18 = var17.validate(var1);
                              if (log.isLoggable(Level.FINE)) {
                                 log.log(Level.FINE, "Manifest ref[" + var17.getURI() + "] is valid: " + var18);
                              }

                              var19 &= var18;
                           }
                        }
                     }
                  }
               }

               this.validationStatus = var19;
               this.validated = true;
               return this.validationStatus;
            }
         }
      }
   }

   public void sign(XMLSignContext var1) throws MarshalException, XMLSignatureException {
      if (var1 == null) {
         throw new NullPointerException("signContext cannot be null");
      } else {
         DOMSignContext var2 = (DOMSignContext)var1;
         this.marshal(var2.getParent(), var2.getNextSibling(), DOMUtils.getSignaturePrefix(var2), var2);
         ArrayList var3 = new ArrayList();
         this.signatureIdMap = new HashMap();
         this.signatureIdMap.put(this.id, this);
         this.signatureIdMap.put(this.si.getId(), this.si);
         List var4 = this.si.getReferences();
         Iterator var5 = var4.iterator();

         Reference var6;
         while(var5.hasNext()) {
            var6 = (Reference)var5.next();
            this.signatureIdMap.put(var6.getId(), var6);
         }

         var5 = this.objects.iterator();

         label79:
         while(var5.hasNext()) {
            XMLObject var16 = (XMLObject)var5.next();
            this.signatureIdMap.put(var16.getId(), var16);
            List var7 = var16.getContent();
            Iterator var8 = var7.iterator();

            while(true) {
               XMLStructure var9;
               do {
                  if (!var8.hasNext()) {
                     continue label79;
                  }

                  var9 = (XMLStructure)var8.next();
               } while(!(var9 instanceof Manifest));

               Manifest var10 = (Manifest)var9;
               this.signatureIdMap.put(var10.getId(), var10);
               List var11 = var10.getReferences();
               Iterator var12 = var11.iterator();

               while(var12.hasNext()) {
                  Reference var13 = (Reference)var12.next();
                  var3.add(var13);
                  this.signatureIdMap.put(var13.getId(), var13);
               }
            }
         }

         var3.addAll(var4);
         var5 = var3.iterator();

         while(var5.hasNext()) {
            var6 = (Reference)var5.next();
            this.digestReference((DOMReference)var6, var1);
         }

         var5 = var3.iterator();

         while(var5.hasNext()) {
            var6 = (Reference)var5.next();
            if (!((DOMReference)var6).isDigested()) {
               ((DOMReference)var6).digest(var1);
            }
         }

         var5 = null;
         var6 = null;

         Key var17;
         KeySelectorResult var19;
         try {
            var19 = var1.getKeySelector().select(this.ki, KeySelector.Purpose.SIGN, this.si.getSignatureMethod(), var1);
            var17 = var19.getKey();
            if (var17 == null) {
               throw new XMLSignatureException("the keySelector did not find a signing key");
            }
         } catch (KeySelectorException var15) {
            throw new XMLSignatureException("cannot find signing key", var15);
         }

         try {
            byte[] var18 = ((AbstractDOMSignatureMethod)this.si.getSignatureMethod()).sign(var17, this.si, var1);
            ((DOMXMLSignature.DOMSignatureValue)this.sv).setValue(var18);
         } catch (InvalidKeyException var14) {
            throw new XMLSignatureException(var14);
         }

         this.localSigElem = this.sigElem;
         this.ksr = var19;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof XMLSignature)) {
         return false;
      } else {
         XMLSignature var2 = (XMLSignature)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         boolean var4 = this.ki == null ? var2.getKeyInfo() == null : this.ki.equals(var2.getKeyInfo());
         return var3 && var4 && this.sv.equals(var2.getSignatureValue()) && this.si.equals(var2.getSignedInfo()) && this.objects.equals(var2.getObjects());
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      if (this.ki != null) {
         var1 = 31 * var1 + this.ki.hashCode();
      }

      var1 = 31 * var1 + this.sv.hashCode();
      var1 = 31 * var1 + this.si.hashCode();
      var1 = 31 * var1 + this.objects.hashCode();
      return var1;
   }

   private void digestReference(DOMReference var1, XMLSignContext var2) throws XMLSignatureException {
      if (!var1.isDigested()) {
         String var3 = var1.getURI();
         if (Utils.sameDocumentURI(var3)) {
            String var4 = Utils.parseIdFromSameDocumentURI(var3);
            if (var4 != null && this.signatureIdMap.containsKey(var4)) {
               XMLStructure var5 = (XMLStructure)this.signatureIdMap.get(var4);
               if (var5 instanceof DOMReference) {
                  this.digestReference((DOMReference)var5, var2);
               } else if (var5 instanceof Manifest) {
                  Manifest var6 = (Manifest)var5;
                  List var7 = var6.getReferences();
                  int var8 = 0;

                  for(int var9 = var7.size(); var8 < var9; ++var8) {
                     this.digestReference((DOMReference)var7.get(var8), var2);
                  }
               }
            }

            if (var3.length() == 0) {
               List var10 = var1.getTransforms();
               Iterator var11 = var10.iterator();

               while(var11.hasNext()) {
                  Transform var12 = (Transform)var11.next();
                  String var13 = var12.getAlgorithm();
                  if (var13.equals("http://www.w3.org/TR/1999/REC-xpath-19991116") || var13.equals("http://www.w3.org/2002/06/xmldsig-filter2")) {
                     return;
                  }
               }
            }
         }

         var1.digest(var2);
      }
   }

   static {
      Init.init();
   }

   public class DOMSignatureValue extends DOMStructure implements XMLSignature.SignatureValue {
      private String id;
      private byte[] value;
      private String valueBase64;
      private Element sigValueElem;
      private boolean validated = false;
      private boolean validationStatus;

      DOMSignatureValue(String var2) {
         this.id = var2;
      }

      DOMSignatureValue(Element var2, XMLCryptoContext var3) throws MarshalException {
         try {
            this.value = Base64.decode(var2);
         } catch (Base64DecodingException var5) {
            throw new MarshalException(var5);
         }

         Attr var4 = var2.getAttributeNodeNS((String)null, "Id");
         if (var4 != null) {
            this.id = var4.getValue();
            var2.setIdAttributeNode(var4, true);
         } else {
            this.id = null;
         }

         this.sigValueElem = var2;
      }

      public String getId() {
         return this.id;
      }

      public byte[] getValue() {
         return this.value == null ? null : (byte[])((byte[])this.value.clone());
      }

      public boolean validate(XMLValidateContext var1) throws XMLSignatureException {
         if (var1 == null) {
            throw new NullPointerException("context cannot be null");
         } else if (this.validated) {
            return this.validationStatus;
         } else {
            SignatureMethod var2 = DOMXMLSignature.this.si.getSignatureMethod();
            Key var3 = null;

            KeySelectorResult var4;
            try {
               var4 = var1.getKeySelector().select(DOMXMLSignature.this.ki, KeySelector.Purpose.VERIFY, var2, var1);
               var3 = var4.getKey();
               if (var3 == null) {
                  throw new XMLSignatureException("the keyselector did not find a validation key");
               }
            } catch (KeySelectorException var7) {
               throw new XMLSignatureException("cannot find validation key", var7);
            }

            try {
               this.validationStatus = ((AbstractDOMSignatureMethod)var2).verify(var3, DOMXMLSignature.this.si, this.value, var1);
            } catch (Exception var6) {
               throw new XMLSignatureException(var6);
            }

            this.validated = true;
            DOMXMLSignature.this.ksr = var4;
            return this.validationStatus;
         }
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof XMLSignature.SignatureValue)) {
            return false;
         } else {
            XMLSignature.SignatureValue var2 = (XMLSignature.SignatureValue)var1;
            boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
            return var3;
         }
      }

      public int hashCode() {
         int var1 = 17;
         if (this.id != null) {
            var1 = 31 * var1 + this.id.hashCode();
         }

         return var1;
      }

      public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
         this.sigValueElem = DOMUtils.createElement(DOMXMLSignature.this.ownerDoc, "SignatureValue", "http://www.w3.org/2000/09/xmldsig#", var2);
         if (this.valueBase64 != null) {
            this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
         }

         DOMUtils.setAttributeID(this.sigValueElem, "Id", this.id);
         var1.appendChild(this.sigValueElem);
      }

      void setValue(byte[] var1) {
         this.value = var1;
         this.valueBase64 = Base64.encode(var1);
         this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
      }
   }
}
