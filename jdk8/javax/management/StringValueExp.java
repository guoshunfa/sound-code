package javax.management;

public class StringValueExp implements ValueExp {
   private static final long serialVersionUID = -3256390509806284044L;
   private String val;

   public StringValueExp() {
   }

   public StringValueExp(String var1) {
      this.val = var1;
   }

   public String getValue() {
      return this.val;
   }

   public String toString() {
      return "'" + this.val.replace("'", "''") + "'";
   }

   /** @deprecated */
   @Deprecated
   public void setMBeanServer(MBeanServer var1) {
   }

   public ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      return this;
   }
}
