package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class NormalizerDataReader implements ICUBinary.Authenticate {
   private DataInputStream dataInputStream;
   private byte[] unicodeVersion;
   private static final byte[] DATA_FORMAT_ID = new byte[]{78, 111, 114, 109};
   private static final byte[] DATA_FORMAT_VERSION = new byte[]{2, 2, 5, 2};

   protected NormalizerDataReader(InputStream var1) throws IOException {
      this.unicodeVersion = ICUBinary.readHeader(var1, DATA_FORMAT_ID, this);
      this.dataInputStream = new DataInputStream(var1);
   }

   protected int[] readIndexes(int var1) throws IOException {
      int[] var2 = new int[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = this.dataInputStream.readInt();
      }

      return var2;
   }

   protected void read(byte[] var1, byte[] var2, byte[] var3, char[] var4, char[] var5) throws IOException {
      this.dataInputStream.readFully(var1);

      int var6;
      for(var6 = 0; var6 < var4.length; ++var6) {
         var4[var6] = this.dataInputStream.readChar();
      }

      for(var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = this.dataInputStream.readChar();
      }

      this.dataInputStream.readFully(var2);
      this.dataInputStream.readFully(var3);
   }

   public byte[] getDataFormatVersion() {
      return DATA_FORMAT_VERSION;
   }

   public boolean isDataVersionAcceptable(byte[] var1) {
      return var1[0] == DATA_FORMAT_VERSION[0] && var1[2] == DATA_FORMAT_VERSION[2] && var1[3] == DATA_FORMAT_VERSION[3];
   }

   public byte[] getUnicodeVersion() {
      return this.unicodeVersion;
   }
}
