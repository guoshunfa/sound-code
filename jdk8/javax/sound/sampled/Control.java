package javax.sound.sampled;

public abstract class Control {
   private final Control.Type type;

   protected Control(Control.Type var1) {
      this.type = var1;
   }

   public Control.Type getType() {
      return this.type;
   }

   public String toString() {
      return new String(this.getType() + " Control");
   }

   public static class Type {
      private String name;

      protected Type(String var1) {
         this.name = var1;
      }

      public final boolean equals(Object var1) {
         return super.equals(var1);
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public final String toString() {
         return this.name;
      }
   }
}
