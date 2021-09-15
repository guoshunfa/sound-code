package javax.management;

class InstanceOfQueryExp extends QueryEval implements QueryExp {
   private static final long serialVersionUID = -1081892073854801359L;
   private StringValueExp classNameValue;

   public InstanceOfQueryExp(StringValueExp var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null class name.");
      } else {
         this.classNameValue = var1;
      }
   }

   public StringValueExp getClassNameValue() {
      return this.classNameValue;
   }

   public boolean apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      StringValueExp var2;
      try {
         var2 = (StringValueExp)this.classNameValue.apply(var1);
      } catch (ClassCastException var6) {
         BadStringOperationException var4 = new BadStringOperationException(var6.toString());
         var4.initCause(var6);
         throw var4;
      }

      try {
         return getMBeanServer().isInstanceOf(var1, var2.getValue());
      } catch (InstanceNotFoundException var5) {
         return false;
      }
   }

   public String toString() {
      return "InstanceOf " + this.classNameValue.toString();
   }
}
