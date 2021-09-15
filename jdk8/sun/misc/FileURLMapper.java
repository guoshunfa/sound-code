package sun.misc;

import java.io.File;
import java.net.URL;
import sun.net.www.ParseUtil;

public class FileURLMapper {
   URL url;
   String path;

   public FileURLMapper(URL var1) {
      this.url = var1;
   }

   public String getPath() {
      if (this.path != null) {
         return this.path;
      } else {
         String var1 = this.url.getHost();
         if (var1 == null || "".equals(var1) || "localhost".equalsIgnoreCase(var1)) {
            this.path = this.url.getFile();
            this.path = ParseUtil.decode(this.path);
         }

         return this.path;
      }
   }

   public boolean exists() {
      String var1 = this.getPath();
      if (var1 == null) {
         return false;
      } else {
         File var2 = new File(var1);
         return var2.exists();
      }
   }
}
