package apple.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.security.auth.x500.X500Principal;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public final class KeychainStore extends KeyStoreSpi {
   private Hashtable deletedEntries = new Hashtable();
   private Hashtable addedEntries = new Hashtable();
   private Hashtable entries = new Hashtable();
   private static final int[] keyBag = new int[]{1, 2, 840, 113549, 1, 12, 10, 1, 2};
   private static final int[] pbeWithSHAAnd3KeyTripleDESCBC = new int[]{1, 2, 840, 113549, 1, 12, 1, 3};
   private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
   private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
   private static final int iterationCount = 1024;
   private static final int SALT_LEN = 20;
   private SecureRandom random;

   private static void permissionCheck() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("useKeychainStore"));
      }

   }

   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      permissionCheck();
      if (var2 == null || var2.length == 0) {
         if (this.random == null) {
            this.random = new SecureRandom();
         }

         var2 = Long.toString(this.random.nextLong()).toCharArray();
      }

      Object var3 = this.entries.get(var1.toLowerCase());
      if (var3 != null && var3 instanceof KeychainStore.KeyEntry) {
         byte[] var4 = this._getEncodedKeyData(((KeychainStore.KeyEntry)var3).keyRef, var2);
         if (var4 == null) {
            return null;
         } else {
            PrivateKey var5 = null;

            try {
               byte[] var6 = this.fetchPrivateKeyFromBag(var4);

               AlgorithmParameters var8;
               ObjectIdentifier var9;
               byte[] var23;
               try {
                  EncryptedPrivateKeyInfo var10 = new EncryptedPrivateKeyInfo(var6);
                  var23 = var10.getEncryptedData();
                  DerValue var25 = new DerValue(var10.getAlgorithm().encode());
                  DerInputStream var12 = var25.toDerInputStream();
                  var9 = var12.getOID();
                  var8 = this.parseAlgParameters(var12);
               } catch (IOException var21) {
                  UnrecoverableKeyException var11 = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + var21);
                  var11.initCause(var21);
                  throw var11;
               }

               SecretKey var24 = this.getPBEKey(var2);
               Cipher var26 = Cipher.getInstance(var9.toString());
               var26.init(2, var24, var8);
               byte[] var27 = var26.doFinal(var23);
               PKCS8EncodedKeySpec var13 = new PKCS8EncodedKeySpec(var27);
               DerValue var14 = new DerValue(var27);
               DerInputStream var15 = var14.toDerInputStream();
               int var16 = var15.getInteger();
               DerValue[] var17 = var15.getSequence(2);
               AlgorithmId var18 = new AlgorithmId(var17[0].getOID());
               String var19 = var18.getName();
               KeyFactory var20 = KeyFactory.getInstance(var19);
               var5 = var20.generatePrivate(var13);
               return var5;
            } catch (Exception var22) {
               UnrecoverableKeyException var7 = new UnrecoverableKeyException("Get Key failed: " + var22.getMessage());
               var7.initCause(var22);
               throw var7;
            }
         }
      } else {
         return null;
      }
   }

   private native byte[] _getEncodedKeyData(long var1, char[] var3);

   public Certificate[] engineGetCertificateChain(String var1) {
      permissionCheck();
      Object var2 = this.entries.get(var1.toLowerCase());
      if (var2 != null && var2 instanceof KeychainStore.KeyEntry) {
         return ((KeychainStore.KeyEntry)var2).chain == null ? null : (Certificate[])((Certificate[])((KeychainStore.KeyEntry)var2).chain.clone());
      } else {
         return null;
      }
   }

   public Certificate engineGetCertificate(String var1) {
      permissionCheck();
      Object var2 = this.entries.get(var1.toLowerCase());
      if (var2 != null) {
         if (var2 instanceof KeychainStore.TrustedCertEntry) {
            return ((KeychainStore.TrustedCertEntry)var2).cert;
         } else {
            return ((KeychainStore.KeyEntry)var2).chain == null ? null : ((KeychainStore.KeyEntry)var2).chain[0];
         }
      } else {
         return null;
      }
   }

   public Date engineGetCreationDate(String var1) {
      permissionCheck();
      Object var2 = this.entries.get(var1.toLowerCase());
      if (var2 != null) {
         return var2 instanceof KeychainStore.TrustedCertEntry ? new Date(((KeychainStore.TrustedCertEntry)var2).date.getTime()) : new Date(((KeychainStore.KeyEntry)var2).date.getTime());
      } else {
         return null;
      }
   }

   public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      permissionCheck();
      synchronized(this.entries) {
         try {
            KeychainStore.KeyEntry var6 = new KeychainStore.KeyEntry();
            var6.date = new Date();
            if (!(var2 instanceof PrivateKey)) {
               throw new KeyStoreException("Key is not a PrivateKey");
            }

            if (!var2.getFormat().equals("PKCS#8") && !var2.getFormat().equals("PKCS8")) {
               throw new KeyStoreException("Private key is not encoded as PKCS#8");
            }

            var6.protectedPrivKey = this.encryptPrivateKey(var2.getEncoded(), var3);
            var6.password = (char[])var3.clone();
            if (var4 != null) {
               if (var4.length > 1 && !this.validateChain(var4)) {
                  throw new KeyStoreException("Certificate chain does not validate");
               }

               var6.chain = (Certificate[])((Certificate[])var4.clone());
               var6.chainRefs = new long[var6.chain.length];
            }

            String var11 = var1.toLowerCase();
            if (this.entries.get(var11) != null) {
               this.deletedEntries.put(var11, this.entries.get(var11));
            }

            this.entries.put(var11, var6);
            this.addedEntries.put(var11, var6);
         } catch (Exception var9) {
            KeyStoreException var7 = new KeyStoreException("Key protection algorithm not found: " + var9);
            var7.initCause(var9);
            throw var7;
         }

      }
   }

   public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      permissionCheck();
      synchronized(this.entries) {
         KeychainStore.KeyEntry var5 = new KeychainStore.KeyEntry();

         try {
            EncryptedPrivateKeyInfo var6 = new EncryptedPrivateKeyInfo(var2);
            var5.protectedPrivKey = var6.getEncoded();
         } catch (IOException var8) {
            throw new KeyStoreException("key is not encoded as EncryptedPrivateKeyInfo");
         }

         var5.date = new Date();
         if (var3 != null && var3.length != 0) {
            var5.chain = (Certificate[])((Certificate[])var3.clone());
            var5.chainRefs = new long[var5.chain.length];
         }

         String var10 = var1.toLowerCase();
         if (this.entries.get(var10) != null) {
            this.deletedEntries.put(var10, this.entries.get(var1));
         }

         this.entries.put(var10, var5);
         this.addedEntries.put(var10, var5);
      }
   }

   public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      permissionCheck();
      synchronized(this.entries) {
         Object var4 = this.entries.get(var1.toLowerCase());
         if (var4 != null && var4 instanceof KeychainStore.KeyEntry) {
            throw new KeyStoreException("Cannot overwrite key entry with certificate");
         } else {
            Collection var5 = this.entries.values();
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               Object var7 = var6.next();
               if (var7 instanceof KeychainStore.TrustedCertEntry) {
                  KeychainStore.TrustedCertEntry var8 = (KeychainStore.TrustedCertEntry)var7;
                  if (var8.cert.equals(var2)) {
                     throw new KeyStoreException("Keychain does not support mulitple copies of same certificate.");
                  }
               }
            }

            KeychainStore.TrustedCertEntry var11 = new KeychainStore.TrustedCertEntry();
            var11.cert = var2;
            var11.date = new Date();
            String var12 = var1.toLowerCase();
            if (this.entries.get(var12) != null) {
               this.deletedEntries.put(var12, this.entries.get(var12));
            }

            this.entries.put(var12, var11);
            this.addedEntries.put(var12, var11);
         }
      }
   }

   public void engineDeleteEntry(String var1) throws KeyStoreException {
      permissionCheck();
      synchronized(this.entries) {
         Object var3 = this.entries.remove(var1.toLowerCase());
         this.deletedEntries.put(var1.toLowerCase(), var3);
      }
   }

   public Enumeration engineAliases() {
      permissionCheck();
      return this.entries.keys();
   }

   public boolean engineContainsAlias(String var1) {
      permissionCheck();
      return this.entries.containsKey(var1.toLowerCase());
   }

   public int engineSize() {
      permissionCheck();
      return this.entries.size();
   }

   public boolean engineIsKeyEntry(String var1) {
      permissionCheck();
      Object var2 = this.entries.get(var1.toLowerCase());
      return var2 != null && var2 instanceof KeychainStore.KeyEntry;
   }

   public boolean engineIsCertificateEntry(String var1) {
      permissionCheck();
      Object var2 = this.entries.get(var1.toLowerCase());
      return var2 != null && var2 instanceof KeychainStore.TrustedCertEntry;
   }

   public String engineGetCertificateAlias(Certificate var1) {
      permissionCheck();
      Enumeration var3 = this.entries.keys();

      Certificate var2;
      String var4;
      label24:
      do {
         Object var5;
         do {
            if (!var3.hasMoreElements()) {
               return null;
            }

            var4 = (String)var3.nextElement();
            var5 = this.entries.get(var4);
            if (var5 instanceof KeychainStore.TrustedCertEntry) {
               var2 = ((KeychainStore.TrustedCertEntry)var5).cert;
               continue label24;
            }
         } while(((KeychainStore.KeyEntry)var5).chain == null);

         var2 = ((KeychainStore.KeyEntry)var5).chain[0];
      } while(!var2.equals(var1));

      return var4;
   }

   public void engineStore(OutputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      permissionCheck();
      Enumeration var3 = this.deletedEntries.keys();

      while(true) {
         String var4;
         Object var5;
         while(var3.hasMoreElements()) {
            var4 = (String)var3.nextElement();
            var5 = this.deletedEntries.get(var4);
            if (var5 instanceof KeychainStore.TrustedCertEntry) {
               if (((KeychainStore.TrustedCertEntry)var5).certRef != 0L) {
                  this._removeItemFromKeychain(((KeychainStore.TrustedCertEntry)var5).certRef);
                  this._releaseKeychainItemRef(((KeychainStore.TrustedCertEntry)var5).certRef);
               }
            } else {
               KeychainStore.KeyEntry var7 = (KeychainStore.KeyEntry)var5;
               if (var7.chain != null) {
                  for(int var8 = 0; var8 < var7.chain.length; ++var8) {
                     if (var7.chainRefs[var8] != 0L) {
                        this._removeItemFromKeychain(var7.chainRefs[var8]);
                        this._releaseKeychainItemRef(var7.chainRefs[var8]);
                     }
                  }

                  if (var7.keyRef != 0L) {
                     this._removeItemFromKeychain(var7.keyRef);
                     this._releaseKeychainItemRef(var7.keyRef);
                  }
               }
            }
         }

         var3 = this.addedEntries.keys();

         while(true) {
            while(var3.hasMoreElements()) {
               var4 = (String)var3.nextElement();
               var5 = this.addedEntries.get(var4);
               if (var5 instanceof KeychainStore.TrustedCertEntry) {
                  KeychainStore.TrustedCertEntry var9 = (KeychainStore.TrustedCertEntry)var5;
                  Certificate var11 = var9.cert;
                  var9.certRef = this.addCertificateToKeychain(var4, var11);
               } else {
                  KeychainStore.KeyEntry var6 = (KeychainStore.KeyEntry)var5;
                  if (var6.chain != null) {
                     for(int var10 = 0; var10 < var6.chain.length; ++var10) {
                        var6.chainRefs[var10] = this.addCertificateToKeychain(var4, var6.chain[var10]);
                     }

                     var6.keyRef = this._addItemToKeychain(var4, false, var6.protectedPrivKey, var6.password);
                  }
               }
            }

            this.deletedEntries.clear();
            this.addedEntries.clear();
            return;
         }
      }
   }

   private long addCertificateToKeychain(String var1, Certificate var2) {
      Object var3 = null;
      long var4 = 0L;

      try {
         byte[] var8 = var2.getEncoded();
         var4 = this._addItemToKeychain(var1, true, var8, (char[])null);
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return var4;
   }

   private native long _addItemToKeychain(String var1, boolean var2, byte[] var3, char[] var4);

   private native int _removeItemFromKeychain(long var1);

   private native void _releaseKeychainItemRef(long var1);

   public void engineLoad(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      permissionCheck();
      synchronized(this.entries) {
         Enumeration var4 = this.entries.keys();

         while(true) {
            while(var4.hasMoreElements()) {
               String var5 = (String)var4.nextElement();
               Object var6 = this.entries.get(var5);
               if (var6 instanceof KeychainStore.TrustedCertEntry) {
                  if (((KeychainStore.TrustedCertEntry)var6).certRef != 0L) {
                     this._releaseKeychainItemRef(((KeychainStore.TrustedCertEntry)var6).certRef);
                  }
               } else {
                  KeychainStore.KeyEntry var7 = (KeychainStore.KeyEntry)var6;
                  if (var7.chain != null) {
                     for(int var8 = 0; var8 < var7.chain.length; ++var8) {
                        if (var7.chainRefs[var8] != 0L) {
                           this._releaseKeychainItemRef(var7.chainRefs[var8]);
                        }
                     }

                     if (var7.keyRef != 0L) {
                        this._releaseKeychainItemRef(var7.keyRef);
                     }
                  }
               }
            }

            this.entries.clear();
            this._scanKeychain();
            return;
         }
      }
   }

   private native void _scanKeychain();

   private void createTrustedCertEntry(String var1, long var2, long var4, byte[] var6) {
      KeychainStore.TrustedCertEntry var7 = new KeychainStore.TrustedCertEntry();

      try {
         CertificateFactory var8 = CertificateFactory.getInstance("X.509");
         ByteArrayInputStream var9 = new ByteArrayInputStream(var6);
         X509Certificate var10 = (X509Certificate)var8.generateCertificate(var9);
         var9.close();
         var7.cert = var10;
         var7.certRef = var2;
         if (var4 != 0L) {
            var7.date = new Date(var4);
         } else {
            var7.date = new Date();
         }

         int var11 = 1;

         for(String var12 = var1; this.entries.containsKey(var1.toLowerCase()); ++var11) {
            var1 = var12 + " " + var11;
         }

         this.entries.put(var1.toLowerCase(), var7);
      } catch (Exception var13) {
         System.err.println("KeychainStore Ignored Exception: " + var13);
      }

   }

   private void createKeyEntry(String var1, long var2, long var4, long[] var6, byte[][] var7) throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
      KeychainStore.KeyEntry var8 = new KeychainStore.KeyEntry();
      var8.protectedPrivKey = null;
      var8.keyRef = var4;
      if (var2 != 0L) {
         var8.date = new Date(var2);
      } else {
         var8.date = new Date();
      }

      ArrayList var9 = new ArrayList();

      try {
         CertificateFactory var10 = CertificateFactory.getInstance("X.509");

         for(int var11 = 0; var11 < var7.length; ++var11) {
            try {
               ByteArrayInputStream var12 = new ByteArrayInputStream(var7[var11]);
               X509Certificate var13 = (X509Certificate)var10.generateCertificate(var12);
               var12.close();
               var9.add(new KeychainStore.CertKeychainItemPair(var6[var11], var13));
            } catch (CertificateException var15) {
               System.err.println("KeychainStore Ignored Exception: " + var15);
            }
         }
      } catch (CertificateException var16) {
         var16.printStackTrace();
      } catch (IOException var17) {
         var17.printStackTrace();
      }

      Object[] var18 = var9.toArray();
      Certificate[] var19 = new Certificate[var18.length];
      long[] var20 = new long[var18.length];

      int var21;
      for(var21 = 0; var21 < var18.length; ++var21) {
         KeychainStore.CertKeychainItemPair var14 = (KeychainStore.CertKeychainItemPair)var18[var21];
         var19[var21] = var14.mCert;
         var20[var21] = var14.mCertificateRef;
      }

      var8.chain = var19;
      var8.chainRefs = var20;
      var21 = 1;

      for(String var22 = var1; this.entries.containsKey(var1.toLowerCase()); ++var21) {
         var1 = var22 + " " + var21;
      }

      this.entries.put(var1.toLowerCase(), var8);
   }

   private boolean validateChain(Certificate[] var1) {
      for(int var2 = 0; var2 < var1.length - 1; ++var2) {
         X500Principal var3 = ((X509Certificate)var1[var2]).getIssuerX500Principal();
         X500Principal var4 = ((X509Certificate)var1[var2 + 1]).getSubjectX500Principal();
         if (!var3.equals(var4)) {
            return false;
         }
      }

      return true;
   }

   private byte[] fetchPrivateKeyFromBag(byte[] var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      byte[] var2 = null;
      DerValue var3 = new DerValue(new ByteArrayInputStream(var1));
      DerInputStream var4 = var3.toDerInputStream();
      int var5 = var4.getInteger();
      if (var5 != 3) {
         throw new IOException("PKCS12 keystore not in version 3 format");
      } else {
         ContentInfo var7 = new ContentInfo(var4);
         ObjectIdentifier var8 = var7.getContentType();
         if (var8.equals(ContentInfo.DATA_OID)) {
            byte[] var6 = var7.getData();
            DerInputStream var9 = new DerInputStream(var6);
            DerValue[] var10 = var9.getSequence(2);
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               Object var16 = null;
               DerInputStream var15 = new DerInputStream(var10[var12].toByteArray());
               ContentInfo var14 = new ContentInfo(var15);
               var8 = var14.getContentType();
               Object var13 = null;
               if (var8.equals(ContentInfo.DATA_OID)) {
                  byte[] var18 = var14.getData();
                  DerInputStream var17 = new DerInputStream(var18);
                  var2 = this.extractKeyData(var17);
               } else if (!var8.equals(ContentInfo.ENCRYPTED_DATA_OID)) {
                  throw new IOException("public key protected PKCS12 not supported");
               }
            }

            return var2;
         } else {
            throw new IOException("public key protected PKCS12 not supported");
         }
      }
   }

   private byte[] extractKeyData(DerInputStream var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      byte[] var2 = null;
      DerValue[] var3 = var1.getSequence(2);
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var9 = null;
         DerInputStream var7 = var3[var5].toDerInputStream();
         ObjectIdentifier var6 = var7.getOID();
         DerValue var8 = var7.getDerValue();
         if (!var8.isContextSpecific((byte)0)) {
            throw new IOException("unsupported PKCS12 bag value type " + var8.tag);
         }

         var8 = var8.data.getDerValue();
         if (var6.equals(PKCS8ShroudedKeyBag_OID)) {
            var2 = var8.toByteArray();
         } else {
            System.out.println("Unsupported bag type '" + var6 + "'");
         }
      }

      return var2;
   }

   private AlgorithmParameters getAlgorithmParameters(String var1) throws IOException {
      AlgorithmParameters var2 = null;
      PBEParameterSpec var3 = new PBEParameterSpec(this.getSalt(), 1024);

      try {
         var2 = AlgorithmParameters.getInstance(var1);
         var2.init((AlgorithmParameterSpec)var3);
         return var2;
      } catch (Exception var6) {
         IOException var5 = new IOException("getAlgorithmParameters failed: " + var6.getMessage());
         var5.initCause(var6);
         throw var5;
      }
   }

   private byte[] getSalt() {
      byte[] var1 = new byte[20];
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      var1 = this.random.generateSeed(20);
      return var1;
   }

   private AlgorithmParameters parseAlgParameters(DerInputStream var1) throws IOException {
      AlgorithmParameters var2 = null;

      try {
         DerValue var3;
         if (var1.available() == 0) {
            var3 = null;
         } else {
            var3 = var1.getDerValue();
            if (var3.tag == 5) {
               var3 = null;
            }
         }

         if (var3 != null) {
            var2 = AlgorithmParameters.getInstance("PBE");
            var2.init(var3.toByteArray());
         }

         return var2;
      } catch (Exception var5) {
         IOException var4 = new IOException("parseAlgParameters failed: " + var5.getMessage());
         var4.initCause(var5);
         throw var4;
      }
   }

   private SecretKey getPBEKey(char[] var1) throws IOException {
      SecretKey var2 = null;

      try {
         PBEKeySpec var3 = new PBEKeySpec(var1);
         SecretKeyFactory var6 = SecretKeyFactory.getInstance("PBE");
         var2 = var6.generateSecret(var3);
         return var2;
      } catch (Exception var5) {
         IOException var4 = new IOException("getSecretKey failed: " + var5.getMessage());
         var4.initCause(var5);
         throw var4;
      }
   }

   private byte[] encryptPrivateKey(byte[] var1, char[] var2) throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
      Object var3 = null;

      byte[] var11;
      try {
         AlgorithmParameters var4 = this.getAlgorithmParameters("PBEWithSHA1AndDESede");
         SecretKey var12 = this.getPBEKey(var2);
         Cipher var6 = Cipher.getInstance("PBEWithSHA1AndDESede");
         var6.init(1, var12, var4);
         byte[] var7 = var6.doFinal(var1);
         AlgorithmId var8 = new AlgorithmId(pbeWithSHAAnd3KeyTripleDESCBC_OID, var4);
         EncryptedPrivateKeyInfo var9 = new EncryptedPrivateKeyInfo(var8, var7);
         var11 = var9.getEncoded();
      } catch (Exception var10) {
         UnrecoverableKeyException var5 = new UnrecoverableKeyException("Encrypt Private Key failed: " + var10.getMessage());
         var5.initCause(var10);
         throw var5;
      }

      return (byte[])var11;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osx");
            return null;
         }
      });

      try {
         PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(keyBag);
         pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(pbeWithSHAAnd3KeyTripleDESCBC);
      } catch (IOException var1) {
      }

   }

   private class CertKeychainItemPair {
      long mCertificateRef;
      Certificate mCert;

      CertKeychainItemPair(long var2, Certificate var4) {
         this.mCertificateRef = var2;
         this.mCert = var4;
      }
   }

   class TrustedCertEntry {
      Date date;
      Certificate cert;
      long certRef;
   }

   class KeyEntry {
      Date date;
      byte[] protectedPrivKey;
      char[] password;
      long keyRef;
      Certificate[] chain;
      long[] chainRefs;
   }
}
