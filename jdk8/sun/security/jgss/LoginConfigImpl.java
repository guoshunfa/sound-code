package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.ietf.jgss.Oid;
import sun.security.action.GetPropertyAction;
import sun.security.util.Debug;

public class LoginConfigImpl extends Configuration {
   private final Configuration config;
   private final GSSCaller caller;
   private final String mechName;
   private static final Debug debug = Debug.getInstance("gssloginconfig", "\t[GSS LoginConfigImpl]");
   public static final boolean HTTP_USE_GLOBAL_CREDS;

   public LoginConfigImpl(GSSCaller var1, Oid var2) {
      this.caller = var1;
      if (var2.equals(GSSUtil.GSS_KRB5_MECH_OID)) {
         this.mechName = "krb5";
         this.config = (Configuration)AccessController.doPrivileged(new PrivilegedAction<Configuration>() {
            public Configuration run() {
               return Configuration.getConfiguration();
            }
         });
      } else {
         throw new IllegalArgumentException(var2.toString() + " not supported");
      }
   }

   public AppConfigurationEntry[] getAppConfigurationEntry(String var1) {
      AppConfigurationEntry[] var2 = null;
      if ("OTHER".equalsIgnoreCase(var1)) {
         return null;
      } else {
         String[] var3 = null;
         if (!"krb5".equals(this.mechName)) {
            throw new IllegalArgumentException(this.mechName + " not supported");
         } else {
            if (this.caller == GSSCaller.CALLER_INITIATE) {
               var3 = new String[]{"com.sun.security.jgss.krb5.initiate", "com.sun.security.jgss.initiate"};
            } else if (this.caller == GSSCaller.CALLER_ACCEPT) {
               var3 = new String[]{"com.sun.security.jgss.krb5.accept", "com.sun.security.jgss.accept"};
            } else if (this.caller == GSSCaller.CALLER_SSL_CLIENT) {
               var3 = new String[]{"com.sun.security.jgss.krb5.initiate", "com.sun.net.ssl.client"};
            } else if (this.caller == GSSCaller.CALLER_SSL_SERVER) {
               var3 = new String[]{"com.sun.security.jgss.krb5.accept", "com.sun.net.ssl.server"};
            } else if (this.caller instanceof HttpCaller) {
               var3 = new String[]{"com.sun.security.jgss.krb5.initiate"};
            } else if (this.caller == GSSCaller.CALLER_UNKNOWN) {
               throw new AssertionError("caller not defined");
            }

            String[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var4[var6];
               var2 = this.config.getAppConfigurationEntry(var7);
               if (debug != null) {
                  debug.println("Trying " + var7 + (var2 == null ? ": does not exist." : ": Found!"));
               }

               if (var2 != null) {
                  break;
               }
            }

            if (var2 == null) {
               if (debug != null) {
                  debug.println("Cannot read JGSS entry, use default values instead.");
               }

               var2 = this.getDefaultConfigurationEntry();
            }

            return var2;
         }
      }
   }

   private AppConfigurationEntry[] getDefaultConfigurationEntry() {
      HashMap var1 = new HashMap(2);
      if (this.mechName != null && !this.mechName.equals("krb5")) {
         return null;
      } else {
         if (isServerSide(this.caller)) {
            var1.put("useKeyTab", "true");
            var1.put("storeKey", "true");
            var1.put("doNotPrompt", "true");
            var1.put("principal", "*");
            var1.put("isInitiator", "false");
         } else {
            if (this.caller instanceof HttpCaller && !HTTP_USE_GLOBAL_CREDS) {
               var1.put("useTicketCache", "false");
            } else {
               var1.put("useTicketCache", "true");
            }

            var1.put("doNotPrompt", "false");
         }

         return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, var1)};
      }
   }

   private static boolean isServerSide(GSSCaller var0) {
      return GSSCaller.CALLER_ACCEPT == var0 || GSSCaller.CALLER_SSL_SERVER == var0;
   }

   static {
      String var0 = GetPropertyAction.privilegedGetProperty("http.use.global.creds");
      HTTP_USE_GLOBAL_CREDS = !"false".equalsIgnoreCase(var0);
   }
}
