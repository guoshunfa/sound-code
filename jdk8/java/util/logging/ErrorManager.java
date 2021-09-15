package java.util.logging;

public class ErrorManager {
   private boolean reported = false;
   public static final int GENERIC_FAILURE = 0;
   public static final int WRITE_FAILURE = 1;
   public static final int FLUSH_FAILURE = 2;
   public static final int CLOSE_FAILURE = 3;
   public static final int OPEN_FAILURE = 4;
   public static final int FORMAT_FAILURE = 5;

   public synchronized void error(String var1, Exception var2, int var3) {
      if (!this.reported) {
         this.reported = true;
         String var4 = "java.util.logging.ErrorManager: " + var3;
         if (var1 != null) {
            var4 = var4 + ": " + var1;
         }

         System.err.println(var4);
         if (var2 != null) {
            var2.printStackTrace();
         }

      }
   }
}
