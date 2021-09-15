package org.omg.CORBA;

public abstract class ServerRequest {
   /** @deprecated */
   @Deprecated
   public String op_name() {
      return this.operation();
   }

   public String operation() {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public void params(NVList var1) {
      this.arguments(var1);
   }

   public void arguments(NVList var1) {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public void result(Any var1) {
      this.set_result(var1);
   }

   public void set_result(Any var1) {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public void except(Any var1) {
      this.set_exception(var1);
   }

   public void set_exception(Any var1) {
      throw new NO_IMPLEMENT();
   }

   public abstract Context ctx();
}
