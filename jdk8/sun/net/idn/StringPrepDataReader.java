package sun.net.idn;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.text.normalizer.ICUBinary;

final class StringPrepDataReader implements ICUBinary.Authenticate {
   private DataInputStream dataInputStream;
   private byte[] unicodeVersion;
   private static final byte[] DATA_FORMAT_ID = new byte[]{83, 80, 82, 80};
   private static final byte[] DATA_FORMAT_VERSION = new byte[]{3, 2, 5, 2};

   public StringPrepDataReader(InputStream var1) throws IOException {
      this.unicodeVersion = ICUBinary.readHeader(var1, DATA_FORMAT_ID, this);
      this.dataInputStream = new DataInputStream(var1);
   }

   public void read(byte[] var1, char[] var2) throws IOException {
      this.dataInputStream.read(var1);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = this.dataInputStream.readChar();
      }

   }

   public byte[] getDataFormatVersion() {
      return DATA_FORMAT_VERSION;
   }

   public boolean isDataVersionAcceptable(byte[] var1) {
      return var1[0] == DATA_FORMAT_VERSION[0] && var1[2] == DATA_FORMAT_VERSION[2] && var1[3] == DATA_FORMAT_VERSION[3];
   }

   public int[] readIndexes(int var1) throws IOException {
      int[] var2 = new int[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = this.dataInputStream.readInt();
      }

      return var2;
   }

   public byte[] getUnicodeVersion() {
      return this.unicodeVersion;
   }
}
