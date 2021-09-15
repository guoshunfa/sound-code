package java.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import sun.security.util.Debug;

public class KeyStore {
   private static final Debug pdebug = Debug.getInstance("provider", "Provider");
   private static final boolean skipDebug = Debug.isOn("engine=") && !Debug.isOn("keystore");
   private static final String KEYSTORE_TYPE = "keystore.type";
   private String type;
   private Provider provider;
   private KeyStoreSpi keyStoreSpi;
   private boolean initialized = false;

   protected KeyStore(KeyStoreSpi var1, Provider var2, String var3) {
      this.keyStoreSpi = var1;
      this.provider = var2;
      this.type = var3;
      if (!skipDebug && pdebug != null) {
         pdebug.println("KeyStore." + var3.toUpperCase() + " type from: " + this.provider.getName());
      }

   }

   public static KeyStore getInstance(String var0) throws KeyStoreException {
      try {
         Object[] var1 = Security.getImpl(var0, "KeyStore", (String)null);
         return new KeyStore((KeyStoreSpi)var1[0], (Provider)var1[1], var0);
      } catch (NoSuchAlgorithmException var2) {
         throw new KeyStoreException(var0 + " not found", var2);
      } catch (NoSuchProviderException var3) {
         throw new KeyStoreException(var0 + " not found", var3);
      }
   }

   public static KeyStore getInstance(String var0, String var1) throws KeyStoreException, NoSuchProviderException {
      if (var1 != null && var1.length() != 0) {
         try {
            Object[] var2 = Security.getImpl(var0, "KeyStore", var1);
            return new KeyStore((KeyStoreSpi)var2[0], (Provider)var2[1], var0);
         } catch (NoSuchAlgorithmException var3) {
            throw new KeyStoreException(var0 + " not found", var3);
         }
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static KeyStore getInstance(String var0, Provider var1) throws KeyStoreException {
      if (var1 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         try {
            Object[] var2 = Security.getImpl(var0, "KeyStore", var1);
            return new KeyStore((KeyStoreSpi)var2[0], (Provider)var2[1], var0);
         } catch (NoSuchAlgorithmException var3) {
            throw new KeyStoreException(var0 + " not found", var3);
         }
      }
   }

   public static final String getDefaultType() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("keystore.type");
         }
      });
      if (var0 == null) {
         var0 = "jks";
      }

      return var0;
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final String getType() {
      return this.type;
   }

   public final Key getKey(String var1, char[] var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineGetKey(var1, var2);
      }
   }

   public final java.security.cert.Certificate[] getCertificateChain(String var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineGetCertificateChain(var1);
      }
   }

   public final java.security.cert.Certificate getCertificate(String var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineGetCertificate(var1);
      }
   }

   public final Date getCreationDate(String var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineGetCreationDate(var1);
      }
   }

   public final void setKeyEntry(String var1, Key var2, char[] var3, java.security.cert.Certificate[] var4) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else if (!(var2 instanceof PrivateKey) || var4 != null && var4.length != 0) {
         this.keyStoreSpi.engineSetKeyEntry(var1, var2, var3, var4);
      } else {
         throw new IllegalArgumentException("Private key must be accompanied by certificate chain");
      }
   }

   public final void setKeyEntry(String var1, byte[] var2, java.security.cert.Certificate[] var3) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         this.keyStoreSpi.engineSetKeyEntry(var1, var2, var3);
      }
   }

   public final void setCertificateEntry(String var1, java.security.cert.Certificate var2) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         this.keyStoreSpi.engineSetCertificateEntry(var1, var2);
      }
   }

   public final void deleteEntry(String var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         this.keyStoreSpi.engineDeleteEntry(var1);
      }
   }

   public final Enumeration<String> aliases() throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineAliases();
      }
   }

   public final boolean containsAlias(String var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineContainsAlias(var1);
      }
   }

   public final int size() throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineSize();
      }
   }

   public final boolean isKeyEntry(String var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineIsKeyEntry(var1);
      }
   }

   public final boolean isCertificateEntry(String var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineIsCertificateEntry(var1);
      }
   }

   public final String getCertificateAlias(java.security.cert.Certificate var1) throws KeyStoreException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineGetCertificateAlias(var1);
      }
   }

   public final void store(OutputStream var1, char[] var2) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         this.keyStoreSpi.engineStore(var1, var2);
      }
   }

   public final void store(KeyStore.LoadStoreParameter var1) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
      if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         this.keyStoreSpi.engineStore(var1);
      }
   }

   public final void load(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      this.keyStoreSpi.engineLoad(var1, var2);
      this.initialized = true;
   }

   public final void load(KeyStore.LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      this.keyStoreSpi.engineLoad(var1);
      this.initialized = true;
   }

   public final KeyStore.Entry getEntry(String var1, KeyStore.ProtectionParameter var2) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
      if (var1 == null) {
         throw new NullPointerException("invalid null input");
      } else if (!this.initialized) {
         throw new KeyStoreException("Uninitialized keystore");
      } else {
         return this.keyStoreSpi.engineGetEntry(var1, var2);
      }
   }

   public final void setEntry(String var1, KeyStore.Entry var2, KeyStore.ProtectionParameter var3) throws KeyStoreException {
      if (var1 != null && var2 != null) {
         if (!this.initialized) {
            throw new KeyStoreException("Uninitialized keystore");
         } else {
            this.keyStoreSpi.engineSetEntry(var1, var2, var3);
         }
      } else {
         throw new NullPointerException("invalid null input");
      }
   }

   public final boolean entryInstanceOf(String var1, Class<? extends KeyStore.Entry> var2) throws KeyStoreException {
      if (var1 != null && var2 != null) {
         if (!this.initialized) {
            throw new KeyStoreException("Uninitialized keystore");
         } else {
            return this.keyStoreSpi.engineEntryInstanceOf(var1, var2);
         }
      } else {
         throw new NullPointerException("invalid null input");
      }
   }

   static class SimpleLoadStoreParameter implements KeyStore.LoadStoreParameter {
      private final KeyStore.ProtectionParameter protection;

      SimpleLoadStoreParameter(KeyStore.ProtectionParameter var1) {
         this.protection = var1;
      }

      public KeyStore.ProtectionParameter getProtectionParameter() {
         return this.protection;
      }
   }

   public abstract static class Builder {
      static final int MAX_CALLBACK_TRIES = 3;

      protected Builder() {
      }

      public abstract KeyStore getKeyStore() throws KeyStoreException;

      public abstract KeyStore.ProtectionParameter getProtectionParameter(String var1) throws KeyStoreException;

      public static KeyStore.Builder newInstance(final KeyStore var0, final KeyStore.ProtectionParameter var1) {
         if (var0 != null && var1 != null) {
            if (!var0.initialized) {
               throw new IllegalArgumentException("KeyStore not initialized");
            } else {
               return new KeyStore.Builder() {
                  private volatile boolean getCalled;

                  public KeyStore getKeyStore() {
                     this.getCalled = true;
                     return var0;
                  }

                  public KeyStore.ProtectionParameter getProtectionParameter(String var1x) {
                     if (var1x == null) {
                        throw new NullPointerException();
                     } else if (!this.getCalled) {
                        throw new IllegalStateException("getKeyStore() must be called first");
                     } else {
                        return var1;
                     }
                  }
               };
            }
         } else {
            throw new NullPointerException();
         }
      }

      public static KeyStore.Builder newInstance(String var0, Provider var1, File var2, KeyStore.ProtectionParameter var3) {
         if (var0 != null && var2 != null && var3 != null) {
            if (!(var3 instanceof KeyStore.PasswordProtection) && !(var3 instanceof KeyStore.CallbackHandlerProtection)) {
               throw new IllegalArgumentException("Protection must be PasswordProtection or CallbackHandlerProtection");
            } else if (!var2.isFile()) {
               throw new IllegalArgumentException("File does not exist or it does not refer to a normal file: " + var2);
            } else {
               return new KeyStore.Builder.FileBuilder(var0, var1, var2, var3, AccessController.getContext());
            }
         } else {
            throw new NullPointerException();
         }
      }

      public static KeyStore.Builder newInstance(final String var0, final Provider var1, final KeyStore.ProtectionParameter var2) {
         if (var0 != null && var2 != null) {
            final AccessControlContext var3 = AccessController.getContext();
            return new KeyStore.Builder() {
               private volatile boolean getCalled;
               private IOException oldException;
               private final PrivilegedExceptionAction<KeyStore> action = new PrivilegedExceptionAction<KeyStore>() {
                  public KeyStore run() throws Exception {
                     KeyStore var1x;
                     if (var1 == null) {
                        var1x = KeyStore.getInstance(var0);
                     } else {
                        var1x = KeyStore.getInstance(var0, var1);
                     }

                     KeyStore.SimpleLoadStoreParameter var2x = new KeyStore.SimpleLoadStoreParameter(var2);
                     if (!(var2 instanceof KeyStore.CallbackHandlerProtection)) {
                        var1x.load(var2x);
                     } else {
                        int var3x = 0;

                        while(true) {
                           ++var3x;

                           try {
                              var1x.load(var2x);
                              break;
                           } catch (IOException var5) {
                              if (var5.getCause() instanceof UnrecoverableKeyException) {
                                 if (var3x < 3) {
                                    continue;
                                 }

                                 oldException = var5;
                              }

                              throw var5;
                           }
                        }
                     }

                     getCalled = true;
                     return var1x;
                  }
               };

               public synchronized KeyStore getKeyStore() throws KeyStoreException {
                  if (this.oldException != null) {
                     throw new KeyStoreException("Previous KeyStore instantiation failed", this.oldException);
                  } else {
                     try {
                        return (KeyStore)AccessController.doPrivileged(this.action, var3);
                     } catch (PrivilegedActionException var3x) {
                        Throwable var2x = var3x.getCause();
                        throw new KeyStoreException("KeyStore instantiation failed", var2x);
                     }
                  }
               }

               public KeyStore.ProtectionParameter getProtectionParameter(String var1x) {
                  if (var1x == null) {
                     throw new NullPointerException();
                  } else if (!this.getCalled) {
                     throw new IllegalStateException("getKeyStore() must be called first");
                  } else {
                     return var2;
                  }
               }
            };
         } else {
            throw new NullPointerException();
         }
      }

      private static final class FileBuilder extends KeyStore.Builder {
         private final String type;
         private final Provider provider;
         private final File file;
         private KeyStore.ProtectionParameter protection;
         private KeyStore.ProtectionParameter keyProtection;
         private final AccessControlContext context;
         private KeyStore keyStore;
         private Throwable oldException;

         FileBuilder(String var1, Provider var2, File var3, KeyStore.ProtectionParameter var4, AccessControlContext var5) {
            this.type = var1;
            this.provider = var2;
            this.file = var3;
            this.protection = var4;
            this.context = var5;
         }

         public synchronized KeyStore getKeyStore() throws KeyStoreException {
            if (this.keyStore != null) {
               return this.keyStore;
            } else if (this.oldException != null) {
               throw new KeyStoreException("Previous KeyStore instantiation failed", this.oldException);
            } else {
               PrivilegedExceptionAction var1 = new PrivilegedExceptionAction<KeyStore>() {
                  public KeyStore run() throws Exception {
                     if (!(FileBuilder.this.protection instanceof KeyStore.CallbackHandlerProtection)) {
                        return this.run0();
                     } else {
                        int var1 = 0;

                        while(true) {
                           ++var1;

                           try {
                              return this.run0();
                           } catch (IOException var3) {
                              if (var1 >= 3 || !(var3.getCause() instanceof UnrecoverableKeyException)) {
                                 throw var3;
                              }
                           }
                        }
                     }
                  }

                  public KeyStore run0() throws Exception {
                     KeyStore var1;
                     if (FileBuilder.this.provider == null) {
                        var1 = KeyStore.getInstance(FileBuilder.this.type);
                     } else {
                        var1 = KeyStore.getInstance(FileBuilder.this.type, FileBuilder.this.provider);
                     }

                     FileInputStream var2 = null;
                     Object var3 = null;

                     KeyStore var10;
                     try {
                        var2 = new FileInputStream(FileBuilder.this.file);
                        char[] var9;
                        if (FileBuilder.this.protection instanceof KeyStore.PasswordProtection) {
                           var9 = ((KeyStore.PasswordProtection)FileBuilder.this.protection).getPassword();
                           FileBuilder.this.keyProtection = FileBuilder.this.protection;
                        } else {
                           CallbackHandler var4 = ((KeyStore.CallbackHandlerProtection)FileBuilder.this.protection).getCallbackHandler();
                           PasswordCallback var5 = new PasswordCallback("Password for keystore " + FileBuilder.this.file.getName(), false);
                           var4.handle(new Callback[]{var5});
                           var9 = var5.getPassword();
                           if (var9 == null) {
                              throw new KeyStoreException("No password provided");
                           }

                           var5.clearPassword();
                           FileBuilder.this.keyProtection = new KeyStore.PasswordProtection(var9);
                        }

                        var1.load(var2, var9);
                        var10 = var1;
                     } finally {
                        if (var2 != null) {
                           var2.close();
                        }

                     }

                     return var10;
                  }
               };

               try {
                  this.keyStore = (KeyStore)AccessController.doPrivileged(var1, this.context);
                  return this.keyStore;
               } catch (PrivilegedActionException var3) {
                  this.oldException = var3.getCause();
                  throw new KeyStoreException("KeyStore instantiation failed", this.oldException);
               }
            }
         }

         public synchronized KeyStore.ProtectionParameter getProtectionParameter(String var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else if (this.keyStore == null) {
               throw new IllegalStateException("getKeyStore() must be called first");
            } else {
               return this.keyProtection;
            }
         }
      }
   }

   public static final class TrustedCertificateEntry implements KeyStore.Entry {
      private final java.security.cert.Certificate cert;
      private final Set<KeyStore.Entry.Attribute> attributes;

      public TrustedCertificateEntry(java.security.cert.Certificate var1) {
         if (var1 == null) {
            throw new NullPointerException("invalid null input");
         } else {
            this.cert = var1;
            this.attributes = Collections.emptySet();
         }
      }

      public TrustedCertificateEntry(java.security.cert.Certificate var1, Set<KeyStore.Entry.Attribute> var2) {
         if (var1 != null && var2 != null) {
            this.cert = var1;
            this.attributes = Collections.unmodifiableSet(new HashSet(var2));
         } else {
            throw new NullPointerException("invalid null input");
         }
      }

      public java.security.cert.Certificate getTrustedCertificate() {
         return this.cert;
      }

      public Set<KeyStore.Entry.Attribute> getAttributes() {
         return this.attributes;
      }

      public String toString() {
         return "Trusted certificate entry:\r\n" + this.cert.toString();
      }
   }

   public static final class SecretKeyEntry implements KeyStore.Entry {
      private final SecretKey sKey;
      private final Set<KeyStore.Entry.Attribute> attributes;

      public SecretKeyEntry(SecretKey var1) {
         if (var1 == null) {
            throw new NullPointerException("invalid null input");
         } else {
            this.sKey = var1;
            this.attributes = Collections.emptySet();
         }
      }

      public SecretKeyEntry(SecretKey var1, Set<KeyStore.Entry.Attribute> var2) {
         if (var1 != null && var2 != null) {
            this.sKey = var1;
            this.attributes = Collections.unmodifiableSet(new HashSet(var2));
         } else {
            throw new NullPointerException("invalid null input");
         }
      }

      public SecretKey getSecretKey() {
         return this.sKey;
      }

      public Set<KeyStore.Entry.Attribute> getAttributes() {
         return this.attributes;
      }

      public String toString() {
         return "Secret key entry with algorithm " + this.sKey.getAlgorithm();
      }
   }

   public static final class PrivateKeyEntry implements KeyStore.Entry {
      private final PrivateKey privKey;
      private final java.security.cert.Certificate[] chain;
      private final Set<KeyStore.Entry.Attribute> attributes;

      public PrivateKeyEntry(PrivateKey var1, java.security.cert.Certificate[] var2) {
         this(var1, var2, Collections.emptySet());
      }

      public PrivateKeyEntry(PrivateKey var1, java.security.cert.Certificate[] var2, Set<KeyStore.Entry.Attribute> var3) {
         if (var1 != null && var2 != null && var3 != null) {
            if (var2.length == 0) {
               throw new IllegalArgumentException("invalid zero-length input chain");
            } else {
               java.security.cert.Certificate[] var4 = (java.security.cert.Certificate[])var2.clone();
               String var5 = var4[0].getType();

               for(int var6 = 1; var6 < var4.length; ++var6) {
                  if (!var5.equals(var4[var6].getType())) {
                     throw new IllegalArgumentException("chain does not contain certificates of the same type");
                  }
               }

               if (!var1.getAlgorithm().equals(var4[0].getPublicKey().getAlgorithm())) {
                  throw new IllegalArgumentException("private key algorithm does not match algorithm of public key in end entity certificate (at index 0)");
               } else {
                  this.privKey = var1;
                  if (var4[0] instanceof X509Certificate && !(var4 instanceof X509Certificate[])) {
                     this.chain = new X509Certificate[var4.length];
                     System.arraycopy(var4, 0, this.chain, 0, var4.length);
                  } else {
                     this.chain = var4;
                  }

                  this.attributes = Collections.unmodifiableSet(new HashSet(var3));
               }
            }
         } else {
            throw new NullPointerException("invalid null input");
         }
      }

      public PrivateKey getPrivateKey() {
         return this.privKey;
      }

      public java.security.cert.Certificate[] getCertificateChain() {
         return (java.security.cert.Certificate[])this.chain.clone();
      }

      public java.security.cert.Certificate getCertificate() {
         return this.chain[0];
      }

      public Set<KeyStore.Entry.Attribute> getAttributes() {
         return this.attributes;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("Private key entry and certificate chain with " + this.chain.length + " elements:\r\n");
         java.security.cert.Certificate[] var2 = this.chain;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            java.security.cert.Certificate var5 = var2[var4];
            var1.append((Object)var5);
            var1.append("\r\n");
         }

         return var1.toString();
      }
   }

   public interface Entry {
      default Set<KeyStore.Entry.Attribute> getAttributes() {
         return Collections.emptySet();
      }

      public interface Attribute {
         String getName();

         String getValue();
      }
   }

   public static class CallbackHandlerProtection implements KeyStore.ProtectionParameter {
      private final CallbackHandler handler;

      public CallbackHandlerProtection(CallbackHandler var1) {
         if (var1 == null) {
            throw new NullPointerException("handler must not be null");
         } else {
            this.handler = var1;
         }
      }

      public CallbackHandler getCallbackHandler() {
         return this.handler;
      }
   }

   public static class PasswordProtection implements KeyStore.ProtectionParameter, Destroyable {
      private final char[] password;
      private final String protectionAlgorithm;
      private final AlgorithmParameterSpec protectionParameters;
      private volatile boolean destroyed = false;

      public PasswordProtection(char[] var1) {
         this.password = var1 == null ? null : (char[])var1.clone();
         this.protectionAlgorithm = null;
         this.protectionParameters = null;
      }

      public PasswordProtection(char[] var1, String var2, AlgorithmParameterSpec var3) {
         if (var2 == null) {
            throw new NullPointerException("invalid null input");
         } else {
            this.password = var1 == null ? null : (char[])var1.clone();
            this.protectionAlgorithm = var2;
            this.protectionParameters = var3;
         }
      }

      public String getProtectionAlgorithm() {
         return this.protectionAlgorithm;
      }

      public AlgorithmParameterSpec getProtectionParameters() {
         return this.protectionParameters;
      }

      public synchronized char[] getPassword() {
         if (this.destroyed) {
            throw new IllegalStateException("password has been cleared");
         } else {
            return this.password;
         }
      }

      public synchronized void destroy() throws DestroyFailedException {
         this.destroyed = true;
         if (this.password != null) {
            Arrays.fill(this.password, ' ');
         }

      }

      public synchronized boolean isDestroyed() {
         return this.destroyed;
      }
   }

   public interface ProtectionParameter {
   }

   public interface LoadStoreParameter {
      KeyStore.ProtectionParameter getProtectionParameter();
   }
}
