package javax.naming.ldap;

import com.sun.jndi.ldap.BerDecoder;
import com.sun.jndi.ldap.LdapCtx;
import java.io.IOException;
import javax.naming.NamingException;

public final class SortResponseControl extends BasicControl {
   public static final String OID = "1.2.840.113556.1.4.474";
   private static final long serialVersionUID = 5142939176006310877L;
   private int resultCode = 0;
   private String badAttrId = null;

   public SortResponseControl(String var1, boolean var2, byte[] var3) throws IOException {
      super(var1, var2, var3);
      BerDecoder var4 = new BerDecoder(var3, 0, var3.length);
      var4.parseSeq((int[])null);
      this.resultCode = var4.parseEnumeration();
      if (var4.bytesLeft() > 0 && var4.peekByte() == 128) {
         this.badAttrId = var4.parseStringWithTag(128, true, (int[])null);
      }

   }

   public boolean isSorted() {
      return this.resultCode == 0;
   }

   public int getResultCode() {
      return this.resultCode;
   }

   public String getAttributeID() {
      return this.badAttrId;
   }

   public NamingException getException() {
      return LdapCtx.mapErrorCode(this.resultCode, (String)null);
   }
}
