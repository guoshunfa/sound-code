package javax.swing.text;

public interface Position {
   int getOffset();

   public static final class Bias {
      public static final Position.Bias Forward = new Position.Bias("Forward");
      public static final Position.Bias Backward = new Position.Bias("Backward");
      private String name;

      public String toString() {
         return this.name;
      }

      private Bias(String var1) {
         this.name = var1;
      }
   }
}
