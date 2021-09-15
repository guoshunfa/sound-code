package javax.management;

class InQueryExp extends QueryEval implements QueryExp {
   private static final long serialVersionUID = -5801329450358952434L;
   private ValueExp val;
   private ValueExp[] valueList;

   public InQueryExp() {
   }

   public InQueryExp(ValueExp var1, ValueExp[] var2) {
      this.val = var1;
      this.valueList = var2;
   }

   public ValueExp getCheckedValue() {
      return this.val;
   }

   public ValueExp[] getExplicitValues() {
      return this.valueList;
   }

   public boolean apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      if (this.valueList != null) {
         ValueExp var2 = this.val.apply(var1);
         boolean var3 = var2 instanceof NumericValueExp;
         ValueExp[] var4 = this.valueList;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ValueExp var7 = var4[var6];
            var7 = var7.apply(var1);
            if (var3) {
               if (((NumericValueExp)var7).doubleValue() == ((NumericValueExp)var2).doubleValue()) {
                  return true;
               }
            } else if (((StringValueExp)var7).getValue().equals(((StringValueExp)var2).getValue())) {
               return true;
            }
         }
      }

      return false;
   }

   public String toString() {
      return this.val + " in (" + this.generateValueList() + ")";
   }

   private String generateValueList() {
      if (this.valueList != null && this.valueList.length != 0) {
         StringBuilder var1 = new StringBuilder(this.valueList[0].toString());

         for(int var2 = 1; var2 < this.valueList.length; ++var2) {
            var1.append(", ");
            var1.append((Object)this.valueList[var2]);
         }

         return var1.toString();
      } else {
         return "";
      }
   }
}
