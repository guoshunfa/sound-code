package javax.management;

class BinaryOpValueExp extends QueryEval implements ValueExp {
   private static final long serialVersionUID = 1216286847881456786L;
   private int op;
   private ValueExp exp1;
   private ValueExp exp2;

   public BinaryOpValueExp() {
   }

   public BinaryOpValueExp(int var1, ValueExp var2, ValueExp var3) {
      this.op = var1;
      this.exp1 = var2;
      this.exp2 = var3;
   }

   public int getOperator() {
      return this.op;
   }

   public ValueExp getLeftValue() {
      return this.exp1;
   }

   public ValueExp getRightValue() {
      return this.exp2;
   }

   public ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      ValueExp var2 = this.exp1.apply(var1);
      ValueExp var3 = this.exp2.apply(var1);
      boolean var14 = var2 instanceof NumericValueExp;
      if (var14) {
         if (((NumericValueExp)var2).isLong()) {
            long var10 = ((NumericValueExp)var2).longValue();
            long var12 = ((NumericValueExp)var3).longValue();
            switch(this.op) {
            case 0:
               return Query.value(var10 + var12);
            case 1:
               return Query.value(var10 - var12);
            case 2:
               return Query.value(var10 * var12);
            case 3:
               return Query.value(var10 / var12);
            }
         } else {
            double var6 = ((NumericValueExp)var2).doubleValue();
            double var8 = ((NumericValueExp)var3).doubleValue();
            switch(this.op) {
            case 0:
               return Query.value(var6 + var8);
            case 1:
               return Query.value(var6 - var8);
            case 2:
               return Query.value(var6 * var8);
            case 3:
               return Query.value(var6 / var8);
            }
         }

         throw new BadBinaryOpValueExpException(this);
      } else {
         String var4 = ((StringValueExp)var2).getValue();
         String var5 = ((StringValueExp)var3).getValue();
         switch(this.op) {
         case 0:
            return new StringValueExp(var4 + var5);
         default:
            throw new BadStringOperationException(this.opString());
         }
      }
   }

   public String toString() {
      try {
         return this.parens(this.exp1, true) + " " + this.opString() + " " + this.parens(this.exp2, false);
      } catch (BadBinaryOpValueExpException var2) {
         return "invalid expression";
      }
   }

   private String parens(ValueExp var1, boolean var2) throws BadBinaryOpValueExpException {
      boolean var3;
      if (var1 instanceof BinaryOpValueExp) {
         int var4 = ((BinaryOpValueExp)var1).op;
         if (var2) {
            var3 = this.precedence(var4) >= this.precedence(this.op);
         } else {
            var3 = this.precedence(var4) > this.precedence(this.op);
         }
      } else {
         var3 = true;
      }

      return var3 ? var1.toString() : "(" + var1 + ")";
   }

   private int precedence(int var1) throws BadBinaryOpValueExpException {
      switch(var1) {
      case 0:
      case 1:
         return 0;
      case 2:
      case 3:
         return 1;
      default:
         throw new BadBinaryOpValueExpException(this);
      }
   }

   private String opString() throws BadBinaryOpValueExpException {
      switch(this.op) {
      case 0:
         return "+";
      case 1:
         return "-";
      case 2:
         return "*";
      case 3:
         return "/";
      default:
         throw new BadBinaryOpValueExpException(this);
      }
   }

   /** @deprecated */
   @Deprecated
   public void setMBeanServer(MBeanServer var1) {
      super.setMBeanServer(var1);
   }
}
