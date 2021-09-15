package java.math;

class SignedMutableBigInteger extends MutableBigInteger {
   int sign = 1;

   SignedMutableBigInteger() {
   }

   SignedMutableBigInteger(int var1) {
      super(var1);
   }

   SignedMutableBigInteger(MutableBigInteger var1) {
      super(var1);
   }

   void signedAdd(SignedMutableBigInteger var1) {
      if (this.sign == var1.sign) {
         this.add(var1);
      } else {
         this.sign *= this.subtract(var1);
      }

   }

   void signedAdd(MutableBigInteger var1) {
      if (this.sign == 1) {
         this.add(var1);
      } else {
         this.sign *= this.subtract(var1);
      }

   }

   void signedSubtract(SignedMutableBigInteger var1) {
      if (this.sign == var1.sign) {
         this.sign *= this.subtract(var1);
      } else {
         this.add(var1);
      }

   }

   void signedSubtract(MutableBigInteger var1) {
      if (this.sign == 1) {
         this.sign *= this.subtract(var1);
      } else {
         this.add(var1);
      }

      if (this.intLen == 0) {
         this.sign = 1;
      }

   }

   public String toString() {
      return this.toBigInteger(this.sign).toString();
   }
}
