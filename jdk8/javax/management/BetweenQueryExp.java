package javax.management;

class BetweenQueryExp extends QueryEval implements QueryExp {
   private static final long serialVersionUID = -2933597532866307444L;
   private ValueExp exp1;
   private ValueExp exp2;
   private ValueExp exp3;

   public BetweenQueryExp() {
   }

   public BetweenQueryExp(ValueExp var1, ValueExp var2, ValueExp var3) {
      this.exp1 = var1;
      this.exp2 = var2;
      this.exp3 = var3;
   }

   public ValueExp getCheckedValue() {
      return this.exp1;
   }

   public ValueExp getLowerBound() {
      return this.exp2;
   }

   public ValueExp getUpperBound() {
      return this.exp3;
   }

   public boolean apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      ValueExp var2 = this.exp1.apply(var1);
      ValueExp var3 = this.exp2.apply(var1);
      ValueExp var4 = this.exp3.apply(var1);
      boolean var5 = var2 instanceof NumericValueExp;
      if (var5) {
         if (((NumericValueExp)var2).isLong()) {
            long var13 = ((NumericValueExp)var2).longValue();
            long var15 = ((NumericValueExp)var3).longValue();
            long var16 = ((NumericValueExp)var4).longValue();
            return var15 <= var13 && var13 <= var16;
         } else {
            double var12 = ((NumericValueExp)var2).doubleValue();
            double var14 = ((NumericValueExp)var3).doubleValue();
            double var10 = ((NumericValueExp)var4).doubleValue();
            return var14 <= var12 && var12 <= var10;
         }
      } else {
         String var6 = ((StringValueExp)var2).getValue();
         String var7 = ((StringValueExp)var3).getValue();
         String var8 = ((StringValueExp)var4).getValue();
         return var7.compareTo(var6) <= 0 && var6.compareTo(var8) <= 0;
      }
   }

   public String toString() {
      return "(" + this.exp1 + ") between (" + this.exp2 + ") and (" + this.exp3 + ")";
   }
}
