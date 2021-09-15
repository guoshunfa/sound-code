package javax.management;

class BinaryRelQueryExp extends QueryEval implements QueryExp {
   private static final long serialVersionUID = -5690656271650491000L;
   private int relOp;
   private ValueExp exp1;
   private ValueExp exp2;

   public BinaryRelQueryExp() {
   }

   public BinaryRelQueryExp(int var1, ValueExp var2, ValueExp var3) {
      this.relOp = var1;
      this.exp1 = var2;
      this.exp2 = var3;
   }

   public int getOperator() {
      return this.relOp;
   }

   public ValueExp getLeftValue() {
      return this.exp1;
   }

   public ValueExp getRightValue() {
      return this.exp2;
   }

   public boolean apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      ValueExp var2 = this.exp1.apply(var1);
      ValueExp var3 = this.exp2.apply(var1);
      boolean var4 = var2 instanceof NumericValueExp;
      boolean var5 = var2 instanceof BooleanValueExp;
      if (var4) {
         if (((NumericValueExp)var2).isLong()) {
            long var6 = ((NumericValueExp)var2).longValue();
            long var8 = ((NumericValueExp)var3).longValue();
            switch(this.relOp) {
            case 0:
               return var6 > var8;
            case 1:
               return var6 < var8;
            case 2:
               return var6 >= var8;
            case 3:
               return var6 <= var8;
            case 4:
               return var6 == var8;
            }
         } else {
            double var10 = ((NumericValueExp)var2).doubleValue();
            double var14 = ((NumericValueExp)var3).doubleValue();
            switch(this.relOp) {
            case 0:
               return var10 > var14;
            case 1:
               return var10 < var14;
            case 2:
               return var10 >= var14;
            case 3:
               return var10 <= var14;
            case 4:
               return var10 == var14;
            }
         }
      } else if (var5) {
         boolean var11 = ((BooleanValueExp)var2).getValue();
         boolean var7 = ((BooleanValueExp)var3).getValue();
         switch(this.relOp) {
         case 0:
            return var11 && !var7;
         case 1:
            return !var11 && var7;
         case 2:
            return var11 || !var7;
         case 3:
            return !var11 || var7;
         case 4:
            return var11 == var7;
         }
      } else {
         String var12 = ((StringValueExp)var2).getValue();
         String var13 = ((StringValueExp)var3).getValue();
         switch(this.relOp) {
         case 0:
            return var12.compareTo(var13) > 0;
         case 1:
            return var12.compareTo(var13) < 0;
         case 2:
            return var12.compareTo(var13) >= 0;
         case 3:
            return var12.compareTo(var13) <= 0;
         case 4:
            return var12.compareTo(var13) == 0;
         }
      }

      return false;
   }

   public String toString() {
      return "(" + this.exp1 + ") " + this.relOpString() + " (" + this.exp2 + ")";
   }

   private String relOpString() {
      switch(this.relOp) {
      case 0:
         return ">";
      case 1:
         return "<";
      case 2:
         return ">=";
      case 3:
         return "<=";
      case 4:
         return "=";
      default:
         return "=";
      }
   }
}
