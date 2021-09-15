package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KRBSafe;
import sun.security.krb5.internal.KRBSafeBody;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;

class KrbSafe extends KrbAppMessage {
   private byte[] obuf;
   private byte[] userData;

   public KrbSafe(byte[] var1, Credentials var2, EncryptionKey var3, KerberosTime var4, SeqNumber var5, HostAddress var6, HostAddress var7) throws KrbException, IOException {
      EncryptionKey var8 = null;
      if (var3 != null) {
         var8 = var3;
      } else {
         var8 = var2.key;
      }

      this.obuf = this.mk_safe(var1, var8, var4, var5, var6, var7);
   }

   public KrbSafe(byte[] var1, Credentials var2, EncryptionKey var3, SeqNumber var4, HostAddress var5, HostAddress var6, boolean var7, boolean var8) throws KrbException, IOException {
      KRBSafe var9 = new KRBSafe(var1);
      EncryptionKey var10 = null;
      if (var3 != null) {
         var10 = var3;
      } else {
         var10 = var2.key;
      }

      this.userData = this.rd_safe(var9, var10, var4, var5, var6, var7, var8, var2.client);
   }

   public byte[] getMessage() {
      return this.obuf;
   }

   public byte[] getData() {
      return this.userData;
   }

   private byte[] mk_safe(byte[] var1, EncryptionKey var2, KerberosTime var3, SeqNumber var4, HostAddress var5, HostAddress var6) throws Asn1Exception, IOException, KdcErrException, KrbApErrException, KrbCryptoException {
      Integer var7 = null;
      Integer var8 = null;
      if (var3 != null) {
         var7 = new Integer(var3.getMicroSeconds());
      }

      if (var4 != null) {
         var8 = new Integer(var4.current());
         var4.step();
      }

      KRBSafeBody var9 = new KRBSafeBody(var1, var3, var7, var8, var5, var6);
      byte[] var10 = var9.asn1Encode();
      Checksum var11 = new Checksum(Checksum.SAFECKSUMTYPE_DEFAULT, var10, var2, 15);
      KRBSafe var12 = new KRBSafe(var9, var11);
      var10 = var12.asn1Encode();
      return var12.asn1Encode();
   }

   private byte[] rd_safe(KRBSafe var1, EncryptionKey var2, SeqNumber var3, HostAddress var4, HostAddress var5, boolean var6, boolean var7, PrincipalName var8) throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException {
      byte[] var9 = var1.safeBody.asn1Encode();
      if (!var1.cksum.verifyKeyedChecksum(var9, var2, 15)) {
         throw new KrbApErrException(41);
      } else {
         this.check(var1.safeBody.timestamp, var1.safeBody.usec, var1.safeBody.seqNumber, var1.safeBody.sAddress, var1.safeBody.rAddress, var3, var4, var5, var6, var7, var8);
         return var1.safeBody.userData;
      }
   }
}
