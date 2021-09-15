package com.sun.jmx.remote.security;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class FileLoginModule implements LoginModule {
   private static final String DEFAULT_PASSWORD_FILE_NAME;
   private static final String USERNAME_KEY = "javax.security.auth.login.name";
   private static final String PASSWORD_KEY = "javax.security.auth.login.password";
   private static final ClassLogger logger;
   private boolean useFirstPass = false;
   private boolean tryFirstPass = false;
   private boolean storePass = false;
   private boolean clearPass = false;
   private boolean succeeded = false;
   private boolean commitSucceeded = false;
   private String username;
   private char[] password;
   private JMXPrincipal user;
   private Subject subject;
   private CallbackHandler callbackHandler;
   private Map<String, Object> sharedState;
   private Map<String, ?> options;
   private String passwordFile;
   private String passwordFileDisplayName;
   private boolean userSuppliedPasswordFile;
   private boolean hasJavaHomePermission;
   private Properties userCredentials;

   public void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4) {
      this.subject = var1;
      this.callbackHandler = var2;
      this.sharedState = (Map)Util.cast(var3);
      this.options = var4;
      this.tryFirstPass = "true".equalsIgnoreCase((String)var4.get("tryFirstPass"));
      this.useFirstPass = "true".equalsIgnoreCase((String)var4.get("useFirstPass"));
      this.storePass = "true".equalsIgnoreCase((String)var4.get("storePass"));
      this.clearPass = "true".equalsIgnoreCase((String)var4.get("clearPass"));
      this.passwordFile = (String)var4.get("passwordFile");
      this.passwordFileDisplayName = this.passwordFile;
      this.userSuppliedPasswordFile = true;
      if (this.passwordFile == null) {
         this.passwordFile = DEFAULT_PASSWORD_FILE_NAME;
         this.userSuppliedPasswordFile = false;

         try {
            System.getProperty("java.home");
            this.hasJavaHomePermission = true;
            this.passwordFileDisplayName = this.passwordFile;
         } catch (SecurityException var6) {
            this.hasJavaHomePermission = false;
            this.passwordFileDisplayName = "jmxremote.password";
         }
      }

   }

   public boolean login() throws LoginException {
      try {
         this.loadPasswordFile();
      } catch (IOException var5) {
         LoginException var2 = new LoginException("Error: unable to load the password file: " + this.passwordFileDisplayName);
         throw (LoginException)EnvHelp.initCause(var2, var5);
      }

      if (this.userCredentials == null) {
         throw new LoginException("Error: unable to locate the users' credentials.");
      } else {
         if (logger.debugOn()) {
            logger.debug("login", "Using password file: " + this.passwordFileDisplayName);
         }

         if (this.tryFirstPass) {
            try {
               this.attemptAuthentication(true);
               this.succeeded = true;
               if (logger.debugOn()) {
                  logger.debug("login", "Authentication using cached password has succeeded");
               }

               return true;
            } catch (LoginException var6) {
               this.cleanState();
               logger.debug("login", "Authentication using cached password has failed");
            }
         } else if (this.useFirstPass) {
            try {
               this.attemptAuthentication(true);
               this.succeeded = true;
               if (logger.debugOn()) {
                  logger.debug("login", "Authentication using cached password has succeeded");
               }

               return true;
            } catch (LoginException var3) {
               this.cleanState();
               logger.debug("login", "Authentication using cached password has failed");
               throw var3;
            }
         }

         if (logger.debugOn()) {
            logger.debug("login", "Acquiring password");
         }

         try {
            this.attemptAuthentication(false);
            this.succeeded = true;
            if (logger.debugOn()) {
               logger.debug("login", "Authentication has succeeded");
            }

            return true;
         } catch (LoginException var4) {
            this.cleanState();
            logger.debug("login", "Authentication has failed");
            throw var4;
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
         if (!this.subject.getPrincipals().contains(this.user)) {
            this.subject.getPrincipals().add(this.user);
         }

         if (logger.debugOn()) {
            logger.debug("commit", "Authentication has completed successfully");
         }

         this.cleanState();
         this.commitSucceeded = true;
         return true;
      }
   }

   public boolean abort() throws LoginException {
      if (logger.debugOn()) {
         logger.debug("abort", "Authentication has not completed successfully");
      }

      if (!this.succeeded) {
         return false;
      } else {
         if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanState();
            this.user = null;
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
         this.subject.getPrincipals().remove(this.user);
         this.cleanState();
         this.succeeded = false;
         this.commitSucceeded = false;
         this.user = null;
         if (logger.debugOn()) {
            logger.debug("logout", "Subject is being logged out");
         }

         return true;
      }
   }

   private void attemptAuthentication(boolean var1) throws LoginException {
      this.getUsernamePassword(var1);
      String var2;
      if ((var2 = this.userCredentials.getProperty(this.username)) != null && var2.equals(new String(this.password))) {
         if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
            this.sharedState.put("javax.security.auth.login.name", this.username);
            this.sharedState.put("javax.security.auth.login.password", this.password);
         }

         this.user = new JMXPrincipal(this.username);
         if (logger.debugOn()) {
            logger.debug("login", "User '" + this.username + "' successfully validated");
         }

      } else {
         if (logger.debugOn()) {
            logger.debug("login", "Invalid username or password");
         }

         throw new FailedLoginException("Invalid username or password");
      }
   }

   private void loadPasswordFile() throws IOException {
      FileInputStream var1;
      try {
         var1 = new FileInputStream(this.passwordFile);
      } catch (SecurityException var15) {
         if (!this.userSuppliedPasswordFile && !this.hasJavaHomePermission) {
            FilePermission var3 = new FilePermission(this.passwordFileDisplayName, "read");
            AccessControlException var4 = new AccessControlException("access denied " + var3.toString());
            var4.setStackTrace(var15.getStackTrace());
            throw var4;
         }

         throw var15;
      }

      try {
         BufferedInputStream var2 = new BufferedInputStream(var1);

         try {
            this.userCredentials = new Properties();
            this.userCredentials.load((InputStream)var2);
         } finally {
            var2.close();
         }
      } finally {
         var1.close();
      }

   }

   private void getUsernamePassword(boolean var1) throws LoginException {
      if (var1) {
         this.username = (String)this.sharedState.get("javax.security.auth.login.name");
         this.password = (char[])((char[])this.sharedState.get("javax.security.auth.login.password"));
      } else if (this.callbackHandler == null) {
         throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
      } else {
         Callback[] var2 = new Callback[]{new NameCallback("username"), new PasswordCallback("password", false)};

         LoginException var4;
         try {
            this.callbackHandler.handle(var2);
            this.username = ((NameCallback)var2[0]).getName();
            char[] var3 = ((PasswordCallback)var2[1]).getPassword();
            this.password = new char[var3.length];
            System.arraycopy(var3, 0, this.password, 0, var3.length);
            ((PasswordCallback)var2[1]).clearPassword();
         } catch (IOException var5) {
            var4 = new LoginException(var5.toString());
            throw (LoginException)EnvHelp.initCause(var4, var5);
         } catch (UnsupportedCallbackException var6) {
            var4 = new LoginException("Error: " + var6.getCallback().toString() + " not available to garner authentication information from the user");
            throw (LoginException)EnvHelp.initCause(var4, var6);
         }
      }
   }

   private void cleanState() {
      this.username = null;
      if (this.password != null) {
         Arrays.fill(this.password, ' ');
         this.password = null;
      }

      if (this.clearPass) {
         this.sharedState.remove("javax.security.auth.login.name");
         this.sharedState.remove("javax.security.auth.login.password");
      }

   }

   static {
      DEFAULT_PASSWORD_FILE_NAME = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.home"))) + File.separatorChar + "lib" + File.separatorChar + "management" + File.separatorChar + "jmxremote.password";
      logger = new ClassLogger("javax.management.remote.misc", "FileLoginModule");
   }
}
