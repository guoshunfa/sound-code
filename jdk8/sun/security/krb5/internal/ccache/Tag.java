package sun.security.krb5.internal.ccache;

import java.io.ByteArrayOutputStream;

public class Tag {
   int length;
   int tag;
   int tagLen;
   Integer time_offset;
   Integer usec_offset;

   public Tag(int var1, int var2, Integer var3, Integer var4) {
      this.tag = var2;
      this.tagLen = 8;
      this.time_offset = var3;
      this.usec_offset = var4;
      this.length = 4 + this.tagLen;
   }

   public Tag(int var1) {
      this.tag = var1;
      this.tagLen = 0;
      this.length = 4 + this.tagLen;
   }

   public byte[] toByteArray() {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      var1.write(this.length);
      var1.write(this.tag);
      var1.write(this.tagLen);
      if (this.time_offset != null) {
         var1.write(this.time_offset);
      }

      if (this.usec_offset != null) {
         var1.write(this.usec_offset);
      }

      return var1.toByteArray();
   }
}
