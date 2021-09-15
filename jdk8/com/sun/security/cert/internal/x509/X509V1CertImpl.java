package com.sun.security.cert.internal.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.X509Certificate;

public class X509V1CertImpl extends X509Certificate implements Serializable {
   static final long serialVersionUID = -2048442350420423405L;
   private java.security.cert.X509Certificate wrappedCert;

   private static synchronized CertificateFactory getFactory() throws CertificateException {
      return CertificateFactory.getInstance("X.509");
   }

   public X509V1CertImpl() {
   }

   public X509V1CertImpl(byte[] var1) throws javax.security.cert.CertificateException {
      try {
         ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
         this.wrappedCert = (java.security.cert.X509Certificate)getFactory().generateCertificate(var2);
      } catch (CertificateException var3) {
         throw new javax.security.cert.CertificateException(var3.getMessage());
      }
   }

   public X509V1CertImpl(InputStream var1) throws javax.security.cert.CertificateException {
      try {
         this.wrappedCert = (java.security.cert.X509Certificate)getFactory().generateCertificate(var1);
      } catch (CertificateException var3) {
         throw new javax.security.cert.CertificateException(var3.getMessage());
      }
   }

   public byte[] getEncoded() throws CertificateEncodingException {
      try {
         return this.wrappedCert.getEncoded();
      } catch (java.security.cert.CertificateEncodingException var2) {
         throw new CertificateEncodingException(var2.getMessage());
      }
   }

   public void verify(PublicKey var1) throws javax.security.cert.CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      try {
         this.wrappedCert.verify(var1);
      } catch (CertificateException var3) {
         throw new javax.security.cert.CertificateException(var3.getMessage());
      }
   }

   public void verify(PublicKey var1, String var2) throws javax.security.cert.CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      try {
         this.wrappedCert.verify(var1, var2);
      } catch (CertificateException var4) {
         throw new javax.security.cert.CertificateException(var4.getMessage());
      }
   }

   public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
      this.checkValidity(new Date());
   }

   public void checkValidity(Date var1) throws CertificateExpiredException, CertificateNotYetValidException {
      try {
         this.wrappedCert.checkValidity(var1);
      } catch (java.security.cert.CertificateNotYetValidException var3) {
         throw new CertificateNotYetValidException(var3.getMessage());
      } catch (java.security.cert.CertificateExpiredException var4) {
         throw new CertificateExpiredException(var4.getMessage());
      }
   }

   public String toString() {
      return this.wrappedCert.toString();
   }

   public PublicKey getPublicKey() {
      PublicKey var1 = this.wrappedCert.getPublicKey();
      return var1;
   }

   public int getVersion() {
      return this.wrappedCert.getVersion() - 1;
   }

   public BigInteger getSerialNumber() {
      return this.wrappedCert.getSerialNumber();
   }

   public Principal getSubjectDN() {
      return this.wrappedCert.getSubjectDN();
   }

   public Principal getIssuerDN() {
      return this.wrappedCert.getIssuerDN();
   }

   public Date getNotBefore() {
      return this.wrappedCert.getNotBefore();
   }

   public Date getNotAfter() {
      return this.wrappedCert.getNotAfter();
   }

   public String getSigAlgName() {
      return this.wrappedCert.getSigAlgName();
   }

   public String getSigAlgOID() {
      return this.wrappedCert.getSigAlgOID();
   }

   public byte[] getSigAlgParams() {
      return this.wrappedCert.getSigAlgParams();
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      try {
         var1.write(this.getEncoded());
      } catch (CertificateEncodingException var3) {
         throw new IOException("getEncoded failed: " + var3.getMessage());
      }
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException {
      try {
         this.wrappedCert = (java.security.cert.X509Certificate)getFactory().generateCertificate(var1);
      } catch (CertificateException var3) {
         throw new IOException("generateCertificate failed: " + var3.getMessage());
      }
   }

   public java.security.cert.X509Certificate getX509Certificate() {
      return this.wrappedCert;
   }
}
