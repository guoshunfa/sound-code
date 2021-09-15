package javax.management;

class QualifiedAttributeValueExp extends AttributeValueExp {
   private static final long serialVersionUID = 8832517277410933254L;
   private String className;

   /** @deprecated */
   @Deprecated
   public QualifiedAttributeValueExp() {
   }

   public QualifiedAttributeValueExp(String var1, String var2) {
      super(var2);
      this.className = var1;
   }

   public String getAttrClassName() {
      return this.className;
   }

   public ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      try {
         MBeanServer var2 = QueryEval.getMBeanServer();
         String var3 = var2.getObjectInstance(var1).getClassName();
         if (var3.equals(this.className)) {
            return super.apply(var1);
         } else {
            throw new InvalidApplicationException("Class name is " + var3 + ", should be " + this.className);
         }
      } catch (Exception var4) {
         throw new InvalidApplicationException("Qualified attribute: " + var4);
      }
   }

   public String toString() {
      return this.className != null ? this.className + "." + super.toString() : super.toString();
   }
}
