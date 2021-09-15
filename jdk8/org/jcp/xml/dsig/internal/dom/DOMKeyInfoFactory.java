package org.jcp.xml.dsig.internal.dom;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMKeyInfoFactory extends KeyInfoFactory {
   public KeyInfo newKeyInfo(List var1) {
      return this.newKeyInfo(var1, (String)null);
   }

   public KeyInfo newKeyInfo(List var1, String var2) {
      return new DOMKeyInfo(var1, var2);
   }

   public KeyName newKeyName(String var1) {
      return new DOMKeyName(var1);
   }

   public KeyValue newKeyValue(PublicKey var1) throws KeyException {
      String var2 = var1.getAlgorithm();
      if (var2.equals("DSA")) {
         return new DOMKeyValue.DSA(var1);
      } else if (var2.equals("RSA")) {
         return new DOMKeyValue.RSA(var1);
      } else if (var2.equals("EC")) {
         return new DOMKeyValue.EC(var1);
      } else {
         throw new KeyException("unsupported key algorithm: " + var2);
      }
   }

   public PGPData newPGPData(byte[] var1) {
      return this.newPGPData(var1, (byte[])null, (List)null);
   }

   public PGPData newPGPData(byte[] var1, byte[] var2, List var3) {
      return new DOMPGPData(var1, var2, var3);
   }

   public PGPData newPGPData(byte[] var1, List var2) {
      return new DOMPGPData(var1, var2);
   }

   public RetrievalMethod newRetrievalMethod(String var1) {
      return this.newRetrievalMethod(var1, (String)null, (List)null);
   }

   public RetrievalMethod newRetrievalMethod(String var1, String var2, List var3) {
      if (var1 == null) {
         throw new NullPointerException("uri must not be null");
      } else {
         return new DOMRetrievalMethod(var1, var2, var3);
      }
   }

   public X509Data newX509Data(List var1) {
      return new DOMX509Data(var1);
   }

   public X509IssuerSerial newX509IssuerSerial(String var1, BigInteger var2) {
      return new DOMX509IssuerSerial(var1, var2);
   }

   public boolean isFeatureSupported(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return false;
      }
   }

   public URIDereferencer getURIDereferencer() {
      return DOMURIDereferencer.INSTANCE;
   }

   public KeyInfo unmarshalKeyInfo(XMLStructure var1) throws MarshalException {
      if (var1 == null) {
         throw new NullPointerException("xmlStructure cannot be null");
      } else if (!(var1 instanceof javax.xml.crypto.dom.DOMStructure)) {
         throw new ClassCastException("xmlStructure must be of type DOMStructure");
      } else {
         Node var2 = ((javax.xml.crypto.dom.DOMStructure)var1).getNode();
         var2.normalize();
         Element var3 = null;
         if (var2.getNodeType() == 9) {
            var3 = ((Document)var2).getDocumentElement();
         } else {
            if (var2.getNodeType() != 1) {
               throw new MarshalException("xmlStructure does not contain a proper Node");
            }

            var3 = (Element)var2;
         }

         String var4 = var3.getLocalName();
         if (var4 == null) {
            throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware");
         } else if (var4.equals("KeyInfo")) {
            return new DOMKeyInfo(var3, new DOMKeyInfoFactory.UnmarshalContext(), this.getProvider());
         } else {
            throw new MarshalException("invalid KeyInfo tag: " + var4);
         }
      }
   }

   private static class UnmarshalContext extends DOMCryptoContext {
      UnmarshalContext() {
      }
   }
}
