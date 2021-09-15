package sun.security.util;

import java.security.InvalidParameterException;
import java.util.regex.PatternSyntaxException;
import sun.security.action.GetPropertyAction;

public final class SecurityProviderConstants {
   private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
   public static final int DEF_DSA_KEY_SIZE;
   public static final int DEF_RSA_KEY_SIZE;
   public static final int DEF_DH_KEY_SIZE;
   public static final int DEF_EC_KEY_SIZE;
   private static final String KEY_LENGTH_PROP = "jdk.security.defaultKeySize";

   private SecurityProviderConstants() {
   }

   public static final int getDefDSASubprimeSize(int var0) {
      if (var0 <= 1024) {
         return 160;
      } else if (var0 == 2048) {
         return 224;
      } else if (var0 == 3072) {
         return 256;
      } else {
         throw new InvalidParameterException("Invalid DSA Prime Size: " + var0);
      }
   }

   static {
      String var0 = GetPropertyAction.privilegedGetProperty("jdk.security.defaultKeySize");
      int var1 = 2048;
      int var2 = 2048;
      int var3 = 2048;
      int var4 = 256;
      if (var0 != null) {
         try {
            String[] var5 = var0.split(",");
            String[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               String[] var10 = var9.split(":");
               if (var10.length != 2) {
                  if (debug != null) {
                     debug.println("Ignoring invalid pair in jdk.security.defaultKeySize property: " + var9);
                  }
               } else {
                  String var11 = var10[0].trim().toUpperCase();
                  boolean var12 = true;

                  int var16;
                  try {
                     var16 = Integer.parseInt(var10[1].trim());
                  } catch (NumberFormatException var14) {
                     if (debug != null) {
                        debug.println("Ignoring invalid value in jdk.security.defaultKeySize property: " + var9);
                     }
                     continue;
                  }

                  if (var11.equals("DSA")) {
                     var1 = var16;
                  } else if (var11.equals("RSA")) {
                     var2 = var16;
                  } else if (var11.equals("DH")) {
                     var3 = var16;
                  } else {
                     if (!var11.equals("EC")) {
                        if (debug != null) {
                           debug.println("Ignoring unsupported algo in jdk.security.defaultKeySize property: " + var9);
                        }
                        continue;
                     }

                     var4 = var16;
                  }

                  if (debug != null) {
                     debug.println("Overriding default " + var11 + " keysize with value from " + "jdk.security.defaultKeySize" + " property: " + var16);
                  }
               }
            }
         } catch (PatternSyntaxException var15) {
            if (debug != null) {
               debug.println("Unexpected exception while parsing jdk.security.defaultKeySize property: " + var15);
            }
         }
      }

      DEF_DSA_KEY_SIZE = var1;
      DEF_RSA_KEY_SIZE = var2;
      DEF_DH_KEY_SIZE = var3;
      DEF_EC_KEY_SIZE = var4;
   }
}
