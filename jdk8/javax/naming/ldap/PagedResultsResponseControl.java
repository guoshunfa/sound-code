package javax.naming.ldap;

import com.sun.jndi.ldap.BerDecoder;
import java.io.IOException;

public final class PagedResultsResponseControl extends BasicControl {
   public static final String OID = "1.2.840.113556.1.4.319";
   private static final long serialVersionUID = -8819778744844514666L;
   private int resultSize;
   private byte[] cookie;

   public PagedResultsResponseControl(String var1, boolean var2, byte[] var3) throws IOException {
      super(var1, var2, var3);
      BerDecoder var4 = new BerDecoder(var3, 0, var3.length);
      var4.parseSeq((int[])null);
      this.resultSize = var4.parseInt();
      this.cookie = var4.parseOctetString(4, (int[])null);
   }

   public int getResultSize() {
      return this.resultSize;
   }

   public byte[] getCookie() {
      return this.cookie.length == 0 ? null : this.cookie;
   }
}
