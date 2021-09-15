package javax.management;

class AndQueryExp extends QueryEval implements QueryExp {
   private static final long serialVersionUID = -1081892073854801359L;
   private QueryExp exp1;
   private QueryExp exp2;

   public AndQueryExp() {
   }

   public AndQueryExp(QueryExp var1, QueryExp var2) {
      this.exp1 = var1;
      this.exp2 = var2;
   }

   public QueryExp getLeftExp() {
      return this.exp1;
   }

   public QueryExp getRightExp() {
      return this.exp2;
   }

   public boolean apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      return this.exp1.apply(var1) && this.exp2.apply(var1);
   }

   public String toString() {
      return "(" + this.exp1 + ") and (" + this.exp2 + ")";
   }
}
