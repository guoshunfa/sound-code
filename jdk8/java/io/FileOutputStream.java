package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class FileOutputStream extends OutputStream {
   private final FileDescriptor fd;
   private final boolean append;
   private FileChannel channel;
   private final String path;
   private final Object closeLock;
   private volatile boolean closed;

   public FileOutputStream(String var1) throws FileNotFoundException {
      this(var1 != null ? new File(var1) : null, false);
   }

   public FileOutputStream(String var1, boolean var2) throws FileNotFoundException {
      this(var1 != null ? new File(var1) : null, var2);
   }

   public FileOutputStream(File var1) throws FileNotFoundException {
      this(var1, false);
   }

   public FileOutputStream(File var1, boolean var2) throws FileNotFoundException {
      this.closeLock = new Object();
      this.closed = false;
      String var3 = var1 != null ? var1.getPath() : null;
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         var4.checkWrite(var3);
      }

      if (var3 == null) {
         throw new NullPointerException();
      } else if (var1.isInvalid()) {
         throw new FileNotFoundException("Invalid file path");
      } else {
         this.fd = new FileDescriptor();
         this.fd.attach(this);
         this.append = var2;
         this.path = var3;
         this.open(var3, var2);
      }
   }

   public FileOutputStream(FileDescriptor var1) {
      this.closeLock = new Object();
      this.closed = false;
      SecurityManager var2 = System.getSecurityManager();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var2 != null) {
            var2.checkWrite(var1);
         }

         this.fd = var1;
         this.append = false;
         this.path = null;
         this.fd.attach(this);
      }
   }

   private native void open0(String var1, boolean var2) throws FileNotFoundException;

   private void open(String var1, boolean var2) throws FileNotFoundException {
      this.open0(var1, var2);
   }

   private native void write(int var1, boolean var2) throws IOException;

   public void write(int var1) throws IOException {
      this.write(var1, this.append);
   }

   private native void writeBytes(byte[] var1, int var2, int var3, boolean var4) throws IOException;

   public void write(byte[] var1) throws IOException {
      this.writeBytes(var1, 0, var1.length, this.append);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.writeBytes(var1, var2, var3, this.append);
   }

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
            FileOutputStream.this.close0();
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
            this.channel = FileChannelImpl.open(this.fd, this.path, false, true, this.append, this);
         }

         return this.channel;
      }
   }

   protected void finalize() throws IOException {
      if (this.fd != null) {
         if (this.fd != FileDescriptor.out && this.fd != FileDescriptor.err) {
            this.close();
         } else {
            this.flush();
         }
      }

   }

   private native void close0() throws IOException;

   private static native void initIDs();

   static {
      initIDs();
   }
}
