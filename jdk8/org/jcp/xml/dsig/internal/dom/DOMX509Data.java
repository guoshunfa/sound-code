package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.ByteArrayInputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMX509Data extends DOMStructure implements X509Data {
   private final List<Object> content;
   private CertificateFactory cf;

   public DOMX509Data(List<?> var1) {
      if (var1 == null) {
         throw new NullPointerException("content cannot be null");
      } else {
         ArrayList var2 = new ArrayList(var1);
         if (var2.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
         } else {
            int var3 = 0;

            for(int var4 = var2.size(); var3 < var4; ++var3) {
               Object var5 = var2.get(var3);
               if (var5 instanceof String) {
                  new X500Principal((String)var5);
               } else if (!(var5 instanceof byte[]) && !(var5 instanceof X509Certificate) && !(var5 instanceof X509CRL) && !(var5 instanceof XMLStructure)) {
                  throw new ClassCastException("content[" + var3 + "] is not a valid X509Data type");
               }
            }

            this.content = Collections.unmodifiableList(var2);
         }
      }
   }

   public DOMX509Data(Element var1) throws MarshalException {
      NodeList var2 = var1.getChildNodes();
      int var3 = var2.getLength();
      ArrayList var4 = new ArrayList(var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         Node var6 = var2.item(var5);
         if (var6.getNodeType() == 1) {
            Element var7 = (Element)var6;
            String var8 = var7.getLocalName();
            if (var8.equals("X509Certificate")) {
               var4.add(this.unmarshalX509Certificate(var7));
            } else if (var8.equals("X509IssuerSerial")) {
               var4.add(new DOMX509IssuerSerial(var7));
            } else if (var8.equals("X509SubjectName")) {
               var4.add(var7.getFirstChild().getNodeValue());
            } else if (var8.equals("X509SKI")) {
               try {
                  var4.add(Base64.decode(var7));
               } catch (Base64DecodingException var10) {
                  throw new MarshalException("cannot decode X509SKI", var10);
               }
            } else if (var8.equals("X509CRL")) {
               var4.add(this.unmarshalX509CRL(var7));
            } else {
               var4.add(new javax.xml.crypto.dom.DOMStructure(var7));
            }
         }
      }

      this.content = Collections.unmodifiableList(var4);
   }

   public List getContent() {
      return this.content;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "X509Data", "http://www.w3.org/2000/09/xmldsig#", var2);
      int var6 = 0;

      for(int var7 = this.content.size(); var6 < var7; ++var6) {
         Object var8 = this.content.get(var6);
         if (var8 instanceof X509Certificate) {
            this.marshalCert((X509Certificate)var8, var5, var4, var2);
         } else if (var8 instanceof XMLStructure) {
            if (var8 instanceof X509IssuerSerial) {
               ((DOMX509IssuerSerial)var8).marshal(var5, var2, var3);
            } else {
               javax.xml.crypto.dom.DOMStructure var9 = (javax.xml.crypto.dom.DOMStructure)var8;
               DOMUtils.appendChild(var5, var9.getNode());
            }
         } else if (var8 instanceof byte[]) {
            this.marshalSKI((byte[])((byte[])var8), var5, var4, var2);
         } else if (var8 instanceof String) {
            this.marshalSubjectName((String)var8, var5, var4, var2);
         } else if (var8 instanceof X509CRL) {
            this.marshalCRL((X509CRL)var8, var5, var4, var2);
         }
      }

      var1.appendChild(var5);
   }

   private void marshalSKI(byte[] var1, Node var2, Document var3, String var4) {
      Element var5 = DOMUtils.createElement(var3, "X509SKI", "http://www.w3.org/2000/09/xmldsig#", var4);
      var5.appendChild(var3.createTextNode(Base64.encode(var1)));
      var2.appendChild(var5);
   }

   private void marshalSubjectName(String var1, Node var2, Document var3, String var4) {
      Element var5 = DOMUtils.createElement(var3, "X509SubjectName", "http://www.w3.org/2000/09/xmldsig#", var4);
      var5.appendChild(var3.createTextNode(var1));
      var2.appendChild(var5);
   }

   private void marshalCert(X509Certificate var1, Node var2, Document var3, String var4) throws MarshalException {
      Element var5 = DOMUtils.createElement(var3, "X509Certificate", "http://www.w3.org/2000/09/xmldsig#", var4);

      try {
         var5.appendChild(var3.createTextNode(Base64.encode(var1.getEncoded())));
      } catch (CertificateEncodingException var7) {
         throw new MarshalException("Error encoding X509Certificate", var7);
      }

      var2.appendChild(var5);
   }

   private void marshalCRL(X509CRL var1, Node var2, Document var3, String var4) throws MarshalException {
      Element var5 = DOMUtils.createElement(var3, "X509CRL", "http://www.w3.org/2000/09/xmldsig#", var4);

      try {
         var5.appendChild(var3.createTextNode(Base64.encode(var1.getEncoded())));
      } catch (CRLException var7) {
         throw new MarshalException("Error encoding X509CRL", var7);
      }

      var2.appendChild(var5);
   }

   private X509Certificate unmarshalX509Certificate(Element var1) throws MarshalException {
      try {
         ByteArrayInputStream var2 = this.unmarshalBase64Binary(var1);
         return (X509Certificate)this.cf.generateCertificate(var2);
      } catch (CertificateException var3) {
         throw new MarshalException("Cannot create X509Certificate", var3);
      }
   }

   private X509CRL unmarshalX509CRL(Element var1) throws MarshalException {
      try {
         ByteArrayInputStream var2 = this.unmarshalBase64Binary(var1);
         return (X509CRL)this.cf.generateCRL(var2);
      } catch (CRLException var3) {
         throw new MarshalException("Cannot create X509CRL", var3);
      }
   }

   private ByteArrayInputStream unmarshalBase64Binary(Element var1) throws MarshalException {
      try {
         if (this.cf == null) {
            this.cf = CertificateFactory.getInstance("X.509");
         }

         return new ByteArrayInputStream(Base64.decode(var1));
      } catch (CertificateException var3) {
         throw new MarshalException("Cannot create CertificateFactory", var3);
      } catch (Base64DecodingException var4) {
         throw new MarshalException("Cannot decode Base64-encoded val", var4);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof X509Data)) {
         return false;
      } else {
         X509Data var2 = (X509Data)var1;
         List var3 = var2.getContent();
         int var4 = this.content.size();
         if (var4 != var3.size()) {
            return false;
         } else {
            for(int var5 = 0; var5 < var4; ++var5) {
               Object var6 = this.content.get(var5);
               Object var7 = var3.get(var5);
               if (var6 instanceof byte[]) {
                  if (!(var7 instanceof byte[]) || !Arrays.equals((byte[])((byte[])var6), (byte[])((byte[])var7))) {
                     return false;
                  }
               } else if (!var6.equals(var7)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 31 * var1 + this.content.hashCode();
      return var2;
   }
}
