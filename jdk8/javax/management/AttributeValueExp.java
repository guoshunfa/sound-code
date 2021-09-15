package javax.management;

public class AttributeValueExp implements ValueExp {
   private static final long serialVersionUID = -7768025046539163385L;
   private String attr;

   /** @deprecated */
   @Deprecated
   public AttributeValueExp() {
   }

   public AttributeValueExp(String var1) {
      this.attr = var1;
   }

   public String getAttributeName() {
      return this.attr;
   }

   public ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      Object var2 = this.getAttribute(var1);
      if (var2 instanceof Number) {
         return new NumericValueExp((Number)var2);
      } else if (var2 instanceof String) {
         return new StringValueExp((String)var2);
      } else if (var2 instanceof Boolean) {
         return new BooleanValueExp((Boolean)var2);
      } else {
         throw new BadAttributeValueExpException(var2);
      }
   }

   public String toString() {
      return this.attr;
   }

   /** @deprecated */
   @Deprecated
   public void setMBeanServer(MBeanServer var1) {
   }

   protected Object getAttribute(ObjectName var1) {
      try {
         MBeanServer var2 = QueryEval.getMBeanServer();
         return var2.getAttribute(var1, this.attr);
      } catch (Exception var3) {
         return null;
      }
   }
}
