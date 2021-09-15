package com.sun.org.apache.regexp.internal;

public final class CharacterArrayCharacterIterator implements CharacterIterator {
   private final char[] src;
   private final int off;
   private final int len;

   public CharacterArrayCharacterIterator(char[] src, int off, int len) {
      this.src = src;
      this.off = off;
      this.len = len;
   }

   public String substring(int beginIndex, int endIndex) {
      if (endIndex > this.len) {
         throw new IndexOutOfBoundsException("endIndex=" + endIndex + "; sequence size=" + this.len);
      } else if (beginIndex >= 0 && beginIndex <= endIndex) {
         return new String(this.src, this.off + beginIndex, endIndex - beginIndex);
      } else {
         throw new IndexOutOfBoundsException("beginIndex=" + beginIndex + "; endIndex=" + endIndex);
      }
   }

   public String substring(int beginIndex) {
      return this.substring(beginIndex, this.len);
   }

   public char charAt(int pos) {
      return this.src[this.off + pos];
   }

   public boolean isEnd(int pos) {
      return pos >= this.len;
   }
}
