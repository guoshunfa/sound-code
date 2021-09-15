package sun.text.normalizer;

public class ReplaceableUCharacterIterator extends UCharacterIterator {
   private Replaceable replaceable;
   private int currentIndex;

   public ReplaceableUCharacterIterator(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         this.replaceable = new ReplaceableString(var1);
         this.currentIndex = 0;
      }
   }

   public ReplaceableUCharacterIterator(StringBuffer var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         this.replaceable = new ReplaceableString(var1);
         this.currentIndex = 0;
      }
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public int current() {
      return this.currentIndex < this.replaceable.length() ? this.replaceable.charAt(this.currentIndex) : -1;
   }

   public int getLength() {
      return this.replaceable.length();
   }

   public int getIndex() {
      return this.currentIndex;
   }

   public int next() {
      return this.currentIndex < this.replaceable.length() ? this.replaceable.charAt(this.currentIndex++) : -1;
   }

   public int previous() {
      return this.currentIndex > 0 ? this.replaceable.charAt(--this.currentIndex) : -1;
   }

   public void setIndex(int var1) {
      if (var1 >= 0 && var1 <= this.replaceable.length()) {
         this.currentIndex = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getText(char[] var1, int var2) {
      int var3 = this.replaceable.length();
      if (var2 >= 0 && var2 + var3 <= var1.length) {
         this.replaceable.getChars(0, var3, var1, var2);
         return var3;
      } else {
         throw new IndexOutOfBoundsException(Integer.toString(var3));
      }
   }
}
