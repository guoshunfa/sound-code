package java.nio.file;

import java.net.URI;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;

public final class Paths {
   private Paths() {
   }

   public static Path get(String var0, String... var1) {
      return FileSystems.getDefault().getPath(var0, var1);
   }

   public static Path get(URI var0) {
      String var1 = var0.getScheme();
      if (var1 == null) {
         throw new IllegalArgumentException("Missing scheme");
      } else if (var1.equalsIgnoreCase("file")) {
         return FileSystems.getDefault().provider().getPath(var0);
      } else {
         Iterator var2 = FileSystemProvider.installedProviders().iterator();

         FileSystemProvider var3;
         do {
            if (!var2.hasNext()) {
               throw new FileSystemNotFoundException("Provider \"" + var1 + "\" not installed");
            }

            var3 = (FileSystemProvider)var2.next();
         } while(!var3.getScheme().equalsIgnoreCase(var1));

         return var3.getPath(var0);
      }
   }
}
