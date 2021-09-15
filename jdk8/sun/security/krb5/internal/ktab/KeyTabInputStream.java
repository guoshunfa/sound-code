package sun.security.krb5.internal.ktab;

import java.io.IOException;
import java.io.InputStream;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.util.KrbDataInputStream;

public class KeyTabInputStream extends KrbDataInputStream implements KeyTabConstants {
   boolean DEBUG;
   int index;

   public KeyTabInputStream(InputStream var1) {
      super(var1);
      this.DEBUG = Krb5.DEBUG;
   }

   int readEntryLength() throws IOException {
      return this.read(4);
   }

   KeyTabEntry readEntry(int var1, int var2) throws IOException, RealmException {
      this.index = var1;
      if (this.index == 0) {
         return null;
      } else if (this.index < 0) {
         this.skip((long)Math.abs(this.index));
         return null;
      } else {
         int var3 = this.read(2);
         this.index -= 2;
         if (var2 == 1281) {
            --var3;
         }

         Realm var4 = new Realm(this.readName());
         String[] var5 = new String[var3];

         int var6;
         for(var6 = 0; var6 < var3; ++var6) {
            var5[var6] = this.readName();
         }

         var6 = this.read(4);
         this.index -= 4;
         PrincipalName var7 = new PrincipalName(var6, var5, var4);
         KerberosTime var8 = this.readTimeStamp();
         int var9 = this.read() & 255;
         --this.index;
         int var10 = this.read(2);
         this.index -= 2;
         int var11 = this.read(2);
         this.index -= 2;
         byte[] var12 = this.readKey(var11);
         this.index -= var11;
         if (this.index >= 4) {
            int var13 = this.read(4);
            if (var13 != 0) {
               var9 = var13;
            }

            this.index -= 4;
         }

         if (this.index < 0) {
            throw new RealmException("Keytab is corrupted");
         } else {
            this.skip((long)this.index);
            return new KeyTabEntry(var7, var4, var8, var9, var10, var12);
         }
      }
   }

   byte[] readKey(int var1) throws IOException {
      byte[] var2 = new byte[var1];
      this.read(var2, 0, var1);
      return var2;
   }

   KerberosTime readTimeStamp() throws IOException {
      this.index -= 4;
      return new KerberosTime((long)this.read(4) * 1000L);
   }

   String readName() throws IOException {
      int var2 = this.read(2);
      this.index -= 2;
      byte[] var3 = new byte[var2];
      this.read(var3, 0, var2);
      this.index -= var2;
      String var1 = new String(var3);
      if (this.DEBUG) {
         System.out.println(">>> KeyTabInputStream, readName(): " + var1);
      }

      return var1;
   }
}
