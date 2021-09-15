package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.EncKrbPrivPart;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KRBPriv;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;
import sun.security.util.DerValue;

class KrbPriv extends KrbAppMessage {
   private byte[] obuf;
   private byte[] userData;

   private KrbPriv(byte[] var1, Credentials var2, EncryptionKey var3, KerberosTime var4, SeqNumber var5, HostAddress var6, HostAddress var7) throws KrbException, IOException {
      EncryptionKey var8 = null;
      if (var3 != null) {
         var8 = var3;
      } else {
         var8 = var2.key;
      }

      this.obuf = this.mk_priv(var1, var8, var4, var5, var6, var7);
   }

   private KrbPriv(byte[] var1, Credentials var2, EncryptionKey var3, SeqNumber var4, HostAddress var5, HostAddress var6, boolean var7, boolean var8) throws KrbException, IOException {
      KRBPriv var9 = new KRBPriv(var1);
      EncryptionKey var10 = null;
      if (var3 != null) {
         var10 = var3;
      } else {
         var10 = var2.key;
      }

      this.userData = this.rd_priv(var9, var10, var4, var5, var6, var7, var8, var2.client);
   }

   public byte[] getMessage() throws KrbException {
      return this.obuf;
   }

   public byte[] getData() {
      return this.userData;
   }

   private byte[] mk_priv(byte[] var1, EncryptionKey var2, KerberosTime var3, SeqNumber var4, HostAddress var5, HostAddress var6) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
      Integer var7 = null;
      Integer var8 = null;
      if (var3 != null) {
         var7 = new Integer(var3.getMicroSeconds());
      }

      if (var4 != null) {
         var8 = new Integer(var4.current());
         var4.step();
      }

      EncKrbPrivPart var9 = new EncKrbPrivPart(var1, var3, var7, var8, var5, var6);
      byte[] var10 = var9.asn1Encode();
      EncryptedData var11 = new EncryptedData(var2, var10, 13);
      KRBPriv var12 = new KRBPriv(var11);
      var10 = var12.asn1Encode();
      return var12.asn1Encode();
   }

   private byte[] rd_priv(KRBPriv var1, EncryptionKey var2, SeqNumber var3, HostAddress var4, HostAddress var5, boolean var6, boolean var7, PrincipalName var8) throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException {
      byte[] var9 = var1.encPart.decrypt(var2, 13);
      byte[] var10 = var1.encPart.reset(var9);
      DerValue var11 = new DerValue(var10);
      EncKrbPrivPart var12 = new EncKrbPrivPart(var11);
      this.check(var12.timestamp, var12.usec, var12.seqNumber, var12.sAddress, var12.rAddress, var3, var4, var5, var6, var7, var8);
      return var12.userData;
   }
}
