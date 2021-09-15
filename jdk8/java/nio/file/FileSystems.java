package java.nio.file;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import sun.nio.fs.DefaultFileSystemProvider;

public final class FileSystems {
   private FileSystems() {
   }

   public static FileSystem getDefault() {
      return FileSystems.DefaultFileSystemHolder.defaultFileSystem;
   }

   public static FileSystem getFileSystem(URI var0) {
      String var1 = var0.getScheme();
      Iterator var2 = FileSystemProvider.installedProviders().iterator();

      FileSystemProvider var3;
      do {
         if (!var2.hasNext()) {
            throw new ProviderNotFoundException("Provider \"" + var1 + "\" not found");
         }

         var3 = (FileSystemProvider)var2.next();
      } while(!var1.equalsIgnoreCase(var3.getScheme()));

      return var3.getFileSystem(var0);
   }

   public static FileSystem newFileSystem(URI var0, Map<String, ?> var1) throws IOException {
      return newFileSystem(var0, var1, (ClassLoader)null);
   }

   public static FileSystem newFileSystem(URI var0, Map<String, ?> var1, ClassLoader var2) throws IOException {
      String var3 = var0.getScheme();
      Iterator var4 = FileSystemProvider.installedProviders().iterator();

      while(var4.hasNext()) {
         FileSystemProvider var5 = (FileSystemProvider)var4.next();
         if (var3.equalsIgnoreCase(var5.getScheme())) {
            return var5.newFileSystem(var0, var1);
         }
      }

      if (var2 != null) {
         ServiceLoader var7 = ServiceLoader.load(FileSystemProvider.class, var2);
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            FileSystemProvider var6 = (FileSystemProvider)var8.next();
            if (var3.equalsIgnoreCase(var6.getScheme())) {
               return var6.newFileSystem(var0, var1);
            }
         }
      }

      throw new ProviderNotFoundException("Provider \"" + var3 + "\" not found");
   }

   public static FileSystem newFileSystem(Path var0, ClassLoader var1) throws IOException {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         Map var2 = Collections.emptyMap();
         Iterator var3 = FileSystemProvider.installedProviders().iterator();

         while(var3.hasNext()) {
            FileSystemProvider var4 = (FileSystemProvider)var3.next();

            try {
               return var4.newFileSystem(var0, var2);
            } catch (UnsupportedOperationException var8) {
            }
         }

         if (var1 != null) {
            ServiceLoader var9 = ServiceLoader.load(FileSystemProvider.class, var1);
            Iterator var10 = var9.iterator();

            while(var10.hasNext()) {
               FileSystemProvider var5 = (FileSystemProvider)var10.next();

               try {
                  return var5.newFileSystem(var0, var2);
               } catch (UnsupportedOperationException var7) {
               }
            }
         }

         throw new ProviderNotFoundException("Provider not found");
      }
   }

   private static class DefaultFileSystemHolder {
      static final FileSystem defaultFileSystem = defaultFileSystem();

      private static FileSystem defaultFileSystem() {
         FileSystemProvider var0 = (FileSystemProvider)AccessController.doPrivileged(new PrivilegedAction<FileSystemProvider>() {
            public FileSystemProvider run() {
               return FileSystems.DefaultFileSystemHolder.getDefaultProvider();
            }
         });
         return var0.getFileSystem(URI.create("file:///"));
      }

      private static FileSystemProvider getDefaultProvider() {
         FileSystemProvider var0 = DefaultFileSystemProvider.create();
         String var1 = System.getProperty("java.nio.file.spi.DefaultFileSystemProvider");
         if (var1 != null) {
            String[] var2 = var1.split(",");
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String var5 = var2[var4];

               try {
                  Class var6 = Class.forName(var5, true, ClassLoader.getSystemClassLoader());
                  Constructor var7 = var6.getDeclaredConstructor(FileSystemProvider.class);
                  var0 = (FileSystemProvider)var7.newInstance(var0);
                  if (!var0.getScheme().equals("file")) {
                     throw new Error("Default provider must use scheme 'file'");
                  }
               } catch (Exception var8) {
                  throw new Error(var8);
               }
            }
         }

         return var0;
      }
   }
}
