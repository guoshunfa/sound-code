package sun.security.provider;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import sun.misc.IOUtils;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.pkcs12.PKCS12KeyStore;

abstract class JavaKeyStore extends KeyStoreSpi {
   private static final int MAGIC = -17957139;
   private static final int VERSION_1 = 1;
   private static final int VERSION_2 = 2;
   private final Hashtable<String, Object> entries = new Hashtable();

   abstract String convertAlias(String var1);

   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      Object var3 = this.entries.get(this.convertAlias(var1));
      if (var3 != null && var3 instanceof JavaKeyStore.KeyEntry) {
         if (var2 == null) {
            throw new UnrecoverableKeyException("Password must not be null");
         } else {
            KeyProtector var4 = new KeyProtector(var2);
            byte[] var5 = ((JavaKeyStore.KeyEntry)var3).protectedPrivKey;

            EncryptedPrivateKeyInfo var6;
            try {
               var6 = new EncryptedPrivateKeyInfo(var5);
            } catch (IOException var9) {
               throw new UnrecoverableKeyException("Private key not stored as PKCS #8 EncryptedPrivateKeyInfo");
            }

            return var4.recover(var6);
         }
      } else {
         return null;
      }
   }

   public Certificate[] engineGetCertificateChain(String var1) {
      Object var2 = this.entries.get(this.convertAlias(var1));
      if (var2 != null && var2 instanceof JavaKeyStore.KeyEntry) {
         return ((JavaKeyStore.KeyEntry)var2).chain == null ? null : (Certificate[])((JavaKeyStore.KeyEntry)var2).chain.clone();
      } else {
         return null;
      }
   }

   public Certificate engineGetCertificate(String var1) {
      Object var2 = this.entries.get(this.convertAlias(var1));
      if (var2 != null) {
         if (var2 instanceof JavaKeyStore.TrustedCertEntry) {
            return ((JavaKeyStore.TrustedCertEntry)var2).cert;
         } else {
            return ((JavaKeyStore.KeyEntry)var2).chain == null ? null : ((JavaKeyStore.KeyEntry)var2).chain[0];
         }
      } else {
         return null;
      }
   }

   public Date engineGetCreationDate(String var1) {
      Object var2 = this.entries.get(this.convertAlias(var1));
      if (var2 != null) {
         return var2 instanceof JavaKeyStore.TrustedCertEntry ? new Date(((JavaKeyStore.TrustedCertEntry)var2).date.getTime()) : new Date(((JavaKeyStore.KeyEntry)var2).date.getTime());
      } else {
         return null;
      }
   }

   public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      KeyProtector var5 = null;
      if (!(var2 instanceof PrivateKey)) {
         throw new KeyStoreException("Cannot store non-PrivateKeys");
      } else {
         try {
            synchronized(this.entries) {
               JavaKeyStore.KeyEntry var7 = new JavaKeyStore.KeyEntry();
               var7.date = new Date();
               var5 = new KeyProtector(var3);
               var7.protectedPrivKey = var5.protect(var2);
               if (var4 != null && var4.length != 0) {
                  var7.chain = (Certificate[])var4.clone();
               } else {
                  var7.chain = null;
               }

               this.entries.put(this.convertAlias(var1), var7);
            }
         } catch (NoSuchAlgorithmException var14) {
            throw new KeyStoreException("Key protection algorithm not found");
         } finally {
            var5 = null;
         }

      }
   }

   public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      synchronized(this.entries) {
         try {
            new EncryptedPrivateKeyInfo(var2);
         } catch (IOException var7) {
            throw new KeyStoreException("key is not encoded as EncryptedPrivateKeyInfo");
         }

         JavaKeyStore.KeyEntry var5 = new JavaKeyStore.KeyEntry();
         var5.date = new Date();
         var5.protectedPrivKey = (byte[])var2.clone();
         if (var3 != null && var3.length != 0) {
            var5.chain = (Certificate[])var3.clone();
         } else {
            var5.chain = null;
         }

         this.entries.put(this.convertAlias(var1), var5);
      }
   }

   public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      synchronized(this.entries) {
         Object var4 = this.entries.get(this.convertAlias(var1));
         if (var4 != null && var4 instanceof JavaKeyStore.KeyEntry) {
            throw new KeyStoreException("Cannot overwrite own certificate");
         } else {
            JavaKeyStore.TrustedCertEntry var5 = new JavaKeyStore.TrustedCertEntry();
            var5.cert = var2;
            var5.date = new Date();
            this.entries.put(this.convertAlias(var1), var5);
         }
      }
   }

   public void engineDeleteEntry(String var1) throws KeyStoreException {
      synchronized(this.entries) {
         this.entries.remove(this.convertAlias(var1));
      }
   }

   public Enumeration<String> engineAliases() {
      return this.entries.keys();
   }

   public boolean engineContainsAlias(String var1) {
      return this.entries.containsKey(this.convertAlias(var1));
   }

   public int engineSize() {
      return this.entries.size();
   }

   public boolean engineIsKeyEntry(String var1) {
      Object var2 = this.entries.get(this.convertAlias(var1));
      return var2 != null && var2 instanceof JavaKeyStore.KeyEntry;
   }

   public boolean engineIsCertificateEntry(String var1) {
      Object var2 = this.entries.get(this.convertAlias(var1));
      return var2 != null && var2 instanceof JavaKeyStore.TrustedCertEntry;
   }

   public String engineGetCertificateAlias(Certificate var1) {
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
            if (var5 instanceof JavaKeyStore.TrustedCertEntry) {
               var2 = ((JavaKeyStore.TrustedCertEntry)var5).cert;
               continue label24;
            }
         } while(((JavaKeyStore.KeyEntry)var5).chain == null);

         var2 = ((JavaKeyStore.KeyEntry)var5).chain[0];
      } while(!var2.equals(var1));

      return var4;
   }

   public void engineStore(OutputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      synchronized(this.entries) {
         if (var2 == null) {
            throw new IllegalArgumentException("password can't be null");
         } else {
            MessageDigest var5 = this.getPreKeyedHash(var2);
            DataOutputStream var6 = new DataOutputStream(new DigestOutputStream(var1, var5));
            var6.writeInt(-17957139);
            var6.writeInt(2);
            var6.writeInt(this.entries.size());
            Enumeration var7 = this.entries.keys();

            while(true) {
               while(var7.hasMoreElements()) {
                  String var8 = (String)var7.nextElement();
                  Object var9 = this.entries.get(var8);
                  byte[] var4;
                  if (var9 instanceof JavaKeyStore.KeyEntry) {
                     var6.writeInt(1);
                     var6.writeUTF(var8);
                     var6.writeLong(((JavaKeyStore.KeyEntry)var9).date.getTime());
                     var6.writeInt(((JavaKeyStore.KeyEntry)var9).protectedPrivKey.length);
                     var6.write(((JavaKeyStore.KeyEntry)var9).protectedPrivKey);
                     int var10;
                     if (((JavaKeyStore.KeyEntry)var9).chain == null) {
                        var10 = 0;
                     } else {
                        var10 = ((JavaKeyStore.KeyEntry)var9).chain.length;
                     }

                     var6.writeInt(var10);

                     for(int var11 = 0; var11 < var10; ++var11) {
                        var4 = ((JavaKeyStore.KeyEntry)var9).chain[var11].getEncoded();
                        var6.writeUTF(((JavaKeyStore.KeyEntry)var9).chain[var11].getType());
                        var6.writeInt(var4.length);
                        var6.write(var4);
                     }
                  } else {
                     var6.writeInt(2);
                     var6.writeUTF(var8);
                     var6.writeLong(((JavaKeyStore.TrustedCertEntry)var9).date.getTime());
                     var4 = ((JavaKeyStore.TrustedCertEntry)var9).cert.getEncoded();
                     var6.writeUTF(((JavaKeyStore.TrustedCertEntry)var9).cert.getType());
                     var6.writeInt(var4.length);
                     var6.write(var4);
                  }
               }

               byte[] var14 = var5.digest();
               var6.write(var14);
               var6.flush();
               return;
            }
         }
      }
   }

   public void engineLoad(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      synchronized(this.entries) {
         MessageDigest var5 = null;
         CertificateFactory var6 = null;
         Hashtable var7 = null;
         ByteArrayInputStream var8 = null;
         Object var9 = null;
         if (var1 != null) {
            DataInputStream var4;
            if (var2 != null) {
               var5 = this.getPreKeyedHash(var2);
               var4 = new DataInputStream(new DigestInputStream(var1, var5));
            } else {
               var4 = new DataInputStream(var1);
            }

            int var10 = var4.readInt();
            int var11 = var4.readInt();
            if (var10 == -17957139 && (var11 == 1 || var11 == 2)) {
               if (var11 == 1) {
                  var6 = CertificateFactory.getInstance("X509");
               } else {
                  var7 = new Hashtable(3);
               }

               this.entries.clear();
               int var12 = var4.readInt();

               for(int var13 = 0; var13 < var12; ++var13) {
                  int var14 = var4.readInt();
                  String var15;
                  byte[] var23;
                  if (var14 != 1) {
                     if (var14 != 2) {
                        throw new IOException("Unrecognized keystore entry");
                     }

                     JavaKeyStore.TrustedCertEntry var27 = new JavaKeyStore.TrustedCertEntry();
                     var15 = var4.readUTF();
                     var27.date = new Date(var4.readLong());
                     if (var11 == 2) {
                        String var29 = var4.readUTF();
                        if (var7.containsKey(var29)) {
                           var6 = (CertificateFactory)var7.get(var29);
                        } else {
                           var6 = CertificateFactory.getInstance(var29);
                           var7.put(var29, var6);
                        }
                     }

                     var23 = IOUtils.readFully(var4, var4.readInt(), true);
                     var8 = new ByteArrayInputStream(var23);
                     var27.cert = var6.generateCertificate(var8);
                     var8.close();
                     this.entries.put(var15, var27);
                  } else {
                     JavaKeyStore.KeyEntry var16 = new JavaKeyStore.KeyEntry();
                     var15 = var4.readUTF();
                     var16.date = new Date(var4.readLong());
                     var16.protectedPrivKey = IOUtils.readFully(var4, var4.readInt(), true);
                     int var17 = var4.readInt();
                     if (var17 > 0) {
                        ArrayList var18 = new ArrayList(var17 > 10 ? 10 : var17);

                        for(int var19 = 0; var19 < var17; ++var19) {
                           if (var11 == 2) {
                              String var20 = var4.readUTF();
                              if (var7.containsKey(var20)) {
                                 var6 = (CertificateFactory)var7.get(var20);
                              } else {
                                 var6 = CertificateFactory.getInstance(var20);
                                 var7.put(var20, var6);
                              }
                           }

                           var23 = IOUtils.readFully(var4, var4.readInt(), true);
                           var8 = new ByteArrayInputStream(var23);
                           var18.add(var6.generateCertificate(var8));
                           var8.close();
                        }

                        var16.chain = (Certificate[])var18.toArray(new Certificate[var17]);
                     }

                     this.entries.put(var15, var16);
                  }
               }

               if (var2 != null) {
                  byte[] var24 = var5.digest();
                  byte[] var25 = new byte[var24.length];
                  var4.readFully(var25);

                  for(int var26 = 0; var26 < var24.length; ++var26) {
                     if (var24[var26] != var25[var26]) {
                        UnrecoverableKeyException var28 = new UnrecoverableKeyException("Password verification failed");
                        throw (IOException)(new IOException("Keystore was tampered with, or password was incorrect")).initCause(var28);
                     }
                  }
               }

            } else {
               throw new IOException("Invalid keystore format");
            }
         }
      }
   }

   private MessageDigest getPreKeyedHash(char[] var1) throws NoSuchAlgorithmException, UnsupportedEncodingException {
      MessageDigest var4 = MessageDigest.getInstance("SHA");
      byte[] var5 = new byte[var1.length * 2];
      int var2 = 0;

      for(int var3 = 0; var2 < var1.length; ++var2) {
         var5[var3++] = (byte)(var1[var2] >> 8);
         var5[var3++] = (byte)var1[var2];
      }

      var4.update(var5);

      for(var2 = 0; var2 < var5.length; ++var2) {
         var5[var2] = 0;
      }

      var4.update("Mighty Aphrodite".getBytes("UTF8"));
      return var4;
   }

   private static class TrustedCertEntry {
      Date date;
      Certificate cert;

      private TrustedCertEntry() {
      }

      // $FF: synthetic method
      TrustedCertEntry(Object var1) {
         this();
      }
   }

   private static class KeyEntry {
      Date date;
      byte[] protectedPrivKey;
      Certificate[] chain;

      private KeyEntry() {
      }

      // $FF: synthetic method
      KeyEntry(Object var1) {
         this();
      }
   }

   public static final class DualFormatJKS extends KeyStoreDelegator {
      public DualFormatJKS() {
         super("JKS", JavaKeyStore.JKS.class, "PKCS12", PKCS12KeyStore.class);
      }
   }

   public static final class CaseExactJKS extends JavaKeyStore {
      String convertAlias(String var1) {
         return var1;
      }
   }

   public static final class JKS extends JavaKeyStore {
      String convertAlias(String var1) {
         return var1.toLowerCase(Locale.ENGLISH);
      }
   }
}
