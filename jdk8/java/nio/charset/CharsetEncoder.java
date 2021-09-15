package java.nio.charset;

import java.lang.ref.WeakReference;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public abstract class CharsetEncoder {
   private final Charset charset;
   private final float averageBytesPerChar;
   private final float maxBytesPerChar;
   private byte[] replacement;
   private CodingErrorAction malformedInputAction;
   private CodingErrorAction unmappableCharacterAction;
   private static final int ST_RESET = 0;
   private static final int ST_CODING = 1;
   private static final int ST_END = 2;
   private static final int ST_FLUSHED = 3;
   private int state;
   private static String[] stateNames = new String[]{"RESET", "CODING", "CODING_END", "FLUSHED"};
   private WeakReference<CharsetDecoder> cachedDecoder;

   protected CharsetEncoder(Charset var1, float var2, float var3, byte[] var4) {
      this.malformedInputAction = CodingErrorAction.REPORT;
      this.unmappableCharacterAction = CodingErrorAction.REPORT;
      this.state = 0;
      this.cachedDecoder = null;
      this.charset = var1;
      if (var2 <= 0.0F) {
         throw new IllegalArgumentException("Non-positive averageBytesPerChar");
      } else if (var3 <= 0.0F) {
         throw new IllegalArgumentException("Non-positive maxBytesPerChar");
      } else if (!Charset.atBugLevel("1.4") && var2 > var3) {
         throw new IllegalArgumentException("averageBytesPerChar exceeds maxBytesPerChar");
      } else {
         this.replacement = var4;
         this.averageBytesPerChar = var2;
         this.maxBytesPerChar = var3;
         this.replaceWith(var4);
      }
   }

   protected CharsetEncoder(Charset var1, float var2, float var3) {
      this(var1, var2, var3, new byte[]{63});
   }

   public final Charset charset() {
      return this.charset;
   }

   public final byte[] replacement() {
      return Arrays.copyOf(this.replacement, this.replacement.length);
   }

   public final CharsetEncoder replaceWith(byte[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null replacement");
      } else {
         int var2 = var1.length;
         if (var2 == 0) {
            throw new IllegalArgumentException("Empty replacement");
         } else if ((float)var2 > this.maxBytesPerChar) {
            throw new IllegalArgumentException("Replacement too long");
         } else if (!this.isLegalReplacement(var1)) {
            throw new IllegalArgumentException("Illegal replacement");
         } else {
            this.replacement = Arrays.copyOf(var1, var1.length);
            this.implReplaceWith(this.replacement);
            return this;
         }
      }
   }

   protected void implReplaceWith(byte[] var1) {
   }

   public boolean isLegalReplacement(byte[] var1) {
      WeakReference var2 = this.cachedDecoder;
      CharsetDecoder var3 = null;
      if (var2 != null && (var3 = (CharsetDecoder)var2.get()) != null) {
         var3.reset();
      } else {
         var3 = this.charset().newDecoder();
         var3.onMalformedInput(CodingErrorAction.REPORT);
         var3.onUnmappableCharacter(CodingErrorAction.REPORT);
         this.cachedDecoder = new WeakReference(var3);
      }

      ByteBuffer var4 = ByteBuffer.wrap(var1);
      CharBuffer var5 = CharBuffer.allocate((int)((float)var4.remaining() * var3.maxCharsPerByte()));
      CoderResult var6 = var3.decode(var4, var5, true);
      return !var6.isError();
   }

   public CodingErrorAction malformedInputAction() {
      return this.malformedInputAction;
   }

   public final CharsetEncoder onMalformedInput(CodingErrorAction var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null action");
      } else {
         this.malformedInputAction = var1;
         this.implOnMalformedInput(var1);
         return this;
      }
   }

   protected void implOnMalformedInput(CodingErrorAction var1) {
   }

   public CodingErrorAction unmappableCharacterAction() {
      return this.unmappableCharacterAction;
   }

   public final CharsetEncoder onUnmappableCharacter(CodingErrorAction var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null action");
      } else {
         this.unmappableCharacterAction = var1;
         this.implOnUnmappableCharacter(var1);
         return this;
      }
   }

   protected void implOnUnmappableCharacter(CodingErrorAction var1) {
   }

   public final float averageBytesPerChar() {
      return this.averageBytesPerChar;
   }

   public final float maxBytesPerChar() {
      return this.maxBytesPerChar;
   }

   public final CoderResult encode(CharBuffer var1, ByteBuffer var2, boolean var3) {
      int var4 = var3 ? 2 : 1;
      if (this.state != 0 && this.state != 1 && (!var3 || this.state != 2)) {
         this.throwIllegalStateException(this.state, var4);
      }

      this.state = var4;

      while(true) {
         CoderResult var5;
         try {
            var5 = this.encodeLoop(var1, var2);
         } catch (BufferUnderflowException var7) {
            throw new CoderMalfunctionError(var7);
         } catch (BufferOverflowException var8) {
            throw new CoderMalfunctionError(var8);
         }

         if (var5.isOverflow()) {
            return var5;
         }

         if (var5.isUnderflow()) {
            if (!var3 || !var1.hasRemaining()) {
               return var5;
            }

            var5 = CoderResult.malformedForLength(var1.remaining());
         }

         CodingErrorAction var6 = null;
         if (var5.isMalformed()) {
            var6 = this.malformedInputAction;
         } else if (var5.isUnmappable()) {
            var6 = this.unmappableCharacterAction;
         } else {
            assert false : var5.toString();
         }

         if (var6 == CodingErrorAction.REPORT) {
            return var5;
         }

         if (var6 == CodingErrorAction.REPLACE) {
            if (var2.remaining() < this.replacement.length) {
               return CoderResult.OVERFLOW;
            }

            var2.put(this.replacement);
         }

         if (var6 != CodingErrorAction.IGNORE && var6 != CodingErrorAction.REPLACE) {
            assert false;
         } else {
            var1.position(var1.position() + var5.length());
         }
      }
   }

   public final CoderResult flush(ByteBuffer var1) {
      if (this.state == 2) {
         CoderResult var2 = this.implFlush(var1);
         if (var2.isUnderflow()) {
            this.state = 3;
         }

         return var2;
      } else {
         if (this.state != 3) {
            this.throwIllegalStateException(this.state, 3);
         }

         return CoderResult.UNDERFLOW;
      }
   }

   protected CoderResult implFlush(ByteBuffer var1) {
      return CoderResult.UNDERFLOW;
   }

   public final CharsetEncoder reset() {
      this.implReset();
      this.state = 0;
      return this;
   }

   protected void implReset() {
   }

   protected abstract CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2);

   public final ByteBuffer encode(CharBuffer var1) throws CharacterCodingException {
      int var2 = (int)((float)var1.remaining() * this.averageBytesPerChar());
      ByteBuffer var3 = ByteBuffer.allocate(var2);
      if (var2 == 0 && var1.remaining() == 0) {
         return var3;
      } else {
         this.reset();

         while(true) {
            CoderResult var4 = var1.hasRemaining() ? this.encode(var1, var3, true) : CoderResult.UNDERFLOW;
            if (var4.isUnderflow()) {
               var4 = this.flush(var3);
            }

            if (var4.isUnderflow()) {
               var3.flip();
               return var3;
            }

            if (var4.isOverflow()) {
               var2 = 2 * var2 + 1;
               ByteBuffer var5 = ByteBuffer.allocate(var2);
               var3.flip();
               var5.put(var3);
               var3 = var5;
            } else {
               var4.throwException();
            }
         }
      }
   }

   private boolean canEncode(CharBuffer var1) {
      if (this.state == 3) {
         this.reset();
      } else if (this.state != 0) {
         this.throwIllegalStateException(this.state, 1);
      }

      CodingErrorAction var2 = this.malformedInputAction();
      CodingErrorAction var3 = this.unmappableCharacterAction();

      boolean var5;
      try {
         this.onMalformedInput(CodingErrorAction.REPORT);
         this.onUnmappableCharacter(CodingErrorAction.REPORT);
         this.encode(var1);
         return true;
      } catch (CharacterCodingException var9) {
         var5 = false;
      } finally {
         this.onMalformedInput(var2);
         this.onUnmappableCharacter(var3);
         this.reset();
      }

      return var5;
   }

   public boolean canEncode(char var1) {
      CharBuffer var2 = CharBuffer.allocate(1);
      var2.put(var1);
      var2.flip();
      return this.canEncode(var2);
   }

   public boolean canEncode(CharSequence var1) {
      CharBuffer var2;
      if (var1 instanceof CharBuffer) {
         var2 = ((CharBuffer)var1).duplicate();
      } else {
         var2 = CharBuffer.wrap((CharSequence)var1.toString());
      }

      return this.canEncode(var2);
   }

   private void throwIllegalStateException(int var1, int var2) {
      throw new IllegalStateException("Current state = " + stateNames[var1] + ", new state = " + stateNames[var2]);
   }
}
