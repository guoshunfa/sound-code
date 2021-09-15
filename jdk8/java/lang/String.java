package java.lang;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class String implements Serializable, Comparable<String>, CharSequence {
   private final char[] value;
   private int hash;
   private static final long serialVersionUID = -6849794470754667710L;
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
   public static final Comparator<String> CASE_INSENSITIVE_ORDER = new String.CaseInsensitiveComparator();

   public String() {
      this.value = "".value;
   }

   public String(String var1) {
      this.value = var1.value;
      this.hash = var1.hash;
   }

   public String(char[] var1) {
      this.value = Arrays.copyOf(var1, var1.length);
   }

   public String(char[] var1, int var2, int var3) {
      if (var2 < 0) {
         throw new StringIndexOutOfBoundsException(var2);
      } else {
         if (var3 <= 0) {
            if (var3 < 0) {
               throw new StringIndexOutOfBoundsException(var3);
            }

            if (var2 <= var1.length) {
               this.value = "".value;
               return;
            }
         }

         if (var2 > var1.length - var3) {
            throw new StringIndexOutOfBoundsException(var2 + var3);
         } else {
            this.value = Arrays.copyOfRange(var1, var2, var2 + var3);
         }
      }
   }

   public String(int[] var1, int var2, int var3) {
      if (var2 < 0) {
         throw new StringIndexOutOfBoundsException(var2);
      } else {
         if (var3 <= 0) {
            if (var3 < 0) {
               throw new StringIndexOutOfBoundsException(var3);
            }

            if (var2 <= var1.length) {
               this.value = "".value;
               return;
            }
         }

         if (var2 > var1.length - var3) {
            throw new StringIndexOutOfBoundsException(var2 + var3);
         } else {
            int var4 = var2 + var3;
            int var5 = var3;

            int var7;
            for(int var6 = var2; var6 < var4; ++var6) {
               var7 = var1[var6];
               if (!Character.isBmpCodePoint(var7)) {
                  if (!Character.isValidCodePoint(var7)) {
                     throw new IllegalArgumentException(Integer.toString(var7));
                  }

                  ++var5;
               }
            }

            char[] var10 = new char[var5];
            var7 = var2;

            for(int var8 = 0; var7 < var4; ++var8) {
               int var9 = var1[var7];
               if (Character.isBmpCodePoint(var9)) {
                  var10[var8] = (char)var9;
               } else {
                  Character.toSurrogates(var9, var10, var8++);
               }

               ++var7;
            }

            this.value = var10;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public String(byte[] var1, int var2, int var3, int var4) {
      checkBounds(var1, var3, var4);
      char[] var5 = new char[var4];
      int var6;
      if (var2 == 0) {
         for(var6 = var4; var6-- > 0; var5[var6] = (char)(var1[var6 + var3] & 255)) {
         }
      } else {
         var2 <<= 8;

         for(var6 = var4; var6-- > 0; var5[var6] = (char)(var2 | var1[var6 + var3] & 255)) {
         }
      }

      this.value = var5;
   }

   /** @deprecated */
   @Deprecated
   public String(byte[] var1, int var2) {
      this(var1, var2, 0, var1.length);
   }

   private static void checkBounds(byte[] var0, int var1, int var2) {
      if (var2 < 0) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var1 > var0.length - var2) {
         throw new StringIndexOutOfBoundsException(var1 + var2);
      }
   }

   public String(byte[] var1, int var2, int var3, String var4) throws UnsupportedEncodingException {
      if (var4 == null) {
         throw new NullPointerException("charsetName");
      } else {
         checkBounds(var1, var2, var3);
         this.value = StringCoding.decode(var4, var1, var2, var3);
      }
   }

   public String(byte[] var1, int var2, int var3, Charset var4) {
      if (var4 == null) {
         throw new NullPointerException("charset");
      } else {
         checkBounds(var1, var2, var3);
         this.value = StringCoding.decode(var4, var1, var2, var3);
      }
   }

   public String(byte[] var1, String var2) throws UnsupportedEncodingException {
      this(var1, 0, var1.length, (String)var2);
   }

   public String(byte[] var1, Charset var2) {
      this(var1, 0, var1.length, (Charset)var2);
   }

   public String(byte[] var1, int var2, int var3) {
      checkBounds(var1, var2, var3);
      this.value = StringCoding.decode(var1, var2, var3);
   }

   public String(byte[] var1) {
      this((byte[])var1, 0, var1.length);
   }

   public String(StringBuffer var1) {
      synchronized(var1) {
         this.value = Arrays.copyOf(var1.getValue(), var1.length());
      }
   }

   public String(StringBuilder var1) {
      this.value = Arrays.copyOf(var1.getValue(), var1.length());
   }

   String(char[] var1, boolean var2) {
      this.value = var1;
   }

   public int length() {
      return this.value.length;
   }

   public boolean isEmpty() {
      return this.value.length == 0;
   }

   public char charAt(int var1) {
      if (var1 >= 0 && var1 < this.value.length) {
         return this.value[var1];
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public int codePointAt(int var1) {
      if (var1 >= 0 && var1 < this.value.length) {
         return Character.codePointAtImpl(this.value, var1, this.value.length);
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public int codePointBefore(int var1) {
      int var2 = var1 - 1;
      if (var2 >= 0 && var2 < this.value.length) {
         return Character.codePointBeforeImpl(this.value, var1, 0);
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public int codePointCount(int var1, int var2) {
      if (var1 >= 0 && var2 <= this.value.length && var1 <= var2) {
         return Character.codePointCountImpl(this.value, var1, var2 - var1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int offsetByCodePoints(int var1, int var2) {
      if (var1 >= 0 && var1 <= this.value.length) {
         return Character.offsetByCodePointsImpl(this.value, 0, this.value.length, var1, var2);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   void getChars(char[] var1, int var2) {
      System.arraycopy(this.value, 0, var1, var2, this.value.length);
   }

   public void getChars(int var1, int var2, char[] var3, int var4) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 > this.value.length) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var1 > var2) {
         throw new StringIndexOutOfBoundsException(var2 - var1);
      } else {
         System.arraycopy(this.value, var1, var3, var4, var2 - var1);
      }
   }

   /** @deprecated */
   @Deprecated
   public void getBytes(int var1, int var2, byte[] var3, int var4) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 > this.value.length) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var1 > var2) {
         throw new StringIndexOutOfBoundsException(var2 - var1);
      } else {
         Objects.requireNonNull(var3);
         int var5 = var4;
         int var6 = var2;
         int var7 = var1;

         for(char[] var8 = this.value; var7 < var6; var3[var5++] = (byte)var8[var7++]) {
         }

      }
   }

   public byte[] getBytes(String var1) throws UnsupportedEncodingException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return StringCoding.encode((String)var1, this.value, 0, this.value.length);
      }
   }

   public byte[] getBytes(Charset var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return StringCoding.encode((Charset)var1, this.value, 0, this.value.length);
      }
   }

   public byte[] getBytes() {
      return StringCoding.encode(this.value, 0, this.value.length);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof String) {
            String var2 = (String)var1;
            int var3 = this.value.length;
            if (var3 == var2.value.length) {
               char[] var4 = this.value;
               char[] var5 = var2.value;

               for(int var6 = 0; var3-- != 0; ++var6) {
                  if (var4[var6] != var5[var6]) {
                     return false;
                  }
               }

               return true;
            }
         }

         return false;
      }
   }

   public boolean contentEquals(StringBuffer var1) {
      return this.contentEquals((CharSequence)var1);
   }

   private boolean nonSyncContentEquals(AbstractStringBuilder var1) {
      char[] var2 = this.value;
      char[] var3 = var1.getValue();
      int var4 = var2.length;
      if (var4 != var1.length()) {
         return false;
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            if (var2[var5] != var3[var5]) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean contentEquals(CharSequence var1) {
      if (var1 instanceof AbstractStringBuilder) {
         if (var1 instanceof StringBuffer) {
            synchronized(var1) {
               return this.nonSyncContentEquals((AbstractStringBuilder)var1);
            }
         } else {
            return this.nonSyncContentEquals((AbstractStringBuilder)var1);
         }
      } else if (var1 instanceof String) {
         return this.equals(var1);
      } else {
         char[] var2 = this.value;
         int var3 = var2.length;
         if (var3 != var1.length()) {
            return false;
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               if (var2[var4] != var1.charAt(var4)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public boolean equalsIgnoreCase(String var1) {
      return this == var1 ? true : var1 != null && var1.value.length == this.value.length && this.regionMatches(true, 0, var1, 0, this.value.length);
   }

   public int compareTo(String var1) {
      int var2 = this.value.length;
      int var3 = var1.value.length;
      int var4 = Math.min(var2, var3);
      char[] var5 = this.value;
      char[] var6 = var1.value;

      for(int var7 = 0; var7 < var4; ++var7) {
         char var8 = var5[var7];
         char var9 = var6[var7];
         if (var8 != var9) {
            return var8 - var9;
         }
      }

      return var2 - var3;
   }

   public int compareToIgnoreCase(String var1) {
      return CASE_INSENSITIVE_ORDER.compare(this, var1);
   }

   public boolean regionMatches(int var1, String var2, int var3, int var4) {
      char[] var5 = this.value;
      int var6 = var1;
      char[] var7 = var2.value;
      int var8 = var3;
      if (var3 >= 0 && var1 >= 0 && (long)var1 <= (long)this.value.length - (long)var4 && (long)var3 <= (long)var2.value.length - (long)var4) {
         do {
            if (var4-- <= 0) {
               return true;
            }
         } while(var5[var6++] == var7[var8++]);

         return false;
      } else {
         return false;
      }
   }

   public boolean regionMatches(boolean var1, int var2, String var3, int var4, int var5) {
      char[] var6 = this.value;
      int var7 = var2;
      char[] var8 = var3.value;
      int var9 = var4;
      if (var4 >= 0 && var2 >= 0 && (long)var2 <= (long)this.value.length - (long)var5 && (long)var4 <= (long)var3.value.length - (long)var5) {
         char var12;
         char var13;
         do {
            char var10;
            char var11;
            do {
               if (var5-- <= 0) {
                  return true;
               }

               var10 = var6[var7++];
               var11 = var8[var9++];
            } while(var10 == var11);

            if (!var1) {
               break;
            }

            var12 = Character.toUpperCase(var10);
            var13 = Character.toUpperCase(var11);
         } while(var12 == var13 || Character.toLowerCase(var12) == Character.toLowerCase(var13));

         return false;
      } else {
         return false;
      }
   }

   public boolean startsWith(String var1, int var2) {
      char[] var3 = this.value;
      int var4 = var2;
      char[] var5 = var1.value;
      int var6 = 0;
      int var7 = var1.value.length;
      if (var2 >= 0 && var2 <= this.value.length - var7) {
         do {
            --var7;
            if (var7 < 0) {
               return true;
            }
         } while(var3[var4++] == var5[var6++]);

         return false;
      } else {
         return false;
      }
   }

   public boolean startsWith(String var1) {
      return this.startsWith(var1, 0);
   }

   public boolean endsWith(String var1) {
      return this.startsWith(var1, this.value.length - var1.value.length);
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 == 0 && this.value.length > 0) {
         char[] var2 = this.value;

         for(int var3 = 0; var3 < this.value.length; ++var3) {
            var1 = 31 * var1 + var2[var3];
         }

         this.hash = var1;
      }

      return var1;
   }

   public int indexOf(int var1) {
      return this.indexOf(var1, 0);
   }

   public int indexOf(int var1, int var2) {
      int var3 = this.value.length;
      if (var2 < 0) {
         var2 = 0;
      } else if (var2 >= var3) {
         return -1;
      }

      if (var1 < 65536) {
         char[] var4 = this.value;

         for(int var5 = var2; var5 < var3; ++var5) {
            if (var4[var5] == var1) {
               return var5;
            }
         }

         return -1;
      } else {
         return this.indexOfSupplementary(var1, var2);
      }
   }

   private int indexOfSupplementary(int var1, int var2) {
      if (Character.isValidCodePoint(var1)) {
         char[] var3 = this.value;
         char var4 = Character.highSurrogate(var1);
         char var5 = Character.lowSurrogate(var1);
         int var6 = var3.length - 1;

         for(int var7 = var2; var7 < var6; ++var7) {
            if (var3[var7] == var4 && var3[var7 + 1] == var5) {
               return var7;
            }
         }
      }

      return -1;
   }

   public int lastIndexOf(int var1) {
      return this.lastIndexOf(var1, this.value.length - 1);
   }

   public int lastIndexOf(int var1, int var2) {
      if (var1 < 65536) {
         char[] var3 = this.value;

         for(int var4 = Math.min(var2, var3.length - 1); var4 >= 0; --var4) {
            if (var3[var4] == var1) {
               return var4;
            }
         }

         return -1;
      } else {
         return this.lastIndexOfSupplementary(var1, var2);
      }
   }

   private int lastIndexOfSupplementary(int var1, int var2) {
      if (Character.isValidCodePoint(var1)) {
         char[] var3 = this.value;
         char var4 = Character.highSurrogate(var1);
         char var5 = Character.lowSurrogate(var1);

         for(int var6 = Math.min(var2, var3.length - 2); var6 >= 0; --var6) {
            if (var3[var6] == var4 && var3[var6 + 1] == var5) {
               return var6;
            }
         }
      }

      return -1;
   }

   public int indexOf(String var1) {
      return this.indexOf(var1, 0);
   }

   public int indexOf(String var1, int var2) {
      return indexOf(this.value, 0, this.value.length, var1.value, 0, var1.value.length, var2);
   }

   static int indexOf(char[] var0, int var1, int var2, String var3, int var4) {
      return indexOf(var0, var1, var2, var3.value, 0, var3.value.length, var4);
   }

   static int indexOf(char[] var0, int var1, int var2, char[] var3, int var4, int var5, int var6) {
      if (var6 >= var2) {
         return var5 == 0 ? var2 : -1;
      } else {
         if (var6 < 0) {
            var6 = 0;
         }

         if (var5 == 0) {
            return var6;
         } else {
            char var7 = var3[var4];
            int var8 = var1 + (var2 - var5);

            for(int var9 = var1 + var6; var9 <= var8; ++var9) {
               if (var0[var9] != var7) {
                  do {
                     ++var9;
                  } while(var9 <= var8 && var0[var9] != var7);
               }

               if (var9 <= var8) {
                  int var10 = var9 + 1;
                  int var11 = var10 + var5 - 1;

                  for(int var12 = var4 + 1; var10 < var11 && var0[var10] == var3[var12]; ++var12) {
                     ++var10;
                  }

                  if (var10 == var11) {
                     return var9 - var1;
                  }
               }
            }

            return -1;
         }
      }
   }

   public int lastIndexOf(String var1) {
      return this.lastIndexOf(var1, this.value.length);
   }

   public int lastIndexOf(String var1, int var2) {
      return lastIndexOf(this.value, 0, this.value.length, var1.value, 0, var1.value.length, var2);
   }

   static int lastIndexOf(char[] var0, int var1, int var2, String var3, int var4) {
      return lastIndexOf(var0, var1, var2, var3.value, 0, var3.value.length, var4);
   }

   static int lastIndexOf(char[] var0, int var1, int var2, char[] var3, int var4, int var5, int var6) {
      int var7 = var2 - var5;
      if (var6 < 0) {
         return -1;
      } else {
         if (var6 > var7) {
            var6 = var7;
         }

         if (var5 == 0) {
            return var6;
         } else {
            int var8 = var4 + var5 - 1;
            char var9 = var3[var8];
            int var10 = var1 + var5 - 1;
            int var11 = var10 + var6;

            while(true) {
               while(var11 < var10 || var0[var11] == var9) {
                  if (var11 < var10) {
                     return -1;
                  }

                  int var12 = var11 - 1;
                  int var13 = var12 - (var5 - 1);
                  int var14 = var8 - 1;

                  do {
                     if (var12 <= var13) {
                        return var13 - var1 + 1;
                     }
                  } while(var0[var12--] == var3[var14--]);

                  --var11;
               }

               --var11;
            }
         }
      }
   }

   public String substring(int var1) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else {
         int var2 = this.value.length - var1;
         if (var2 < 0) {
            throw new StringIndexOutOfBoundsException(var2);
         } else {
            return var1 == 0 ? this : new String(this.value, var1, var2);
         }
      }
   }

   public String substring(int var1, int var2) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 > this.value.length) {
         throw new StringIndexOutOfBoundsException(var2);
      } else {
         int var3 = var2 - var1;
         if (var3 < 0) {
            throw new StringIndexOutOfBoundsException(var3);
         } else {
            return var1 == 0 && var2 == this.value.length ? this : new String(this.value, var1, var3);
         }
      }
   }

   public CharSequence subSequence(int var1, int var2) {
      return this.substring(var1, var2);
   }

   public String concat(String var1) {
      int var2 = var1.length();
      if (var2 == 0) {
         return this;
      } else {
         int var3 = this.value.length;
         char[] var4 = Arrays.copyOf(this.value, var3 + var2);
         var1.getChars(var4, var3);
         return new String(var4, true);
      }
   }

   public String replace(char var1, char var2) {
      if (var1 != var2) {
         int var3 = this.value.length;
         int var4 = -1;
         char[] var5 = this.value;

         do {
            ++var4;
         } while(var4 < var3 && var5[var4] != var1);

         if (var4 < var3) {
            char[] var6 = new char[var3];

            for(int var7 = 0; var7 < var4; ++var7) {
               var6[var7] = var5[var7];
            }

            while(var4 < var3) {
               char var8 = var5[var4];
               var6[var4] = var8 == var1 ? var2 : var8;
               ++var4;
            }

            return new String(var6, true);
         }
      }

      return this;
   }

   public boolean matches(String var1) {
      return Pattern.matches(var1, this);
   }

   public boolean contains(CharSequence var1) {
      return this.indexOf(var1.toString()) > -1;
   }

   public String replaceFirst(String var1, String var2) {
      return Pattern.compile(var1).matcher(this).replaceFirst(var2);
   }

   public String replaceAll(String var1, String var2) {
      return Pattern.compile(var1).matcher(this).replaceAll(var2);
   }

   public String replace(CharSequence var1, CharSequence var2) {
      return Pattern.compile(var1.toString(), 16).matcher(this).replaceAll(Matcher.quoteReplacement(var2.toString()));
   }

   public String[] split(String var1, int var2) {
      char var3 = 0;
      if ((var1.value.length != 1 || ".$|()[{^?*+\\".indexOf(var3 = var1.charAt(0)) != -1) && (var1.length() != 2 || var1.charAt(0) != '\\' || ((var3 = var1.charAt(1)) - 48 | 57 - var3) >= 0 || (var3 - 97 | 122 - var3) >= 0 || (var3 - 65 | 90 - var3) >= 0) || var3 >= '\ud800' && var3 <= '\udfff') {
         return Pattern.compile(var1).split(this, var2);
      } else {
         int var4 = 0;
         boolean var5 = false;
         boolean var6 = var2 > 0;

         ArrayList var7;
         int var10;
         for(var7 = new ArrayList(); (var10 = this.indexOf(var3, var4)) != -1; var4 = var10 + 1) {
            if (var6 && var7.size() >= var2 - 1) {
               var7.add(this.substring(var4, this.value.length));
               var4 = this.value.length;
               break;
            }

            var7.add(this.substring(var4, var10));
         }

         if (var4 == 0) {
            return new String[]{this};
         } else {
            if (!var6 || var7.size() < var2) {
               var7.add(this.substring(var4, this.value.length));
            }

            int var8 = var7.size();
            if (var2 == 0) {
               while(var8 > 0 && ((String)var7.get(var8 - 1)).length() == 0) {
                  --var8;
               }
            }

            String[] var9 = new String[var8];
            return (String[])var7.subList(0, var8).toArray(var9);
         }
      }
   }

   public String[] split(String var1) {
      return this.split(var1, 0);
   }

   public static String join(CharSequence var0, CharSequence... var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      StringJoiner var2 = new StringJoiner(var0);
      CharSequence[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         CharSequence var6 = var3[var5];
         var2.add(var6);
      }

      return var2.toString();
   }

   public static String join(CharSequence var0, Iterable<? extends CharSequence> var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      StringJoiner var2 = new StringJoiner(var0);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         CharSequence var4 = (CharSequence)var3.next();
         var2.add(var4);
      }

      return var2.toString();
   }

   public String toLowerCase(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var3 = this.value.length;
         int var2 = 0;

         int var5;
         while(true) {
            if (var2 >= var3) {
               return this;
            }

            char var4 = this.value[var2];
            if (var4 >= '\ud800' && var4 <= '\udbff') {
               var5 = this.codePointAt(var2);
               if (var5 != Character.toLowerCase(var5)) {
                  break;
               }

               var2 += Character.charCount(var5);
            } else {
               if (var4 != Character.toLowerCase(var4)) {
                  break;
               }

               ++var2;
            }
         }

         char[] var15 = new char[var3];
         var5 = 0;
         System.arraycopy(this.value, 0, var15, 0, var2);
         String var6 = var1.getLanguage();
         boolean var7 = var6 == "tr" || var6 == "az" || var6 == "lt";

         int var11;
         for(int var12 = var2; var12 < var3; var12 += var11) {
            int var10 = this.value[var12];
            if ((char)var10 >= '\ud800' && (char)var10 <= '\udbff') {
               var10 = this.codePointAt(var12);
               var11 = Character.charCount(var10);
            } else {
               var11 = 1;
            }

            int var9;
            if (!var7 && var10 != 931 && var10 != 304) {
               var9 = Character.toLowerCase(var10);
            } else {
               var9 = ConditionalSpecialCasing.toLowerCaseEx(this, var12, var1);
            }

            if (var9 != -1 && var9 < 65536) {
               var15[var12 + var5] = (char)var9;
            } else {
               char[] var8;
               if (var9 == -1) {
                  var8 = ConditionalSpecialCasing.toLowerCaseCharArray(this, var12, var1);
               } else {
                  if (var11 == 2) {
                     var5 += Character.toChars(var9, var15, var12 + var5) - var11;
                     continue;
                  }

                  var8 = Character.toChars(var9);
               }

               int var13 = var8.length;
               if (var13 > var11) {
                  char[] var14 = new char[var15.length + var13 - var11];
                  System.arraycopy(var15, 0, var14, 0, var12 + var5);
                  var15 = var14;
               }

               for(int var16 = 0; var16 < var13; ++var16) {
                  var15[var12 + var5 + var16] = var8[var16];
               }

               var5 += var13 - var11;
            }
         }

         return new String(var15, 0, var3 + var5);
      }
   }

   public String toLowerCase() {
      return this.toLowerCase(Locale.getDefault());
   }

   public String toUpperCase(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var3 = this.value.length;

         int var5;
         for(int var2 = 0; var2 < var3; var2 += var5) {
            int var4 = this.value[var2];
            if (var4 >= 55296 && var4 <= 56319) {
               var4 = this.codePointAt(var2);
               var5 = Character.charCount(var4);
            } else {
               var5 = 1;
            }

            int var6 = Character.toUpperCaseEx(var4);
            if (var6 == -1 || var4 != var6) {
               var4 = 0;
               char[] var15 = new char[var3];
               System.arraycopy(this.value, 0, var15, 0, var2);
               String var16 = var1.getLanguage();
               boolean var7 = var16 == "tr" || var16 == "az" || var16 == "lt";

               int var11;
               for(int var12 = var2; var12 < var3; var12 += var11) {
                  int var10 = this.value[var12];
                  if ((char)var10 >= '\ud800' && (char)var10 <= '\udbff') {
                     var10 = this.codePointAt(var12);
                     var11 = Character.charCount(var10);
                  } else {
                     var11 = 1;
                  }

                  int var9;
                  if (var7) {
                     var9 = ConditionalSpecialCasing.toUpperCaseEx(this, var12, var1);
                  } else {
                     var9 = Character.toUpperCaseEx(var10);
                  }

                  if (var9 != -1 && var9 < 65536) {
                     var15[var12 + var4] = (char)var9;
                  } else {
                     char[] var8;
                     if (var9 == -1) {
                        if (var7) {
                           var8 = ConditionalSpecialCasing.toUpperCaseCharArray(this, var12, var1);
                        } else {
                           var8 = Character.toUpperCaseCharArray(var10);
                        }
                     } else {
                        if (var11 == 2) {
                           var4 += Character.toChars(var9, var15, var12 + var4) - var11;
                           continue;
                        }

                        var8 = Character.toChars(var9);
                     }

                     int var13 = var8.length;
                     if (var13 > var11) {
                        char[] var14 = new char[var15.length + var13 - var11];
                        System.arraycopy(var15, 0, var14, 0, var12 + var4);
                        var15 = var14;
                     }

                     for(int var17 = 0; var17 < var13; ++var17) {
                        var15[var12 + var4 + var17] = var8[var17];
                     }

                     var4 += var13 - var11;
                  }
               }

               return new String(var15, 0, var3 + var4);
            }
         }

         return this;
      }
   }

   public String toUpperCase() {
      return this.toUpperCase(Locale.getDefault());
   }

   public String trim() {
      int var1 = this.value.length;
      int var2 = 0;

      char[] var3;
      for(var3 = this.value; var2 < var1 && var3[var2] <= ' '; ++var2) {
      }

      while(var2 < var1 && var3[var1 - 1] <= ' ') {
         --var1;
      }

      return var2 <= 0 && var1 >= this.value.length ? this : this.substring(var2, var1);
   }

   public String toString() {
      return this;
   }

   public char[] toCharArray() {
      char[] var1 = new char[this.value.length];
      System.arraycopy(this.value, 0, var1, 0, this.value.length);
      return var1;
   }

   public static String format(String var0, Object... var1) {
      return (new Formatter()).format(var0, var1).toString();
   }

   public static String format(Locale var0, String var1, Object... var2) {
      return (new Formatter(var0)).format(var1, var2).toString();
   }

   public static String valueOf(Object var0) {
      return var0 == null ? "null" : var0.toString();
   }

   public static String valueOf(char[] var0) {
      return new String(var0);
   }

   public static String valueOf(char[] var0, int var1, int var2) {
      return new String(var0, var1, var2);
   }

   public static String copyValueOf(char[] var0, int var1, int var2) {
      return new String(var0, var1, var2);
   }

   public static String copyValueOf(char[] var0) {
      return new String(var0);
   }

   public static String valueOf(boolean var0) {
      return var0 ? "true" : "false";
   }

   public static String valueOf(char var0) {
      char[] var1 = new char[]{var0};
      return new String(var1, true);
   }

   public static String valueOf(int var0) {
      return Integer.toString(var0);
   }

   public static String valueOf(long var0) {
      return Long.toString(var0);
   }

   public static String valueOf(float var0) {
      return Float.toString(var0);
   }

   public static String valueOf(double var0) {
      return Double.toString(var0);
   }

   public native String intern();

   private static class CaseInsensitiveComparator implements Comparator<String>, Serializable {
      private static final long serialVersionUID = 8575799808933029326L;

      private CaseInsensitiveComparator() {
      }

      public int compare(String var1, String var2) {
         int var3 = var1.length();
         int var4 = var2.length();
         int var5 = Math.min(var3, var4);

         for(int var6 = 0; var6 < var5; ++var6) {
            char var7 = var1.charAt(var6);
            char var8 = var2.charAt(var6);
            if (var7 != var8) {
               var7 = Character.toUpperCase(var7);
               var8 = Character.toUpperCase(var8);
               if (var7 != var8) {
                  var7 = Character.toLowerCase(var7);
                  var8 = Character.toLowerCase(var8);
                  if (var7 != var8) {
                     return var7 - var8;
                  }
               }
            }
         }

         return var3 - var4;
      }

      private Object readResolve() {
         return String.CASE_INSENSITIVE_ORDER;
      }

      // $FF: synthetic method
      CaseInsensitiveComparator(Object var1) {
         this();
      }
   }
}
