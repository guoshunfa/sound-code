package sun.security.krb5.internal.ktab;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class KeyTabOutputStream extends KrbDataOutputStream implements KeyTabConstants {
   private KeyTabEntry entry;
   private int keyType;
   private byte[] keyValue;
   public int version;

   public KeyTabOutputStream(OutputStream var1) {
      super(var1);
   }

   public void writeVersion(int var1) throws IOException {
      this.version = var1;
      this.write16(var1);
   }

   public void writeEntry(KeyTabEntry var1) throws IOException {
      this.write32(var1.entryLength());
      String[] var2 = var1.service.getNameStrings();
      int var3 = var2.length;
      if (this.version == 1281) {
         this.write16(var3 + 1);
      } else {
         this.write16(var3);
      }

      byte[] var4 = null;

      try {
         var4 = var1.service.getRealmString().getBytes("8859_1");
      } catch (UnsupportedEncodingException var8) {
      }

      this.write16(var4.length);
      this.write(var4);

      for(int var5 = 0; var5 < var3; ++var5) {
         try {
            this.write16(var2[var5].getBytes("8859_1").length);
            this.write(var2[var5].getBytes("8859_1"));
         } catch (UnsupportedEncodingException var7) {
         }
      }

      this.write32(var1.service.getNameType());
      this.write32((int)(var1.timestamp.getTime() / 1000L));
      this.write8(var1.keyVersion % 256);
      this.write16(var1.keyType);
      this.write16(var1.keyblock.length);
      this.write(var1.keyblock);
   }
}
