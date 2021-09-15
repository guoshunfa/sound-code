package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class AWTCharset extends Charset {
   protected Charset awtCs;
   protected Charset javaCs;

   public AWTCharset(String var1, Charset var2) {
      super(var1, (String[])null);
      this.javaCs = var2;
      this.awtCs = this;
   }

   public boolean contains(Charset var1) {
      return this.javaCs == null ? false : this.javaCs.contains(var1);
   }

   public CharsetEncoder newEncoder() {
      if (this.javaCs == null) {
         throw new Error("Encoder is not supported by this Charset");
      } else {
         return new AWTCharset.Encoder(this.javaCs.newEncoder());
      }
   }

   public CharsetDecoder newDecoder() {
      if (this.javaCs == null) {
         throw new Error("Decoder is not supported by this Charset");
      } else {
         return new AWTCharset.Decoder(this.javaCs.newDecoder());
      }
   }

   public class Decoder extends CharsetDecoder {
      protected CharsetDecoder dec;
      private String nr;
      ByteBuffer fbb;

      protected Decoder() {
         this(AWTCharset.this.javaCs.newDecoder());
      }

      protected Decoder(CharsetDecoder var2) {
         super(AWTCharset.this.awtCs, var2.averageCharsPerByte(), var2.maxCharsPerByte());
         this.fbb = ByteBuffer.allocate(0);
         this.dec = var2;
      }

      protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
         return this.dec.decode(var1, var2, true);
      }

      protected CoderResult implFlush(CharBuffer var1) {
         this.dec.decode(this.fbb, var1, true);
         return this.dec.flush(var1);
      }

      protected void implReset() {
         this.dec.reset();
      }

      protected void implReplaceWith(String var1) {
         if (this.dec != null) {
            this.dec.replaceWith(var1);
         }

      }

      protected void implOnMalformedInput(CodingErrorAction var1) {
         this.dec.onMalformedInput(var1);
      }

      protected void implOnUnmappableCharacter(CodingErrorAction var1) {
         this.dec.onUnmappableCharacter(var1);
      }
   }

   public class Encoder extends CharsetEncoder {
      protected CharsetEncoder enc;

      protected Encoder() {
         this(AWTCharset.this.javaCs.newEncoder());
      }

      protected Encoder(CharsetEncoder var2) {
         super(AWTCharset.this.awtCs, var2.averageBytesPerChar(), var2.maxBytesPerChar());
         this.enc = var2;
      }

      public boolean canEncode(char var1) {
         return this.enc.canEncode(var1);
      }

      public boolean canEncode(CharSequence var1) {
         return this.enc.canEncode(var1);
      }

      protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         return this.enc.encode(var1, var2, true);
      }

      protected CoderResult implFlush(ByteBuffer var1) {
         return this.enc.flush(var1);
      }

      protected void implReset() {
         this.enc.reset();
      }

      protected void implReplaceWith(byte[] var1) {
         if (this.enc != null) {
            this.enc.replaceWith(var1);
         }

      }

      protected void implOnMalformedInput(CodingErrorAction var1) {
         this.enc.onMalformedInput(var1);
      }

      protected void implOnUnmappableCharacter(CodingErrorAction var1) {
         this.enc.onUnmappableCharacter(var1);
      }

      public boolean isLegalReplacement(byte[] var1) {
         return true;
      }
   }
}
