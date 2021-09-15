package javax.security.auth.login;

import java.util.Collections;
import java.util.Map;
import sun.security.util.ResourcesMgr;

public class AppConfigurationEntry {
   private String loginModuleName;
   private AppConfigurationEntry.LoginModuleControlFlag controlFlag;
   private Map<String, ?> options;

   public AppConfigurationEntry(String var1, AppConfigurationEntry.LoginModuleControlFlag var2, Map<String, ?> var3) {
      if (var1 != null && var1.length() != 0 && (var2 == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED || var2 == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE || var2 == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT || var2 == AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL) && var3 != null) {
         this.loginModuleName = var1;
         this.controlFlag = var2;
         this.options = Collections.unmodifiableMap(var3);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getLoginModuleName() {
      return this.loginModuleName;
   }

   public AppConfigurationEntry.LoginModuleControlFlag getControlFlag() {
      return this.controlFlag;
   }

   public Map<String, ?> getOptions() {
      return this.options;
   }

   public static class LoginModuleControlFlag {
      private String controlFlag;
      public static final AppConfigurationEntry.LoginModuleControlFlag REQUIRED = new AppConfigurationEntry.LoginModuleControlFlag("required");
      public static final AppConfigurationEntry.LoginModuleControlFlag REQUISITE = new AppConfigurationEntry.LoginModuleControlFlag("requisite");
      public static final AppConfigurationEntry.LoginModuleControlFlag SUFFICIENT = new AppConfigurationEntry.LoginModuleControlFlag("sufficient");
      public static final AppConfigurationEntry.LoginModuleControlFlag OPTIONAL = new AppConfigurationEntry.LoginModuleControlFlag("optional");

      private LoginModuleControlFlag(String var1) {
         this.controlFlag = var1;
      }

      public String toString() {
         return ResourcesMgr.getString("LoginModuleControlFlag.") + this.controlFlag;
      }
   }
}
