package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorizationData implements Cloneable {
   private AuthorizationDataEntry[] entry = null;

   private AuthorizationData() {
   }

   public AuthorizationData(AuthorizationDataEntry[] var1) throws IOException {
      if (var1 != null) {
         this.entry = new AuthorizationDataEntry[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] == null) {
               throw new IOException("Cannot create an AuthorizationData");
            }

            this.entry[var2] = (AuthorizationDataEntry)var1[var2].clone();
         }
      }

   }

   public AuthorizationData(AuthorizationDataEntry var1) {
      this.entry = new AuthorizationDataEntry[1];
      this.entry[0] = var1;
   }

   public Object clone() {
      AuthorizationData var1 = new AuthorizationData();
      if (this.entry != null) {
         var1.entry = new AuthorizationDataEntry[this.entry.length];

         for(int var2 = 0; var2 < this.entry.length; ++var2) {
            var1.entry[var2] = (AuthorizationDataEntry)this.entry[var2].clone();
         }
      }

      return var1;
   }

   public AuthorizationData(DerValue var1) throws Asn1Exception, IOException {
      Vector var2 = new Vector();
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         while(var1.getData().available() > 0) {
            var2.addElement(new AuthorizationDataEntry(var1.getData().getDerValue()));
         }

         if (var2.size() > 0) {
            this.entry = new AuthorizationDataEntry[var2.size()];
            var2.copyInto(this.entry);
         }

      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerValue[] var2 = new DerValue[this.entry.length];

      for(int var3 = 0; var3 < this.entry.length; ++var3) {
         var2[var3] = new DerValue(this.entry[var3].asn1Encode());
      }

      var1.putSequence(var2);
      return var1.toByteArray();
   }

   public static AuthorizationData parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new AuthorizationData(var4);
         }
      }
   }

   public void writeAuth(CCacheOutputStream var1) throws IOException {
      for(int var2 = 0; var2 < this.entry.length; ++var2) {
         this.entry[var2].writeEntry(var1);
      }

   }

   public String toString() {
      String var1 = "AuthorizationData:\n";

      for(int var2 = 0; var2 < this.entry.length; ++var2) {
         var1 = var1 + this.entry[var2].toString();
      }

      return var1;
   }

   public int count() {
      return this.entry.length;
   }

   public AuthorizationDataEntry item(int var1) {
      return (AuthorizationDataEntry)this.entry[var1].clone();
   }
}
