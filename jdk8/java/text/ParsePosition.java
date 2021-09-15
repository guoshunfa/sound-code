package java.text;

public class ParsePosition {
   int index = 0;
   int errorIndex = -1;

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int var1) {
      this.index = var1;
   }

   public ParsePosition(int var1) {
      this.index = var1;
   }

   public void setErrorIndex(int var1) {
      this.errorIndex = var1;
   }

   public int getErrorIndex() {
      return this.errorIndex;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ParsePosition)) {
         return false;
      } else {
         ParsePosition var2 = (ParsePosition)var1;
         return this.index == var2.index && this.errorIndex == var2.errorIndex;
      }
   }

   public int hashCode() {
      return this.errorIndex << 16 | this.index;
   }

   public String toString() {
      return this.getClass().getName() + "[index=" + this.index + ",errorIndex=" + this.errorIndex + ']';
   }
}
