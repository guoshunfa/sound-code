package sun.rmi.transport.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Connection;
import sun.rmi.transport.proxy.RMISocketInfo;

public class TCPConnection implements Connection {
   private Socket socket;
   private Channel channel;
   private InputStream in;
   private OutputStream out;
   private long expiration;
   private long lastuse;
   private long roundtrip;

   TCPConnection(TCPChannel var1, Socket var2, InputStream var3, OutputStream var4) {
      this.in = null;
      this.out = null;
      this.expiration = Long.MAX_VALUE;
      this.lastuse = Long.MIN_VALUE;
      this.roundtrip = 5L;
      this.socket = var2;
      this.channel = var1;
      this.in = var3;
      this.out = var4;
   }

   TCPConnection(TCPChannel var1, InputStream var2, OutputStream var3) {
      this(var1, (Socket)null, var2, var3);
   }

   TCPConnection(TCPChannel var1, Socket var2) {
      this(var1, var2, (InputStream)null, (OutputStream)null);
   }

   public OutputStream getOutputStream() throws IOException {
      if (this.out == null) {
         this.out = new BufferedOutputStream(this.socket.getOutputStream());
      }

      return this.out;
   }

   public void releaseOutputStream() throws IOException {
      if (this.out != null) {
         this.out.flush();
      }

   }

   public InputStream getInputStream() throws IOException {
      if (this.in == null) {
         this.in = new BufferedInputStream(this.socket.getInputStream());
      }

      return this.in;
   }

   public void releaseInputStream() {
   }

   public boolean isReusable() {
      return this.socket != null && this.socket instanceof RMISocketInfo ? ((RMISocketInfo)this.socket).isReusable() : true;
   }

   void setExpiration(long var1) {
      this.expiration = var1;
   }

   void setLastUseTime(long var1) {
      this.lastuse = var1;
   }

   boolean expired(long var1) {
      return this.expiration <= var1;
   }

   public boolean isDead() {
      long var3 = System.currentTimeMillis();
      if (this.roundtrip > 0L && var3 < this.lastuse + this.roundtrip) {
         return false;
      } else {
         InputStream var1;
         OutputStream var2;
         try {
            var1 = this.getInputStream();
            var2 = this.getOutputStream();
         } catch (IOException var8) {
            return true;
         }

         boolean var5 = false;

         int var9;
         try {
            var2.write(82);
            var2.flush();
            var9 = var1.read();
         } catch (IOException var7) {
            TCPTransport.tcpLog.log(Log.VERBOSE, "exception: ", var7);
            TCPTransport.tcpLog.log(Log.BRIEF, "server ping failed");
            return true;
         }

         if (var9 == 83) {
            this.roundtrip = (System.currentTimeMillis() - var3) * 2L;
            return false;
         } else {
            if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
               TCPTransport.tcpLog.log(Log.BRIEF, var9 == -1 ? "server has been deactivated" : "server protocol error: ping response = " + var9);
            }

            return true;
         }
      }
   }

   public void close() throws IOException {
      TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
      if (this.socket != null) {
         this.socket.close();
      } else {
         this.in.close();
         this.out.close();
      }

   }

   public Channel getChannel() {
      return this.channel;
   }
}
