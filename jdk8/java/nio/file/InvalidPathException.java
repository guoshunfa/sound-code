package java.nio.file;

public class InvalidPathException extends IllegalArgumentException {
   static final long serialVersionUID = 4355821422286746137L;
   private String input;
   private int index;

   public InvalidPathException(String var1, String var2, int var3) {
      super(var2);
      if (var1 != null && var2 != null) {
         if (var3 < -1) {
            throw new IllegalArgumentException();
         } else {
            this.input = var1;
            this.index = var3;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public InvalidPathException(String var1, String var2) {
      this(var1, var2, -1);
   }

   public String getInput() {
      return this.input;
   }

   public String getReason() {
      return super.getMessage();
   }

   public int getIndex() {
      return this.index;
   }

   public String getMessage() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.getReason());
      if (this.index > -1) {
         var1.append(" at index ");
         var1.append(this.index);
      }

      var1.append(": ");
      var1.append(this.input);
      return var1.toString();
   }
}
