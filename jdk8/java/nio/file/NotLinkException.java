package java.nio.file;

public class NotLinkException extends FileSystemException {
   static final long serialVersionUID = -388655596416518021L;

   public NotLinkException(String var1) {
      super(var1);
   }

   public NotLinkException(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }
}
