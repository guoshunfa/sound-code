package javax.annotation.processing;

public class Completions {
   private Completions() {
   }

   public static Completion of(String var0, String var1) {
      return new Completions.SimpleCompletion(var0, var1);
   }

   public static Completion of(String var0) {
      return new Completions.SimpleCompletion(var0, "");
   }

   private static class SimpleCompletion implements Completion {
      private String value;
      private String message;

      SimpleCompletion(String var1, String var2) {
         if (var1 != null && var2 != null) {
            this.value = var1;
            this.message = var2;
         } else {
            throw new NullPointerException("Null completion strings not accepted.");
         }
      }

      public String getValue() {
         return this.value;
      }

      public String getMessage() {
         return this.message;
      }

      public String toString() {
         return "[\"" + this.value + "\", \"" + this.message + "\"]";
      }
   }
}
