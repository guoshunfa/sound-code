package java.beans;

public class Expression extends Statement {
   private static Object unbound = new Object();
   private Object value;

   @ConstructorProperties({"target", "methodName", "arguments"})
   public Expression(Object var1, String var2, Object[] var3) {
      super(var1, var2, var3);
      this.value = unbound;
   }

   public Expression(Object var1, Object var2, String var3, Object[] var4) {
      this(var2, var3, var4);
      this.setValue(var1);
   }

   public void execute() throws Exception {
      this.setValue(this.invoke());
   }

   public Object getValue() throws Exception {
      if (this.value == unbound) {
         this.setValue(this.invoke());
      }

      return this.value;
   }

   public void setValue(Object var1) {
      this.value = var1;
   }

   String instanceName(Object var1) {
      return var1 == unbound ? "<unbound>" : super.instanceName(var1);
   }

   public String toString() {
      return this.instanceName(this.value) + "=" + super.toString();
   }
}
