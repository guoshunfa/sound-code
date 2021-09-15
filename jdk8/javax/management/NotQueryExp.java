package javax.management;

class NotQueryExp extends QueryEval implements QueryExp {
   private static final long serialVersionUID = 5269643775896723397L;
   private QueryExp exp;

   public NotQueryExp() {
   }

   public NotQueryExp(QueryExp var1) {
      this.exp = var1;
   }

   public QueryExp getNegatedExp() {
      return this.exp;
   }

   public boolean apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      return !this.exp.apply(var1);
   }

   public String toString() {
      return "not (" + this.exp + ")";
   }
}
