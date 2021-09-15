package sun.security.jgss;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.GSSException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class GSSHeader {
   private ObjectIdentifier mechOid = null;
   private byte[] mechOidBytes = null;
   private int mechTokenLength = 0;
   public static final int TOKEN_ID = 96;

   public GSSHeader(ObjectIdentifier var1, int var2) throws IOException {
      this.mechOid = var1;
      DerOutputStream var3 = new DerOutputStream();
      var3.putOID(var1);
      this.mechOidBytes = var3.toByteArray();
      this.mechTokenLength = var2;
   }

   public GSSHeader(InputStream var1) throws IOException, GSSException {
      int var2 = var1.read();
      if (var2 != 96) {
         throw new GSSException(10, -1, "GSSHeader did not find the right tag");
      } else {
         int var3 = this.getLength(var1);
         DerValue var4 = new DerValue(var1);
         this.mechOidBytes = var4.toByteArray();
         this.mechOid = var4.getOID();
         this.mechTokenLength = var3 - this.mechOidBytes.length;
      }
   }

   public ObjectIdentifier getOid() {
      return this.mechOid;
   }

   public int getMechTokenLength() {
      return this.mechTokenLength;
   }

   public int getLength() {
      int var1 = this.mechOidBytes.length + this.mechTokenLength;
      return 1 + this.getLenFieldSize(var1) + this.mechOidBytes.length;
   }

   public static int getMaxMechTokenSize(ObjectIdentifier var0, int var1) {
      int var2 = 0;

      try {
         DerOutputStream var3 = new DerOutputStream();
         var3.putOID(var0);
         var2 = var3.toByteArray().length;
      } catch (IOException var4) {
      }

      var1 -= 1 + var2;
      var1 -= 5;
      return var1;
   }

   private int getLenFieldSize(int var1) {
      boolean var2 = true;
      byte var3;
      if (var1 < 128) {
         var3 = 1;
      } else if (var1 < 256) {
         var3 = 2;
      } else if (var1 < 65536) {
         var3 = 3;
      } else if (var1 < 16777216) {
         var3 = 4;
      } else {
         var3 = 5;
      }

      return var3;
   }

   public int encode(OutputStream var1) throws IOException {
      int var2 = 1 + this.mechOidBytes.length;
      var1.write(96);
      int var3 = this.mechOidBytes.length + this.mechTokenLength;
      var2 += this.putLength(var3, var1);
      var1.write(this.mechOidBytes);
      return var2;
   }

   private int getLength(InputStream var1) throws IOException {
      return this.getLength(var1.read(), var1);
   }

   private int getLength(int var1, InputStream var2) throws IOException {
      int var3;
      if ((var1 & 128) == 0) {
         var3 = var1;
      } else {
         int var4 = var1 & 127;
         if (var4 == 0) {
            return -1;
         }

         if (var4 < 0 || var4 > 4) {
            throw new IOException("DerInputStream.getLength(): lengthTag=" + var4 + ", " + (var4 < 0 ? "incorrect DER encoding." : "too big."));
         }

         for(var3 = 0; var4 > 0; --var4) {
            var3 <<= 8;
            var3 += 255 & var2.read();
         }

         if (var3 < 0) {
            throw new IOException("Invalid length bytes");
         }
      }

      return var3;
   }

   private int putLength(int var1, OutputStream var2) throws IOException {
      boolean var3 = false;
      byte var4;
      if (var1 < 128) {
         var2.write((byte)var1);
         var4 = 1;
      } else if (var1 < 256) {
         var2.write(-127);
         var2.write((byte)var1);
         var4 = 2;
      } else if (var1 < 65536) {
         var2.write(-126);
         var2.write((byte)(var1 >> 8));
         var2.write((byte)var1);
         var4 = 3;
      } else if (var1 < 16777216) {
         var2.write(-125);
         var2.write((byte)(var1 >> 16));
         var2.write((byte)(var1 >> 8));
         var2.write((byte)var1);
         var4 = 4;
      } else {
         var2.write(-124);
         var2.write((byte)(var1 >> 24));
         var2.write((byte)(var1 >> 16));
         var2.write((byte)(var1 >> 8));
         var2.write((byte)var1);
         var4 = 5;
      }

      return var4;
   }

   private void debug(String var1) {
      System.err.print(var1);
   }

   private String getHexBytes(byte[] var1, int var2) throws IOException {
      StringBuffer var3 = new StringBuffer();

      for(int var4 = 0; var4 < var2; ++var4) {
         int var5 = var1[var4] >> 4 & 15;
         int var6 = var1[var4] & 15;
         var3.append(Integer.toHexString(var5));
         var3.append(Integer.toHexString(var6));
         var3.append(' ');
      }

      return var3.toString();
   }
}
