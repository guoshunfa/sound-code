package sun.net.httpserver;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

class SSLStreams {
   SSLContext sslctx;
   SocketChannel chan;
   TimeSource time;
   ServerImpl server;
   SSLEngine engine;
   SSLStreams.EngineWrapper wrapper;
   SSLStreams.OutputStream os;
   SSLStreams.InputStream is;
   Lock handshaking = new ReentrantLock();
   int app_buf_size;
   int packet_buf_size;

   SSLStreams(ServerImpl var1, SSLContext var2, SocketChannel var3) throws IOException {
      this.server = var1;
      this.time = var1;
      this.sslctx = var2;
      this.chan = var3;
      InetSocketAddress var4 = (InetSocketAddress)var3.socket().getRemoteSocketAddress();
      this.engine = var2.createSSLEngine(var4.getHostName(), var4.getPort());
      this.engine.setUseClientMode(false);
      HttpsConfigurator var5 = var1.getHttpsConfigurator();
      this.configureEngine(var5, var4);
      this.wrapper = new SSLStreams.EngineWrapper(var3, this.engine);
   }

   private void configureEngine(HttpsConfigurator var1, InetSocketAddress var2) {
      if (var1 != null) {
         SSLStreams.Parameters var3 = new SSLStreams.Parameters(var1, var2);
         var1.configure(var3);
         SSLParameters var4 = var3.getSSLParameters();
         if (var4 != null) {
            this.engine.setSSLParameters(var4);
         } else {
            if (var3.getCipherSuites() != null) {
               try {
                  this.engine.setEnabledCipherSuites(var3.getCipherSuites());
               } catch (IllegalArgumentException var7) {
               }
            }

            this.engine.setNeedClientAuth(var3.getNeedClientAuth());
            this.engine.setWantClientAuth(var3.getWantClientAuth());
            if (var3.getProtocols() != null) {
               try {
                  this.engine.setEnabledProtocols(var3.getProtocols());
               } catch (IllegalArgumentException var6) {
               }
            }
         }
      }

   }

   void close() throws IOException {
      this.wrapper.close();
   }

   SSLStreams.InputStream getInputStream() throws IOException {
      if (this.is == null) {
         this.is = new SSLStreams.InputStream();
      }

      return this.is;
   }

   SSLStreams.OutputStream getOutputStream() throws IOException {
      if (this.os == null) {
         this.os = new SSLStreams.OutputStream();
      }

      return this.os;
   }

   SSLEngine getSSLEngine() {
      return this.engine;
   }

   void beginHandshake() throws SSLException {
      this.engine.beginHandshake();
   }

   private ByteBuffer allocate(SSLStreams.BufType var1) {
      return this.allocate(var1, -1);
   }

   private ByteBuffer allocate(SSLStreams.BufType var1, int var2) {
      assert this.engine != null;

      synchronized(this) {
         int var4;
         SSLSession var5;
         if (var1 == SSLStreams.BufType.PACKET) {
            if (this.packet_buf_size == 0) {
               var5 = this.engine.getSession();
               this.packet_buf_size = var5.getPacketBufferSize();
            }

            if (var2 > this.packet_buf_size) {
               this.packet_buf_size = var2;
            }

            var4 = this.packet_buf_size;
         } else {
            if (this.app_buf_size == 0) {
               var5 = this.engine.getSession();
               this.app_buf_size = var5.getApplicationBufferSize();
            }

            if (var2 > this.app_buf_size) {
               this.app_buf_size = var2;
            }

            var4 = this.app_buf_size;
         }

         return ByteBuffer.allocate(var4);
      }
   }

   private ByteBuffer realloc(ByteBuffer var1, boolean var2, SSLStreams.BufType var3) {
      synchronized(this) {
         int var5 = 2 * var1.capacity();
         ByteBuffer var6 = this.allocate(var3, var5);
         if (var2) {
            var1.flip();
         }

         var6.put(var1);
         return var6;
      }
   }

   public SSLStreams.WrapperResult sendData(ByteBuffer var1) throws IOException {
      SSLStreams.WrapperResult var2 = null;

      while(var1.remaining() > 0) {
         var2 = this.wrapper.wrapAndSend(var1);
         SSLEngineResult.Status var3 = var2.result.getStatus();
         if (var3 == SSLEngineResult.Status.CLOSED) {
            this.doClosure();
            return var2;
         }

         SSLEngineResult.HandshakeStatus var4 = var2.result.getHandshakeStatus();
         if (var4 != SSLEngineResult.HandshakeStatus.FINISHED && var4 != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            this.doHandshake(var4);
         }
      }

      return var2;
   }

   public SSLStreams.WrapperResult recvData(ByteBuffer var1) throws IOException {
      SSLStreams.WrapperResult var2 = null;

      assert var1.position() == 0;

      while(var1.position() == 0) {
         var2 = this.wrapper.recvAndUnwrap(var1);
         var1 = var2.buf != var1 ? var2.buf : var1;
         SSLEngineResult.Status var3 = var2.result.getStatus();
         if (var3 == SSLEngineResult.Status.CLOSED) {
            this.doClosure();
            return var2;
         }

         SSLEngineResult.HandshakeStatus var4 = var2.result.getHandshakeStatus();
         if (var4 != SSLEngineResult.HandshakeStatus.FINISHED && var4 != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            this.doHandshake(var4);
         }
      }

      var1.flip();
      return var2;
   }

   void doClosure() throws IOException {
      try {
         this.handshaking.lock();
         ByteBuffer var1 = this.allocate(SSLStreams.BufType.APPLICATION);

         SSLStreams.WrapperResult var2;
         do {
            var1.clear();
            var1.flip();
            var2 = this.wrapper.wrapAndSendX(var1, true);
         } while(var2.result.getStatus() != SSLEngineResult.Status.CLOSED);
      } finally {
         this.handshaking.unlock();
      }

   }

   void doHandshake(SSLEngineResult.HandshakeStatus var1) throws IOException {
      try {
         this.handshaking.lock();

         SSLStreams.WrapperResult var3;
         for(ByteBuffer var2 = this.allocate(SSLStreams.BufType.APPLICATION); var1 != SSLEngineResult.HandshakeStatus.FINISHED && var1 != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING; var1 = var3.result.getHandshakeStatus()) {
            var3 = null;
            Runnable var4;
            switch(var1) {
            case NEED_TASK:
               while((var4 = this.engine.getDelegatedTask()) != null) {
                  var4.run();
               }
            case NEED_WRAP:
               break;
            case NEED_UNWRAP:
               var2.clear();
               var3 = this.wrapper.recvAndUnwrap(var2);
               if (var3.buf != var2) {
                  var2 = var3.buf;
               }

               assert var2.position() == 0;
            default:
               continue;
            }

            var2.clear();
            var2.flip();
            var3 = this.wrapper.wrapAndSend(var2);
         }
      } finally {
         this.handshaking.unlock();
      }

   }

   class OutputStream extends java.io.OutputStream {
      ByteBuffer buf;
      boolean closed = false;
      byte[] single = new byte[1];

      OutputStream() {
         this.buf = SSLStreams.this.allocate(SSLStreams.BufType.APPLICATION);
      }

      public void write(int var1) throws IOException {
         this.single[0] = (byte)var1;
         this.write(this.single, 0, 1);
      }

      public void write(byte[] var1) throws IOException {
         this.write(var1, 0, var1.length);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (this.closed) {
            throw new IOException("output stream is closed");
         } else {
            while(var3 > 0) {
               int var4 = var3 > this.buf.capacity() ? this.buf.capacity() : var3;
               this.buf.clear();
               this.buf.put(var1, var2, var4);
               var3 -= var4;
               var2 += var4;
               this.buf.flip();
               SSLStreams.WrapperResult var5 = SSLStreams.this.sendData(this.buf);
               if (var5.result.getStatus() == SSLEngineResult.Status.CLOSED) {
                  this.closed = true;
                  if (var3 > 0) {
                     throw new IOException("output stream is closed");
                  }
               }
            }

         }
      }

      public void flush() throws IOException {
      }

      public void close() throws IOException {
         SSLStreams.WrapperResult var1 = null;
         SSLStreams.this.engine.closeOutbound();
         this.closed = true;
         SSLEngineResult.HandshakeStatus var2 = SSLEngineResult.HandshakeStatus.NEED_WRAP;
         this.buf.clear();

         while(var2 == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
            var1 = SSLStreams.this.wrapper.wrapAndSend(this.buf);
            var2 = var1.result.getHandshakeStatus();
         }

         assert var1.result.getStatus() == SSLEngineResult.Status.CLOSED;

      }
   }

   class InputStream extends java.io.InputStream {
      ByteBuffer bbuf;
      boolean closed = false;
      boolean eof = false;
      boolean needData = true;
      byte[] single = new byte[1];

      InputStream() {
         this.bbuf = SSLStreams.this.allocate(SSLStreams.BufType.APPLICATION);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (this.closed) {
            throw new IOException("SSL stream is closed");
         } else if (this.eof) {
            return 0;
         } else {
            int var4 = 0;
            if (!this.needData) {
               var4 = this.bbuf.remaining();
               this.needData = var4 == 0;
            }

            if (this.needData) {
               this.bbuf.clear();
               SSLStreams.WrapperResult var5 = SSLStreams.this.recvData(this.bbuf);
               this.bbuf = var5.buf == this.bbuf ? this.bbuf : var5.buf;
               if ((var4 = this.bbuf.remaining()) == 0) {
                  this.eof = true;
                  return 0;
               }

               this.needData = false;
            }

            if (var3 > var4) {
               var3 = var4;
            }

            this.bbuf.get(var1, var2, var3);
            return var3;
         }
      }

      public int available() throws IOException {
         return this.bbuf.remaining();
      }

      public boolean markSupported() {
         return false;
      }

      public void reset() throws IOException {
         throw new IOException("mark/reset not supported");
      }

      public long skip(long var1) throws IOException {
         int var3 = (int)var1;
         if (this.closed) {
            throw new IOException("SSL stream is closed");
         } else if (this.eof) {
            return 0L;
         } else {
            int var4;
            SSLStreams.WrapperResult var5;
            for(var4 = var3; var3 > 0; this.bbuf = var5.buf == this.bbuf ? this.bbuf : var5.buf) {
               if (this.bbuf.remaining() >= var3) {
                  this.bbuf.position(this.bbuf.position() + var3);
                  return (long)var4;
               }

               var3 -= this.bbuf.remaining();
               this.bbuf.clear();
               var5 = SSLStreams.this.recvData(this.bbuf);
            }

            return (long)var4;
         }
      }

      public void close() throws IOException {
         this.eof = true;
         SSLStreams.this.engine.closeInbound();
      }

      public int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public int read() throws IOException {
         int var1 = this.read(this.single, 0, 1);
         return var1 == 0 ? -1 : this.single[0] & 255;
      }
   }

   class EngineWrapper {
      SocketChannel chan;
      SSLEngine engine;
      Object wrapLock;
      Object unwrapLock;
      ByteBuffer unwrap_src;
      ByteBuffer wrap_dst;
      boolean closed = false;
      int u_remaining;

      EngineWrapper(SocketChannel var2, SSLEngine var3) throws IOException {
         this.chan = var2;
         this.engine = var3;
         this.wrapLock = new Object();
         this.unwrapLock = new Object();
         this.unwrap_src = SSLStreams.this.allocate(SSLStreams.BufType.PACKET);
         this.wrap_dst = SSLStreams.this.allocate(SSLStreams.BufType.PACKET);
      }

      void close() throws IOException {
      }

      SSLStreams.WrapperResult wrapAndSend(ByteBuffer var1) throws IOException {
         return this.wrapAndSendX(var1, false);
      }

      SSLStreams.WrapperResult wrapAndSendX(ByteBuffer var1, boolean var2) throws IOException {
         if (this.closed && !var2) {
            throw new IOException("Engine is closed");
         } else {
            SSLStreams.WrapperResult var4 = SSLStreams.this.new WrapperResult();
            synchronized(this.wrapLock) {
               this.wrap_dst.clear();

               SSLEngineResult.Status var3;
               do {
                  var4.result = this.engine.wrap(var1, this.wrap_dst);
                  var3 = var4.result.getStatus();
                  if (var3 == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                     this.wrap_dst = SSLStreams.this.realloc(this.wrap_dst, true, SSLStreams.BufType.PACKET);
                  }
               } while(var3 == SSLEngineResult.Status.BUFFER_OVERFLOW);

               if (var3 == SSLEngineResult.Status.CLOSED && !var2) {
                  this.closed = true;
                  return var4;
               } else {
                  if (var4.result.bytesProduced() > 0) {
                     this.wrap_dst.flip();
                     int var6 = this.wrap_dst.remaining();

                     assert var6 == var4.result.bytesProduced();

                     while(var6 > 0) {
                        var6 -= this.chan.write(this.wrap_dst);
                     }
                  }

                  return var4;
               }
            }
         }
      }

      SSLStreams.WrapperResult recvAndUnwrap(ByteBuffer var1) throws IOException {
         SSLEngineResult.Status var2 = SSLEngineResult.Status.OK;
         SSLStreams.WrapperResult var3 = SSLStreams.this.new WrapperResult();
         var3.buf = var1;
         if (this.closed) {
            throw new IOException("Engine is closed");
         } else {
            boolean var4;
            if (this.u_remaining > 0) {
               this.unwrap_src.compact();
               this.unwrap_src.flip();
               var4 = false;
            } else {
               this.unwrap_src.clear();
               var4 = true;
            }

            synchronized(this.unwrapLock) {
               do {
                  if (var4) {
                     int var6;
                     do {
                        var6 = this.chan.read(this.unwrap_src);
                     } while(var6 == 0);

                     if (var6 == -1) {
                        throw new IOException("connection closed for reading");
                     }

                     this.unwrap_src.flip();
                  }

                  var3.result = this.engine.unwrap(this.unwrap_src, var3.buf);
                  var2 = var3.result.getStatus();
                  if (var2 == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                     if (this.unwrap_src.limit() == this.unwrap_src.capacity()) {
                        this.unwrap_src = SSLStreams.this.realloc(this.unwrap_src, false, SSLStreams.BufType.PACKET);
                     } else {
                        this.unwrap_src.position(this.unwrap_src.limit());
                        this.unwrap_src.limit(this.unwrap_src.capacity());
                     }

                     var4 = true;
                  } else if (var2 == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                     var3.buf = SSLStreams.this.realloc(var3.buf, true, SSLStreams.BufType.APPLICATION);
                     var4 = false;
                  } else if (var2 == SSLEngineResult.Status.CLOSED) {
                     this.closed = true;
                     var3.buf.flip();
                     return var3;
                  }
               } while(var2 != SSLEngineResult.Status.OK);
            }

            this.u_remaining = this.unwrap_src.remaining();
            return var3;
         }
      }
   }

   static enum BufType {
      PACKET,
      APPLICATION;
   }

   class WrapperResult {
      SSLEngineResult result;
      ByteBuffer buf;
   }

   class Parameters extends HttpsParameters {
      InetSocketAddress addr;
      HttpsConfigurator cfg;
      SSLParameters params;

      Parameters(HttpsConfigurator var2, InetSocketAddress var3) {
         this.addr = var3;
         this.cfg = var2;
      }

      public InetSocketAddress getClientAddress() {
         return this.addr;
      }

      public HttpsConfigurator getHttpsConfigurator() {
         return this.cfg;
      }

      public void setSSLParameters(SSLParameters var1) {
         this.params = var1;
      }

      SSLParameters getSSLParameters() {
         return this.params;
      }
   }
}
