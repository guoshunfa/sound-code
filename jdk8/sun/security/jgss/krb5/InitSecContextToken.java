package sun.security.jgss.krb5;

import com.sun.security.jgss.AuthorizationDataEntry;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import org.ietf.jgss.GSSException;
import sun.security.krb5.Checksum;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.KerberosTime;
import sun.security.util.DerValue;

class InitSecContextToken extends InitialToken {
   private KrbApReq apReq = null;

   InitSecContextToken(Krb5Context var1, Credentials var2, Credentials var3) throws KrbException, IOException, GSSException {
      boolean var4 = var1.getMutualAuthState();
      boolean var5 = true;
      boolean var6 = true;
      InitialToken.OverloadedChecksum var7 = new InitialToken.OverloadedChecksum(var1, var2, var3);
      Checksum var8 = var7.getChecksum();
      var1.setTktFlags(var3.getFlags());
      var1.setAuthTime((new KerberosTime(var3.getAuthTime())).toString());
      this.apReq = new KrbApReq(var3, var4, var5, var6, var8);
      var1.resetMySequenceNumber(this.apReq.getSeqNumber());
      EncryptionKey var9 = this.apReq.getSubKey();
      if (var9 != null) {
         var1.setKey(1, var9);
      } else {
         var1.setKey(0, var3.getSessionKey());
      }

      if (!var4) {
         var1.resetPeerSequenceNumber(0);
      }

   }

   InitSecContextToken(Krb5Context var1, Krb5AcceptCredential var2, InputStream var3) throws IOException, GSSException, KrbException {
      int var4 = var3.read() << 8 | var3.read();
      if (var4 != 256) {
         throw new GSSException(10, -1, "AP_REQ token id does not match!");
      } else {
         byte[] var5 = (new DerValue(var3)).toByteArray();
         InetAddress var6 = null;
         if (var1.getChannelBinding() != null) {
            var6 = var1.getChannelBinding().getInitiatorAddress();
         }

         this.apReq = new KrbApReq(var5, var2, var6);
         EncryptionKey var7 = this.apReq.getCreds().getSessionKey();
         EncryptionKey var8 = this.apReq.getSubKey();
         if (var8 != null) {
            var1.setKey(1, var8);
         } else {
            var1.setKey(0, var7);
         }

         InitialToken.OverloadedChecksum var9 = new InitialToken.OverloadedChecksum(var1, this.apReq.getChecksum(), var7, var8);
         var9.setContextFlags(var1);
         Credentials var10 = var9.getDelegatedCreds();
         if (var10 != null) {
            Krb5InitCredential var11 = Krb5InitCredential.getInstance((Krb5NameElement)var1.getSrcName(), var10);
            var1.setDelegCred(var11);
         }

         Integer var16 = this.apReq.getSeqNumber();
         int var12 = var16 != null ? var16 : 0;
         var1.resetPeerSequenceNumber(var12);
         if (!var1.getMutualAuthState()) {
            var1.resetMySequenceNumber(var12);
         }

         var1.setAuthTime((new KerberosTime(this.apReq.getCreds().getAuthTime())).toString());
         var1.setTktFlags(this.apReq.getCreds().getFlags());
         AuthorizationData var13 = this.apReq.getCreds().getAuthzData();
         if (var13 == null) {
            var1.setAuthzData((AuthorizationDataEntry[])null);
         } else {
            AuthorizationDataEntry[] var14 = new AuthorizationDataEntry[var13.count()];

            for(int var15 = 0; var15 < var13.count(); ++var15) {
               var14[var15] = new AuthorizationDataEntry(var13.item(var15).adType, var13.item(var15).adData);
            }

            var1.setAuthzData(var14);
         }

      }
   }

   public final KrbApReq getKrbApReq() {
      return this.apReq;
   }

   public final byte[] encode() throws IOException {
      byte[] var1 = this.apReq.getMessage();
      byte[] var2 = new byte[2 + var1.length];
      writeInt(256, var2, 0);
      System.arraycopy(var1, 0, var2, 2, var1.length);
      return var2;
   }
}
