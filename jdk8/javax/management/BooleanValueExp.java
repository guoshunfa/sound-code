package javax.management;

class BooleanValueExp extends QueryEval implements ValueExp {
   private static final long serialVersionUID = 7754922052666594581L;
   private boolean val = false;

   BooleanValueExp(boolean var1) {
      this.val = var1;
   }

   BooleanValueExp(Boolean var1) {
      this.val = var1;
   }

   public Boolean getValue() {
      return this.val;
   }

   public String toString() {
      return String.valueOf(this.val);
   }

   public ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      return this;
   }

   /** @deprecated */
   @Deprecated
   public void setMBeanServer(MBeanServer var1) {
      super.setMBeanServer(var1);
   }
}
