package sun.security.pkcs12;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PKCS12Attribute;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.x500.X500Principal;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public final class PKCS12KeyStore extends KeyStoreSpi {
   public static final int VERSION_3 = 3;
   private static final String[] KEY_PROTECTION_ALGORITHM = new String[]{"keystore.pkcs12.keyProtectionAlgorithm", "keystore.PKCS12.keyProtectionAlgorithm"};
   private static final int MAX_ITERATION_COUNT = 5000000;
   private static final int PBE_ITERATION_COUNT = 50000;
   private static final int MAC_ITERATION_COUNT = 100000;
   private static final int SALT_LEN = 20;
   private static final String[] CORE_ATTRIBUTES = new String[]{"1.2.840.113549.1.9.20", "1.2.840.113549.1.9.21", "2.16.840.1.113894.746875.1.1"};
   private static final Debug debug = Debug.getInstance("pkcs12");
   private static final int[] keyBag = new int[]{1, 2, 840, 113549, 1, 12, 10, 1, 2};
   private static final int[] certBag = new int[]{1, 2, 840, 113549, 1, 12, 10, 1, 3};
   private static final int[] secretBag = new int[]{1, 2, 840, 113549, 1, 12, 10, 1, 5};
   private static final int[] pkcs9Name = new int[]{1, 2, 840, 113549, 1, 9, 20};
   private static final int[] pkcs9KeyId = new int[]{1, 2, 840, 113549, 1, 9, 21};
   private static final int[] pkcs9certType = new int[]{1, 2, 840, 113549, 1, 9, 22, 1};
   private static final int[] pbeWithSHAAnd40BitRC2CBC = new int[]{1, 2, 840, 113549, 1, 12, 1, 6};
   private static final int[] pbeWithSHAAnd3KeyTripleDESCBC = new int[]{1, 2, 840, 113549, 1, 12, 1, 3};
   private static final int[] pbes2 = new int[]{1, 2, 840, 113549, 1, 5, 13};
   private static final int[] TrustedKeyUsage = new int[]{2, 16, 840, 1, 113894, 746875, 1, 1};
   private static final int[] AnyExtendedKeyUsage = new int[]{2, 5, 29, 37, 0};
   private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
   private static ObjectIdentifier CertBag_OID;
   private static ObjectIdentifier SecretBag_OID;
   private static ObjectIdentifier PKCS9FriendlyName_OID;
   private static ObjectIdentifier PKCS9LocalKeyId_OID;
   private static ObjectIdentifier PKCS9CertType_OID;
   private static ObjectIdentifier pbeWithSHAAnd40BitRC2CBC_OID;
   private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
   private static ObjectIdentifier pbes2_OID;
   private static ObjectIdentifier TrustedKeyUsage_OID;
   private static ObjectIdentifier[] AnyUsage;
   private int counter = 0;
   private int privateKeyCount = 0;
   private int secretKeyCount = 0;
   private int certificateCount = 0;
   private SecureRandom random;
   private Map<String, PKCS12KeyStore.Entry> entries = Collections.synchronizedMap(new LinkedHashMap());
   private ArrayList<PKCS12KeyStore.KeyEntry> keyList = new ArrayList();
   private LinkedHashMap<X500Principal, X509Certificate> certsMap = new LinkedHashMap();
   private ArrayList<PKCS12KeyStore.CertEntry> certEntries = new ArrayList();

   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      PKCS12KeyStore.Entry var3 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      Object var4 = null;
      if (var3 != null && var3 instanceof PKCS12KeyStore.KeyEntry) {
         Object var5 = null;
         byte[] var26;
         if (var3 instanceof PKCS12KeyStore.PrivateKeyEntry) {
            var26 = ((PKCS12KeyStore.PrivateKeyEntry)var3).protectedPrivKey;
         } else {
            if (!(var3 instanceof PKCS12KeyStore.SecretKeyEntry)) {
               throw new UnrecoverableKeyException("Error locating key");
            }

            var26 = ((PKCS12KeyStore.SecretKeyEntry)var3).protectedSecretKey;
         }

         byte[] var6;
         AlgorithmParameters var7;
         ObjectIdentifier var8;
         UnrecoverableKeyException var10;
         try {
            EncryptedPrivateKeyInfo var9 = new EncryptedPrivateKeyInfo(var26);
            var6 = var9.getEncryptedData();
            DerValue var28 = new DerValue(var9.getAlgorithm().encode());
            DerInputStream var11 = var28.toDerInputStream();
            var8 = var11.getOID();
            var7 = this.parseAlgParameters(var8, var11);
         } catch (IOException var23) {
            var10 = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + var23);
            var10.initCause(var23);
            throw var10;
         }

         try {
            int var29 = 0;
            if (var7 != null) {
               PBEParameterSpec var27;
               try {
                  var27 = (PBEParameterSpec)var7.getParameterSpec(PBEParameterSpec.class);
               } catch (InvalidParameterSpecException var22) {
                  throw new IOException("Invalid PBE algorithm parameters");
               }

               var29 = var27.getIterationCount();
               if (var29 > 5000000) {
                  throw new IOException("PBE iteration count too large");
               }
            }

            byte[] var30;
            while(true) {
               try {
                  SecretKey var12 = this.getPBEKey(var2);
                  Cipher var13 = Cipher.getInstance(mapPBEParamsToAlgorithm(var8, var7));
                  var13.init(2, var12, var7);
                  var30 = var13.doFinal(var6);
                  break;
               } catch (Exception var24) {
                  if (var2.length != 0) {
                     throw var24;
                  }

                  var2 = new char[1];
               }
            }

            DerValue var31 = new DerValue(var30);
            DerInputStream var32 = var31.toDerInputStream();
            int var14 = var32.getInteger();
            DerValue[] var15 = var32.getSequence(2);
            AlgorithmId var16 = new AlgorithmId(var15[0].getOID());
            String var17 = var16.getName();
            if (var3 instanceof PKCS12KeyStore.PrivateKeyEntry) {
               KeyFactory var18 = KeyFactory.getInstance(var17);
               PKCS8EncodedKeySpec var19 = new PKCS8EncodedKeySpec(var30);
               var4 = var18.generatePrivate(var19);
               if (debug != null) {
                  debug.println("Retrieved a protected private key at alias '" + var1 + "' (" + (new AlgorithmId(var8)).getName() + " iterations: " + var29 + ")");
               }
            } else {
               byte[] var33 = var32.getOctetString();
               SecretKeySpec var34 = new SecretKeySpec(var33, var17);
               if (var17.startsWith("PBE")) {
                  SecretKeyFactory var20 = SecretKeyFactory.getInstance(var17);
                  KeySpec var21 = var20.getKeySpec(var34, PBEKeySpec.class);
                  var4 = var20.generateSecret(var21);
               } else {
                  var4 = var34;
               }

               if (debug != null) {
                  debug.println("Retrieved a protected secret key at alias '" + var1 + "' (" + (new AlgorithmId(var8)).getName() + " iterations: " + var29 + ")");
               }
            }

            return (Key)var4;
         } catch (Exception var25) {
            var10 = new UnrecoverableKeyException("Get Key failed: " + var25.getMessage());
            var10.initCause(var25);
            throw var10;
         }
      } else {
         return null;
      }
   }

   public Certificate[] engineGetCertificateChain(String var1) {
      PKCS12KeyStore.Entry var2 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      if (var2 != null && var2 instanceof PKCS12KeyStore.PrivateKeyEntry) {
         if (((PKCS12KeyStore.PrivateKeyEntry)var2).chain == null) {
            return null;
         } else {
            if (debug != null) {
               debug.println("Retrieved a " + ((PKCS12KeyStore.PrivateKeyEntry)var2).chain.length + "-certificate chain at alias '" + var1 + "'");
            }

            return (Certificate[])((PKCS12KeyStore.PrivateKeyEntry)var2).chain.clone();
         }
      } else {
         return null;
      }
   }

   public Certificate engineGetCertificate(String var1) {
      PKCS12KeyStore.Entry var2 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      if (var2 == null) {
         return null;
      } else if (var2 instanceof PKCS12KeyStore.CertEntry && ((PKCS12KeyStore.CertEntry)var2).trustedKeyUsage != null) {
         if (debug != null) {
            if (Arrays.equals((Object[])AnyUsage, (Object[])((PKCS12KeyStore.CertEntry)var2).trustedKeyUsage)) {
               debug.println("Retrieved a certificate at alias '" + var1 + "' (trusted for any purpose)");
            } else {
               debug.println("Retrieved a certificate at alias '" + var1 + "' (trusted for limited purposes)");
            }
         }

         return ((PKCS12KeyStore.CertEntry)var2).cert;
      } else if (var2 instanceof PKCS12KeyStore.PrivateKeyEntry) {
         if (((PKCS12KeyStore.PrivateKeyEntry)var2).chain == null) {
            return null;
         } else {
            if (debug != null) {
               debug.println("Retrieved a certificate at alias '" + var1 + "'");
            }

            return ((PKCS12KeyStore.PrivateKeyEntry)var2).chain[0];
         }
      } else {
         return null;
      }
   }

   public Date engineGetCreationDate(String var1) {
      PKCS12KeyStore.Entry var2 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      return var2 != null ? new Date(var2.date.getTime()) : null;
   }

   public synchronized void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      KeyStore.PasswordProtection var5 = new KeyStore.PasswordProtection(var3);

      try {
         this.setKeyEntry(var1, var2, var5, var4, (Set)null);
      } finally {
         try {
            var5.destroy();
         } catch (DestroyFailedException var12) {
         }

      }

   }

   private void setKeyEntry(String var1, Key var2, KeyStore.PasswordProtection var3, Certificate[] var4, Set<KeyStore.Entry.Attribute> var5) throws KeyStoreException {
      try {
         Object var6;
         if (var2 instanceof PrivateKey) {
            PKCS12KeyStore.PrivateKeyEntry var7 = new PKCS12KeyStore.PrivateKeyEntry();
            var7.date = new Date();
            if (!var2.getFormat().equals("PKCS#8") && !var2.getFormat().equals("PKCS8")) {
               throw new KeyStoreException("Private key is not encodedas PKCS#8");
            }

            if (debug != null) {
               debug.println("Setting a protected private key at alias '" + var1 + "'");
            }

            var7.protectedPrivKey = this.encryptPrivateKey(var2.getEncoded(), var3);
            if (var4 != null) {
               if (var4.length > 1 && !this.validateChain(var4)) {
                  throw new KeyStoreException("Certificate chain is not valid");
               }

               var7.chain = (Certificate[])var4.clone();
               this.certificateCount += var4.length;
               if (debug != null) {
                  debug.println("Setting a " + var4.length + "-certificate chain at alias '" + var1 + "'");
               }
            }

            ++this.privateKeyCount;
            var6 = var7;
         } else {
            if (!(var2 instanceof SecretKey)) {
               throw new KeyStoreException("Unsupported Key type");
            }

            PKCS12KeyStore.SecretKeyEntry var12 = new PKCS12KeyStore.SecretKeyEntry();
            var12.date = new Date();
            DerOutputStream var8 = new DerOutputStream();
            DerOutputStream var9 = new DerOutputStream();
            var9.putInteger(0);
            AlgorithmId var10 = AlgorithmId.get(var2.getAlgorithm());
            var10.encode(var9);
            var9.putOctetString(var2.getEncoded());
            var8.write((byte)48, (DerOutputStream)var9);
            var12.protectedSecretKey = this.encryptPrivateKey(var8.toByteArray(), var3);
            if (debug != null) {
               debug.println("Setting a protected secret key at alias '" + var1 + "'");
            }

            ++this.secretKeyCount;
            var6 = var12;
         }

         ((PKCS12KeyStore.Entry)var6).attributes = new HashSet();
         if (var5 != null) {
            ((PKCS12KeyStore.Entry)var6).attributes.addAll(var5);
         }

         ((PKCS12KeyStore.Entry)var6).keyId = ("Time " + ((PKCS12KeyStore.Entry)var6).date.getTime()).getBytes("UTF8");
         ((PKCS12KeyStore.Entry)var6).alias = var1.toLowerCase(Locale.ENGLISH);
         this.entries.put(var1.toLowerCase(Locale.ENGLISH), var6);
      } catch (Exception var11) {
         throw new KeyStoreException("Key protection  algorithm not found: " + var11, var11);
      }
   }

   public synchronized void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      try {
         new EncryptedPrivateKeyInfo(var2);
      } catch (IOException var7) {
         throw new KeyStoreException("Private key is not stored as PKCS#8 EncryptedPrivateKeyInfo: " + var7, var7);
      }

      PKCS12KeyStore.PrivateKeyEntry var4 = new PKCS12KeyStore.PrivateKeyEntry();
      var4.date = new Date();
      if (debug != null) {
         debug.println("Setting a protected private key at alias '" + var1 + "'");
      }

      try {
         var4.keyId = ("Time " + var4.date.getTime()).getBytes("UTF8");
      } catch (UnsupportedEncodingException var6) {
      }

      var4.alias = var1.toLowerCase(Locale.ENGLISH);
      var4.protectedPrivKey = (byte[])var2.clone();
      if (var3 != null) {
         if (var3.length > 1 && !this.validateChain(var3)) {
            throw new KeyStoreException("Certificate chain is not valid");
         }

         var4.chain = (Certificate[])var3.clone();
         this.certificateCount += var3.length;
         if (debug != null) {
            debug.println("Setting a " + var4.chain.length + "-certificate chain at alias '" + var1 + "'");
         }
      }

      ++this.privateKeyCount;
      this.entries.put(var1.toLowerCase(Locale.ENGLISH), var4);
   }

   private byte[] getSalt() {
      byte[] var1 = new byte[20];
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      this.random.nextBytes(var1);
      return var1;
   }

   private AlgorithmParameters getPBEAlgorithmParameters(String var1) throws IOException {
      AlgorithmParameters var2 = null;
      PBEParameterSpec var3 = new PBEParameterSpec(this.getSalt(), 50000);

      try {
         var2 = AlgorithmParameters.getInstance(var1);
         var2.init((AlgorithmParameterSpec)var3);
         return var2;
      } catch (Exception var5) {
         throw new IOException("getPBEAlgorithmParameters failed: " + var5.getMessage(), var5);
      }
   }

   private AlgorithmParameters parseAlgParameters(ObjectIdentifier var1, DerInputStream var2) throws IOException {
      AlgorithmParameters var3 = null;

      try {
         DerValue var4;
         if (var2.available() == 0) {
            var4 = null;
         } else {
            var4 = var2.getDerValue();
            if (var4.tag == 5) {
               var4 = null;
            }
         }

         if (var4 != null) {
            if (var1.equals((Object)pbes2_OID)) {
               var3 = AlgorithmParameters.getInstance("PBES2");
            } else {
               var3 = AlgorithmParameters.getInstance("PBE");
            }

            var3.init(var4.toByteArray());
         }

         return var3;
      } catch (Exception var5) {
         throw new IOException("parseAlgParameters failed: " + var5.getMessage(), var5);
      }
   }

   private SecretKey getPBEKey(char[] var1) throws IOException {
      SecretKey var2 = null;

      try {
         PBEKeySpec var3 = new PBEKeySpec(var1);
         SecretKeyFactory var4 = SecretKeyFactory.getInstance("PBE");
         var2 = var4.generateSecret(var3);
         var3.clearPassword();
         return var2;
      } catch (Exception var5) {
         throw new IOException("getSecretKey failed: " + var5.getMessage(), var5);
      }
   }

   private byte[] encryptPrivateKey(byte[] var1, KeyStore.PasswordProtection var2) throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
      Object var3 = null;

      try {
         String var4 = var2.getProtectionAlgorithm();
         AlgorithmParameters var14;
         if (var4 != null) {
            AlgorithmParameterSpec var7 = var2.getProtectionParameters();
            if (var7 != null) {
               var14 = AlgorithmParameters.getInstance(var4);
               var14.init(var7);
            } else {
               var14 = this.getPBEAlgorithmParameters(var4);
            }
         } else {
            var4 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  String var1 = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[0]);
                  if (var1 == null) {
                     var1 = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[1]);
                  }

                  return var1;
               }
            });
            if (var4 == null || var4.isEmpty()) {
               var4 = "PBEWithSHA1AndDESede";
            }

            var14 = this.getPBEAlgorithmParameters(var4);
         }

         ObjectIdentifier var15 = mapPBEAlgorithmToOID(var4);
         if (var15 == null) {
            throw new IOException("PBE algorithm '" + var4 + " 'is not supported for key entry protection");
         } else {
            SecretKey var8 = this.getPBEKey(var2.getPassword());
            Cipher var9 = Cipher.getInstance(var4);
            var9.init(1, var8, var14);
            byte[] var10 = var9.doFinal(var1);
            AlgorithmId var6 = new AlgorithmId(var15, var9.getParameters());
            if (debug != null) {
               debug.println("  (Cipher algorithm: " + var9.getAlgorithm() + ")");
            }

            EncryptedPrivateKeyInfo var11 = new EncryptedPrivateKeyInfo(var6, var10);
            byte[] var13 = var11.getEncoded();
            return var13;
         }
      } catch (Exception var12) {
         UnrecoverableKeyException var5 = new UnrecoverableKeyException("Encrypt Private Key failed: " + var12.getMessage());
         var5.initCause(var12);
         throw var5;
      }
   }

   private static ObjectIdentifier mapPBEAlgorithmToOID(String var0) throws NoSuchAlgorithmException {
      return var0.toLowerCase(Locale.ENGLISH).startsWith("pbewithhmacsha") ? pbes2_OID : AlgorithmId.get(var0).getOID();
   }

   private static String mapPBEParamsToAlgorithm(ObjectIdentifier var0, AlgorithmParameters var1) throws NoSuchAlgorithmException {
      return var0.equals((Object)pbes2_OID) && var1 != null ? var1.toString() : var0.toString();
   }

   public synchronized void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      this.setCertEntry(var1, var2, (Set)null);
   }

   private void setCertEntry(String var1, Certificate var2, Set<KeyStore.Entry.Attribute> var3) throws KeyStoreException {
      PKCS12KeyStore.Entry var4 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      if (var4 != null && var4 instanceof PKCS12KeyStore.KeyEntry) {
         throw new KeyStoreException("Cannot overwrite own certificate");
      } else {
         PKCS12KeyStore.CertEntry var5 = new PKCS12KeyStore.CertEntry((X509Certificate)var2, (byte[])null, var1, AnyUsage, var3);
         ++this.certificateCount;
         this.entries.put(var1, var5);
         if (debug != null) {
            debug.println("Setting a trusted certificate at alias '" + var1 + "'");
         }

      }
   }

   public synchronized void engineDeleteEntry(String var1) throws KeyStoreException {
      if (debug != null) {
         debug.println("Removing entry at alias '" + var1 + "'");
      }

      PKCS12KeyStore.Entry var2 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      if (var2 instanceof PKCS12KeyStore.PrivateKeyEntry) {
         PKCS12KeyStore.PrivateKeyEntry var3 = (PKCS12KeyStore.PrivateKeyEntry)var2;
         if (var3.chain != null) {
            this.certificateCount -= var3.chain.length;
         }

         --this.privateKeyCount;
      } else if (var2 instanceof PKCS12KeyStore.CertEntry) {
         --this.certificateCount;
      } else if (var2 instanceof PKCS12KeyStore.SecretKeyEntry) {
         --this.secretKeyCount;
      }

      this.entries.remove(var1.toLowerCase(Locale.ENGLISH));
   }

   public Enumeration<String> engineAliases() {
      return Collections.enumeration(this.entries.keySet());
   }

   public boolean engineContainsAlias(String var1) {
      return this.entries.containsKey(var1.toLowerCase(Locale.ENGLISH));
   }

   public int engineSize() {
      return this.entries.size();
   }

   public boolean engineIsKeyEntry(String var1) {
      PKCS12KeyStore.Entry var2 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      return var2 != null && var2 instanceof PKCS12KeyStore.KeyEntry;
   }

   public boolean engineIsCertificateEntry(String var1) {
      PKCS12KeyStore.Entry var2 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
      return var2 != null && var2 instanceof PKCS12KeyStore.CertEntry && ((PKCS12KeyStore.CertEntry)var2).trustedKeyUsage != null;
   }

   public boolean engineEntryInstanceOf(String var1, Class<? extends KeyStore.Entry> var2) {
      if (var2 == KeyStore.TrustedCertificateEntry.class) {
         return this.engineIsCertificateEntry(var1);
      } else {
         PKCS12KeyStore.Entry var3 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
         if (var2 == KeyStore.PrivateKeyEntry.class) {
            return var3 != null && var3 instanceof PKCS12KeyStore.PrivateKeyEntry;
         } else if (var2 != KeyStore.SecretKeyEntry.class) {
            return false;
         } else {
            return var3 != null && var3 instanceof PKCS12KeyStore.SecretKeyEntry;
         }
      }
   }

   public String engineGetCertificateAlias(Certificate var1) {
      Object var2 = null;
      Enumeration var3 = this.engineAliases();

      String var4;
      label31:
      do {
         PKCS12KeyStore.Entry var5;
         do {
            if (!var3.hasMoreElements()) {
               return null;
            }

            var4 = (String)var3.nextElement();
            var5 = (PKCS12KeyStore.Entry)this.entries.get(var4);
            if (var5 instanceof PKCS12KeyStore.PrivateKeyEntry) {
               if (((PKCS12KeyStore.PrivateKeyEntry)var5).chain != null) {
                  var2 = ((PKCS12KeyStore.PrivateKeyEntry)var5).chain[0];
               }
               continue label31;
            }
         } while(!(var5 instanceof PKCS12KeyStore.CertEntry) || ((PKCS12KeyStore.CertEntry)var5).trustedKeyUsage == null);

         var2 = ((PKCS12KeyStore.CertEntry)var5).cert;
      } while(var2 == null || !((Certificate)var2).equals(var1));

      return var4;
   }

   public synchronized void engineStore(OutputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (var2 == null) {
         throw new IllegalArgumentException("password can't be null");
      } else {
         DerOutputStream var3 = new DerOutputStream();
         DerOutputStream var4 = new DerOutputStream();
         var4.putInteger(3);
         byte[] var5 = var4.toByteArray();
         var3.write(var5);
         DerOutputStream var6 = new DerOutputStream();
         DerOutputStream var7 = new DerOutputStream();
         byte[] var8;
         ContentInfo var9;
         if (this.privateKeyCount > 0 || this.secretKeyCount > 0) {
            if (debug != null) {
               debug.println("Storing " + (this.privateKeyCount + this.secretKeyCount) + " protected key(s) in a PKCS#7 data");
            }

            var8 = this.createSafeContent();
            var9 = new ContentInfo(var8);
            var9.encode(var7);
         }

         if (this.certificateCount > 0) {
            if (debug != null) {
               debug.println("Storing " + this.certificateCount + " certificate(s) in a PKCS#7 encryptedData");
            }

            var8 = this.createEncryptedData(var2);
            var9 = new ContentInfo(ContentInfo.ENCRYPTED_DATA_OID, new DerValue(var8));
            var9.encode(var7);
         }

         DerOutputStream var15 = new DerOutputStream();
         var15.write((byte)48, (DerOutputStream)var7);
         byte[] var16 = var15.toByteArray();
         ContentInfo var10 = new ContentInfo(var16);
         var10.encode(var6);
         byte[] var11 = var6.toByteArray();
         var3.write(var11);
         byte[] var12 = this.calculateMac(var2, var16);
         var3.write(var12);
         DerOutputStream var13 = new DerOutputStream();
         var13.write((byte)48, (DerOutputStream)var3);
         byte[] var14 = var13.toByteArray();
         var1.write(var14);
         var1.flush();
      }
   }

   public KeyStore.Entry engineGetEntry(String var1, KeyStore.ProtectionParameter var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
      if (!this.engineContainsAlias(var1)) {
         return null;
      } else {
         PKCS12KeyStore.Entry var3 = (PKCS12KeyStore.Entry)this.entries.get(var1.toLowerCase(Locale.ENGLISH));
         if (var2 == null) {
            if (!this.engineIsCertificateEntry(var1)) {
               throw new UnrecoverableKeyException("requested entry requires a password");
            }

            if (var3 instanceof PKCS12KeyStore.CertEntry && ((PKCS12KeyStore.CertEntry)var3).trustedKeyUsage != null) {
               if (debug != null) {
                  debug.println("Retrieved a trusted certificate at alias '" + var1 + "'");
               }

               return new KeyStore.TrustedCertificateEntry(((PKCS12KeyStore.CertEntry)var3).cert, this.getAttributes(var3));
            }
         }

         if (var2 instanceof KeyStore.PasswordProtection) {
            if (this.engineIsCertificateEntry(var1)) {
               throw new UnsupportedOperationException("trusted certificate entries are not password-protected");
            }

            if (this.engineIsKeyEntry(var1)) {
               KeyStore.PasswordProtection var4 = (KeyStore.PasswordProtection)var2;
               char[] var5 = var4.getPassword();
               Key var6 = this.engineGetKey(var1, var5);
               if (var6 instanceof PrivateKey) {
                  Certificate[] var7 = this.engineGetCertificateChain(var1);
                  return new KeyStore.PrivateKeyEntry((PrivateKey)var6, var7, this.getAttributes(var3));
               }

               if (var6 instanceof SecretKey) {
                  return new KeyStore.SecretKeyEntry((SecretKey)var6, this.getAttributes(var3));
               }
            } else if (!this.engineIsKeyEntry(var1)) {
               throw new UnsupportedOperationException("untrusted certificate entries are not password-protected");
            }
         }

         throw new UnsupportedOperationException();
      }
   }

   public synchronized void engineSetEntry(String var1, KeyStore.Entry var2, KeyStore.ProtectionParameter var3) throws KeyStoreException {
      if (var3 != null && !(var3 instanceof KeyStore.PasswordProtection)) {
         throw new KeyStoreException("unsupported protection parameter");
      } else {
         KeyStore.PasswordProtection var4 = null;
         if (var3 != null) {
            var4 = (KeyStore.PasswordProtection)var3;
         }

         if (var2 instanceof KeyStore.TrustedCertificateEntry) {
            if (var3 != null && var4.getPassword() != null) {
               throw new KeyStoreException("trusted certificate entries are not password-protected");
            } else {
               KeyStore.TrustedCertificateEntry var7 = (KeyStore.TrustedCertificateEntry)var2;
               this.setCertEntry(var1, var7.getTrustedCertificate(), var7.getAttributes());
            }
         } else if (var2 instanceof KeyStore.PrivateKeyEntry) {
            if (var4 != null && var4.getPassword() != null) {
               KeyStore.PrivateKeyEntry var6 = (KeyStore.PrivateKeyEntry)var2;
               this.setKeyEntry(var1, var6.getPrivateKey(), var4, var6.getCertificateChain(), var6.getAttributes());
            } else {
               throw new KeyStoreException("non-null password required to create PrivateKeyEntry");
            }
         } else if (var2 instanceof KeyStore.SecretKeyEntry) {
            if (var4 != null && var4.getPassword() != null) {
               KeyStore.SecretKeyEntry var5 = (KeyStore.SecretKeyEntry)var2;
               this.setKeyEntry(var1, var5.getSecretKey(), var4, (Certificate[])null, var5.getAttributes());
            } else {
               throw new KeyStoreException("non-null password required to create SecretKeyEntry");
            }
         } else {
            throw new KeyStoreException("unsupported entry type: " + var2.getClass().getName());
         }
      }
   }

   private Set<KeyStore.Entry.Attribute> getAttributes(PKCS12KeyStore.Entry var1) {
      if (var1.attributes == null) {
         var1.attributes = new HashSet();
      }

      var1.attributes.add(new PKCS12Attribute(PKCS9FriendlyName_OID.toString(), var1.alias));
      byte[] var2 = var1.keyId;
      if (var2 != null) {
         var1.attributes.add(new PKCS12Attribute(PKCS9LocalKeyId_OID.toString(), Debug.toString(var2)));
      }

      if (var1 instanceof PKCS12KeyStore.CertEntry) {
         ObjectIdentifier[] var3 = ((PKCS12KeyStore.CertEntry)var1).trustedKeyUsage;
         if (var3 != null) {
            if (var3.length == 1) {
               var1.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), var3[0].toString()));
            } else {
               var1.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), Arrays.toString((Object[])var3)));
            }
         }
      }

      return var1.attributes;
   }

   private byte[] generateHash(byte[] var1) throws IOException {
      Object var2 = null;

      try {
         MessageDigest var3 = MessageDigest.getInstance("SHA1");
         var3.update(var1);
         byte[] var5 = var3.digest();
         return var5;
      } catch (Exception var4) {
         throw new IOException("generateHash failed: " + var4, var4);
      }
   }

   private byte[] calculateMac(char[] var1, byte[] var2) throws IOException {
      Object var3 = null;
      String var4 = "SHA1";

      try {
         byte[] var5 = this.getSalt();
         Mac var6 = Mac.getInstance("HmacPBESHA1");
         PBEParameterSpec var7 = new PBEParameterSpec(var5, 100000);
         SecretKey var8 = this.getPBEKey(var1);
         var6.init(var8, var7);
         var6.update(var2);
         byte[] var9 = var6.doFinal();
         MacData var10 = new MacData(var4, var9, var5, 100000);
         DerOutputStream var11 = new DerOutputStream();
         var11.write(var10.getEncoded());
         byte[] var13 = var11.toByteArray();
         return var13;
      } catch (Exception var12) {
         throw new IOException("calculateMac failed: " + var12, var12);
      }
   }

   private boolean validateChain(Certificate[] var1) {
      for(int var2 = 0; var2 < var1.length - 1; ++var2) {
         X500Principal var3 = ((X509Certificate)var1[var2]).getIssuerX500Principal();
         X500Principal var4 = ((X509Certificate)var1[var2 + 1]).getSubjectX500Principal();
         if (!var3.equals(var4)) {
            return false;
         }
      }

      HashSet var5 = new HashSet(Arrays.asList(var1));
      return var5.size() == var1.length;
   }

   private byte[] getBagAttributes(String var1, byte[] var2, Set<KeyStore.Entry.Attribute> var3) throws IOException {
      return this.getBagAttributes(var1, var2, (ObjectIdentifier[])null, var3);
   }

   private byte[] getBagAttributes(String var1, byte[] var2, ObjectIdentifier[] var3, Set<KeyStore.Entry.Attribute> var4) throws IOException {
      byte[] var5 = null;
      byte[] var6 = null;
      byte[] var7 = null;
      if (var1 == null && var2 == null && var7 == null) {
         return null;
      } else {
         DerOutputStream var8 = new DerOutputStream();
         DerOutputStream var9;
         DerOutputStream var10;
         DerOutputStream var11;
         if (var1 != null) {
            var9 = new DerOutputStream();
            var9.putOID(PKCS9FriendlyName_OID);
            var10 = new DerOutputStream();
            var11 = new DerOutputStream();
            var10.putBMPString(var1);
            var9.write((byte)49, (DerOutputStream)var10);
            var11.write((byte)48, (DerOutputStream)var9);
            var6 = var11.toByteArray();
         }

         if (var2 != null) {
            var9 = new DerOutputStream();
            var9.putOID(PKCS9LocalKeyId_OID);
            var10 = new DerOutputStream();
            var11 = new DerOutputStream();
            var10.putOctetString(var2);
            var9.write((byte)49, (DerOutputStream)var10);
            var11.write((byte)48, (DerOutputStream)var9);
            var5 = var11.toByteArray();
         }

         if (var3 != null) {
            var9 = new DerOutputStream();
            var9.putOID(TrustedKeyUsage_OID);
            var10 = new DerOutputStream();
            var11 = new DerOutputStream();
            ObjectIdentifier[] var12 = var3;
            int var13 = var3.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               ObjectIdentifier var15 = var12[var14];
               var10.putOID(var15);
            }

            var9.write((byte)49, (DerOutputStream)var10);
            var11.write((byte)48, (DerOutputStream)var9);
            var7 = var11.toByteArray();
         }

         var9 = new DerOutputStream();
         if (var6 != null) {
            var9.write(var6);
         }

         if (var5 != null) {
            var9.write(var5);
         }

         if (var7 != null) {
            var9.write(var7);
         }

         if (var4 != null) {
            Iterator var16 = var4.iterator();

            while(var16.hasNext()) {
               KeyStore.Entry.Attribute var17 = (KeyStore.Entry.Attribute)var16.next();
               String var18 = var17.getName();
               if (!CORE_ATTRIBUTES[0].equals(var18) && !CORE_ATTRIBUTES[1].equals(var18) && !CORE_ATTRIBUTES[2].equals(var18)) {
                  var9.write(((PKCS12Attribute)var17).getEncoded());
               }
            }
         }

         var8.write((byte)49, (DerOutputStream)var9);
         return var8.toByteArray();
      }
   }

   private byte[] createEncryptedData(char[] var1) throws CertificateException, IOException {
      DerOutputStream var2 = new DerOutputStream();
      Enumeration var3 = this.engineAliases();

      while(var3.hasMoreElements()) {
         String var4 = (String)var3.nextElement();
         PKCS12KeyStore.Entry var5 = (PKCS12KeyStore.Entry)this.entries.get(var4);
         Certificate[] var6;
         if (var5 instanceof PKCS12KeyStore.PrivateKeyEntry) {
            PKCS12KeyStore.PrivateKeyEntry var7 = (PKCS12KeyStore.PrivateKeyEntry)var5;
            if (var7.chain != null) {
               var6 = var7.chain;
            } else {
               var6 = new Certificate[0];
            }
         } else if (var5 instanceof PKCS12KeyStore.CertEntry) {
            var6 = new Certificate[]{((PKCS12KeyStore.CertEntry)var5).cert};
         } else {
            var6 = new Certificate[0];
         }

         for(int var20 = 0; var20 < var6.length; ++var20) {
            DerOutputStream var8 = new DerOutputStream();
            var8.putOID(CertBag_OID);
            DerOutputStream var9 = new DerOutputStream();
            var9.putOID(PKCS9CertType_OID);
            DerOutputStream var10 = new DerOutputStream();
            X509Certificate var11 = (X509Certificate)var6[var20];
            var10.putOctetString(var11.getEncoded());
            var9.write(DerValue.createTag((byte)-128, true, (byte)0), var10);
            DerOutputStream var12 = new DerOutputStream();
            var12.write((byte)48, (DerOutputStream)var9);
            byte[] var13 = var12.toByteArray();
            DerOutputStream var14 = new DerOutputStream();
            var14.write(var13);
            var8.write(DerValue.createTag((byte)-128, true, (byte)0), var14);
            Object var15 = null;
            byte[] var23;
            if (var20 == 0) {
               if (var5 instanceof PKCS12KeyStore.KeyEntry) {
                  PKCS12KeyStore.KeyEntry var16 = (PKCS12KeyStore.KeyEntry)var5;
                  var23 = this.getBagAttributes(var16.alias, var16.keyId, var16.attributes);
               } else {
                  PKCS12KeyStore.CertEntry var24 = (PKCS12KeyStore.CertEntry)var5;
                  var23 = this.getBagAttributes(var24.alias, var24.keyId, var24.trustedKeyUsage, var24.attributes);
               }
            } else {
               var23 = this.getBagAttributes(var11.getSubjectX500Principal().getName(), (byte[])null, var5.attributes);
            }

            if (var23 != null) {
               var8.write(var23);
            }

            var2.write((byte)48, (DerOutputStream)var8);
         }
      }

      DerOutputStream var17 = new DerOutputStream();
      var17.write((byte)48, (DerOutputStream)var2);
      byte[] var18 = var17.toByteArray();
      byte[] var19 = this.encryptContent(var18, var1);
      DerOutputStream var21 = new DerOutputStream();
      DerOutputStream var22 = new DerOutputStream();
      var21.putInteger(0);
      var21.write(var19);
      var22.write((byte)48, (DerOutputStream)var21);
      return var22.toByteArray();
   }

   private byte[] createSafeContent() throws CertificateException, IOException {
      DerOutputStream var1 = new DerOutputStream();
      Enumeration var2 = this.engineAliases();

      while(true) {
         String var3;
         PKCS12KeyStore.Entry var4;
         DerOutputStream var5;
         byte[] var14;
         while(true) {
            do {
               do {
                  if (!var2.hasMoreElements()) {
                     DerOutputStream var13 = new DerOutputStream();
                     var13.write((byte)48, (DerOutputStream)var1);
                     return var13.toByteArray();
                  }

                  var3 = (String)var2.nextElement();
                  var4 = (PKCS12KeyStore.Entry)this.entries.get(var3);
               } while(var4 == null);
            } while(!(var4 instanceof PKCS12KeyStore.KeyEntry));

            var5 = new DerOutputStream();
            PKCS12KeyStore.KeyEntry var6 = (PKCS12KeyStore.KeyEntry)var4;
            DerOutputStream var8;
            DerOutputStream var9;
            if (var6 instanceof PKCS12KeyStore.PrivateKeyEntry) {
               var5.putOID(PKCS8ShroudedKeyBag_OID);
               var14 = ((PKCS12KeyStore.PrivateKeyEntry)var6).protectedPrivKey;
               var8 = null;

               EncryptedPrivateKeyInfo var15;
               try {
                  var15 = new EncryptedPrivateKeyInfo(var14);
               } catch (IOException var12) {
                  throw new IOException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo" + var12.getMessage());
               }

               var9 = new DerOutputStream();
               var9.write(var15.getEncoded());
               var5.write(DerValue.createTag((byte)-128, true, (byte)0), var9);
               break;
            }

            if (var6 instanceof PKCS12KeyStore.SecretKeyEntry) {
               var5.putOID(SecretBag_OID);
               DerOutputStream var7 = new DerOutputStream();
               var7.putOID(PKCS8ShroudedKeyBag_OID);
               var8 = new DerOutputStream();
               var8.putOctetString(((PKCS12KeyStore.SecretKeyEntry)var6).protectedSecretKey);
               var7.write(DerValue.createTag((byte)-128, true, (byte)0), var8);
               var9 = new DerOutputStream();
               var9.write((byte)48, (DerOutputStream)var7);
               byte[] var10 = var9.toByteArray();
               DerOutputStream var11 = new DerOutputStream();
               var11.write(var10);
               var5.write(DerValue.createTag((byte)-128, true, (byte)0), var11);
               break;
            }
         }

         var14 = this.getBagAttributes(var3, var4.keyId, var4.attributes);
         var5.write(var14);
         var1.write((byte)48, (DerOutputStream)var5);
      }
   }

   private byte[] encryptContent(byte[] var1, char[] var2) throws IOException {
      Object var3 = null;
      AlgorithmParameters var4 = this.getPBEAlgorithmParameters("PBEWithSHA1AndRC2_40");
      DerOutputStream var5 = new DerOutputStream();
      AlgorithmId var6 = new AlgorithmId(pbeWithSHAAnd40BitRC2CBC_OID, var4);
      var6.encode(var5);
      byte[] var7 = var5.toByteArray();

      byte[] var12;
      try {
         SecretKey var8 = this.getPBEKey(var2);
         Cipher var9 = Cipher.getInstance("PBEWithSHA1AndRC2_40");
         var9.init(1, var8, var4);
         var12 = var9.doFinal(var1);
         if (debug != null) {
            debug.println("  (Cipher algorithm: " + var9.getAlgorithm() + ")");
         }
      } catch (Exception var11) {
         throw new IOException("Failed to encrypt safe contents entry: " + var11, var11);
      }

      DerOutputStream var13 = new DerOutputStream();
      var13.putOID(ContentInfo.DATA_OID);
      var13.write(var7);
      DerOutputStream var14 = new DerOutputStream();
      var14.putOctetString(var12);
      var13.writeImplicit(DerValue.createTag((byte)-128, false, (byte)0), var14);
      DerOutputStream var10 = new DerOutputStream();
      var10.write((byte)48, (DerOutputStream)var13);
      return var10.toByteArray();
   }

   public synchronized void engineLoad(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      Object var4 = null;
      Object var5 = null;
      Object var6 = null;
      if (var1 != null) {
         this.counter = 0;
         DerValue var7 = new DerValue(var1);
         DerInputStream var8 = var7.toDerInputStream();
         int var9 = var8.getInteger();
         if (var9 != 3) {
            throw new IOException("PKCS12 keystore not in version 3 format");
         } else {
            this.entries.clear();
            ContentInfo var11 = new ContentInfo(var8);
            ObjectIdentifier var12 = var11.getContentType();
            if (!var12.equals((Object)ContentInfo.DATA_OID)) {
               throw new IOException("public key protected PKCS12 not supported");
            } else {
               byte[] var10 = var11.getData();
               DerInputStream var13 = new DerInputStream(var10);
               DerValue[] var14 = var13.getSequence(2);
               int var15 = var14.length;
               this.privateKeyCount = 0;
               this.secretKeyCount = 0;
               this.certificateCount = 0;

               PBEParameterSpec var20;
               for(int var16 = 0; var16 < var15; ++var16) {
                  var20 = null;
                  DerInputStream var19 = new DerInputStream(var14[var16].toByteArray());
                  ContentInfo var18 = new ContentInfo(var19);
                  var12 = var18.getContentType();
                  Object var17 = null;
                  DerInputStream var21;
                  byte[] var38;
                  if (var12.equals((Object)ContentInfo.DATA_OID)) {
                     if (debug != null) {
                        debug.println("Loading PKCS#7 data");
                     }

                     var38 = var18.getData();
                  } else {
                     if (!var12.equals((Object)ContentInfo.ENCRYPTED_DATA_OID)) {
                        throw new IOException("public key protected PKCS12 not supported");
                     }

                     if (var2 == null) {
                        if (debug != null) {
                           debug.println("Warning: skipping PKCS#7 encryptedData - no password was supplied");
                        }
                        continue;
                     }

                     var21 = var18.getContent().toDerInputStream();
                     int var22 = var21.getInteger();
                     DerValue[] var23 = var21.getSequence(2);
                     ObjectIdentifier var24 = var23[0].getOID();
                     byte[] var44 = var23[1].toByteArray();
                     if (!var23[2].isContextSpecific((byte)0)) {
                        throw new IOException("encrypted content not present!");
                     }

                     byte var25 = 4;
                     if (var23[2].isConstructed()) {
                        var25 = (byte)(var25 | 32);
                     }

                     var23[2].resetTag(var25);
                     var38 = var23[2].getOctetString();
                     DerInputStream var26 = var23[1].toDerInputStream();
                     ObjectIdentifier var27 = var26.getOID();
                     AlgorithmParameters var28 = this.parseAlgParameters(var27, var26);
                     int var30 = 0;
                     if (var28 != null) {
                        PBEParameterSpec var29;
                        try {
                           var29 = (PBEParameterSpec)var28.getParameterSpec(PBEParameterSpec.class);
                        } catch (InvalidParameterSpecException var34) {
                           throw new IOException("Invalid PBE algorithm parameters");
                        }

                        var30 = var29.getIterationCount();
                        if (var30 > 5000000) {
                           throw new IOException("PBE iteration count too large");
                        }
                     }

                     if (debug != null) {
                        debug.println("Loading PKCS#7 encryptedData (" + (new AlgorithmId(var27)).getName() + " iterations: " + var30 + ")");
                     }

                     while(true) {
                        try {
                           SecretKey var31 = this.getPBEKey(var2);
                           Cipher var32 = Cipher.getInstance(var27.toString());
                           var32.init(2, var31, var28);
                           var38 = var32.doFinal(var38);
                           break;
                        } catch (Exception var35) {
                           if (var2.length != 0) {
                              throw new IOException("keystore password was incorrect", new UnrecoverableKeyException("failed to decrypt safe contents entry: " + var35));
                           }

                           var2 = new char[1];
                        }
                     }
                  }

                  var21 = new DerInputStream(var38);
                  this.loadSafeContents(var21, var2);
               }

               int var40;
               if (var2 != null && var8.available() > 0) {
                  MacData var36 = new MacData(var8);
                  var40 = var36.getIterations();

                  try {
                     if (var40 > 5000000) {
                        throw new InvalidAlgorithmParameterException("MAC iteration count too large: " + var40);
                     }

                     String var39 = var36.getDigestAlgName().toUpperCase(Locale.ENGLISH);
                     var39 = var39.replace("-", "");
                     Mac var42 = Mac.getInstance("HmacPBE" + var39);
                     var20 = new PBEParameterSpec(var36.getSalt(), var40);
                     SecretKey var46 = this.getPBEKey(var2);
                     var42.init(var46, var20);
                     var42.update(var10);
                     byte[] var48 = var42.doFinal();
                     if (debug != null) {
                        debug.println("Checking keystore integrity (" + var42.getAlgorithm() + " iterations: " + var40 + ")");
                     }

                     if (!MessageDigest.isEqual(var36.getDigest(), var48)) {
                        throw new UnrecoverableKeyException("Failed PKCS12 integrity checking");
                     }
                  } catch (Exception var33) {
                     throw new IOException("Integrity check failed: " + var33, var33);
                  }
               }

               PKCS12KeyStore.PrivateKeyEntry[] var37 = (PKCS12KeyStore.PrivateKeyEntry[])this.keyList.toArray(new PKCS12KeyStore.PrivateKeyEntry[this.keyList.size()]);

               for(var40 = 0; var40 < var37.length; ++var40) {
                  PKCS12KeyStore.PrivateKeyEntry var41 = var37[var40];
                  if (var41.keyId != null) {
                     ArrayList var43 = new ArrayList();

                     X500Principal var49;
                     label140:
                     for(X509Certificate var45 = this.findMatchedCertificate(var41); var45 != null; var45 = (X509Certificate)this.certsMap.get(var49)) {
                        if (!var43.isEmpty()) {
                           Iterator var47 = var43.iterator();

                           while(var47.hasNext()) {
                              X509Certificate var50 = (X509Certificate)var47.next();
                              if (var45.equals(var50)) {
                                 if (debug != null) {
                                    debug.println("Loop detected in certificate chain. Skip adding repeated cert to chain. Subject: " + var45.getSubjectX500Principal().toString());
                                 }
                                 break label140;
                              }
                           }
                        }

                        var43.add(var45);
                        var49 = var45.getIssuerX500Principal();
                        if (var49.equals(var45.getSubjectX500Principal())) {
                           break;
                        }
                     }

                     if (var43.size() > 0) {
                        var41.chain = (Certificate[])var43.toArray(new Certificate[var43.size()]);
                     }
                  }
               }

               if (debug != null) {
                  if (this.privateKeyCount > 0) {
                     debug.println("Loaded " + this.privateKeyCount + " protected private key(s)");
                  }

                  if (this.secretKeyCount > 0) {
                     debug.println("Loaded " + this.secretKeyCount + " protected secret key(s)");
                  }

                  if (this.certificateCount > 0) {
                     debug.println("Loaded " + this.certificateCount + " certificate(s)");
                  }
               }

               this.certEntries.clear();
               this.certsMap.clear();
               this.keyList.clear();
            }
         }
      }
   }

   private X509Certificate findMatchedCertificate(PKCS12KeyStore.PrivateKeyEntry var1) {
      PKCS12KeyStore.CertEntry var2 = null;
      PKCS12KeyStore.CertEntry var3 = null;
      Iterator var4 = this.certEntries.iterator();

      while(var4.hasNext()) {
         PKCS12KeyStore.CertEntry var5 = (PKCS12KeyStore.CertEntry)var4.next();
         if (Arrays.equals(var1.keyId, var5.keyId)) {
            var2 = var5;
            if (var1.alias.equalsIgnoreCase(var5.alias)) {
               return var5.cert;
            }
         } else if (var1.alias.equalsIgnoreCase(var5.alias)) {
            var3 = var5;
         }
      }

      if (var2 != null) {
         return var2.cert;
      } else if (var3 != null) {
         return var3.cert;
      } else {
         return null;
      }
   }

   private void loadSafeContents(DerInputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
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
         X509Certificate var15;
         if (var6.equals((Object)PKCS8ShroudedKeyBag_OID)) {
            PKCS12KeyStore.PrivateKeyEntry var10 = new PKCS12KeyStore.PrivateKeyEntry();
            var10.protectedPrivKey = var8.toByteArray();
            var9 = var10;
            ++this.privateKeyCount;
         } else {
            DerValue[] var11;
            ObjectIdentifier var12;
            DerValue var13;
            DerInputStream var26;
            if (var6.equals((Object)CertBag_OID)) {
               var26 = new DerInputStream(var8.toByteArray());
               var11 = var26.getSequence(2);
               var12 = var11[0].getOID();
               if (!var11[1].isContextSpecific((byte)0)) {
                  throw new IOException("unsupported PKCS12 cert value type " + var11[1].tag);
               }

               var13 = var11[1].data.getDerValue();
               CertificateFactory var14 = CertificateFactory.getInstance("X509");
               var15 = (X509Certificate)var14.generateCertificate(new ByteArrayInputStream(var13.getOctetString()));
               var9 = var15;
               ++this.certificateCount;
            } else if (var6.equals((Object)SecretBag_OID)) {
               var26 = new DerInputStream(var8.toByteArray());
               var11 = var26.getSequence(2);
               var12 = var11[0].getOID();
               if (!var11[1].isContextSpecific((byte)0)) {
                  throw new IOException("unsupported PKCS12 secret value type " + var11[1].tag);
               }

               var13 = var11[1].data.getDerValue();
               PKCS12KeyStore.SecretKeyEntry var31 = new PKCS12KeyStore.SecretKeyEntry();
               var31.protectedSecretKey = var13.getOctetString();
               var9 = var31;
               ++this.secretKeyCount;
            } else if (debug != null) {
               debug.println("Unsupported PKCS12 bag type: " + var6);
            }
         }

         DerValue[] var27;
         try {
            var27 = var7.getSet(3);
         } catch (IOException var25) {
            var27 = null;
         }

         String var28 = null;
         byte[] var29 = null;
         ObjectIdentifier[] var30 = null;
         HashSet var32 = new HashSet();
         if (var27 != null) {
            for(int var33 = 0; var33 < var27.length; ++var33) {
               byte[] var16 = var27[var33].toByteArray();
               DerInputStream var17 = new DerInputStream(var16);
               DerValue[] var18 = var17.getSequence(2);
               ObjectIdentifier var19 = var18[0].getOID();
               DerInputStream var20 = new DerInputStream(var18[1].toByteArray());

               DerValue[] var21;
               try {
                  var21 = var20.getSet(1);
               } catch (IOException var24) {
                  throw new IOException("Attribute " + var19 + " should have a value " + var24.getMessage());
               }

               if (var19.equals((Object)PKCS9FriendlyName_OID)) {
                  var28 = var21[0].getBMPString();
               } else if (var19.equals((Object)PKCS9LocalKeyId_OID)) {
                  var29 = var21[0].getOctetString();
               } else if (var19.equals((Object)TrustedKeyUsage_OID)) {
                  var30 = new ObjectIdentifier[var21.length];

                  for(int var22 = 0; var22 < var21.length; ++var22) {
                     var30[var22] = var21[var22].getOID();
                  }
               } else {
                  var32.add(new PKCS12Attribute(var16));
               }
            }
         }

         if (var9 instanceof PKCS12KeyStore.KeyEntry) {
            PKCS12KeyStore.KeyEntry var34 = (PKCS12KeyStore.KeyEntry)var9;
            if (var9 instanceof PKCS12KeyStore.PrivateKeyEntry && var29 == null) {
               if (this.privateKeyCount != 1) {
                  continue;
               }

               var29 = "01".getBytes("UTF8");
            }

            var34.keyId = var29;
            String var37 = new String(var29, "UTF8");
            Date var38 = null;
            if (var37.startsWith("Time ")) {
               try {
                  var38 = new Date(Long.parseLong(var37.substring(5)));
               } catch (Exception var23) {
                  var38 = null;
               }
            }

            if (var38 == null) {
               var38 = new Date();
            }

            var34.date = var38;
            if (var9 instanceof PKCS12KeyStore.PrivateKeyEntry) {
               this.keyList.add((PKCS12KeyStore.PrivateKeyEntry)var34);
            }

            if (var34.attributes == null) {
               var34.attributes = new HashSet();
            }

            var34.attributes.addAll(var32);
            if (var28 == null) {
               var28 = this.getUnfriendlyName();
            }

            var34.alias = var28;
            this.entries.put(var28.toLowerCase(Locale.ENGLISH), var34);
         } else if (var9 instanceof X509Certificate) {
            var15 = (X509Certificate)var9;
            if (var29 == null && this.privateKeyCount == 1 && var5 == 0) {
               var29 = "01".getBytes("UTF8");
            }

            if (var30 != null) {
               if (var28 == null) {
                  var28 = this.getUnfriendlyName();
               }

               PKCS12KeyStore.CertEntry var35 = new PKCS12KeyStore.CertEntry(var15, var29, var28, var30, var32);
               this.entries.put(var28.toLowerCase(Locale.ENGLISH), var35);
            } else {
               this.certEntries.add(new PKCS12KeyStore.CertEntry(var15, var29, var28));
            }

            X500Principal var36 = var15.getSubjectX500Principal();
            if (var36 != null && !this.certsMap.containsKey(var36)) {
               this.certsMap.put(var36, var15);
            }
         }
      }

   }

   private String getUnfriendlyName() {
      ++this.counter;
      return String.valueOf(this.counter);
   }

   static {
      try {
         PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(keyBag);
         CertBag_OID = new ObjectIdentifier(certBag);
         SecretBag_OID = new ObjectIdentifier(secretBag);
         PKCS9FriendlyName_OID = new ObjectIdentifier(pkcs9Name);
         PKCS9LocalKeyId_OID = new ObjectIdentifier(pkcs9KeyId);
         PKCS9CertType_OID = new ObjectIdentifier(pkcs9certType);
         pbeWithSHAAnd40BitRC2CBC_OID = new ObjectIdentifier(pbeWithSHAAnd40BitRC2CBC);
         pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(pbeWithSHAAnd3KeyTripleDESCBC);
         pbes2_OID = new ObjectIdentifier(pbes2);
         TrustedKeyUsage_OID = new ObjectIdentifier(TrustedKeyUsage);
         AnyUsage = new ObjectIdentifier[]{new ObjectIdentifier(AnyExtendedKeyUsage)};
      } catch (IOException var1) {
      }

   }

   private static class CertEntry extends PKCS12KeyStore.Entry {
      final X509Certificate cert;
      ObjectIdentifier[] trustedKeyUsage;

      CertEntry(X509Certificate var1, byte[] var2, String var3) {
         this(var1, var2, var3, (ObjectIdentifier[])null, (Set)null);
      }

      CertEntry(X509Certificate var1, byte[] var2, String var3, ObjectIdentifier[] var4, Set<? extends KeyStore.Entry.Attribute> var5) {
         super(null);
         this.date = new Date();
         this.cert = var1;
         this.keyId = var2;
         this.alias = var3;
         this.trustedKeyUsage = var4;
         this.attributes = new HashSet();
         if (var5 != null) {
            this.attributes.addAll(var5);
         }

      }
   }

   private static class SecretKeyEntry extends PKCS12KeyStore.KeyEntry {
      byte[] protectedSecretKey;

      private SecretKeyEntry() {
         super(null);
      }

      // $FF: synthetic method
      SecretKeyEntry(Object var1) {
         this();
      }
   }

   private static class PrivateKeyEntry extends PKCS12KeyStore.KeyEntry {
      byte[] protectedPrivKey;
      Certificate[] chain;

      private PrivateKeyEntry() {
         super(null);
      }

      // $FF: synthetic method
      PrivateKeyEntry(Object var1) {
         this();
      }
   }

   private static class KeyEntry extends PKCS12KeyStore.Entry {
      private KeyEntry() {
         super(null);
      }

      // $FF: synthetic method
      KeyEntry(Object var1) {
         this();
      }
   }

   private static class Entry {
      Date date;
      String alias;
      byte[] keyId;
      Set<KeyStore.Entry.Attribute> attributes;

      private Entry() {
      }

      // $FF: synthetic method
      Entry(Object var1) {
         this();
      }
   }
}
