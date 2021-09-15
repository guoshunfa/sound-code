package sun.nio.fs;

import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class DefaultFileSystemProvider {
   private DefaultFileSystemProvider() {
   }

   private static FileSystemProvider createProvider(String var0) {
      Class var1;
      try {
         var1 = Class.forName(var0);
      } catch (ClassNotFoundException var4) {
         throw new AssertionError(var4);
      }

      try {
         return (FileSystemProvider)var1.newInstance();
      } catch (InstantiationException | IllegalAccessException var3) {
         throw new AssertionError(var3);
      }
   }

   public static FileSystemProvider create() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));
      if (var0.equals("SunOS")) {
         return createProvider("sun.nio.fs.SolarisFileSystemProvider");
      } else if (var0.equals("Linux")) {
         return createProvider("sun.nio.fs.LinuxFileSystemProvider");
      } else if (var0.contains("OS X")) {
         return createProvider("sun.nio.fs.MacOSXFileSystemProvider");
      } else if (var0.equals("AIX")) {
         return createProvider("sun.nio.fs.AixFileSystemProvider");
      } else {
         throw new AssertionError("Platform not recognized");
      }
   }
}
