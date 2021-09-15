package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class RandomAccessFile implements DataOutput, DataInput, Closeable {
   private FileDescriptor fd;
   private FileChannel channel;
   private boolean rw;
   private final String path;
   private Object closeLock;
   private volatile boolean closed;
   private static final int O_RDONLY = 1;
   private static final int O_RDWR = 2;
   private static final int O_SYNC = 4;
   private static final int O_DSYNC = 8;

   public RandomAccessFile(String var1, String var2) throws FileNotFoundException {
      this(var1 != null ? new File(var1) : null, var2);
   }

   public RandomAccessFile(File var1, String var2) throws FileNotFoundException {
      this.channel = null;
      this.closeLock = new Object();
      this.closed = false;
      String var3 = var1 != null ? var1.getPath() : null;
      int var4 = -1;
      if (var2.equals("r")) {
         var4 = 1;
      } else if (var2.startsWith("rw")) {
         var4 = 2;
         this.rw = true;
         if (var2.length() > 2) {
            if (var2.equals("rws")) {
               var4 |= 4;
            } else if (var2.equals("rwd")) {
               var4 |= 8;
            } else {
               var4 = -1;
            }
         }
      }

      if (var4 < 0) {
         throw new IllegalArgumentException("Illegal mode \"" + var2 + "\" must be one of \"r\", \"rw\", \"rws\", or \"rwd\"");
      } else {
         SecurityManager var5 = System.getSecurityManager();
         if (var5 != null) {
            var5.checkRead(var3);
            if (this.rw) {
               var5.checkWrite(var3);
            }
         }

         if (var3 == null) {
            throw new NullPointerException();
         } else if (var1.isInvalid()) {
            throw new FileNotFoundException("Invalid file path");
         } else {
            this.fd = new FileDescriptor();
            this.fd.attach(this);
            this.path = var3;
            this.open(var3, var4);
         }
      }
   }

   public final FileDescriptor getFD() throws IOException {
      if (this.fd != null) {
         return this.fd;
      } else {
         throw new IOException();
      }
   }

   public final FileChannel getChannel() {
      synchronized(this) {
         if (this.channel == null) {
            this.channel = FileChannelImpl.open(this.fd, this.path, true, this.rw, this);
         }

         return this.channel;
      }
   }

   private native void open0(String var1, int var2) throws FileNotFoundException;

   private void open(String var1, int var2) throws FileNotFoundException {
      this.open0(var1, var2);
   }

   public int read() throws IOException {
      return this.read0();
   }

   private native int read0() throws IOException;

   private native int readBytes(byte[] var1, int var2, int var3) throws IOException;

   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.readBytes(var1, var2, var3);
   }

   public int read(byte[] var1) throws IOException {
      return this.readBytes(var1, 0, var1.length);
   }

   public final void readFully(byte[] var1) throws IOException {
      this.readFully(var1, 0, var1.length);
   }

   public final void readFully(byte[] var1, int var2, int var3) throws IOException {
      int var4 = 0;

      do {
         int var5 = this.read(var1, var2 + var4, var3 - var4);
         if (var5 < 0) {
            throw new EOFException();
         }

         var4 += var5;
      } while(var4 < var3);

   }

   public int skipBytes(int var1) throws IOException {
      if (var1 <= 0) {
         return 0;
      } else {
         long var2 = this.getFilePointer();
         long var4 = this.length();
         long var6 = var2 + (long)var1;
         if (var6 > var4) {
            var6 = var4;
         }

         this.seek(var6);
         return (int)(var6 - var2);
      }
   }

   public void write(int var1) throws IOException {
      this.write0(var1);
   }

   private native void write0(int var1) throws IOException;

   private native void writeBytes(byte[] var1, int var2, int var3) throws IOException;

   public void write(byte[] var1) throws IOException {
      this.writeBytes(var1, 0, var1.length);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.writeBytes(var1, var2, var3);
   }

   public native long getFilePointer() throws IOException;

   public void seek(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IOException("Negative seek offset");
      } else {
         this.seek0(var1);
      }
   }

   private native void seek0(long var1) throws IOException;

   public native long length() throws IOException;

   public native void setLength(long var1) throws IOException;

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
            RandomAccessFile.this.close0();
         }
      });
   }

   public final boolean readBoolean() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1 != 0;
      }
   }

   public final byte readByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return (byte)var1;
      }
   }

   public final int readUnsignedByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1;
      }
   }

   public final short readShort() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (short)((var1 << 8) + (var2 << 0));
      }
   }

   public final int readUnsignedShort() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 8) + (var2 << 0);
      }
   }

   public final char readChar() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (char)((var1 << 8) + (var2 << 0));
      }
   }

   public final int readInt() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      int var3 = this.read();
      int var4 = this.read();
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 24) + (var2 << 16) + (var3 << 8) + (var4 << 0);
      }
   }

   public final long readLong() throws IOException {
      return ((long)this.readInt() << 32) + ((long)this.readInt() & 4294967295L);
   }

   public final float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   public final double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   public final String readLine() throws IOException {
      StringBuffer var1 = new StringBuffer();
      int var2 = -1;
      boolean var3 = false;

      while(!var3) {
         switch(var2 = this.read()) {
         case -1:
         case 10:
            var3 = true;
            break;
         case 13:
            var3 = true;
            long var4 = this.getFilePointer();
            if (this.read() != 10) {
               this.seek(var4);
            }
            break;
         default:
            var1.append((char)var2);
         }
      }

      if (var2 == -1 && var1.length() == 0) {
         return null;
      } else {
         return var1.toString();
      }
   }

   public final String readUTF() throws IOException {
      return DataInputStream.readUTF(this);
   }

   public final void writeBoolean(boolean var1) throws IOException {
      this.write(var1 ? 1 : 0);
   }

   public final void writeByte(int var1) throws IOException {
      this.write(var1);
   }

   public final void writeShort(int var1) throws IOException {
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public final void writeChar(int var1) throws IOException {
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public final void writeInt(int var1) throws IOException {
      this.write(var1 >>> 24 & 255);
      this.write(var1 >>> 16 & 255);
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public final void writeLong(long var1) throws IOException {
      this.write((int)(var1 >>> 56) & 255);
      this.write((int)(var1 >>> 48) & 255);
      this.write((int)(var1 >>> 40) & 255);
      this.write((int)(var1 >>> 32) & 255);
      this.write((int)(var1 >>> 24) & 255);
      this.write((int)(var1 >>> 16) & 255);
      this.write((int)(var1 >>> 8) & 255);
      this.write((int)(var1 >>> 0) & 255);
   }

   public final void writeFloat(float var1) throws IOException {
      this.writeInt(Float.floatToIntBits(var1));
   }

   public final void writeDouble(double var1) throws IOException {
      this.writeLong(Double.doubleToLongBits(var1));
   }

   public final void writeBytes(String var1) throws IOException {
      int var2 = var1.length();
      byte[] var3 = new byte[var2];
      var1.getBytes(0, var2, var3, 0);
      this.writeBytes(var3, 0, var2);
   }

   public final void writeChars(String var1) throws IOException {
      int var2 = var1.length();
      int var3 = 2 * var2;
      byte[] var4 = new byte[var3];
      char[] var5 = new char[var2];
      var1.getChars(0, var2, var5, 0);
      int var6 = 0;

      for(int var7 = 0; var6 < var2; ++var6) {
         var4[var7++] = (byte)(var5[var6] >>> 8);
         var4[var7++] = (byte)(var5[var6] >>> 0);
      }

      this.writeBytes(var4, 0, var3);
   }

   public final void writeUTF(String var1) throws IOException {
      DataOutputStream.writeUTF(var1, this);
   }

   private static native void initIDs();

   private native void close0() throws IOException;

   static {
      initIDs();
   }
}
