package java.nio.file;

public class AccessDeniedException extends FileSystemException {
   private static final long serialVersionUID = 4943049599949219617L;

   public AccessDeniedException(String var1) {
      super(var1);
   }

   public AccessDeniedException(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }
}
