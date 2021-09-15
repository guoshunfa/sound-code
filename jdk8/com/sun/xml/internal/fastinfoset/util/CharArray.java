package com.sun.xml.internal.fastinfoset.util;

public class CharArray implements CharSequence {
   public char[] ch;
   public int start;
   public int length;
   protected int _hash;

   protected CharArray() {
   }

   public CharArray(char[] _ch, int _start, int _length, boolean copy) {
      this.set(_ch, _start, _length, copy);
   }

   public final void set(char[] _ch, int _start, int _length, boolean copy) {
      if (copy) {
         this.ch = new char[_length];
         this.start = 0;
         this.length = _length;
         System.arraycopy(_ch, _start, this.ch, 0, _length);
      } else {
         this.ch = _ch;
         this.start = _start;
         this.length = _length;
      }

      this._hash = 0;
   }

   public final void cloneArray() {
      char[] _ch = new char[this.length];
      System.arraycopy(this.ch, this.start, _ch, 0, this.length);
      this.ch = _ch;
      this.start = 0;
   }

   public String toString() {
      return new String(this.ch, this.start, this.length);
   }

   public int hashCode() {
      if (this._hash == 0) {
         for(int i = this.start; i < this.start + this.length; ++i) {
            this._hash = 31 * this._hash + this.ch[i];
         }
      }

      return this._hash;
   }

   public static final int hashCode(char[] ch, int start, int length) {
      int hash = 0;

      for(int i = start; i < start + length; ++i) {
         hash = 31 * hash + ch[i];
      }

      return hash;
   }

   public final boolean equalsCharArray(CharArray cha) {
      if (this == cha) {
         return true;
      } else if (this.length == cha.length) {
         int n = this.length;
         int i = this.start;
         int var4 = cha.start;

         do {
            if (n-- == 0) {
               return true;
            }
         } while(this.ch[i++] == cha.ch[var4++]);

         return false;
      } else {
         return false;
      }
   }

   public final boolean equalsCharArray(char[] ch, int start, int length) {
      if (this.length == length) {
         int n = this.length;
         int i = this.start;
         int var6 = start;

         do {
            if (n-- == 0) {
               return true;
            }
         } while(this.ch[i++] == ch[var6++]);

         return false;
      } else {
         return false;
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj instanceof CharArray) {
            CharArray cha = (CharArray)obj;
            if (this.length == cha.length) {
               int n = this.length;
               int i = this.start;
               int var5 = cha.start;

               do {
                  if (n-- == 0) {
                     return true;
                  }
               } while(this.ch[i++] == cha.ch[var5++]);

               return false;
            }
         }

         return false;
      }
   }

   public final int length() {
      return this.length;
   }

   public final char charAt(int index) {
      return this.ch[this.start + index];
   }

   public final CharSequence subSequence(int start, int end) {
      return new CharArray(this.ch, this.start + start, end - start, false);
   }
}
