package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.APRep;
import sun.security.krb5.internal.EncAPRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.SeqNumber;
import sun.security.util.DerValue;

public class KrbApRep {
   private byte[] obuf;
   private byte[] ibuf;
   private EncAPRepPart encPart;
   private APRep apRepMessg;

   public KrbApRep(KrbApReq var1, boolean var2, EncryptionKey var3) throws KrbException, IOException {
      LocalSeqNumber var4 = new LocalSeqNumber();
      this.init(var1, var3, var4);
   }

   public KrbApRep(byte[] var1, Credentials var2, KrbApReq var3) throws KrbException, IOException {
      this(var1, var2);
      this.authenticate(var3);
   }

   private void init(KrbApReq var1, EncryptionKey var2, SeqNumber var3) throws KrbException, IOException {
      this.createMessage(var1.getCreds().key, var1.getCtime(), var1.cusec(), var2, var3);
      this.obuf = this.apRepMessg.asn1Encode();
   }

   private KrbApRep(byte[] var1, Credentials var2) throws KrbException, IOException {
      this(new DerValue(var1), var2);
   }

   private KrbApRep(DerValue var1, Credentials var2) throws KrbException, IOException {
      APRep var3 = null;

      try {
         var3 = new APRep(var1);
      } catch (Asn1Exception var9) {
         var3 = null;
         KRBError var5 = new KRBError(var1);
         String var6 = var5.getErrorString();
         String var7;
         if (var6.charAt(var6.length() - 1) == 0) {
            var7 = var6.substring(0, var6.length() - 1);
         } else {
            var7 = var6;
         }

         KrbException var8 = new KrbException(var5.getErrorCode(), var7);
         var8.initCause(var9);
         throw var8;
      }

      byte[] var4 = var3.encPart.decrypt(var2.key, 12);
      byte[] var10 = var3.encPart.reset(var4);
      var1 = new DerValue(var10);
      this.encPart = new EncAPRepPart(var1);
   }

   private void authenticate(KrbApReq var1) throws KrbException, IOException {
      if (this.encPart.ctime.getSeconds() != var1.getCtime().getSeconds() || this.encPart.cusec != var1.getCtime().getMicroSeconds()) {
         throw new KrbApErrException(46);
      }
   }

   public EncryptionKey getSubKey() {
      return this.encPart.getSubKey();
   }

   public Integer getSeqNumber() {
      return this.encPart.getSeqNumber();
   }

   public byte[] getMessage() {
      return this.obuf;
   }

   private void createMessage(EncryptionKey var1, KerberosTime var2, int var3, EncryptionKey var4, SeqNumber var5) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
      Integer var6 = null;
      if (var5 != null) {
         var6 = new Integer(var5.current());
      }

      this.encPart = new EncAPRepPart(var2, var3, var4, var6);
      byte[] var7 = this.encPart.asn1Encode();
      EncryptedData var8 = new EncryptedData(var1, var7, 12);
      this.apRepMessg = new APRep(var8);
   }
}
