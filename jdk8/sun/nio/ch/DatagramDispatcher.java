package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;

class DatagramDispatcher extends NativeDispatcher {
   int read(FileDescriptor var1, long var2, int var4) throws IOException {
      return read0(var1, var2, var4);
   }

   long readv(FileDescriptor var1, long var2, int var4) throws IOException {
      return readv0(var1, var2, var4);
   }

   int write(FileDescriptor var1, long var2, int var4) throws IOException {
      return write0(var1, var2, var4);
   }

   long writev(FileDescriptor var1, long var2, int var4) throws IOException {
      return writev0(var1, var2, var4);
   }

   void close(FileDescriptor var1) throws IOException {
      FileDispatcherImpl.close0(var1);
   }

   void preClose(FileDescriptor var1) throws IOException {
      FileDispatcherImpl.preClose0(var1);
   }

   static native int read0(FileDescriptor var0, long var1, int var3) throws IOException;

   static native long readv0(FileDescriptor var0, long var1, int var3) throws IOException;

   static native int write0(FileDescriptor var0, long var1, int var3) throws IOException;

   static native long writev0(FileDescriptor var0, long var1, int var3) throws IOException;

   static {
      IOUtil.load();
   }
}
