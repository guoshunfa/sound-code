package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

class FileDispatcherImpl extends FileDispatcher {
   FileDispatcherImpl(boolean var1) {
   }

   FileDispatcherImpl() {
   }

   int read(FileDescriptor var1, long var2, int var4) throws IOException {
      return read0(var1, var2, var4);
   }

   int pread(FileDescriptor var1, long var2, int var4, long var5) throws IOException {
      return pread0(var1, var2, var4, var5);
   }

   long readv(FileDescriptor var1, long var2, int var4) throws IOException {
      return readv0(var1, var2, var4);
   }

   int write(FileDescriptor var1, long var2, int var4) throws IOException {
      return write0(var1, var2, var4);
   }

   int pwrite(FileDescriptor var1, long var2, int var4, long var5) throws IOException {
      return pwrite0(var1, var2, var4, var5);
   }

   long writev(FileDescriptor var1, long var2, int var4) throws IOException {
      return writev0(var1, var2, var4);
   }

   int force(FileDescriptor var1, boolean var2) throws IOException {
      return force0(var1, var2);
   }

   int truncate(FileDescriptor var1, long var2) throws IOException {
      return truncate0(var1, var2);
   }

   long size(FileDescriptor var1) throws IOException {
      return size0(var1);
   }

   int lock(FileDescriptor var1, boolean var2, long var3, long var5, boolean var7) throws IOException {
      return lock0(var1, var2, var3, var5, var7);
   }

   void release(FileDescriptor var1, long var2, long var4) throws IOException {
      release0(var1, var2, var4);
   }

   void close(FileDescriptor var1) throws IOException {
      close0(var1);
   }

   void preClose(FileDescriptor var1) throws IOException {
      preClose0(var1);
   }

   FileDescriptor duplicateForMapping(FileDescriptor var1) {
      return new FileDescriptor();
   }

   boolean canTransferToDirectly(SelectableChannel var1) {
      return true;
   }

   boolean transferToDirectlyNeedsPositionLock() {
      return false;
   }

   static native int read0(FileDescriptor var0, long var1, int var3) throws IOException;

   static native int pread0(FileDescriptor var0, long var1, int var3, long var4) throws IOException;

   static native long readv0(FileDescriptor var0, long var1, int var3) throws IOException;

   static native int write0(FileDescriptor var0, long var1, int var3) throws IOException;

   static native int pwrite0(FileDescriptor var0, long var1, int var3, long var4) throws IOException;

   static native long writev0(FileDescriptor var0, long var1, int var3) throws IOException;

   static native int force0(FileDescriptor var0, boolean var1) throws IOException;

   static native int truncate0(FileDescriptor var0, long var1) throws IOException;

   static native long size0(FileDescriptor var0) throws IOException;

   static native int lock0(FileDescriptor var0, boolean var1, long var2, long var4, boolean var6) throws IOException;

   static native void release0(FileDescriptor var0, long var1, long var3) throws IOException;

   static native void close0(FileDescriptor var0) throws IOException;

   static native void preClose0(FileDescriptor var0) throws IOException;

   static native void closeIntFD(int var0) throws IOException;

   static native void init();

   static {
      IOUtil.load();
      init();
   }
}
