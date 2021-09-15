package com.sun.security.auth.module;

import com.sun.security.auth.UnixNumericGroupPrincipal;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;

@Exported
public class JndiLoginModule implements LoginModule {
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   public final String USER_PROVIDER = "user.provider.url";
   public final String GROUP_PROVIDER = "group.provider.url";
   private boolean debug = false;
   private boolean strongDebug = false;
   private String userProvider;
   private String groupProvider;
   private boolean useFirstPass = false;
   private boolean tryFirstPass = false;
   private boolean storePass = false;
   private boolean clearPass = false;
   private boolean succeeded = false;
   private boolean commitSucceeded = false;
   private String username;
   private char[] password;
   DirContext ctx;
   private UnixPrincipal userPrincipal;
   private UnixNumericUserPrincipal UIDPrincipal;
   private UnixNumericGroupPrincipal GIDPrincipal;
   private LinkedList<UnixNumericGroupPrincipal> supplementaryGroups = new LinkedList();
   private Subject subject;
   private CallbackHandler callbackHandler;
   private Map<String, Object> sharedState;
   private Map<String, ?> options;
   private static final String CRYPT = "{crypt}";
   private static final String USER_PWD = "userPassword";
   private static final String USER_UID = "uidNumber";
   private static final String USER_GID = "gidNumber";
   private static final String GROUP_ID = "gidNumber";
   private static final String NAME = "javax.security.auth.login.name";
   private static final String PWD = "javax.security.auth.login.password";

   public void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4) {
      this.subject = var1;
      this.callbackHandler = var2;
      this.sharedState = var3;
      this.options = var4;
      this.debug = "true".equalsIgnoreCase((String)var4.get("debug"));
      this.strongDebug = "true".equalsIgnoreCase((String)var4.get("strongDebug"));
      this.userProvider = (String)var4.get("user.provider.url");
      this.groupProvider = (String)var4.get("group.provider.url");
      this.tryFirstPass = "true".equalsIgnoreCase((String)var4.get("tryFirstPass"));
      this.useFirstPass = "true".equalsIgnoreCase((String)var4.get("useFirstPass"));
      this.storePass = "true".equalsIgnoreCase((String)var4.get("storePass"));
      this.clearPass = "true".equalsIgnoreCase((String)var4.get("clearPass"));
   }

   public boolean login() throws LoginException {
      if (this.userProvider == null) {
         throw new LoginException("Error: Unable to locate JNDI user provider");
      } else if (this.groupProvider == null) {
         throw new LoginException("Error: Unable to locate JNDI group provider");
      } else {
         if (this.debug) {
            System.out.println("\t\t[JndiLoginModule] user provider: " + this.userProvider);
            System.out.println("\t\t[JndiLoginModule] group provider: " + this.groupProvider);
         }

         if (this.tryFirstPass) {
            try {
               this.attemptAuthentication(true);
               this.succeeded = true;
               if (this.debug) {
                  System.out.println("\t\t[JndiLoginModule] tryFirstPass succeeded");
               }

               return true;
            } catch (LoginException var4) {
               this.cleanState();
               if (this.debug) {
                  System.out.println("\t\t[JndiLoginModule] tryFirstPass failed with:" + var4.toString());
               }
            }
         } else if (this.useFirstPass) {
            try {
               this.attemptAuthentication(true);
               this.succeeded = true;
               if (this.debug) {
                  System.out.println("\t\t[JndiLoginModule] useFirstPass succeeded");
               }

               return true;
            } catch (LoginException var2) {
               this.cleanState();
               if (this.debug) {
                  System.out.println("\t\t[JndiLoginModule] useFirstPass failed");
               }

               throw var2;
            }
         }

         try {
            this.attemptAuthentication(false);
            this.succeeded = true;
            if (this.debug) {
               System.out.println("\t\t[JndiLoginModule] regular authentication succeeded");
            }

            return true;
         } catch (LoginException var3) {
            this.cleanState();
            if (this.debug) {
               System.out.println("\t\t[JndiLoginModule] regular authentication failed");
            }

            throw var3;
         }
      }
   }

   public boolean commit() throws LoginException {
      if (!this.succeeded) {
         return false;
      } else if (this.subject.isReadOnly()) {
         this.cleanState();
         throw new LoginException("Subject is Readonly");
      } else {
         if (!this.subject.getPrincipals().contains(this.userPrincipal)) {
            this.subject.getPrincipals().add(this.userPrincipal);
         }

         if (!this.subject.getPrincipals().contains(this.UIDPrincipal)) {
            this.subject.getPrincipals().add(this.UIDPrincipal);
         }

         if (!this.subject.getPrincipals().contains(this.GIDPrincipal)) {
            this.subject.getPrincipals().add(this.GIDPrincipal);
         }

         for(int var1 = 0; var1 < this.supplementaryGroups.size(); ++var1) {
            if (!this.subject.getPrincipals().contains(this.supplementaryGroups.get(var1))) {
               this.subject.getPrincipals().add(this.supplementaryGroups.get(var1));
            }
         }

         if (this.debug) {
            System.out.println("\t\t[JndiLoginModule]: added UnixPrincipal,");
            System.out.println("\t\t\t\tUnixNumericUserPrincipal,");
            System.out.println("\t\t\t\tUnixNumericGroupPrincipal(s),");
            System.out.println("\t\t\t to Subject");
         }

         this.cleanState();
         this.commitSucceeded = true;
         return true;
      }
   }

   public boolean abort() throws LoginException {
      if (this.debug) {
         System.out.println("\t\t[JndiLoginModule]: aborted authentication failed");
      }

      if (!this.succeeded) {
         return false;
      } else {
         if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanState();
            this.userPrincipal = null;
            this.UIDPrincipal = null;
            this.GIDPrincipal = null;
            this.supplementaryGroups = new LinkedList();
         } else {
            this.logout();
         }

         return true;
      }
   }

   public boolean logout() throws LoginException {
      if (this.subject.isReadOnly()) {
         this.cleanState();
         throw new LoginException("Subject is Readonly");
      } else {
         this.subject.getPrincipals().remove(this.userPrincipal);
         this.subject.getPrincipals().remove(this.UIDPrincipal);
         this.subject.getPrincipals().remove(this.GIDPrincipal);

         for(int var1 = 0; var1 < this.supplementaryGroups.size(); ++var1) {
            this.subject.getPrincipals().remove(this.supplementaryGroups.get(var1));
         }

         this.cleanState();
         this.succeeded = false;
         this.commitSucceeded = false;
         this.userPrincipal = null;
         this.UIDPrincipal = null;
         this.GIDPrincipal = null;
         this.supplementaryGroups = new LinkedList();
         if (this.debug) {
            System.out.println("\t\t[JndiLoginModule]: logged out Subject");
         }

         return true;
      }
   }

   private void attemptAuthentication(boolean var1) throws LoginException {
      String var2 = null;
      this.getUsernamePassword(var1);

      try {
         InitialContext var3 = new InitialContext();
         this.ctx = (DirContext)var3.lookup(this.userProvider);
         SearchControls var4 = new SearchControls();
         NamingEnumeration var5 = this.ctx.search("", "(uid=" + this.username + ")", var4);
         if (var5.hasMore()) {
            SearchResult var6 = (SearchResult)var5.next();
            Attributes var7 = var6.getAttributes();
            Attribute var8 = var7.get("userPassword");
            String var9 = new String((byte[])((byte[])var8.get()), "UTF8");
            var2 = var9.substring("{crypt}".length());
            if (this.verifyPassword(var2, new String(this.password))) {
               if (this.debug) {
                  System.out.println("\t\t[JndiLoginModule] attemptAuthentication() succeeded");
               }

               if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
                  this.sharedState.put("javax.security.auth.login.name", this.username);
                  this.sharedState.put("javax.security.auth.login.password", this.password);
               }

               this.userPrincipal = new UnixPrincipal(this.username);
               Attribute var10 = var7.get("uidNumber");
               String var11 = (String)var10.get();
               this.UIDPrincipal = new UnixNumericUserPrincipal(var11);
               if (this.debug && var11 != null) {
                  System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has UID: " + var11);
               }

               Attribute var12 = var7.get("gidNumber");
               String var13 = (String)var12.get();
               this.GIDPrincipal = new UnixNumericGroupPrincipal(var13, true);
               if (this.debug && var13 != null) {
                  System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has GID: " + var13);
               }

               this.ctx = (DirContext)var3.lookup(this.groupProvider);
               var5 = this.ctx.search((String)"", new BasicAttributes("memberUid", this.username));

               while(var5.hasMore()) {
                  var6 = (SearchResult)var5.next();
                  var7 = var6.getAttributes();
                  var12 = var7.get("gidNumber");
                  String var14 = (String)var12.get();
                  if (!var13.equals(var14)) {
                     UnixNumericGroupPrincipal var15 = new UnixNumericGroupPrincipal(var14, false);
                     this.supplementaryGroups.add(var15);
                     if (this.debug && var14 != null) {
                        System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has Supplementary Group: " + var14);
                     }
                  }
               }

            } else {
               if (this.debug) {
                  System.out.println("\t\t[JndiLoginModule] attemptAuthentication() failed");
               }

               throw new FailedLoginException("Login incorrect");
            }
         } else {
            if (this.debug) {
               System.out.println("\t\t[JndiLoginModule]: User not found");
            }

            throw new FailedLoginException("User not found");
         }
      } catch (NamingException var16) {
         if (this.debug) {
            System.out.println("\t\t[JndiLoginModule]:  User not found");
            var16.printStackTrace();
         }

         throw new FailedLoginException("User not found");
      } catch (UnsupportedEncodingException var17) {
         if (this.debug) {
            System.out.println("\t\t[JndiLoginModule]:  password incorrectly encoded");
            var17.printStackTrace();
         }

         throw new LoginException("Login failure due to incorrect password encoding in the password database");
      }
   }

   private void getUsernamePassword(boolean var1) throws LoginException {
      if (var1) {
         this.username = (String)this.sharedState.get("javax.security.auth.login.name");
         this.password = (char[])((char[])this.sharedState.get("javax.security.auth.login.password"));
      } else if (this.callbackHandler == null) {
         throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
      } else {
         String var2 = this.userProvider.substring(0, this.userProvider.indexOf(":"));
         Callback[] var3 = new Callback[]{new NameCallback(var2 + " " + rb.getString("username.")), new PasswordCallback(var2 + " " + rb.getString("password."), false)};

         try {
            this.callbackHandler.handle(var3);
            this.username = ((NameCallback)var3[0]).getName();
            char[] var4 = ((PasswordCallback)var3[1]).getPassword();
            this.password = new char[var4.length];
            System.arraycopy(var4, 0, this.password, 0, var4.length);
            ((PasswordCallback)var3[1]).clearPassword();
         } catch (IOException var5) {
            throw new LoginException(var5.toString());
         } catch (UnsupportedCallbackException var6) {
            throw new LoginException("Error: " + var6.getCallback().toString() + " not available to garner authentication information from the user");
         }

         if (this.strongDebug) {
            System.out.println("\t\t[JndiLoginModule] user entered username: " + this.username);
            System.out.print("\t\t[JndiLoginModule] user entered password: ");

            for(int var7 = 0; var7 < this.password.length; ++var7) {
               System.out.print(this.password[var7]);
            }

            System.out.println();
         }

      }
   }

   private boolean verifyPassword(String var1, String var2) {
      if (var1 == null) {
         return false;
      } else {
         Crypt var3 = new Crypt();

         try {
            byte[] var4 = var1.getBytes("UTF8");
            byte[] var5 = var3.crypt(var2.getBytes("UTF8"), var4);
            if (var5.length != var4.length) {
               return false;
            } else {
               for(int var6 = 0; var6 < var5.length; ++var6) {
                  if (var4[var6] != var5[var6]) {
                     return false;
                  }
               }

               return true;
            }
         } catch (UnsupportedEncodingException var7) {
            return false;
         }
      }
   }

   private void cleanState() {
      this.username = null;
      if (this.password != null) {
         for(int var1 = 0; var1 < this.password.length; ++var1) {
            this.password[var1] = ' ';
         }

         this.password = null;
      }

      this.ctx = null;
      if (this.clearPass) {
         this.sharedState.remove("javax.security.auth.login.name");
         this.sharedState.remove("javax.security.auth.login.password");
      }

   }
}
