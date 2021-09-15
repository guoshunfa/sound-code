package com.sun.jmx.remote.security;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXAuthenticator;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public final class JMXPluggableAuthenticator implements JMXAuthenticator {
   private LoginContext loginContext;
   private String username;
   private String password;
   private static final String LOGIN_CONFIG_PROP = "jmx.remote.x.login.config";
   private static final String LOGIN_CONFIG_NAME = "JMXPluggableAuthenticator";
   private static final String PASSWORD_FILE_PROP = "jmx.remote.x.password.file";
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXPluggableAuthenticator");

   public JMXPluggableAuthenticator(Map<?, ?> var1) {
      String var2 = null;
      String var3 = null;
      if (var1 != null) {
         var2 = (String)var1.get("jmx.remote.x.login.config");
         var3 = (String)var1.get("jmx.remote.x.password.file");
      }

      try {
         if (var2 != null) {
            this.loginContext = new LoginContext(var2, new JMXPluggableAuthenticator.JMXCallbackHandler());
         } else {
            SecurityManager var4 = System.getSecurityManager();
            if (var4 != null) {
               var4.checkPermission(new AuthPermission("createLoginContext.JMXPluggableAuthenticator"));
            }

            final String var5 = var3;

            try {
               this.loginContext = (LoginContext)AccessController.doPrivileged(new PrivilegedExceptionAction<LoginContext>() {
                  public LoginContext run() throws LoginException {
                     return new LoginContext("JMXPluggableAuthenticator", (Subject)null, JMXPluggableAuthenticator.this.new JMXCallbackHandler(), new JMXPluggableAuthenticator.FileLoginConfig(var5));
                  }
               });
            } catch (PrivilegedActionException var7) {
               throw (LoginException)var7.getException();
            }
         }
      } catch (LoginException var8) {
         authenticationFailure("authenticate", (Exception)var8);
      } catch (SecurityException var9) {
         authenticationFailure("authenticate", (Exception)var9);
      }

   }

   public Subject authenticate(Object var1) {
      if (!(var1 instanceof String[])) {
         if (var1 == null) {
            authenticationFailure("authenticate", "Credentials required");
         }

         String var2 = "Credentials should be String[] instead of " + var1.getClass().getName();
         authenticationFailure("authenticate", var2);
      }

      String[] var5 = (String[])((String[])var1);
      if (var5.length != 2) {
         String var3 = "Credentials should have 2 elements not " + var5.length;
         authenticationFailure("authenticate", var3);
      }

      this.username = var5[0];
      this.password = var5[1];
      if (this.username == null || this.password == null) {
         authenticationFailure("authenticate", "Username or password is null");
      }

      try {
         this.loginContext.login();
         final Subject var6 = this.loginContext.getSubject();
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               var6.setReadOnly();
               return null;
            }
         });
         return var6;
      } catch (LoginException var4) {
         authenticationFailure("authenticate", (Exception)var4);
         return null;
      }
   }

   private static void authenticationFailure(String var0, String var1) throws SecurityException {
      String var2 = "Authentication failed! " + var1;
      SecurityException var3 = new SecurityException(var2);
      logException(var0, var2, var3);
      throw var3;
   }

   private static void authenticationFailure(String var0, Exception var1) throws SecurityException {
      String var2;
      SecurityException var3;
      if (var1 instanceof SecurityException) {
         var2 = var1.getMessage();
         var3 = (SecurityException)var1;
      } else {
         var2 = "Authentication failed! " + var1.getMessage();
         SecurityException var4 = new SecurityException(var2);
         EnvHelp.initCause(var4, var1);
         var3 = var4;
      }

      logException(var0, var2, var3);
      throw var3;
   }

   private static void logException(String var0, String var1, Exception var2) {
      if (logger.traceOn()) {
         logger.trace(var0, var1);
      }

      if (logger.debugOn()) {
         logger.debug(var0, (Throwable)var2);
      }

   }

   private static class FileLoginConfig extends Configuration {
      private AppConfigurationEntry[] entries;
      private static final String FILE_LOGIN_MODULE = FileLoginModule.class.getName();
      private static final String PASSWORD_FILE_OPTION = "passwordFile";

      public FileLoginConfig(String var1) {
         Object var2;
         if (var1 != null) {
            var2 = new HashMap(1);
            ((Map)var2).put("passwordFile", var1);
         } else {
            var2 = Collections.emptyMap();
         }

         this.entries = new AppConfigurationEntry[]{new AppConfigurationEntry(FILE_LOGIN_MODULE, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, (Map)var2)};
      }

      public AppConfigurationEntry[] getAppConfigurationEntry(String var1) {
         return var1.equals("JMXPluggableAuthenticator") ? this.entries : null;
      }

      public void refresh() {
      }
   }

   private final class JMXCallbackHandler implements CallbackHandler {
      private JMXCallbackHandler() {
      }

      public void handle(Callback[] var1) throws IOException, UnsupportedCallbackException {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] instanceof NameCallback) {
               ((NameCallback)var1[var2]).setName(JMXPluggableAuthenticator.this.username);
            } else {
               if (!(var1[var2] instanceof PasswordCallback)) {
                  throw new UnsupportedCallbackException(var1[var2], "Unrecognized Callback");
               }

               ((PasswordCallback)var1[var2]).setPassword(JMXPluggableAuthenticator.this.password.toCharArray());
            }
         }

      }

      // $FF: synthetic method
      JMXCallbackHandler(Object var2) {
         this();
      }
   }
}
