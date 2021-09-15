package sun.security.krb5.internal.util;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetBooleanAction;
import sun.security.util.DerValue;

public final class KerberosString {
   public static final boolean MSNAME = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.security.krb5.msinterop.kstring")));
   private final String s;

   public KerberosString(String var1) {
      this.s = var1;
   }

   public KerberosString(DerValue var1) throws IOException {
      if (var1.tag != 27) {
         throw new IOException("KerberosString's tag is incorrect: " + var1.tag);
      } else {
         this.s = new String(var1.getDataBytes(), MSNAME ? "UTF8" : "ASCII");
      }
   }

   public String toString() {
      return this.s;
   }

   public DerValue toDerValue() throws IOException {
      return new DerValue((byte)27, this.s.getBytes(MSNAME ? "UTF8" : "ASCII"));
   }
}
