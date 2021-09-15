package sun.security.krb5;

import java.io.IOException;
import java.net.UnknownHostException;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.TGSReq;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.crypto.Nonce;

public class KrbTgsReq {
   private PrincipalName princName;
   private PrincipalName servName;
   private TGSReq tgsReqMessg;
   private KerberosTime ctime;
   private Ticket secondTicket;
   private boolean useSubkey;
   EncryptionKey tgsReqKey;
   private static final boolean DEBUG;
   private byte[] obuf;
   private byte[] ibuf;

   public KrbTgsReq(Credentials var1, PrincipalName var2) throws KrbException, IOException {
      this(new KDCOptions(), var1, var2, (KerberosTime)null, (KerberosTime)null, (KerberosTime)null, (int[])null, (HostAddresses)null, (AuthorizationData)null, (Ticket[])null, (EncryptionKey)null);
   }

   public KrbTgsReq(Credentials var1, Ticket var2, PrincipalName var3) throws KrbException, IOException {
      this(KDCOptions.with(14, 1), var1, var3, (KerberosTime)null, (KerberosTime)null, (KerberosTime)null, (int[])null, (HostAddresses)null, (AuthorizationData)null, new Ticket[]{var2}, (EncryptionKey)null);
   }

   public KrbTgsReq(Credentials var1, PrincipalName var2, PAData var3) throws KrbException, IOException {
      this(KDCOptions.with(1), var1, var1.getClient(), var2, (KerberosTime)null, (KerberosTime)null, (KerberosTime)null, (int[])null, (HostAddresses)null, (AuthorizationData)null, (Ticket[])null, (EncryptionKey)null, var3);
   }

   KrbTgsReq(KDCOptions var1, Credentials var2, PrincipalName var3, KerberosTime var4, KerberosTime var5, KerberosTime var6, int[] var7, HostAddresses var8, AuthorizationData var9, Ticket[] var10, EncryptionKey var11) throws KrbException, IOException {
      this(var1, var2, var2.getClient(), var3, var4, var5, var6, var7, var8, var9, var10, var11, (PAData)null);
   }

   private KrbTgsReq(KDCOptions var1, Credentials var2, PrincipalName var3, PrincipalName var4, KerberosTime var5, KerberosTime var6, KerberosTime var7, int[] var8, HostAddresses var9, AuthorizationData var10, Ticket[] var11, EncryptionKey var12, PAData var13) throws KrbException, IOException {
      this.secondTicket = null;
      this.useSubkey = false;
      this.princName = var3;
      this.servName = var4;
      this.ctime = KerberosTime.now();
      if (var1.get(1) && !var2.flags.get(1)) {
         var1.set(1, false);
      }

      if (var1.get(2) && !var2.flags.get(1)) {
         throw new KrbException(101);
      } else if (var1.get(3) && !var2.flags.get(3)) {
         throw new KrbException(101);
      } else if (var1.get(4) && !var2.flags.get(3)) {
         throw new KrbException(101);
      } else if (var1.get(5) && !var2.flags.get(5)) {
         throw new KrbException(101);
      } else if (var1.get(8) && !var2.flags.get(8)) {
         throw new KrbException(101);
      } else {
         if (var1.get(6)) {
            if (!var2.flags.get(6)) {
               throw new KrbException(101);
            }
         } else if (var5 != null) {
            var5 = null;
         }

         if (var1.get(8)) {
            if (!var2.flags.get(8)) {
               throw new KrbException(101);
            }
         } else if (var7 != null) {
            var7 = null;
         }

         if (!var1.get(28) && !var1.get(14)) {
            if (var11 != null) {
               var11 = null;
            }
         } else {
            if (var11 == null) {
               throw new KrbException(101);
            }

            this.secondTicket = var11[0];
         }

         this.tgsReqMessg = this.createRequest(var1, var2.ticket, var2.key, this.ctime, this.princName, this.servName, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         this.obuf = this.tgsReqMessg.asn1Encode();
         if (var2.flags.get(2)) {
            var1.set(2, true);
         }

      }
   }

   public void send() throws IOException, KrbException {
      String var1 = null;
      if (this.servName != null) {
         var1 = this.servName.getRealmString();
      }

      KdcComm var2 = new KdcComm(var1);
      this.ibuf = var2.send(this.obuf);
   }

   public KrbTgsRep getReply() throws KrbException, IOException {
      return new KrbTgsRep(this.ibuf, this);
   }

   public Credentials sendAndGetCreds() throws IOException, KrbException {
      KrbTgsRep var1 = null;
      Object var2 = null;
      this.send();
      var1 = this.getReply();
      return var1.getCreds();
   }

   KerberosTime getCtime() {
      return this.ctime;
   }

   private TGSReq createRequest(KDCOptions var1, Ticket var2, EncryptionKey var3, KerberosTime var4, PrincipalName var5, PrincipalName var6, KerberosTime var7, KerberosTime var8, KerberosTime var9, int[] var10, HostAddresses var11, AuthorizationData var12, Ticket[] var13, EncryptionKey var14, PAData var15) throws IOException, KrbException, UnknownHostException {
      KerberosTime var16 = null;
      if (var8 == null) {
         var16 = new KerberosTime(0L);
      } else {
         var16 = var8;
      }

      this.tgsReqKey = var3;
      Object var17 = null;
      int[] var25;
      if (var10 == null) {
         var25 = EType.getDefaults("default_tgs_enctypes");
      } else {
         var25 = var10;
      }

      EncryptionKey var18 = null;
      EncryptedData var19 = null;
      if (var12 != null) {
         byte[] var20 = var12.asn1Encode();
         if (var14 != null) {
            var18 = var14;
            this.tgsReqKey = var14;
            this.useSubkey = true;
            var19 = new EncryptedData(var14, var20, 5);
         } else {
            var19 = new EncryptedData(var3, var20, 4);
         }
      }

      KDCReqBody var26 = new KDCReqBody(var1, var5, var6, var7, var16, var9, Nonce.value(), var25, var11, var19, var13);
      byte[] var21 = var26.asn1Encode(12);
      Checksum var22;
      switch(Checksum.CKSUMTYPE_DEFAULT) {
      case -138:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
      case 12:
      case 15:
      case 16:
         var22 = new Checksum(Checksum.CKSUMTYPE_DEFAULT, var21, var3, 6);
         break;
      case 1:
      case 2:
      case 7:
      default:
         var22 = new Checksum(Checksum.CKSUMTYPE_DEFAULT, var21);
      }

      byte[] var23 = (new KrbApReq(new APOptions(), var2, var3, var5, var22, var4, var18, (SeqNumber)null, (AuthorizationData)null)).getMessage();
      PAData var24 = new PAData(1, var23);
      return new TGSReq(var15 != null ? new PAData[]{var15, var24} : new PAData[]{var24}, var26);
   }

   TGSReq getMessage() {
      return this.tgsReqMessg;
   }

   Ticket getSecondTicket() {
      return this.secondTicket;
   }

   private static void debug(String var0) {
   }

   boolean usedSubkey() {
      return this.useSubkey;
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
