package java.nio.charset;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public abstract class CharsetDecoder {
   private final Charset charset;
   private final float averageCharsPerByte;
   private final float maxCharsPerByte;
   private String replacement;
   private CodingErrorAction malformedInputAction;
   private CodingErrorAction unmappableCharacterAction;
   private static final int ST_RESET = 0;
   private static final int ST_CODING = 1;
   private static final int ST_END = 2;
   private static final int ST_FLUSHED = 3;
   private int state;
   private static String[] stateNames = new String[]{"RESET", "CODING", "CODING_END", "FLUSHED"};

   private CharsetDecoder(Charset var1, float var2, float var3, String var4) {
      this.malformedInputAction = CodingErrorAction.REPORT;
      this.unmappableCharacterAction = CodingErrorAction.REPORT;
      this.state = 0;
      this.charset = var1;
      if (var2 <= 0.0F) {
         throw new IllegalArgumentException("Non-positive averageCharsPerByte");
      } else if (var3 <= 0.0F) {
         throw new IllegalArgumentException("Non-positive maxCharsPerByte");
      } else if (!Charset.atBugLevel("1.4") && var2 > var3) {
         throw new IllegalArgumentException("averageCharsPerByte exceeds maxCharsPerByte");
      } else {
         this.replacement = var4;
         this.averageCharsPerByte = var2;
         this.maxCharsPerByte = var3;
         this.replaceWith(var4);
      }
   }

   protected CharsetDecoder(Charset var1, float var2, float var3) {
      this(var1, var2, var3, "�");
   }

   public final Charset charset() {
      return this.charset;
   }

   public final String replacement() {
      return this.replacement;
   }

   public final CharsetDecoder replaceWith(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null replacement");
      } else {
         int var2 = var1.length();
         if (var2 == 0) {
            throw new IllegalArgumentException("Empty replacement");
         } else if ((float)var2 > this.maxCharsPerByte) {
            throw new IllegalArgumentException("Replacement too long");
         } else {
            this.replacement = var1;
            this.implReplaceWith(this.replacement);
            return this;
         }
      }
   }

   protected void implReplaceWith(String var1) {
   }

   public CodingErrorAction malformedInputAction() {
      return this.malformedInputAction;
   }

   public final CharsetDecoder onMalformedInput(CodingErrorAction var1) {
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

   public final CharsetDecoder onUnmappableCharacter(CodingErrorAction var1) {
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

   public final float averageCharsPerByte() {
      return this.averageCharsPerByte;
   }

   public final float maxCharsPerByte() {
      return this.maxCharsPerByte;
   }

   public final CoderResult decode(ByteBuffer var1, CharBuffer var2, boolean var3) {
      int var4 = var3 ? 2 : 1;
      if (this.state != 0 && this.state != 1 && (!var3 || this.state != 2)) {
         this.throwIllegalStateException(this.state, var4);
      }

      this.state = var4;

      while(true) {
         CoderResult var5;
         try {
            var5 = this.decodeLoop(var1, var2);
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
            if (var2.remaining() < this.replacement.length()) {
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

   public final CoderResult flush(CharBuffer var1) {
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

   protected CoderResult implFlush(CharBuffer var1) {
      return CoderResult.UNDERFLOW;
   }

   public final CharsetDecoder reset() {
      this.implReset();
      this.state = 0;
      return this;
   }

   protected void implReset() {
   }

   protected abstract CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2);

   public final CharBuffer decode(ByteBuffer var1) throws CharacterCodingException {
      int var2 = (int)((float)var1.remaining() * this.averageCharsPerByte());
      CharBuffer var3 = CharBuffer.allocate(var2);
      if (var2 == 0 && var1.remaining() == 0) {
         return var3;
      } else {
         this.reset();

         while(true) {
            CoderResult var4 = var1.hasRemaining() ? this.decode(var1, var3, true) : CoderResult.UNDERFLOW;
            if (var4.isUnderflow()) {
               var4 = this.flush(var3);
            }

            if (var4.isUnderflow()) {
               var3.flip();
               return var3;
            }

            if (var4.isOverflow()) {
               var2 = 2 * var2 + 1;
               CharBuffer var5 = CharBuffer.allocate(var2);
               var3.flip();
               var5.put(var3);
               var3 = var5;
            } else {
               var4.throwException();
            }
         }
      }
   }

   public boolean isAutoDetecting() {
      return false;
   }

   public boolean isCharsetDetected() {
      throw new UnsupportedOperationException();
   }

   public Charset detectedCharset() {
      throw new UnsupportedOperationException();
   }

   private void throwIllegalStateException(int var1, int var2) {
      throw new IllegalStateException("Current state = " + stateNames[var1] + ", new state = " + stateNames[var2]);
   }
}
