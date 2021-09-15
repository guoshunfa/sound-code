package javax.security.auth.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import sun.security.util.Debug;
import sun.security.util.PendingException;
import sun.security.util.ResourcesMgr;

public class LoginContext {
   private static final String INIT_METHOD = "initialize";
   private static final String LOGIN_METHOD = "login";
   private static final String COMMIT_METHOD = "commit";
   private static final String ABORT_METHOD = "abort";
   private static final String LOGOUT_METHOD = "logout";
   private static final String OTHER = "other";
   private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
   private Subject subject;
   private boolean subjectProvided;
   private boolean loginSucceeded;
   private CallbackHandler callbackHandler;
   private Map<String, ?> state;
   private Configuration config;
   private AccessControlContext creatorAcc;
   private LoginContext.ModuleInfo[] moduleStack;
   private ClassLoader contextClassLoader;
   private static final Class<?>[] PARAMS = new Class[0];
   private int moduleIndex;
   private LoginException firstError;
   private LoginException firstRequiredError;
   private boolean success;
   private static final Debug debug = Debug.getInstance("logincontext", "\t[LoginContext]");

   private void init(String var1) throws LoginException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null && this.creatorAcc == null) {
         var2.checkPermission(new AuthPermission("createLoginContext." + var1));
      }

      if (var1 == null) {
         throw new LoginException(ResourcesMgr.getString("Invalid.null.input.name"));
      } else {
         if (this.config == null) {
            this.config = (Configuration)AccessController.doPrivileged(new PrivilegedAction<Configuration>() {
               public Configuration run() {
                  return Configuration.getConfiguration();
               }
            });
         }

         AppConfigurationEntry[] var3 = this.config.getAppConfigurationEntry(var1);
         if (var3 == null) {
            if (var2 != null && this.creatorAcc == null) {
               var2.checkPermission(new AuthPermission("createLoginContext.other"));
            }

            var3 = this.config.getAppConfigurationEntry("other");
            if (var3 == null) {
               MessageFormat var6 = new MessageFormat(ResourcesMgr.getString("No.LoginModules.configured.for.name"));
               Object[] var5 = new Object[]{var1};
               throw new LoginException(var6.format(var5));
            }
         }

         this.moduleStack = new LoginContext.ModuleInfo[var3.length];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            this.moduleStack[var4] = new LoginContext.ModuleInfo(new AppConfigurationEntry(var3[var4].getLoginModuleName(), var3[var4].getControlFlag(), var3[var4].getOptions()), (Object)null);
         }

         this.contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               ClassLoader var1 = Thread.currentThread().getContextClassLoader();
               if (var1 == null) {
                  var1 = ClassLoader.getSystemClassLoader();
               }

               return var1;
            }
         });
      }
   }

   private void loadDefaultCallbackHandler() throws LoginException {
      try {
         final ClassLoader var1 = this.contextClassLoader;
         this.callbackHandler = (CallbackHandler)AccessController.doPrivileged(new PrivilegedExceptionAction<CallbackHandler>() {
            public CallbackHandler run() throws Exception {
               String var1x = Security.getProperty("auth.login.defaultCallbackHandler");
               if (var1x != null && var1x.length() != 0) {
                  Class var2 = Class.forName(var1x, true, var1).asSubclass(CallbackHandler.class);
                  return (CallbackHandler)var2.newInstance();
               } else {
                  return null;
               }
            }
         });
      } catch (PrivilegedActionException var2) {
         throw new LoginException(var2.getException().toString());
      }

      if (this.callbackHandler != null && this.creatorAcc == null) {
         this.callbackHandler = new LoginContext.SecureCallbackHandler(AccessController.getContext(), this.callbackHandler);
      }

   }

   public LoginContext(String var1) throws LoginException {
      this.subject = null;
      this.subjectProvided = false;
      this.loginSucceeded = false;
      this.state = new HashMap();
      this.creatorAcc = null;
      this.contextClassLoader = null;
      this.moduleIndex = 0;
      this.firstError = null;
      this.firstRequiredError = null;
      this.success = false;
      this.init(var1);
      this.loadDefaultCallbackHandler();
   }

   public LoginContext(String var1, Subject var2) throws LoginException {
      this.subject = null;
      this.subjectProvided = false;
      this.loginSucceeded = false;
      this.state = new HashMap();
      this.creatorAcc = null;
      this.contextClassLoader = null;
      this.moduleIndex = 0;
      this.firstError = null;
      this.firstRequiredError = null;
      this.success = false;
      this.init(var1);
      if (var2 == null) {
         throw new LoginException(ResourcesMgr.getString("invalid.null.Subject.provided"));
      } else {
         this.subject = var2;
         this.subjectProvided = true;
         this.loadDefaultCallbackHandler();
      }
   }

   public LoginContext(String var1, CallbackHandler var2) throws LoginException {
      this.subject = null;
      this.subjectProvided = false;
      this.loginSucceeded = false;
      this.state = new HashMap();
      this.creatorAcc = null;
      this.contextClassLoader = null;
      this.moduleIndex = 0;
      this.firstError = null;
      this.firstRequiredError = null;
      this.success = false;
      this.init(var1);
      if (var2 == null) {
         throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
      } else {
         this.callbackHandler = new LoginContext.SecureCallbackHandler(AccessController.getContext(), var2);
      }
   }

   public LoginContext(String var1, Subject var2, CallbackHandler var3) throws LoginException {
      this(var1, var2);
      if (var3 == null) {
         throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
      } else {
         this.callbackHandler = new LoginContext.SecureCallbackHandler(AccessController.getContext(), var3);
      }
   }

   public LoginContext(String var1, Subject var2, CallbackHandler var3, Configuration var4) throws LoginException {
      this.subject = null;
      this.subjectProvided = false;
      this.loginSucceeded = false;
      this.state = new HashMap();
      this.creatorAcc = null;
      this.contextClassLoader = null;
      this.moduleIndex = 0;
      this.firstError = null;
      this.firstRequiredError = null;
      this.success = false;
      this.config = var4;
      if (var4 != null) {
         this.creatorAcc = AccessController.getContext();
      }

      this.init(var1);
      if (var2 != null) {
         this.subject = var2;
         this.subjectProvided = true;
      }

      if (var3 == null) {
         this.loadDefaultCallbackHandler();
      } else if (this.creatorAcc == null) {
         this.callbackHandler = new LoginContext.SecureCallbackHandler(AccessController.getContext(), var3);
      } else {
         this.callbackHandler = var3;
      }

   }

   public void login() throws LoginException {
      this.loginSucceeded = false;
      if (this.subject == null) {
         this.subject = new Subject();
      }

      try {
         this.invokePriv("login");
         this.invokePriv("commit");
         this.loginSucceeded = true;
      } catch (LoginException var4) {
         try {
            this.invokePriv("abort");
         } catch (LoginException var3) {
            throw var4;
         }

         throw var4;
      }
   }

   public void logout() throws LoginException {
      if (this.subject == null) {
         throw new LoginException(ResourcesMgr.getString("null.subject.logout.called.before.login"));
      } else {
         this.invokePriv("logout");
      }
   }

   public Subject getSubject() {
      return !this.loginSucceeded && !this.subjectProvided ? null : this.subject;
   }

   private void clearState() {
      this.moduleIndex = 0;
      this.firstError = null;
      this.firstRequiredError = null;
      this.success = false;
   }

   private void throwException(LoginException var1, LoginException var2) throws LoginException {
      this.clearState();
      LoginException var3 = var1 != null ? var1 : var2;
      throw var3;
   }

   private void invokePriv(final String var1) throws LoginException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws LoginException {
               LoginContext.this.invoke(var1);
               return null;
            }
         }, this.creatorAcc);
      } catch (PrivilegedActionException var3) {
         throw (LoginException)var3.getException();
      }
   }

   private void invoke(String var1) throws LoginException {
      for(int var2 = this.moduleIndex; var2 < this.moduleStack.length; ++this.moduleIndex) {
         LoginException var4;
         Object[] var15;
         try {
            boolean var3 = false;
            var4 = null;
            int var14;
            Method[] var19;
            if (this.moduleStack[var2].module != null) {
               var19 = this.moduleStack[var2].module.getClass().getMethods();
            } else {
               Class var16 = Class.forName(this.moduleStack[var2].entry.getLoginModuleName(), true, this.contextClassLoader);
               Constructor var6 = var16.getConstructor(PARAMS);
               Object[] var7 = new Object[0];
               this.moduleStack[var2].module = var6.newInstance(var7);
               var19 = this.moduleStack[var2].module.getClass().getMethods();

               for(var14 = 0; var14 < var19.length && !var19[var14].getName().equals("initialize"); ++var14) {
               }

               Object[] var8 = new Object[]{this.subject, this.callbackHandler, this.state, this.moduleStack[var2].entry.getOptions()};
               var19[var14].invoke(this.moduleStack[var2].module, var8);
            }

            for(var14 = 0; var14 < var19.length && !var19[var14].getName().equals(var1); ++var14) {
            }

            var15 = new Object[0];
            boolean var18 = (Boolean)var19[var14].invoke(this.moduleStack[var2].module, var15);
            if (var18) {
               if (!var1.equals("abort") && !var1.equals("logout") && this.moduleStack[var2].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT && this.firstRequiredError == null) {
                  this.clearState();
                  if (debug != null) {
                     debug.println(var1 + " SUFFICIENT success");
                  }

                  return;
               }

               if (debug != null) {
                  debug.println(var1 + " success");
               }

               this.success = true;
            } else if (debug != null) {
               debug.println(var1 + " ignored");
            }
         } catch (NoSuchMethodException var9) {
            MessageFormat var17 = new MessageFormat(ResourcesMgr.getString("unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor"));
            var15 = new Object[]{this.moduleStack[var2].entry.getLoginModuleName()};
            this.throwException((LoginException)null, new LoginException(var17.format(var15)));
         } catch (InstantiationException var10) {
            this.throwException((LoginException)null, new LoginException(ResourcesMgr.getString("unable.to.instantiate.LoginModule.") + var10.getMessage()));
         } catch (ClassNotFoundException var11) {
            this.throwException((LoginException)null, new LoginException(ResourcesMgr.getString("unable.to.find.LoginModule.class.") + var11.getMessage()));
         } catch (IllegalAccessException var12) {
            this.throwException((LoginException)null, new LoginException(ResourcesMgr.getString("unable.to.access.LoginModule.") + var12.getMessage()));
         } catch (InvocationTargetException var13) {
            if (var13.getCause() instanceof PendingException && var1.equals("login")) {
               throw (PendingException)var13.getCause();
            }

            if (var13.getCause() instanceof LoginException) {
               var4 = (LoginException)var13.getCause();
            } else if (var13.getCause() instanceof SecurityException) {
               var4 = new LoginException("Security Exception");
               var4.initCause(new SecurityException());
               if (debug != null) {
                  debug.println("original security exception with detail msg replaced by new exception with empty detail msg");
                  debug.println("original security exception: " + var13.getCause().toString());
               }
            } else {
               StringWriter var5 = new StringWriter();
               var13.getCause().printStackTrace(new PrintWriter(var5));
               var5.flush();
               var4 = new LoginException(var5.toString());
            }

            if (this.moduleStack[var2].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
               if (debug != null) {
                  debug.println(var1 + " REQUISITE failure");
               }

               if (!var1.equals("abort") && !var1.equals("logout")) {
                  this.throwException(this.firstRequiredError, var4);
               } else if (this.firstRequiredError == null) {
                  this.firstRequiredError = var4;
               }
            } else if (this.moduleStack[var2].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
               if (debug != null) {
                  debug.println(var1 + " REQUIRED failure");
               }

               if (this.firstRequiredError == null) {
                  this.firstRequiredError = var4;
               }
            } else {
               if (debug != null) {
                  debug.println(var1 + " OPTIONAL failure");
               }

               if (this.firstError == null) {
                  this.firstError = var4;
               }
            }
         }

         ++var2;
      }

      if (this.firstRequiredError != null) {
         this.throwException(this.firstRequiredError, (LoginException)null);
      } else if (!this.success && this.firstError != null) {
         this.throwException(this.firstError, (LoginException)null);
      } else {
         if (this.success) {
            this.clearState();
            return;
         }

         this.throwException(new LoginException(ResourcesMgr.getString("Login.Failure.all.modules.ignored")), (LoginException)null);
      }

   }

   private static class ModuleInfo {
      AppConfigurationEntry entry;
      Object module;

      ModuleInfo(AppConfigurationEntry var1, Object var2) {
         this.entry = var1;
         this.module = var2;
      }
   }

   private static class SecureCallbackHandler implements CallbackHandler {
      private final AccessControlContext acc;
      private final CallbackHandler ch;

      SecureCallbackHandler(AccessControlContext var1, CallbackHandler var2) {
         this.acc = var1;
         this.ch = var2;
      }

      public void handle(final Callback[] var1) throws IOException, UnsupportedCallbackException {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               public Void run() throws IOException, UnsupportedCallbackException {
                  SecureCallbackHandler.this.ch.handle(var1);
                  return null;
               }
            }, this.acc);
         } catch (PrivilegedActionException var3) {
            if (var3.getException() instanceof IOException) {
               throw (IOException)var3.getException();
            } else {
               throw (UnsupportedCallbackException)var3.getException();
            }
         }
      }
   }
}
