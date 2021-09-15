package sun.security.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.text.Collator;
import java.util.Locale;
import java.util.ResourceBundle;

public class KeyStoreUtil {
   private static final String JKS = "jks";
   private static final Collator collator = Collator.getInstance();

   private KeyStoreUtil() {
   }

   public static boolean isWindowsKeyStore(String var0) {
      return var0 != null && (var0.equalsIgnoreCase("Windows-MY") || var0.equalsIgnoreCase("Windows-ROOT"));
   }

   public static String niceStoreTypeName(String var0) {
      if (var0.equalsIgnoreCase("Windows-MY")) {
         return "Windows-MY";
      } else {
         return var0.equalsIgnoreCase("Windows-ROOT") ? "Windows-ROOT" : var0.toUpperCase(Locale.ENGLISH);
      }
   }

   public static KeyStore getCacertsKeyStore() throws Exception {
      String var0 = File.separator;
      File var1 = new File(System.getProperty("java.home") + var0 + "lib" + var0 + "security" + var0 + "cacerts");
      if (!var1.exists()) {
         return null;
      } else {
         KeyStore var2 = null;
         FileInputStream var3 = new FileInputStream(var1);
         Throwable var4 = null;

         try {
            var2 = KeyStore.getInstance("jks");
            var2.load(var3, (char[])null);
         } catch (Throwable var13) {
            var4 = var13;
            throw var13;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var2;
      }
   }

   public static char[] getPassWithModifier(String var0, String var1, ResourceBundle var2) {
      if (var0 == null) {
         return var1.toCharArray();
      } else if (collator.compare(var0, "env") == 0) {
         String var23 = System.getenv(var1);
         if (var23 == null) {
            System.err.println(var2.getString("Cannot.find.environment.variable.") + var1);
            return null;
         } else {
            return var23.toCharArray();
         }
      } else if (collator.compare(var0, "file") == 0) {
         try {
            URL var3 = null;

            try {
               var3 = new URL(var1);
            } catch (MalformedURLException var21) {
               File var5 = new File(var1);
               if (!var5.exists()) {
                  System.err.println(var2.getString("Cannot.find.file.") + var1);
                  return null;
               }

               var3 = var5.toURI().toURL();
            }

            BufferedReader var4 = new BufferedReader(new InputStreamReader(var3.openStream()));
            Throwable var24 = null;

            char[] var7;
            try {
               String var6 = var4.readLine();
               if (var6 == null) {
                  var7 = new char[0];
                  return var7;
               }

               var7 = var6.toCharArray();
            } catch (Throwable var19) {
               var24 = var19;
               throw var19;
            } finally {
               if (var4 != null) {
                  if (var24 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var18) {
                        var24.addSuppressed(var18);
                     }
                  } else {
                     var4.close();
                  }
               }

            }

            return var7;
         } catch (IOException var22) {
            System.err.println((Object)var22);
            return null;
         }
      } else {
         System.err.println(var2.getString("Unknown.password.type.") + var0);
         return null;
      }
   }

   static {
      collator.setStrength(0);
   }
}
