package sun.rmi.transport.proxy;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.rmi.runtime.Log;

class HttpInputStream extends FilterInputStream {
   protected int bytesLeft;
   protected int bytesLeftAtMark;

   public HttpInputStream(InputStream var1) throws IOException {
      super(var1);
      if (var1.markSupported()) {
         var1.mark(0);
      }

      DataInputStream var2 = new DataInputStream(var1);
      String var3 = "Content-length:".toLowerCase();
      boolean var4 = false;

      String var5;
      do {
         var5 = var2.readLine();
         if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
            RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received header line: \"" + var5 + "\"");
         }

         if (var5 == null) {
            throw new EOFException();
         }

         if (var5.toLowerCase().startsWith(var3)) {
            if (var4) {
               throw new IOException("Multiple Content-length entries found.");
            }

            this.bytesLeft = Integer.parseInt(var5.substring(var3.length()).trim());
            var4 = true;
         }
      } while(var5.length() != 0 && var5.charAt(0) != '\r' && var5.charAt(0) != '\n');

      if (!var4 || this.bytesLeft < 0) {
         this.bytesLeft = Integer.MAX_VALUE;
      }

      this.bytesLeftAtMark = this.bytesLeft;
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
         RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "content length: " + this.bytesLeft);
      }

   }

   public int available() throws IOException {
      int var1 = this.in.available();
      if (var1 > this.bytesLeft) {
         var1 = this.bytesLeft;
      }

      return var1;
   }

   public int read() throws IOException {
      if (this.bytesLeft > 0) {
         int var1 = this.in.read();
         if (var1 != -1) {
            --this.bytesLeft;
         }

         if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
            RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received byte: '" + ((var1 & 127) < 32 ? " " : String.valueOf((char)var1)) + "' " + var1);
         }

         return var1;
      } else {
         RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
         return -1;
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.bytesLeft == 0 && var3 > 0) {
         RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
         return -1;
      } else {
         if (var3 > this.bytesLeft) {
            var3 = this.bytesLeft;
         }

         int var4 = this.in.read(var1, var2, var3);
         this.bytesLeft -= var4;
         if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
            RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read " + var4 + " bytes, " + this.bytesLeft + " remaining");
         }

         return var4;
      }
   }

   public void mark(int var1) {
      this.in.mark(var1);
      if (this.in.markSupported()) {
         this.bytesLeftAtMark = this.bytesLeft;
      }

   }

   public void reset() throws IOException {
      this.in.reset();
      this.bytesLeft = this.bytesLeftAtMark;
   }

   public long skip(long var1) throws IOException {
      if (var1 > (long)this.bytesLeft) {
         var1 = (long)this.bytesLeft;
      }

      long var3 = this.in.skip(var1);
      this.bytesLeft = (int)((long)this.bytesLeft - var3);
      return var3;
   }
}
