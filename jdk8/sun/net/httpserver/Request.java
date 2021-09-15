package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class Request {
   static final int BUF_LEN = 2048;
   static final byte CR = 13;
   static final byte LF = 10;
   private String startLine;
   private SocketChannel chan;
   private InputStream is;
   private OutputStream os;
   char[] buf = new char[2048];
   int pos;
   StringBuffer lineBuf;
   Headers hdrs = null;

   Request(InputStream var1, OutputStream var2) throws IOException {
      this.is = var1;
      this.os = var2;

      do {
         this.startLine = this.readLine();
         if (this.startLine == null) {
            return;
         }
      } while(this.startLine != null && this.startLine.equals(""));

   }

   public InputStream inputStream() {
      return this.is;
   }

   public OutputStream outputStream() {
      return this.os;
   }

   public String readLine() throws IOException {
      boolean var1 = false;
      boolean var2 = false;
      this.pos = 0;
      this.lineBuf = new StringBuffer();

      while(!var2) {
         int var3 = this.is.read();
         if (var3 == -1) {
            return null;
         }

         if (var1) {
            if (var3 == 10) {
               var2 = true;
            } else {
               var1 = false;
               this.consume(13);
               this.consume(var3);
            }
         } else if (var3 == 13) {
            var1 = true;
         } else {
            this.consume(var3);
         }
      }

      this.lineBuf.append((char[])this.buf, 0, this.pos);
      return new String(this.lineBuf);
   }

   private void consume(int var1) {
      if (this.pos == 2048) {
         this.lineBuf.append(this.buf);
         this.pos = 0;
      }

      this.buf[this.pos++] = (char)var1;
   }

   public String requestLine() {
      return this.startLine;
   }

   Headers headers() throws IOException {
      if (this.hdrs != null) {
         return this.hdrs;
      } else {
         this.hdrs = new Headers();
         char[] var1 = new char[10];
         byte var2 = 0;
         int var3 = this.is.read();
         int var4;
         if (var3 == 13 || var3 == 10) {
            var4 = this.is.read();
            if (var4 == 13 || var4 == 10) {
               return this.hdrs;
            }

            var1[0] = (char)var3;
            var2 = 1;
            var3 = var4;
         }

         while(var3 != 10 && var3 != 13 && var3 >= 0) {
            var4 = -1;
            boolean var6 = var3 > 32;
            int var9 = var2 + 1;
            var1[var2] = (char)var3;

            label112:
            while(true) {
               int var5;
               if ((var5 = this.is.read()) < 0) {
                  var3 = -1;
                  break;
               }

               switch(var5) {
               case 9:
                  var5 = 32;
               case 32:
                  var6 = false;
                  break;
               case 10:
               case 13:
                  var3 = this.is.read();
                  if (var5 == 13 && var3 == 10) {
                     var3 = this.is.read();
                     if (var3 == 13) {
                        var3 = this.is.read();
                     }
                  }

                  if (var3 == 10 || var3 == 13 || var3 > 32) {
                     break label112;
                  }

                  var5 = 32;
                  break;
               case 58:
                  if (var6 && var9 > 0) {
                     var4 = var9;
                  }

                  var6 = false;
               }

               if (var9 >= var1.length) {
                  char[] var7 = new char[var1.length * 2];
                  System.arraycopy(var1, 0, var7, 0, var9);
                  var1 = var7;
               }

               var1[var9++] = (char)var5;
            }

            while(var9 > 0 && var1[var9 - 1] <= ' ') {
               --var9;
            }

            String var10;
            if (var4 <= 0) {
               var10 = null;
               var4 = 0;
            } else {
               var10 = String.copyValueOf(var1, 0, var4);
               if (var4 < var9 && var1[var4] == ':') {
                  ++var4;
               }

               while(var4 < var9 && var1[var4] <= ' ') {
                  ++var4;
               }
            }

            String var8;
            if (var4 >= var9) {
               var8 = new String();
            } else {
               var8 = String.copyValueOf(var1, var4, var9 - var4);
            }

            if (this.hdrs.size() >= ServerConfig.getMaxReqHeaders()) {
               throw new IOException("Maximum number of request headers (sun.net.httpserver.maxReqHeaders) exceeded, " + ServerConfig.getMaxReqHeaders() + ".");
            }

            this.hdrs.add(var10, var8);
            var2 = 0;
         }

         return this.hdrs;
      }
   }

   static class WriteStream extends OutputStream {
      SocketChannel channel;
      ByteBuffer buf;
      SelectionKey key;
      boolean closed;
      byte[] one;
      ServerImpl server;

      public WriteStream(ServerImpl var1, SocketChannel var2) throws IOException {
         this.channel = var2;
         this.server = var1;

         assert var2.isBlocking();

         this.closed = false;
         this.one = new byte[1];
         this.buf = ByteBuffer.allocate(4096);
      }

      public synchronized void write(int var1) throws IOException {
         this.one[0] = (byte)var1;
         this.write(this.one, 0, 1);
      }

      public synchronized void write(byte[] var1) throws IOException {
         this.write(var1, 0, var1.length);
      }

      public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
         int var4 = var3;
         if (this.closed) {
            throw new IOException("stream is closed");
         } else {
            int var5 = this.buf.capacity();
            int var6;
            if (var5 < var3) {
               var6 = var3 - var5;
               this.buf = ByteBuffer.allocate(2 * (var5 + var6));
            }

            this.buf.clear();
            this.buf.put(var1, var2, var3);
            this.buf.flip();

            do {
               if ((var6 = this.channel.write(this.buf)) >= var4) {
                  return;
               }

               var4 -= var6;
            } while(var4 != 0);

         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.channel.close();
            this.closed = true;
         }
      }
   }

   static class ReadStream extends InputStream {
      SocketChannel channel;
      ByteBuffer chanbuf;
      byte[] one;
      private boolean closed = false;
      private boolean eof = false;
      ByteBuffer markBuf;
      boolean marked;
      boolean reset;
      int readlimit;
      static long readTimeout;
      ServerImpl server;
      static final int BUFSIZE = 8192;

      public ReadStream(ServerImpl var1, SocketChannel var2) throws IOException {
         this.channel = var2;
         this.server = var1;
         this.chanbuf = ByteBuffer.allocate(8192);
         this.chanbuf.clear();
         this.one = new byte[1];
         this.closed = this.marked = this.reset = false;
      }

      public synchronized int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public synchronized int read() throws IOException {
         int var1 = this.read(this.one, 0, 1);
         return var1 == 1 ? this.one[0] & 255 : -1;
      }

      public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
         if (this.closed) {
            throw new IOException("Stream closed");
         } else if (this.eof) {
            return -1;
         } else {
            assert this.channel.isBlocking();

            if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
               int var5;
               if (this.reset) {
                  int var4 = this.markBuf.remaining();
                  var5 = var4 > var3 ? var3 : var4;
                  this.markBuf.get(var1, var2, var5);
                  if (var4 == var5) {
                     this.reset = false;
                  }
               } else {
                  this.chanbuf.clear();
                  if (var3 < 8192) {
                     this.chanbuf.limit(var3);
                  }

                  do {
                     var5 = this.channel.read(this.chanbuf);
                  } while(var5 == 0);

                  if (var5 == -1) {
                     this.eof = true;
                     return -1;
                  }

                  this.chanbuf.flip();
                  this.chanbuf.get(var1, var2, var5);
                  if (this.marked) {
                     try {
                        this.markBuf.put(var1, var2, var5);
                     } catch (BufferOverflowException var7) {
                        this.marked = false;
                     }
                  }
               }

               return var5;
            } else {
               throw new IndexOutOfBoundsException();
            }
         }
      }

      public boolean markSupported() {
         return true;
      }

      public synchronized int available() throws IOException {
         if (this.closed) {
            throw new IOException("Stream is closed");
         } else if (this.eof) {
            return -1;
         } else {
            return this.reset ? this.markBuf.remaining() : this.chanbuf.remaining();
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.channel.close();
            this.closed = true;
         }
      }

      public synchronized void mark(int var1) {
         if (!this.closed) {
            this.readlimit = var1;
            this.markBuf = ByteBuffer.allocate(var1);
            this.marked = true;
            this.reset = false;
         }
      }

      public synchronized void reset() throws IOException {
         if (!this.closed) {
            if (!this.marked) {
               throw new IOException("Stream not marked");
            } else {
               this.marked = false;
               this.reset = true;
               this.markBuf.flip();
            }
         }
      }
   }
}
