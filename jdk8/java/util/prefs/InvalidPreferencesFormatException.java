package java.util.prefs;

public class InvalidPreferencesFormatException extends Exception {
   private static final long serialVersionUID = -791715184232119669L;

   public InvalidPreferencesFormatException(Throwable var1) {
      super(var1);
   }

   public InvalidPreferencesFormatException(String var1) {
      super(var1);
   }

   public InvalidPreferencesFormatException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
