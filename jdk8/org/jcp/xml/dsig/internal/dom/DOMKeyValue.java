package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMKeyValue extends DOMStructure implements KeyValue {
   private static final String XMLDSIG_11_XMLNS = "http://www.w3.org/2009/xmldsig11#";
   private final PublicKey publicKey;

   public DOMKeyValue(PublicKey var1) throws KeyException {
      if (var1 == null) {
         throw new NullPointerException("key cannot be null");
      } else {
         this.publicKey = var1;
      }
   }

   public DOMKeyValue(Element var1) throws MarshalException {
      this.publicKey = this.unmarshalKeyValue(var1);
   }

   static KeyValue unmarshal(Element var0) throws MarshalException {
      Element var1 = DOMUtils.getFirstChildElement(var0);
      if (var1.getLocalName().equals("DSAKeyValue")) {
         return new DOMKeyValue.DSA(var1);
      } else if (var1.getLocalName().equals("RSAKeyValue")) {
         return new DOMKeyValue.RSA(var1);
      } else {
         return (KeyValue)(var1.getLocalName().equals("ECKeyValue") ? new DOMKeyValue.EC(var1) : new DOMKeyValue.Unknown(var1));
      }
   }

   public PublicKey getPublicKey() throws KeyException {
      if (this.publicKey == null) {
         throw new KeyException("can't convert KeyValue to PublicKey");
      } else {
         return this.publicKey;
      }
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "KeyValue", "http://www.w3.org/2000/09/xmldsig#", var2);
      this.marshalPublicKey(var5, var4, var2, var3);
      var1.appendChild(var5);
   }

   abstract void marshalPublicKey(Node var1, Document var2, String var3, DOMCryptoContext var4) throws MarshalException;

   abstract PublicKey unmarshalKeyValue(Element var1) throws MarshalException;

   private static PublicKey generatePublicKey(KeyFactory var0, KeySpec var1) {
      try {
         return var0.generatePublic(var1);
      } catch (InvalidKeySpecException var3) {
         return null;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeyValue)) {
         return false;
      } else {
         try {
            KeyValue var2 = (KeyValue)var1;
            if (this.publicKey == null) {
               if (var2.getPublicKey() != null) {
                  return false;
               }
            } else if (!this.publicKey.equals(var2.getPublicKey())) {
               return false;
            }

            return true;
         } catch (KeyException var3) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.publicKey != null) {
         var1 = 31 * var1 + this.publicKey.hashCode();
      }

      return var1;
   }

   static final class Unknown extends DOMKeyValue {
      private javax.xml.crypto.dom.DOMStructure externalPublicKey;

      Unknown(Element var1) throws MarshalException {
         super(var1);
      }

      PublicKey unmarshalKeyValue(Element var1) throws MarshalException {
         this.externalPublicKey = new javax.xml.crypto.dom.DOMStructure(var1);
         return null;
      }

      void marshalPublicKey(Node var1, Document var2, String var3, DOMCryptoContext var4) throws MarshalException {
         var1.appendChild(this.externalPublicKey.getNode());
      }
   }

   static final class EC extends DOMKeyValue {
      private byte[] ecPublicKey;
      private KeyFactory eckf;
      private ECParameterSpec ecParams;
      private Method encodePoint;
      private Method decodePoint;
      private Method getCurveName;
      private Method getECParameterSpec;

      EC(PublicKey var1) throws KeyException {
         super(var1);
         ECPublicKey var2 = (ECPublicKey)var1;
         ECPoint var3 = var2.getW();
         this.ecParams = var2.getParams();

         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               public Void run() throws ClassNotFoundException, NoSuchMethodException {
                  EC.this.getMethods();
                  return null;
               }
            });
         } catch (PrivilegedActionException var8) {
            throw new KeyException("ECKeyValue not supported", var8.getException());
         }

         Object[] var4 = new Object[]{var3, this.ecParams.getCurve()};

         try {
            this.ecPublicKey = (byte[])((byte[])this.encodePoint.invoke((Object)null, var4));
         } catch (IllegalAccessException var6) {
            throw new KeyException(var6);
         } catch (InvocationTargetException var7) {
            throw new KeyException(var7);
         }
      }

      EC(Element var1) throws MarshalException {
         super(var1);
      }

      void getMethods() throws ClassNotFoundException, NoSuchMethodException {
         Class var1 = Class.forName("sun.security.ec.ECParameters");
         Class[] var2 = new Class[]{ECPoint.class, EllipticCurve.class};
         this.encodePoint = var1.getMethod("encodePoint", var2);
         var2 = new Class[]{ECParameterSpec.class};
         this.getCurveName = var1.getMethod("getCurveName", var2);
         var2 = new Class[]{byte[].class, EllipticCurve.class};
         this.decodePoint = var1.getMethod("decodePoint", var2);
         var1 = Class.forName("sun.security.ec.NamedCurve");
         var2 = new Class[]{String.class};
         this.getECParameterSpec = var1.getMethod("getECParameterSpec", var2);
      }

      void marshalPublicKey(Node var1, Document var2, String var3, DOMCryptoContext var4) throws MarshalException {
         String var5 = DOMUtils.getNSPrefix(var4, "http://www.w3.org/2009/xmldsig11#");
         Element var6 = DOMUtils.createElement(var2, "ECKeyValue", "http://www.w3.org/2009/xmldsig11#", var5);
         Element var7 = DOMUtils.createElement(var2, "NamedCurve", "http://www.w3.org/2009/xmldsig11#", var5);
         Element var8 = DOMUtils.createElement(var2, "PublicKey", "http://www.w3.org/2009/xmldsig11#", var5);
         Object[] var9 = new Object[]{this.ecParams};

         String var10;
         try {
            var10 = (String)this.getCurveName.invoke((Object)null, var9);
            DOMUtils.setAttribute(var7, "URI", "urn:oid:" + var10);
         } catch (IllegalAccessException var12) {
            throw new MarshalException(var12);
         } catch (InvocationTargetException var13) {
            throw new MarshalException(var13);
         }

         var10 = var5 != null && var5.length() != 0 ? "xmlns:" + var5 : "xmlns";
         var7.setAttributeNS("http://www.w3.org/2000/xmlns/", var10, "http://www.w3.org/2009/xmldsig11#");
         var6.appendChild(var7);
         String var11 = Base64.encode(this.ecPublicKey);
         var8.appendChild(DOMUtils.getOwnerDocument(var8).createTextNode(var11));
         var6.appendChild(var8);
         var1.appendChild(var6);
      }

      PublicKey unmarshalKeyValue(Element var1) throws MarshalException {
         if (this.eckf == null) {
            try {
               this.eckf = KeyFactory.getInstance("EC");
            } catch (NoSuchAlgorithmException var13) {
               throw new RuntimeException("unable to create EC KeyFactory: " + var13.getMessage());
            }
         }

         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               public Void run() throws ClassNotFoundException, NoSuchMethodException {
                  EC.this.getMethods();
                  return null;
               }
            });
         } catch (PrivilegedActionException var12) {
            throw new MarshalException("ECKeyValue not supported", var12.getException());
         }

         ECParameterSpec var2 = null;
         Element var3 = DOMUtils.getFirstChildElement(var1);
         if (var3.getLocalName().equals("ECParameters")) {
            throw new UnsupportedOperationException("ECParameters not supported");
         } else if (var3.getLocalName().equals("NamedCurve")) {
            String var4 = DOMUtils.getAttributeValue(var3, "URI");
            if (var4.startsWith("urn:oid:")) {
               String var5 = var4.substring(8);

               try {
                  Object[] var6 = new Object[]{var5};
                  var2 = (ECParameterSpec)this.getECParameterSpec.invoke((Object)null, var6);
               } catch (IllegalAccessException var10) {
                  throw new MarshalException(var10);
               } catch (InvocationTargetException var11) {
                  throw new MarshalException(var11);
               }

               var3 = DOMUtils.getNextSiblingElement(var3, "PublicKey");
               var4 = null;

               ECPoint var14;
               try {
                  Object[] var15 = new Object[]{Base64.decode(var3), var2.getCurve()};
                  var14 = (ECPoint)this.decodePoint.invoke((Object)null, var15);
               } catch (Base64DecodingException var7) {
                  throw new MarshalException("Invalid EC PublicKey", var7);
               } catch (IllegalAccessException var8) {
                  throw new MarshalException(var8);
               } catch (InvocationTargetException var9) {
                  throw new MarshalException(var9);
               }

               ECPublicKeySpec var16 = new ECPublicKeySpec(var14, var2);
               return DOMKeyValue.generatePublicKey(this.eckf, var16);
            } else {
               throw new MarshalException("Invalid NamedCurve URI");
            }
         } else {
            throw new MarshalException("Invalid ECKeyValue");
         }
      }
   }

   static final class DSA extends DOMKeyValue {
      private DOMCryptoBinary p;
      private DOMCryptoBinary q;
      private DOMCryptoBinary g;
      private DOMCryptoBinary y;
      private DOMCryptoBinary j;
      private KeyFactory dsakf;

      DSA(PublicKey var1) throws KeyException {
         super(var1);
         DSAPublicKey var2 = (DSAPublicKey)var1;
         DSAParams var3 = var2.getParams();
         this.p = new DOMCryptoBinary(var3.getP());
         this.q = new DOMCryptoBinary(var3.getQ());
         this.g = new DOMCryptoBinary(var3.getG());
         this.y = new DOMCryptoBinary(var2.getY());
      }

      DSA(Element var1) throws MarshalException {
         super(var1);
      }

      void marshalPublicKey(Node var1, Document var2, String var3, DOMCryptoContext var4) throws MarshalException {
         Element var5 = DOMUtils.createElement(var2, "DSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", var3);
         Element var6 = DOMUtils.createElement(var2, "P", "http://www.w3.org/2000/09/xmldsig#", var3);
         Element var7 = DOMUtils.createElement(var2, "Q", "http://www.w3.org/2000/09/xmldsig#", var3);
         Element var8 = DOMUtils.createElement(var2, "G", "http://www.w3.org/2000/09/xmldsig#", var3);
         Element var9 = DOMUtils.createElement(var2, "Y", "http://www.w3.org/2000/09/xmldsig#", var3);
         this.p.marshal(var6, var3, var4);
         this.q.marshal(var7, var3, var4);
         this.g.marshal(var8, var3, var4);
         this.y.marshal(var9, var3, var4);
         var5.appendChild(var6);
         var5.appendChild(var7);
         var5.appendChild(var8);
         var5.appendChild(var9);
         var1.appendChild(var5);
      }

      PublicKey unmarshalKeyValue(Element var1) throws MarshalException {
         if (this.dsakf == null) {
            try {
               this.dsakf = KeyFactory.getInstance("DSA");
            } catch (NoSuchAlgorithmException var4) {
               throw new RuntimeException("unable to create DSA KeyFactory: " + var4.getMessage());
            }
         }

         Element var2 = DOMUtils.getFirstChildElement(var1);
         if (var2.getLocalName().equals("P")) {
            this.p = new DOMCryptoBinary(var2.getFirstChild());
            var2 = DOMUtils.getNextSiblingElement(var2, "Q");
            this.q = new DOMCryptoBinary(var2.getFirstChild());
            var2 = DOMUtils.getNextSiblingElement(var2);
         }

         if (var2.getLocalName().equals("G")) {
            this.g = new DOMCryptoBinary(var2.getFirstChild());
            var2 = DOMUtils.getNextSiblingElement(var2, "Y");
         }

         this.y = new DOMCryptoBinary(var2.getFirstChild());
         var2 = DOMUtils.getNextSiblingElement(var2);
         if (var2 != null && var2.getLocalName().equals("J")) {
            this.j = new DOMCryptoBinary(var2.getFirstChild());
         }

         DSAPublicKeySpec var3 = new DSAPublicKeySpec(this.y.getBigNum(), this.p.getBigNum(), this.q.getBigNum(), this.g.getBigNum());
         return DOMKeyValue.generatePublicKey(this.dsakf, var3);
      }
   }

   static final class RSA extends DOMKeyValue {
      private DOMCryptoBinary modulus;
      private DOMCryptoBinary exponent;
      private KeyFactory rsakf;

      RSA(PublicKey var1) throws KeyException {
         super(var1);
         RSAPublicKey var2 = (RSAPublicKey)var1;
         this.exponent = new DOMCryptoBinary(var2.getPublicExponent());
         this.modulus = new DOMCryptoBinary(var2.getModulus());
      }

      RSA(Element var1) throws MarshalException {
         super(var1);
      }

      void marshalPublicKey(Node var1, Document var2, String var3, DOMCryptoContext var4) throws MarshalException {
         Element var5 = DOMUtils.createElement(var2, "RSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", var3);
         Element var6 = DOMUtils.createElement(var2, "Modulus", "http://www.w3.org/2000/09/xmldsig#", var3);
         Element var7 = DOMUtils.createElement(var2, "Exponent", "http://www.w3.org/2000/09/xmldsig#", var3);
         this.modulus.marshal(var6, var3, var4);
         this.exponent.marshal(var7, var3, var4);
         var5.appendChild(var6);
         var5.appendChild(var7);
         var1.appendChild(var5);
      }

      PublicKey unmarshalKeyValue(Element var1) throws MarshalException {
         if (this.rsakf == null) {
            try {
               this.rsakf = KeyFactory.getInstance("RSA");
            } catch (NoSuchAlgorithmException var5) {
               throw new RuntimeException("unable to create RSA KeyFactory: " + var5.getMessage());
            }
         }

         Element var2 = DOMUtils.getFirstChildElement(var1, "Modulus");
         this.modulus = new DOMCryptoBinary(var2.getFirstChild());
         Element var3 = DOMUtils.getNextSiblingElement(var2, "Exponent");
         this.exponent = new DOMCryptoBinary(var3.getFirstChild());
         RSAPublicKeySpec var4 = new RSAPublicKeySpec(this.modulus.getBigNum(), this.exponent.getBigNum());
         return DOMKeyValue.generatePublicKey(this.rsakf, var4);
      }
   }
}
