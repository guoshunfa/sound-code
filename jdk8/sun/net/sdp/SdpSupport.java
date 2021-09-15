package sun.net.sdp;

import java.io.FileDescriptor;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;

public final class SdpSupport {
   private static final String os = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));
   private static final boolean isSupported;
   private static final JavaIOFileDescriptorAccess fdAccess;

   private SdpSupport() {
   }

   public static FileDescriptor createSocket() throws IOException {
      if (!isSupported) {
         throw new UnsupportedOperationException("SDP not supported on this platform");
      } else {
         int var0 = create0();
         FileDescriptor var1 = new FileDescriptor();
         fdAccess.set(var1, var0);
         return var1;
      }
   }

   public static void convertSocket(FileDescriptor var0) throws IOException {
      if (!isSupported) {
         throw new UnsupportedOperationException("SDP not supported on this platform");
      } else {
         int var1 = fdAccess.get(var0);
         convert0(var1);
      }
   }

   private static native int create0() throws IOException;

   private static native void convert0(int var0) throws IOException;

   static {
      isSupported = os.equals("SunOS") || os.equals("Linux");
      fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
   }
}
