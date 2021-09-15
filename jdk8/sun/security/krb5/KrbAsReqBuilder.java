package sun.security.krb5;

import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.kerberos.KeyTab;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.crypto.EType;

public final class KrbAsReqBuilder {
   private KDCOptions options;
   private PrincipalName cname;
   private PrincipalName sname;
   private KerberosTime from;
   private KerberosTime till;
   private KerberosTime rtime;
   private HostAddresses addresses;
   private final char[] password;
   private final KeyTab ktab;
   private PAData[] paList;
   private KrbAsReq req;
   private KrbAsRep rep;
   private KrbAsReqBuilder.State state;

   private void init(PrincipalName var1) throws KrbException {
      this.cname = var1;
      this.state = KrbAsReqBuilder.State.INIT;
   }

   public KrbAsReqBuilder(PrincipalName var1, KeyTab var2) throws KrbException {
      this.init(var1);
      this.ktab = var2;
      this.password = null;
   }

   public KrbAsReqBuilder(PrincipalName var1, char[] var2) throws KrbException {
      this.init(var1);
      this.password = (char[])var2.clone();
      this.ktab = null;
   }

   public EncryptionKey[] getKeys(boolean var1) throws KrbException {
      this.checkState(var1 ? KrbAsReqBuilder.State.REQ_OK : KrbAsReqBuilder.State.INIT, "Cannot get keys");
      if (this.password != null) {
         int[] var2 = EType.getDefaults("default_tkt_enctypes");
         EncryptionKey[] var3 = new EncryptionKey[var2.length];
         String var4 = null;

         try {
            int var5;
            for(var5 = 0; var5 < var2.length; ++var5) {
               PAData.SaltAndParams var8 = PAData.getSaltAndParams(var2[var5], this.paList);
               if (var8 != null) {
                  if (var2[var5] != 23 && var8.salt != null) {
                     var4 = var8.salt;
                  }

                  var3[var5] = EncryptionKey.acquireSecretKey(this.cname, this.password, var2[var5], var8);
               }
            }

            if (var4 == null) {
               var4 = this.cname.getSalt();
            }

            for(var5 = 0; var5 < var2.length; ++var5) {
               if (var3[var5] == null) {
                  var3[var5] = EncryptionKey.acquireSecretKey((char[])this.password, (String)var4, var2[var5], (byte[])null);
               }
            }

            return var3;
         } catch (IOException var7) {
            KrbException var6 = new KrbException(909);
            var6.initCause(var7);
            throw var6;
         }
      } else {
         throw new IllegalStateException("Required password not provided");
      }
   }

   public void setOptions(KDCOptions var1) {
      this.checkState(KrbAsReqBuilder.State.INIT, "Cannot specify options");
      this.options = var1;
   }

   public void setTarget(PrincipalName var1) {
      this.checkState(KrbAsReqBuilder.State.INIT, "Cannot specify target");
      this.sname = var1;
   }

   public void setAddresses(HostAddresses var1) {
      this.checkState(KrbAsReqBuilder.State.INIT, "Cannot specify addresses");
      this.addresses = var1;
   }

   private KrbAsReq build(EncryptionKey var1) throws KrbException, IOException {
      int[] var2;
      if (this.password != null) {
         var2 = EType.getDefaults("default_tkt_enctypes");
      } else {
         EncryptionKey[] var3 = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
         var2 = EType.getDefaults("default_tkt_enctypes", var3);
         EncryptionKey[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EncryptionKey var7 = var4[var6];
            var7.destroy();
         }
      }

      return new KrbAsReq(var1, this.options, this.cname, this.sname, this.from, this.till, this.rtime, var2, this.addresses);
   }

   private KrbAsReqBuilder resolve() throws KrbException, Asn1Exception, IOException {
      if (this.ktab != null) {
         this.rep.decryptUsingKeyTab(this.ktab, this.req, this.cname);
      } else {
         this.rep.decryptUsingPassword(this.password, this.req, this.cname);
      }

      if (this.rep.getPA() != null) {
         if (this.paList != null && this.paList.length != 0) {
            int var1 = this.rep.getPA().length;
            if (var1 > 0) {
               int var2 = this.paList.length;
               this.paList = (PAData[])Arrays.copyOf((Object[])this.paList, this.paList.length + var1);
               System.arraycopy(this.rep.getPA(), 0, this.paList, var2, var1);
            }
         } else {
            this.paList = this.rep.getPA();
         }
      }

      return this;
   }

   private KrbAsReqBuilder send() throws KrbException, IOException {
      boolean var1 = false;
      KdcComm var2 = new KdcComm(this.cname.getRealmAsString());
      EncryptionKey var3 = null;

      while(true) {
         try {
            this.req = this.build(var3);
            this.rep = new KrbAsRep(var2.send(this.req.encoding()));
            return this;
         } catch (KrbException var12) {
            if (var1 || var12.returnCode() != 24 && var12.returnCode() != 25) {
               throw var12;
            }

            if (Krb5.DEBUG) {
               System.out.println("KrbAsReqBuilder: PREAUTH FAILED/REQ, re-send AS-REQ");
            }

            var1 = true;
            KRBError var5 = var12.getError();
            int var6 = PAData.getPreferredEType(var5.getPA(), EType.getDefaults("default_tkt_enctypes")[0]);
            if (this.password == null) {
               EncryptionKey[] var7 = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
               var3 = EncryptionKey.findKey(var6, var7);
               if (var3 != null) {
                  var3 = (EncryptionKey)var3.clone();
               }

               EncryptionKey[] var8 = var7;
               int var9 = var7.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  EncryptionKey var11 = var8[var10];
                  var11.destroy();
               }
            } else {
               var3 = EncryptionKey.acquireSecretKey(this.cname, this.password, var6, PAData.getSaltAndParams(var6, var5.getPA()));
            }

            this.paList = var5.getPA();
         }
      }
   }

   public KrbAsReqBuilder action() throws KrbException, Asn1Exception, IOException {
      this.checkState(KrbAsReqBuilder.State.INIT, "Cannot call action");
      this.state = KrbAsReqBuilder.State.REQ_OK;
      return this.send().resolve();
   }

   public Credentials getCreds() {
      this.checkState(KrbAsReqBuilder.State.REQ_OK, "Cannot retrieve creds");
      return this.rep.getCreds();
   }

   public sun.security.krb5.internal.ccache.Credentials getCCreds() {
      this.checkState(KrbAsReqBuilder.State.REQ_OK, "Cannot retrieve CCreds");
      return this.rep.getCCreds();
   }

   public void destroy() {
      this.state = KrbAsReqBuilder.State.DESTROYED;
      if (this.password != null) {
         Arrays.fill(this.password, '\u0000');
      }

   }

   private void checkState(KrbAsReqBuilder.State var1, String var2) {
      if (this.state != var1) {
         throw new IllegalStateException(var2 + " at " + var1 + " state");
      }
   }

   private static enum State {
      INIT,
      REQ_OK,
      DESTROYED;
   }
}
