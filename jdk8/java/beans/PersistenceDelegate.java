package java.beans;

public abstract class PersistenceDelegate {
   public void writeObject(Object var1, Encoder var2) {
      Object var3 = var2.get(var1);
      if (!this.mutatesTo(var1, var3)) {
         var2.remove(var1);
         var2.writeExpression(this.instantiate(var1, var2));
      } else {
         this.initialize(var1.getClass(), var1, var3, var2);
      }

   }

   protected boolean mutatesTo(Object var1, Object var2) {
      return var2 != null && var1 != null && var1.getClass() == var2.getClass();
   }

   protected abstract Expression instantiate(Object var1, Encoder var2);

   protected void initialize(Class<?> var1, Object var2, Object var3, Encoder var4) {
      Class var5 = var1.getSuperclass();
      PersistenceDelegate var6 = var4.getPersistenceDelegate(var5);
      var6.initialize(var5, var2, var3, var4);
   }
}
