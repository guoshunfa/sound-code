package java.lang;

import java.util.Arrays;
import sun.misc.FloatingDecimal;

abstract class AbstractStringBuilder implements Appendable, CharSequence {
   char[] value;
   int count;
   private static final int MAX_ARRAY_SIZE = 2147483639;

   AbstractStringBuilder() {
   }

   AbstractStringBuilder(int var1) {
      this.value = new char[var1];
   }

   public int length() {
      return this.count;
   }

   public int capacity() {
      return this.value.length;
   }

   public void ensureCapacity(int var1) {
      if (var1 > 0) {
         this.ensureCapacityInternal(var1);
      }

   }

   private void ensureCapacityInternal(int var1) {
      if (var1 - this.value.length > 0) {
         this.value = Arrays.copyOf(this.value, this.newCapacity(var1));
      }

   }

   private int newCapacity(int var1) {
      int var2 = (this.value.length << 1) + 2;
      if (var2 - var1 < 0) {
         var2 = var1;
      }

      return var2 > 0 && 2147483639 - var2 >= 0 ? var2 : this.hugeCapacity(var1);
   }

   private int hugeCapacity(int var1) {
      if (Integer.MAX_VALUE - var1 < 0) {
         throw new OutOfMemoryError();
      } else {
         return var1 > 2147483639 ? var1 : 2147483639;
      }
   }

   public void trimToSize() {
      if (this.count < this.value.length) {
         this.value = Arrays.copyOf(this.value, this.count);
      }

   }

   public void setLength(int var1) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else {
         this.ensureCapacityInternal(var1);
         if (this.count < var1) {
            Arrays.fill(this.value, this.count, var1, '\u0000');
         }

         this.count = var1;
      }
   }

   public char charAt(int var1) {
      if (var1 >= 0 && var1 < this.count) {
         return this.value[var1];
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public int codePointAt(int var1) {
      if (var1 >= 0 && var1 < this.count) {
         return Character.codePointAtImpl(this.value, var1, this.count);
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public int codePointBefore(int var1) {
      int var2 = var1 - 1;
      if (var2 >= 0 && var2 < this.count) {
         return Character.codePointBeforeImpl(this.value, var1, 0);
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public int codePointCount(int var1, int var2) {
      if (var1 >= 0 && var2 <= this.count && var1 <= var2) {
         return Character.codePointCountImpl(this.value, var1, var2 - var1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int offsetByCodePoints(int var1, int var2) {
      if (var1 >= 0 && var1 <= this.count) {
         return Character.offsetByCodePointsImpl(this.value, 0, this.count, var1, var2);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void getChars(int var1, int var2, char[] var3, int var4) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 >= 0 && var2 <= this.count) {
         if (var1 > var2) {
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
         } else {
            System.arraycopy(this.value, var1, var3, var4, var2 - var1);
         }
      } else {
         throw new StringIndexOutOfBoundsException(var2);
      }
   }

   public void setCharAt(int var1, char var2) {
      if (var1 >= 0 && var1 < this.count) {
         this.value[var1] = var2;
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public AbstractStringBuilder append(Object var1) {
      return this.append(String.valueOf(var1));
   }

   public AbstractStringBuilder append(String var1) {
      if (var1 == null) {
         return this.appendNull();
      } else {
         int var2 = var1.length();
         this.ensureCapacityInternal(this.count + var2);
         var1.getChars(0, var2, this.value, this.count);
         this.count += var2;
         return this;
      }
   }

   public AbstractStringBuilder append(StringBuffer var1) {
      if (var1 == null) {
         return this.appendNull();
      } else {
         int var2 = var1.length();
         this.ensureCapacityInternal(this.count + var2);
         var1.getChars(0, var2, this.value, this.count);
         this.count += var2;
         return this;
      }
   }

   AbstractStringBuilder append(AbstractStringBuilder var1) {
      if (var1 == null) {
         return this.appendNull();
      } else {
         int var2 = var1.length();
         this.ensureCapacityInternal(this.count + var2);
         var1.getChars(0, var2, this.value, this.count);
         this.count += var2;
         return this;
      }
   }

   public AbstractStringBuilder append(CharSequence var1) {
      if (var1 == null) {
         return this.appendNull();
      } else if (var1 instanceof String) {
         return this.append((String)var1);
      } else {
         return var1 instanceof AbstractStringBuilder ? this.append((AbstractStringBuilder)var1) : this.append((CharSequence)var1, 0, var1.length());
      }
   }

   private AbstractStringBuilder appendNull() {
      int var1 = this.count;
      this.ensureCapacityInternal(var1 + 4);
      char[] var2 = this.value;
      var2[var1++] = 'n';
      var2[var1++] = 'u';
      var2[var1++] = 'l';
      var2[var1++] = 'l';
      this.count = var1;
      return this;
   }

   public AbstractStringBuilder append(CharSequence var1, int var2, int var3) {
      if (var1 == null) {
         var1 = "null";
      }

      if (var2 >= 0 && var2 <= var3 && var3 <= ((CharSequence)var1).length()) {
         int var4 = var3 - var2;
         this.ensureCapacityInternal(this.count + var4);
         int var5 = var2;

         for(int var6 = this.count; var5 < var3; ++var6) {
            this.value[var6] = ((CharSequence)var1).charAt(var5);
            ++var5;
         }

         this.count += var4;
         return this;
      } else {
         throw new IndexOutOfBoundsException("start " + var2 + ", end " + var3 + ", s.length() " + ((CharSequence)var1).length());
      }
   }

   public AbstractStringBuilder append(char[] var1) {
      int var2 = var1.length;
      this.ensureCapacityInternal(this.count + var2);
      System.arraycopy(var1, 0, this.value, this.count, var2);
      this.count += var2;
      return this;
   }

   public AbstractStringBuilder append(char[] var1, int var2, int var3) {
      if (var3 > 0) {
         this.ensureCapacityInternal(this.count + var3);
      }

      System.arraycopy(var1, var2, this.value, this.count, var3);
      this.count += var3;
      return this;
   }

   public AbstractStringBuilder append(boolean var1) {
      if (var1) {
         this.ensureCapacityInternal(this.count + 4);
         this.value[this.count++] = 't';
         this.value[this.count++] = 'r';
         this.value[this.count++] = 'u';
         this.value[this.count++] = 'e';
      } else {
         this.ensureCapacityInternal(this.count + 5);
         this.value[this.count++] = 'f';
         this.value[this.count++] = 'a';
         this.value[this.count++] = 'l';
         this.value[this.count++] = 's';
         this.value[this.count++] = 'e';
      }

      return this;
   }

   public AbstractStringBuilder append(char var1) {
      this.ensureCapacityInternal(this.count + 1);
      this.value[this.count++] = var1;
      return this;
   }

   public AbstractStringBuilder append(int var1) {
      if (var1 == Integer.MIN_VALUE) {
         this.append("-2147483648");
         return this;
      } else {
         int var2 = var1 < 0 ? Integer.stringSize(-var1) + 1 : Integer.stringSize(var1);
         int var3 = this.count + var2;
         this.ensureCapacityInternal(var3);
         Integer.getChars(var1, var3, this.value);
         this.count = var3;
         return this;
      }
   }

   public AbstractStringBuilder append(long var1) {
      if (var1 == Long.MIN_VALUE) {
         this.append("-9223372036854775808");
         return this;
      } else {
         int var3 = var1 < 0L ? Long.stringSize(-var1) + 1 : Long.stringSize(var1);
         int var4 = this.count + var3;
         this.ensureCapacityInternal(var4);
         Long.getChars(var1, var4, this.value);
         this.count = var4;
         return this;
      }
   }

   public AbstractStringBuilder append(float var1) {
      FloatingDecimal.appendTo(var1, this);
      return this;
   }

   public AbstractStringBuilder append(double var1) {
      FloatingDecimal.appendTo(var1, this);
      return this;
   }

   public AbstractStringBuilder delete(int var1, int var2) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else {
         if (var2 > this.count) {
            var2 = this.count;
         }

         if (var1 > var2) {
            throw new StringIndexOutOfBoundsException();
         } else {
            int var3 = var2 - var1;
            if (var3 > 0) {
               System.arraycopy(this.value, var1 + var3, this.value, var1, this.count - var2);
               this.count -= var3;
            }

            return this;
         }
      }
   }

   public AbstractStringBuilder appendCodePoint(int var1) {
      int var2 = this.count;
      if (Character.isBmpCodePoint(var1)) {
         this.ensureCapacityInternal(var2 + 1);
         this.value[var2] = (char)var1;
         this.count = var2 + 1;
      } else {
         if (!Character.isValidCodePoint(var1)) {
            throw new IllegalArgumentException();
         }

         this.ensureCapacityInternal(var2 + 2);
         Character.toSurrogates(var1, this.value, var2);
         this.count = var2 + 2;
      }

      return this;
   }

   public AbstractStringBuilder deleteCharAt(int var1) {
      if (var1 >= 0 && var1 < this.count) {
         System.arraycopy(this.value, var1 + 1, this.value, var1, this.count - var1 - 1);
         --this.count;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public AbstractStringBuilder replace(int var1, int var2, String var3) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var1 > this.count) {
         throw new StringIndexOutOfBoundsException("start > length()");
      } else if (var1 > var2) {
         throw new StringIndexOutOfBoundsException("start > end");
      } else {
         if (var2 > this.count) {
            var2 = this.count;
         }

         int var4 = var3.length();
         int var5 = this.count + var4 - (var2 - var1);
         this.ensureCapacityInternal(var5);
         System.arraycopy(this.value, var2, this.value, var1 + var4, this.count - var2);
         var3.getChars(this.value, var1);
         this.count = var5;
         return this;
      }
   }

   public String substring(int var1) {
      return this.substring(var1, this.count);
   }

   public CharSequence subSequence(int var1, int var2) {
      return this.substring(var1, var2);
   }

   public String substring(int var1, int var2) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 > this.count) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var1 > var2) {
         throw new StringIndexOutOfBoundsException(var2 - var1);
      } else {
         return new String(this.value, var1, var2 - var1);
      }
   }

   public AbstractStringBuilder insert(int var1, char[] var2, int var3, int var4) {
      if (var1 >= 0 && var1 <= this.length()) {
         if (var3 >= 0 && var4 >= 0 && var3 <= var2.length - var4) {
            this.ensureCapacityInternal(this.count + var4);
            System.arraycopy(this.value, var1, this.value, var1 + var4, this.count - var1);
            System.arraycopy(var2, var3, this.value, var1, var4);
            this.count += var4;
            return this;
         } else {
            throw new StringIndexOutOfBoundsException("offset " + var3 + ", len " + var4 + ", str.length " + var2.length);
         }
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public AbstractStringBuilder insert(int var1, Object var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public AbstractStringBuilder insert(int var1, String var2) {
      if (var1 >= 0 && var1 <= this.length()) {
         if (var2 == null) {
            var2 = "null";
         }

         int var3 = var2.length();
         this.ensureCapacityInternal(this.count + var3);
         System.arraycopy(this.value, var1, this.value, var1 + var3, this.count - var1);
         var2.getChars(this.value, var1);
         this.count += var3;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public AbstractStringBuilder insert(int var1, char[] var2) {
      if (var1 >= 0 && var1 <= this.length()) {
         int var3 = var2.length;
         this.ensureCapacityInternal(this.count + var3);
         System.arraycopy(this.value, var1, this.value, var1 + var3, this.count - var1);
         System.arraycopy(var2, 0, this.value, var1, var3);
         this.count += var3;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public AbstractStringBuilder insert(int var1, CharSequence var2) {
      if (var2 == null) {
         var2 = "null";
      }

      return var2 instanceof String ? this.insert(var1, (String)var2) : this.insert(var1, (CharSequence)var2, 0, ((CharSequence)var2).length());
   }

   public AbstractStringBuilder insert(int var1, CharSequence var2, int var3, int var4) {
      if (var2 == null) {
         var2 = "null";
      }

      if (var1 >= 0 && var1 <= this.length()) {
         if (var3 >= 0 && var4 >= 0 && var3 <= var4 && var4 <= ((CharSequence)var2).length()) {
            int var5 = var4 - var3;
            this.ensureCapacityInternal(this.count + var5);
            System.arraycopy(this.value, var1, this.value, var1 + var5, this.count - var1);

            for(int var6 = var3; var6 < var4; ++var6) {
               this.value[var1++] = ((CharSequence)var2).charAt(var6);
            }

            this.count += var5;
            return this;
         } else {
            throw new IndexOutOfBoundsException("start " + var3 + ", end " + var4 + ", s.length() " + ((CharSequence)var2).length());
         }
      } else {
         throw new IndexOutOfBoundsException("dstOffset " + var1);
      }
   }

   public AbstractStringBuilder insert(int var1, boolean var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public AbstractStringBuilder insert(int var1, char var2) {
      this.ensureCapacityInternal(this.count + 1);
      System.arraycopy(this.value, var1, this.value, var1 + 1, this.count - var1);
      this.value[var1] = var2;
      ++this.count;
      return this;
   }

   public AbstractStringBuilder insert(int var1, int var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public AbstractStringBuilder insert(int var1, long var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public AbstractStringBuilder insert(int var1, float var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public AbstractStringBuilder insert(int var1, double var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public int indexOf(String var1) {
      return this.indexOf(var1, 0);
   }

   public int indexOf(String var1, int var2) {
      return String.indexOf(this.value, 0, this.count, var1, var2);
   }

   public int lastIndexOf(String var1) {
      return this.lastIndexOf(var1, this.count);
   }

   public int lastIndexOf(String var1, int var2) {
      return String.lastIndexOf(this.value, 0, this.count, var1, var2);
   }

   public AbstractStringBuilder reverse() {
      boolean var1 = false;
      int var2 = this.count - 1;

      for(int var3 = var2 - 1 >> 1; var3 >= 0; --var3) {
         int var4 = var2 - var3;
         char var5 = this.value[var3];
         char var6 = this.value[var4];
         this.value[var3] = var6;
         this.value[var4] = var5;
         if (Character.isSurrogate(var5) || Character.isSurrogate(var6)) {
            var1 = true;
         }
      }

      if (var1) {
         this.reverseAllValidSurrogatePairs();
      }

      return this;
   }

   private void reverseAllValidSurrogatePairs() {
      for(int var1 = 0; var1 < this.count - 1; ++var1) {
         char var2 = this.value[var1];
         if (Character.isLowSurrogate(var2)) {
            char var3 = this.value[var1 + 1];
            if (Character.isHighSurrogate(var3)) {
               this.value[var1++] = var3;
               this.value[var1] = var2;
            }
         }
      }

   }

   public abstract String toString();

   final char[] getValue() {
      return this.value;
   }
}
