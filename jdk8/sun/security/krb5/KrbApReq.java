package sun.security.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.APReq;
import sun.security.krb5.internal.Authenticator;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.EncTicketPart;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.ReplayCache;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import sun.security.util.DerValue;

public class KrbApReq {
   private byte[] obuf;
   private KerberosTime ctime;
   private int cusec;
   private Authenticator authenticator;
   private Credentials creds;
   private APReq apReqMessg;
   private static ReplayCache rcache = ReplayCache.getInstance();
   private static boolean DEBUG;
   private static final char[] hexConst;

   public KrbApReq(Credentials var1, boolean var2, boolean var3, boolean var4, Checksum var5) throws Asn1Exception, KrbCryptoException, KrbException, IOException {
      APOptions var6 = var2 ? new APOptions(2) : new APOptions();
      if (DEBUG) {
         System.out.println(">>> KrbApReq: APOptions are " + var6);
      }

      EncryptionKey var7 = var3 ? new EncryptionKey(var1.getSessionKey()) : null;
      LocalSeqNumber var8 = new LocalSeqNumber();
      this.init(var6, var1, var5, var7, var8, (AuthorizationData)null, 11);
   }

   public KrbApReq(byte[] var1, Krb5AcceptCredential var2, InetAddress var3) throws KrbException, IOException {
      this.obuf = var1;
      if (this.apReqMessg == null) {
         this.decode();
      }

      this.authenticate(var2, var3);
   }

   KrbApReq(APOptions var1, Ticket var2, EncryptionKey var3, PrincipalName var4, Checksum var5, KerberosTime var6, EncryptionKey var7, SeqNumber var8, AuthorizationData var9) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
      this.init(var1, var2, var3, var4, var5, var6, var7, var8, var9, 7);
   }

   private void init(APOptions var1, Credentials var2, Checksum var3, EncryptionKey var4, SeqNumber var5, AuthorizationData var6, int var7) throws KrbException, IOException {
      this.ctime = KerberosTime.now();
      this.init(var1, var2.ticket, var2.key, var2.client, var3, this.ctime, var4, var5, var6, var7);
   }

   private void init(APOptions var1, Ticket var2, EncryptionKey var3, PrincipalName var4, Checksum var5, KerberosTime var6, EncryptionKey var7, SeqNumber var8, AuthorizationData var9, int var10) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
      this.createMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      this.obuf = this.apReqMessg.asn1Encode();
   }

   void decode() throws KrbException, IOException {
      DerValue var1 = new DerValue(this.obuf);
      this.decode(var1);
   }

   void decode(DerValue var1) throws KrbException, IOException {
      this.apReqMessg = null;

      try {
         this.apReqMessg = new APReq(var1);
      } catch (Asn1Exception var7) {
         this.apReqMessg = null;
         KRBError var3 = new KRBError(var1);
         String var4 = var3.getErrorString();
         String var5;
         if (var4.charAt(var4.length() - 1) == 0) {
            var5 = var4.substring(0, var4.length() - 1);
         } else {
            var5 = var4;
         }

         KrbException var6 = new KrbException(var3.getErrorCode(), var5);
         var6.initCause(var7);
         throw var6;
      }
   }

   private void authenticate(Krb5AcceptCredential var1, InetAddress var2) throws KrbException, IOException {
      int var3 = this.apReqMessg.ticket.encPart.getEType();
      Integer var4 = this.apReqMessg.ticket.encPart.getKeyVersionNumber();
      EncryptionKey[] var5 = var1.getKrb5EncryptionKeys(this.apReqMessg.ticket.sname);
      EncryptionKey var6 = EncryptionKey.findKey(var3, var4, var5);
      if (var6 == null) {
         throw new KrbException(400, "Cannot find key of appropriate type to decrypt AP REP - " + EType.toString(var3));
      } else {
         byte[] var7 = this.apReqMessg.ticket.encPart.decrypt(var6, 2);
         byte[] var8 = this.apReqMessg.ticket.encPart.reset(var7);
         EncTicketPart var9 = new EncTicketPart(var8);
         checkPermittedEType(var9.key.getEType());
         byte[] var10 = this.apReqMessg.authenticator.decrypt(var9.key, 11);
         byte[] var11 = this.apReqMessg.authenticator.reset(var10);
         this.authenticator = new Authenticator(var11);
         this.ctime = this.authenticator.ctime;
         this.cusec = this.authenticator.cusec;
         this.authenticator.ctime = this.authenticator.ctime.withMicroSeconds(this.authenticator.cusec);
         if (!this.authenticator.cname.equals(var9.cname)) {
            throw new KrbApErrException(36);
         } else if (!this.authenticator.ctime.inClockSkew()) {
            throw new KrbApErrException(37);
         } else {
            byte[] var12;
            try {
               var12 = MessageDigest.getInstance("MD5").digest(this.apReqMessg.authenticator.cipher);
            } catch (NoSuchAlgorithmException var16) {
               throw new AssertionError("Impossible");
            }

            char[] var13 = new char[var12.length * 2];

            for(int var14 = 0; var14 < var12.length; ++var14) {
               var13[2 * var14] = hexConst[(var12[var14] & 255) >> 4];
               var13[2 * var14 + 1] = hexConst[var12[var14] & 15];
            }

            AuthTimeWithHash var17 = new AuthTimeWithHash(this.authenticator.cname.toString(), this.apReqMessg.ticket.sname.toString(), this.authenticator.ctime.getSeconds(), this.authenticator.cusec, new String(var13));
            rcache.checkAndStore(KerberosTime.now(), var17);
            if (var2 != null) {
               HostAddress var15 = new HostAddress(var2);
               if (var9.caddr != null && !var9.caddr.inList(var15)) {
                  if (DEBUG) {
                     System.out.println(">>> KrbApReq: initiator is " + var15.getInetAddress() + ", but caddr is " + Arrays.toString((Object[])var9.caddr.getInetAddresses()));
                  }

                  throw new KrbApErrException(38);
               }
            }

            KerberosTime var18 = KerberosTime.now();
            if ((var9.starttime == null || !var9.starttime.greaterThanWRTClockSkew(var18)) && !var9.flags.get(7)) {
               if (var9.endtime != null && var18.greaterThanWRTClockSkew(var9.endtime)) {
                  throw new KrbApErrException(32);
               } else {
                  this.creds = new Credentials(this.apReqMessg.ticket, this.authenticator.cname, this.apReqMessg.ticket.sname, var9.key, var9.flags, var9.authtime, var9.starttime, var9.endtime, var9.renewTill, var9.caddr, var9.authorizationData);
                  if (DEBUG) {
                     System.out.println(">>> KrbApReq: authenticate succeed.");
                  }

               }
            } else {
               throw new KrbApErrException(33);
            }
         }
      }
   }

   public Credentials getCreds() {
      return this.creds;
   }

   KerberosTime getCtime() {
      return this.ctime != null ? this.ctime : this.authenticator.ctime;
   }

   int cusec() {
      return this.cusec;
   }

   APOptions getAPOptions() throws KrbException, IOException {
      if (this.apReqMessg == null) {
         this.decode();
      }

      return this.apReqMessg != null ? this.apReqMessg.apOptions : null;
   }

   public boolean getMutualAuthRequired() throws KrbException, IOException {
      if (this.apReqMessg == null) {
         this.decode();
      }

      return this.apReqMessg != null ? this.apReqMessg.apOptions.get(2) : false;
   }

   boolean useSessionKey() throws KrbException, IOException {
      if (this.apReqMessg == null) {
         this.decode();
      }

      return this.apReqMessg != null ? this.apReqMessg.apOptions.get(1) : false;
   }

   public EncryptionKey getSubKey() {
      return this.authenticator.getSubKey();
   }

   public Integer getSeqNumber() {
      return this.authenticator.getSeqNumber();
   }

   public Checksum getChecksum() {
      return this.authenticator.getChecksum();
   }

   public byte[] getMessage() {
      return this.obuf;
   }

   public PrincipalName getClient() {
      return this.creds.getClient();
   }

   private void createMessage(APOptions var1, Ticket var2, EncryptionKey var3, PrincipalName var4, Checksum var5, KerberosTime var6, EncryptionKey var7, SeqNumber var8, AuthorizationData var9, int var10) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
      Integer var11 = null;
      if (var8 != null) {
         var11 = new Integer(var8.current());
      }

      this.authenticator = new Authenticator(var4, var5, var6.getMicroSeconds(), var6, var7, var11, var9);
      byte[] var12 = this.authenticator.asn1Encode();
      EncryptedData var13 = new EncryptedData(var3, var12, var10);
      this.apReqMessg = new APReq(var1, var2, var13);
   }

   private static void checkPermittedEType(int var0) throws KrbException {
      int[] var1 = EType.getDefaults("permitted_enctypes");
      if (!EType.isSupported(var0, var1)) {
         throw new KrbException(EType.toString(var0) + " encryption type not in permitted_enctypes list");
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
      hexConst = "0123456789ABCDEF".toCharArray();
   }
}
