package sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.krb5.Confounder;

class WrapToken_v2 extends MessageToken_v2 {
   byte[] confounder = null;
   private final boolean privacy;

   public WrapToken_v2(Krb5Context var1, byte[] var2, int var3, int var4, MessageProp var5) throws GSSException {
      super(1284, var1, var2, var3, var4, var5);
      this.privacy = var5.getPrivacy();
   }

   public WrapToken_v2(Krb5Context var1, InputStream var2, MessageProp var3) throws GSSException {
      super(1284, var1, var2, var3);
      this.privacy = var3.getPrivacy();
   }

   public byte[] getData() throws GSSException {
      byte[] var1 = new byte[this.tokenDataLen];
      int var2 = this.getData(var1, 0);
      return Arrays.copyOf(var1, var2);
   }

   public int getData(byte[] var1, int var2) throws GSSException {
      if (this.privacy) {
         this.cipherHelper.decryptData(this, this.tokenData, 0, this.tokenDataLen, var1, var2, this.getKeyUsage());
         return this.tokenDataLen - 16 - 16 - this.cipherHelper.getChecksumLength();
      } else {
         int var3 = this.tokenDataLen - this.cipherHelper.getChecksumLength();
         System.arraycopy(this.tokenData, 0, var1, var2, var3);
         if (!this.verifySign(var1, var2, var3)) {
            throw new GSSException(6, -1, "Corrupt checksum in Wrap token");
         } else {
            return var3;
         }
      }
   }

   public WrapToken_v2(Krb5Context var1, MessageProp var2, byte[] var3, int var4, int var5) throws GSSException {
      super(1284, var1);
      this.confounder = Confounder.bytes(16);
      this.genSignAndSeqNumber(var2, var3, var4, var5);
      if (!var1.getConfState()) {
         var2.setPrivacy(false);
      }

      this.privacy = var2.getPrivacy();
      if (!this.privacy) {
         this.tokenData = new byte[var5 + this.checksum.length];
         System.arraycopy(var3, var4, this.tokenData, 0, var5);
         System.arraycopy(this.checksum, 0, this.tokenData, var5, this.checksum.length);
      } else {
         this.tokenData = this.cipherHelper.encryptData(this, this.confounder, this.getTokenHeader(), var3, var4, var5, this.getKeyUsage());
      }

   }

   public void encode(OutputStream var1) throws IOException {
      this.encodeHeader(var1);
      var1.write(this.tokenData);
   }

   public byte[] encode() throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(16 + this.tokenData.length);
      this.encode(var1);
      return var1.toByteArray();
   }

   public int encode(byte[] var1, int var2) throws IOException {
      byte[] var3 = this.encode();
      System.arraycopy(var3, 0, var1, var2, var3.length);
      return var3.length;
   }

   static int getSizeLimit(int var0, boolean var1, int var2, CipherHelper var3) throws GSSException {
      return GSSHeader.getMaxMechTokenSize(OID, var2) - (16 + var3.getChecksumLength() + 16) - 8;
   }
}
