package java.nio.charset;

import java.lang.ref.WeakReference;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.Map;

public class CoderResult {
   private static final int CR_UNDERFLOW = 0;
   private static final int CR_OVERFLOW = 1;
   private static final int CR_ERROR_MIN = 2;
   private static final int CR_MALFORMED = 2;
   private static final int CR_UNMAPPABLE = 3;
   private static final String[] names = new String[]{"UNDERFLOW", "OVERFLOW", "MALFORMED", "UNMAPPABLE"};
   private final int type;
   private final int length;
   public static final CoderResult UNDERFLOW = new CoderResult(0, 0);
   public static final CoderResult OVERFLOW = new CoderResult(1, 0);
   private static CoderResult.Cache malformedCache = new CoderResult.Cache() {
      public CoderResult create(int var1) {
         return new CoderResult(2, var1);
      }
   };
   private static CoderResult.Cache unmappableCache = new CoderResult.Cache() {
      public CoderResult create(int var1) {
         return new CoderResult(3, var1);
      }
   };

   private CoderResult(int var1, int var2) {
      this.type = var1;
      this.length = var2;
   }

   public String toString() {
      String var1 = names[this.type];
      return this.isError() ? var1 + "[" + this.length + "]" : var1;
   }

   public boolean isUnderflow() {
      return this.type == 0;
   }

   public boolean isOverflow() {
      return this.type == 1;
   }

   public boolean isError() {
      return this.type >= 2;
   }

   public boolean isMalformed() {
      return this.type == 2;
   }

   public boolean isUnmappable() {
      return this.type == 3;
   }

   public int length() {
      if (!this.isError()) {
         throw new UnsupportedOperationException();
      } else {
         return this.length;
      }
   }

   public static CoderResult malformedForLength(int var0) {
      return malformedCache.get(var0);
   }

   public static CoderResult unmappableForLength(int var0) {
      return unmappableCache.get(var0);
   }

   public void throwException() throws CharacterCodingException {
      switch(this.type) {
      case 0:
         throw new BufferUnderflowException();
      case 1:
         throw new BufferOverflowException();
      case 2:
         throw new MalformedInputException(this.length);
      case 3:
         throw new UnmappableCharacterException(this.length);
      default:
         assert false;

      }
   }

   // $FF: synthetic method
   CoderResult(int var1, int var2, Object var3) {
      this(var1, var2);
   }

   private abstract static class Cache {
      private Map<Integer, WeakReference<CoderResult>> cache;

      private Cache() {
         this.cache = null;
      }

      protected abstract CoderResult create(int var1);

      private synchronized CoderResult get(int var1) {
         if (var1 <= 0) {
            throw new IllegalArgumentException("Non-positive length");
         } else {
            Integer var2 = new Integer(var1);
            CoderResult var4 = null;
            if (this.cache == null) {
               this.cache = new HashMap();
            } else {
               WeakReference var3;
               if ((var3 = (WeakReference)this.cache.get(var2)) != null) {
                  var4 = (CoderResult)var3.get();
               }
            }

            if (var4 == null) {
               var4 = this.create(var1);
               this.cache.put(var2, new WeakReference(var4));
            }

            return var4;
         }
      }

      // $FF: synthetic method
      Cache(Object var1) {
         this();
      }
   }
}
