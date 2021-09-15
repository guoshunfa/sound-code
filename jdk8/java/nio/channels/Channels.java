package java.nio.channels;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.ExecutionException;
import sun.nio.ch.ChannelInputStream;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

public final class Channels {
   private Channels() {
   }

   private static void checkNotNull(Object var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException("\"" + var1 + "\" is null!");
      }
   }

   private static void writeFullyImpl(WritableByteChannel var0, ByteBuffer var1) throws IOException {
      while(true) {
         if (var1.remaining() > 0) {
            int var2 = var0.write(var1);
            if (var2 > 0) {
               continue;
            }

            throw new RuntimeException("no bytes written");
         }

         return;
      }
   }

   private static void writeFully(WritableByteChannel var0, ByteBuffer var1) throws IOException {
      if (var0 instanceof SelectableChannel) {
         SelectableChannel var2 = (SelectableChannel)var0;
         synchronized(var2.blockingLock()) {
            if (!var2.isBlocking()) {
               throw new IllegalBlockingModeException();
            }

            writeFullyImpl(var0, var1);
         }
      } else {
         writeFullyImpl(var0, var1);
      }

   }

   public static InputStream newInputStream(ReadableByteChannel var0) {
      checkNotNull(var0, "ch");
      return new ChannelInputStream(var0);
   }

   public static OutputStream newOutputStream(final WritableByteChannel var0) {
      checkNotNull(var0, "ch");
      return new OutputStream() {
         private ByteBuffer bb = null;
         private byte[] bs = null;
         private byte[] b1 = null;

         public synchronized void write(int var1) throws IOException {
            if (this.b1 == null) {
               this.b1 = new byte[1];
            }

            this.b1[0] = (byte)var1;
            this.write(this.b1);
         }

         public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
            if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
               if (var3 != 0) {
                  ByteBuffer var4 = this.bs == var1 ? this.bb : ByteBuffer.wrap(var1);
                  var4.limit(Math.min(var2 + var3, var4.capacity()));
                  var4.position(var2);
                  this.bb = var4;
                  this.bs = var1;
                  Channels.writeFully(var0, var4);
               }
            } else {
               throw new IndexOutOfBoundsException();
            }
         }

         public void close() throws IOException {
            var0.close();
         }
      };
   }

   public static InputStream newInputStream(final AsynchronousByteChannel var0) {
      checkNotNull(var0, "ch");
      return new InputStream() {
         private ByteBuffer bb = null;
         private byte[] bs = null;
         private byte[] b1 = null;

         public synchronized int read() throws IOException {
            if (this.b1 == null) {
               this.b1 = new byte[1];
            }

            int var1 = this.read(this.b1);
            return var1 == 1 ? this.b1[0] & 255 : -1;
         }

         public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
            if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
               if (var3 == 0) {
                  return 0;
               } else {
                  ByteBuffer var4 = this.bs == var1 ? this.bb : ByteBuffer.wrap(var1);
                  var4.position(var2);
                  var4.limit(Math.min(var2 + var3, var4.capacity()));
                  this.bb = var4;
                  this.bs = var1;
                  boolean var5 = false;

                  try {
                     while(true) {
                        try {
                           int var6 = (Integer)var0.read(var4).get();
                           return var6;
                        } catch (ExecutionException var11) {
                           throw new IOException(var11.getCause());
                        } catch (InterruptedException var12) {
                           var5 = true;
                        }
                     }
                  } finally {
                     if (var5) {
                        Thread.currentThread().interrupt();
                     }

                  }
               }
            } else {
               throw new IndexOutOfBoundsException();
            }
         }

         public void close() throws IOException {
            var0.close();
         }
      };
   }

   public static OutputStream newOutputStream(final AsynchronousByteChannel var0) {
      checkNotNull(var0, "ch");
      return new OutputStream() {
         private ByteBuffer bb = null;
         private byte[] bs = null;
         private byte[] b1 = null;

         public synchronized void write(int var1) throws IOException {
            if (this.b1 == null) {
               this.b1 = new byte[1];
            }

            this.b1[0] = (byte)var1;
            this.write(this.b1);
         }

         public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
            if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
               if (var3 != 0) {
                  ByteBuffer var4 = this.bs == var1 ? this.bb : ByteBuffer.wrap(var1);
                  var4.limit(Math.min(var2 + var3, var4.capacity()));
                  var4.position(var2);
                  this.bb = var4;
                  this.bs = var1;
                  boolean var5 = false;

                  try {
                     while(var4.remaining() > 0) {
                        try {
                           var0.write(var4).get();
                        } catch (ExecutionException var11) {
                           throw new IOException(var11.getCause());
                        } catch (InterruptedException var12) {
                           var5 = true;
                        }
                     }
                  } finally {
                     if (var5) {
                        Thread.currentThread().interrupt();
                     }

                  }

               }
            } else {
               throw new IndexOutOfBoundsException();
            }
         }

         public void close() throws IOException {
            var0.close();
         }
      };
   }

   public static ReadableByteChannel newChannel(InputStream var0) {
      checkNotNull(var0, "in");
      return (ReadableByteChannel)(var0 instanceof FileInputStream && FileInputStream.class.equals(var0.getClass()) ? ((FileInputStream)var0).getChannel() : new Channels.ReadableByteChannelImpl(var0));
   }

   public static WritableByteChannel newChannel(OutputStream var0) {
      checkNotNull(var0, "out");
      return (WritableByteChannel)(var0 instanceof FileOutputStream && FileOutputStream.class.equals(var0.getClass()) ? ((FileOutputStream)var0).getChannel() : new Channels.WritableByteChannelImpl(var0));
   }

   public static Reader newReader(ReadableByteChannel var0, CharsetDecoder var1, int var2) {
      checkNotNull(var0, "ch");
      return StreamDecoder.forDecoder(var0, var1.reset(), var2);
   }

   public static Reader newReader(ReadableByteChannel var0, String var1) {
      checkNotNull(var1, "csName");
      return newReader(var0, Charset.forName(var1).newDecoder(), -1);
   }

   public static Writer newWriter(WritableByteChannel var0, CharsetEncoder var1, int var2) {
      checkNotNull(var0, "ch");
      return StreamEncoder.forEncoder(var0, var1.reset(), var2);
   }

   public static Writer newWriter(WritableByteChannel var0, String var1) {
      checkNotNull(var1, "csName");
      return newWriter(var0, Charset.forName(var1).newEncoder(), -1);
   }

   private static class WritableByteChannelImpl extends AbstractInterruptibleChannel implements WritableByteChannel {
      OutputStream out;
      private static final int TRANSFER_SIZE = 8192;
      private byte[] buf = new byte[0];
      private boolean open = true;
      private Object writeLock = new Object();

      WritableByteChannelImpl(OutputStream var1) {
         this.out = var1;
      }

      public int write(ByteBuffer var1) throws IOException {
         int var2 = var1.remaining();
         int var3 = 0;
         synchronized(this.writeLock) {
            int var5;
            for(; var3 < var2; var3 += var5) {
               var5 = Math.min(var2 - var3, 8192);
               if (this.buf.length < var5) {
                  this.buf = new byte[var5];
               }

               var1.get(this.buf, 0, var5);

               try {
                  this.begin();
                  this.out.write(this.buf, 0, var5);
               } finally {
                  this.end(var5 > 0);
               }
            }

            return var3;
         }
      }

      protected void implCloseChannel() throws IOException {
         this.out.close();
         this.open = false;
      }
   }

   private static class ReadableByteChannelImpl extends AbstractInterruptibleChannel implements ReadableByteChannel {
      InputStream in;
      private static final int TRANSFER_SIZE = 8192;
      private byte[] buf = new byte[0];
      private boolean open = true;
      private Object readLock = new Object();

      ReadableByteChannelImpl(InputStream var1) {
         this.in = var1;
      }

      public int read(ByteBuffer var1) throws IOException {
         int var2 = var1.remaining();
         int var3 = 0;
         int var4 = 0;
         synchronized(this.readLock) {
            while(var3 < var2) {
               int var6 = Math.min(var2 - var3, 8192);
               if (this.buf.length < var6) {
                  this.buf = new byte[var6];
               }

               if (var3 > 0 && this.in.available() <= 0) {
                  break;
               }

               try {
                  this.begin();
                  var4 = this.in.read(this.buf, 0, var6);
               } finally {
                  this.end(var4 > 0);
               }

               if (var4 < 0) {
                  break;
               }

               var3 += var4;
               var1.put(this.buf, 0, var4);
            }

            return var4 < 0 && var3 == 0 ? -1 : var3;
         }
      }

      protected void implCloseChannel() throws IOException {
         this.in.close();
         this.open = false;
      }
   }
}
