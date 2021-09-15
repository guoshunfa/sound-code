package sun.security.krb5.internal.ktab;

import java.io.UnsupportedEncodingException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;

public class KeyTabEntry implements KeyTabConstants {
   PrincipalName service;
   Realm realm;
   KerberosTime timestamp;
   int keyVersion;
   int keyType;
   byte[] keyblock = null;
   boolean DEBUG;

   public KeyTabEntry(PrincipalName var1, Realm var2, KerberosTime var3, int var4, int var5, byte[] var6) {
      this.DEBUG = Krb5.DEBUG;
      this.service = var1;
      this.realm = var2;
      this.timestamp = var3;
      this.keyVersion = var4;
      this.keyType = var5;
      if (var6 != null) {
         this.keyblock = (byte[])var6.clone();
      }

   }

   public PrincipalName getService() {
      return this.service;
   }

   public EncryptionKey getKey() {
      EncryptionKey var1 = new EncryptionKey(this.keyblock, this.keyType, new Integer(this.keyVersion));
      return var1;
   }

   public String getKeyString() {
      StringBuffer var1 = new StringBuffer("0x");

      for(int var2 = 0; var2 < this.keyblock.length; ++var2) {
         var1.append(String.format("%02x", this.keyblock[var2] & 255));
      }

      return var1.toString();
   }

   public int entryLength() {
      int var1 = 0;
      String[] var2 = this.service.getNameStrings();

      int var3;
      for(var3 = 0; var3 < var2.length; ++var3) {
         try {
            var1 += 2 + var2[var3].getBytes("8859_1").length;
         } catch (UnsupportedEncodingException var6) {
         }
      }

      var3 = 0;

      try {
         var3 = this.realm.toString().getBytes("8859_1").length;
      } catch (UnsupportedEncodingException var5) {
      }

      int var4 = 4 + var3 + var1 + 4 + 4 + 1 + 2 + 2 + this.keyblock.length;
      if (this.DEBUG) {
         System.out.println(">>> KeyTabEntry: key tab entry size is " + var4);
      }

      return var4;
   }

   public KerberosTime getTimeStamp() {
      return this.timestamp;
   }
}
