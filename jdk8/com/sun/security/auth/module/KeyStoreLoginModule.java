package com.sun.security.auth.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.AuthProvider;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;
import jdk.Exported;
import sun.security.util.Password;

@Exported
public class KeyStoreLoginModule implements LoginModule {
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private static final int UNINITIALIZED = 0;
   private static final int INITIALIZED = 1;
   private static final int AUTHENTICATED = 2;
   private static final int LOGGED_IN = 3;
   private static final int PROTECTED_PATH = 0;
   private static final int TOKEN = 1;
   private static final int NORMAL = 2;
   private static final String NONE = "NONE";
   private static final String P11KEYSTORE = "PKCS11";
   private static final TextOutputCallback bannerCallback;
   private final ConfirmationCallback confirmationCallback = new ConfirmationCallback(0, 2, 3);
   private Subject subject;
   private CallbackHandler callbackHandler;
   private Map<String, Object> sharedState;
   private Map<String, ?> options;
   private char[] keyStorePassword;
   private char[] privateKeyPassword;
   private KeyStore keyStore;
   private String keyStoreURL;
   private String keyStoreType;
   private String keyStoreProvider;
   private String keyStoreAlias;
   private String keyStorePasswordURL;
   private String privateKeyPasswordURL;
   private boolean debug;
   private X500Principal principal;
   private Certificate[] fromKeyStore;
   private CertPath certP = null;
   private X500PrivateCredential privateCredential;
   private int status = 0;
   private boolean nullStream = false;
   private boolean token = false;
   private boolean protectedPath = false;

   public void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4) {
      this.subject = var1;
      this.callbackHandler = var2;
      this.sharedState = var3;
      this.options = var4;
      this.processOptions();
      this.status = 1;
   }

   private void processOptions() {
      this.keyStoreURL = (String)this.options.get("keyStoreURL");
      if (this.keyStoreURL == null) {
         this.keyStoreURL = "file:" + System.getProperty("user.home").replace(File.separatorChar, '/') + '/' + ".keystore";
      } else if ("NONE".equals(this.keyStoreURL)) {
         this.nullStream = true;
      }

      this.keyStoreType = (String)this.options.get("keyStoreType");
      if (this.keyStoreType == null) {
         this.keyStoreType = KeyStore.getDefaultType();
      }

      if ("PKCS11".equalsIgnoreCase(this.keyStoreType)) {
         this.token = true;
      }

      this.keyStoreProvider = (String)this.options.get("keyStoreProvider");
      this.keyStoreAlias = (String)this.options.get("keyStoreAlias");
      this.keyStorePasswordURL = (String)this.options.get("keyStorePasswordURL");
      this.privateKeyPasswordURL = (String)this.options.get("privateKeyPasswordURL");
      this.protectedPath = "true".equalsIgnoreCase((String)this.options.get("protected"));
      this.debug = "true".equalsIgnoreCase((String)this.options.get("debug"));
      if (this.debug) {
         this.debugPrint((String)null);
         this.debugPrint("keyStoreURL=" + this.keyStoreURL);
         this.debugPrint("keyStoreType=" + this.keyStoreType);
         this.debugPrint("keyStoreProvider=" + this.keyStoreProvider);
         this.debugPrint("keyStoreAlias=" + this.keyStoreAlias);
         this.debugPrint("keyStorePasswordURL=" + this.keyStorePasswordURL);
         this.debugPrint("privateKeyPasswordURL=" + this.privateKeyPasswordURL);
         this.debugPrint("protectedPath=" + this.protectedPath);
         this.debugPrint((String)null);
      }

   }

   public boolean login() throws LoginException {
      switch(this.status) {
      case 0:
      default:
         throw new LoginException("The login module is not initialized");
      case 1:
      case 2:
         if (this.token && !this.nullStream) {
            throw new LoginException("if keyStoreType is PKCS11 then keyStoreURL must be NONE");
         } else if (this.token && this.privateKeyPasswordURL != null) {
            throw new LoginException("if keyStoreType is PKCS11 then privateKeyPasswordURL must not be specified");
         } else if (this.protectedPath && (this.keyStorePasswordURL != null || this.privateKeyPasswordURL != null)) {
            throw new LoginException("if protected is true then keyStorePasswordURL and privateKeyPasswordURL must not be specified");
         } else {
            if (this.protectedPath) {
               this.getAliasAndPasswords(0);
            } else if (this.token) {
               this.getAliasAndPasswords(1);
            } else {
               this.getAliasAndPasswords(2);
            }

            try {
               this.getKeyStoreInfo();
            } finally {
               if (this.privateKeyPassword != null && this.privateKeyPassword != this.keyStorePassword) {
                  Arrays.fill(this.privateKeyPassword, '\u0000');
                  this.privateKeyPassword = null;
               }

               if (this.keyStorePassword != null) {
                  Arrays.fill(this.keyStorePassword, '\u0000');
                  this.keyStorePassword = null;
               }

            }

            this.status = 2;
            return true;
         }
      case 3:
         return true;
      }
   }

   private void getAliasAndPasswords(int var1) throws LoginException {
      if (this.callbackHandler == null) {
         switch(var1) {
         case 0:
            this.checkAlias();
            break;
         case 1:
            this.checkAlias();
            this.checkStorePass();
            break;
         case 2:
            this.checkAlias();
            this.checkStorePass();
            this.checkKeyPass();
         }
      } else {
         NameCallback var2;
         if (this.keyStoreAlias != null && this.keyStoreAlias.length() != 0) {
            var2 = new NameCallback(rb.getString("Keystore.alias."), this.keyStoreAlias);
         } else {
            var2 = new NameCallback(rb.getString("Keystore.alias."));
         }

         PasswordCallback var3 = null;
         PasswordCallback var4 = null;
         switch(var1) {
         case 2:
            var4 = new PasswordCallback(rb.getString("Private.key.password.optional."), false);
         case 1:
            var3 = new PasswordCallback(rb.getString("Keystore.password."), false);
         case 0:
         default:
            this.prompt(var2, var3, var4);
         }
      }

      if (this.debug) {
         this.debugPrint("alias=" + this.keyStoreAlias);
      }

   }

   private void checkAlias() throws LoginException {
      if (this.keyStoreAlias == null) {
         throw new LoginException("Need to specify an alias option to use KeyStoreLoginModule non-interactively.");
      }
   }

   private void checkStorePass() throws LoginException {
      if (this.keyStorePasswordURL == null) {
         throw new LoginException("Need to specify keyStorePasswordURL option to use KeyStoreLoginModule non-interactively.");
      } else {
         InputStream var1 = null;
         boolean var11 = false;

         LoginException var3;
         try {
            var11 = true;
            var1 = (new URL(this.keyStorePasswordURL)).openStream();
            this.keyStorePassword = Password.readPassword(var1);
            var11 = false;
         } catch (IOException var14) {
            var3 = new LoginException("Problem accessing keystore password \"" + this.keyStorePasswordURL + "\"");
            var3.initCause(var14);
            throw var3;
         } finally {
            if (var11) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (IOException var12) {
                     LoginException var6 = new LoginException("Problem closing the keystore password stream");
                     var6.initCause(var12);
                     throw var6;
                  }
               }

            }
         }

         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var13) {
               var3 = new LoginException("Problem closing the keystore password stream");
               var3.initCause(var13);
               throw var3;
            }
         }

      }
   }

   private void checkKeyPass() throws LoginException {
      if (this.privateKeyPasswordURL == null) {
         this.privateKeyPassword = this.keyStorePassword;
      } else {
         InputStream var1 = null;
         boolean var11 = false;

         LoginException var3;
         try {
            var11 = true;
            var1 = (new URL(this.privateKeyPasswordURL)).openStream();
            this.privateKeyPassword = Password.readPassword(var1);
            var11 = false;
         } catch (IOException var14) {
            var3 = new LoginException("Problem accessing private key password \"" + this.privateKeyPasswordURL + "\"");
            var3.initCause(var14);
            throw var3;
         } finally {
            if (var11) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (IOException var12) {
                     LoginException var6 = new LoginException("Problem closing the private key password stream");
                     var6.initCause(var12);
                     throw var6;
                  }
               }

            }
         }

         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var13) {
               var3 = new LoginException("Problem closing the private key password stream");
               var3.initCause(var13);
               throw var3;
            }
         }
      }

   }

   private void prompt(NameCallback var1, PasswordCallback var2, PasswordCallback var3) throws LoginException {
      int var4;
      LoginException var5;
      if (var2 == null) {
         try {
            this.callbackHandler.handle(new Callback[]{bannerCallback, var1, this.confirmationCallback});
         } catch (IOException var10) {
            var5 = new LoginException("Problem retrieving keystore alias");
            var5.initCause(var10);
            throw var5;
         } catch (UnsupportedCallbackException var11) {
            throw new LoginException("Error: " + var11.getCallback().toString() + " is not available to retrieve authentication  information from the user");
         }

         var4 = this.confirmationCallback.getSelectedIndex();
         if (var4 == 2) {
            throw new LoginException("Login cancelled");
         }

         this.saveAlias(var1);
      } else if (var3 == null) {
         try {
            this.callbackHandler.handle(new Callback[]{bannerCallback, var1, var2, this.confirmationCallback});
         } catch (IOException var8) {
            var5 = new LoginException("Problem retrieving keystore alias and password");
            var5.initCause(var8);
            throw var5;
         } catch (UnsupportedCallbackException var9) {
            throw new LoginException("Error: " + var9.getCallback().toString() + " is not available to retrieve authentication  information from the user");
         }

         var4 = this.confirmationCallback.getSelectedIndex();
         if (var4 == 2) {
            throw new LoginException("Login cancelled");
         }

         this.saveAlias(var1);
         this.saveStorePass(var2);
      } else {
         try {
            this.callbackHandler.handle(new Callback[]{bannerCallback, var1, var2, var3, this.confirmationCallback});
         } catch (IOException var6) {
            var5 = new LoginException("Problem retrieving keystore alias and passwords");
            var5.initCause(var6);
            throw var5;
         } catch (UnsupportedCallbackException var7) {
            throw new LoginException("Error: " + var7.getCallback().toString() + " is not available to retrieve authentication  information from the user");
         }

         var4 = this.confirmationCallback.getSelectedIndex();
         if (var4 == 2) {
            throw new LoginException("Login cancelled");
         }

         this.saveAlias(var1);
         this.saveStorePass(var2);
         this.saveKeyPass(var3);
      }

   }

   private void saveAlias(NameCallback var1) {
      this.keyStoreAlias = var1.getName();
   }

   private void saveStorePass(PasswordCallback var1) {
      this.keyStorePassword = var1.getPassword();
      if (this.keyStorePassword == null) {
         this.keyStorePassword = new char[0];
      }

      var1.clearPassword();
   }

   private void saveKeyPass(PasswordCallback var1) {
      this.privateKeyPassword = var1.getPassword();
      if (this.privateKeyPassword == null || this.privateKeyPassword.length == 0) {
         this.privateKeyPassword = this.keyStorePassword;
      }

      var1.clearPassword();
   }

   private void getKeyStoreInfo() throws LoginException {
      LoginException var2;
      try {
         if (this.keyStoreProvider == null) {
            this.keyStore = KeyStore.getInstance(this.keyStoreType);
         } else {
            this.keyStore = KeyStore.getInstance(this.keyStoreType, this.keyStoreProvider);
         }
      } catch (KeyStoreException var26) {
         var2 = new LoginException("The specified keystore type was not available");
         var2.initCause(var26);
         throw var2;
      } catch (NoSuchProviderException var27) {
         var2 = new LoginException("The specified keystore provider was not available");
         var2.initCause(var27);
         throw var2;
      }

      InputStream var1 = null;
      boolean var20 = false;

      LoginException var3;
      try {
         var20 = true;
         if (this.nullStream) {
            this.keyStore.load((InputStream)null, this.keyStorePassword);
            var20 = false;
         } else {
            var1 = (new URL(this.keyStoreURL)).openStream();
            this.keyStore.load(var1, this.keyStorePassword);
            var20 = false;
         }
      } catch (MalformedURLException var23) {
         var3 = new LoginException("Incorrect keyStoreURL option");
         var3.initCause(var23);
         throw var3;
      } catch (GeneralSecurityException var24) {
         var3 = new LoginException("Error initializing keystore");
         var3.initCause(var24);
         throw var3;
      } catch (IOException var25) {
         var3 = new LoginException("Error initializing keystore");
         var3.initCause(var25);
         throw var3;
      } finally {
         if (var20) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (IOException var21) {
                  LoginException var6 = new LoginException("Error initializing keystore");
                  var6.initCause(var21);
                  throw var6;
               }
            }

         }
      }

      if (var1 != null) {
         try {
            var1.close();
         } catch (IOException var22) {
            var3 = new LoginException("Error initializing keystore");
            var3.initCause(var22);
            throw var3;
         }
      }

      try {
         this.fromKeyStore = this.keyStore.getCertificateChain(this.keyStoreAlias);
         if (this.fromKeyStore == null || this.fromKeyStore.length == 0 || !(this.fromKeyStore[0] instanceof X509Certificate)) {
            throw new FailedLoginException("Unable to find X.509 certificate chain in keystore");
         }

         LinkedList var34 = new LinkedList();
         int var36 = 0;

         while(true) {
            if (var36 >= this.fromKeyStore.length) {
               CertificateFactory var37 = CertificateFactory.getInstance("X.509");
               this.certP = var37.generateCertPath((List)var34);
               break;
            }

            var34.add(this.fromKeyStore[var36]);
            ++var36;
         }
      } catch (KeyStoreException var32) {
         var3 = new LoginException("Error using keystore");
         var3.initCause(var32);
         throw var3;
      } catch (CertificateException var33) {
         var3 = new LoginException("Error: X.509 Certificate type unavailable");
         var3.initCause(var33);
         throw var3;
      }

      try {
         X509Certificate var35 = (X509Certificate)this.fromKeyStore[0];
         this.principal = new X500Principal(var35.getSubjectDN().getName());
         Key var39 = this.keyStore.getKey(this.keyStoreAlias, this.privateKeyPassword);
         if (var39 == null || !(var39 instanceof PrivateKey)) {
            throw new FailedLoginException("Unable to recover key from keystore");
         }

         this.privateCredential = new X500PrivateCredential(var35, (PrivateKey)var39, this.keyStoreAlias);
      } catch (KeyStoreException var29) {
         var3 = new LoginException("Error using keystore");
         var3.initCause(var29);
         throw var3;
      } catch (NoSuchAlgorithmException var30) {
         var3 = new LoginException("Error using keystore");
         var3.initCause(var30);
         throw var3;
      } catch (UnrecoverableKeyException var31) {
         FailedLoginException var38 = new FailedLoginException("Unable to recover key from keystore");
         var38.initCause(var31);
         throw var38;
      }

      if (this.debug) {
         this.debugPrint("principal=" + this.principal + "\n certificate=" + this.privateCredential.getCertificate() + "\n alias =" + this.privateCredential.getAlias());
      }

   }

   public boolean commit() throws LoginException {
      switch(this.status) {
      case 0:
      default:
         throw new LoginException("The login module is not initialized");
      case 1:
         this.logoutInternal();
         throw new LoginException("Authentication failed");
      case 2:
         if (this.commitInternal()) {
            return true;
         }

         this.logoutInternal();
         throw new LoginException("Unable to retrieve certificates");
      case 3:
         return true;
      }
   }

   private boolean commitInternal() throws LoginException {
      if (this.subject.isReadOnly()) {
         throw new LoginException("Subject is set readonly");
      } else {
         this.subject.getPrincipals().add(this.principal);
         this.subject.getPublicCredentials().add(this.certP);
         this.subject.getPrivateCredentials().add(this.privateCredential);
         this.status = 3;
         return true;
      }
   }

   public boolean abort() throws LoginException {
      switch(this.status) {
      case 0:
      default:
         return false;
      case 1:
         return false;
      case 2:
         this.logoutInternal();
         return true;
      case 3:
         this.logoutInternal();
         return true;
      }
   }

   public boolean logout() throws LoginException {
      if (this.debug) {
         this.debugPrint("Entering logout " + this.status);
      }

      switch(this.status) {
      case 0:
         throw new LoginException("The login module is not initialized");
      case 1:
      case 2:
      default:
         return false;
      case 3:
         this.logoutInternal();
         return true;
      }
   }

   private void logoutInternal() throws LoginException {
      if (this.debug) {
         this.debugPrint("Entering logoutInternal");
      }

      LoginException var1 = null;
      Provider var2 = this.keyStore.getProvider();
      if (var2 instanceof AuthProvider) {
         AuthProvider var3 = (AuthProvider)var2;

         try {
            var3.logout();
            if (this.debug) {
               this.debugPrint("logged out of KeyStore AuthProvider");
            }
         } catch (LoginException var8) {
            var1 = var8;
         }
      }

      if (!this.subject.isReadOnly()) {
         if (this.principal != null) {
            this.subject.getPrincipals().remove(this.principal);
            this.principal = null;
         }

         if (this.certP != null) {
            this.subject.getPublicCredentials().remove(this.certP);
            this.certP = null;
         }

         if (this.privateCredential != null) {
            this.subject.getPrivateCredentials().remove(this.privateCredential);
            this.privateCredential = null;
         }

         if (var1 != null) {
            throw var1;
         } else {
            this.status = 1;
         }
      } else {
         this.principal = null;
         this.certP = null;
         this.status = 1;
         Iterator var9 = this.subject.getPrivateCredentials().iterator();

         while(var9.hasNext()) {
            Object var4 = var9.next();
            if (this.privateCredential.equals(var4)) {
               this.privateCredential = null;

               try {
                  ((Destroyable)var4).destroy();
                  if (this.debug) {
                     this.debugPrint("Destroyed private credential, " + var4.getClass().getName());
                  }
                  break;
               } catch (DestroyFailedException var7) {
                  LoginException var6 = new LoginException("Unable to destroy private credential, " + var4.getClass().getName());
                  var6.initCause(var7);
                  throw var6;
               }
            }
         }

         throw new LoginException("Unable to remove Principal (X500Principal ) and public credential (certificatepath) from read-only Subject");
      }
   }

   private void debugPrint(String var1) {
      if (var1 == null) {
         System.err.println();
      } else {
         System.err.println("Debug KeyStoreLoginModule: " + var1);
      }

   }

   static {
      bannerCallback = new TextOutputCallback(0, rb.getString("Please.enter.keystore.information"));
   }
}
