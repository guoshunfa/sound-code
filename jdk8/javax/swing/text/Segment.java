package javax.swing.text;

import java.text.CharacterIterator;

public class Segment implements Cloneable, CharacterIterator, CharSequence {
   public char[] array;
   public int offset;
   public int count;
   private boolean partialReturn;
   private int pos;

   public Segment() {
      this((char[])null, 0, 0);
   }

   public Segment(char[] var1, int var2, int var3) {
      this.array = var1;
      this.offset = var2;
      this.count = var3;
      this.partialReturn = false;
   }

   public void setPartialReturn(boolean var1) {
      this.partialReturn = var1;
   }

   public boolean isPartialReturn() {
      return this.partialReturn;
   }

   public String toString() {
      return this.array != null ? new String(this.array, this.offset, this.count) : "";
   }

   public char first() {
      this.pos = this.offset;
      return this.count != 0 ? this.array[this.pos] : '\uffff';
   }

   public char last() {
      this.pos = this.offset + this.count;
      if (this.count != 0) {
         --this.pos;
         return this.array[this.pos];
      } else {
         return '\uffff';
      }
   }

   public char current() {
      return this.count != 0 && this.pos < this.offset + this.count ? this.array[this.pos] : '\uffff';
   }

   public char next() {
      ++this.pos;
      int var1 = this.offset + this.count;
      if (this.pos >= var1) {
         this.pos = var1;
         return '\uffff';
      } else {
         return this.current();
      }
   }

   public char previous() {
      if (this.pos == this.offset) {
         return '\uffff';
      } else {
         --this.pos;
         return this.current();
      }
   }

   public char setIndex(int var1) {
      int var2 = this.offset + this.count;
      if (var1 >= this.offset && var1 <= var2) {
         this.pos = var1;
         return this.pos != var2 && this.count != 0 ? this.array[this.pos] : '\uffff';
      } else {
         throw new IllegalArgumentException("bad position: " + var1);
      }
   }

   public int getBeginIndex() {
      return this.offset;
   }

   public int getEndIndex() {
      return this.offset + this.count;
   }

   public int getIndex() {
      return this.pos;
   }

   public char charAt(int var1) {
      if (var1 >= 0 && var1 < this.count) {
         return this.array[this.offset + var1];
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public int length() {
      return this.count;
   }

   public CharSequence subSequence(int var1, int var2) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 > this.count) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var1 > var2) {
         throw new StringIndexOutOfBoundsException(var2 - var1);
      } else {
         Segment var3 = new Segment();
         var3.array = this.array;
         var3.offset = this.offset + var1;
         var3.count = var2 - var1;
         return var3;
      }
   }

   public Object clone() {
      Object var1;
      try {
         var1 = super.clone();
      } catch (CloneNotSupportedException var3) {
         var1 = null;
      }

      return var1;
   }
}
