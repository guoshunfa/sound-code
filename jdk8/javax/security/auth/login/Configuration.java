package javax.security.auth.login;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;
import javax.security.auth.AuthPermission;
import sun.security.jca.GetInstance;

public abstract class Configuration {
   private static Configuration configuration;
   private final AccessControlContext acc = AccessController.getContext();

   private static void checkPermission(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AuthPermission("createLoginConfiguration." + var0));
      }

   }

   protected Configuration() {
   }

   public static Configuration getConfiguration() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new AuthPermission("getLoginConfiguration"));
      }

      Class var1 = Configuration.class;
      synchronized(Configuration.class) {
         if (configuration == null) {
            final String var2 = null;
            var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  return Security.getProperty("login.configuration.provider");
               }
            });
            if (var2 == null) {
               var2 = "sun.security.provider.ConfigFile";
            }

            try {
               final Configuration var8 = (Configuration)AccessController.doPrivileged(new PrivilegedExceptionAction<Configuration>() {
                  public Configuration run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                     Class var1 = Class.forName(var2, false, Thread.currentThread().getContextClassLoader()).asSubclass(Configuration.class);
                     return (Configuration)var1.newInstance();
                  }
               });
               AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                  public Void run() {
                     Configuration.setConfiguration(var8);
                     return null;
                  }
               }, (AccessControlContext)Objects.requireNonNull(var8.acc));
            } catch (PrivilegedActionException var6) {
               Exception var4 = var6.getException();
               if (var4 instanceof InstantiationException) {
                  throw (SecurityException)(new SecurityException("Configuration error:" + var4.getCause().getMessage() + "\n")).initCause(var4.getCause());
               }

               throw (SecurityException)(new SecurityException("Configuration error: " + var4.toString() + "\n")).initCause(var4);
            }
         }

         return configuration;
      }
   }

   public static void setConfiguration(Configuration var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AuthPermission("setLoginConfiguration"));
      }

      configuration = var0;
   }

   public static Configuration getInstance(String var0, Configuration.Parameters var1) throws NoSuchAlgorithmException {
      checkPermission(var0);

      try {
         GetInstance.Instance var2 = GetInstance.getInstance("Configuration", ConfigurationSpi.class, var0, (Object)var1);
         return new Configuration.ConfigDelegate((ConfigurationSpi)var2.impl, var2.provider, var0, var1);
      } catch (NoSuchAlgorithmException var3) {
         return handleException(var3);
      }
   }

   public static Configuration getInstance(String var0, Configuration.Parameters var1, String var2) throws NoSuchProviderException, NoSuchAlgorithmException {
      if (var2 != null && var2.length() != 0) {
         checkPermission(var0);

         try {
            GetInstance.Instance var3 = GetInstance.getInstance("Configuration", ConfigurationSpi.class, var0, var1, (String)var2);
            return new Configuration.ConfigDelegate((ConfigurationSpi)var3.impl, var3.provider, var0, var1);
         } catch (NoSuchAlgorithmException var4) {
            return handleException(var4);
         }
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static Configuration getInstance(String var0, Configuration.Parameters var1, Provider var2) throws NoSuchAlgorithmException {
      if (var2 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         checkPermission(var0);

         try {
            GetInstance.Instance var3 = GetInstance.getInstance("Configuration", ConfigurationSpi.class, var0, var1, (Provider)var2);
            return new Configuration.ConfigDelegate((ConfigurationSpi)var3.impl, var3.provider, var0, var1);
         } catch (NoSuchAlgorithmException var4) {
            return handleException(var4);
         }
      }
   }

   private static Configuration handleException(NoSuchAlgorithmException var0) throws NoSuchAlgorithmException {
      Throwable var1 = var0.getCause();
      if (var1 instanceof IllegalArgumentException) {
         throw (IllegalArgumentException)var1;
      } else {
         throw var0;
      }
   }

   public Provider getProvider() {
      return null;
   }

   public String getType() {
      return null;
   }

   public Configuration.Parameters getParameters() {
      return null;
   }

   public abstract AppConfigurationEntry[] getAppConfigurationEntry(String var1);

   public void refresh() {
   }

   public interface Parameters {
   }

   private static class ConfigDelegate extends Configuration {
      private ConfigurationSpi spi;
      private Provider p;
      private String type;
      private Configuration.Parameters params;

      private ConfigDelegate(ConfigurationSpi var1, Provider var2, String var3, Configuration.Parameters var4) {
         this.spi = var1;
         this.p = var2;
         this.type = var3;
         this.params = var4;
      }

      public String getType() {
         return this.type;
      }

      public Configuration.Parameters getParameters() {
         return this.params;
      }

      public Provider getProvider() {
         return this.p;
      }

      public AppConfigurationEntry[] getAppConfigurationEntry(String var1) {
         return this.spi.engineGetAppConfigurationEntry(var1);
      }

      public void refresh() {
         this.spi.engineRefresh();
      }

      // $FF: synthetic method
      ConfigDelegate(ConfigurationSpi var1, Provider var2, String var3, Configuration.Parameters var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}
