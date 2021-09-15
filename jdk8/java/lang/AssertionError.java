package java.lang;

public class AssertionError extends Error {
   private static final long serialVersionUID = -5013299493970297370L;

   public AssertionError() {
   }

   private AssertionError(String var1) {
      super(var1);
   }

   public AssertionError(Object var1) {
      this(String.valueOf(var1));
      if (var1 instanceof Throwable) {
         this.initCause((Throwable)var1);
      }

   }

   public AssertionError(boolean var1) {
      this(String.valueOf(var1));
   }

   public AssertionError(char var1) {
      this(String.valueOf(var1));
   }

   public AssertionError(int var1) {
      this(String.valueOf(var1));
   }

   public AssertionError(long var1) {
      this(String.valueOf(var1));
   }

   public AssertionError(float var1) {
      this(String.valueOf(var1));
   }

   public AssertionError(double var1) {
      this(String.valueOf(var1));
   }

   public AssertionError(String var1, Throwable var2) {
      super(var1, var2);
   }
}
