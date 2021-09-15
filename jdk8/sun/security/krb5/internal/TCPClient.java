package sun.security.krb5.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import sun.misc.IOUtils;

class TCPClient extends NetClient {
   private Socket tcpSocket = new Socket();
   private BufferedOutputStream out;
   private BufferedInputStream in;

   TCPClient(String var1, int var2, int var3) throws IOException {
      this.tcpSocket.connect(new InetSocketAddress(var1, var2), var3);
      this.out = new BufferedOutputStream(this.tcpSocket.getOutputStream());
      this.in = new BufferedInputStream(this.tcpSocket.getInputStream());
      this.tcpSocket.setSoTimeout(var3);
   }

   public void send(byte[] var1) throws IOException {
      byte[] var2 = new byte[4];
      intToNetworkByteOrder(var1.length, var2, 0, 4);
      this.out.write(var2);
      this.out.write(var1);
      this.out.flush();
   }

   public byte[] receive() throws IOException {
      byte[] var1 = new byte[4];
      int var2 = this.readFully(var1, 4);
      if (var2 != 4) {
         if (Krb5.DEBUG) {
            System.out.println(">>>DEBUG: TCPClient could not read length field");
         }

         return null;
      } else {
         int var3 = networkByteOrderToInt(var1, 0, 4);
         if (Krb5.DEBUG) {
            System.out.println(">>>DEBUG: TCPClient reading " + var3 + " bytes");
         }

         if (var3 <= 0) {
            if (Krb5.DEBUG) {
               System.out.println(">>>DEBUG: TCPClient zero or negative length field: " + var3);
            }

            return null;
         } else {
            try {
               return IOUtils.readFully(this.in, var3, true);
            } catch (IOException var5) {
               if (Krb5.DEBUG) {
                  System.out.println(">>>DEBUG: TCPClient could not read complete packet (" + var3 + "/" + var2 + ")");
               }

               return null;
            }
         }
      }
   }

   public void close() throws IOException {
      this.tcpSocket.close();
   }

   private int readFully(byte[] var1, int var2) throws IOException {
      int var3;
      int var4;
      for(var4 = 0; var2 > 0; var2 -= var3) {
         var3 = this.in.read(var1, var4, var2);
         if (var3 == -1) {
            return var4 == 0 ? -1 : var4;
         }

         var4 += var3;
      }

      return var4;
   }

   private static int networkByteOrderToInt(byte[] var0, int var1, int var2) {
      if (var2 > 4) {
         throw new IllegalArgumentException("Cannot handle more than 4 bytes");
      } else {
         int var3 = 0;

         for(int var4 = 0; var4 < var2; ++var4) {
            var3 <<= 8;
            var3 |= var0[var1 + var4] & 255;
         }

         return var3;
      }
   }

   private static void intToNetworkByteOrder(int var0, byte[] var1, int var2, int var3) {
      if (var3 > 4) {
         throw new IllegalArgumentException("Cannot handle more than 4 bytes");
      } else {
         for(int var4 = var3 - 1; var4 >= 0; --var4) {
            var1[var2 + var4] = (byte)(var0 & 255);
            var0 >>>= 8;
         }

      }
   }
}
