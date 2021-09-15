package sun.awt.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileImageSource extends InputStreamImageSource {
   String imagefile;

   public FileImageSource(String var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkRead(var1);
      }

      this.imagefile = var1;
   }

   final boolean checkSecurity(Object var1, boolean var2) {
      return true;
   }

   protected ImageDecoder getDecoder() {
      if (this.imagefile == null) {
         return null;
      } else {
         BufferedInputStream var1;
         try {
            var1 = new BufferedInputStream(new FileInputStream(this.imagefile));
         } catch (FileNotFoundException var3) {
            return null;
         }

         return this.getDecoder(var1);
      }
   }
}
