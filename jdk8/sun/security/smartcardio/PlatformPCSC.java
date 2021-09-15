package sun.security.smartcardio;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.util.Debug;

class PlatformPCSC {
   static final Debug debug = Debug.getInstance("pcsc");
   static final Throwable initException = (Throwable)AccessController.doPrivileged(new PrivilegedAction<Throwable>() {
      public Throwable run() {
         try {
            System.loadLibrary("j2pcsc");
            String var1 = PlatformPCSC.getLibraryName();
            if (PlatformPCSC.debug != null) {
               PlatformPCSC.debug.println("Using PC/SC library: " + var1);
            }

            PlatformPCSC.initialize(var1);
            return null;
         } catch (Throwable var2) {
            return var2;
         }
      }
   });
   private static final String PROP_NAME = "sun.security.smartcardio.library";
   private static final String LIB1 = "/usr/$LIBISA/libpcsclite.so";
   private static final String LIB2 = "/usr/local/$LIBISA/libpcsclite.so";
   private static final String PCSC_FRAMEWORK = "/System/Library/Frameworks/PCSC.framework/Versions/Current/PCSC";
   static final int SCARD_PROTOCOL_T0 = 1;
   static final int SCARD_PROTOCOL_T1 = 2;
   static final int SCARD_PROTOCOL_RAW = 4;
   static final int SCARD_UNKNOWN = 1;
   static final int SCARD_ABSENT = 2;
   static final int SCARD_PRESENT = 4;
   static final int SCARD_SWALLOWED = 8;
   static final int SCARD_POWERED = 16;
   static final int SCARD_NEGOTIABLE = 32;
   static final int SCARD_SPECIFIC = 64;

   private static String expand(String var0) {
      int var1 = var0.indexOf("$LIBISA");
      if (var1 == -1) {
         return var0;
      } else {
         String var2 = var0.substring(0, var1);
         String var3 = var0.substring(var1 + 7);
         String var4;
         if ("64".equals(System.getProperty("sun.arch.data.model"))) {
            if ("SunOS".equals(System.getProperty("os.name"))) {
               var4 = "lib/64";
            } else {
               var4 = "lib64";
            }
         } else {
            var4 = "lib";
         }

         String var5 = var2 + var4 + var3;
         return var5;
      }
   }

   private static String getLibraryName() throws IOException {
      String var0 = expand(System.getProperty("sun.security.smartcardio.library", "").trim());
      if (var0.length() != 0) {
         return var0;
      } else {
         var0 = expand("/usr/$LIBISA/libpcsclite.so");
         if ((new File(var0)).isFile()) {
            return var0;
         } else {
            var0 = expand("/usr/local/$LIBISA/libpcsclite.so");
            if ((new File(var0)).isFile()) {
               return var0;
            } else {
               var0 = "/System/Library/Frameworks/PCSC.framework/Versions/Current/PCSC";
               if ((new File(var0)).isFile()) {
                  return var0;
               } else {
                  throw new IOException("No PC/SC library found on this system");
               }
            }
         }
      }
   }

   private static native void initialize(String var0);
}
