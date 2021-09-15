package sun.security.krb5.internal.rcache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class AuthTime {
   final int ctime;
   final int cusec;
   final String client;
   final String server;

   public AuthTime(String var1, String var2, int var3, int var4) {
      this.ctime = var3;
      this.cusec = var4;
      this.client = var1;
      this.server = var2;
   }

   public String toString() {
      return String.format("%d/%06d/----/%s", this.ctime, this.cusec, this.client);
   }

   private static String readStringWithLength(SeekableByteChannel var0) throws IOException {
      ByteBuffer var1 = ByteBuffer.allocate(4);
      var1.order(ByteOrder.nativeOrder());
      var0.read(var1);
      var1.flip();
      int var2 = var1.getInt();
      if (var2 > 1024) {
         throw new IOException("Invalid string length");
      } else {
         var1 = ByteBuffer.allocate(var2);
         if (var0.read(var1) != var2) {
            throw new IOException("Not enough string");
         } else {
            byte[] var3 = var1.array();
            return var3[var2 - 1] == 0 ? new String(var3, 0, var2 - 1, StandardCharsets.UTF_8) : new String(var3, StandardCharsets.UTF_8);
         }
      }
   }

   public static AuthTime readFrom(SeekableByteChannel var0) throws IOException {
      String var1 = readStringWithLength(var0);
      String var2 = readStringWithLength(var0);
      ByteBuffer var3 = ByteBuffer.allocate(8);
      var0.read(var3);
      var3.order(ByteOrder.nativeOrder());
      int var4 = var3.getInt(0);
      int var5 = var3.getInt(4);
      if (var1.isEmpty()) {
         StringTokenizer var6 = new StringTokenizer(var2, " :");
         if (var6.countTokens() != 6) {
            throw new IOException("Incorrect rcache style");
         } else {
            var6.nextToken();
            String var7 = var6.nextToken();
            var6.nextToken();
            var1 = var6.nextToken();
            var6.nextToken();
            var2 = var6.nextToken();
            return new AuthTimeWithHash(var1, var2, var5, var4, var7);
         }
      } else {
         return new AuthTime(var1, var2, var5, var4);
      }
   }

   protected byte[] encode0(String var1, String var2) {
      byte[] var3 = var1.getBytes(StandardCharsets.UTF_8);
      byte[] var4 = var2.getBytes(StandardCharsets.UTF_8);
      byte[] var5 = new byte[1];
      int var6 = 4 + var3.length + 1 + 4 + var4.length + 1 + 4 + 4;
      ByteBuffer var7 = ByteBuffer.allocate(var6).order(ByteOrder.nativeOrder());
      var7.putInt(var3.length + 1).put(var3).put(var5).putInt(var4.length + 1).put(var4).put(var5).putInt(this.cusec).putInt(this.ctime);
      return var7.array();
   }

   public byte[] encode(boolean var1) {
      return this.encode0(this.client, this.server);
   }
}
