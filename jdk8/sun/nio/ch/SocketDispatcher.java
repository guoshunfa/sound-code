package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;

class SocketDispatcher extends NativeDispatcher {
   int read(FileDescriptor var1, long var2, int var4) throws IOException {
      return FileDispatcherImpl.read0(var1, var2, var4);
   }

   long readv(FileDescriptor var1, long var2, int var4) throws IOException {
      return FileDispatcherImpl.readv0(var1, var2, var4);
   }

   int write(FileDescriptor var1, long var2, int var4) throws IOException {
      return FileDispatcherImpl.write0(var1, var2, var4);
   }

   long writev(FileDescriptor var1, long var2, int var4) throws IOException {
      return FileDispatcherImpl.writev0(var1, var2, var4);
   }

   void close(FileDescriptor var1) throws IOException {
      FileDispatcherImpl.close0(var1);
   }

   void preClose(FileDescriptor var1) throws IOException {
      FileDispatcherImpl.preClose0(var1);
   }
}
