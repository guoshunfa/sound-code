package sun.security.jgss.krb5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSToken;
import sun.security.krb5.EncryptionKey;

abstract class MessageToken_v2 extends Krb5Token {
   protected static final int TOKEN_HEADER_SIZE = 16;
   private static final int TOKEN_ID_POS = 0;
   private static final int TOKEN_FLAG_POS = 2;
   private static final int TOKEN_EC_POS = 4;
   private static final int TOKEN_RRC_POS = 6;
   protected static final int CONFOUNDER_SIZE = 16;
   static final int KG_USAGE_ACCEPTOR_SEAL = 22;
   static final int KG_USAGE_ACCEPTOR_SIGN = 23;
   static final int KG_USAGE_INITIATOR_SEAL = 24;
   static final int KG_USAGE_INITIATOR_SIGN = 25;
   private static final int FLAG_SENDER_IS_ACCEPTOR = 1;
   private static final int FLAG_WRAP_CONFIDENTIAL = 2;
   private static final int FLAG_ACCEPTOR_SUBKEY = 4;
   private static final int FILLER = 255;
   private MessageToken_v2.MessageTokenHeader tokenHeader;
   private int tokenId;
   private int seqNumber;
   protected byte[] tokenData;
   protected int tokenDataLen;
   private int key_usage;
   private int ec;
   private int rrc;
   byte[] checksum;
   private boolean confState;
   private boolean initiator;
   private boolean have_acceptor_subkey;
   CipherHelper cipherHelper;

   MessageToken_v2(int var1, Krb5Context var2, byte[] var3, int var4, int var5, MessageProp var6) throws GSSException {
      this(var1, var2, new ByteArrayInputStream(var3, var4, var5), var6);
   }

   MessageToken_v2(int var1, Krb5Context var2, InputStream var3, MessageProp var4) throws GSSException {
      this.tokenHeader = null;
      this.tokenId = 0;
      this.key_usage = 0;
      this.ec = 0;
      this.rrc = 0;
      this.checksum = null;
      this.confState = true;
      this.initiator = true;
      this.have_acceptor_subkey = false;
      this.cipherHelper = null;
      this.init(var1, var2);

      try {
         if (!this.confState) {
            var4.setPrivacy(false);
         }

         this.tokenHeader = new MessageToken_v2.MessageTokenHeader(var3, var4, var1);
         if (var1 == 1284) {
            this.key_usage = !this.initiator ? 24 : 22;
         } else if (var1 == 1028) {
            this.key_usage = !this.initiator ? 25 : 23;
         }

         boolean var5 = false;
         int var9;
         if (var1 == 1284 && var4.getPrivacy()) {
            var9 = 32 + this.cipherHelper.getChecksumLength();
         } else {
            var9 = this.cipherHelper.getChecksumLength();
         }

         if (var1 == 1028) {
            this.tokenDataLen = var9;
            this.tokenData = new byte[var9];
            readFully(var3, this.tokenData);
         } else {
            this.tokenDataLen = var3.available();
            if (this.tokenDataLen >= var9) {
               this.tokenData = new byte[this.tokenDataLen];
               readFully(var3, this.tokenData);
            } else {
               byte[] var6 = new byte[var9];
               readFully(var3, var6);
               int var7 = var3.available();
               this.tokenDataLen = var9 + var7;
               this.tokenData = Arrays.copyOf(var6, this.tokenDataLen);
               readFully(var3, this.tokenData, var9, var7);
            }
         }

         if (var1 == 1284) {
            this.rotate();
         }

         if (var1 == 1028 || var1 == 1284 && !var4.getPrivacy()) {
            int var10 = this.cipherHelper.getChecksumLength();
            this.checksum = new byte[var10];
            System.arraycopy(this.tokenData, this.tokenDataLen - var10, this.checksum, 0, var10);
            if (var1 == 1284 && !var4.getPrivacy() && var10 != this.ec) {
               throw new GSSException(10, -1, getTokenName(var1) + ":EC incorrect!");
            }
         }

      } catch (IOException var8) {
         throw new GSSException(10, -1, getTokenName(var1) + ":" + var8.getMessage());
      }
   }

   public final int getTokenId() {
      return this.tokenId;
   }

   public final int getKeyUsage() {
      return this.key_usage;
   }

   public final boolean getConfState() {
      return this.confState;
   }

   public void genSignAndSeqNumber(MessageProp var1, byte[] var2, int var3, int var4) throws GSSException {
      int var5 = var1.getQOP();
      if (var5 != 0) {
         byte var7 = 0;
         var1.setQOP(var7);
      }

      if (!this.confState) {
         var1.setPrivacy(false);
      }

      this.tokenHeader = new MessageToken_v2.MessageTokenHeader(this.tokenId, var1.getPrivacy());
      if (this.tokenId == 1284) {
         this.key_usage = this.initiator ? 24 : 22;
      } else if (this.tokenId == 1028) {
         this.key_usage = this.initiator ? 25 : 23;
      }

      if (this.tokenId == 1028 || !var1.getPrivacy() && this.tokenId == 1284) {
         this.checksum = this.getChecksum(var2, var3, var4);
      }

      if (!var1.getPrivacy() && this.tokenId == 1284) {
         byte[] var6 = this.tokenHeader.getBytes();
         var6[4] = (byte)(this.checksum.length >>> 8);
         var6[5] = (byte)this.checksum.length;
      }

   }

   public final boolean verifySign(byte[] var1, int var2, int var3) throws GSSException {
      byte[] var4 = this.getChecksum(var1, var2, var3);
      return MessageDigest.isEqual(this.checksum, var4);
   }

   private void rotate() {
      if (this.rrc % this.tokenDataLen != 0) {
         this.rrc %= this.tokenDataLen;
         byte[] var1 = new byte[this.tokenDataLen];
         System.arraycopy(this.tokenData, this.rrc, var1, 0, this.tokenDataLen - this.rrc);
         System.arraycopy(this.tokenData, 0, var1, this.tokenDataLen - this.rrc, this.rrc);
         this.tokenData = var1;
      }

   }

   public final int getSequenceNumber() {
      return this.seqNumber;
   }

   byte[] getChecksum(byte[] var1, int var2, int var3) throws GSSException {
      byte[] var4 = this.tokenHeader.getBytes();
      int var5 = var4[2] & 2;
      if (var5 == 0 && this.tokenId == 1284) {
         var4[4] = 0;
         var4[5] = 0;
         var4[6] = 0;
         var4[7] = 0;
      }

      return this.cipherHelper.calculateChecksum(var4, var1, var2, var3, this.key_usage);
   }

   MessageToken_v2(int var1, Krb5Context var2) throws GSSException {
      this.tokenHeader = null;
      this.tokenId = 0;
      this.key_usage = 0;
      this.ec = 0;
      this.rrc = 0;
      this.checksum = null;
      this.confState = true;
      this.initiator = true;
      this.have_acceptor_subkey = false;
      this.cipherHelper = null;
      this.init(var1, var2);
      this.seqNumber = var2.incrementMySequenceNumber();
   }

   private void init(int var1, Krb5Context var2) throws GSSException {
      this.tokenId = var1;
      this.confState = var2.getConfState();
      this.initiator = var2.isInitiator();
      this.have_acceptor_subkey = var2.getKeySrc() == 2;
      this.cipherHelper = var2.getCipherHelper((EncryptionKey)null);
   }

   protected void encodeHeader(OutputStream var1) throws IOException {
      this.tokenHeader.encode(var1);
   }

   public abstract void encode(OutputStream var1) throws IOException;

   protected final byte[] getTokenHeader() {
      return this.tokenHeader.getBytes();
   }

   class MessageTokenHeader {
      private int tokenId;
      private byte[] bytes = new byte[16];

      public MessageTokenHeader(int var2, boolean var3) throws GSSException {
         this.tokenId = var2;
         this.bytes[0] = (byte)(var2 >>> 8);
         this.bytes[1] = (byte)var2;
         boolean var4 = false;
         int var6 = (MessageToken_v2.this.initiator ? 0 : 1) | (var3 && var2 != 1028 ? 2 : 0) | (MessageToken_v2.this.have_acceptor_subkey ? 4 : 0);
         this.bytes[2] = (byte)var6;
         this.bytes[3] = -1;
         if (var2 == 1284) {
            this.bytes[4] = 0;
            this.bytes[5] = 0;
            this.bytes[6] = 0;
            this.bytes[7] = 0;
         } else if (var2 == 1028) {
            for(int var5 = 4; var5 < 8; ++var5) {
               this.bytes[var5] = -1;
            }
         }

         GSSToken.writeBigEndian(MessageToken_v2.this.seqNumber, this.bytes, 12);
      }

      public MessageTokenHeader(InputStream var2, MessageProp var3, int var4) throws IOException, GSSException {
         GSSToken.readFully(var2, this.bytes, 0, 16);
         this.tokenId = GSSToken.readInt(this.bytes, 0);
         if (this.tokenId != var4) {
            throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token ID!");
         } else {
            int var5 = MessageToken_v2.this.initiator ? 1 : 0;
            int var6 = this.bytes[2] & 1;
            if (var6 != var5) {
               throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Acceptor Flag Error!");
            } else {
               int var7 = this.bytes[2] & 2;
               if (var7 == 2 && this.tokenId == 1284) {
                  var3.setPrivacy(true);
               } else {
                  var3.setPrivacy(false);
               }

               if (this.tokenId == 1284) {
                  if ((this.bytes[3] & 255) != 255) {
                     throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token Filler!");
                  }

                  MessageToken_v2.this.ec = GSSToken.readBigEndian(this.bytes, 4, 2);
                  MessageToken_v2.this.rrc = GSSToken.readBigEndian(this.bytes, 6, 2);
               } else if (this.tokenId == 1028) {
                  for(int var8 = 3; var8 < 8; ++var8) {
                     if ((this.bytes[var8] & 255) != 255) {
                        throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token Filler!");
                     }
                  }
               }

               var3.setQOP(0);
               MessageToken_v2.this.seqNumber = GSSToken.readBigEndian(this.bytes, 0, 8);
            }
         }
      }

      public final void encode(OutputStream var1) throws IOException {
         var1.write(this.bytes);
      }

      public final int getTokenId() {
         return this.tokenId;
      }

      public final byte[] getBytes() {
         return this.bytes;
      }
   }
}
