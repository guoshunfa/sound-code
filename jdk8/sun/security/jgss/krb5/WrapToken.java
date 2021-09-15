package sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.krb5.Confounder;

class WrapToken extends MessageToken {
   static final int CONFOUNDER_SIZE = 8;
   static final byte[][] pads = new byte[][]{null, {1}, {2, 2}, {3, 3, 3}, {4, 4, 4, 4}, {5, 5, 5, 5, 5}, {6, 6, 6, 6, 6, 6}, {7, 7, 7, 7, 7, 7, 7}, {8, 8, 8, 8, 8, 8, 8, 8}};
   private boolean readTokenFromInputStream = true;
   private InputStream is = null;
   private byte[] tokenBytes = null;
   private int tokenOffset = 0;
   private int tokenLen = 0;
   private byte[] dataBytes = null;
   private int dataOffset = 0;
   private int dataLen = 0;
   private int dataSize = 0;
   byte[] confounder = null;
   byte[] padding = null;
   private boolean privacy = false;

   public WrapToken(Krb5Context var1, byte[] var2, int var3, int var4, MessageProp var5) throws GSSException {
      super(513, var1, var2, var3, var4, var5);
      this.readTokenFromInputStream = false;
      this.tokenBytes = var2;
      this.tokenOffset = var3;
      this.tokenLen = var4;
      this.privacy = var5.getPrivacy();
      this.dataSize = this.getGSSHeader().getMechTokenLength() - this.getKrb5TokenSize();
   }

   public WrapToken(Krb5Context var1, InputStream var2, MessageProp var3) throws GSSException {
      super(513, var1, var2, var3);
      this.is = var2;
      this.privacy = var3.getPrivacy();
      this.dataSize = this.getGSSHeader().getMechTokenLength() - this.getTokenSize();
   }

   public byte[] getData() throws GSSException {
      byte[] var1 = new byte[this.dataSize];
      this.getData(var1, 0);
      byte[] var2 = new byte[this.dataSize - this.confounder.length - this.padding.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }

   public int getData(byte[] var1, int var2) throws GSSException {
      if (this.readTokenFromInputStream) {
         this.getDataFromStream(var1, var2);
      } else {
         this.getDataFromBuffer(var1, var2);
      }

      return this.dataSize - this.confounder.length - this.padding.length;
   }

   private void getDataFromBuffer(byte[] var1, int var2) throws GSSException {
      GSSHeader var3 = this.getGSSHeader();
      int var4 = this.tokenOffset + var3.getLength() + this.getTokenSize();
      if (var4 + this.dataSize > this.tokenOffset + this.tokenLen) {
         throw new GSSException(10, -1, "Insufficient data in " + getTokenName(this.getTokenId()));
      } else {
         this.confounder = new byte[8];
         if (this.privacy) {
            this.cipherHelper.decryptData(this, this.tokenBytes, var4, this.dataSize, var1, var2);
         } else {
            System.arraycopy(this.tokenBytes, var4, this.confounder, 0, 8);
            int var5 = this.tokenBytes[var4 + this.dataSize - 1];
            if (var5 < 0) {
               var5 = 0;
            }

            if (var5 > 8) {
               var5 %= 8;
            }

            this.padding = pads[var5];
            System.arraycopy(this.tokenBytes, var4 + 8, var1, var2, this.dataSize - 8 - var5);
         }

         if (!this.verifySignAndSeqNumber(this.confounder, var1, var2, this.dataSize - 8 - this.padding.length, this.padding)) {
            throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token");
         }
      }
   }

   private void getDataFromStream(byte[] var1, int var2) throws GSSException {
      GSSHeader var3 = this.getGSSHeader();
      this.confounder = new byte[8];

      try {
         if (this.privacy) {
            this.cipherHelper.decryptData(this, this.is, this.dataSize, var1, var2);
         } else {
            readFully(this.is, this.confounder);
            if (this.cipherHelper.isArcFour()) {
               this.padding = pads[1];
               readFully(this.is, var1, var2, this.dataSize - 8 - 1);
            } else {
               int var4 = (this.dataSize - 8) / 8 - 1;
               int var5 = var2;

               for(int var6 = 0; var6 < var4; ++var6) {
                  readFully(this.is, var1, var5, 8);
                  var5 += 8;
               }

               byte[] var9 = new byte[8];
               readFully(this.is, var9);
               byte var7 = var9[7];
               this.padding = pads[var7];
               System.arraycopy(var9, 0, var1, var5, var9.length - var7);
            }
         }
      } catch (IOException var8) {
         throw new GSSException(10, -1, getTokenName(this.getTokenId()) + ": " + var8.getMessage());
      }

      if (!this.verifySignAndSeqNumber(this.confounder, var1, var2, this.dataSize - 8 - this.padding.length, this.padding)) {
         throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token");
      }
   }

   private byte[] getPadding(int var1) {
      boolean var2 = false;
      int var3;
      if (this.cipherHelper.isArcFour()) {
         var3 = 1;
      } else {
         var3 = var1 % 8;
         var3 = 8 - var3;
      }

      return pads[var3];
   }

   public WrapToken(Krb5Context var1, MessageProp var2, byte[] var3, int var4, int var5) throws GSSException {
      super(513, var1);
      this.confounder = Confounder.bytes(8);
      this.padding = this.getPadding(var5);
      this.dataSize = this.confounder.length + var5 + this.padding.length;
      this.dataBytes = var3;
      this.dataOffset = var4;
      this.dataLen = var5;
      this.genSignAndSeqNumber(var2, this.confounder, var3, var4, var5, this.padding);
      if (!var1.getConfState()) {
         var2.setPrivacy(false);
      }

      this.privacy = var2.getPrivacy();
   }

   public void encode(OutputStream var1) throws IOException, GSSException {
      super.encode(var1);
      if (!this.privacy) {
         var1.write(this.confounder);
         var1.write(this.dataBytes, this.dataOffset, this.dataLen);
         var1.write(this.padding);
      } else {
         this.cipherHelper.encryptData(this, this.confounder, this.dataBytes, this.dataOffset, this.dataLen, this.padding, var1);
      }

   }

   public byte[] encode() throws IOException, GSSException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(this.dataSize + 50);
      this.encode(var1);
      return var1.toByteArray();
   }

   public int encode(byte[] var1, int var2) throws IOException, GSSException {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      super.encode(var3);
      byte[] var4 = var3.toByteArray();
      System.arraycopy(var4, 0, var1, var2, var4.length);
      var2 += var4.length;
      if (!this.privacy) {
         System.arraycopy(this.confounder, 0, var1, var2, this.confounder.length);
         var2 += this.confounder.length;
         System.arraycopy(this.dataBytes, this.dataOffset, var1, var2, this.dataLen);
         var2 += this.dataLen;
         System.arraycopy(this.padding, 0, var1, var2, this.padding.length);
      } else {
         this.cipherHelper.encryptData(this, this.confounder, this.dataBytes, this.dataOffset, this.dataLen, this.padding, var1, var2);
      }

      return var4.length + this.confounder.length + this.dataLen + this.padding.length;
   }

   protected int getKrb5TokenSize() throws GSSException {
      return this.getTokenSize() + this.dataSize;
   }

   protected int getSealAlg(boolean var1, int var2) throws GSSException {
      return !var1 ? '\uffff' : this.cipherHelper.getSealAlg();
   }

   static int getSizeLimit(int var0, boolean var1, int var2, CipherHelper var3) throws GSSException {
      return GSSHeader.getMaxMechTokenSize(OID, var2) - (getTokenSize(var3) + 8) - 8;
   }
}
