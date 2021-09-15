package java.lang.invoke;

import java.io.FilePermission;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import sun.util.logging.PlatformLogger;

final class ProxyClassesDumper {
   private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   private static final char[] BAD_CHARS = new char[]{'\\', ':', '*', '?', '"', '<', '>', '|'};
   private static final String[] REPLACEMENT = new String[]{"%5C", "%3A", "%2A", "%3F", "%22", "%3C", "%3E", "%7C"};
   private final Path dumpDir;

   public static ProxyClassesDumper getInstance(String var0) {
      if (null == var0) {
         return null;
      } else {
         try {
            var0 = var0.trim();
            final Path var1 = Paths.get(var0.length() == 0 ? "." : var0);
            AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<Void>() {
               public Void run() {
                  ProxyClassesDumper.validateDumpDir(var1);
                  return null;
               }
            }), (AccessControlContext)null, new FilePermission("<<ALL FILES>>", "read, write"));
            return new ProxyClassesDumper(var1);
         } catch (InvalidPathException var2) {
            PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Path " + var0 + " is not valid - dumping disabled", (Throwable)var2);
         } catch (IllegalArgumentException var3) {
            PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning(var3.getMessage() + " - dumping disabled");
         }

         return null;
      }
   }

   private ProxyClassesDumper(Path var1) {
      this.dumpDir = (Path)Objects.requireNonNull(var1);
   }

   private static void validateDumpDir(Path var0) {
      if (!Files.exists(var0)) {
         throw new IllegalArgumentException("Directory " + var0 + " does not exist");
      } else if (!Files.isDirectory(var0)) {
         throw new IllegalArgumentException("Path " + var0 + " is not a directory");
      } else if (!Files.isWritable(var0)) {
         throw new IllegalArgumentException("Directory " + var0 + " is not writable");
      }
   }

   public static String encodeForFilename(String var0) {
      int var1 = var0.length();
      StringBuilder var2 = new StringBuilder(var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0.charAt(var3);
         if (var4 <= 31) {
            var2.append('%');
            var2.append(HEX[var4 >> 4 & 15]);
            var2.append(HEX[var4 & 15]);
         } else {
            int var5;
            for(var5 = 0; var5 < BAD_CHARS.length; ++var5) {
               if (var4 == BAD_CHARS[var5]) {
                  var2.append(REPLACEMENT[var5]);
                  break;
               }
            }

            if (var5 >= BAD_CHARS.length) {
               var2.append(var4);
            }
         }
      }

      return var2.toString();
   }

   public void dumpClass(String var1, byte[] var2) {
      Path var3;
      try {
         var3 = this.dumpDir.resolve(encodeForFilename(var1) + ".class");
      } catch (InvalidPathException var6) {
         PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Invalid path for class " + var1);
         return;
      }

      try {
         Path var4 = var3.getParent();
         Files.createDirectories(var4);
         Files.write(var3, var2);
      } catch (Exception var5) {
         PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Exception writing to path at " + var3.toString());
      }

   }
}
