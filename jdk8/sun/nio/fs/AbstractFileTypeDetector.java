package sun.nio.fs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Locale;

public abstract class AbstractFileTypeDetector extends FileTypeDetector {
   private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";

   protected AbstractFileTypeDetector() {
   }

   public final String probeContentType(Path var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("'file' is null");
      } else {
         String var2 = this.implProbeContentType(var1);
         return var2 == null ? null : parse(var2);
      }
   }

   protected abstract String implProbeContentType(Path var1) throws IOException;

   private static String parse(String var0) {
      int var1 = var0.indexOf(47);
      int var2 = var0.indexOf(59);
      if (var1 < 0) {
         return null;
      } else {
         String var3 = var0.substring(0, var1).trim().toLowerCase(Locale.ENGLISH);
         if (!isValidToken(var3)) {
            return null;
         } else {
            String var4 = var2 < 0 ? var0.substring(var1 + 1) : var0.substring(var1 + 1, var2);
            var4 = var4.trim().toLowerCase(Locale.ENGLISH);
            if (!isValidToken(var4)) {
               return null;
            } else {
               StringBuilder var5 = new StringBuilder(var3.length() + var4.length() + 1);
               var5.append(var3);
               var5.append('/');
               var5.append(var4);
               return var5.toString();
            }
         }
      }
   }

   private static boolean isTokenChar(char var0) {
      return var0 > ' ' && var0 < 127 && "()<>@,;:/[]?=\\\"".indexOf(var0) < 0;
   }

   private static boolean isValidToken(String var0) {
      int var1 = var0.length();
      if (var1 == 0) {
         return false;
      } else {
         for(int var2 = 0; var2 < var1; ++var2) {
            if (!isTokenChar(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }
}
