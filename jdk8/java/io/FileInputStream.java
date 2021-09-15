package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class FileInputStream extends InputStream {
   private final FileDescriptor fd;
   private final String path;
   private FileChannel channel;
   private final Object closeLock;
   private volatile boolean closed;

   public FileInputStream(String var1) throws FileNotFoundException {
      this(var1 != null ? new File(var1) : null);
   }

   public FileInputStream(File var1) throws FileNotFoundException {
      this.channel = null;
      this.closeLock = new Object();
      this.closed = false;
      String var2 = var1 != null ? var1.getPath() : null;
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkRead(var2);
      }

      if (var2 == null) {
         throw new NullPointerException();
      } else if (var1.isInvalid()) {
         throw new FileNotFoundException("Invalid file path");
      } else {
         this.fd = new FileDescriptor();
         this.fd.attach(this);
         this.path = var2;
         this.open(var2);
      }
   }

   public FileInputStream(FileDescriptor var1) {
      this.channel = null;
      this.closeLock = new Object();
      this.closed = false;
      SecurityManager var2 = System.getSecurityManager();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var2 != null) {
            var2.checkRead(var1);
         }

         this.fd = var1;
         this.path = null;
         this.fd.attach(this);
      }
   }

   private native void open0(String var1) throws FileNotFoundException;

   private void open(String var1) throws FileNotFoundException {
      this.open0(var1);
   }

   public int read() throws IOException {
      return this.read0();
   }

   private native int read0() throws IOException;

   private native int readBytes(byte[] var1, int var2, int var3) throws IOException;

   public int read(byte[] var1) throws IOException {
      return this.readBytes(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.readBytes(var1, var2, var3);
   }

   public long skip(long var1) throws IOException {
      return this.skip0(var1);
   }

   private native long skip0(long var1) throws IOException;

   public int available() throws IOException {
      return this.available0();
   }

   private native int available0() throws IOException;

   public void close() throws IOException {
      synchronized(this.closeLock) {
         if (this.closed) {
            return;
         }

         this.closed = true;
      }

      if (this.channel != null) {
         this.channel.close();
      }

      this.fd.closeAll(new Closeable() {
         public void close() throws IOException {
            FileInputStream.this.close0();
         }
      });
   }

   public final FileDescriptor getFD() throws IOException {
      if (this.fd != null) {
         return this.fd;
      } else {
         throw new IOException();
      }
   }

   public FileChannel getChannel() {
      synchronized(this) {
         if (this.channel == null) {
            this.channel = FileChannelImpl.open(this.fd, this.path, true, false, this);
         }

         return this.channel;
      }
   }

   private static native void initIDs();

   private native void close0() throws IOException;

   protected void finalize() throws IOException {
      if (this.fd != null && this.fd != FileDescriptor.in) {
         this.close();
      }

   }

   static {
      initIDs();
   }
}
