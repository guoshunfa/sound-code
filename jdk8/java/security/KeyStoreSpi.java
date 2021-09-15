package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import javax.crypto.SecretKey;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public abstract class KeyStoreSpi {
   public abstract Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException;

   public abstract java.security.cert.Certificate[] engineGetCertificateChain(String var1);

   public abstract java.security.cert.Certificate engineGetCertificate(String var1);

   public abstract Date engineGetCreationDate(String var1);

   public abstract void engineSetKeyEntry(String var1, Key var2, char[] var3, java.security.cert.Certificate[] var4) throws KeyStoreException;

   public abstract void engineSetKeyEntry(String var1, byte[] var2, java.security.cert.Certificate[] var3) throws KeyStoreException;

   public abstract void engineSetCertificateEntry(String var1, java.security.cert.Certificate var2) throws KeyStoreException;

   public abstract void engineDeleteEntry(String var1) throws KeyStoreException;

   public abstract Enumeration<String> engineAliases();

   public abstract boolean engineContainsAlias(String var1);

   public abstract int engineSize();

   public abstract boolean engineIsKeyEntry(String var1);

   public abstract boolean engineIsCertificateEntry(String var1);

   public abstract String engineGetCertificateAlias(java.security.cert.Certificate var1);

   public abstract void engineStore(OutputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException;

   public void engineStore(KeyStore.LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      throw new UnsupportedOperationException();
   }

   public abstract void engineLoad(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException;

   public void engineLoad(KeyStore.LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (var1 == null) {
         this.engineLoad((InputStream)null, (char[])null);
      } else if (var1 instanceof KeyStore.SimpleLoadStoreParameter) {
         KeyStore.ProtectionParameter var2 = var1.getProtectionParameter();
         char[] var3;
         if (var2 instanceof KeyStore.PasswordProtection) {
            var3 = ((KeyStore.PasswordProtection)var2).getPassword();
         } else {
            if (!(var2 instanceof KeyStore.CallbackHandlerProtection)) {
               throw new NoSuchAlgorithmException("ProtectionParameter must be PasswordProtection or CallbackHandlerProtection");
            }

            CallbackHandler var4 = ((KeyStore.CallbackHandlerProtection)var2).getCallbackHandler();
            PasswordCallback var5 = new PasswordCallback("Password: ", false);

            try {
               var4.handle(new Callback[]{var5});
            } catch (UnsupportedCallbackException var7) {
               throw new NoSuchAlgorithmException("Could not obtain password", var7);
            }

            var3 = var5.getPassword();
            var5.clearPassword();
            if (var3 == null) {
               throw new NoSuchAlgorithmException("No password provided");
            }
         }

         this.engineLoad((InputStream)null, var3);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public KeyStore.Entry engineGetEntry(String var1, KeyStore.ProtectionParameter var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
      if (!this.engineContainsAlias(var1)) {
         return null;
      } else if (var2 == null) {
         if (this.engineIsCertificateEntry(var1)) {
            return new KeyStore.TrustedCertificateEntry(this.engineGetCertificate(var1));
         } else {
            throw new UnrecoverableKeyException("requested entry requires a password");
         }
      } else {
         if (var2 instanceof KeyStore.PasswordProtection) {
            if (this.engineIsCertificateEntry(var1)) {
               throw new UnsupportedOperationException("trusted certificate entries are not password-protected");
            }

            if (this.engineIsKeyEntry(var1)) {
               KeyStore.PasswordProtection var3 = (KeyStore.PasswordProtection)var2;
               char[] var4 = var3.getPassword();
               Key var5 = this.engineGetKey(var1, var4);
               if (var5 instanceof PrivateKey) {
                  java.security.cert.Certificate[] var6 = this.engineGetCertificateChain(var1);
                  return new KeyStore.PrivateKeyEntry((PrivateKey)var5, var6);
               }

               if (var5 instanceof SecretKey) {
                  return new KeyStore.SecretKeyEntry((SecretKey)var5);
               }
            }
         }

         throw new UnsupportedOperationException();
      }
   }

   public void engineSetEntry(String var1, KeyStore.Entry var2, KeyStore.ProtectionParameter var3) throws KeyStoreException {
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
               KeyStore.TrustedCertificateEntry var5 = (KeyStore.TrustedCertificateEntry)var2;
               this.engineSetCertificateEntry(var1, var5.getTrustedCertificate());
            }
         } else if (var2 instanceof KeyStore.PrivateKeyEntry) {
            if (var4 != null && var4.getPassword() != null) {
               this.engineSetKeyEntry(var1, ((KeyStore.PrivateKeyEntry)var2).getPrivateKey(), var4.getPassword(), ((KeyStore.PrivateKeyEntry)var2).getCertificateChain());
            } else {
               throw new KeyStoreException("non-null password required to create PrivateKeyEntry");
            }
         } else if (var2 instanceof KeyStore.SecretKeyEntry) {
            if (var4 != null && var4.getPassword() != null) {
               this.engineSetKeyEntry(var1, ((KeyStore.SecretKeyEntry)var2).getSecretKey(), var4.getPassword(), (java.security.cert.Certificate[])null);
            } else {
               throw new KeyStoreException("non-null password required to create SecretKeyEntry");
            }
         } else {
            throw new KeyStoreException("unsupported entry type: " + var2.getClass().getName());
         }
      }
   }

   public boolean engineEntryInstanceOf(String var1, Class<? extends KeyStore.Entry> var2) {
      if (var2 == KeyStore.TrustedCertificateEntry.class) {
         return this.engineIsCertificateEntry(var1);
      } else if (var2 == KeyStore.PrivateKeyEntry.class) {
         return this.engineIsKeyEntry(var1) && this.engineGetCertificate(var1) != null;
      } else if (var2 != KeyStore.SecretKeyEntry.class) {
         return false;
      } else {
         return this.engineIsKeyEntry(var1) && this.engineGetCertificate(var1) == null;
      }
   }
}
