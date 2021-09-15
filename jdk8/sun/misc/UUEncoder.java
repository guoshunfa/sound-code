package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoder extends CharacterEncoder {
   private String bufferName;
   private int mode;

   public UUEncoder() {
      this.bufferName = "encoder.buf";
      this.mode = 644;
   }

   public UUEncoder(String var1) {
      this.bufferName = var1;
      this.mode = 644;
   }

   public UUEncoder(String var1, int var2) {
      this.bufferName = var1;
      this.mode = var2;
   }

   protected int bytesPerAtom() {
      return 3;
   }

   protected int bytesPerLine() {
      return 45;
   }

   protected void encodeAtom(OutputStream var1, byte[] var2, int var3, int var4) throws IOException {
      byte var6 = 1;
      byte var7 = 1;
      byte var5 = var2[var3];
      if (var4 > 1) {
         var6 = var2[var3 + 1];
      }

      if (var4 > 2) {
         var7 = var2[var3 + 2];
      }

      int var8 = var5 >>> 2 & 63;
      int var9 = var5 << 4 & 48 | var6 >>> 4 & 15;
      int var10 = var6 << 2 & 60 | var7 >>> 6 & 3;
      int var11 = var7 & 63;
      var1.write(var8 + 32);
      var1.write(var9 + 32);
      var1.write(var10 + 32);
      var1.write(var11 + 32);
   }

   protected void encodeLinePrefix(OutputStream var1, int var2) throws IOException {
      var1.write((var2 & 63) + 32);
   }

   protected void encodeLineSuffix(OutputStream var1) throws IOException {
      this.pStream.println();
   }

   protected void encodeBufferPrefix(OutputStream var1) throws IOException {
      super.pStream = new PrintStream(var1);
      super.pStream.print("begin " + this.mode + " ");
      if (this.bufferName != null) {
         super.pStream.println(this.bufferName);
      } else {
         super.pStream.println("encoder.bin");
      }

      super.pStream.flush();
   }

   protected void encodeBufferSuffix(OutputStream var1) throws IOException {
      super.pStream.println(" \nend");
      super.pStream.flush();
   }
}
