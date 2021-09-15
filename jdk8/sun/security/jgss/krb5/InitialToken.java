package sun.security.jgss.krb5;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.security.auth.kerberos.DelegationPermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import sun.security.jgss.GSSToken;
import sun.security.krb5.Checksum;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbCred;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

abstract class InitialToken extends Krb5Token {
   private static final int CHECKSUM_TYPE = 32771;
   private static final int CHECKSUM_LENGTH_SIZE = 4;
   private static final int CHECKSUM_BINDINGS_SIZE = 16;
   private static final int CHECKSUM_FLAGS_SIZE = 4;
   private static final int CHECKSUM_DELEG_OPT_SIZE = 2;
   private static final int CHECKSUM_DELEG_LGTH_SIZE = 2;
   private static final int CHECKSUM_DELEG_FLAG = 1;
   private static final int CHECKSUM_MUTUAL_FLAG = 2;
   private static final int CHECKSUM_REPLAY_FLAG = 4;
   private static final int CHECKSUM_SEQUENCE_FLAG = 8;
   private static final int CHECKSUM_CONF_FLAG = 16;
   private static final int CHECKSUM_INTEG_FLAG = 32;
   private final byte[] CHECKSUM_FIRST_BYTES = new byte[]{16, 0, 0, 0};
   private static final int CHANNEL_BINDING_AF_INET = 2;
   private static final int CHANNEL_BINDING_AF_INET6 = 24;
   private static final int CHANNEL_BINDING_AF_NULL_ADDR = 255;
   private static final int Inet4_ADDRSZ = 4;
   private static final int Inet6_ADDRSZ = 16;

   private int getAddrType(InetAddress var1) {
      short var2 = 255;
      if (var1 instanceof Inet4Address) {
         var2 = 2;
      } else if (var1 instanceof Inet6Address) {
         var2 = 24;
      }

      return var2;
   }

   private byte[] getAddrBytes(InetAddress var1) throws GSSException {
      int var2 = this.getAddrType(var1);
      byte[] var3 = var1.getAddress();
      if (var3 != null) {
         switch(var2) {
         case 2:
            if (var3.length != 4) {
               throw new GSSException(11, -1, "Incorrect AF-INET address length in ChannelBinding.");
            }

            return var3;
         case 24:
            if (var3.length != 16) {
               throw new GSSException(11, -1, "Incorrect AF-INET6 address length in ChannelBinding.");
            }

            return var3;
         default:
            throw new GSSException(11, -1, "Cannot handle non AF-INET addresses in ChannelBinding.");
         }
      } else {
         return null;
      }
   }

   private byte[] computeChannelBinding(ChannelBinding var1) throws GSSException {
      InetAddress var2 = var1.getInitiatorAddress();
      InetAddress var3 = var1.getAcceptorAddress();
      int var4 = 20;
      int var5 = this.getAddrType(var2);
      int var6 = this.getAddrType(var3);
      byte[] var7 = null;
      if (var2 != null) {
         var7 = this.getAddrBytes(var2);
         var4 += var7.length;
      }

      byte[] var8 = null;
      if (var3 != null) {
         var8 = this.getAddrBytes(var3);
         var4 += var8.length;
      }

      byte[] var9 = var1.getApplicationData();
      if (var9 != null) {
         var4 += var9.length;
      }

      byte[] var10 = new byte[var4];
      byte var11 = 0;
      writeLittleEndian(var5, var10, var11);
      int var14 = var11 + 4;
      if (var7 != null) {
         writeLittleEndian(var7.length, var10, var14);
         var14 += 4;
         System.arraycopy(var7, 0, var10, var14, var7.length);
         var14 += var7.length;
      } else {
         var14 += 4;
      }

      writeLittleEndian(var6, var10, var14);
      var14 += 4;
      if (var8 != null) {
         writeLittleEndian(var8.length, var10, var14);
         var14 += 4;
         System.arraycopy(var8, 0, var10, var14, var8.length);
         var14 += var8.length;
      } else {
         var14 += 4;
      }

      if (var9 != null) {
         writeLittleEndian(var9.length, var10, var14);
         var14 += 4;
         System.arraycopy(var9, 0, var10, var14, var9.length);
         int var10000 = var14 + var9.length;
      } else {
         var14 += 4;
      }

      try {
         MessageDigest var12 = MessageDigest.getInstance("MD5");
         return var12.digest(var10);
      } catch (NoSuchAlgorithmException var13) {
         throw new GSSException(11, -1, "Could not get MD5 Message Digest - " + var13.getMessage());
      }
   }

   public abstract byte[] encode() throws IOException;

   protected class OverloadedChecksum {
      private byte[] checksumBytes = null;
      private Credentials delegCreds = null;
      private int flags = 0;

      public OverloadedChecksum(Krb5Context var2, Credentials var3, Credentials var4) throws KrbException, IOException, GSSException {
         byte[] var5 = null;
         byte var6 = 0;
         int var7 = 24;
         if (!var3.isForwardable()) {
            var2.setCredDelegState(false);
            var2.setDelegPolicyState(false);
         } else if (var2.getCredDelegState()) {
            if (var2.getDelegPolicyState() && !var4.checkDelegate()) {
               var2.setDelegPolicyState(false);
            }
         } else if (var2.getDelegPolicyState()) {
            if (var4.checkDelegate()) {
               var2.setCredDelegState(true);
            } else {
               var2.setDelegPolicyState(false);
            }
         }

         if (var2.getCredDelegState()) {
            KrbCred var8 = null;
            CipherHelper var9 = var2.getCipherHelper(var4.getSessionKey());
            if (this.useNullKey(var9)) {
               var8 = new KrbCred(var3, var4, EncryptionKey.NULL_KEY);
            } else {
               var8 = new KrbCred(var3, var4, var4.getSessionKey());
            }

            var5 = var8.getMessage();
            var7 += 4 + var5.length;
         }

         this.checksumBytes = new byte[var7];
         int var15 = var6 + 1;
         this.checksumBytes[var6] = InitialToken.this.CHECKSUM_FIRST_BYTES[0];
         this.checksumBytes[var15++] = InitialToken.this.CHECKSUM_FIRST_BYTES[1];
         this.checksumBytes[var15++] = InitialToken.this.CHECKSUM_FIRST_BYTES[2];
         this.checksumBytes[var15++] = InitialToken.this.CHECKSUM_FIRST_BYTES[3];
         ChannelBinding var16 = var2.getChannelBinding();
         byte[] var17;
         if (var16 != null) {
            var17 = InitialToken.this.computeChannelBinding(var2.getChannelBinding());
            System.arraycopy(var17, 0, this.checksumBytes, var15, var17.length);
         }

         var15 += 16;
         if (var2.getCredDelegState()) {
            this.flags |= 1;
         }

         if (var2.getMutualAuthState()) {
            this.flags |= 2;
         }

         if (var2.getReplayDetState()) {
            this.flags |= 4;
         }

         if (var2.getSequenceDetState()) {
            this.flags |= 8;
         }

         if (var2.getIntegState()) {
            this.flags |= 32;
         }

         if (var2.getConfState()) {
            this.flags |= 16;
         }

         var17 = new byte[4];
         GSSToken.writeLittleEndian(this.flags, var17);
         this.checksumBytes[var15++] = var17[0];
         this.checksumBytes[var15++] = var17[1];
         this.checksumBytes[var15++] = var17[2];
         this.checksumBytes[var15++] = var17[3];
         if (var2.getCredDelegState()) {
            PrincipalName var10 = var4.getServer();
            StringBuffer var11 = new StringBuffer("\"");
            var11.append(var10.getName()).append('"');
            String var12 = var10.getRealmAsString();
            var11.append(" \"krbtgt/").append(var12).append('@');
            var11.append(var12).append('"');
            SecurityManager var13 = System.getSecurityManager();
            if (var13 != null) {
               DelegationPermission var14 = new DelegationPermission(var11.toString());
               var13.checkPermission(var14);
            }

            this.checksumBytes[var15++] = 1;
            this.checksumBytes[var15++] = 0;
            if (var5.length > 65535) {
               throw new GSSException(11, -1, "Incorrect message length");
            }

            GSSToken.writeLittleEndian(var5.length, var17);
            this.checksumBytes[var15++] = var17[0];
            this.checksumBytes[var15++] = var17[1];
            System.arraycopy(var5, 0, this.checksumBytes, var15, var5.length);
         }

      }

      public OverloadedChecksum(Krb5Context var2, Checksum var3, EncryptionKey var4, EncryptionKey var5) throws GSSException, KrbException, IOException {
         boolean var6 = false;
         if (var3 == null) {
            GSSException var13 = new GSSException(11, -1, "No cksum in AP_REQ's authenticator");
            var13.initCause(new KrbException(50));
            throw var13;
         } else {
            this.checksumBytes = var3.getBytes();
            if (this.checksumBytes[0] == InitialToken.this.CHECKSUM_FIRST_BYTES[0] && this.checksumBytes[1] == InitialToken.this.CHECKSUM_FIRST_BYTES[1] && this.checksumBytes[2] == InitialToken.this.CHECKSUM_FIRST_BYTES[2] && this.checksumBytes[3] == InitialToken.this.CHECKSUM_FIRST_BYTES[3]) {
               ChannelBinding var7 = var2.getChannelBinding();
               byte[] var9;
               if (var7 != null) {
                  byte[] var8 = new byte[16];
                  System.arraycopy(this.checksumBytes, 4, var8, 0, 16);
                  var9 = new byte[16];
                  if (Arrays.equals(var9, var8)) {
                     throw new GSSException(1, -1, "Token missing ChannelBinding!");
                  }

                  byte[] var10 = InitialToken.this.computeChannelBinding(var7);
                  if (!Arrays.equals(var10, var8)) {
                     throw new GSSException(1, -1, "Bytes mismatch!");
                  }
               }

               this.flags = GSSToken.readLittleEndian(this.checksumBytes, 20, 4);
               if ((this.flags & 1) > 0) {
                  int var14 = GSSToken.readLittleEndian(this.checksumBytes, 26, 2);
                  var9 = new byte[var14];
                  System.arraycopy(this.checksumBytes, 28, var9, 0, var14);

                  KrbCred var15;
                  try {
                     var15 = new KrbCred(var9, var4);
                  } catch (KrbException var12) {
                     if (var5 == null) {
                        throw var12;
                     }

                     var15 = new KrbCred(var9, var5);
                  }

                  this.delegCreds = var15.getDelegatedCreds()[0];
               }

            } else {
               throw new GSSException(11, -1, "Incorrect checksum");
            }
         }
      }

      private boolean useNullKey(CipherHelper var1) {
         boolean var2 = true;
         if (var1.getProto() == 1 || var1.isArcFour()) {
            var2 = false;
         }

         return var2;
      }

      public Checksum getChecksum() throws KrbException {
         return new Checksum(this.checksumBytes, 32771);
      }

      public Credentials getDelegatedCreds() {
         return this.delegCreds;
      }

      public void setContextFlags(Krb5Context var1) {
         if ((this.flags & 1) > 0) {
            var1.setCredDelegState(true);
         }

         if ((this.flags & 2) == 0) {
            var1.setMutualAuthState(false);
         }

         if ((this.flags & 4) == 0) {
            var1.setReplayDetState(false);
         }

         if ((this.flags & 8) == 0) {
            var1.setSequenceDetState(false);
         }

         if ((this.flags & 16) == 0) {
            var1.setConfState(false);
         }

         if ((this.flags & 32) == 0) {
            var1.setIntegState(false);
         }

      }
   }
}
