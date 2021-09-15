package sun.security.krb5.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAForUserEnc {
   public final PrincipalName name;
   private final EncryptionKey key;
   public static final String AUTH_PACKAGE = "Kerberos";

   public PAForUserEnc(PrincipalName var1, EncryptionKey var2) {
      this.name = var1;
      this.key = var2;
   }

   public PAForUserEnc(DerValue var1, EncryptionKey var2) throws Asn1Exception, KrbException, IOException {
      DerValue var3 = null;
      this.key = var2;
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         PrincipalName var4 = null;
         var3 = var1.getData().getDerValue();
         if ((var3.getTag() & 31) == 0) {
            try {
               var4 = new PrincipalName(var3.getData().getDerValue(), new Realm("PLACEHOLDER"));
            } catch (RealmException var7) {
            }

            var3 = var1.getData().getDerValue();
            if ((var3.getTag() & 31) == 1) {
               try {
                  Realm var5 = new Realm(var3.getData().getDerValue());
                  this.name = new PrincipalName(var4.getNameType(), var4.getNameStrings(), var5);
               } catch (RealmException var6) {
                  throw new IOException(var6);
               }

               var3 = var1.getData().getDerValue();
               if ((var3.getTag() & 31) == 2) {
                  var3 = var1.getData().getDerValue();
                  if ((var3.getTag() & 31) == 3) {
                     String var8 = (new KerberosString(var3.getData().getDerValue())).toString();
                     if (!var8.equalsIgnoreCase("Kerberos")) {
                        throw new IOException("Incorrect auth-package");
                     } else if (var1.getData().available() > 0) {
                        throw new Asn1Exception(906);
                     }
                  } else {
                     throw new Asn1Exception(906);
                  }
               } else {
                  throw new Asn1Exception(906);
               }
            } else {
               throw new Asn1Exception(906);
            }
         } else {
            throw new Asn1Exception(906);
         }
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), this.name.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), this.name.getRealm().asn1Encode());

      try {
         Checksum var2 = new Checksum(-138, this.getS4UByteArray(), this.key, 17);
         var1.write(DerValue.createTag((byte)-128, true, (byte)2), var2.asn1Encode());
      } catch (KrbException var3) {
         throw new IOException(var3);
      }

      DerOutputStream var4 = new DerOutputStream();
      var4.putDerValue((new KerberosString("Kerberos")).toDerValue());
      var1.write(DerValue.createTag((byte)-128, true, (byte)3), var4);
      var4 = new DerOutputStream();
      var4.write((byte)48, (DerOutputStream)var1);
      return var4.toByteArray();
   }

   public byte[] getS4UByteArray() {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         var1.write(new byte[4]);
         String[] var2 = this.name.getNameStrings();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            var1.write(var5.getBytes("UTF-8"));
         }

         var1.write(this.name.getRealm().toString().getBytes("UTF-8"));
         var1.write("Kerberos".getBytes("UTF-8"));
         byte[] var7 = var1.toByteArray();
         var3 = this.name.getNameType();
         var7[0] = (byte)(var3 & 255);
         var7[1] = (byte)(var3 >> 8 & 255);
         var7[2] = (byte)(var3 >> 16 & 255);
         var7[3] = (byte)(var3 >> 24 & 255);
         return var7;
      } catch (IOException var6) {
         throw new AssertionError("Cannot write ByteArrayOutputStream", var6);
      }
   }

   public String toString() {
      return "PA-FOR-USER: " + this.name;
   }
}
