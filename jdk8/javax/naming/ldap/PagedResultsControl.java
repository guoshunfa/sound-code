package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class PagedResultsControl extends BasicControl {
   public static final String OID = "1.2.840.113556.1.4.319";
   private static final byte[] EMPTY_COOKIE = new byte[0];
   private static final long serialVersionUID = 6684806685736844298L;

   public PagedResultsControl(int var1, boolean var2) throws IOException {
      super("1.2.840.113556.1.4.319", var2, (byte[])null);
      this.value = this.setEncodedValue(var1, EMPTY_COOKIE);
   }

   public PagedResultsControl(int var1, byte[] var2, boolean var3) throws IOException {
      super("1.2.840.113556.1.4.319", var3, (byte[])null);
      if (var2 == null) {
         var2 = EMPTY_COOKIE;
      }

      this.value = this.setEncodedValue(var1, var2);
   }

   private byte[] setEncodedValue(int var1, byte[] var2) throws IOException {
      BerEncoder var3 = new BerEncoder(10 + var2.length);
      var3.beginSeq(48);
      var3.encodeInt(var1);
      var3.encodeOctetString(var2, 4);
      var3.endSeq();
      return var3.getTrimmedBuf();
   }
}
