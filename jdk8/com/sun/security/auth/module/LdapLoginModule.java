package com.sun.security.auth.module;

import com.sun.security.auth.LdapPrincipal;
import com.sun.security.auth.UserPrincipal;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
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
public class LdapLoginModule implements LoginModule {
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private static final String USERNAME_KEY = "javax.security.auth.login.name";
   private static final String PASSWORD_KEY = "javax.security.auth.login.password";
   private static final String USER_PROVIDER = "userProvider";
   private static final String USER_FILTER = "userFilter";
   private static final String AUTHC_IDENTITY = "authIdentity";
   private static final String AUTHZ_IDENTITY = "authzIdentity";
   private static final String USERNAME_TOKEN = "{USERNAME}";
   private static final Pattern USERNAME_PATTERN = Pattern.compile("\\{USERNAME\\}");
   private String userProvider;
   private String userFilter;
   private String authcIdentity;
   private String authzIdentity;
   private String authzIdentityAttr = null;
   private boolean useSSL = true;
   private boolean authFirst = false;
   private boolean authOnly = false;
   private boolean useFirstPass = false;
   private boolean tryFirstPass = false;
   private boolean storePass = false;
   private boolean clearPass = false;
   private boolean debug = false;
   private boolean succeeded = false;
   private boolean commitSucceeded = false;
   private String username;
   private char[] password;
   private LdapPrincipal ldapPrincipal;
   private UserPrincipal userPrincipal;
   private UserPrincipal authzPrincipal;
   private Subject subject;
   private CallbackHandler callbackHandler;
   private Map<String, Object> sharedState;
   private Map<String, ?> options;
   private LdapContext ctx;
   private Matcher identityMatcher = null;
   private Matcher filterMatcher = null;
   private Hashtable<String, Object> ldapEnvironment;
   private SearchControls constraints = null;

   public void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4) {
      this.subject = var1;
      this.callbackHandler = var2;
      this.sharedState = var3;
      this.options = var4;
      this.ldapEnvironment = new Hashtable(9);
      this.ldapEnvironment.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      Iterator var5 = var4.keySet().iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (var6.indexOf(".") > -1) {
            this.ldapEnvironment.put(var6, var4.get(var6));
         }
      }

      this.userProvider = (String)var4.get("userProvider");
      if (this.userProvider != null) {
         this.ldapEnvironment.put("java.naming.provider.url", this.userProvider);
      }

      this.authcIdentity = (String)var4.get("authIdentity");
      if (this.authcIdentity != null && this.authcIdentity.indexOf("{USERNAME}") != -1) {
         this.identityMatcher = USERNAME_PATTERN.matcher(this.authcIdentity);
      }

      this.userFilter = (String)var4.get("userFilter");
      if (this.userFilter != null) {
         if (this.userFilter.indexOf("{USERNAME}") != -1) {
            this.filterMatcher = USERNAME_PATTERN.matcher(this.userFilter);
         }

         this.constraints = new SearchControls();
         this.constraints.setSearchScope(2);
         this.constraints.setReturningAttributes(new String[0]);
      }

      this.authzIdentity = (String)var4.get("authzIdentity");
      if (this.authzIdentity != null && this.authzIdentity.startsWith("{") && this.authzIdentity.endsWith("}")) {
         if (this.constraints != null) {
            this.authzIdentityAttr = this.authzIdentity.substring(1, this.authzIdentity.length() - 1);
            this.constraints.setReturningAttributes(new String[]{this.authzIdentityAttr});
         }

         this.authzIdentity = null;
      }

      if (this.authcIdentity != null) {
         if (this.userFilter != null) {
            this.authFirst = true;
         } else {
            this.authOnly = true;
         }
      }

      if ("false".equalsIgnoreCase((String)var4.get("useSSL"))) {
         this.useSSL = false;
         this.ldapEnvironment.remove("java.naming.security.protocol");
      } else {
         this.ldapEnvironment.put("java.naming.security.protocol", "ssl");
      }

      this.tryFirstPass = "true".equalsIgnoreCase((String)var4.get("tryFirstPass"));
      this.useFirstPass = "true".equalsIgnoreCase((String)var4.get("useFirstPass"));
      this.storePass = "true".equalsIgnoreCase((String)var4.get("storePass"));
      this.clearPass = "true".equalsIgnoreCase((String)var4.get("clearPass"));
      this.debug = "true".equalsIgnoreCase((String)var4.get("debug"));
      if (this.debug) {
         if (this.authFirst) {
            System.out.println("\t\t[LdapLoginModule] authentication-first mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
         } else if (this.authOnly) {
            System.out.println("\t\t[LdapLoginModule] authentication-only mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
         } else {
            System.out.println("\t\t[LdapLoginModule] search-first mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
         }
      }

   }

   public boolean login() throws LoginException {
      if (this.userProvider == null) {
         throw new LoginException("Unable to locate the LDAP directory service");
      } else {
         if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] user provider: " + this.userProvider);
         }

         if (this.tryFirstPass) {
            try {
               this.attemptAuthentication(true);
               this.succeeded = true;
               if (this.debug) {
                  System.out.println("\t\t[LdapLoginModule] tryFirstPass succeeded");
               }

               return true;
            } catch (LoginException var4) {
               this.cleanState();
               if (this.debug) {
                  System.out.println("\t\t[LdapLoginModule] tryFirstPass failed: " + var4.toString());
               }
            }
         } else if (this.useFirstPass) {
            try {
               this.attemptAuthentication(true);
               this.succeeded = true;
               if (this.debug) {
                  System.out.println("\t\t[LdapLoginModule] useFirstPass succeeded");
               }

               return true;
            } catch (LoginException var3) {
               this.cleanState();
               if (this.debug) {
                  System.out.println("\t\t[LdapLoginModule] useFirstPass failed");
               }

               throw var3;
            }
         }

         try {
            this.attemptAuthentication(false);
            this.succeeded = true;
            if (this.debug) {
               System.out.println("\t\t[LdapLoginModule] authentication succeeded");
            }

            return true;
         } catch (LoginException var2) {
            this.cleanState();
            if (this.debug) {
               System.out.println("\t\t[LdapLoginModule] authentication failed");
            }

            throw var2;
         }
      }
   }

   public boolean commit() throws LoginException {
      if (!this.succeeded) {
         return false;
      } else if (this.subject.isReadOnly()) {
         this.cleanState();
         throw new LoginException("Subject is read-only");
      } else {
         Set var1 = this.subject.getPrincipals();
         if (!var1.contains(this.ldapPrincipal)) {
            var1.add(this.ldapPrincipal);
         }

         if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] added LdapPrincipal \"" + this.ldapPrincipal + "\" to Subject");
         }

         if (!var1.contains(this.userPrincipal)) {
            var1.add(this.userPrincipal);
         }

         if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + this.userPrincipal + "\" to Subject");
         }

         if (this.authzPrincipal != null && !var1.contains(this.authzPrincipal)) {
            var1.add(this.authzPrincipal);
            if (this.debug) {
               System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + this.authzPrincipal + "\" to Subject");
            }
         }

         this.cleanState();
         this.commitSucceeded = true;
         return true;
      }
   }

   public boolean abort() throws LoginException {
      if (this.debug) {
         System.out.println("\t\t[LdapLoginModule] aborted authentication");
      }

      if (!this.succeeded) {
         return false;
      } else {
         if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanState();
            this.ldapPrincipal = null;
            this.userPrincipal = null;
            this.authzPrincipal = null;
         } else {
            this.logout();
         }

         return true;
      }
   }

   public boolean logout() throws LoginException {
      if (this.subject.isReadOnly()) {
         this.cleanState();
         throw new LoginException("Subject is read-only");
      } else {
         Set var1 = this.subject.getPrincipals();
         var1.remove(this.ldapPrincipal);
         var1.remove(this.userPrincipal);
         if (this.authzIdentity != null) {
            var1.remove(this.authzPrincipal);
         }

         this.cleanState();
         this.succeeded = false;
         this.commitSucceeded = false;
         this.ldapPrincipal = null;
         this.userPrincipal = null;
         this.authzPrincipal = null;
         if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] logged out Subject");
         }

         return true;
      }
   }

   private void attemptAuthentication(boolean var1) throws LoginException {
      this.getUsernamePassword(var1);
      if (this.password != null && this.password.length != 0) {
         String var2 = "";
         if (!this.authFirst && !this.authOnly) {
            try {
               this.ctx = new InitialLdapContext(this.ldapEnvironment, (Control[])null);
            } catch (NamingException var6) {
               throw (LoginException)(new FailedLoginException("Cannot connect to LDAP server")).initCause(var6);
            }

            var2 = this.findUserDN(this.ctx);

            try {
               this.ctx.addToEnvironment("java.naming.security.authentication", "simple");
               this.ctx.addToEnvironment("java.naming.security.principal", var2);
               this.ctx.addToEnvironment("java.naming.security.credentials", this.password);
               if (this.debug) {
                  System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + this.username);
               }

               this.ctx.reconnect((Control[])null);
            } catch (NamingException var5) {
               throw (LoginException)(new FailedLoginException("Cannot bind to LDAP server")).initCause(var5);
            }
         } else {
            String var3 = this.replaceUsernameToken(this.identityMatcher, this.authcIdentity, this.username);
            this.ldapEnvironment.put("java.naming.security.credentials", this.password);
            this.ldapEnvironment.put("java.naming.security.principal", var3);
            if (this.debug) {
               System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + this.username);
            }

            try {
               this.ctx = new InitialLdapContext(this.ldapEnvironment, (Control[])null);
            } catch (NamingException var7) {
               throw (LoginException)(new FailedLoginException("Cannot bind to LDAP server")).initCause(var7);
            }

            if (this.userFilter != null) {
               var2 = this.findUserDN(this.ctx);
            } else {
               var2 = var3;
            }
         }

         if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
            this.sharedState.put("javax.security.auth.login.name", this.username);
            this.sharedState.put("javax.security.auth.login.password", this.password);
         }

         this.userPrincipal = new UserPrincipal(this.username);
         if (this.authzIdentity != null) {
            this.authzPrincipal = new UserPrincipal(this.authzIdentity);
         }

         try {
            this.ldapPrincipal = new LdapPrincipal(var2);
         } catch (InvalidNameException var8) {
            if (this.debug) {
               System.out.println("\t\t[LdapLoginModule] cannot create LdapPrincipal: bad DN");
            }

            throw (LoginException)(new FailedLoginException("Cannot create LdapPrincipal")).initCause(var8);
         }
      } else {
         throw new FailedLoginException("No password was supplied");
      }
   }

   private String findUserDN(LdapContext var1) throws LoginException {
      String var2 = "";
      if (this.userFilter != null) {
         if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] searching for entry belonging to user: " + this.username);
         }

         try {
            String var3 = this.replaceUsernameToken(this.filterMatcher, this.userFilter, this.escapeUsernameChars());
            NamingEnumeration var4 = var1.search("", var3, this.constraints);
            if (var4.hasMore()) {
               SearchResult var5 = (SearchResult)var4.next();
               var2 = var5.getNameInNamespace();
               if (this.debug) {
                  System.out.println("\t\t[LdapLoginModule] found entry: " + var2);
               }

               if (this.authzIdentityAttr != null) {
                  Attribute var6 = var5.getAttributes().get(this.authzIdentityAttr);
                  if (var6 != null) {
                     Object var7 = var6.get();
                     if (var7 instanceof String) {
                        this.authzIdentity = (String)var7;
                     }
                  }
               }

               var4.close();
            } else if (this.debug) {
               System.out.println("\t\t[LdapLoginModule] user's entry not found");
            }
         } catch (NamingException var8) {
         }

         if (var2.equals("")) {
            throw new FailedLoginException("Cannot find user's LDAP entry");
         } else {
            return var2;
         }
      } else {
         if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] cannot search for entry belonging to user: " + this.username);
         }

         throw new FailedLoginException("Cannot find user's LDAP entry");
      }
   }

   private String escapeUsernameChars() {
      int var1 = this.username.length();
      StringBuilder var2 = new StringBuilder(var1 + 16);

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = this.username.charAt(var3);
         switch(var4) {
         case '\u0000':
            var2.append("\\\\00");
            break;
         case '(':
            var2.append("\\\\28");
            break;
         case ')':
            var2.append("\\\\29");
            break;
         case '*':
            var2.append("\\\\2A");
            break;
         case '\\':
            var2.append("\\\\5C");
            break;
         default:
            var2.append(var4);
         }
      }

      return var2.toString();
   }

   private String replaceUsernameToken(Matcher var1, String var2, String var3) {
      return var1 != null ? var1.replaceAll(var3) : var2;
   }

   private void getUsernamePassword(boolean var1) throws LoginException {
      if (var1) {
         this.username = (String)this.sharedState.get("javax.security.auth.login.name");
         this.password = (char[])((char[])this.sharedState.get("javax.security.auth.login.password"));
      } else if (this.callbackHandler == null) {
         throw new LoginException("No CallbackHandler available to acquire authentication information from the user");
      } else {
         Callback[] var2 = new Callback[]{new NameCallback(rb.getString("username.")), new PasswordCallback(rb.getString("password."), false)};

         try {
            this.callbackHandler.handle(var2);
            this.username = ((NameCallback)var2[0]).getName();
            char[] var3 = ((PasswordCallback)var2[1]).getPassword();
            this.password = new char[var3.length];
            System.arraycopy(var3, 0, this.password, 0, var3.length);
            ((PasswordCallback)var2[1]).clearPassword();
         } catch (IOException var4) {
            throw new LoginException(var4.toString());
         } catch (UnsupportedCallbackException var5) {
            throw new LoginException("Error: " + var5.getCallback().toString() + " not available to acquire authentication information from the user");
         }
      }
   }

   private void cleanState() {
      this.username = null;
      if (this.password != null) {
         Arrays.fill(this.password, ' ');
         this.password = null;
      }

      try {
         if (this.ctx != null) {
            this.ctx.close();
         }
      } catch (NamingException var2) {
      }

      this.ctx = null;
      if (this.clearPass) {
         this.sharedState.remove("javax.security.auth.login.name");
         this.sharedState.remove("javax.security.auth.login.password");
      }

   }
}
