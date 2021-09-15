package javax.swing.filechooser;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public final class FileNameExtensionFilter extends FileFilter {
   private final String description;
   private final String[] extensions;
   private final String[] lowerCaseExtensions;

   public FileNameExtensionFilter(String var1, String... var2) {
      if (var2 != null && var2.length != 0) {
         this.description = var1;
         this.extensions = new String[var2.length];
         this.lowerCaseExtensions = new String[var2.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3] == null || var2[var3].length() == 0) {
               throw new IllegalArgumentException("Each extension must be non-null and not empty");
            }

            this.extensions[var3] = var2[var3];
            this.lowerCaseExtensions[var3] = var2[var3].toLowerCase(Locale.ENGLISH);
         }

      } else {
         throw new IllegalArgumentException("Extensions must be non-null and not empty");
      }
   }

   public boolean accept(File var1) {
      if (var1 != null) {
         if (var1.isDirectory()) {
            return true;
         }

         String var2 = var1.getName();
         int var3 = var2.lastIndexOf(46);
         if (var3 > 0 && var3 < var2.length() - 1) {
            String var4 = var2.substring(var3 + 1).toLowerCase(Locale.ENGLISH);
            String[] var5 = this.lowerCaseExtensions;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               if (var4.equals(var8)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public String getDescription() {
      return this.description;
   }

   public String[] getExtensions() {
      String[] var1 = new String[this.extensions.length];
      System.arraycopy(this.extensions, 0, var1, 0, this.extensions.length);
      return var1;
   }

   public String toString() {
      return super.toString() + "[description=" + this.getDescription() + " extensions=" + Arrays.asList(this.getExtensions()) + "]";
   }
}
