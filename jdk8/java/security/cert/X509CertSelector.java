package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificatePoliciesExtension;
import sun.security.x509.CertificatePolicyId;
import sun.security.x509.CertificatePolicySet;
import sun.security.x509.DNSName;
import sun.security.x509.EDIPartyName;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralSubtree;
import sun.security.x509.GeneralSubtrees;
import sun.security.x509.IPAddressName;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.OIDName;
import sun.security.x509.OtherName;
import sun.security.x509.PolicyInformation;
import sun.security.x509.PrivateKeyUsageExtension;
import sun.security.x509.RFC822Name;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.URIName;
import sun.security.x509.X400Address;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509Key;

public class X509CertSelector implements CertSelector {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final ObjectIdentifier ANY_EXTENDED_KEY_USAGE = ObjectIdentifier.newInternal(new int[]{2, 5, 29, 37, 0});
   private BigInteger serialNumber;
   private X500Principal issuer;
   private X500Principal subject;
   private byte[] subjectKeyID;
   private byte[] authorityKeyID;
   private Date certificateValid;
   private Date privateKeyValid;
   private ObjectIdentifier subjectPublicKeyAlgID;
   private PublicKey subjectPublicKey;
   private byte[] subjectPublicKeyBytes;
   private boolean[] keyUsage;
   private Set<String> keyPurposeSet;
   private Set<ObjectIdentifier> keyPurposeOIDSet;
   private Set<List<?>> subjectAlternativeNames;
   private Set<GeneralNameInterface> subjectAlternativeGeneralNames;
   private CertificatePolicySet policy;
   private Set<String> policySet;
   private Set<List<?>> pathToNames;
   private Set<GeneralNameInterface> pathToGeneralNames;
   private NameConstraintsExtension nc;
   private byte[] ncBytes;
   private int basicConstraints = -1;
   private X509Certificate x509Cert;
   private boolean matchAllSubjectAltNames = true;
   private static final Boolean FALSE;
   private static final int PRIVATE_KEY_USAGE_ID = 0;
   private static final int SUBJECT_ALT_NAME_ID = 1;
   private static final int NAME_CONSTRAINTS_ID = 2;
   private static final int CERT_POLICIES_ID = 3;
   private static final int EXTENDED_KEY_USAGE_ID = 4;
   private static final int NUM_OF_EXTENSIONS = 5;
   private static final String[] EXTENSION_OIDS;
   static final int NAME_ANY = 0;
   static final int NAME_RFC822 = 1;
   static final int NAME_DNS = 2;
   static final int NAME_X400 = 3;
   static final int NAME_DIRECTORY = 4;
   static final int NAME_EDI = 5;
   static final int NAME_URI = 6;
   static final int NAME_IP = 7;
   static final int NAME_OID = 8;

   public void setCertificate(X509Certificate var1) {
      this.x509Cert = var1;
   }

   public void setSerialNumber(BigInteger var1) {
      this.serialNumber = var1;
   }

   public void setIssuer(X500Principal var1) {
      this.issuer = var1;
   }

   public void setIssuer(String var1) throws IOException {
      if (var1 == null) {
         this.issuer = null;
      } else {
         this.issuer = (new X500Name(var1)).asX500Principal();
      }

   }

   public void setIssuer(byte[] var1) throws IOException {
      try {
         this.issuer = var1 == null ? null : new X500Principal(var1);
      } catch (IllegalArgumentException var3) {
         throw new IOException("Invalid name", var3);
      }
   }

   public void setSubject(X500Principal var1) {
      this.subject = var1;
   }

   public void setSubject(String var1) throws IOException {
      if (var1 == null) {
         this.subject = null;
      } else {
         this.subject = (new X500Name(var1)).asX500Principal();
      }

   }

   public void setSubject(byte[] var1) throws IOException {
      try {
         this.subject = var1 == null ? null : new X500Principal(var1);
      } catch (IllegalArgumentException var3) {
         throw new IOException("Invalid name", var3);
      }
   }

   public void setSubjectKeyIdentifier(byte[] var1) {
      if (var1 == null) {
         this.subjectKeyID = null;
      } else {
         this.subjectKeyID = (byte[])var1.clone();
      }

   }

   public void setAuthorityKeyIdentifier(byte[] var1) {
      if (var1 == null) {
         this.authorityKeyID = null;
      } else {
         this.authorityKeyID = (byte[])var1.clone();
      }

   }

   public void setCertificateValid(Date var1) {
      if (var1 == null) {
         this.certificateValid = null;
      } else {
         this.certificateValid = (Date)var1.clone();
      }

   }

   public void setPrivateKeyValid(Date var1) {
      if (var1 == null) {
         this.privateKeyValid = null;
      } else {
         this.privateKeyValid = (Date)var1.clone();
      }

   }

   public void setSubjectPublicKeyAlgID(String var1) throws IOException {
      if (var1 == null) {
         this.subjectPublicKeyAlgID = null;
      } else {
         this.subjectPublicKeyAlgID = new ObjectIdentifier(var1);
      }

   }

   public void setSubjectPublicKey(PublicKey var1) {
      if (var1 == null) {
         this.subjectPublicKey = null;
         this.subjectPublicKeyBytes = null;
      } else {
         this.subjectPublicKey = var1;
         this.subjectPublicKeyBytes = var1.getEncoded();
      }

   }

   public void setSubjectPublicKey(byte[] var1) throws IOException {
      if (var1 == null) {
         this.subjectPublicKey = null;
         this.subjectPublicKeyBytes = null;
      } else {
         this.subjectPublicKeyBytes = (byte[])var1.clone();
         this.subjectPublicKey = X509Key.parse(new DerValue(this.subjectPublicKeyBytes));
      }

   }

   public void setKeyUsage(boolean[] var1) {
      if (var1 == null) {
         this.keyUsage = null;
      } else {
         this.keyUsage = (boolean[])var1.clone();
      }

   }

   public void setExtendedKeyUsage(Set<String> var1) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         this.keyPurposeSet = Collections.unmodifiableSet(new HashSet(var1));
         this.keyPurposeOIDSet = new HashSet();
         Iterator var2 = this.keyPurposeSet.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            this.keyPurposeOIDSet.add(new ObjectIdentifier(var3));
         }
      } else {
         this.keyPurposeSet = null;
         this.keyPurposeOIDSet = null;
      }

   }

   public void setMatchAllSubjectAltNames(boolean var1) {
      this.matchAllSubjectAltNames = var1;
   }

   public void setSubjectAlternativeNames(Collection<List<?>> var1) throws IOException {
      if (var1 == null) {
         this.subjectAlternativeNames = null;
         this.subjectAlternativeGeneralNames = null;
      } else {
         if (var1.isEmpty()) {
            this.subjectAlternativeNames = null;
            this.subjectAlternativeGeneralNames = null;
            return;
         }

         Set var2 = cloneAndCheckNames(var1);
         this.subjectAlternativeGeneralNames = parseNames(var2);
         this.subjectAlternativeNames = var2;
      }

   }

   public void addSubjectAlternativeName(int var1, String var2) throws IOException {
      this.addSubjectAlternativeNameInternal(var1, var2);
   }

   public void addSubjectAlternativeName(int var1, byte[] var2) throws IOException {
      this.addSubjectAlternativeNameInternal(var1, var2.clone());
   }

   private void addSubjectAlternativeNameInternal(int var1, Object var2) throws IOException {
      GeneralNameInterface var3 = makeGeneralNameInterface(var1, var2);
      if (this.subjectAlternativeNames == null) {
         this.subjectAlternativeNames = new HashSet();
      }

      if (this.subjectAlternativeGeneralNames == null) {
         this.subjectAlternativeGeneralNames = new HashSet();
      }

      ArrayList var4 = new ArrayList(2);
      var4.add(var1);
      var4.add(var2);
      this.subjectAlternativeNames.add(var4);
      this.subjectAlternativeGeneralNames.add(var3);
   }

   private static Set<GeneralNameInterface> parseNames(Collection<List<?>> var0) throws IOException {
      HashSet var1 = new HashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         List var3 = (List)var2.next();
         if (var3.size() != 2) {
            throw new IOException("name list size not 2");
         }

         Object var4 = var3.get(0);
         if (!(var4 instanceof Integer)) {
            throw new IOException("expected an Integer");
         }

         int var5 = (Integer)var4;
         var4 = var3.get(1);
         var1.add(makeGeneralNameInterface(var5, var4));
      }

      return var1;
   }

   static boolean equalNames(Collection<?> var0, Collection<?> var1) {
      if (var0 != null && var1 != null) {
         return var0.equals(var1);
      } else {
         return var0 == var1;
      }
   }

   static GeneralNameInterface makeGeneralNameInterface(int var0, Object var1) throws IOException {
      if (debug != null) {
         debug.println("X509CertSelector.makeGeneralNameInterface(" + var0 + ")...");
      }

      Object var2;
      if (var1 instanceof String) {
         if (debug != null) {
            debug.println("X509CertSelector.makeGeneralNameInterface() name is String: " + var1);
         }

         switch(var0) {
         case 1:
            var2 = new RFC822Name((String)var1);
            break;
         case 2:
            var2 = new DNSName((String)var1);
            break;
         case 3:
         case 5:
         default:
            throw new IOException("unable to parse String names of type " + var0);
         case 4:
            var2 = new X500Name((String)var1);
            break;
         case 6:
            var2 = new URIName((String)var1);
            break;
         case 7:
            var2 = new IPAddressName((String)var1);
            break;
         case 8:
            var2 = new OIDName((String)var1);
         }

         if (debug != null) {
            debug.println("X509CertSelector.makeGeneralNameInterface() result: " + var2.toString());
         }
      } else {
         if (!(var1 instanceof byte[])) {
            if (debug != null) {
               debug.println("X509CertSelector.makeGeneralName() input name not String or byte array");
            }

            throw new IOException("name not String or byte array");
         }

         DerValue var3 = new DerValue((byte[])((byte[])var1));
         if (debug != null) {
            debug.println("X509CertSelector.makeGeneralNameInterface() is byte[]");
         }

         switch(var0) {
         case 0:
            var2 = new OtherName(var3);
            break;
         case 1:
            var2 = new RFC822Name(var3);
            break;
         case 2:
            var2 = new DNSName(var3);
            break;
         case 3:
            var2 = new X400Address(var3);
            break;
         case 4:
            var2 = new X500Name(var3);
            break;
         case 5:
            var2 = new EDIPartyName(var3);
            break;
         case 6:
            var2 = new URIName(var3);
            break;
         case 7:
            var2 = new IPAddressName(var3);
            break;
         case 8:
            var2 = new OIDName(var3);
            break;
         default:
            throw new IOException("unable to parse byte array names of type " + var0);
         }

         if (debug != null) {
            debug.println("X509CertSelector.makeGeneralNameInterface() result: " + var2.toString());
         }
      }

      return (GeneralNameInterface)var2;
   }

   public void setNameConstraints(byte[] var1) throws IOException {
      if (var1 == null) {
         this.ncBytes = null;
         this.nc = null;
      } else {
         this.ncBytes = (byte[])var1.clone();
         this.nc = new NameConstraintsExtension(FALSE, var1);
      }

   }

   public void setBasicConstraints(int var1) {
      if (var1 < -2) {
         throw new IllegalArgumentException("basic constraints less than -2");
      } else {
         this.basicConstraints = var1;
      }
   }

   public void setPolicy(Set<String> var1) throws IOException {
      if (var1 == null) {
         this.policySet = null;
         this.policy = null;
      } else {
         Set var2 = Collections.unmodifiableSet(new HashSet(var1));
         Iterator var3 = var2.iterator();
         Vector var4 = new Vector();

         while(var3.hasNext()) {
            Object var5 = var3.next();
            if (!(var5 instanceof String)) {
               throw new IOException("non String in certPolicySet");
            }

            var4.add(new CertificatePolicyId(new ObjectIdentifier((String)var5)));
         }

         this.policySet = var2;
         this.policy = new CertificatePolicySet(var4);
      }

   }

   public void setPathToNames(Collection<List<?>> var1) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         Set var2 = cloneAndCheckNames(var1);
         this.pathToGeneralNames = parseNames(var2);
         this.pathToNames = var2;
      } else {
         this.pathToNames = null;
         this.pathToGeneralNames = null;
      }

   }

   void setPathToNamesInternal(Set<GeneralNameInterface> var1) {
      this.pathToNames = Collections.emptySet();
      this.pathToGeneralNames = var1;
   }

   public void addPathToName(int var1, String var2) throws IOException {
      this.addPathToNameInternal(var1, var2);
   }

   public void addPathToName(int var1, byte[] var2) throws IOException {
      this.addPathToNameInternal(var1, var2.clone());
   }

   private void addPathToNameInternal(int var1, Object var2) throws IOException {
      GeneralNameInterface var3 = makeGeneralNameInterface(var1, var2);
      if (this.pathToGeneralNames == null) {
         this.pathToNames = new HashSet();
         this.pathToGeneralNames = new HashSet();
      }

      ArrayList var4 = new ArrayList(2);
      var4.add(var1);
      var4.add(var2);
      this.pathToNames.add(var4);
      this.pathToGeneralNames.add(var3);
   }

   public X509Certificate getCertificate() {
      return this.x509Cert;
   }

   public BigInteger getSerialNumber() {
      return this.serialNumber;
   }

   public X500Principal getIssuer() {
      return this.issuer;
   }

   public String getIssuerAsString() {
      return this.issuer == null ? null : this.issuer.getName();
   }

   public byte[] getIssuerAsBytes() throws IOException {
      return this.issuer == null ? null : this.issuer.getEncoded();
   }

   public X500Principal getSubject() {
      return this.subject;
   }

   public String getSubjectAsString() {
      return this.subject == null ? null : this.subject.getName();
   }

   public byte[] getSubjectAsBytes() throws IOException {
      return this.subject == null ? null : this.subject.getEncoded();
   }

   public byte[] getSubjectKeyIdentifier() {
      return this.subjectKeyID == null ? null : (byte[])this.subjectKeyID.clone();
   }

   public byte[] getAuthorityKeyIdentifier() {
      return this.authorityKeyID == null ? null : (byte[])this.authorityKeyID.clone();
   }

   public Date getCertificateValid() {
      return this.certificateValid == null ? null : (Date)this.certificateValid.clone();
   }

   public Date getPrivateKeyValid() {
      return this.privateKeyValid == null ? null : (Date)this.privateKeyValid.clone();
   }

   public String getSubjectPublicKeyAlgID() {
      return this.subjectPublicKeyAlgID == null ? null : this.subjectPublicKeyAlgID.toString();
   }

   public PublicKey getSubjectPublicKey() {
      return this.subjectPublicKey;
   }

   public boolean[] getKeyUsage() {
      return this.keyUsage == null ? null : (boolean[])this.keyUsage.clone();
   }

   public Set<String> getExtendedKeyUsage() {
      return this.keyPurposeSet;
   }

   public boolean getMatchAllSubjectAltNames() {
      return this.matchAllSubjectAltNames;
   }

   public Collection<List<?>> getSubjectAlternativeNames() {
      return this.subjectAlternativeNames == null ? null : cloneNames(this.subjectAlternativeNames);
   }

   private static Set<List<?>> cloneNames(Collection<List<?>> var0) {
      try {
         return cloneAndCheckNames(var0);
      } catch (IOException var2) {
         throw new RuntimeException("cloneNames encountered IOException: " + var2.getMessage());
      }
   }

   private static Set<List<?>> cloneAndCheckNames(Collection<List<?>> var0) throws IOException {
      HashSet var1 = new HashSet();
      Iterator var2 = var0.iterator();

      List var3;
      while(var2.hasNext()) {
         var3 = (List)var2.next();
         var1.add(new ArrayList(var3));
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         var3 = (List)var2.next();
         if (var3.size() != 2) {
            throw new IOException("name list size not 2");
         }

         Object var5 = var3.get(0);
         if (!(var5 instanceof Integer)) {
            throw new IOException("expected an Integer");
         }

         int var6 = (Integer)var5;
         if (var6 < 0 || var6 > 8) {
            throw new IOException("name type not 0-8");
         }

         Object var7 = var3.get(1);
         if (!(var7 instanceof byte[]) && !(var7 instanceof String)) {
            if (debug != null) {
               debug.println("X509CertSelector.cloneAndCheckNames() name not byte array");
            }

            throw new IOException("name not byte array or String");
         }

         if (var7 instanceof byte[]) {
            var3.set(1, ((byte[])((byte[])var7)).clone());
         }
      }

      return var1;
   }

   public byte[] getNameConstraints() {
      return this.ncBytes == null ? null : (byte[])this.ncBytes.clone();
   }

   public int getBasicConstraints() {
      return this.basicConstraints;
   }

   public Set<String> getPolicy() {
      return this.policySet;
   }

   public Collection<List<?>> getPathToNames() {
      return this.pathToNames == null ? null : cloneNames(this.pathToNames);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("X509CertSelector: [\n");
      if (this.x509Cert != null) {
         var1.append("  Certificate: " + this.x509Cert.toString() + "\n");
      }

      if (this.serialNumber != null) {
         var1.append("  Serial Number: " + this.serialNumber.toString() + "\n");
      }

      if (this.issuer != null) {
         var1.append("  Issuer: " + this.getIssuerAsString() + "\n");
      }

      if (this.subject != null) {
         var1.append("  Subject: " + this.getSubjectAsString() + "\n");
      }

      var1.append("  matchAllSubjectAltNames flag: " + String.valueOf(this.matchAllSubjectAltNames) + "\n");
      Iterator var2;
      if (this.subjectAlternativeNames != null) {
         var1.append("  SubjectAlternativeNames:\n");
         var2 = this.subjectAlternativeNames.iterator();

         while(var2.hasNext()) {
            List var3 = (List)var2.next();
            var1.append("    type " + var3.get(0) + ", name " + var3.get(1) + "\n");
         }
      }

      HexDumpEncoder var4;
      if (this.subjectKeyID != null) {
         var4 = new HexDumpEncoder();
         var1.append("  Subject Key Identifier: " + var4.encodeBuffer(this.subjectKeyID) + "\n");
      }

      if (this.authorityKeyID != null) {
         var4 = new HexDumpEncoder();
         var1.append("  Authority Key Identifier: " + var4.encodeBuffer(this.authorityKeyID) + "\n");
      }

      if (this.certificateValid != null) {
         var1.append("  Certificate Valid: " + this.certificateValid.toString() + "\n");
      }

      if (this.privateKeyValid != null) {
         var1.append("  Private Key Valid: " + this.privateKeyValid.toString() + "\n");
      }

      if (this.subjectPublicKeyAlgID != null) {
         var1.append("  Subject Public Key AlgID: " + this.subjectPublicKeyAlgID.toString() + "\n");
      }

      if (this.subjectPublicKey != null) {
         var1.append("  Subject Public Key: " + this.subjectPublicKey.toString() + "\n");
      }

      if (this.keyUsage != null) {
         var1.append("  Key Usage: " + keyUsageToString(this.keyUsage) + "\n");
      }

      if (this.keyPurposeSet != null) {
         var1.append("  Extended Key Usage: " + this.keyPurposeSet.toString() + "\n");
      }

      if (this.policy != null) {
         var1.append("  Policy: " + this.policy.toString() + "\n");
      }

      if (this.pathToGeneralNames != null) {
         var1.append("  Path to names:\n");
         var2 = this.pathToGeneralNames.iterator();

         while(var2.hasNext()) {
            var1.append("    " + var2.next() + "\n");
         }
      }

      var1.append("]");
      return var1.toString();
   }

   private static String keyUsageToString(boolean[] var0) {
      String var1 = "KeyUsage [\n";

      try {
         if (var0[0]) {
            var1 = var1 + "  DigitalSignature\n";
         }

         if (var0[1]) {
            var1 = var1 + "  Non_repudiation\n";
         }

         if (var0[2]) {
            var1 = var1 + "  Key_Encipherment\n";
         }

         if (var0[3]) {
            var1 = var1 + "  Data_Encipherment\n";
         }

         if (var0[4]) {
            var1 = var1 + "  Key_Agreement\n";
         }

         if (var0[5]) {
            var1 = var1 + "  Key_CertSign\n";
         }

         if (var0[6]) {
            var1 = var1 + "  Crl_Sign\n";
         }

         if (var0[7]) {
            var1 = var1 + "  Encipher_Only\n";
         }

         if (var0[8]) {
            var1 = var1 + "  Decipher_Only\n";
         }
      } catch (ArrayIndexOutOfBoundsException var3) {
      }

      var1 = var1 + "]\n";
      return var1;
   }

   private static Extension getExtensionObject(X509Certificate var0, int var1) throws IOException {
      if (var0 instanceof X509CertImpl) {
         X509CertImpl var7 = (X509CertImpl)var0;
         switch(var1) {
         case 0:
            return var7.getPrivateKeyUsageExtension();
         case 1:
            return var7.getSubjectAlternativeNameExtension();
         case 2:
            return var7.getNameConstraintsExtension();
         case 3:
            return var7.getCertificatePoliciesExtension();
         case 4:
            return var7.getExtendedKeyUsageExtension();
         default:
            return null;
         }
      } else {
         byte[] var2 = var0.getExtensionValue(EXTENSION_OIDS[var1]);
         if (var2 == null) {
            return null;
         } else {
            DerInputStream var3 = new DerInputStream(var2);
            byte[] var4 = var3.getOctetString();
            switch(var1) {
            case 0:
               try {
                  return new PrivateKeyUsageExtension(FALSE, var4);
               } catch (CertificateException var6) {
                  throw new IOException(var6.getMessage());
               }
            case 1:
               return new SubjectAlternativeNameExtension(FALSE, var4);
            case 2:
               return new NameConstraintsExtension(FALSE, var4);
            case 3:
               return new CertificatePoliciesExtension(FALSE, var4);
            case 4:
               return new ExtendedKeyUsageExtension(FALSE, var4);
            default:
               return null;
            }
         }
      }
   }

   public boolean match(Certificate var1) {
      if (!(var1 instanceof X509Certificate)) {
         return false;
      } else {
         X509Certificate var2 = (X509Certificate)var1;
         if (debug != null) {
            debug.println("X509CertSelector.match(SN: " + var2.getSerialNumber().toString(16) + "\n  Issuer: " + var2.getIssuerDN() + "\n  Subject: " + var2.getSubjectDN() + ")");
         }

         if (this.x509Cert != null && !this.x509Cert.equals(var2)) {
            if (debug != null) {
               debug.println("X509CertSelector.match: certs don't match");
            }

            return false;
         } else if (this.serialNumber != null && !this.serialNumber.equals(var2.getSerialNumber())) {
            if (debug != null) {
               debug.println("X509CertSelector.match: serial numbers don't match");
            }

            return false;
         } else if (this.issuer != null && !this.issuer.equals(var2.getIssuerX500Principal())) {
            if (debug != null) {
               debug.println("X509CertSelector.match: issuer DNs don't match");
            }

            return false;
         } else if (this.subject != null && !this.subject.equals(var2.getSubjectX500Principal())) {
            if (debug != null) {
               debug.println("X509CertSelector.match: subject DNs don't match");
            }

            return false;
         } else {
            if (this.certificateValid != null) {
               try {
                  var2.checkValidity(this.certificateValid);
               } catch (CertificateException var4) {
                  if (debug != null) {
                     debug.println("X509CertSelector.match: certificate not within validity period");
                  }

                  return false;
               }
            }

            if (this.subjectPublicKeyBytes != null) {
               byte[] var3 = var2.getPublicKey().getEncoded();
               if (!Arrays.equals(this.subjectPublicKeyBytes, var3)) {
                  if (debug != null) {
                     debug.println("X509CertSelector.match: subject public keys don't match");
                  }

                  return false;
               }
            }

            boolean var5 = this.matchBasicConstraints(var2) && this.matchKeyUsage(var2) && this.matchExtendedKeyUsage(var2) && this.matchSubjectKeyID(var2) && this.matchAuthorityKeyID(var2) && this.matchPrivateKeyValid(var2) && this.matchSubjectPublicKeyAlgID(var2) && this.matchPolicy(var2) && this.matchSubjectAlternativeNames(var2) && this.matchPathToNames(var2) && this.matchNameConstraints(var2);
            if (var5 && debug != null) {
               debug.println("X509CertSelector.match returning: true");
            }

            return var5;
         }
      }
   }

   private boolean matchSubjectKeyID(X509Certificate var1) {
      if (this.subjectKeyID == null) {
         return true;
      } else {
         try {
            byte[] var2 = var1.getExtensionValue("2.5.29.14");
            if (var2 == null) {
               if (debug != null) {
                  debug.println("X509CertSelector.match: no subject key ID extension");
               }

               return false;
            } else {
               DerInputStream var3 = new DerInputStream(var2);
               byte[] var4 = var3.getOctetString();
               if (var4 != null && Arrays.equals(this.subjectKeyID, var4)) {
                  return true;
               } else {
                  if (debug != null) {
                     debug.println("X509CertSelector.match: subject key IDs don't match");
                  }

                  return false;
               }
            }
         } catch (IOException var5) {
            if (debug != null) {
               debug.println("X509CertSelector.match: exception in subject key ID check");
            }

            return false;
         }
      }
   }

   private boolean matchAuthorityKeyID(X509Certificate var1) {
      if (this.authorityKeyID == null) {
         return true;
      } else {
         try {
            byte[] var2 = var1.getExtensionValue("2.5.29.35");
            if (var2 == null) {
               if (debug != null) {
                  debug.println("X509CertSelector.match: no authority key ID extension");
               }

               return false;
            } else {
               DerInputStream var3 = new DerInputStream(var2);
               byte[] var4 = var3.getOctetString();
               if (var4 != null && Arrays.equals(this.authorityKeyID, var4)) {
                  return true;
               } else {
                  if (debug != null) {
                     debug.println("X509CertSelector.match: authority key IDs don't match");
                  }

                  return false;
               }
            }
         } catch (IOException var5) {
            if (debug != null) {
               debug.println("X509CertSelector.match: exception in authority key ID check");
            }

            return false;
         }
      }
   }

   private boolean matchPrivateKeyValid(X509Certificate var1) {
      if (this.privateKeyValid == null) {
         return true;
      } else {
         PrivateKeyUsageExtension var2 = null;

         String var4;
         Date var5;
         try {
            var2 = (PrivateKeyUsageExtension)getExtensionObject(var1, 0);
            if (var2 != null) {
               var2.valid(this.privateKeyValid);
            }

            return true;
         } catch (CertificateExpiredException var8) {
            if (debug != null) {
               var4 = "n/a";

               try {
                  var5 = var2.get("not_after");
                  var4 = var5.toString();
               } catch (CertificateException var6) {
               }

               debug.println("X509CertSelector.match: private key usage not within validity date; ext.NOT_After: " + var4 + "; X509CertSelector: " + this.toString());
               var8.printStackTrace();
            }

            return false;
         } catch (CertificateNotYetValidException var9) {
            if (debug != null) {
               var4 = "n/a";

               try {
                  var5 = var2.get("not_before");
                  var4 = var5.toString();
               } catch (CertificateException var7) {
               }

               debug.println("X509CertSelector.match: private key usage not within validity date; ext.NOT_BEFORE: " + var4 + "; X509CertSelector: " + this.toString());
               var9.printStackTrace();
            }

            return false;
         } catch (IOException var10) {
            if (debug != null) {
               debug.println("X509CertSelector.match: IOException in private key usage check; X509CertSelector: " + this.toString());
               var10.printStackTrace();
            }

            return false;
         }
      }
   }

   private boolean matchSubjectPublicKeyAlgID(X509Certificate var1) {
      if (this.subjectPublicKeyAlgID == null) {
         return true;
      } else {
         try {
            byte[] var2 = var1.getPublicKey().getEncoded();
            DerValue var3 = new DerValue(var2);
            if (var3.tag != 48) {
               throw new IOException("invalid key format");
            } else {
               AlgorithmId var4 = AlgorithmId.parse(var3.data.getDerValue());
               if (debug != null) {
                  debug.println("X509CertSelector.match: subjectPublicKeyAlgID = " + this.subjectPublicKeyAlgID + ", xcert subjectPublicKeyAlgID = " + var4.getOID());
               }

               if (!this.subjectPublicKeyAlgID.equals((Object)var4.getOID())) {
                  if (debug != null) {
                     debug.println("X509CertSelector.match: subject public key alg IDs don't match");
                  }

                  return false;
               } else {
                  return true;
               }
            }
         } catch (IOException var5) {
            if (debug != null) {
               debug.println("X509CertSelector.match: IOException in subject public key algorithm OID check");
            }

            return false;
         }
      }
   }

   private boolean matchKeyUsage(X509Certificate var1) {
      if (this.keyUsage == null) {
         return true;
      } else {
         boolean[] var2 = var1.getKeyUsage();
         if (var2 != null) {
            for(int var3 = 0; var3 < this.keyUsage.length; ++var3) {
               if (this.keyUsage[var3] && (var3 >= var2.length || !var2[var3])) {
                  if (debug != null) {
                     debug.println("X509CertSelector.match: key usage bits don't match");
                  }

                  return false;
               }
            }
         }

         return true;
      }
   }

   private boolean matchExtendedKeyUsage(X509Certificate var1) {
      if (this.keyPurposeSet != null && !this.keyPurposeSet.isEmpty()) {
         try {
            ExtendedKeyUsageExtension var2 = (ExtendedKeyUsageExtension)getExtensionObject(var1, 4);
            if (var2 != null) {
               Vector var3 = var2.get("usages");
               if (!var3.contains(ANY_EXTENDED_KEY_USAGE) && !var3.containsAll(this.keyPurposeOIDSet)) {
                  if (debug != null) {
                     debug.println("X509CertSelector.match: cert failed extendedKeyUsage criterion");
                  }

                  return false;
               }
            }

            return true;
         } catch (IOException var4) {
            if (debug != null) {
               debug.println("X509CertSelector.match: IOException in extended key usage check");
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private boolean matchSubjectAlternativeNames(X509Certificate var1) {
      if (this.subjectAlternativeNames != null && !this.subjectAlternativeNames.isEmpty()) {
         try {
            SubjectAlternativeNameExtension var2 = (SubjectAlternativeNameExtension)getExtensionObject(var1, 1);
            if (var2 == null) {
               if (debug != null) {
                  debug.println("X509CertSelector.match: no subject alternative name extension");
               }

               return false;
            } else {
               GeneralNames var3 = var2.get("subject_name");
               Iterator var4 = this.subjectAlternativeGeneralNames.iterator();

               while(var4.hasNext()) {
                  GeneralNameInterface var5 = (GeneralNameInterface)var4.next();
                  boolean var6 = false;

                  GeneralNameInterface var8;
                  for(Iterator var7 = var3.iterator(); var7.hasNext() && !var6; var6 = var8.equals(var5)) {
                     var8 = ((GeneralName)var7.next()).getName();
                  }

                  if (!var6 && (this.matchAllSubjectAltNames || !var4.hasNext())) {
                     if (debug != null) {
                        debug.println("X509CertSelector.match: subject alternative name " + var5 + " not found");
                     }

                     return false;
                  }

                  if (var6 && !this.matchAllSubjectAltNames) {
                     break;
                  }
               }

               return true;
            }
         } catch (IOException var9) {
            if (debug != null) {
               debug.println("X509CertSelector.match: IOException in subject alternative name check");
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private boolean matchNameConstraints(X509Certificate var1) {
      if (this.nc == null) {
         return true;
      } else {
         try {
            if (!this.nc.verify(var1)) {
               if (debug != null) {
                  debug.println("X509CertSelector.match: name constraints not satisfied");
               }

               return false;
            } else {
               return true;
            }
         } catch (IOException var3) {
            if (debug != null) {
               debug.println("X509CertSelector.match: IOException in name constraints check");
            }

            return false;
         }
      }
   }

   private boolean matchPolicy(X509Certificate var1) {
      if (this.policy == null) {
         return true;
      } else {
         try {
            CertificatePoliciesExtension var2 = (CertificatePoliciesExtension)getExtensionObject(var1, 3);
            if (var2 == null) {
               if (debug != null) {
                  debug.println("X509CertSelector.match: no certificate policy extension");
               }

               return false;
            } else {
               List var3 = var2.get("policies");
               ArrayList var4 = new ArrayList(var3.size());
               Iterator var5 = var3.iterator();

               while(var5.hasNext()) {
                  PolicyInformation var6 = (PolicyInformation)var5.next();
                  var4.add(var6.getPolicyIdentifier());
               }

               if (this.policy != null) {
                  boolean var9 = false;
                  if (this.policy.getCertPolicyIds().isEmpty()) {
                     if (var4.isEmpty()) {
                        if (debug != null) {
                           debug.println("X509CertSelector.match: cert failed policyAny criterion");
                        }

                        return false;
                     }
                  } else {
                     Iterator var10 = this.policy.getCertPolicyIds().iterator();

                     while(var10.hasNext()) {
                        CertificatePolicyId var7 = (CertificatePolicyId)var10.next();
                        if (var4.contains(var7)) {
                           var9 = true;
                           break;
                        }
                     }

                     if (!var9) {
                        if (debug != null) {
                           debug.println("X509CertSelector.match: cert failed policyAny criterion");
                        }

                        return false;
                     }
                  }
               }

               return true;
            }
         } catch (IOException var8) {
            if (debug != null) {
               debug.println("X509CertSelector.match: IOException in certificate policy ID check");
            }

            return false;
         }
      }
   }

   private boolean matchPathToNames(X509Certificate var1) {
      if (this.pathToGeneralNames == null) {
         return true;
      } else {
         try {
            NameConstraintsExtension var2 = (NameConstraintsExtension)getExtensionObject(var1, 2);
            if (var2 == null) {
               return true;
            } else {
               if (debug != null && Debug.isOn("certpath")) {
                  debug.println("X509CertSelector.match pathToNames:\n");
                  Iterator var3 = this.pathToGeneralNames.iterator();

                  while(var3.hasNext()) {
                     debug.println("    " + var3.next() + "\n");
                  }
               }

               GeneralSubtrees var6 = var2.get("permitted_subtrees");
               GeneralSubtrees var4 = var2.get("excluded_subtrees");
               if (var4 != null && !this.matchExcluded(var4)) {
                  return false;
               } else {
                  return var6 == null || this.matchPermitted(var6);
               }
            }
         } catch (IOException var5) {
            if (debug != null) {
               debug.println("X509CertSelector.match: IOException in name constraints check");
            }

            return false;
         }
      }
   }

   private boolean matchExcluded(GeneralSubtrees var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         GeneralSubtree var3 = (GeneralSubtree)var2.next();
         GeneralNameInterface var4 = var3.getName().getName();
         Iterator var5 = this.pathToGeneralNames.iterator();

         while(var5.hasNext()) {
            GeneralNameInterface var6 = (GeneralNameInterface)var5.next();
            if (var4.getType() == var6.getType()) {
               switch(var6.constrains(var4)) {
               case 0:
               case 2:
                  if (debug != null) {
                     debug.println("X509CertSelector.match: name constraints inhibit path to specified name");
                     debug.println("X509CertSelector.match: excluded name: " + var6);
                  }

                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean matchPermitted(GeneralSubtrees var1) {
      Iterator var2 = this.pathToGeneralNames.iterator();

      GeneralNameInterface var3;
      boolean var5;
      boolean var6;
      String var7;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (GeneralNameInterface)var2.next();
         Iterator var4 = var1.iterator();
         var5 = false;
         var6 = false;
         var7 = "";

         while(var4.hasNext() && !var5) {
            GeneralSubtree var8 = (GeneralSubtree)var4.next();
            GeneralNameInterface var9 = var8.getName().getName();
            if (var9.getType() == var3.getType()) {
               var6 = true;
               var7 = var7 + "  " + var9;
               switch(var3.constrains(var9)) {
               case 0:
               case 2:
                  var5 = true;
               }
            }
         }
      } while(var5 || !var6);

      if (debug != null) {
         debug.println("X509CertSelector.match: name constraints inhibit path to specified name; permitted names of type " + var3.getType() + ": " + var7);
      }

      return false;
   }

   private boolean matchBasicConstraints(X509Certificate var1) {
      if (this.basicConstraints == -1) {
         return true;
      } else {
         int var2 = var1.getBasicConstraints();
         if (this.basicConstraints == -2) {
            if (var2 != -1) {
               if (debug != null) {
                  debug.println("X509CertSelector.match: not an EE cert");
               }

               return false;
            }
         } else if (var2 < this.basicConstraints) {
            if (debug != null) {
               debug.println("X509CertSelector.match: cert's maxPathLen is less than the min maxPathLen set by basicConstraints. (" + var2 + " < " + this.basicConstraints + ")");
            }

            return false;
         }

         return true;
      }
   }

   private static <T> Set<T> cloneSet(Set<T> var0) {
      if (var0 instanceof HashSet) {
         Object var1 = ((HashSet)var0).clone();
         return (Set)var1;
      } else {
         return new HashSet(var0);
      }
   }

   public Object clone() {
      try {
         X509CertSelector var1 = (X509CertSelector)super.clone();
         if (this.subjectAlternativeNames != null) {
            var1.subjectAlternativeNames = cloneSet(this.subjectAlternativeNames);
            var1.subjectAlternativeGeneralNames = cloneSet(this.subjectAlternativeGeneralNames);
         }

         if (this.pathToGeneralNames != null) {
            var1.pathToNames = cloneSet(this.pathToNames);
            var1.pathToGeneralNames = cloneSet(this.pathToGeneralNames);
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   static {
      CertPathHelperImpl.initialize();
      FALSE = Boolean.FALSE;
      EXTENSION_OIDS = new String[5];
      EXTENSION_OIDS[0] = "2.5.29.16";
      EXTENSION_OIDS[1] = "2.5.29.17";
      EXTENSION_OIDS[2] = "2.5.29.30";
      EXTENSION_OIDS[3] = "2.5.29.32";
      EXTENSION_OIDS[4] = "2.5.29.37";
   }
}
