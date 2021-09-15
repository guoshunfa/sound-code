package javax.naming;

public class Binding extends NameClassPair {
   private Object boundObj;
   private static final long serialVersionUID = 8839217842691845890L;

   public Binding(String var1, Object var2) {
      super(var1, (String)null);
      this.boundObj = var2;
   }

   public Binding(String var1, Object var2, boolean var3) {
      super(var1, (String)null, var3);
      this.boundObj = var2;
   }

   public Binding(String var1, String var2, Object var3) {
      super(var1, var2);
      this.boundObj = var3;
   }

   public Binding(String var1, String var2, Object var3, boolean var4) {
      super(var1, var2, var4);
      this.boundObj = var3;
   }

   public String getClassName() {
      String var1 = super.getClassName();
      if (var1 != null) {
         return var1;
      } else {
         return this.boundObj != null ? this.boundObj.getClass().getName() : null;
      }
   }

   public Object getObject() {
      return this.boundObj;
   }

   public void setObject(Object var1) {
      this.boundObj = var1;
   }

   public String toString() {
      return super.toString() + ":" + this.getObject();
   }
}
