package java.nio.file;

public class NoSuchFileException extends FileSystemException {
   static final long serialVersionUID = -1390291775875351931L;

   public NoSuchFileException(String var1) {
      super(var1);
   }

   public NoSuchFileException(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }
}
