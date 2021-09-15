package com.sun.security.auth.module;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;
import sun.misc.HexDumpEncoder;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.Config;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbAsReqBuilder;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

@Exported
public class Krb5LoginModule implements LoginModule {
   private Subject subject;
   private CallbackHandler callbackHandler;
   private Map<String, Object> sharedState;
   private Map<String, ?> options;
   private boolean debug = false;
   private boolean storeKey = false;
   private boolean doNotPrompt = false;
   private boolean useTicketCache = false;
   private boolean useKeyTab = false;
   private String ticketCacheName = null;
   private String keyTabName = null;
   private String princName = null;
   private boolean useFirstPass = false;
   private boolean tryFirstPass = false;
   private boolean storePass = false;
   private boolean clearPass = false;
   private boolean refreshKrb5Config = false;
   private boolean renewTGT = false;
   private boolean isInitiator = true;
   private boolean succeeded = false;
   private boolean commitSucceeded = false;
   private String username;
   private EncryptionKey[] encKeys = null;
   KeyTab ktab = null;
   private Credentials cred = null;
   private PrincipalName principal = null;
   private KerberosPrincipal kerbClientPrinc = null;
   private KerberosTicket kerbTicket = null;
   private KerberosKey[] kerbKeys = null;
   private StringBuffer krb5PrincName = null;
   private boolean unboundServer = false;
   private char[] password = null;
   private static final String NAME = "javax.security.auth.login.name";
   private static final String PWD = "javax.security.auth.login.password";
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });

   public void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4) {
      this.subject = var1;
      this.callbackHandler = var2;
      this.sharedState = var3;
      this.options = var4;
      this.debug = "true".equalsIgnoreCase((String)var4.get("debug"));
      this.storeKey = "true".equalsIgnoreCase((String)var4.get("storeKey"));
      this.doNotPrompt = "true".equalsIgnoreCase((String)var4.get("doNotPrompt"));
      this.useTicketCache = "true".equalsIgnoreCase((String)var4.get("useTicketCache"));
      this.useKeyTab = "true".equalsIgnoreCase((String)var4.get("useKeyTab"));
      this.ticketCacheName = (String)var4.get("ticketCache");
      this.keyTabName = (String)var4.get("keyTab");
      if (this.keyTabName != null) {
         this.keyTabName = sun.security.krb5.internal.ktab.KeyTab.normalize(this.keyTabName);
      }

      this.princName = (String)var4.get("principal");
      this.refreshKrb5Config = "true".equalsIgnoreCase((String)var4.get("refreshKrb5Config"));
      this.renewTGT = "true".equalsIgnoreCase((String)var4.get("renewTGT"));
      String var5 = (String)var4.get("isInitiator");
      if (var5 != null) {
         this.isInitiator = "true".equalsIgnoreCase(var5);
      }

      this.tryFirstPass = "true".equalsIgnoreCase((String)var4.get("tryFirstPass"));
      this.useFirstPass = "true".equalsIgnoreCase((String)var4.get("useFirstPass"));
      this.storePass = "true".equalsIgnoreCase((String)var4.get("storePass"));
      this.clearPass = "true".equalsIgnoreCase((String)var4.get("clearPass"));
      if (this.debug) {
         System.out.print("Debug is  " + this.debug + " storeKey " + this.storeKey + " useTicketCache " + this.useTicketCache + " useKeyTab " + this.useKeyTab + " doNotPrompt " + this.doNotPrompt + " ticketCache is " + this.ticketCacheName + " isInitiator " + this.isInitiator + " KeyTab is " + this.keyTabName + " refreshKrb5Config is " + this.refreshKrb5Config + " principal is " + this.princName + " tryFirstPass is " + this.tryFirstPass + " useFirstPass is " + this.useFirstPass + " storePass is " + this.storePass + " clearPass is " + this.clearPass + "\n");
      }

   }

   public boolean login() throws LoginException {
      if (this.refreshKrb5Config) {
         try {
            if (this.debug) {
               System.out.println("Refreshing Kerberos configuration");
            }

            Config.refresh();
         } catch (KrbException var3) {
            LoginException var2 = new LoginException(var3.getMessage());
            var2.initCause(var3);
            throw var2;
         }
      }

      String var1 = System.getProperty("sun.security.krb5.principal");
      if (var1 != null) {
         this.krb5PrincName = new StringBuffer(var1);
      } else if (this.princName != null) {
         this.krb5PrincName = new StringBuffer(this.princName);
      }

      this.validateConfiguration();
      if (this.krb5PrincName != null && this.krb5PrincName.toString().equals("*")) {
         this.unboundServer = true;
      }

      if (this.tryFirstPass) {
         try {
            this.attemptAuthentication(true);
            if (this.debug) {
               System.out.println("\t\t[Krb5LoginModule] authentication succeeded");
            }

            this.succeeded = true;
            this.cleanState();
            return true;
         } catch (LoginException var6) {
            this.cleanState();
            if (this.debug) {
               System.out.println("\t\t[Krb5LoginModule] tryFirstPass failed with:" + var6.getMessage());
            }
         }
      } else if (this.useFirstPass) {
         try {
            this.attemptAuthentication(true);
            this.succeeded = true;
            this.cleanState();
            return true;
         } catch (LoginException var4) {
            if (this.debug) {
               System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + var4.getMessage());
            }

            this.succeeded = false;
            this.cleanState();
            throw var4;
         }
      }

      try {
         this.attemptAuthentication(false);
         this.succeeded = true;
         this.cleanState();
         return true;
      } catch (LoginException var5) {
         if (this.debug) {
            System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + var5.getMessage());
         }

         this.succeeded = false;
         this.cleanState();
         throw var5;
      }
   }

   private void attemptAuthentication(boolean var1) throws LoginException {
      LoginException var3;
      if (this.krb5PrincName != null) {
         try {
            this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
         } catch (KrbException var5) {
            var3 = new LoginException(var5.getMessage());
            var3.initCause(var5);
            throw var3;
         }
      }

      try {
         if (this.useTicketCache) {
            if (this.debug) {
               System.out.println("Acquire TGT from Cache");
            }

            this.cred = Credentials.acquireTGTFromCache(this.principal, this.ticketCacheName);
            if (this.cred != null && !this.isCurrent(this.cred)) {
               if (this.renewTGT) {
                  this.cred = this.renewCredentials(this.cred);
               } else {
                  this.cred = null;
                  if (this.debug) {
                     System.out.println("Credentials are no longer valid");
                  }
               }
            }

            if (this.cred != null && this.principal == null) {
               this.principal = this.cred.getClient();
            }

            if (this.debug) {
               System.out.println("Principal is " + this.principal);
               if (this.cred == null) {
                  System.out.println("null credentials from Ticket Cache");
               }
            }
         }

         if (this.cred == null) {
            if (this.principal == null) {
               this.promptForName(var1);
               this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
            }

            if (this.useKeyTab) {
               if (!this.unboundServer) {
                  KerberosPrincipal var2 = new KerberosPrincipal(this.principal.getName());
                  this.ktab = this.keyTabName == null ? KeyTab.getInstance(var2) : KeyTab.getInstance(var2, new File(this.keyTabName));
               } else {
                  this.ktab = this.keyTabName == null ? KeyTab.getUnboundInstance() : KeyTab.getUnboundInstance(new File(this.keyTabName));
               }

               if (this.isInitiator && Krb5Util.keysFromJavaxKeyTab(this.ktab, this.principal).length == 0) {
                  this.ktab = null;
                  if (this.debug) {
                     System.out.println("Key for the principal " + this.principal + " not available in " + (this.keyTabName == null ? "default key tab" : this.keyTabName));
                  }
               }
            }

            KrbAsReqBuilder var8;
            if (this.ktab == null) {
               this.promptForPass(var1);
               var8 = new KrbAsReqBuilder(this.principal, this.password);
               if (this.isInitiator) {
                  this.cred = var8.action().getCreds();
               }

               if (this.storeKey) {
                  this.encKeys = var8.getKeys(this.isInitiator);
               }
            } else {
               var8 = new KrbAsReqBuilder(this.principal, this.ktab);
               if (this.isInitiator) {
                  this.cred = var8.action().getCreds();
               }
            }

            var8.destroy();
            if (this.debug) {
               System.out.println("principal is " + this.principal);
               HexDumpEncoder var9 = new HexDumpEncoder();
               if (this.ktab != null) {
                  System.out.println("Will use keytab");
               } else if (this.storeKey) {
                  for(int var4 = 0; var4 < this.encKeys.length; ++var4) {
                     System.out.println("EncryptionKey: keyType=" + this.encKeys[var4].getEType() + " keyBytes (hex dump)=" + var9.encodeBuffer(this.encKeys[var4].getBytes()));
                  }
               }
            }

            if (this.isInitiator && this.cred == null) {
               throw new LoginException("TGT Can not be obtained from the KDC ");
            }
         }

      } catch (KrbException var6) {
         var3 = new LoginException(var6.getMessage());
         var3.initCause(var6);
         throw var3;
      } catch (IOException var7) {
         var3 = new LoginException(var7.getMessage());
         var3.initCause(var7);
         throw var3;
      }
   }

   private void promptForName(boolean var1) throws LoginException {
      this.krb5PrincName = new StringBuffer("");
      if (var1) {
         this.username = (String)this.sharedState.get("javax.security.auth.login.name");
         if (this.debug) {
            System.out.println("username from shared state is " + this.username + "\n");
         }

         if (this.username == null) {
            System.out.println("username from shared state is null\n");
            throw new LoginException("Username can not be obtained from sharedstate ");
         }

         if (this.debug) {
            System.out.println("username from shared state is " + this.username + "\n");
         }

         if (this.username != null && this.username.length() > 0) {
            this.krb5PrincName.insert(0, (String)this.username);
            return;
         }
      }

      if (this.doNotPrompt) {
         throw new LoginException("Unable to obtain Principal Name for authentication ");
      } else if (this.callbackHandler == null) {
         throw new LoginException("No CallbackHandler available to garner authentication information from the user");
      } else {
         try {
            String var2 = System.getProperty("user.name");
            Callback[] var3 = new Callback[1];
            MessageFormat var4 = new MessageFormat(rb.getString("Kerberos.username.defUsername."));
            Object[] var5 = new Object[]{var2};
            var3[0] = new NameCallback(var4.format(var5));
            this.callbackHandler.handle(var3);
            this.username = ((NameCallback)var3[0]).getName();
            if (this.username == null || this.username.length() == 0) {
               this.username = var2;
            }

            this.krb5PrincName.insert(0, (String)this.username);
         } catch (IOException var6) {
            throw new LoginException(var6.getMessage());
         } catch (UnsupportedCallbackException var7) {
            throw new LoginException(var7.getMessage() + " not available to garner  authentication information  from the user");
         }
      }
   }

   private void promptForPass(boolean var1) throws LoginException {
      if (var1) {
         this.password = (char[])((char[])this.sharedState.get("javax.security.auth.login.password"));
         if (this.password == null) {
            if (this.debug) {
               System.out.println("Password from shared state is null");
            }

            throw new LoginException("Password can not be obtained from sharedstate ");
         } else {
            if (this.debug) {
               System.out.println("password is " + new String(this.password));
            }

         }
      } else if (this.doNotPrompt) {
         throw new LoginException("Unable to obtain password from user\n");
      } else if (this.callbackHandler == null) {
         throw new LoginException("No CallbackHandler available to garner authentication information from the user");
      } else {
         try {
            Callback[] var2 = new Callback[1];
            String var3 = this.krb5PrincName.toString();
            MessageFormat var4 = new MessageFormat(rb.getString("Kerberos.password.for.username."));
            Object[] var5 = new Object[]{var3};
            var2[0] = new PasswordCallback(var4.format(var5), false);
            this.callbackHandler.handle(var2);
            char[] var6 = ((PasswordCallback)var2[0]).getPassword();
            if (var6 == null) {
               throw new LoginException("No password provided");
            } else {
               this.password = new char[var6.length];
               System.arraycopy(var6, 0, this.password, 0, var6.length);
               ((PasswordCallback)var2[0]).clearPassword();

               for(int var7 = 0; var7 < var6.length; ++var7) {
                  var6[var7] = ' ';
               }

               Object var10 = null;
               if (this.debug) {
                  System.out.println("\t\t[Krb5LoginModule] user entered username: " + this.krb5PrincName);
                  System.out.println();
               }

            }
         } catch (IOException var8) {
            throw new LoginException(var8.getMessage());
         } catch (UnsupportedCallbackException var9) {
            throw new LoginException(var9.getMessage() + " not available to garner  authentication information from the user");
         }
      }
   }

   private void validateConfiguration() throws LoginException {
      if (this.doNotPrompt && !this.useTicketCache && !this.useKeyTab && !this.tryFirstPass && !this.useFirstPass) {
         throw new LoginException("Configuration Error - either doNotPrompt should be  false or at least one of useTicketCache,  useKeyTab, tryFirstPass and useFirstPass should be true");
      } else if (this.ticketCacheName != null && !this.useTicketCache) {
         throw new LoginException("Configuration Error  - useTicketCache should be set to true to use the ticket cache" + this.ticketCacheName);
      } else if (this.keyTabName != null & !this.useKeyTab) {
         throw new LoginException("Configuration Error - useKeyTab should be set to true to use the keytab" + this.keyTabName);
      } else if (this.storeKey && this.doNotPrompt && !this.useKeyTab && !this.tryFirstPass && !this.useFirstPass) {
         throw new LoginException("Configuration Error - either doNotPrompt should be set to  false or at least one of tryFirstPass, useFirstPass or useKeyTab must be set to true for storeKey option");
      } else if (this.renewTGT && !this.useTicketCache) {
         throw new LoginException("Configuration Error - either useTicketCache should be  true or renewTGT should be false");
      } else if (this.krb5PrincName != null && this.krb5PrincName.toString().equals("*") && this.isInitiator) {
         throw new LoginException("Configuration Error - principal cannot be * when isInitiator is true");
      }
   }

   private boolean isCurrent(Credentials var1) {
      Date var2 = var1.getEndTime();
      if (var2 != null) {
         return System.currentTimeMillis() <= var2.getTime();
      } else {
         return true;
      }
   }

   private Credentials renewCredentials(Credentials var1) {
      Credentials var2;
      try {
         if (!var1.isRenewable()) {
            throw new RefreshFailedException("This ticket is not renewable");
         }

         if (System.currentTimeMillis() > this.cred.getRenewTill().getTime()) {
            throw new RefreshFailedException("This ticket is past its last renewal time.");
         }

         var2 = var1.renew();
         if (this.debug) {
            System.out.println("Renewed Kerberos Ticket");
         }
      } catch (Exception var4) {
         var2 = null;
         if (this.debug) {
            System.out.println("Ticket could not be renewed : " + var4.getMessage());
         }
      }

      return var2;
   }

   public boolean commit() throws LoginException {
      if (!this.succeeded) {
         return false;
      } else if (this.isInitiator && this.cred == null) {
         this.succeeded = false;
         throw new LoginException("Null Client Credential");
      } else if (this.subject.isReadOnly()) {
         this.cleanKerberosCred();
         throw new LoginException("Subject is Readonly");
      } else {
         Set var1 = this.subject.getPrivateCredentials();
         Set var2 = this.subject.getPrincipals();
         this.kerbClientPrinc = new KerberosPrincipal(this.principal.getName());
         if (this.isInitiator) {
            this.kerbTicket = Krb5Util.credsToTicket(this.cred);
         }

         int var3;
         if (this.storeKey && this.encKeys != null) {
            if (this.encKeys.length == 0) {
               this.succeeded = false;
               throw new LoginException("Null Server Key ");
            }

            this.kerbKeys = new KerberosKey[this.encKeys.length];

            for(var3 = 0; var3 < this.encKeys.length; ++var3) {
               Integer var4 = this.encKeys[var3].getKeyVersionNumber();
               this.kerbKeys[var3] = new KerberosKey(this.kerbClientPrinc, this.encKeys[var3].getBytes(), this.encKeys[var3].getEType(), var4 == null ? 0 : var4);
            }
         }

         if (!this.unboundServer && !var2.contains(this.kerbClientPrinc)) {
            var2.add(this.kerbClientPrinc);
         }

         if (this.kerbTicket != null && !var1.contains(this.kerbTicket)) {
            var1.add(this.kerbTicket);
         }

         if (this.storeKey) {
            if (this.encKeys == null) {
               if (this.ktab == null) {
                  this.succeeded = false;
                  throw new LoginException("No key to store");
               }

               if (!var1.contains(this.ktab)) {
                  var1.add(this.ktab);
               }
            } else {
               for(var3 = 0; var3 < this.kerbKeys.length; ++var3) {
                  if (!var1.contains(this.kerbKeys[var3])) {
                     var1.add(this.kerbKeys[var3]);
                  }

                  this.encKeys[var3].destroy();
                  this.encKeys[var3] = null;
                  if (this.debug) {
                     System.out.println("Added server's key" + this.kerbKeys[var3]);
                     System.out.println("\t\t[Krb5LoginModule] added Krb5Principal  " + this.kerbClientPrinc.toString() + " to Subject");
                  }
               }
            }
         }

         this.commitSucceeded = true;
         if (this.debug) {
            System.out.println("Commit Succeeded \n");
         }

         return true;
      }
   }

   public boolean abort() throws LoginException {
      if (!this.succeeded) {
         return false;
      } else {
         if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanKerberosCred();
         } else {
            this.logout();
         }

         return true;
      }
   }

   public boolean logout() throws LoginException {
      if (this.debug) {
         System.out.println("\t\t[Krb5LoginModule]: Entering logout");
      }

      if (this.subject.isReadOnly()) {
         this.cleanKerberosCred();
         throw new LoginException("Subject is Readonly");
      } else {
         this.subject.getPrincipals().remove(this.kerbClientPrinc);
         Iterator var1 = this.subject.getPrivateCredentials().iterator();

         while(true) {
            Object var2;
            do {
               if (!var1.hasNext()) {
                  this.cleanKerberosCred();
                  this.succeeded = false;
                  this.commitSucceeded = false;
                  if (this.debug) {
                     System.out.println("\t\t[Krb5LoginModule]: logged out Subject");
                  }

                  return true;
               }

               var2 = var1.next();
            } while(!(var2 instanceof KerberosTicket) && !(var2 instanceof KerberosKey) && !(var2 instanceof KeyTab));

            var1.remove();
         }
      }
   }

   private void cleanKerberosCred() throws LoginException {
      try {
         if (this.kerbTicket != null) {
            this.kerbTicket.destroy();
         }

         if (this.kerbKeys != null) {
            for(int var1 = 0; var1 < this.kerbKeys.length; ++var1) {
               this.kerbKeys[var1].destroy();
            }
         }
      } catch (DestroyFailedException var2) {
         throw new LoginException("Destroy Failed on Kerberos Private Credentials");
      }

      this.kerbTicket = null;
      this.kerbKeys = null;
      this.kerbClientPrinc = null;
   }

   private void cleanState() {
      if (this.succeeded) {
         if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
            this.sharedState.put("javax.security.auth.login.name", this.username);
            this.sharedState.put("javax.security.auth.login.password", this.password);
         }
      } else {
         this.encKeys = null;
         this.ktab = null;
         this.principal = null;
      }

      this.username = null;
      this.password = null;
      if (this.krb5PrincName != null && this.krb5PrincName.length() != 0) {
         this.krb5PrincName.delete(0, this.krb5PrincName.length());
      }

      this.krb5PrincName = null;
      if (this.clearPass) {
         this.sharedState.remove("javax.security.auth.login.name");
         this.sharedState.remove("javax.security.auth.login.password");
      }

   }
}
