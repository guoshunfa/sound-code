package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class SortControl extends BasicControl {
   public static final String OID = "1.2.840.113556.1.4.473";
   private static final long serialVersionUID = -1965961680233330744L;

   public SortControl(String var1, boolean var2) throws IOException {
      super("1.2.840.113556.1.4.473", var2, (byte[])null);
      super.value = this.setEncodedValue(new SortKey[]{new SortKey(var1)});
   }

   public SortControl(String[] var1, boolean var2) throws IOException {
      super("1.2.840.113556.1.4.473", var2, (byte[])null);
      SortKey[] var3 = new SortKey[var1.length];

      for(int var4 = 0; var4 < var1.length; ++var4) {
         var3[var4] = new SortKey(var1[var4]);
      }

      super.value = this.setEncodedValue(var3);
   }

   public SortControl(SortKey[] var1, boolean var2) throws IOException {
      super("1.2.840.113556.1.4.473", var2, (byte[])null);
      super.value = this.setEncodedValue(var1);
   }

   private byte[] setEncodedValue(SortKey[] var1) throws IOException {
      BerEncoder var2 = new BerEncoder(30 * var1.length + 10);
      var2.beginSeq(48);

      for(int var4 = 0; var4 < var1.length; ++var4) {
         var2.beginSeq(48);
         var2.encodeString(var1[var4].getAttributeID(), true);
         String var3;
         if ((var3 = var1[var4].getMatchingRuleID()) != null) {
            var2.encodeString(var3, 128, true);
         }

         if (!var1[var4].isAscending()) {
            var2.encodeBoolean(true, 129);
         }

         var2.endSeq();
      }

      var2.endSeq();
      return var2.getTrimmedBuf();
   }
}
