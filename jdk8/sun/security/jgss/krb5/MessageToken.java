package sun.security.jgss.krb5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.jgss.GSSToken;
import sun.security.krb5.EncryptionKey;

abstract class MessageToken extends Krb5Token {
   private static final int TOKEN_NO_CKSUM_SIZE = 16;
   private static final int FILLER = 65535;
   static final int SGN_ALG_DES_MAC_MD5 = 0;
   static final int SGN_ALG_DES_MAC = 512;
   static final int SGN_ALG_HMAC_SHA1_DES3_KD = 1024;
   static final int SEAL_ALG_NONE = 65535;
   static final int SEAL_ALG_DES = 0;
   static final int SEAL_ALG_DES3_KD = 512;
   static final int SEAL_ALG_ARCFOUR_HMAC = 4096;
   static final int SGN_ALG_HMAC_MD5_ARCFOUR = 4352;
   private static final int TOKEN_ID_POS = 0;
   private static final int SIGN_ALG_POS = 2;
   private static final int SEAL_ALG_POS = 4;
   private int seqNumber;
   private boolean confState;
   private boolean initiator;
   private int tokenId;
   private GSSHeader gssHeader;
   private MessageToken.MessageTokenHeader tokenHeader;
   private byte[] checksum;
   private byte[] encSeqNumber;
   private byte[] seqNumberData;
   CipherHelper cipherHelper;

   MessageToken(int var1, Krb5Context var2, byte[] var3, int var4, int var5, MessageProp var6) throws GSSException {
      this(var1, var2, new ByteArrayInputStream(var3, var4, var5), var6);
   }

   MessageToken(int var1, Krb5Context var2, InputStream var3, MessageProp var4) throws GSSException {
      this.confState = true;
      this.initiator = true;
      this.tokenId = 0;
      this.gssHeader = null;
      this.tokenHeader = null;
      this.checksum = null;
      this.encSeqNumber = null;
      this.seqNumberData = null;
      this.cipherHelper = null;
      this.init(var1, var2);

      try {
         this.gssHeader = new GSSHeader(var3);
         if (!this.gssHeader.getOid().equals((Object)OID)) {
            throw new GSSException(10, -1, getTokenName(var1));
         } else {
            if (!this.confState) {
               var4.setPrivacy(false);
            }

            this.tokenHeader = new MessageToken.MessageTokenHeader(var3, var4);
            this.encSeqNumber = new byte[8];
            readFully(var3, this.encSeqNumber);
            this.checksum = new byte[this.cipherHelper.getChecksumLength()];
            readFully(var3, this.checksum);
         }
      } catch (IOException var6) {
         throw new GSSException(10, -1, getTokenName(var1) + ":" + var6.getMessage());
      }
   }

   public final GSSHeader getGSSHeader() {
      return this.gssHeader;
   }

   public final int getTokenId() {
      return this.tokenId;
   }

   public final byte[] getEncSeqNumber() {
      return this.encSeqNumber;
   }

   public final byte[] getChecksum() {
      return this.checksum;
   }

   public final boolean getConfState() {
      return this.confState;
   }

   public void genSignAndSeqNumber(MessageProp var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws GSSException {
      int var7 = var1.getQOP();
      if (var7 != 0) {
         var7 = 0;
         var1.setQOP(var7);
      }

      if (!this.confState) {
         var1.setPrivacy(false);
      }

      this.tokenHeader = new MessageToken.MessageTokenHeader(this.tokenId, var1.getPrivacy(), var7);
      this.checksum = this.getChecksum(var2, var3, var4, var5, var6);
      this.seqNumberData = new byte[8];
      if (this.cipherHelper.isArcFour()) {
         writeBigEndian(this.seqNumber, this.seqNumberData);
      } else {
         writeLittleEndian(this.seqNumber, this.seqNumberData);
      }

      if (!this.initiator) {
         this.seqNumberData[4] = -1;
         this.seqNumberData[5] = -1;
         this.seqNumberData[6] = -1;
         this.seqNumberData[7] = -1;
      }

      this.encSeqNumber = this.cipherHelper.encryptSeq(this.checksum, this.seqNumberData, 0, 8);
   }

   public final boolean verifySignAndSeqNumber(byte[] var1, byte[] var2, int var3, int var4, byte[] var5) throws GSSException {
      byte[] var6 = this.getChecksum(var1, var2, var3, var4, var5);
      if (MessageDigest.isEqual(this.checksum, var6)) {
         this.seqNumberData = this.cipherHelper.decryptSeq(this.checksum, this.encSeqNumber, 0, 8);
         byte var7 = 0;
         if (this.initiator) {
            var7 = -1;
         }

         if (this.seqNumberData[4] == var7 && this.seqNumberData[5] == var7 && this.seqNumberData[6] == var7 && this.seqNumberData[7] == var7) {
            return true;
         }
      }

      return false;
   }

   public final int getSequenceNumber() {
      boolean var1 = false;
      int var2;
      if (this.cipherHelper.isArcFour()) {
         var2 = readBigEndian(this.seqNumberData, 0, 4);
      } else {
         var2 = readLittleEndian(this.seqNumberData, 0, 4);
      }

      return var2;
   }

   private byte[] getChecksum(byte[] var1, byte[] var2, int var3, int var4, byte[] var5) throws GSSException {
      byte[] var6 = this.tokenHeader.getBytes();
      byte[] var8 = var6;
      if (var1 != null) {
         var8 = new byte[var6.length + var1.length];
         System.arraycopy(var6, 0, var8, 0, var6.length);
         System.arraycopy(var1, 0, var8, var6.length, var1.length);
      }

      return this.cipherHelper.calculateChecksum(this.tokenHeader.getSignAlg(), var8, var5, var2, var3, var4, this.tokenId);
   }

   MessageToken(int var1, Krb5Context var2) throws GSSException {
      this.confState = true;
      this.initiator = true;
      this.tokenId = 0;
      this.gssHeader = null;
      this.tokenHeader = null;
      this.checksum = null;
      this.encSeqNumber = null;
      this.seqNumberData = null;
      this.cipherHelper = null;
      this.init(var1, var2);
      this.seqNumber = var2.incrementMySequenceNumber();
   }

   private void init(int var1, Krb5Context var2) throws GSSException {
      this.tokenId = var1;
      this.confState = var2.getConfState();
      this.initiator = var2.isInitiator();
      this.cipherHelper = var2.getCipherHelper((EncryptionKey)null);
   }

   public void encode(OutputStream var1) throws IOException, GSSException {
      this.gssHeader = new GSSHeader(OID, this.getKrb5TokenSize());
      this.gssHeader.encode(var1);
      this.tokenHeader.encode(var1);
      var1.write(this.encSeqNumber);
      var1.write(this.checksum);
   }

   protected int getKrb5TokenSize() throws GSSException {
      return this.getTokenSize();
   }

   protected final int getTokenSize() throws GSSException {
      return 16 + this.cipherHelper.getChecksumLength();
   }

   protected static final int getTokenSize(CipherHelper var0) throws GSSException {
      return 16 + var0.getChecksumLength();
   }

   protected abstract int getSealAlg(boolean var1, int var2) throws GSSException;

   protected int getSgnAlg(int var1) throws GSSException {
      return this.cipherHelper.getSgnAlg();
   }

   class MessageTokenHeader {
      private int tokenId;
      private int signAlg;
      private int sealAlg;
      private byte[] bytes = new byte[8];

      public MessageTokenHeader(int var2, boolean var3, int var4) throws GSSException {
         this.tokenId = var2;
         this.signAlg = MessageToken.this.getSgnAlg(var4);
         this.sealAlg = MessageToken.this.getSealAlg(var3, var4);
         this.bytes[0] = (byte)(var2 >>> 8);
         this.bytes[1] = (byte)var2;
         this.bytes[2] = (byte)(this.signAlg >>> 8);
         this.bytes[3] = (byte)this.signAlg;
         this.bytes[4] = (byte)(this.sealAlg >>> 8);
         this.bytes[5] = (byte)this.sealAlg;
         this.bytes[6] = -1;
         this.bytes[7] = -1;
      }

      public MessageTokenHeader(InputStream var2, MessageProp var3) throws IOException {
         GSSToken.readFully(var2, this.bytes);
         this.tokenId = GSSToken.readInt(this.bytes, 0);
         this.signAlg = GSSToken.readInt(this.bytes, 2);
         this.sealAlg = GSSToken.readInt(this.bytes, 4);
         int var4 = GSSToken.readInt(this.bytes, 6);
         switch(this.sealAlg) {
         case 0:
         case 512:
         case 4096:
            var3.setPrivacy(true);
            break;
         default:
            var3.setPrivacy(false);
         }

         var3.setQOP(0);
      }

      public final void encode(OutputStream var1) throws IOException {
         var1.write(this.bytes);
      }

      public final int getTokenId() {
         return this.tokenId;
      }

      public final int getSignAlg() {
         return this.signAlg;
      }

      public final int getSealAlg() {
         return this.sealAlg;
      }

      public final byte[] getBytes() {
         return this.bytes;
      }
   }
}
